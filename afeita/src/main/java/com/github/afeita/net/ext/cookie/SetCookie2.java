package com.github.afeita.net.ext.cookie;

/**
 * Cookies版本1(RFC2965)，支持的属性操作...
 * <br /> author: chenshufei
 * <br /> date: 15/9/13
 * <br /> email: chenshufei2@sina.com
 */
public interface SetCookie2 extends SetCookie {
    /**
     * If a user agent (web browser) presents this cookie to a user, the
     * cookie's purpose will be described by the information at this URL.
     */
    void setCommentURL(String commentURL);

    /**
     * Sets the Port attribute. It restricts the ports to which a cookie
     * may be returned in a Cookie request header.
     */
    void setPorts(int[] ports);

    /**
     * Set the Discard attribute.
     *
     * Note: {@code Discard} attribute overrides {@code Max-age}.
     *
     * @see #isPersistent()
     */
    void setDiscard(boolean discard);


}
