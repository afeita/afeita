package com.github.afeita.net.ext.exception;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/6
 * <br /> email: chenshufei2@sina.com
 */
public class ReadIOException extends RuntimeException {
    public ReadIOException() {
        super();
    }

    public ReadIOException(String detailMessage) {
        super(detailMessage);
    }

    public ReadIOException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ReadIOException(Throwable throwable) {
        super(throwable);
    }
}
