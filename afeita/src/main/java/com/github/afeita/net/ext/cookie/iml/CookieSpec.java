package com.github.afeita.net.ext.cookie.iml;

import com.github.afeita.net.ext.cookie.BasicClientCookie;
import com.github.afeita.net.ext.cookie.BasicClientCookie2;
import com.github.afeita.net.ext.cookie.ClientCookie;
import com.github.afeita.net.ext.cookie.CommonCookieAttributeHandler;
import com.github.afeita.net.ext.cookie.Cookie;
import com.github.afeita.net.ext.cookie.CookieAttributeHandler;
import com.github.afeita.net.ext.cookie.CookieOrigin;
import com.github.afeita.net.ext.exception.MalformedCookieException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cookie 核心处理者..，将响应的Cookie字符串转成Cookie集合，将符合条件的Cookie集合转成请求头Map<String,Sting>
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class CookieSpec {

    private final CookieAttributeHandler[] attribHandlers;
    private final Map<String, CookieAttributeHandler> attribHandlerMap;

    public CookieSpec(CommonCookieAttributeHandler... handlers) {
        this.attribHandlers = handlers.clone();
        this.attribHandlerMap = new ConcurrentHashMap<String, CookieAttributeHandler>(handlers.length);
        for (CommonCookieAttributeHandler handler : handlers) {
            this.attribHandlerMap.put(handler.getAttributeName().toLowerCase(Locale.ROOT), handler);
        }
    }

    public final void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        for (final CookieAttributeHandler handler : this.attribHandlers) {
            handler.validate(cookie, origin);
        }
    }

    public final boolean match(final Cookie cookie, final CookieOrigin origin) {
        for (final CookieAttributeHandler handler : this.attribHandlers) {
            if (!handler.match(cookie, origin)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将Cookie集合转成 请求头的Cookie字符串
     * @param cookies
     * @return
     */
    public final String formatCookies(List<Cookie> cookies){
        if (null != cookies && cookies.size() >0){
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (Cookie cookie : cookies){
                if (i >0 ){
                    sb.append(";");
                }
                sb.append(cookie.getName()).append("=").append(cookie.getValue());
                i++;
            }
            return sb.toString();
        }else{
            return "";
        }
    }

    public final List<Cookie> parse(List<String> responseCookies, CookieOrigin origin) throws MalformedCookieException {
        if (null == responseCookies || responseCookies.size() <= 0) {
            return null;
        }
        List<Cookie> retCookies =  new ArrayList<Cookie>();
        List<String> cookieAttrs = Arrays.asList(ClientCookie.ATTRS);
        for (String responseCookie : responseCookies) {
            if (!responseCookie.contains(";")) {
                if (responseCookie.contains("=")) { //单条Cookie中没有;号,那么只有是一个key-value对
                    String[] keyValues = responseCookie.split("=");
                    BasicClientCookie cookie = new BasicClientCookie(keyValues[0].trim(), keyValues[1]);
                    cookie.setPath(getDefaultPath(origin));
                    cookie.setDomain(getDefaultDomain(origin));
                    cookie.setCreationDate(new Date());
                    retCookies.add(cookie);
                } else {
                    throw new MalformedCookieException("illegal cookie value :" + responseCookie);
                }
            } else {
                String[] keyValues = responseCookie.split(";");
                Map<String,String> attrs = new HashMap<String,String>();
                String name = null;
                String value = null;
                BasicClientCookie2 cookie = null;
                for (String keyValue : keyValues) {
                    if (null == cookie && null != name){
                        cookie = new BasicClientCookie2(name,value);
                    }
                    keyValue = keyValue.trim();
                    if (keyValue.contains("=")) {
                        String[] arrKeyValue = keyValue.split("=");
                        if (cookieAttrs.contains(arrKeyValue[0].toLowerCase().trim())){
                            attrs.put(arrKeyValue[0],arrKeyValue[1]);
                            if (null != cookie){
                                cookie.setAttribute(arrKeyValue[0],arrKeyValue[1]);
                            }
                        }else{
                            name = arrKeyValue[0];
                            value = arrKeyValue[1];
                        }
                    }else{
                        if (cookieAttrs.contains(keyValue.toLowerCase().trim())) {
                            attrs.put(keyValue,"true"); //带了个secure那么就...
                        }else{ //这是什么鬼，不是key-value键值对而且也不是secure。
                            //还有个HttpOnly，不知道是什么鬼，还不能随便抛个异常。。。
                            //throw new MalformedCookieException("illegal cookie value :" + responseCookie);
                        }
                    }
                }
                if (null == name){
                    throw new MalformedCookieException("illegal cookie value :" + responseCookie);
                }
                //   若有 'Max-Age' ，则干掉'Expires'
                if (attrs.containsKey(ClientCookie.MAX_AGE_ATTR)) {
                    attrs.remove(ClientCookie.EXPIRES_ATTR);
                }
                if (null == cookie){
                    cookie = new BasicClientCookie2(name,value);
                }
                cookie.setPath(getDefaultPath(origin)); //先设置默认的 domain/path等，若服务端有返回就自动覆盖
                cookie.setDomain(getDefaultDomain(origin));
                cookie.setCreationDate(new Date());
                for (Map.Entry<String,String> entry : attrs.entrySet()){
                    String key = entry.getKey();
                    if (null != key && key.length() >0){
                        key = key.toLowerCase().trim();
                    }
                    CookieAttributeHandler handler = this.attribHandlerMap.get(key);
                    if (handler != null) {
                        handler.parse(cookie, entry.getValue());
                    }
                }

                retCookies.add(cookie);
            }
        }
        return retCookies;
    }


    static String getDefaultPath(final CookieOrigin origin) {
        String defaultPath = origin.getPath();
        int lastSlashIndex = defaultPath.lastIndexOf('/');
        if (lastSlashIndex >= 0) {
            if (lastSlashIndex == 0) {
                lastSlashIndex = 1;
            }
            defaultPath = defaultPath.substring(0, lastSlashIndex);
        }
        return defaultPath;
    }

    static String getDefaultDomain(final CookieOrigin origin) {
        return origin.getHost();
    }

}
