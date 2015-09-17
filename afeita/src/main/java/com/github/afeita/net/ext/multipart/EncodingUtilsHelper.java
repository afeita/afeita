package com.github.afeita.net.ext.multipart;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/6
 * <br /> email: chenshufei2@sina.com
 */
public class EncodingUtilsHelper {

    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset ASCII = Charset.forName("US-ASCII");
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    /**
     * URLEncode...,默认以utf-8 编码
     * @param content
     * @param encoding
     * @return
     */
    public static String encode(final String content, final String encoding) {
        try {
            return URLEncoder.encode(content, encoding != null ? encoding : "utf-8");
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

    public static String getAsciiString(final byte[] data, final int offset, final int length) {
        return new String(data, offset, length, ASCII);
    }

    public static byte[] getAsciiBytes(final String data) {
        return data.getBytes(ASCII);
    }
}