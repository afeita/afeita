package com.github.afeita.net.ext.cookie;

import java.util.Date;

/**
 * Cookie 是 用来客户端与服务端 保持session会话
 * <br />它是一种简单的表单，以键值key-value形式保存的
 * <br /> author: chenshufei
 * <br /> date: 15/9/13
 * <br /> email: chenshufei2@sina.com
 */
public interface Cookie {

    /**
     * Returns the name.
     *
     * @return String name The name
     */
    String getName();

    /**
     * Returns the value.
     *
     * @return String value The current value.
     */
    String getValue();

    /**
     * Returns the comment describing the purpose of this cookie, or
     * {@code null} if no such comment has been defined.
     *
     * @return comment
     */
    String getComment();

    /**
     * If a user agent (web browser) presents this cookie to a user, the
     * cookie's purpose will be described by the information at this URL.
     */
    String getCommentURL();

    /**
     * Returns the expiration {@link Date} of the cookie, or {@code null}
     * if none exists.
     * <p><strong>Note:</strong> the object returned by this method is
     * considered immutable. Changing it (e.g. using setTime()) could result
     * in undefined behaviour. Do so at your peril. </p>
     * @return Expiration {@link Date}, or {@code null}.
     */
    Date getExpiryDate();

    /**
     * Returns {@code false} if the cookie should be discarded at the end
     * of the "session"; {@code true} otherwise.
     *
     * @return {@code false} if the cookie should be discarded at the end
     *         of the "session"; {@code true} otherwise
     */
    boolean isPersistent();

    /**
     * Returns domain attribute of the cookie. The value of the Domain
     * attribute specifies the domain for which the cookie is valid.
     *
     * @return the value of the domain attribute.
     */
    String getDomain();

    /**
     * Returns the path attribute of the cookie. The value of the Path
     * attribute specifies the subset of URLs on the origin server to which
     * this cookie applies.
     *
     * @return The value of the path attribute.
     */
    String getPath();

    /**
     * Get the Port attribute. It restricts the ports to which a cookie
     * may be returned in a Cookie request header.
     */
    int[] getPorts();

    /**
     * Indicates whether this cookie requires a secure connection.
     *
     * @return  {@code true} if this cookie should only be sent
     *          over secure connections, {@code false} otherwise.
     */
    boolean isSecure();

    /**
     * Returns the version of the cookie specification to which this
     * cookie conforms.
     *
     * @return the version of the cookie.
     */
    int getVersion();

    /**
     * Returns true if this cookie has expired.
     * @param date Current time
     *
     * @return {@code true} if the cookie has expired.
     */
    boolean isExpired(final Date date);
}
