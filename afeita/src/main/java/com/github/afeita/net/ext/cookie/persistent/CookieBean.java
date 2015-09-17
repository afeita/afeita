package com.github.afeita.net.ext.cookie.persistent;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class CookieBean {
    private String id;
    private String name;
    private String value;
    private String comment;
    private String commentUrl;
    //-- expired与max-age统一了，都放在这,有max-age就优先放这个,将date转在milliseconds毫秒持久化数据库
    private String expiryDatetime;
    private String domain;
    private String path;
    private String ports; //int[] ,中间有,隔开
    private String isSecure;
    private String version;
    private String isDiscard;  //true指示退出应用时马上丢掉此cookie，

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentUrl() {
        return commentUrl;
    }

    public void setCommentUrl(String commentUrl) {
        this.commentUrl = commentUrl;
    }

    public String getExpiryDatetime() {
        return expiryDatetime;
    }

    public void setExpiryDatetime(String expiryDatetime) {
        this.expiryDatetime = expiryDatetime;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPorts() {
        return ports;
    }

    public void setPorts(String ports) {
        this.ports = ports;
    }

    public String getIsSecure() {
        return isSecure;
    }

    public void setIsSecure(String isSecure) {
        this.isSecure = isSecure;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIsDiscard() {
        return isDiscard;
    }

    public void setIsDiscard(String isDiscard) {
        this.isDiscard = isDiscard;
    }

    @Override
    public String toString() {
        return "CookieBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", comment='" + comment + '\'' +
                ", commentUrl='" + commentUrl + '\'' +
                ", expiryDatetime='" + expiryDatetime + '\'' +
                ", domain='" + domain + '\'' +
                ", path='" + path + '\'' +
                ", ports='" + ports + '\'' +
                ", isSecure='" + isSecure + '\'' +
                ", version='" + version + '\'' +
                ", isDiscard='" + isDiscard + '\'' +
                '}';
    }
}
