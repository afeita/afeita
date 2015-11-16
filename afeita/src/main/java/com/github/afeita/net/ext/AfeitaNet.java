package com.github.afeita.net.ext;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;

import com.github.afeita.net.Request;
import com.github.afeita.net.RequestQueue;
import com.github.afeita.net.Response;
import com.github.afeita.net.ext.cookie.CookieStore;
import com.github.afeita.net.ext.cookie.iml.CookieStoreHandler;
import com.github.afeita.net.ext.request.CacheRequest;
import com.github.afeita.net.ext.request.FileRequest;
import com.github.afeita.net.ext.request.JsonFileRequest;
import com.github.afeita.net.ext.request.JsonRequest;
import com.github.afeita.net.ext.request.StringFileRequest;
import com.github.afeita.net.ext.request.StringRequest;
import com.github.afeita.net.toolbox.Volley;
import com.github.afeita.tools.deviceyear.YearClass;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求帮助类，提供简单统一的访问网络方式
 * <br /> author: chenshufei
 * <br /> date: 15/9/9
 * <br /> email: chenshufei2@sina.com
 */
public class AfeitaNet {
    private static RequestQueue requestQueue;

    //请求loading...中提示的对话框 FragmentDialog 请求进度提示 默认的提示，可以setRequestLoadingTipsDialog对话框自定义样式
    private WeakReference<DialogFragment> mRequestLoadingTipsDialogRef;

    private WeakReference<Activity> mActivityRefercens;

    //是否开启请求进度提示true,false  false则此AfeitaNet实例发出的请求都不会显示进度提示对话框,
    //默认true会显示（若AfeitaNet构造实例化传入的Context是Activity会是true显示，否则会是false不显示）
    private boolean mIsShowLoadingDialog ;

    //是否打印请求日志
    private boolean mIsDebugLog = true;

    //全局请求头
    private Map<String,String> mGlobalRequestHeaders;


    private static void initRequestQueue(Context context) {
        if (null == requestQueue){
            //获取应用的上下文，防止若传activity进来，后续使用不当造成activity泄露
            Context applicationContext = context.getApplicationContext();
            //获取设备配置信息的所谓年份
            int year = YearClass.get(applicationContext);
            if (year >= 2014){ //主流手机，手机的硬件配置很高，可以多跑几个线程没问题
                requestQueue = Volley.newRequestQueue(applicationContext, 8);
            }else{ //13年以下采用默认的线程数，默认4个网络线程
                requestQueue = Volley.newRequestQueue(applicationContext);
            }
        }
    }

    public AfeitaNet(Context context){
        initRequestQueue(context);
        //CookieStore初始化
        CookieStoreHandler.getInstance().init(context);
        if (context instanceof Activity){
            Activity activity = (Activity) context;
            mActivityRefercens = new WeakReference<Activity>(activity);
            mIsShowLoadingDialog = true;
        }else{
            mIsShowLoadingDialog = false;
        }
    }

    /**
     * 获取CookieStore
     * @return
     */
    public CookieStore getCookieStore(){
        return CookieStoreHandler.getInstance();
    }

    public void setGlobalRequestHeaders(Map<String,String> headers){
        this.mGlobalRequestHeaders = headers;
    }

    /**
     * 是否开启请求loading加载中...提示对话框true,false <br />
     * false代表此AfeitaNet实例发出的请求都不会显示loading加载中...进度提示对话框, <br />
     * true代表会显示loading加载中...提示对话框 <br />
     * 默认true会显示（若AfeitaNet构造实例化传入的Context是Activity会是true显示，否则会是false不显示）
     * @param isShowLoadingDialog
     */
    public void setIsShowLoadingDialog(boolean isShowLoadingDialog) {
        this.mIsShowLoadingDialog = isShowLoadingDialog;
    }

    /**
     *  设置是否打印请求的详细日志，包括请求的url、参数、响应等
     * @param isDebugLog true 打印， false 不打印
     */
    public void setIsDebugLog(boolean isDebugLog){
        this.mIsDebugLog = isDebugLog;
    }

    /**
     * 设置请求loading...提示的对话框,作用：可自定义请求loading...加载中的对话框样式
     * @param requestLoadingTipsDialog
     */
    public void setRequestLoadingTipsDialog(DialogFragment requestLoadingTipsDialog){
        if (null != requestLoadingTipsDialog){
            if (null != mRequestLoadingTipsDialogRef){
                mRequestLoadingTipsDialogRef.clear();
                mRequestLoadingTipsDialogRef = null;
            }
            mRequestLoadingTipsDialogRef = new WeakReference<DialogFragment>(requestLoadingTipsDialog);
        }
    }

    /**
     * 获取RequestQueue，若更习惯使用原始RequestQueue访问。
     * 亦或已经使用了Volley，想去原来Volley库的依赖。
     * @param context
     * @return
     */
    public RequestQueue getRequestQueue(Context context){
        initRequestQueue(context);
        return requestQueue;
    }

    /**
     * 取消正在进行或等待中的... 网络访问请求 <br />
     * 注意cancelTag 传入null时，则取消本次AfeitaNet发出的所有正在进行或等待中的... 网络访问请求
     * @param cancelTag
     */
    public void cancelAll(String cancelTag){
        if (cancelTag != null){
            if (null != requestQueue){
                requestQueue.cancelAll(cancelTag);
            }
        }else{
            requestQueue.cancelAll(AfeitaNet.class.getSimpleName());
        }
    }

    /**
     * 当确定不用时，应该关闭掉线程访问。
     * 比如当退出应用时，应该调用stop释放相关的资源
     */
    public static void stop(){
        if (null != requestQueue){
            requestQueue.stop();
            requestQueue = null;
        }
    }



    //-------------------------------------------------public支持Restful访问网络风格部分----------------------------------------------------------------------------------
    /**
     * get方式的网络访问
     * @param url
     * @param params 请求入参 key参数名-value参数值 的map
     * @param callback 请求处理的回调（String格式回调）
     */
    public void get(String url,Map<String,String> params,NetCallback<String> callback){
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.stringParams = params;
        get(requestInfo, callback);
    }
    /**
     * get方式的网络访问
     * @param requestInfo
     * @param callback 请求处理的回调（String格式回调）
     */
    public void get(RequestInfo requestInfo,NetCallback<String> callback){
        String url = UrlParamsUtil.getFullUrl(requestInfo.url,requestInfo.stringParams);
        StringRequest stringRequest = new StringRequest(url,null);
        requestInfo.stringParams = null;
        setCommonRequestInfo(requestInfo,stringRequest);
        setCommonCallback(callback, stringRequest,requestInfo);
        requestQueue.add(stringRequest);
    }

    /**
     * get方式的网络访问
     * @param url
     * @param params 请求入参 key参数名-value参数值 的map
     * @param callback callback 请求处理的回调（json自动转化实例类对象格式回调）
     * @param clazz 返回json格式对应的实体类
     * @param <T>
     */
    public <T> void getForJson(String url,Map<String,String> params,NetCallback<T> callback,Class<T> clazz){
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.stringParams = params;
        getForJson(requestInfo, callback, clazz);
    }

    /**
     * get方式的网络访问
     * @param requestInfo
     * @param callback callback 请求处理的回调（json自动转化实例类对象格式回调）
     * @param clazz 返回json格式对应的实体类
     * @param <T>
     */
    public <T> void getForJson(RequestInfo requestInfo,NetCallback<T> callback,Class<T> clazz){
        String url = UrlParamsUtil.getFullUrl(requestInfo.url,requestInfo.stringParams);
        JsonRequest<T> jsonRequest = new JsonRequest<T>(url,null,clazz);
        requestInfo.stringParams = null;
        setCommonRequestInfo(requestInfo,jsonRequest);
        setCommonCallback(callback, jsonRequest,requestInfo);
        requestQueue.add(jsonRequest);
    }

    /**
     * post方式的网络访问
     * @param url
     * @param params
     * @param callback 请求处理的回调（String格式回调）
     */
    public void post(String url,Map<String,String> params,NetCallback<String> callback){
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.stringParams = params;
        post(requestInfo, callback);
    }

    /**
     * post方式的网络访问
     * @param requestInfo 当RequestInfo的fileParams设置了时，自动变成文件上传的post请求
     *                    fileParams为空，就是普通的POST请求
     * @param callback 请求处理的回调（String格式回调）
     */
    public void post(RequestInfo requestInfo, NetCallback<String> callback) {
        CacheRequest<String> cacheRequest = null;
        if(null != requestInfo.fileParams && requestInfo.fileParams.size() > 0){
            cacheRequest = new StringFileRequest(requestInfo.url,null);
        }else{
            cacheRequest = new StringRequest(Request.Method.POST,requestInfo.url,null);
        }
        setCommonRequestInfo(requestInfo,cacheRequest);
        setCommonCallback(callback, cacheRequest,requestInfo);
        requestQueue.add(cacheRequest);
    }

    /**
     * post方式的网络访问
     * @param url
     * @param params
     * @param callback callback 请求处理的回调（json自动转化实例类对象格式回调）
     * @param clazz 返回json格式对应的实体类
     * @param <T>
     */
    public <T> void postJson(String url,Map<String,String> params,NetCallback<T> callback,Class<T> clazz){
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.stringParams = params;
        postJson(requestInfo, callback, clazz);
    }

    /**
     * post方式的网络访问
     * @param requestInfo 当RequestInfo的fileParams设置了时，自动变成文件上传的post请求
     *                    fileParams为空，就是普通的POST请求
     * @param callback callback 请求处理的回调（json自动转化实例类对象格式回调）
     * @param clazz 返回json格式对应的实体类
     * @param <T>
     */
    public <T> void postJson(RequestInfo requestInfo, NetCallback<T> callback, Class<T> clazz) {
        CacheRequest<T> cacheRequest = null;
        if(null != requestInfo.fileParams && requestInfo.fileParams.size() > 0){
            cacheRequest = new JsonFileRequest<T>(requestInfo.url,null,clazz);
        }else{
            cacheRequest = new JsonRequest<T>(Request.Method.POST,requestInfo.url,null,clazz);
        }
        setCommonRequestInfo(requestInfo,cacheRequest);
        setCommonCallback(callback, cacheRequest,requestInfo);
        requestQueue.add(cacheRequest);
    }

    /**
     * delete方式的网络访问
     * @param url
     * @param params 请求入参 key参数名-value参数值 的map
     * @param callback 请求处理的回调（String格式回调）
     */
    public void delete(String url,Map<String,String> params,NetCallback<String> callback){
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.stringParams = params;
        delete(requestInfo, callback);
    }

    /**
     * delete方式的网络访问
     * @param requestInfo
     * @param callback 请求处理的回调（String格式回调）
     */
    public void delete(RequestInfo requestInfo,NetCallback<String> callback){
        String url = UrlParamsUtil.getFullUrl(requestInfo.url,requestInfo.stringParams);
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,url,null);
        requestInfo.stringParams = null;
        setCommonRequestInfo(requestInfo,stringRequest);
        setCommonCallback(callback, stringRequest,requestInfo);
        requestQueue.add(stringRequest);
    }

    /**
     * delete方式的网络访问
     * @param url
     * @param params
     * @param callback callback 请求处理的回调（json自动转化实例类对象格式回调）
     * @param clazz 返回json格式对应的实体类
     * @param <T>
     */
    public <T> void deleteJson(String url,Map<String,String> params,NetCallback<T> callback,Class<T> clazz){
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.stringParams = params;
        deleteJson(requestInfo, callback, clazz);
    }

    /**
     * delete方式的网络访问
     * @param requestInfo
     * @param callback callback 请求处理的回调（json自动转化实例类对象格式回调）
     * @param clazz 返回json格式对应的实体类
     * @param <T>
     */
    public <T> void deleteJson(RequestInfo requestInfo,NetCallback<T> callback,Class<T> clazz){
        String url = UrlParamsUtil.getFullUrl(requestInfo.url,requestInfo.stringParams);
        JsonRequest<T> jsonRequest = new JsonRequest<T>(Request.Method.DELETE,url,null,clazz);
        requestInfo.stringParams = null;
        setCommonRequestInfo(requestInfo,jsonRequest);
        setCommonCallback(callback, jsonRequest,requestInfo);
        requestQueue.add(jsonRequest);
    }


    /**
     * put方式的网络访问
     * @param url
     * @param params
     * @param callback 请求处理的回调（String格式回调）
     */
    public void put(String url,Map<String,String> params,NetCallback<String> callback){
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.stringParams = params;
        put(requestInfo, callback);
    }

    /**
     * put方式的网络访问
     * @param requestInfo 当RequestInfo的fileParams设置了时，自动变成文件上传的post请求
     *                    fileParams为空，就是普通的POST请求
     * @param callback 请求处理的回调（String格式回调）
     */
    public void put(RequestInfo requestInfo, NetCallback<String> callback) {
        CacheRequest<String> cacheRequest = null;
        if(null != requestInfo.fileParams && requestInfo.fileParams.size() > 0){
            cacheRequest = new StringFileRequest(Request.Method.PUT,requestInfo.url,null);
        }else{
            cacheRequest = new StringRequest(Request.Method.PUT,requestInfo.url,null);
        }
        setCommonRequestInfo(requestInfo,cacheRequest);
        setCommonCallback(callback, cacheRequest,requestInfo);
        requestQueue.add(cacheRequest);
    }


    /**
     * put方式的网络访问
     * @param url
     * @param params
     * @param callback callback 请求处理的回调（json自动转化实例类对象格式回调）
     * @param clazz 返回json格式对应的实体类
     * @param <T>
     */
    public <T> void putJson(String url,Map<String,String> params,NetCallback<T> callback,Class<T> clazz){
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.stringParams = params;
        putJson(requestInfo, callback, clazz);
    }

    /**
     * put方式的网络访问
     * @param requestInfo 当RequestInfo的fileParams设置了时，自动变成文件上传的post请求
     *                    fileParams为空，就是普通的POST请求
     * @param callback callback 请求处理的回调（json自动转化实例类对象格式回调）
     * @param clazz 返回json格式对应的实体类
     * @param <T>
     */
    public <T> void putJson(RequestInfo requestInfo, NetCallback<T> callback, Class<T> clazz) {
        CacheRequest<T> cacheRequest = null;
        if(null != requestInfo.fileParams && requestInfo.fileParams.size() > 0){
            cacheRequest = new JsonFileRequest<T>(Request.Method.PUT,requestInfo.url,null,null,clazz);
        }else{
            cacheRequest = new JsonRequest<T>(Request.Method.PUT,requestInfo.url,null,clazz);
        }
        setCommonRequestInfo(requestInfo,cacheRequest);
        setCommonCallback(callback, cacheRequest,requestInfo);
        requestQueue.add(cacheRequest);
    }


    //-------------------------------------------private内部通常设置部分----------------------------------------------------------------------------
    private <T> void setCommonCallback(final NetCallback<T> callback, final CacheRequest<T> cacheRequest,RequestInfo requestInfo) {
        TipsingNetCallback<T> tempTipsNetCallback= null;
        if (mIsShowLoadingDialog&&requestInfo.isShowLoadingDialog){ //是否展示 请求loading...加载中对话框
            if (null != mActivityRefercens){
                tempTipsNetCallback = new TipsingNetCallback<T>(mActivityRefercens.get()) {
                    @Override
                    public void onBegin() {
                        if (null != callback){
                            callback.onStart();
                        }
                    }

                    @Override
                    public void onUpload(int fileNum,String currentUploadingFilename,long sumSize, long sumDonedSize, long sumSpendedTime) {
                        if (null != callback){
                            callback.onUpload(fileNum,currentUploadingFilename,sumSize,sumDonedSize,sumSpendedTime);
                        }
                    }

                    @Override
                    public void onLoad(long loaded) {
                        if (null != callback){
                            callback.onLoad(loaded);
                        }
                    }

                    @Override
                    public void onCancle() {
                        if (null != callback){
                            callback.onCancle();
                        }
                    }

                    @Override
                    public void onResult(T response) {
                        super.onResult(response);
                        if (null != callback){
                            callback.onResult(response);
                        }
                    }

                    @Override
                    public void onError(Exception error) {
                        super.onError(error);
                        if (null != callback){
                            callback.onError(error);
                        }
                    }

                    @Override
                    public void onEnd(boolean isCancel) {
                        if (null != callback){
                            callback.onFinish(isCancel);
                        }
                    }
                };
                //有自定义的，显示自定义的对话框
                if (null != mRequestLoadingTipsDialogRef){
                    tempTipsNetCallback.setRequestTipsDialog(mRequestLoadingTipsDialogRef.get());
                }
            }
        }

        final TipsingNetCallback<T> tipsingNetCallback = tempTipsNetCallback;

        final boolean isFileUploadRequest = cacheRequest instanceof FileRequest;
        final boolean isActivityFinish = (null != mActivityRefercens) && (null != mActivityRefercens.get()) && (mActivityRefercens.get().isFinishing());
        if (isFileUploadRequest){
            FileRequest fileRequest = (FileRequest) cacheRequest;
            fileRequest.setOnUploadProgressListener(new Response.OnUploadProgressListener() {
                @Override
                public void onUploadProgress(int fileNum,String currentUploadingFilename,long sumSize, long sumDonedSize, long sumSpendedTime) {
                    if (null != tipsingNetCallback){
                        tipsingNetCallback.onUpload(fileNum,currentUploadingFilename,sumSize, sumDonedSize, sumSpendedTime);
                    }else{
                        if (null != callback){
                            callback.onUpload(fileNum,currentUploadingFilename,sumSize,sumDonedSize,sumSpendedTime);
                        }
                    }
                }
            });
        }
        cacheRequest.setOnRequestLifecycleCallback(cacheRequest.new OnRequestLifecycleCallback(){
            @Override
            public void onStart() {
                if (null != tipsingNetCallback){
                    tipsingNetCallback.onStart();
                }else{
                    if (null != callback){
                        callback.onStart();
                    }
                }
            }

            @Override
            public void onLoad(long loaded) {
                cancelRequestIfActivityFinish();
                if (null != tipsingNetCallback){
                    tipsingNetCallback.onLoad(loaded);
                }else{
                    if (null != callback){
                        callback.onLoad(loaded);
                    }
                }
            }

            private void cancelRequestIfActivityFinish() {
                if (!isFileUploadRequest&&isActivityFinish){
                    Object tag = cacheRequest.getTag();
                    if (null != tag){
                        requestQueue.cancelAll(tag);
                    }
                }
            }

            @Override
            public void onCancle() {
                if (null != tipsingNetCallback){
                    tipsingNetCallback.onCancle();
                }else{
                    if (null != callback){
                        callback.onCancle();
                    }
                }
            }

            @Override
            public void onResult(T response) {
                cancelRequestIfActivityFinish();
                if (null != tipsingNetCallback){
                    tipsingNetCallback.onResult(response);
                }else{
                    if (null != callback){
                        callback.onResult(response);
                    }
                }
            }

            @Override
            public void onError(Exception error) {
                cancelRequestIfActivityFinish();
                if (null != tipsingNetCallback){
                    tipsingNetCallback.onError(error);
                }else{
                    if (null != callback){
                        callback.onError(error);
                    }
                }
            }

            @Override
            public void onFinish(boolean isCancel) {
                if (null != tipsingNetCallback){
                    tipsingNetCallback.onFinish(isCancel);
                }else{
                    if (null != callback){
                        callback.onFinish(isCancel);
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void setCommonRequestInfo(RequestInfo requestInfo,CacheRequest<?> cacheRequest){
        if (null != requestInfo.cancelTag){
            cacheRequest.setTag(requestInfo.cancelTag);
        }

        cacheRequest.setLogDebugRequest(mIsDebugLog);
        cacheRequest.setInstantExpire(requestInfo.instantExpire);
        cacheRequest.setFinalExpire(requestInfo.finalExpire);
        cacheRequest.setIsUseCacheIfCacheExist(requestInfo.isUseCacheIfCacheExist);
        switch (requestInfo.contentType){
            case CT_JSON:
                cacheRequest.setBodyContentType(CacheRequest.RctType.RCT_JSON);
                break;
            case CT_XML:
                cacheRequest.setBodyContentType(CacheRequest.RctType.RCT_XML);
                break;
            case CT_DEFAULT:
                cacheRequest.setBodyContentType(CacheRequest.RctType.RCT_DEFAULT);
                break;
        }
        if (cacheRequest instanceof  FileRequest){ //处理文件上传类的请求信息设置
            FileRequest fileRequest = (FileRequest)cacheRequest;
            if (null != requestInfo.stringParams && requestInfo.stringParams.size() > 0){
                fileRequest.addStringParts(requestInfo.stringParams);
            }
            if (null != requestInfo.fileParams && requestInfo.fileParams.size() > 0){
                fileRequest.addFileParts(requestInfo.fileParams);
            }
        }else{ //普通类的请求，body中放数据，要求key=value形式的,要求json/xml等其它的字符串类
            cacheRequest.setParams(requestInfo.stringParams);
            cacheRequest.setBodyContent(requestInfo.bodyContent);
        }

        //设置请求头
        Map<String,String> headers = new HashMap<String,String>();
        if (null != mGlobalRequestHeaders && mGlobalRequestHeaders.size() > 0){
            headers.putAll(mGlobalRequestHeaders);
        }
        if (null != requestInfo.header && requestInfo.header.size() > 0){
            headers.putAll(requestInfo.header);
        }
        if (headers.size() > 0){
            cacheRequest.setHeader(headers);
        }
    }


}
