
package com.github.afeita.net.ext.cookie.iml;

import com.github.afeita.net.ext.InetAddressUtils;
import com.github.afeita.net.ext.cookie.ClientCookie;
import com.github.afeita.net.ext.cookie.CommonCookieAttributeHandler;
import com.github.afeita.net.ext.cookie.Cookie;
import com.github.afeita.net.ext.cookie.CookieOrigin;
import com.github.afeita.net.ext.cookie.SetCookie;
import com.github.afeita.net.ext.exception.MalformedCookieException;

import java.util.Locale;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class BasicDomainHandler implements CommonCookieAttributeHandler{

    @Override
    public void parse(final SetCookie cookie, final String value)
            throws MalformedCookieException {
        if (null == value || value.trim().length() <=0) {
            throw new MalformedCookieException("Blank or null value for domain attribute");
        }
        // Ignore domain attributes ending with '.' per RFC 6265, 4.1.2.3
        if (value.endsWith(".")) {
            return;
        }
        String domain = value;
        if (domain.startsWith(".")) {
            domain = domain.substring(1);
        }
        domain = domain.toLowerCase(Locale.ROOT);
        cookie.setDomain(domain);
    }

    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin)
            throws MalformedCookieException {
        // Validate the cookies domain attribute.  NOTE:  Domains without
        // any dots are allowed to support hosts on private LANs that don't
        // have DNS names.  Since they have no dots, to domain-match the
        // request-host and domain must be identical for the cookie to sent
        // back to the origin-server.
        final String host = origin.getHost();
        final String domain = cookie.getDomain();
        if (domain == null) {
            throw new MalformedCookieException("Cookie 'domain' may not be null");
        }
        if (!host.equals(domain) && !domainMatch(domain, host)) {
            throw new MalformedCookieException(
                    "Illegal 'domain' attribute \"" + domain + "\". Domain of origin: \"" + host + "\"");
        }
    }

    static boolean domainMatch(final String domain, final String host) {
        if (InetAddressUtils.isIPv4Address(host) || InetAddressUtils.isIPv6Address(host)) {
            return false;
        }
        final String normalizedDomain = domain.startsWith(".") ? domain.substring(1) : domain;
        if (host.endsWith(normalizedDomain)) {
            final int prefix = host.length() - normalizedDomain.length();
            // Either a full match or a prefix endidng with a '.'
            if (prefix == 0) {
                return true;
            }
            if (prefix > 1 && host.charAt(prefix - 1) == '.') {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        String host = origin.getHost();
        String domain = cookie.getDomain();
        if (domain == null) {
            return false;
        }
        if (domain.startsWith(".")) {
            domain = domain.substring(1);
        }
        domain = domain.toLowerCase(Locale.ROOT);
        if (host.equals(domain)) {
            return true;
        }
        boolean retValue = false;
        if (cookie instanceof ClientCookie) {
            if (((ClientCookie) cookie).containsAttribute(ClientCookie.DOMAIN_ATTR)) {
                retValue =  domainMatch(domain, host);
            }
        }
        host = host.toLowerCase(Locale.ROOT);
        //注意需要支持host:app.huijiacn.com与domain:huijiacn.com的匹配的支持
        if (host.length() > domain.length() && host.contains(domain) && !InetAddressUtils.isIPv4Address(host)){
            return true;
        }
        return retValue;
    }

    @Override
    public String getAttributeName() {
        return ClientCookie.DOMAIN_ATTR;
    }
}
