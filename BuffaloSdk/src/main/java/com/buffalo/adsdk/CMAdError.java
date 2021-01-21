package com.buffalo.adsdk;


/**
 * Created by chenhao on 2015/8/24.
 */
public class CMAdError {
    /*
    Facebook
    public static final int NETWORK_ERROR_CODE = 1000;
    public static final int NO_FILL_ERROR_CODE = 1001;
    public static final int LOAD_TOO_FREQUENTLY_ERROR_CODE = 1002;
    public static final int SERVER_ERROR_CODE = 2000;
    public static final int INTERNAL_ERROR_CODE = 2001;
    public static final AdError NETWORK_ERROR = new AdError(1000, "Network Error");
    public static final AdError NO_FILL = new AdError(1001, "No Fill");
    public static final AdError LOAD_TOO_FREQUENTLY = new AdError(1002, "Ad was re-loaded too frequently");
    public static final AdError SERVER_ERROR = new AdError(2000, "Server Error");
    public static final AdError INTERNAL_ERROR = new AdError(2001, "Internal Error");
    @Deprecated
    public static final AdError MISSING_PROPERTIES = new AdError(2002, "Native ad failed to load due to missing properties");

    Mopub
    EMPTY_AD_RESPONSE("Server returned empty response."),
    INVALID_JSON("Unable to parse JSON response from server."),
    IMAGE_DOWNLOAD_FAILURE("Unable to download images associated with ad."),
    INVALID_REQUEST_URL("Invalid request url."),
    UNEXPECTED_RESPONSE_CODE("Received unexpected response code from server."),
    SERVER_ERROR_RESPONSE_CODE("Server returned erroneous response code."),
    CONNECTION_ERROR("Network is unavailable."),
    UNSPECIFIED("Unspecified error occurred."),
    NETWORK_INVALID_REQUEST("Third-party network received invalid request."),
    NETWORK_TIMEOUT("Third-party network failed to respond in a timely manner."),
    NETWORK_NO_FILL("Third-party network failed to provide an ad."),
    NETWORK_INVALID_STATE("Third-party network failed due to invalid internal state."),
    NATIVE_ADAPTER_CONFIGURATION_ERROR("Custom Event Native was configured incorrectly."),
    NATIVE_ADAPTER_NOT_FOUND("Unable to find Custom Event Native.");

    Admob
    ERROR_CODE_INTERNAL_ERROR  0 Something happened internally; for instance, an invalid response was received from the ad server.
            ERROR_CODE_INVALID_REQUEST 1 The ad request was invalid; for instance, the ad unit ID was incorrect.
    ERROR_CODE_NETWORK_ERROR   2 The ad request was unsuccessful due to network connectivity.
            ERROR_CODE_NO_FILL         3 The ad request was successful, but no ad was returned due to lack of ad inventory.
            GENDER_FEMALE              4 Female gender.

    Picks

            无
   */
    /*
    //facebook ErrorCode  暂时不用了
    public static final int NETWORK_ERROR_CODE = 1000;
    public static final int NO_FILL_ERROR_CODE = 1001;
    public static final int LOAD_TOO_FREQUENTLY_ERROR_CODE = 1002;
    public static final int SERVER_ERROR_CODE = 2000;
    public static final int INTERNAL_ERROR_CODE = 2001;
    public static final int MISSING_PROPERTIES = 2002;
*/
    //mopub -- NativeErrorCode
    public static final int EMPTY_AD_RESPONSE = 3000;
    public static final int INVALID_JSON = 3001;
    public static final int IMAGE_DOWNLOAD_FAILURE = 3002;
    public static final int INVALID_REQUEST_URL = 3003;
    public static final int UNEXPECTED_RESPONSE_CODE = 3004;
    public static final int SERVER_ERROR_RESPONSE_CODE = 3005;
    public static final int CONNECTION_ERROR = 3006;
    public static final int UNSPECIFIED = 3007;
    public static final int NETWORK_INVALID_REQUEST = 3008;
    public static final int NETWORK_TIMEOUT = 3009;
    public static final int NETWORK_NO_FILL = 3010;
    public static final int NETWORK_INVALID_STATE = 3011;
    public static final int NATIVE_ADAPTER_CONFIGURATION_ERROR = 3012;
    public static final int NATIVE_ADAPTER_NOT_FOUND = 3013;
    //mopub -- MoPubErrorCode
    public static final int NO_FILL = 3014;
    public static final int WARMUP = 3015;
    public static final int SERVER_ERROR = 3016;
    public static final int NO_CONNECTION = 3017;
    public static final int CANCELLED = 3018;
    public static final int ADAPTER_NOT_FOUND = 3019;
    public static final int ADAPTER_CONFIGURATION_ERROR = 3020;
    public static final int MRAID_LOAD_ERROR = 3021;
    public static final int VIDEO_CACHE_ERROR = 3022;
    public static final int VIDEO_DOWNLOAD_ERROR = 3023;
    public static final int VIDEO_NOT_AVAILABLE = 3024;
    public static final int VIDEO_PLAYBACK_ERROR = 3025;
    public static final int LOADIMAGE_ERROR = 3026;
    public static final int BANNER_CREATE_ERROR = 3027;

  /*  //admob ErrorCode  暂时不用了
    public static final int ERROR_CODE_INTERNAL_ERROR = 4000;
    public static final int ERROR_CODE_INVALID_REQUEST = 4001;
    public static final int ERROR_CODE_NETWORK_ERROR = 4002;
    public static final int ERROR_CODE_NO_FILL = 4003;
    */

    public static final int NO_CONFIG_ERROR = 10001;
    public static final int NO_FILL_ERROR = 10002;
    //    public static final String NETWORK_ERROR = "network error";
//    public static final String INTERNAL_CONTEXT_ERROR = "internal_context_error";
    public static final int INTERNAL_ERROR = 10003;
    public static final int TIMEOUT_ERROR = 10004;
    public static final int NO_AD_TYPE_EROOR = 10005;
    public static final int NETWORK_ERROR = 10006;
    public static final int FREQUENCY_CONTROL = 10007;
    public static final int PICKS_LOAD_ERROR = 10008;
    public static final int PARAMS_ERROR = 10009;
    public static final int SIZE_ERROR = 10010;

    public static final String ERROR_CONFIG = "ssp adtype configured incorrectly";
    public static final String ERROR_RESPONSE_NULL = "response is null";
    public static final String VIDEO_TOO_LARGE = "video size too large";
//    public static final String INTERNAL_NO_SDK_ERROR = "no sdk error";
//    public static final String INTERNAL_NO_PARAMS_ERROR = "no params error";
//    public static final String NO_AD_TYPE_EROOR_INFO = "no ad type error";


    public static final int NO_VALID_DATA_ERROR = 20000;
    public static final int EXTERNAL_CONFIG_ERROR = 20001;
    public static final int DEEPLINK_ERROR = 20002;
    public static final int BANNER_LOADING = 20003;
    public static final int BANNER_SIZE_ERROR = 20004;
    public static final int BANNER_HTML_ERROR = 20005;



    public static final int VAST_PARAM_ERROR = 30000;
    public static final int VAST_NTEWORK_ERROR = 30001;
    public static final int VAST_LOADING_ERROR = 30002;
    public static final int VAST_NO_VALID_AD = 30003;
    public static final int VAST_DOWNLOAD_ERROR = 30004;
    public static final int VAST_TAG_ERROR = 30005;
    public static final int VAST_PARSE_MODEL_ERROR = 30006;
    public static final int VAST_NOT_WIFI = 30007;
    public static final int API_VERSION_INVALID = 30008;


    public static final int VIDEO_LOADING_ERROR = 40000;


/*mopub errorCode
    public enum  NativeErrorCode {
        EMPTY_AD_RESPONSE("Server returned empty response."),
        INVALID_RESPONSE("Unable to parse response from server."),
        IMAGE_DOWNLOAD_FAILURE("Unable to download images associated with ad."),
        INVALID_REQUEST_URL("Invalid request url."),
        UNEXPECTED_RESPONSE_CODE("Received unexpected response code from server."),
        SERVER_ERROR_RESPONSE_CODE("Server returned erroneous response code."),
        CONNECTION_ERROR("Network is unavailable."),
        UNSPECIFIED("Unspecified error occurred."),
        NETWORK_INVALID_REQUEST("Third-party network received invalid request."),
        NETWORK_TIMEOUT("Third-party network failed to respond in a timely manner."),
        NETWORK_NO_FILL("Third-party network failed to provide an ad."),
        NETWORK_INVALID_STATE("Third-party network failed due to invalid internal state."),
        NATIVE_RENDERER_CONFIGURATION_ERROR("A required renderer was not registered for the Custom Event Native."),
        NATIVE_ADAPTER_CONFIGURATION_ERROR("Native was configured incorrectly."),
        NATIVE_ADAPTER_NOT_FOUND("Unable to find Custom Event Native.");

        private final String message;

        NativeErrorCode(String message) {
            this.message = message;
        }

        @NonNull
        @Override
        public final String toString() {
            return message;
        }
    }
*/
}
