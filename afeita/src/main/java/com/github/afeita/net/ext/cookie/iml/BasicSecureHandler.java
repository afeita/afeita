
package com.github.afeita.net.ext.cookie.iml;

import com.github.afeita.net.ext.cookie.ClientCookie;
import com.github.afeita.net.ext.cookie.Cookie;
import com.github.afeita.net.ext.cookie.CookieOrigin;
import com.github.afeita.net.ext.cookie.SetCookie;
import com.github.afeita.net.ext.exception.MalformedCookieException;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class BasicSecureHandler extends AbstractCookieAttributeHandler{
    @Override
    public void parse(final SetCookie cookie, final String value)
            throws MalformedCookieException {
        cookie.setSecure(true);
    }

    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        return !cookie.isSecure() || origin.isSecure();
    }

    @Override
    public String getAttributeName() {
        return ClientCookie.SECURE_ATTR;
    }
}
