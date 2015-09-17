package com.github.afeita.sample.netsample.bean;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/11
 * <br /> email: chenshufei2@sina.com
 */
public class UserInfo {
    private String username;
    private String email;
    private String picUrl;
    private String mobileNum;
    //....


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", mobileNum='" + mobileNum + '\'' +
                '}';
    }
}
