
package com.github.afeita.net.ext.cookie.iml;

import com.github.afeita.net.ext.cookie.CommonCookieAttributeHandler;
import com.github.afeita.net.ext.cookie.Cookie;
import com.github.afeita.net.ext.cookie.CookieOrigin;
import com.github.afeita.net.ext.exception.MalformedCookieException;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public abstract class AbstractCookieAttributeHandler implements CommonCookieAttributeHandler{
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin)
            throws MalformedCookieException {
        // Do nothing
    }

    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        // Always match
        return true;
    }
}
