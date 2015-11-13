package com.github.afeita.filemanager.mutildownloader.cons;

/**
 * HTTP参数枚举类
 * HTTP Parameters.
 * <br /> author: chenshufei
 * <br /> date: 15/10/4
 * <br /> email: chenshufei2@sina.com
 */
public enum HttpConnPars {
    GET("GET"),
    ACCEPT("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*"),
    ACCEPT_LANGUAGE("Accept-Language", "zh-CN"),
    ACCEPT_RANGE("Accept-Ranges", "bytes"),
    CHARSET("Charset", "UTF-8"),
    CONNECT_TIMEOUT("20000"),
    KEEP_CONNECT("Connection", "Keep-Alive"),
    LOCATION("location"),
    REFERER("referer");

    public String header;// 标题
    public String content;// 内容

    HttpConnPars(String header, String content) {
        this.header = header;
        this.content = content;
    }

    HttpConnPars(String content) {
        this.content = content;
    }
}
