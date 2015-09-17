package com.github.afeita.net.ext.cookie;

import java.util.Locale;

/**
 * details of an origin server that are relevant when parsing,
 * validating or matching HTTP cookies.
 * 从CookieStore将与此http匹配的符合规范Cookie带给服务端
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class CookieOrigin {
    private final String host;
    private final int port;
    private final String path;
    private final boolean secure;

    public CookieOrigin(final String host, final int port, final String path, final boolean secure) {
        super();
        this.host = host.toLowerCase(Locale.ROOT);
        this.port = port;
        if (null != path && path.length() > 0) {
            this.path = path;
        } else {
            this.path = "/";
        }
        this.secure = secure;
    }

    public String getHost() {
        return this.host;
    }

    public String getPath() {
        return this.path;
    }

    public int getPort() {
        return this.port;
    }

    public boolean isSecure() {
        return this.secure;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        if (this.secure) {
            buffer.append("(secure)");
        }
        buffer.append(this.host);
        buffer.append(':');
        buffer.append(Integer.toString(this.port));
        buffer.append(this.path);
        buffer.append(']');
        return buffer.toString();
    }
}
