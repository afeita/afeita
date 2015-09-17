
package com.github.afeita.net.ext.cookie.iml;

import com.github.afeita.net.ext.cookie.ClientCookie;
import com.github.afeita.net.ext.cookie.SetCookie;
import com.github.afeita.net.ext.exception.MalformedCookieException;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class BasicCommentHandler extends AbstractCookieAttributeHandler {
    @Override
    public void parse(final SetCookie cookie, final String value)
            throws MalformedCookieException {
        cookie.setComment(value);
    }

    @Override
    public String getAttributeName() {
        return ClientCookie.COMMENT_ATTR;
    }
}
