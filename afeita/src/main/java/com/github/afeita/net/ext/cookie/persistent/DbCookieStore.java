package com.github.afeita.net.ext.cookie.persistent;

import com.github.afeita.db.AfeitaDb;
import com.github.afeita.net.ext.cookie.Cookie;
import com.github.afeita.net.ext.cookie.CookieStore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 将Cookie持久会到数据库
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class DbCookieStore implements CookieStore {

    private AfeitaDb afeitaDb;

    public DbCookieStore(AfeitaDb afeitaDb){
        this.afeitaDb = afeitaDb;
    }

    @Override
    public synchronized void addCookie(Cookie cookie) {
        CookieBean cookieBean = CookieAttValueSet.cookie2CookieBean(cookie);
        afeitaDb.deleteByWhereStr(CookieBean.class," name='"+cookie.getName()+
                "'  and domain='"+cookie.getDomain()+
                "' and path='"+cookie.getPath()+"' and ports='"+cookie.getPorts()+
                "' and isSecure='"+cookie.isSecure()+"'");
        afeitaDb.save(cookieBean);
    }

    @Override
    public synchronized List<Cookie> getCookies() {
        List<CookieBean> cookieBeans = afeitaDb.findAllByWhereStr(CookieBean.class, "" );
        List<Cookie> cookies = null;
        if (null != cookieBeans && cookieBeans.size()>0){
            cookies = new ArrayList<Cookie>();
            for (CookieBean cookieBean:cookieBeans ) {
                cookies.add(CookieAttValueSet.cookieBean2Cookie(cookieBean));
            }
        }
        return cookies;
    }

    @Override
    public synchronized boolean clearExpired(Date date) {
        List<CookieBean> cookieBeans = afeitaDb.findAllByWhereStr(CookieBean.class, " expiryDatetime <= " + date.getTime());
        boolean retVal = false;
        if (null != cookieBeans && cookieBeans.size()>0){
            retVal = true;
        }
        afeitaDb.deleteByWhereStr(CookieBean.class, " expiryDatetime <= " + date.getTime());
        return retVal;
    }

    @Override
    public synchronized void clear() {
        afeitaDb.deleteByWhereStr(CookieBean.class,"");
    }
}
