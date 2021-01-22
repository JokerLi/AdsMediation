package com.buffalo.utils;

import android.net.SSLCertificateSocketFactory;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import com.buffalo.adsdk.InternalAdError;
import com.buffalo.utils.internal.ThreadFactoryUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Networking {
    private static final String TAG = "Networking";
    private static final String ENCODING = "UTF-8";
    private static final int MAX_REDIRECTS = 10;

    private static final String ENCODING_ERROR_TAG = "ENCODING_ERROR_TAG:";
    private static final String PROTOCOL_ERROR_TAG = "PROTOCOL_ERROR_TAG:";
    private static final String REDIRECT_ERROR_TAG = "REDIRECT_ERROR_TAG:";

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(CPU_COUNT, 5); // 至少允许5个线程
    private static final int THREAD_KEEP_LIVE_TIME = 30; // 线程如果30秒不用，允许超时
    private static final int TASK_QUEUE_MAX_COUNT = 128;


    private static Executor mExecutor = initExecute();

    private static Executor initExecute() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE,
                CORE_POOL_SIZE, THREAD_KEEP_LIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(TASK_QUEUE_MAX_COUNT),
                ThreadFactoryUtil.createNamedThreadFactory("Networking: normal request"));
        try {
            executor.allowCoreThreadTimeOut(true);
        } catch (Exception e) {
            Log.e("stacktrace_tag", "stackerror:", e);
        } catch (NoSuchMethodError error) {
            error.printStackTrace();
        }
        return executor;
    }

    private static Executor mDownloadExecutor = new ThreadPoolExecutor(1, 1, THREAD_KEEP_LIVE_TIME,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(TASK_QUEUE_MAX_COUNT),
            ThreadFactoryUtil.createNamedThreadFactory("Networking: video request"));

    public static Request get(String url) {
        return get(url, null);
    }

    public static Request get(String url, HttpListener listener) {
        return get(url, null, listener);
    }

    public static Request get(String url, String withParams, HttpListener listener) {
        final Request request = new Request();
        String getUrl = concatUrl(url, withParams);
        request.setUrl(getUrl);
        request.setListener(listener);
        request.setMethod(Method.GET);
        boolean success = safeExecute(mExecutor, new Runnable() {
            @Override
            public void run() {
                doRequest(request);
            }
        });

        return success ? request : null;
    }

    public static Request post(String url, final String message, HttpListener listener) {
        return post(url, message, null, listener);
    }

    public static Request post(String url, final String message, Map<String, String> headersMap, HttpListener listener) {
        final Request request = new Request() {
            @Override
            public byte[] getBody() {
                try {
                    return message.getBytes(ENCODING);
                } catch (Exception e) {
                    Log.e("stacktrace_tag", "stackerror:", e);
                }
                return null;
            }
        };
        request.setUrl(url);
        request.setListener(listener);
        request.setMethod(Method.POST);
        if (headersMap != null && !headersMap.isEmpty()) {
            request.putHeader(headersMap);
        }
        boolean success = safeExecute(mExecutor, new Runnable() {
            @Override
            public void run() {
                doRequest(request);
            }
        });

        return success ? request : null;
    }

    public static Request download(final String url, final File toDir, final long maxSize, final FileListener listener) {
        final Request request = new Request();
        request.setUrl(url);
        request.setMethod(Method.GET);
        request.setListener(new HttpListener() {
            @Override
            public void onResponse(int responseCode, HashMap<String, String> headers, InputStream result, String encode, int contentLength) {
                long maxLength = maxSize <= 0 ? Long.MAX_VALUE : maxSize;
                if (contentLength > 0 && contentLength <= maxLength) {
                    Logger.d(TAG, "to create tmp file");
                    if (toDir == null || FileUtil.diskFreeSize(toDir) < contentLength * 2) {
                        notifyFileError(listener, InternalAdError.NETWORK_DISK_SPACE_ERROR);
                        return;
                    }

                    String errorMsg = "";
                    FileOutputStream fos = null;
                    BufferedOutputStream bos = null;
                    File tmpFile = null;
                    try {
                        // delete it if it's not a directory
                        if (!toDir.isDirectory()) {
                            toDir.delete();
                        }

                        // create directory if it does not exists.
                        if (!toDir.exists()) {
                            toDir.mkdirs();
                        }
                        String tmpFilePath = toDir.getAbsolutePath() + File.separator + System.currentTimeMillis();
                        tmpFile = new File(tmpFilePath);

                        fos = new FileOutputStream(tmpFile);
                        bos = new BufferedOutputStream(fos);
                        StreamUtil.copyContent(result, bos);

                        if (listener != null) {
                            listener.onDownloaded(url, tmpFile);
                        }
                    } catch (Throwable e) {
                        errorMsg = e.getMessage();
                        notifyFileError(listener, InternalAdError.NETWORK_OTHER_ERROR.withMessage(errorMsg));
                        if (tmpFile != null) {
                            tmpFile.delete();
                        }
                    } finally {
                        StreamUtil.closeStream(fos);
                        StreamUtil.closeStream(bos);
                    }
                } else {
                    notifyFileError(listener, InternalAdError.NETWORK_MAX_SIZE_ERROR);
                }
            }

            @Override
            public void onError(int responseCode, InternalAdError error) {
                notifyFileError(listener, error);
            }
        });

        boolean success = safeExecute(mDownloadExecutor, new Runnable() {
            @Override
            public void run() {
                doRequest(request);
            }
        });

        return success ? request : null;
    }

    private static boolean safeExecute(Executor executor, Runnable runnable) {
        try {
            executor.execute(runnable);
            return true;
        } catch (Exception e) {
            Log.e("stacktrace_tag", "stackerror:", e);
            return false;
        }
    }

    public static NetworkingResponse requestForResponse(String url) {
        Request request = new Request();
        request.setUrl(url);
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        int responseCode = -1;
        String content = null;
        String exceptionName = null;
        try {
            connection = makeConnection(request);
            responseCode = connection.getResponseCode();
            if (connection != null) {
                int respCode = connection.getResponseCode();
                if (respCode == HttpURLConnection.HTTP_OK) {
                    String encoding = parseCharset(connection.getContentType());
                    inputStream = connection.getInputStream();
                    content = readString(inputStream, encoding);
                }
            }
        } catch (Throwable e) {
            exceptionName = e.getClass().getName();
            Log.e("stacktrace_tag", "stackerror:", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("stacktrace_tag", "stackerror:", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        NetworkingResponse response = new NetworkingResponse();
        response.content = content;
        response.responseCode = responseCode;
        response.exceptionName = exceptionName;
        return response;
    }

    public static String requestForString(String url) {
        TypedValue encoding = new TypedValue();
        byte[] bytes = requestForBytes(url, encoding);
        if (bytes != null) {
            try {
                return new String(bytes, String.valueOf(encoding.string));
            } catch (UnsupportedEncodingException e) {
                Log.e("stacktrace_tag", "stackerror:", e);
            }
        }

        return null;
    }

    public static byte[] requestForBytes(String url, TypedValue returnEncoding) {
        Request request = new Request();
        request.setUrl(url);
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            connection = makeConnection(request);
            if (connection != null) {
                int respCode = connection.getResponseCode();
                if (respCode == HttpURLConnection.HTTP_OK) {
                    if (returnEncoding != null) {
                        returnEncoding.string = parseCharset(connection.getContentType());
                    }

                    inputStream = connection.getInputStream();
                    return readBytes(inputStream);
                }
            }
        } catch (Throwable e) {
            Log.e("stacktrace_tag", "stackerror:", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("stacktrace_tag", "stackerror:", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    public static String readString(InputStream inputStream, String encode) {
        try {
            byte[] data = readBytes(inputStream);
            if (data != null) {
                return new String(data, encode);
            }
        } catch (Exception e) {
            Log.e("stacktrace_tag", "stackerror:", e);
        }

        return null;
    }

    public static byte[] readBytes(InputStream inputStream) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n;
            while ((n = inputStream.read(buf)) >= 0) {
                bos.write(buf, 0, n);
            }
            bos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            Log.e("stacktrace_tag", "stackerror:", e);
        }

        return null;
    }

    public static String concatUrl(String baseUrl, String params) {
        if (!TextUtils.isEmpty(params)) {
            if (baseUrl.trim().endsWith("?")) {
                baseUrl += params;
            } else {
                baseUrl += "?" + params;
            }
        }

        return baseUrl;
    }

    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    public interface HttpListener {
        void onResponse(int responseCode,
                        HashMap<String, String> headers,
                        InputStream result,
                        String encode,
                        int contentLength);

        void onError(int responseCode, InternalAdError error);
    }

    public interface FileListener {
        void onDownloaded(String url, File downFile);

        void onError(InternalAdError error);
    }

    public static class NetworkingResponse {
        public String content;
        public int responseCode;
        public String exceptionName;
    }

    public static class Request {
        private static final String HEADER_CONTENT_TYPE = "Content-Type";
        private static final String USER_AGENT = "User-Agent";
        public static final int NETWORKING_TIME_OUT_MILLS = 5000; // 5 seconds

        private int mMethod = Method.GET;
        private String mUrl;
        private HashMap<String, String> mHeaders = new HashMap<>();
        private HashMap<String, String> mParameters = new HashMap<>();

        private HttpListener mListener;
        private boolean mCanceled = false;

        private String mRedirectedUrl;

        private int mTimeoutMills = NETWORKING_TIME_OUT_MILLS;

        Request() {
            mHeaders.put(USER_AGENT, MarketConfig.getCacheUserAgent());
        }

        public void putHeader(Map<String, String> headers) {
            if (headers == null || headers.isEmpty()) {
                return;
            }
            mHeaders.putAll(headers);
        }

        public void setListener(HttpListener listener) {
            this.mListener = listener;
        }

        public void setUrl(String mUrl) {
            this.mUrl = mUrl;
        }

        public int getTimeoutMills() {
            return mTimeoutMills;
        }

        public byte[] getBody() {
            Map<String, String> params = mParameters;
            if (params != null && params.size() > 0) {
                return encodeParameters(params, ENCODING);
            }
            return null;
        }

        /**
         * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
         */
        private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
            StringBuilder encodedParams = new StringBuilder();
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                    encodedParams.append('=');
                    encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                    encodedParams.append('&');
                }
                return encodedParams.toString().getBytes(paramsEncoding);
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException(ENCODING_ERROR_TAG + paramsEncoding, uee);
            }
        }

        /**
         * Returns the content type of the POST or PUT body.
         */
        private String getBodyContentType() {
            return "application/x-www-form-urlencoded; charset=" + ENCODING;
        }

        public void setMethod(int method) {
            mMethod = method;
        }

        public String getRedirectedUrl() {
            return mRedirectedUrl;
        }
    }

    public static class CustomSSLSocketFactory extends SSLSocketFactory {

        private SSLSocketFactory mCertificateSocketFactory;

        private CustomSSLSocketFactory() {
        }

        public static CustomSSLSocketFactory getDefault(final int handshakeTimeoutMillis) {
            CustomSSLSocketFactory factory = new CustomSSLSocketFactory();
            factory.mCertificateSocketFactory = SSLCertificateSocketFactory.getDefault(handshakeTimeoutMillis, null);

            return factory;
        }

        // Forward all methods. Enable TLS 1.1 and 1.2 before returning.

        // SocketFactory overrides
        @Override
        public Socket createSocket() throws IOException {
            final Socket socket = mCertificateSocketFactory.createSocket();
            enableTlsIfAvailable(socket);
            return socket;
        }

        @Override
        public Socket createSocket(final String host, final int i) throws IOException {
            final Socket socket = mCertificateSocketFactory.createSocket(host, i);
            enableTlsIfAvailable(socket);
            return socket;
        }

        @Override
        public Socket createSocket(final String host, final int port, final InetAddress localhost, final int localPort) throws IOException {
            final Socket socket = mCertificateSocketFactory.createSocket(host, port, localhost, localPort);
            enableTlsIfAvailable(socket);
            return socket;
        }

        @Override
        public Socket createSocket(final InetAddress address, final int port) throws IOException {
            final Socket socket = mCertificateSocketFactory.createSocket(address, port);
            enableTlsIfAvailable(socket);
            return socket;
        }

        @Override
        public Socket createSocket(final InetAddress address, final int port, final InetAddress localhost, final int localPort) throws IOException {
            final Socket socket = mCertificateSocketFactory.createSocket(address, port, localhost, localPort);
            enableTlsIfAvailable(socket);
            return socket;
        }

        // SSLSocketFactory overrides

        @Override
        public String[] getDefaultCipherSuites() {
            return mCertificateSocketFactory.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return mCertificateSocketFactory.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(final Socket socketParam, final String host, final int port, final boolean autoClose) throws IOException {
            Socket socket = mCertificateSocketFactory.createSocket(socketParam, host, port, autoClose);
            enableTlsIfAvailable(socket);
            return socket;
        }

        private void enableTlsIfAvailable(Socket socket) {
            if (socket instanceof SSLSocket) {
                SSLSocket sslSocket = (SSLSocket) socket;
                String[] supportedProtocols = sslSocket.getSupportedProtocols();
                // Make sure all supported protocols are enabled. Android does not enable TLSv1.1 or
                // TLSv1.2 by default.
                sslSocket.setEnabledProtocols(supportedProtocols);
            }
        }
    }

    private static void doRequest(@NonNull Request request) {
        HttpListener listener = request.mListener;
        int responseCode = -1;
        if (TextUtils.isEmpty(request.mUrl)) {
            notifyHttpError(listener, responseCode, InternalAdError.NETWORK_URL_ERROR);
            return;
        }

        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            connection = makeConnection(request);
            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                HashMap<String, String> respHeaders = new HashMap<>();
                String key;
                int i = 1;
                while ((key = connection.getHeaderFieldKey(i++)) != null) {
                    String value = connection.getHeaderField(key);
                    respHeaders.put(key, value);
                }

                is = connection.getInputStream();
                String encode = parseCharset(connection.getContentType());
                // parse response and notify the mListener
                if (listener != null) {
                    listener.onResponse(responseCode, respHeaders, is, encode, connection.getContentLength());
                } else {
                    String resultString = readString(is, encode);
                    Logger.d(TAG, "Discarded response data:" + resultString);
                }
            } else {
                notifyHttpError(listener, responseCode, InternalAdError.NETWORK_RESPONSE_ERROR);
            }
        } catch (SocketTimeoutException e) {
            notifyHttpError(listener, responseCode, InternalAdError.NETWORK_TIMEOUT_ERROR.withExceptionName(e));
        } catch (Throwable e) {
            String errorMsg = e.getMessage();
            if (!TextUtils.isEmpty(errorMsg)) {
                if (errorMsg.startsWith(ENCODING_ERROR_TAG)) {
                    notifyHttpError(listener, responseCode, InternalAdError.NETWORK_ENCODING_ERROR.withExceptionName(e));
                } else if (errorMsg.startsWith(PROTOCOL_ERROR_TAG)) {
                    notifyHttpError(listener, responseCode, InternalAdError.NETWORK_PROTOCOL_ERROR.withExceptionName(e));
                } else if (errorMsg.startsWith(REDIRECT_ERROR_TAG)) {
                    notifyHttpError(listener, responseCode, InternalAdError.NETWORK_REDIRECT_ERROR.withExceptionName(e));
                } else {
                    notifyHttpError(listener, responseCode, InternalAdError.NETWORK_OTHER_ERROR.withMessage(errorMsg).withExceptionName(e));
                }
            } else {
                notifyHttpError(listener, responseCode, InternalAdError.NETWORK_OTHER_ERROR.withExceptionName(e));
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e("stacktrace_tag", "stackerror:", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void notifyHttpError(HttpListener listener, int responseCode, InternalAdError error) {
        if (listener != null) {
            listener.onError(responseCode, error);
        }
    }

    private static void notifyFileError(FileListener listener, InternalAdError error) {
        if (listener != null) {
            listener.onError(error);
        }
    }

    public static String parseCharset(String contentType) {
        if (!TextUtils.isEmpty(contentType)) {
            String[] params = contentType.split(";");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }

        return ENCODING;
    }

    private static void setConnectionParametersForRequest(@NonNull HttpURLConnection connection, @NonNull Request request) throws IOException {
        // set up request property
        Map<String, String> map = request.mHeaders;
        for (String headerName : map.keySet()) {
            connection.addRequestProperty(headerName, map.get(headerName));
        }

        // set up http mMethod
        switch (request.mMethod) {
            case Method.DEPRECATED_GET_OR_POST:
                // This is the deprecated way that needs to be handled for backwards compatibility.
                // If the doRequest's post body is null, then the assumption is that the doRequest is
                // GET.  Otherwise, it is assumed that the doRequest is a POST.
                byte[] postBody = request.getBody();
                if (postBody != null) {
                    // Prepare output. There is no need to set Content-Length explicitly,
                    // since this is handled by HttpURLConnection using the size of the prepared
                    // output stream.
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.write(postBody);
                    out.close();
                }
                break;
            case Method.GET:
                // Not necessary to set the doRequest mMethod because connection defaults to GET but
                // being explicit here.
                connection.setRequestMethod("GET");
                break;
            case Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case Method.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            case Method.HEAD:
                connection.setRequestMethod("HEAD");
                break;
            case Method.OPTIONS:
                connection.setRequestMethod("OPTIONS");
                break;
            case Method.TRACE:
                connection.setRequestMethod("TRACE");
                break;
            case Method.PATCH:
                connection.setRequestMethod("PATCH");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown mMethod type.");
        }
    }

    private static void addBodyIfExists(HttpURLConnection connection, @NonNull Request request) throws IOException {
        byte[] body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(body);
            out.close();
        }
    }

    private static HttpURLConnection makeConnection(@NonNull Request request) throws Exception {
        URL url = new URL(request.mUrl);
        String protocolCur = url.getProtocol();
        int redirectCount = 0;
        while (redirectCount++ <= MAX_REDIRECTS && !request.mCanceled) {
            if (!"https".equalsIgnoreCase(protocolCur) && !"http".equalsIgnoreCase(protocolCur)) {
                // only support http/https protocol now.
                throw new Exception(PROTOCOL_ERROR_TAG + "url = " + request.mUrl);
            }

            HttpURLConnection connection = openConnection(url, request);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM
                    || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                String location = connection.getHeaderField("Location");
                request.mRedirectedUrl = location;
                connection.disconnect();

                url = new URL(url, location);
                protocolCur = url.getProtocol();
            } else {
                return connection;
            }
        }

        throw new Exception(REDIRECT_ERROR_TAG + "max count = " + MAX_REDIRECTS);
    }

    /**
     * Opens an {@link HttpURLConnection} with mParameters.
     *
     * @param url url to connect
     * @return an open connection
     * @throws IOException
     */
    private static HttpURLConnection openConnection(@NonNull URL url, @NonNull Request request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 调试发现https进行跨协议redirect时，如果碰到market等非标准http协议，会抛出异常，导致我们无法得知最终跳转到的url.
        // 因此我们禁用系统自带的redirect功能，自己进行redirect的处理，以拿到类似market这样的url地址。
        connection.setInstanceFollowRedirects(false);

        connection.setConnectTimeout(request.getTimeoutMills());
        connection.setReadTimeout(request.getTimeoutMills());
        connection.setUseCaches(false);
        connection.setDoInput(true);

        // use caller-provided custom SslSocketFactory, if any, for HTTPS
        if ("https".equals(url.getProtocol())) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(CustomSSLSocketFactory.getDefault(request.getTimeoutMills()));
        }

        setConnectionParametersForRequest(connection, request);
        return connection;
    }
}
