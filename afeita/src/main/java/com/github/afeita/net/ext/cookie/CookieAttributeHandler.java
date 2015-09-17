
package com.github.afeita.net.ext.cookie;

import com.github.afeita.net.ext.exception.MalformedCookieException;

/**
 * 响应的原始value设置进cookie中
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public interface CookieAttributeHandler {
    /**
     * Parse the given cookie attribute value and update the corresponding
     * {@link com.github.afeita.net.ext.cookie.Cookie} property.
     *
     * @param cookie {@link com.github.afeita.net.ext.cookie.Cookie} to be updated
     * @param value cookie attribute value from the cookie response header
     */
    void parse(SetCookie cookie, String value)
            throws MalformedCookieException;

    /**
     * Peforms cookie validation for the given attribute value.
     *
     * @param cookie {@link com.github.afeita.net.ext.cookie.Cookie} to validate
     * @param origin the cookie source to validate against
     * @throws MalformedCookieException if cookie validation fails for this attribute
     */
    void validate(Cookie cookie, CookieOrigin origin)
            throws MalformedCookieException;

    /**
     * Matches the given value (property of the destination host where request is being
     * submitted) with the corresponding cookie attribute.
     *
     * @param cookie {@link com.github.afeita.net.ext.cookie.Cookie} to match
     * @param origin the cookie source to match against
     * @return {@code true} if the match is successful; {@code false} otherwise
     */
    boolean match(Cookie cookie, CookieOrigin origin);

}
