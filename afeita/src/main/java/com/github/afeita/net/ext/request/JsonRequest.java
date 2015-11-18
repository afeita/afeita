package com.github.afeita.net.ext.request;

import android.text.TextUtils;

import com.github.afeita.net.NetworkResponse;
import com.github.afeita.net.Response;
import com.github.afeita.net.ext.HttpHeaderParserUtil;
import com.github.afeita.net.ext.ResponseCallback;
import com.github.afeita.net.ext.exception.ResponseConentParseException;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

/**
 * JsonRequest，将json格式式的响应，转成java对象
 * <br /> author: chenshufei
 * <br /> date: 15/9/1
 * <br /> email: chenshufei2@sina.com
 */
public class JsonRequest<T> extends CacheRequest<T> {

    private Class<T> mClazz;

    public JsonRequest(int method, String url, ResponseCallback<T> responseCallback, Class<T> clazz) {
        super(method, url, responseCallback);
        this.mClazz = clazz;
    }

    public JsonRequest(String url, ResponseCallback<T> responseCallback, Class<T> clazz) {
        super(url, responseCallback);
        this.mClazz = clazz;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String retString;
        try {
            retString = new String(response.data, HttpHeaderParserUtil.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            retString = new String(response.data);
        }
        if(TextUtils.isEmpty(retString)||null == mClazz){
            return Response.success(null,HttpHeaderParserUtil.parseCacheHeaders(response, mInstantExpire, mFinalExpire));
        }else{

            T retVal = null;
            try {
                Gson gson = new Gson();
                retVal = gson.fromJson(retString, mClazz);
            } catch (Exception e) {
                throw new ResponseConentParseException("fastjson parser failure , for :"+e.getMessage());
            }

            return Response.success(retVal,HttpHeaderParserUtil.parseCacheHeaders(response, mInstantExpire, mFinalExpire));
        }
    }
}
