package com.github.afeita.net.ext.request;

import android.text.TextUtils;

import com.github.afeita.net.ext.exception.ResponseConentParseException;
import com.github.afeita.tools.fastjson.JSON;
import com.github.afeita.net.NetworkResponse;
import com.github.afeita.net.Response;
import com.github.afeita.net.ext.HttpHeaderParserUtil;
import com.github.afeita.net.ext.ResponseCallback;

import java.io.UnsupportedEncodingException;

/**
 * 响应是JSON串的，文件上传请求
 * <br /> author: chenshufei
 * <br /> date: 15/9/7
 * <br /> email: chenshufei2@sina.com
 */
public class JsonFileRequest<T> extends FileRequest<T> {

    private Class<T> mClazz;

    public JsonFileRequest(int method, String url, ResponseCallback<T> responseCallback, Response.OnUploadProgressListener onUploadProgressListener,Class clazz) {
        super(method, url, responseCallback, onUploadProgressListener);
        this.mClazz = clazz;
    }

    public JsonFileRequest(int method, String url, ResponseCallback<T> responseCallback) {
        this(method, url, responseCallback,null,null);
    }

    public JsonFileRequest(String url, ResponseCallback<T> responseCallback,Class clazz) {
        this(Method.POST, url, responseCallback,null,clazz);
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
            return Response.success(null,null);
        }else{
            T retVal = null;
            try {
                retVal = JSON.parseObject(retString, mClazz);
            } catch (Exception e) {
                throw new ResponseConentParseException("fastjson parser failure , for :"+e.getMessage());
            }
            //Cache.Entry 直接为空,不进行缓存请求
            return Response.success(retVal,null);
        }
    }
}
