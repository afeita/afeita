package com.github.afeita.net.ext.request;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.github.afeita.net.AuthFailureError;
import com.github.afeita.net.ExecutorDelivery;
import com.github.afeita.net.Request;
import com.github.afeita.net.Response;
import com.github.afeita.net.VolleyError;
import com.github.afeita.net.ext.ResponseCallback;
import com.github.afeita.net.ext.exception.RequestBodyContentSettingException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

/**
 * CacheRequest 请求基类，可设置：
 * <br />1、请求结果统一的回调
 * <br />2、可设置每一个请求的缓存过期时间及二次刷新缓存时间
 * <br />3、对于POST/PUT，对于默认的content-type即application/x-www-form-urlencoded 的请求参数 可直接setParams
 *                      对content-type是application/json或xml的，可setBodyContent设置请求体内容
 * <br />4、请求日志打印设置
 * <br />5、请求的生命周期回调，包括onStart、onLoad、onCancle、onResult、onError、onFinish
 * <br /> author: chenshufei
 * <br /> date: 15/9/1
 * <br /> email: chenshufei2@sina.com
 */
public abstract class CacheRequest<T> extends Request<T> {

    /**
     * RctType 代表请求体中的格式，
     * RCT_DEFAULT 代表  application/x-www-form-urlencoded
     * RCT_MULTIPART   multipart/form-data
     * RCT_JSON 代表  application/json
     * RCT_XML 代表   application/xml
     */
    public enum  RctType{
        RCT_DEFAULT,RCT_MULTIPART,RCT_JSON,RCT_XML
    }

    private ResponseCallback<T> mResponseCallback;
    private OnRequestLifecycleCallback mOnRequestLifecycleCallback;
    private Response.OnUpdateProgressListener mOnUpdateProgressListener; //进度...
    protected ExecutorDelivery mExecutorDelivery;

    protected long mInstantExpire; //瞬时缓存时间
    protected long mFinalExpire;   //二次缓存且更新网络数据时间

    private Map<String,String>  mParams;
    //请求头
    private Map<String,String> mHeaders;
    //对应RCT_xxx，代表若有请求体时，请求体的格式 即上面常用的四种之一,默认为RCT_DEFAULT
    private RctType mBodyContentType = RctType.RCT_DEFAULT;
    private String mBodyContent;

    //--请求结果设置监听进度时间，当大于此监听的时间时，可收到进度. 默认10毫秒。可
    private long mListenProgreeTime = 10;

    //--是否打印请求的详细日志，包括请求的url、参数、响应等
    private boolean mLogDebugRequest ;


    public void setHeader(Map<String,String> headers){
        this.mHeaders = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (null == mHeaders){
            mHeaders = Collections.emptyMap();
        }
        return mHeaders;
    }

    /**
     * 返回是否打印请求的详细日志，包括请求的url、参数、响应等
     * @return true 打印， false 不打印
     */
    public boolean isLogDebugRequest() {
        return mLogDebugRequest;
    }

    /**
     * 设置是否打印请求的详细日志，包括请求的url、参数、响应等
     * @param logDebugRequest true 打印， false 不打印
     */
    public void setLogDebugRequest(boolean logDebugRequest) {
        this.mLogDebugRequest = logDebugRequest;
    }

    public long getListenProgreeTime() {
        return mListenProgreeTime;
    }

    /**
     * 设置监听，网络监听响应的进度时间。当大于listenProgreeTime时间时，可setOnUpdateProgressListener中获取想着进度信息
     * @param listenProgreeTime
     */
    public void setListenProgreeTime(long listenProgreeTime) {
        this.mListenProgreeTime = listenProgreeTime;
    }

    /**
     * 设置请求体的请求格式，
     * RCT_DEFAULT,RCT_MULTIPART,RCT_JSON,RCT_XML 之一
     * 分别代表application/x-www-form-urlencoded，multipart/form-data，application/json，application/xml
     * 当请求的是RCT_MULTIPART格式且使用FileRequest子类的，不用设置setBodyContentType
     * 当然若请求的是RCT_MULTIPART格式，自定义重写CacheRequest,处理getBody也是可以，但不建议...，文件大可能会OOM
     * @param bodyContentType
     */
    public void setBodyContentType(RctType bodyContentType){
        this.mBodyContentType = bodyContentType;
    }

    /**
     * 获取请求体body格式
     * @return
     */
    public RctType getRequestBodyContentType(){
        return  mBodyContentType;
    }
    /**
     * 若METHOD，是POT/PUT时的请求参数
     * 当contenet-type是application/x-www-form-urlencoded时，且参数是非query/path形式的
     * query与path形式的参数，请直接拼在url中
     * 或者当content-type是multipart-data时，此处理的参数将以请求体body的形式传给服务端，
     * 若参数是query/path形式的，请直接拼在url中
     * <b>注意，不能与setBodyContent同时使用，这两者是互斥的，同一请求content-type不能有两个。</b>
     *
     * @param params
     */
    public void setParams(Map<String,String> params){
        this.mParams = params;
    }

    /**
     * 若METHOD，是POT/PUT时的请求参数
     * 当contenet-type是application/json或application/xml,设置一个json或xml字符串给请求体
     * <b>注意，不能与setParams同时使用，这两者是互斥的，同一请求content-type不能有两个。</b>
     * @param bodyContent
     */
    public void setBodyContent(String bodyContent){
        this.mBodyContent = bodyContent;
    }

    /**
     * 获取请求体的内容
     * @return
     */
    public String getBodyContent(){
        return mBodyContent;
    }

    public void setOnUpdateProgressListener(Response.OnUpdateProgressListener onUpdateProgressListener) {
        this.mOnUpdateProgressListener = onUpdateProgressListener;
    }

    public Response.OnUpdateProgressListener getOnUpdateProgressListener() {
        return mOnUpdateProgressListener;
    }

    /**
     * 设置 即时缓存超时时间
     * @param expire
     */
    public void setInstantExpire(long expire) {
        this.mInstantExpire = expire;
    }

    /**
     *
     * @param expire
     */
    public void setFinalExpire(long expire) {
        this.mFinalExpire = expire;
    }

    /**
     * 构造缓存请求对象，responseCallback 统一的回调（请求失败与请求成功）
     * @param method
     * @param url
     * @param responseCallback
     */
    public CacheRequest(int method, String url, ResponseCallback<T> responseCallback) {
        super(method, url, responseCallback);
        this.mResponseCallback = responseCallback;
        this.mExecutorDelivery = new ExecutorDelivery(new Handler(Looper.getMainLooper()));
    }

    public CacheRequest(String url, ResponseCallback<T> responseCallback) {
        this(Method.GET, url, responseCallback);
    }


    /**
     * 缓存的key <br />
     * 重写缓存key，防止同一url，不同的方法比如get与delete都是同一url
     * @return
     */
    @Override
    public String getCacheKey() {
        return super.getCacheKey() + "Method"+getMethod();
    }

    /**
     * 整个请求完成时的回调
     */
    public abstract class OnRequestLifecycleCallback {
        public void onStart(){};
        /**
         * 网络请求访问的速度，loaded代表每秒访问下载的是多少byte
         * @param loaded
         */
        public void onLoad(long loaded){};
        public void onCancle(){};
        public abstract void onResult(T response);
        public void onError(Exception error){};

        /**
         * 请求结束，isCancel 是否正常执行的（有报文响应或异常错误返回 false），手工cancel isCancel是true
         * @param isCancel
         */
        public void onFinish(boolean isCancel){};
    }

    /**
     * 设置 请求执行情况的监听，一个请求同时只能设置一个（为了不至于前面设置的监听被覆盖掉）
     * 后期 考虑做成addOnRequestLifecycleCallback 可同时服务于多个监听者
     * @param onRequestLifecycleCallback
     */
    public void setOnRequestLifecycleCallback(OnRequestLifecycleCallback onRequestLifecycleCallback){
        if (null != mOnRequestLifecycleCallback){
            throw new IllegalArgumentException("OnRequestLifecycleListener already has, to invork this call removeOnRequestLifecycleListener first");
        }
        this.mOnRequestLifecycleCallback = onRequestLifecycleCallback;
    }
    /**
     * 注销 OnRequestLifecycleCallback 监听者
     */
    public void removeOnRequestLifecycleCallback(){
        mOnRequestLifecycleCallback = null;
    }

    @Override
    public void addMarker(String tag) {
        super.addMarker(tag);
        if ("add-to-queue".equals(tag)){
            mExecutorDelivery.postOnStart(this);
            if (null == getTag()){
                setTag(getSequence()+"");
            }
        }
    }
    public void deliverOnStart(){
        if (null != mOnRequestLifecycleCallback){
            mOnRequestLifecycleCallback.onStart();
        }
    }

    /**
     * 请求被取消
     */
    @Override
    public void cancel() {
        super.cancel();
        mExecutorDelivery.postOnCancle(this);
    }

    public void deliverOnCancel(){
        if(null != mOnRequestLifecycleCallback){
            mOnRequestLifecycleCallback.onCancle();
        }
    }

    @Override
    protected void finish(String tag) {
        super.finish(tag);
        mExecutorDelivery.postOnFinish(this,!("done".equals(tag)||"not-modified".equals(tag)));
    }

    public void deliverOnFinish(boolean isCancel){
        if(null != mOnRequestLifecycleCallback){
            mOnRequestLifecycleCallback.onFinish(isCancel);
        }
    }

    @Override
    protected void deliverResponse(T response) { //主线程中完成，由ExecutorDelivery的主线程handler执行
        if (null != mResponseCallback){
            mResponseCallback.onResponse(response);
        }
        mExecutorDelivery.postOnResult(this, response);
    }

    public void deliverOnResult(T response){
        if (null != mOnRequestLifecycleCallback){
            mOnRequestLifecycleCallback.onResult(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) { ////主线程中完成，由ExecutorDelivery的主线程handler执行
        super.deliverError(error);
        mExecutorDelivery.postOnError(this,error);
    }

    public void deliverOnError(Exception e){
        if(null != mOnRequestLifecycleCallback){
            mOnRequestLifecycleCallback.onError(e);
        }
    }

    public void deliverLoading(long sumDonedSize,long currentDone,long currentSpendedTime,long sumSpendedTime){
        long loaded = (0 != sumSpendedTime) ? ((1000 * sumDonedSize) / sumSpendedTime) : sumDonedSize;
        mExecutorDelivery.postOnLoad(this,loaded);
        if (null != mOnUpdateProgressListener){
            mOnUpdateProgressListener.onUpdateProgress(sumDonedSize, currentDone, currentSpendedTime, sumSpendedTime);
        }
    }

    public void deliverOnLoad(long loaded){
        if(null != mOnRequestLifecycleCallback){
            mOnRequestLifecycleCallback.onLoad(loaded);
        }
    }

    /**
     * 获取请求参数
     * @return
     * @throws AuthFailureError
     */
    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    public String getBodyContentType() {
        String contentType = "application/x-www-form-urlencoded";
        switch (mBodyContentType){
            case RCT_DEFAULT:
                contentType = "application/x-www-form-urlencoded";
                break;
            case RCT_MULTIPART: //若是multipart 文件上传的类型，则必须重写getBodyContentType方法（自定义个CacheRequest子类），返回指定的content-type(此中含有boundary)
                throw new RequestBodyContentSettingException(" for file multipart/form-data , you should override getBodyContentType ");
            case RCT_JSON:
                contentType = "application/json";
                break;
            case RCT_XML:
                contentType = "application/xml";
                break;
            default:
                contentType = "application/x-www-form-urlencoded";
                break;
        }

        return contentType+"; charset=" + getParamsEncoding();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        //setParams与setBodyContent检查，这两者不能同时设置
        if((null!=mParams&&mParams.size()>0)&&!TextUtils.isEmpty(mBodyContent)){
            throw new RequestBodyContentSettingException(" can't  setParams and setBodyContent, at same time! ");
        }

        switch (mBodyContentType){
            case RCT_DEFAULT: // application/x-www-form-urlencoded 普通表单数据
                Map<String, String> params = getParams();
                if (params != null && params.size() > 0) {
                    return encodeParameters(params, getParamsEncoding());
                }
                break;
            case RCT_MULTIPART: // multipart/form-data 文件上传表单数据，不处理。需要自定义CacheRequest子类，但不建议，直接使用getBody有可能会OOM
                throw new RequestBodyContentSettingException(" for file multipart/form-data , you should override getBodyContentType ");
            case RCT_JSON:
                if (null == mBodyContent || mBodyContent.length() <= 0){
                    return null;
                }
                //简单的检查提供的是否是json格式
                if(!(mBodyContent.startsWith("{")&&mBodyContent.endsWith("}"))){
                    throw new RequestBodyContentSettingException("request content body format error,is not json format! ");
                }
                try {
                    return mBodyContent.getBytes(getParamsEncoding());
                } catch (UnsupportedEncodingException e) {
                    throw new RequestBodyContentSettingException("Encoding not supported: " + getParamsEncoding(), e);
                }
            case RCT_XML:
                if (null == mBodyContent || mBodyContent.length() <= 0){
                    return null;
                }
                //简单的检查提供的是否是json格式
                if(!(mBodyContent.startsWith("<")&&mBodyContent.endsWith(">"))){
                    throw new RequestBodyContentSettingException("request content body format error,is not xml format! ");
                }
                try {
                    return mBodyContent.getBytes(getParamsEncoding());
                } catch (UnsupportedEncodingException e) {
                    throw new RequestBodyContentSettingException("Encoding not supported: " + getParamsEncoding(), e);
                }
            default:
                Map<String, String> defparams = getParams();
                if (defparams != null && defparams.size() > 0) {
                    return encodeParameters(defparams, getParamsEncoding());
                }
                break;
        }
        return null;
    }


    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }
}
