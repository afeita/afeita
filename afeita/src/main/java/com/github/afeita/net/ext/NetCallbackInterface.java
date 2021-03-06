package com.github.afeita.net.ext;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/8
 * <br /> email: chenshufei2@sina.com
 */
public interface NetCallbackInterface<T> {

    void onStart();
    /**
     * 网络请求访问的速度，loaded代表每秒访问下载的是多少byte
     * @param loaded
     */
    void onLoad(long loaded);
    void onCancle();
    void onResult(T response);
    void onError(Exception error);

    /**
     * 请求结束，isCancel 是否正常执行的（有报文响应或异常错误返回 false），手工cancel isCancel是true
     * @param isCancel
     */
    void onFinish(boolean isCancel);

}
