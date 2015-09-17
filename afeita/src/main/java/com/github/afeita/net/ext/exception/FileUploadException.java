package com.github.afeita.net.ext.exception;

/**
 * 文件上传类的相关异常
 * <br /> author: chenshufei
 * <br /> date: 15/9/4
 * <br /> email: chenshufei2@sina.com
 */
public class FileUploadException extends RuntimeException {

    public FileUploadException() {
        super();
    }

    public FileUploadException(String detailMessage) {
        super(detailMessage);
    }

    public FileUploadException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public FileUploadException(Throwable throwable) {
        super(throwable);
    }
}
