package com.github.afeita.net.ext;

import com.github.afeita.net.ext.request.CacheRequest;
import com.github.afeita.net.ext.request.FileRequest;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/9
 * <br /> email: chenshufei2@sina.com
 */
public interface RequestExcuteProcessDelivery<T> {
    public void postOnStart(CacheRequest<T> cacheRequest);

    public void postOnUpload(FileRequest<T> fileRequest,int fileNum,String currentUploadFilename,long sumSize,long sumDonedSize,long sumSpendedTime);

    public void postOnLoad(CacheRequest<T> cacheRequest,long loaded);

    public void postOnCancle(CacheRequest<T> cacheRequest);

    public void postOnResult(CacheRequest<T> cacheRequest,T response);

    public void postOnError(CacheRequest<T> cacheRequest,Exception error);

    public void postOnFinish(CacheRequest<T> cacheRequest,boolean isCancel);
}
