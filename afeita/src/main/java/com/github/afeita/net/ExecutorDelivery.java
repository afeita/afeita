/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.afeita.net;

import android.os.Handler;

import com.github.afeita.net.ext.RequestExcuteProcessDelivery;
import com.github.afeita.net.ext.request.CacheRequest;
import com.github.afeita.net.ext.request.FileRequest;

import java.util.concurrent.Executor;

/**
 * Delivers responses and errors.
 */
public class ExecutorDelivery<T> implements ResponseDelivery , RequestExcuteProcessDelivery<T> {
    /** Used for posting responses, typically to the main thread. */
    private final Executor mResponsePoster;

    /**
     * Creates a new response delivery interface.
     * @param handler {@link Handler} to post responses on
     */
    public ExecutorDelivery(final Handler handler) {
        // Make an Executor that just wraps the handler.
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    /**
     * Creates a new response delivery interface, mockable version
     * for testing.
     * @param executor For running delivery tasks
     */
    public ExecutorDelivery(Executor executor) {
        mResponsePoster = executor;
    }

    @Override
    public void postResponse(Request<?> request, Response<?> response) {
        postResponse(request, response, null);
    }

    @Override
    public void postResponse(Request<?> request, Response<?> response, Runnable runnable) {
        request.markDelivered();
        request.addMarker("post-response");
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, runnable));
    }

    @Override
    public void postError(Request<?> request, VolleyError error) {
        request.addMarker("post-error");
        Response<?> response = Response.error(error);
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, null));
    }

    @Override
    public void postLoading(final Request<?> request, final long sumDonedSize, final long currentDone, final long currentSpendedTime, final long sumSpendedTime) {
        request.addMarker("post-loading-calculate");
        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                if (request instanceof CacheRequest){
                    CacheRequest cacheRequest = (CacheRequest)request;
                    cacheRequest.deliverLoading(sumDonedSize,currentDone,currentSpendedTime,sumSpendedTime);
                }
            }
        });
    }

    /**
     * 上传进度，分派处理
     * @param request
     * @param sumSize
     * @param sumDonedSize
     * @param sumSpendedTime
     */
    @Override
    public void postUploading(final Request<?> request,final int fileNum,final String currentUploadFilename, final long sumSize, final long sumDonedSize, final long sumSpendedTime) {
        request.addMarker("post-uploading-calculate");
        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                if (request instanceof FileRequest){
                    FileRequest fileRequest = (FileRequest)request;
                    fileRequest.deliverUploading(fileNum,currentUploadFilename,sumSize,sumDonedSize,sumSpendedTime);
                }
            }
        });
    }

    @Override
    public void postOnStart(final CacheRequest<T> cacheRequest) {
        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                cacheRequest.deliverOnStart();
            }
        });
    }

    @Override
    public void postOnUpload(final FileRequest<T> fileRequest,final int fileNum,final String currentUploadFilename, final long sumSize, final long sumDonedSize, final long sumSpendedTime) {
        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                fileRequest.deliverOnUpload(fileNum,currentUploadFilename,sumSize,sumDonedSize,sumSpendedTime);
            }
        });
    }

    @Override
    public void postOnLoad(final CacheRequest<T> cacheRequest, final long loaded) {
        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                cacheRequest.deliverOnLoad(loaded);
            }
        });
    }

    @Override
    public void postOnCancle(final CacheRequest<T> cacheRequest) {
        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                cacheRequest.deliverOnCancel();
            }
        });
    }

    @Override
    public void postOnResult(final CacheRequest<T> cacheRequest, final T response) {
        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                cacheRequest.deliverOnResult(response);
            }
        });
    }

    @Override
    public void postOnError(final CacheRequest<T> cacheRequest, final Exception error) {
        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                cacheRequest.deliverOnError(error);
            }
        });
    }

    @Override
    public void postOnFinish(final CacheRequest<T> cacheRequest, final boolean isCancel) {
        mResponsePoster.execute(new Runnable() {
            @Override
            public void run() {
                cacheRequest.deliverOnFinish(isCancel);
            }
        });
    }

    /**
     * A Runnable used for delivering network responses to a listener on the
     * main thread.
     */
    @SuppressWarnings("rawtypes")
    private class ResponseDeliveryRunnable implements Runnable {
        private final Request mRequest;
        private final Response mResponse;
        private final Runnable mRunnable;

        public ResponseDeliveryRunnable(Request request, Response response, Runnable runnable) {
            mRequest = request;
            mResponse = response;
            mRunnable = runnable;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            // If this request has canceled, finish it and don't deliver.
            if (mRequest.isCanceled()) {
                mRequest.finish("canceled-at-delivery");
                return;
            }

            // Deliver a normal response or error, depending.
            if (mResponse.isSuccess()) {
                mRequest.deliverResponse(mResponse.result);
            } else {
                mRequest.deliverError(mResponse.error);
            }

            // If this is an intermediate response, add a marker, otherwise we're done
            // and the request can be finished.
            if (mResponse.intermediate) {
                mRequest.addMarker("intermediate-response");
            } else {
                mRequest.finish("done");
            }

            // If we have been provided a post-delivery runnable, run it.
            if (mRunnable != null) {
                mRunnable.run();
            }
       }
    }
}
