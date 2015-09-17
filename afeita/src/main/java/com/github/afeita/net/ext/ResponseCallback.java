package com.github.afeita.net.ext;

import com.github.afeita.net.Response;
import com.github.afeita.net.VolleyError;

/**
 * 对正常响应与异常响应的简单封装
 * <br /> author: chenshufei
 * <br /> date: 15/8/31
 * <br /> email: chenshufei2@sina.com
 */
public abstract class ResponseCallback<T> implements Response.ErrorListener,Response.Listener<T>{

    @Override
    public void onErrorResponse(VolleyError error) {
        onError(error);
    }

    @Override
    public void onResponse(T response) {
        onSuccess(response);
    }

    protected abstract void onSuccess(T response);

    protected abstract void onError(VolleyError error);
}