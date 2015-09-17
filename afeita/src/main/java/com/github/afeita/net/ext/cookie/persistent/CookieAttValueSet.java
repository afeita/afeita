
package com.github.afeita.net.ext.cookie.persistent;

import com.github.afeita.net.ext.cookie.BasicClientCookie2;
import com.github.afeita.net.ext.cookie.Cookie;

import java.util.Date;
import java.util.StringTokenizer;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class CookieAttValueSet {

    /**
     * CookieBean转Cookie
     * @param cookieBean
     * @return
     */
    public static Cookie cookieBean2Cookie(CookieBean cookieBean){
        BasicClientCookie2 cookie = new BasicClientCookie2(cookieBean.getName(),cookieBean.getValue());
        cookie.setComment(cookieBean.getComment());
        cookie.setCommentURL(cookieBean.getCommentUrl());
        cookie.setExpiryDate(new Date(Long.valueOf(cookieBean.getExpiryDatetime())));
        cookie.setDomain(cookieBean.getDomain());
        cookie.setPath(cookieBean.getPath());
        String strPorts = cookieBean.getPorts();
        final StringTokenizer st = new StringTokenizer(strPorts, ",");
        int[] ports = null;
        int i = 0;
        while(st.hasMoreTokens()) {
            if (null == ports){
                ports = new int[st.countTokens()];
            }
            ports[i] = Integer.parseInt(st.nextToken().trim());
            ++i;
        }
        if (null != ports){
            cookie.setPorts(ports);
        }
        cookie.setSecure(Boolean.valueOf(cookieBean.getIsSecure()));
        cookie.setVersion(Integer.valueOf(cookieBean.getVersion()));
        cookie.setDiscard(Boolean.valueOf(cookieBean.getIsDiscard()));
        return cookie;
    }

    /**
     * Cookie转CookieBean
     * @param cookie
     * @return
     */
    public static CookieBean cookie2CookieBean(Cookie cookie){
        CookieBean cookieBean = new CookieBean();
        cookieBean.setName(cookie.getName());
        cookieBean.setValue(cookie.getValue());
        cookieBean.setComment(cookie.getComment());
        cookieBean.setCommentUrl(cookie.getCommentURL());
        //cookieBean.setExpiryDatetime(DateTimeUtil.getDateTimeString(cookie.getExpiryDate()));
        Date expiryDate = cookie.getExpiryDate();
        if (null != expiryDate){
            cookieBean.setExpiryDatetime(expiryDate.getTime()+"");
        }
        cookieBean.setDomain(cookie.getDomain());
        cookieBean.setPath(cookie.getPath());
        int[] ports = cookie.getPorts();
        StringBuffer sb = new StringBuffer();
        if (null != ports && ports.length > 0){
            for (int port : ports) {
                sb.append(port).append(",");
            }
        }
        if (sb.length() > 0){
            sb.delete(sb.length()-2,sb.length()-1);
            cookieBean.setPorts(sb.toString());
        }
        cookieBean.setIsSecure(cookie.isSecure() + "");
        cookieBean.setVersion(cookie.getVersion() + "");
        cookieBean.setIsDiscard(cookie.isPersistent() + "");
        return cookieBean;
    }
}
