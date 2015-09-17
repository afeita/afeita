package com.github.afeita.net.ext.exception;

/**
 * 请求BodyContent设置错的异常
 * 比如在同一请求中，既设setParams又设置setBodyContent
 * <br /> author: chenshufei
 * <br /> date: 15/9/2
 * <br /> email: chenshufei2@sina.com
 */
public class RequestBodyContentSettingException extends RuntimeException {

    public RequestBodyContentSettingException() {
        super();
    }

    public RequestBodyContentSettingException(String detailMessage) {
        super(detailMessage);
    }

    public RequestBodyContentSettingException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RequestBodyContentSettingException(Throwable throwable) {
        super(throwable);
    }
}
