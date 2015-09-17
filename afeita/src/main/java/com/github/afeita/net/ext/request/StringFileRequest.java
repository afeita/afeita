package com.github.afeita.net.ext.request;

import com.github.afeita.net.NetworkResponse;
import com.github.afeita.net.Response;
import com.github.afeita.net.ext.HttpHeaderParserUtil;
import com.github.afeita.net.ext.ResponseCallback;

import java.io.UnsupportedEncodingException;

/**
 * 响应是普通字符串的，文件上传请求
 * <br /> author: chenshufei
 * <br /> date: 15/9/7
 * <br /> email: chenshufei2@sina.com
 */
public class StringFileRequest extends FileRequest<String> {

    public StringFileRequest(int method, String url, ResponseCallback<String> responseCallback, Response.OnUploadProgressListener onUploadProgressListener) {
        super(method, url, responseCallback, onUploadProgressListener);
    }

    /**
     * method 为Method.POST/Method.PUT
     * @param method
     * @param url
     * @param responseCallback
     */
    public StringFileRequest(int method, String url, ResponseCallback<String> responseCallback) {
        super(method, url, responseCallback);
    }

    public StringFileRequest(String url, ResponseCallback<String> responseCallback) {
        this(Method.POST, url, responseCallback);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String retString;
        try {
            retString = new String(response.data, HttpHeaderParserUtil.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            retString = new String(response.data);
        }
        //不进行缓存响应
        return Response.success(retString,null);
    }
}
