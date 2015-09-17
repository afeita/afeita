package com.github.afeita.net.ext.exception;

/**
 * Cookie相关格式异常
 * <br /> author: chenshufei
 * <br /> date: 15/9/13
 * <br /> email: chenshufei2@sina.com
 */
public class MalformedCookieException extends Exception {
    public MalformedCookieException() {
        super();
    }

    public MalformedCookieException(String detailMessage) {
        super(detailMessage);
    }

    public MalformedCookieException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public MalformedCookieException(Throwable throwable) {
        super(throwable);
    }
}
