package com.github.afeita.net.ext.request;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.github.afeita.net.AuthFailureError;
import com.github.afeita.net.DefaultRetryPolicy;
import com.github.afeita.net.ExecutorDelivery;
import com.github.afeita.net.Response;
import com.github.afeita.net.ext.FileMimeTypeUitl;
import com.github.afeita.net.ext.ResponseCallback;
import com.github.afeita.net.ext.exception.FileUploadException;
import com.github.afeita.net.ext.exception.NotSupportDataFormatException;
import com.github.afeita.net.ext.exception.NotSupportOperationException;
import com.github.afeita.net.ext.exception.RequestBodyContentSettingException;
import com.github.afeita.net.ext.multipart.BasePart;
import com.github.afeita.net.ext.multipart.ByteArrayPart;
import com.github.afeita.net.ext.multipart.FileMultipartEntity;
import com.github.afeita.net.ext.multipart.FilePart;
import com.github.afeita.net.ext.multipart.StringPart;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 简单的文件上传请求，此请求中，若有其它非文件的入参，可以用setParams设置好参数。
 * 注意不适合 大文件的上传，大文件可能会oom.
 * <br /> author: chenshufei
 * <br /> date: 15/9/2
 * <br /> email: chenshufei2@sina.com
 */
public abstract class FileRequest<T> extends CacheRequest<T> {

    private static final int SOCEET_TIMEOUT_MS = 30000; //30秒 上传连接未建议超时
    private int mMaxFixedSize = 5 * 1024 * 1024;  //5M,若超过5M，将采用块的流式传输。默认小于5M是固定长度的流式传输。非内存缓存输出

    private FileMultipartEntity mFileMultipartEntity; //multipart,处理字符串与文件流参数
    //上传进度监听
    private Response.OnUploadProgressListener mOnUploadProgressListener;

    private OnFileRequestLifecycleCallback mOnFileRequestLifecycleCallback;

    //字符类型的请求
    private Map<String,String> mStringParamters;

    public FileRequest(int method, String url, ResponseCallback<T> responseCallback,Response.OnUploadProgressListener onUploadProgressListener) {
        super(method, url, responseCallback);
        setShouldCache(false);
        mFileMultipartEntity = new FileMultipartEntity();
        setOnUploadProgressListener(onUploadProgressListener);
        //更新超时时间 ，默认是2.5秒，上传有可能这时间显示不够，设置大点
        setRetryPolicy(new DefaultRetryPolicy(SOCEET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void setParams(Map<String, String> params) {
        throw new NotSupportOperationException("not support setParams, use addPart to replace!");
    }

    @Override
    public void setBodyContent(String bodyContent) {
        throw new NotSupportOperationException("not support setBodyContent, use addPart to replace!");
    }

    public FileRequest(int method, String url, ResponseCallback<T> responseCallback) {
        this(method, url, responseCallback, null);
    }

    @SuppressWarnings("unchecked")
    public FileMultipartEntity getFileMultipartEntity(){
        if (null != mOnUploadProgressListener){//在最后一步，再决定是否给FileMultipartEntity设置上传进度监听
            mFileMultipartEntity.setOnUploadProgressListener(new FileMultipartEntity.OnUploadProgressListener() {
                long time = SystemClock.uptimeMillis();
                ExecutorDelivery delivery = null;
                long sumSpeededTime = 0;
                @Override
                public void onUploadProgress(int fileNum,String currentUploadFilename,long fileSumSize,long transferredSize) {

                    if (null == delivery){ //确保是会在主线中，回调回来,即用主线程的handle.post
                        delivery = new ExecutorDelivery(new Handler(Looper.getMainLooper()));
                    }
                    long thisTime = SystemClock.uptimeMillis();
                    long gapTime = thisTime - time;
                    sumSpeededTime += gapTime;
                    if (gapTime >= getListenProgreeTime() || fileSumSize == transferredSize) {
                        time = thisTime;
                        delivery.postUploading(FileRequest.this,fileNum,currentUploadFilename, fileSumSize, transferredSize,sumSpeededTime);
                    }
                }
            });
        }
        return mFileMultipartEntity;
    }

    public int getMaxFixedSize() {
        return mMaxFixedSize;
    }

    /**
     * 默认5M，超过maxFixedSize大小了，则以分块流上传。避免大文件上传OOM
     * @param maxFixedSize
     */
    public void setMaxFixedSize(int maxFixedSize) {
        this.mMaxFixedSize = maxFixedSize;
    }

    /**
     * can't reach, you're still too young too simple ,sometime naive
     * @return
     * @throws AuthFailureError
     */
    @Deprecated
    @Override
    public byte[] getBody() throws AuthFailureError {
        //检查请求体的格式是否是multipart/form-data,若不是这种类型的，则请求结束。
        if (RctType.RCT_MULTIPART != getRequestBodyContentType()) {
            throw new RequestBodyContentSettingException(" bodyContentType is only allow RctType.RCT_MULTIPART! ");
        }
        return super.getBody();
    }

    @Override
    public String getBodyContentType() {
        return mFileMultipartEntity.getContentType();
    }

    public abstract class OnFileRequestLifecycleCallback extends OnRequestLifecycleCallback{
        /**
         * Post 上传的进度
         * @param fileNum 共有多少个文件要上专
         * @param currentUploadingFilename 当前正在上传文件名
         * @param sumSize 所有文件总大小
         * @param sumDonedSize 已经上传的文件总大小
         * @param sumSpendedTime
         */
        public abstract void onUpload(int fileNum,String currentUploadingFilename,long sumSize,long sumDonedSize,long sumSpendedTime);
    }

    /**
     * 设置OnFileRequestLifecycleCallback的监听，比OnRequestLifecycleCallback <br />
     * 多了onUpload(long sumSize,long sumDonedSize,long sumSpendedTime) 上传进度监听
     * @param onFileRequestLifecycleCallback
     */
    public void setOnFileRequestLifecycleCallback(OnFileRequestLifecycleCallback onFileRequestLifecycleCallback){
        if (null != mOnFileRequestLifecycleCallback){
            throw new IllegalArgumentException("OnFileRequestLifecycleCallback already has, to invork this call removeOnFileRequestLifecycleCallback first");
        }
        this.mOnFileRequestLifecycleCallback = onFileRequestLifecycleCallback;
        //设置 其它的onStart ... onLoad ... onResult 等监听回调
        setOnRequestLifecycleCallback(mOnFileRequestLifecycleCallback);
    }

    public void removeOnFileRequestLifecycleCallback(){
        mOnFileRequestLifecycleCallback = null;
        removeOnRequestLifecycleCallback();
    }

    public void setOnUploadProgressListener(Response.OnUploadProgressListener onUploadProgressListener){
        this.mOnUploadProgressListener = onUploadProgressListener;
    }

    @SuppressWarnings("unchecked")
    public void deliverUploading(int fileNum,String currentUploadFilename,long sumSize,long sumDonedSize,long sumSpendedTime){
        mExecutorDelivery.postOnUpload(this,fileNum,currentUploadFilename,sumSize,sumDonedSize,sumSpendedTime);
        if (null != mOnUploadProgressListener){
            mOnUploadProgressListener.onUploadProgress(fileNum, currentUploadFilename, sumSize, sumDonedSize, sumSpendedTime);
        }
    }

    public void deliverOnUpload(int fileNum,String currentUploadFilename,long sumSize,long sumDonedSize,long sumSpendedTime){
        if (null != mOnFileRequestLifecycleCallback){
            mOnFileRequestLifecycleCallback.onUpload(fileNum,currentUploadFilename,sumSize, sumDonedSize, sumSpendedTime);
        }
    }

    /**
     * 添加 字符串 形式的请求入参，如 key = username , value = zhangsa ; key = password ,value = 123456
     * @param key
     * @param value
     */
    public void addPart(String key,String value){
        StringPart stringPart = new StringPart(key,value, BasePart.DEFAULT_PARAMVALUE_CHARSET);
        mFileMultipartEntity.addPart(stringPart);
        if (null == mStringParamters){
            mStringParamters = new HashMap<String,String>();
        }
        mStringParamters.put(key, value);
    }

    public Map<String, String> getStringParamters() {
        return mStringParamters;
    }

    public void addStringParts(Map<String,String> stringParts){
        for (Map.Entry<String,String> entry : stringParts.entrySet()) {
            if (null != entry.getKey() && entry.getKey().length() > 0){
                addPart(entry.getKey(),entry.getValue());
            }
        }
    }
    /**
     * 添加 文件类型的请求入参
     * @param key 如/对应 表单<form>  <input type="file" name="key" />  </form> 中的name属性值 ,
     *            其实基本上用不上，建议写成 "file1","file2"等类型这样的key即可
     * @param file
     */
    public void addPart(String key,File file)  {
        if (!file.exists()){
            throw new FileUploadException(" file "+file.getAbsolutePath() +" can't not find");
        }
        FilePart filePart = new FilePart(key,file,null,null);
        mFileMultipartEntity.addPart(filePart);
    }


    public void addFileParts(Map<String,File> fileParts){
        for (Map.Entry<String,File> entry:fileParts.entrySet()) {
            if (null != entry.getKey() && entry.getKey().length() > 0){
                addPart(entry.getKey(),entry.getValue());
            }
        }
    }

    /**
     * 添加文件类型的请求入参，只允许上传小的图片文件，只支持 jpg/png/gif/bmp 四种图片类型的上传
     * @param key
     * @param data
     * @throws NotSupportDataFormatException 非jpg/png/gif/bmp  的数据类型将抛此异常
     */
    public void addPart(String key,byte[] data) throws NotSupportDataFormatException {
        if (data.length > mMaxFixedSize*2){
            throw new FileUploadException(" data "+ data.length +"is too larger ,try use addPart(String key,File file) ");
        }
        String ext = FileMimeTypeUitl.guessImgExtByDataArray(data);
        String mimeType = FileMimeTypeUitl.getImgRealMimeType(ext);
        long time = SystemClock.uptimeMillis();
        String filename = time+"."+ext;
        ByteArrayPart byteArrayPart = new ByteArrayPart(key,data,filename,mimeType);
        mFileMultipartEntity.addPart(byteArrayPart);
    }

}
