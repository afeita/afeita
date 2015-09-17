
package com.github.afeita.net.ext.cookie.iml;

import com.github.afeita.net.ext.cookie.ClientCookie;
import com.github.afeita.net.ext.cookie.CommonCookieAttributeHandler;
import com.github.afeita.net.ext.cookie.Cookie;
import com.github.afeita.net.ext.cookie.CookieOrigin;
import com.github.afeita.net.ext.cookie.SetCookie;
import com.github.afeita.net.ext.exception.MalformedCookieException;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class BasicPathHandler implements CommonCookieAttributeHandler {
    @Override
    public void parse(
            final SetCookie cookie, final String value) throws MalformedCookieException {
        boolean isNotEmpty = (null != value && value.length() > 0);
        cookie.setPath(isNotEmpty ? value : "/");
    }

    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin)
            throws MalformedCookieException {
        if (!match(cookie, origin)) {
            throw new MalformedCookieException(
                    "Illegal 'path' attribute \"" + cookie.getPath()
                            + "\". Path of origin: \"" + origin.getPath() + "\"");
        }
    }

    static boolean pathMatch(final String uriPath, final String cookiePath) {
        String normalizedCookiePath = cookiePath;
        if (normalizedCookiePath == null) {
            normalizedCookiePath = "/";
        }
        if (normalizedCookiePath.length() > 1 && normalizedCookiePath.endsWith("/")) {
            normalizedCookiePath = normalizedCookiePath.substring(0, normalizedCookiePath.length() - 1);
        }
        if (uriPath.startsWith(normalizedCookiePath)) {
            if (normalizedCookiePath.equals("/")) {
                return true;
            }
            if (uriPath.length() == normalizedCookiePath.length()) {
                return true;
            }
            if (uriPath.charAt(normalizedCookiePath.length()) == '/') {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        return pathMatch(origin.getPath(), cookie.getPath());
    }

    @Override
    public String getAttributeName() {
        return ClientCookie.PATH_ATTR;
    }
}
