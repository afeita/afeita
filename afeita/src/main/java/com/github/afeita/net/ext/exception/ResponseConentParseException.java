package com.github.afeita.net.ext.exception;

/**
 * 请求的响应内容，解析异常。通常是定义的实体Class与响应的json或xml属性不配对解析不了
 * <br /> author: chenshufei
 * <br /> date: 15/9/10
 * <br /> email: chenshufei2@sina.com
 */
public class ResponseConentParseException extends RuntimeException {

    public ResponseConentParseException() {
        super();
    }

    public ResponseConentParseException(String detailMessage) {
        super(detailMessage);
    }

    public ResponseConentParseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ResponseConentParseException(Throwable throwable) {
        super(throwable);
    }
}
