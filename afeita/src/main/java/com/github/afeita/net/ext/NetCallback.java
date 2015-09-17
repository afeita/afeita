package com.github.afeita.net.ext;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/9
 * <br /> email: chenshufei2@sina.com
 */
public abstract class NetCallback<T> implements NetCallbackInterface<T> {
    /**
     * 网络请求开始
     */
    @Override
    public void onStart() {

    }

    /**
     * 文件上传类的请求的回调，代表正在上传的，上传的进度
     * @param fileNum 共有文件的总个数
     * @param currentUploadingFilename 当前正在上传的文件名
     * @param sumSize 要上传的总大小 单位byte字节
     * @param sumDonedSize 已经上传的大小 单位byte字节
     * @param sumSpendedTime 上传已经花的时间 单位毫秒mm
     */
    public void onUpload(int fileNum,String currentUploadingFilename,long sumSize,long sumDonedSize,long sumSpendedTime){

    }
    /**
     * 网络请求访问进行中
     * loaded代表每秒访问下载的是多少byte
     * @param loaded
     */
    @Override
    public void onLoad(long loaded) {

    }

    /**
     * 网络请求访问被取消
     */
    @Override
    public void onCancle() {

    }

    /**
     * 网络请求返回的结果
     * @param response
     */
    @Override
    public abstract void onResult(T response);

    /**
     * 网络请求异常失败
     * @param error
     */
    @Override
    public void onError(Exception error) {

    }

    /**
     * 网络请求访问结束，结束的回调包括成功的响应与异常失败的响应之后的回调及被cancel中断
     * isDong，true代表正常的结束（含成功的响应与异常失败的响应）
     *        false代表被cancel的中断
     * @param isCancel
     */
    @Override
    public void onFinish(boolean isCancel) {

    }
}
