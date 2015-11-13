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

public interface ResponseDelivery {
    /**
     * Parses a response from the network or cache and delivers it.
     */
    void postResponse(Request<?> request, Response<?> response);

    /**
     * Parses a response from the network or cache and delivers it. The provided
     * Runnable will be executed after delivery.
     */
    void postResponse(Request<?> request, Response<?> response, Runnable runnable);

    /**
     * Posts an error for the given request.
     */
    void postError(Request<?> request, VolleyError error);

    /**
     * Post 一个请求响应的进度
     * @param request
     * @param sumDonedSize
     * @param currentDone
     * @param currentSpendedTime
     * @param sumSpendedTime
     */
    void postLoading(Request<?> request, long sumDonedSize, long currentDone, long currentSpendedTime, long sumSpendedTime);

    /**
     * Post 上传的进度
     * @param request
     * @param fileNum 共有多少个文件要上专
     * @param currentUploadFilename 当前正在上传文件名
     * @param sumSize 所有文件总大小
     * @param sumDonedSize 已经上传的文件总大小
     * @param sumSpendedTime
     */
    void postUploading(Request<?> request, int fileNum, String currentUploadFilename, long sumSize, long sumDonedSize, long sumSpendedTime);
}
