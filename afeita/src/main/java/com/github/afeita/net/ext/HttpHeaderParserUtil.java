package com.github.afeita.net.ext;

import com.github.afeita.net.Cache;
import com.github.afeita.net.NetworkResponse;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.protocol.HTTP;

import java.util.Map;

/**
 * Http Header 解析工具类，提供默认的即时缓存及最大缓存时间，
 * 并可设置 即时缓存及最大缓存时间
 * <br /> author: chenshufei
 * <br /> date: 15/9/4
 * <br /> email: chenshufei2@sina.com
 */
public class HttpHeaderParserUtil {

    private static long softExpireGap = 3 * 60 * 1000;  //即时缓存默认是3分钟
    private static long finalExpireGap = 8 * 60 * 60 * 1000; //最大缓存是8小时，最大缓存会刷新网络

    /**
     * 设置即时缓存时间，单位mm毫秒
     * @param expire
     */
    public static void setSoftExpireGap(long expire){
        softExpireGap = expire;
    }

    /**
     * 设置最大缓存时间，单位mm毫秒
     * @param expire
     */
    public static void setFinalExpireGap(long expire){
        finalExpireGap = expire;
    }

    /**
     * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
     *
     * @param response The network response to parse headers from
     * @return a cache entry for the given response, or null if the response is not cacheable.
     */
    public static Cache.Entry parseCacheHeaders(NetworkResponse response) {
        return parseCacheHeaders(response,softExpireGap,finalExpireGap);
    }


    /**
     * 获取指定即时缓存与最大缓存时间的，将响应数据缓存起来
     * @param response
     * @param softExpireGap
     * @param finalExpireGap
     * @return
     */
    public static Cache.Entry parseCacheHeaders(NetworkResponse response,long softExpireGap,long finalExpireGap) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;

        long serverDate = 0;
        long lastModified = 0;
        long serverExpires = 0;
        long softExpire = 0;
        long finalExpire = 0;
        long maxAge = 0;
        long staleWhileRevalidate = 0;
        boolean hasCacheControl = false;
        boolean mustRevalidate = false;

        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = parseDateAsEpoch(headerValue);
        }

        headerValue = headers.get("Cache-Control");
        if (headerValue != null) {
            hasCacheControl = true;
            String[] tokens = headerValue.split(",");
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i].trim();
                if (token.equals("no-cache") || token.equals("no-store")) {
                    return null;
                } else if (token.startsWith("max-age=")) {
                    try {
                        maxAge = Long.parseLong(token.substring(8));
                    } catch (Exception e) {
                    }
                } else if (token.startsWith("stale-while-revalidate=")) {
                    try {
                        staleWhileRevalidate = Long.parseLong(token.substring(23));
                    } catch (Exception e) {
                    }
                } else if (token.equals("must-revalidate") || token.equals("proxy-revalidate")) {
                    mustRevalidate = true;
                }
            }
        }

        headerValue = headers.get("Expires");
        if (headerValue != null) {
            serverExpires = parseDateAsEpoch(headerValue);
        }

        headerValue = headers.get("Last-Modified");
        if (headerValue != null) {
            lastModified = parseDateAsEpoch(headerValue);
        }

        serverEtag = headers.get("ETag");

        // Cache-Control takes precedence over an Expires header, even if both exist and Expires
        // is more restrictive.
        if (hasCacheControl) {
            softExpire = now + maxAge * 1000;
            finalExpire = mustRevalidate
                    ? softExpire
                    : softExpire + staleWhileRevalidate * 1000;
        } else if (serverDate > 0 && serverExpires >= serverDate) {
            // Default semantic for Expire header in HTTP specification is softExpire.
            softExpire = now + (serverExpires - serverDate);
            finalExpire = softExpire;
        }

        Cache.Entry entry = new Cache.Entry();

        if(0==finalExpire&&0==softExpire||softExpire == now){//若服务端没有返回相应的标识位，则使用自己设置的即时缓存与最大缓存时间
            softExpire = now + softExpireGap; //
            finalExpire = now + finalExpireGap; //
        }
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = finalExpire;
        entry.serverDate = serverDate;
        entry.lastModified = lastModified;
        entry.responseHeaders = headers;

        return entry;
    }
    /**
     * Parse date in RFC1123 format, and return its value as epoch
     */
    public static long parseDateAsEpoch(String dateStr) {
        try {
            // Parse date in RFC1123 format if this header contains one
            return DateUtils.parseDate(dateStr).getTime();
        } catch (DateParseException e) {
            // Date in invalid format, fallback to 0
            return 0;
        }
    }

    /**
     * Retrieve a charset from headers
     *
     * @param headers An {@link Map} of headers
     * @param defaultCharset Charset to return if none can be found
     * @return Returns the charset specified in the Content-Type of this header,
     * or the defaultCharset if none can be found.
     */
    public static String parseCharset(Map<String, String> headers, String defaultCharset) {
        String contentType = headers.get(HTTP.CONTENT_TYPE);
        if (contentType != null) {
            String[] params = contentType.split(";");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }

        return defaultCharset;
    }

    /**
     * Returns the charset specified in the Content-Type of this header,
     * or the HTTP default (ISO-8859-1) if none can be found. "ISO-8859-1"
     */
    public static String parseCharset(Map<String, String> headers) {
        return parseCharset(headers, HTTP.DEFAULT_CONTENT_CHARSET);
    }
}