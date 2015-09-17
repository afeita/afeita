package com.github.afeita.net.ext.cookie;

import java.util.Arrays;
import java.util.Date;

/**
 * SetCookie2的实现，一般用BasicClientCookie2处理Cookie，防止服务端坑，设置了discard
 * <br /> author: chenshufei
 * <br /> date: 15/9/13
 * <br /> email: chenshufei2@sina.com
 */
public class BasicClientCookie2 extends BasicClientCookie implements SetCookie2 {

    private String commentURL;
    private int[] ports;
    private boolean discard;

    /**
     * Default Constructor taking a name and a value. The value may be null.
     *
     * @param name The name.
     * @param value The value.
     */
    public BasicClientCookie2(final String name, final String value) {
        super(name, value);
    }

    @Override
    public int[] getPorts() {
        return this.ports;
    }

    @Override
    public void setPorts(final int[] ports) {
        this.ports = ports;
    }

    @Override
    public String getCommentURL() {
        return this.commentURL;
    }

    @Override
    public void setCommentURL(final String commentURL) {
        this.commentURL = commentURL;
    }

    @Override
    public void setDiscard(final boolean discard) {
        this.discard = discard;
    }

    @Override
    public boolean isPersistent() {
        return !this.discard && super.isPersistent();
    }

    @Override
    public boolean isExpired(final Date date) {
        return this.discard || super.isExpired(date);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        final BasicClientCookie2 clone = (BasicClientCookie2) super.clone();
        if (this.ports != null) {
            clone.ports = this.ports.clone();
        }
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        BasicClientCookie2 that = (BasicClientCookie2) o;

        if (discard != that.discard)
            return false;
        if (commentURL != null ? !commentURL.equals(that.commentURL) : that.commentURL != null)
            return false;
        return Arrays.equals(ports, that.ports);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (commentURL != null ? commentURL.hashCode() : 0);
        result = 31 * result + (ports != null ? Arrays.hashCode(ports) : 0);
        result = 31 * result + (discard ? 1 : 0);
        return result;
    }
}
