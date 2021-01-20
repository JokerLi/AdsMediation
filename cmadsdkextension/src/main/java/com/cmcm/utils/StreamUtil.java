package com.cmcm.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {
    public static void copyContent(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        if (inputStream == null || outputStream == null) {
            throw new IOException("Unable to copy from or to a null stream.");
        }

        byte[] buffer = new byte[16384];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
    }

    public static void readStream(final InputStream inputStream, byte[] buffer) throws IOException {
        int offset = 0;
        int bytesRead = 0;
        int maxBytes = buffer.length;
        while ((bytesRead = inputStream.read(buffer, offset, maxBytes)) != -1) {
            offset += bytesRead;
            maxBytes -= bytesRead;
            if (maxBytes <= 0) {
                return;
            }
        }
    }

    public static void closeStream(Closeable stream) {
        if (stream == null) {
            return;
        }

        try {
            stream.close();
        } catch (IOException e) {
            // Unable to close the stream
        }
    }
}
