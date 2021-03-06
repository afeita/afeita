package com.github.afeita.net.ext;

import com.github.afeita.net.ext.request.CacheRequest;
import com.github.afeita.net.ext.request.FileRequest;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/9
 * <br /> email: chenshufei2@sina.com
 */
public interface RequestExcuteProcessDelivery<T> {
    void postOnStart(CacheRequest<T> cacheRequest);

    void postOnUpload(FileRequest<T> fileRequest, int fileNum, String currentUploadFilename, long sumSize, long sumDonedSize, long sumSpendedTime);

    void postOnLoad(CacheRequest<T> cacheRequest, long loaded);

    void postOnCancle(CacheRequest<T> cacheRequest);

    void postOnResult(CacheRequest<T> cacheRequest, T response);

    void postOnError(CacheRequest<T> cacheRequest, Exception error);

    void postOnFinish(CacheRequest<T> cacheRequest, boolean isCancel);
}
