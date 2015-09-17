
package com.github.afeita.net.ext.cookie.iml;

import com.github.afeita.net.ext.cookie.ClientCookie;
import com.github.afeita.net.ext.cookie.CommonCookieAttributeHandler;
import com.github.afeita.net.ext.cookie.Cookie;
import com.github.afeita.net.ext.cookie.CookieOrigin;
import com.github.afeita.net.ext.cookie.SetCookie;
import com.github.afeita.net.ext.cookie.SetCookie2;
import com.github.afeita.net.ext.exception.MalformedCookieException;

import java.util.StringTokenizer;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class BasicPortHandler implements CommonCookieAttributeHandler{

    /**
     * Parses the given Port attribute value (e.g. "8000,8001,8002")
     * into an array of ports.
     *
     * @param portValue port attribute value
     * @return parsed array of ports
     * @throws MalformedCookieException if there is a problem in
     *          parsing due to invalid portValue.
     */
    private static int[] parsePortAttribute(final String portValue)
            throws MalformedCookieException {
        final StringTokenizer st = new StringTokenizer(portValue, ",");
        final int[] ports = new int[st.countTokens()];
        try {
            int i = 0;
            while(st.hasMoreTokens()) {
                ports[i] = Integer.parseInt(st.nextToken().trim());
                if (ports[i] < 0) {
                    throw new MalformedCookieException ("Invalid Port attribute.");
                }
                ++i;
            }
        } catch (final NumberFormatException e) {
            throw new MalformedCookieException ("Invalid Port "
                    + "attribute: " + e.getMessage());
        }
        return ports;
    }

    /**
     * Returns {@code true} if the given port exists in the given
     * ports list.
     *
     * @param port port of host where cookie was received from or being sent to.
     * @param ports port list
     * @return true returns {@code true} if the given port exists in
     *         the given ports list; {@code false} otherwise.
     */
    private static boolean portMatch(final int port, final int[] ports) {
        boolean portInList = false;
        for (final int port2 : ports) {
            if (port == port2) {
                portInList = true;
                break;
            }
        }
        return portInList;
    }

    /**
     * Parse cookie port attribute.
     */
    @Override
    public void parse(final SetCookie cookie, final String portValue)
            throws MalformedCookieException {
        if (cookie instanceof SetCookie2) {
            final SetCookie2 cookie2 = (SetCookie2) cookie;
            if (portValue != null && !portValue.trim().isEmpty()) {
                final int[] ports = parsePortAttribute(portValue);
                cookie2.setPorts(ports);
            }
        }
    }

    /**
     * Validate cookie port attribute. If the Port attribute was specified
     * in header, the request port must be in cookie's port list.
     */
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin)
            throws MalformedCookieException {
        final int port = origin.getPort();
        if (cookie instanceof ClientCookie
                && ((ClientCookie) cookie).containsAttribute(ClientCookie.PORT_ATTR)) {
            if (!portMatch(port, cookie.getPorts())) {
                throw new MalformedCookieException(
                        "Port attribute violates RFC 2965: "
                                + "Request port not found in cookie's port list.");
            }
        }
    }

    /**
     * Match cookie port attribute. If the Port attribute is not specified
     * in header, the cookie can be sent to any port. Otherwise, the request port
     * must be in the cookie's port list.
     */
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        final int port = origin.getPort();
        if (cookie instanceof ClientCookie
                && ((ClientCookie) cookie).containsAttribute(ClientCookie.PORT_ATTR)) {
            if (cookie.getPorts() == null) {
                // Invalid cookie state: port not specified
                return false;
            }
            if (!portMatch(port, cookie.getPorts())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getAttributeName() {
        return ClientCookie.PORT_ATTR;
    }
}
