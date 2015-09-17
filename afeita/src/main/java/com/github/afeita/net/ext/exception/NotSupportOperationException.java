package com.github.afeita.net.ext.exception;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/7
 * <br /> email: chenshufei2@sina.com
 */
public class NotSupportOperationException extends RuntimeException{

    public NotSupportOperationException() {
        super();
    }

    public NotSupportOperationException(String detailMessage) {
        super(detailMessage);
    }

    public NotSupportOperationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NotSupportOperationException(Throwable throwable) {
        super(throwable);
    }
}
