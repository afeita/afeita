package com.github.afeita.net.ext.exception;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/6
 * <br /> email: chenshufei2@sina.com
 */
public class NotSupportDataFormatException extends Exception {
    public NotSupportDataFormatException() {
        super();
    }

    public NotSupportDataFormatException(String detailMessage) {
        super(detailMessage);
    }

    public NotSupportDataFormatException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NotSupportDataFormatException(Throwable throwable) {
        super(throwable);
    }
}
