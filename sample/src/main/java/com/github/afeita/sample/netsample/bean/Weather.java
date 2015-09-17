package com.github.afeita.sample.netsample.bean;

/**
 * Created by chenshufei on 15/8/31.
 */
public class Weather {

    private String resultcode;
    private String reason;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "resultcode='" + resultcode + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
