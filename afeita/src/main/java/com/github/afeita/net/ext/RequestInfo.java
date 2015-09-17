package com.github.afeita.net.ext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求信息类  收集url/param参数等等
 * <br /> author: chenshufei
 * <br /> date: 15/9/9
 * <br /> email: chenshufei2@sina.com
 */
public class RequestInfo {
    /**
     * 请求访问的url
     */
    public String url;
    /**
     * 用于取消请正在处理的请求的执行,设置了tag标准后，
     * 没有设置canTag时，将自动设置。activity退出时自动取消
     * 可在afeitaNet.cancalAll(cancelTag)
     */
    public String cancelTag;

    /**
     * 普通String key - value 类的请求参数
     */
    public Map<String,String> stringParams;

    /**
     * 文件上传的 请求参数
     */
    public Map<String,File> fileParams;

    /**
     * 请求头，可设置相关的请求头
     */
    public Map<String,String> header;

    /**
     * 是否显示网络访问请求的loading加载中...提示对话框，默认为true显示。 <br />
     * 最佳实践是当上传文件请求且要显示文件的上传进度时，isShowDialog设置成false，关闭默认的正在加载数据中...提示。<br />
     * 或者确认此网络访问请求，不需要提示用户，正在加载数据中...可以设置成false <br />
     * 注意默认true显示的，仅在activity中的网络访问请求。在service默认不显示。
     */
    public boolean isShowLoadingDialog = true;


    /**
     * 当请求异步时NetCall的onError被回调时，错误信息是否以自定义Toast显示错误信息
     */
    //public boolean isShowError = true;

    //-------------------------------------------缓存时间设置部分----------------------------------------------------------------------------
    //------当响应头中Cache-Control是no-cache或no-store 缓存时间设置无效，代表服务端强制要求客服端不要缓存
    /**
     * 瞬时缓存时间 单位mm 毫秒
     * 代表，mInstantExpire内，将不访问网络，直接从缓存中读取响应数据 <br />
     * 当响应头中Cache-Control是no-cache或no-store 缓存时间设置无效，代表服务端强制要求客服端不要缓存
     */
    public long instantExpire;

    /**
     * 二次缓存时间 单位mm 毫秒
     * 代表 超过mInstantExpire，小于finalExpire时内，
     * 首先从缓存中读取响应数据，并立即重新访问网络获取新数据，有新数据（非304的响应）再回调新数据结果 <br />
     * 当响应头中Cache-Control是no-cache或no-store 缓存时间设置无效，代表服务端强制要求客服端不要缓存
     */
    public long finalExpire;

    //-------------------------------------------请求体数据设置部分----------------------------------------------------------------------------

    /**
     * 请求体数据 类型 枚举
     * CT_DEFAULT 代表 application/x-www-form-urlencoded  <br />
     * CT_JSON 代表 application/json  <br />
     * CT_XML 代表 application/xml  <br />
     * 注意：当使用afeitaNet.postXXX且fileParams有数据，表单类型multipart/form-data自动识别追加
     */
    public enum ContentType{
        CT_DEFAULT,CT_JSON,CT_XML
    }

    /**
     * 请求体 类型,默认为application/x-www-form-urlencoded
     */
    public ContentType contentType = ContentType.CT_DEFAULT;


    /**
     * 请求体 内容，当contentType设置成CT_JSON或CT_XML时，可将json串或xml串设置成请求体
     */
    public String bodyContent;

    //-------------------------------------------请求参数快速设置部分----------------------------------------------------------------------------

    public void setParams(String key,String value){
        if (null == stringParams){
            stringParams = new HashMap<String,String>();
        }
        stringParams.put(key,value);
    }

    public void setParams(String key,File file){
        if (null == fileParams){
            fileParams = new HashMap<String,File>();
        }
        fileParams.put(key,file);
    }


}
