
package com.github.afeita.net.ext.cookie.iml;

import com.github.afeita.net.ext.cookie.ClientCookie;
import com.github.afeita.net.ext.cookie.CommonCookieAttributeHandler;
import com.github.afeita.net.ext.cookie.Cookie;
import com.github.afeita.net.ext.cookie.CookieOrigin;
import com.github.afeita.net.ext.cookie.SetCookie;
import com.github.afeita.net.ext.cookie.SetCookie2;
import com.github.afeita.net.ext.exception.MalformedCookieException;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class BasicCommentUrlHandler implements CommonCookieAttributeHandler{
    @Override
    public void parse(final SetCookie cookie, final String commenturl)
            throws MalformedCookieException {
        if (cookie instanceof SetCookie2) {
            final SetCookie2 cookie2 = (SetCookie2) cookie;
            cookie2.setCommentURL(commenturl);
        }
    }

    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin)
            throws MalformedCookieException {
    }

    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        return true;
    }

    @Override
    public String getAttributeName() {
        return ClientCookie.COMMENTURL_ATTR;
    }
}
