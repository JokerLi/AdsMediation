package com.buffalo.utils.internal.report;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * This class consists exclusively of static methods for obtaining
 * encoders and decoders for the Base64 encoding scheme. The
 * implementation of this class supports the following types of Base64
 * as specified in
 * <a href="http://www.ietf.org/rfc/rfc4648.txt">RFC 4648</a> and
 * <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>.
 * <p>
 * <ul>
 * <li><a name="basic"><b>Basic</b></a>
 * <p> Uses "The Base64 Alphabet" as specified in Table 1 of
 * RFC 4648 and RFC 2045 for encoding and decoding operation.
 * The encoder does not add any line feed (line separator)
 * character. The decoder rejects data that contains characters
 * outside the base64 alphabet.</p></li>
 * <p>
 * <li><a name="url"><b>URL and Filename safe</b></a>
 * <p> Uses the "URL and Filename safe Base64 Alphabet" as specified
 * in Table 2 of RFC 4648 for encoding and decoding. The
 * encoder does not add any line feed (line separator) character.
 * The decoder rejects data that contains characters outside the
 * base64 alphabet.</p></li>
 * <p>
 * <li><a name="mime"><b>MIME</b></a>
 * <p> Uses the "The Base64 Alphabet" as specified in Table 1 of
 * RFC 2045 for encoding and decoding operation. The encoded output
 * must be represented in lines of no more than 76 characters each
 * and uses a carriage return {@code '\r'} followed immediately by
 * a linefeed {@code '\n'} as the line separator. No line separator
 * is added to the end of the encoded output. All line separators
 * or other characters not found in the base64 alphabet table are
 * ignored in decoding operation.</p></li>
 * </ul>
 * <p>
 * <p> Unless otherwise noted, passing a {@code null} argument to a
 * method of this class will cause a {@link NullPointerException
 * NullPointerException} to be thrown.
 *
 * @author Xueming Shen
 * @since 1.8
 */

public class AspBase64 {

    private AspBase64() {
    }

    /**
     * Returns a {@link Encoder} that encodes using the
     * <a href="#basic">Basic</a> type base64 encoding scheme.
     *
     * @return A Base64 encoder.
     */
    public static Encoder getEncoder() {
        return Encoder.RFC4648;
    }
    public static class Encoder {

        private final byte[] newline;
        private final int linemax;
        private final boolean isURL;
        private final boolean doPadding;

        private Encoder(boolean isURL, byte[] newline, int linemax, boolean doPadding) {
            this.isURL = isURL;
            this.newline = newline;
            this.linemax = linemax;
            this.doPadding = doPadding;
        }

        /**
         * This array is a lookup table that translates 6-bit positive integer
         * index values into their "Base64 Alphabet" equivalents as specified
         * in "Table 1: The Base64 Alphabet" of RFC 2045 (and RFC 4648).
         */
        private static final char[] toBase64 = {'0', 'K', 'a', 'j', 'D', '7', 'A', 'Z', 'c', 'F', '2', 'Q', 'n', 'P', 'r', '5', 'f', 'w', 'i', 'H', 'R', 'N', 'y', 'g', 'm', 'u', 'p', 'U', 'T', 'I', 'X', 'x', '6', '9', 'B', 'W', 'b', '-', 'h', 'M', 'C', 'G', 'J', 'o', '_', 'V', '8', 'E', 's', 'k', 'z', '1', 'Y', 'd', 'v', 'L', '3', '4', 'l', 'e', 't', 'q', 'S', 'O'
        };

        /**
         * It's the lookup table for "URL and Filename safe Base64" as specified
         * in Table 2 of the RFC 4648, with the '+' and '/' changed to '-' and
         * '_'. This table is used when BASE64_URL is specified.
         */
        private static final char[] toBase64URL = toBase64;

        static final Encoder RFC4648 = new Encoder(false, null, -1, true);

        private int outLength(int srclen) {
            int len;
            if (doPadding) {
                len = 4 * ((srclen + 2) / 3);
            } else {
                int n = srclen % 3;
                len = 4 * (srclen / 3) + (n == 0 ? 0 : n + 1);
            }
            if (linemax > 0)                                  // line separators
                len += (len - 1) / linemax * newline.length;
            return len;
        }

        /**
         * Encodes all bytes from the specified byte array into a newly-allocated
         * byte array using the {@link AspBase64} encoding scheme. The returned byte
         * array is of the length of the resulting bytes.
         *
         * @param src the byte array to encode
         * @return A newly-allocated byte array containing the resulting
         * encoded bytes.
         */
        public byte[] encode(byte[] src) {
            int len = outLength(src.length);          // dst array size
            byte[] dst = new byte[len];
            int ret = encode0(src, 0, src.length, dst);
            if (ret != dst.length)
                return Arrays.copyOf(dst, ret);
            return dst;
        }

        /**
         * Encodes the specified byte array into a String using the {@link AspBase64}
         * encoding scheme.
         * <p>
         * <p> This method first encodes all input bytes into a base64 encoded
         * byte array and then constructs a new String by using the encoded byte
         * array and the {@link StandardCharsets#ISO_8859_1
         * ISO-8859-1} charset.
         * <p>
         * <p> In other words, an invocation of this method has exactly the same
         * effect as invoking
         * {@code new String(encode(src), StandardCharsets.ISO_8859_1)}.
         *
         * @param src the byte array to encode
         * @return A String containing the resulting Base64 encoded characters
         */
        @SuppressWarnings("deprecation")
        public String encodeToString(byte[] src) {
            byte[] encoded = encode(src);
            return new String(encoded, 0, 0, encoded.length);
        }

        private int encode0(byte[] src, int off, int end, byte[] dst) {
            char[] base64 = isURL ? toBase64URL : toBase64;
            int sp = off;
            int slen = (end - off) / 3 * 3;
            int sl = off + slen;
            if (linemax > 0 && slen > linemax / 4 * 3)
                slen = linemax / 4 * 3;
            int dp = 0;
            while (sp < sl) {
                int sl0 = Math.min(sp + slen, sl);
                for (int sp0 = sp, dp0 = dp; sp0 < sl0; ) {
                    int bits = (src[sp0++] & 0xff) << 16 |
                            (src[sp0++] & 0xff) << 8 |
                            (src[sp0++] & 0xff);
                    dst[dp0++] = (byte) base64[(bits >>> 18) & 0x3f];
                    dst[dp0++] = (byte) base64[(bits >>> 12) & 0x3f];
                    dst[dp0++] = (byte) base64[(bits >>> 6) & 0x3f];
                    dst[dp0++] = (byte) base64[bits & 0x3f];
                }
                int dlen = (sl0 - sp) / 3 * 4;
                dp += dlen;
                sp = sl0;
                if (dlen == linemax && sp < end) {
                    for (byte b : newline) {
                        dst[dp++] = b;
                    }
                }
            }
            if (sp < end) {               // 1 or 2 leftover bytes
                int b0 = src[sp++] & 0xff;
                dst[dp++] = (byte) base64[b0 >> 2];
                if (sp == end) {
                    dst[dp++] = (byte) base64[(b0 << 4) & 0x3f];
                    if (doPadding) {
                        dst[dp++] = '=';
                        dst[dp++] = '=';
                    }
                } else {
                    int b1 = src[sp++] & 0xff;
                    dst[dp++] = (byte) base64[(b0 << 4) & 0x3f | (b1 >> 4)];
                    dst[dp++] = (byte) base64[(b1 << 2) & 0x3f];
                    if (doPadding) {
                        dst[dp++] = '=';
                    }
                }
            }
            return dp;
        }
    }
}