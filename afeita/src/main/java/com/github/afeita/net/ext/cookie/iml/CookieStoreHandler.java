package com.github.afeita.net.ext.cookie.iml;

import android.content.Context;

import com.github.afeita.db.AfeitaDb;
import com.github.afeita.net.ext.cookie.BasicCookieStore;
import com.github.afeita.net.ext.cookie.Cookie;
import com.github.afeita.net.ext.cookie.CookieStore;
import com.github.afeita.net.ext.cookie.persistent.DbCookieStore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CookieStoreHandler 优先使用内存CookieStore，内存CookieStore丢失时，从硬盘CookieStore中获取
 * <br /> author: chenshufei
 * <br /> date: 15/9/14
 * <br /> email: chenshufei2@sina.com
 */
public class CookieStoreHandler implements CookieStore{
    private static Context ctx;
    //通过CookieStoreHandler获取一个CookieStore,这里是个单例，在网络快要请求时，取出CookieStore处理Cookie
    //在响应中，取出CookieStore存或更新Cookie
    private List<CookieStore> cookieStores;
    //-----若应用重启后，不在重启里自动登录，如何保持原来的会话...
    private static CookieStoreHandler cookieStoreHandler = new CookieStoreHandler();

    private boolean finishInit;

    private CookieStoreHandler(){
    }

    public static CookieStoreHandler getInstance(){
        return cookieStoreHandler;
    }

    public synchronized void addCookieStore(CookieStore cookieStore){
        if (null != cookieStores){
            cookieStores.add(cookieStore);
        }
    }

    /**
     * 初始化CookieStoreHandler，由于其单例性，仅需要调用一次，在AfeitaNet的init里初始化
     * @param context null则只内存CookieStore保存
     */
    public  void init(Context context){
        if (!finishInit){
            synchronized(CookieStoreHandler.class){
                if (!finishInit){ //双重检查，防止多次init
                    if (null != cookieStores){
                        cookieStores.clear();
                        cookieStores = null;
                    }
                    cookieStores = new ArrayList<CookieStore>();
                    CookieStore cookieStore = new BasicCookieStore();
                    cookieStores.add(cookieStore); //优先提供 RAM Cookie
                    if (null != context){
                        ctx = context.getApplicationContext();
                        AfeitaDb afeitaDb = AfeitaDb.create(ctx);
                        CookieStore dbCookieStore = new DbCookieStore(afeitaDb);
                        cookieStores.add(dbCookieStore);
                    }
                    finishInit = true;
                }
            }
        }

    }


    /**
     * 添加一条cookie，若之前没有调用init则，只添加的Cookie只保存在内存中，不持久化到硬盘中
     * @param cookie the {@link Cookie cookie} to be added
     */
    @Override
    public void addCookie(Cookie cookie) {
        if (null == cookieStores){
            init(null);
        }
        if (null != cookieStores && cookieStores.size() >0 ){
            for (CookieStore cookieStore: cookieStores) {
                cookieStore.addCookie(cookie);
            }
        }

    }

    /**
     * 获取所有的Cookie
     * @return
     */
    @Override
    public List<Cookie> getCookies() {
        CookieStore ramCookieStore = null;
        CookieStore dbCookieStore = null;
        if (null != cookieStores && cookieStores.size() >0 ){
            for (CookieStore cookieStore: cookieStores) {
                if (cookieStore instanceof BasicCookieStore){
                    ramCookieStore = cookieStore;
                }else if (cookieStore instanceof DbCookieStore){
                    dbCookieStore = cookieStore;
                }
            }
        }
        if (null != ramCookieStore){
            List<Cookie> ramCookies = ramCookieStore.getCookies();
            if (null != ramCookies && ramCookies.size() >0 ){
                return ramCookies;
            }
        }else if(null != dbCookieStore){
            List<Cookie> dbCookies = ramCookieStore.getCookies();
            if (null != dbCookies && dbCookies.size() >0 ){
                return dbCookies;
            }
        }
        return new ArrayList<Cookie>(0);
    }

    /**
     * 删除所有的Cookie中过期日期时间 大小date的 Cookie
     * @param date
     * @return
     */
    @Override
    public boolean clearExpired(Date date) {
        boolean retVal = false;
        if (null != cookieStores && cookieStores.size() >0 ){
            for (CookieStore cookieStore: cookieStores) {
                retVal = cookieStore.clearExpired(date);
            }
        }
        return retVal;
    }

    /**
     * 删除所有的Cookie
     */
    @Override
    public void clear() {
        if (null != cookieStores && cookieStores.size() >0 ){
            for (CookieStore cookieStore: cookieStores) {
                cookieStore.clear();
            }
        }
    }
}
