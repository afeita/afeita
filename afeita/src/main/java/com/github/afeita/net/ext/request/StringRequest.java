package com.github.afeita.net.ext.request;

import com.github.afeita.net.NetworkResponse;
import com.github.afeita.net.Response;
import com.github.afeita.net.ext.HttpHeaderParserUtil;
import com.github.afeita.net.ext.ResponseCallback;

import java.io.UnsupportedEncodingException;

/**
 * 可设置即时缓存与最终缓存时间 的 StringRequest
 * <br /> author: chenshufei
 * <br /> date: 15/9/1
 * <br /> email: chenshufei2@sina.com
 */
public class StringRequest extends CacheRequest<String> {

    public StringRequest(int method, String url, ResponseCallback<String> responseCallback) {
        super(method, url, responseCallback);
    }

    public StringRequest(String url, ResponseCallback<String> responseCallback) {
        super(url, responseCallback);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParserUtil.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParserUtil.parseCacheHeaders(response, mInstantExpire, mFinalExpire));
    }
}
