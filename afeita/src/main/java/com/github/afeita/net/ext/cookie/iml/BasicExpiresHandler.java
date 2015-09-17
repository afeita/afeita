
package com.github.afeita.net.ext.cookie.iml;

import com.github.afeita.net.ext.cookie.ClientCookie;
import com.github.afeita.net.ext.cookie.SetCookie;
import com.github.afeita.net.ext.exception.MalformedCookieException;
import com.github.afeita.tools.DateTimeUtil;

import java.util.Date;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class BasicExpiresHandler extends AbstractCookieAttributeHandler {
    private final String[]  datepatterns;

    private final String[] DEFAULT_DATEPATTERNS = {
        "EEE, dd-MMM-yy HH:mm:ss z", //默认以Cookie0 Netscape公司的标准，获取不到时，尝试其它的格式
        "EEE, dd MMM yyyy HH:mm:ss zzz", //  RFC 1123 format
        "EEE, dd-MMM-yy HH:mm:ss zzz", // RFC 1036 format
        "EEE MMM d HH:mm:ss yyyy" //ANSI C
    };

    public BasicExpiresHandler(final String[] datepatterns) {
        if (null == datepatterns || datepatterns.length <=0 ){
            this.datepatterns = DEFAULT_DATEPATTERNS;
        }else {
            this.datepatterns = datepatterns;
        }
    }

    @Override
    public void parse(final SetCookie cookie, final String value)
            throws MalformedCookieException {
        if (value == null) {
            throw new MalformedCookieException("Missing value for 'expires' attribute");
        }
        Date expiry = null;
        for (String datepattern : datepatterns) {
            expiry = DateTimeUtil.parseDateTime(value,datepattern);
            if (null != expiry){
                break;
            }
        }
        if (null == expiry){
            throw new MalformedCookieException("Invalid 'expires' attribute: "
                    + value);
        }
        cookie.setExpiryDate(expiry);
    }

    @Override
    public String getAttributeName() {
        return ClientCookie.EXPIRES_ATTR;
    }
}
