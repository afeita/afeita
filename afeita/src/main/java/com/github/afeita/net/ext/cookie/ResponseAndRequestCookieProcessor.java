
package com.github.afeita.net.ext.cookie;

import com.github.afeita.log.L;
import com.github.afeita.net.VolleyLog;
import com.github.afeita.net.ext.cookie.iml.BasicCommentHandler;
import com.github.afeita.net.ext.cookie.iml.BasicCommentUrlHandler;
import com.github.afeita.net.ext.cookie.iml.BasicDiscardHandler;
import com.github.afeita.net.ext.cookie.iml.BasicDomainHandler;
import com.github.afeita.net.ext.cookie.iml.BasicExpiresHandler;
import com.github.afeita.net.ext.cookie.iml.BasicMaxAgeHandler;
import com.github.afeita.net.ext.cookie.iml.BasicPathHandler;
import com.github.afeita.net.ext.cookie.iml.BasicPortHandler;
import com.github.afeita.net.ext.cookie.iml.BasicSecureHandler;
import com.github.afeita.net.ext.cookie.iml.BasicVersionHandler;
import com.github.afeita.net.ext.cookie.iml.CookieSpec;
import com.github.afeita.net.ext.cookie.iml.CookieStoreHandler;
import com.github.afeita.net.ext.exception.MalformedCookieException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/15
 * <br /> email: chenshufei2@sina.com
 */
public class ResponseAndRequestCookieProcessor {

    private CookieSpec cookieSpec ;

    public void processRequestCookie(HttpURLConnection connection,HashMap<String, String> cookieHeaders){
        connection.setInstanceFollowRedirects(false);
        //-----------------------------------------------------------------------------------------------------
        // 1、获取所有的Cookie集合
        //-----------------------------------------------------------------------------------------------------
        CookieStore cookieStore = CookieStoreHandler.getInstance();
        List<Cookie> cookies = cookieStore.getCookies();
        if (null == cookies || cookies.size() <= 0 ){
            return;
        }
        //-----------------------------------------------------------------------------------------------------
        // 2、获取CookieOrigin
        //-----------------------------------------------------------------------------------------------------
        CookieOrigin cookieOrigin = getCookieOrigin(connection);
        //-----------------------------------------------------------------------------------------------------
        // 3、过滤符合条件数据，获取符合的Cookie集合
        //-----------------------------------------------------------------------------------------------------
        CookieSpec cookieSpec = obtainCookieSpec();
        final List<Cookie> matchedCookies = new ArrayList<Cookie>();
        final Date now = new Date();
        boolean expired = false;
        for (final Cookie cookie : cookies) {
            if (cookie.isExpired(now)) { //过期了..,没有设置expire不过期
                VolleyLog.v("Cookie " + cookie + " expired");
                expired = true;
            } else {
                if (cookieSpec.match(cookie, cookieOrigin)) {
                    VolleyLog.v("Cookie " + cookie + " match " + cookieOrigin);
                    matchedCookies.add(cookie);
                }
            }
        }
        // 有过期的Cookie存在，则去掉所有过期的Cookie
        if (expired) {
            cookieStore.clearExpired(now);
        }
        //-----------------------------------------------------------------------------------------------------
        // 4、将符合条件的Cookie字符串，设置成请求头headers Map中
        //-----------------------------------------------------------------------------------------------------
        String cookie = cookieSpec.formatCookies(matchedCookies);
        if (null != cookie && cookie.length() >0 ){
            cookieHeaders.put("Cookie",cookie);
        }

    }

    public void processResponseCookie(HttpURLConnection connection)  {
        //-----------------------------------------------------------------------------------------------------
        // 1、获取响应的Set-Cookie值，得到响应的Cookie字符串集合
        //-----------------------------------------------------------------------------------------------------
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> setCookies = headerFields.get("Set-Cookie");
        List<String> responseCookies = new ArrayList<String>();
        if (null != setCookies && setCookies.size()>0){
            for(String setCookie : setCookies){
                if (setCookie.contains(",")){
                    String[] arrResponseCookies = setCookie.split(",");
                    for (String rCookie : arrResponseCookies){
                        responseCookies.add(rCookie);
                    }
                }else{
                    responseCookies.add(setCookie);
                }
            }
        }else{ //服务端没有响应数据过来，直接不处理了
            return;
        }
        //-----------------------------------------------------------------------------------------------------
        // 2、获取CookieOrigin
        //-----------------------------------------------------------------------------------------------------
        CookieOrigin cookieOrigin = getCookieOrigin(connection);
        //-----------------------------------------------------------------------------------------------------
        // 3、根据响应的Cookie字符串集合与CookieOrigin，获取Cookie集合
        //-----------------------------------------------------------------------------------------------------
        CookieSpec cookieSpec = obtainCookieSpec();
        try {
            List<Cookie> cookies = cookieSpec.parse(responseCookies, cookieOrigin);
            //-----------------------------------------------------------------------------------------------------
            // 4、将验证服务端返回的Cookie，并存起来
            //-----------------------------------------------------------------------------------------------------
            for (Cookie cookie : cookies){
                try {
                    cookieSpec.validate(cookie,cookieOrigin); //验证Cookie,不符合规则的将抛MalformedCookieException
                    CookieStoreHandler.getInstance().addCookie(cookie);
                }catch (MalformedCookieException ex){
                    L.w("Cookie rejected [" + formatCooke(cookie) + "] "
                            + ex.getMessage());
                }
            }
        }catch (MalformedCookieException ex){
            L.w("Invalid Cookie Header [" + responseCookies + "] "
                    + ex.getMessage());
        }
    }

    private CookieOrigin getCookieOrigin(HttpURLConnection connection) {
        URL url = connection.getURL();
        String path = url.getPath();
        if (null == path || path.trim().length() <= 0){
            path = "/";
        }
        int port = url.getPort() > 0 ? url.getPort() : 0;
        return new CookieOrigin(url.getHost(), port, path,false);
    }


    private CookieSpec obtainCookieSpec(){
        if (null == cookieSpec){
            CommonCookieAttributeHandler[] commonCookieAttributeHandlers ={
                    new BasicCommentHandler(),new BasicCommentUrlHandler(),
                    new BasicDiscardHandler(),new BasicDomainHandler(),
                    new BasicExpiresHandler(null),new BasicMaxAgeHandler(),
                    new BasicPathHandler(),new BasicPortHandler(),
                    new BasicSecureHandler(),new BasicVersionHandler()
            };
            cookieSpec = new CookieSpec(commonCookieAttributeHandlers);
        }
        return cookieSpec;
    }

    private static String formatCooke(final Cookie cookie) {
        final StringBuilder buf = new StringBuilder();
        buf.append(cookie.getName());
        buf.append("=\"");
        String v = cookie.getValue();
        if (v != null) {
            if (v.length() > 100) {
                v = v.substring(0, 100) + "...";
            }
            buf.append(v);
        }
        buf.append("\"");
        buf.append(", version:");
        buf.append(Integer.toString(cookie.getVersion()));
        buf.append(", domain:");
        buf.append(cookie.getDomain());
        buf.append(", path:");
        buf.append(cookie.getPath());
        buf.append(", expiry:");
        buf.append(cookie.getExpiryDate());
        return buf.toString();
    }

}
