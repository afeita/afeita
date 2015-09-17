
package com.github.afeita.net.ext.cookie.iml;

import com.github.afeita.net.ext.cookie.ClientCookie;
import com.github.afeita.net.ext.cookie.SetCookie;
import com.github.afeita.net.ext.exception.MalformedCookieException;

import java.util.Date;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class BasicMaxAgeHandler extends AbstractCookieAttributeHandler {

    @Override
    public void parse(final SetCookie cookie, final String value)
            throws MalformedCookieException {
        if (value == null) {
            throw new MalformedCookieException("Missing value for 'max-age' attribute");
        }
        final int age;
        try {
            age = Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw new MalformedCookieException ("Invalid 'max-age' attribute: "
                    + value);
        }
        if (age < 0) {
            throw new MalformedCookieException("Negative 'max-age' attribute: "
                    + value);
        }
        cookie.setExpiryDate(new Date(System.currentTimeMillis() + age * 1000L));
    }

    @Override
    public String getAttributeName() {
        return ClientCookie.MAX_AGE_ATTR;
    }

}
