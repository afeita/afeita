package com.github.afeita.sample.netsample.bean;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/11
 * <br /> email: chenshufei2@sina.com
 */
public class Message {

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }


}
