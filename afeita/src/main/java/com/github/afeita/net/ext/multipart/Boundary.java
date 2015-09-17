package com.github.afeita.net.ext.multipart;

import android.text.TextUtils;

import java.util.Random;

/**
 * 上传分隔部分
 * <br /> author: chenshufei
 * <br /> date: 15/9/6
 * <br /> email: chenshufei2@sina.com
 */
public class Boundary {

    /* 字符集用于随机产生字符 */
    private final static char[] MULTIPART_CHARS =
            "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final String boundary;
    private final byte[] startingBoundary;
    private final byte[] closingBoundary;

    public Boundary(String boundary) {
        if (TextUtils.isEmpty(boundary)) {
            boundary = generateBoundary();
        }
        this.boundary = boundary;

        final String starting = "--" + boundary + MultipartEntity.CRLF;
        final String closing  = "--" + boundary + "--" + MultipartEntity.CRLF;
        startingBoundary = EncodingUtilsHelper.getAsciiBytes(starting);
        closingBoundary  = EncodingUtilsHelper.getAsciiBytes(closing);
    }

    public String getBoundary() {
        return boundary;
    }

    public byte[] getStartingBoundary() {
        return startingBoundary;
    }

    public byte[] getClosingBoundary() {
        return closingBoundary;
    }

    private static String generateBoundary() {
        Random rand = new Random();
        final int count = rand.nextInt(11) + 20; // a random size from 20 to 30
        StringBuilder buffer = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }
}
