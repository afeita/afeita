
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
public class BasicVersionHandler extends AbstractCookieAttributeHandler{
    @Override
    public void parse(final SetCookie cookie, final String value)
            throws MalformedCookieException {
        if (value == null) {
            throw new MalformedCookieException("Missing value for version attribute");
        }
        if (value.trim().isEmpty()) {
            throw new MalformedCookieException("Blank value for version attribute");
        }
        try {
            cookie.setVersion(Integer.parseInt(value));
        } catch (final NumberFormatException e) {
            throw new MalformedCookieException("Invalid version: "
                    + e.getMessage());
        }
    }

    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin)
            throws MalformedCookieException {
        if (cookie.getVersion() < 0) {
            throw new MalformedCookieException("Cookie version may not be negative");
        }
    }

    @Override
    public String getAttributeName() {
        return ClientCookie.VERSION_ATTR;
    }
}
