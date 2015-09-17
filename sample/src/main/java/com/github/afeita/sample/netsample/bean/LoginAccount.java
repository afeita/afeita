package com.github.afeita.sample.netsample.bean;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/11
 * <br /> email: chenshufei2@sina.com
 */
public class LoginAccount {

    private String username;
    private String password;

    public LoginAccount(){}

    public LoginAccount(String username,String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginAccount{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
