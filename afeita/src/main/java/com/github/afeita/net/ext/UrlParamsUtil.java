package com.github.afeita.net.ext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/9
 * <br /> email: chenshufei2@sina.com
 */
public class UrlParamsUtil {

    public static String getFullUrl(String url,Map<String,String> params){
        if (url != null && params != null) {
            StringBuilder sb = new StringBuilder();
            if (!url.contains("?")) {
                url = url + "?";
            } else {
                if (!url.endsWith("?")) {
                    url = url + "&";
                }
            }
            try {
                for (Map.Entry<String,String> entry : params.entrySet()) {
                    String key = entry.getKey().trim();
                    if (null != key && key.length() >0 ){
                        sb.append(URLEncoder.encode(key, "utf-8")).append("=")
                                .append(URLEncoder.encode(entry.getValue(), "utf-8")).append("&");
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (sb.lastIndexOf("&") == sb.length() - 1) {
                sb.deleteCharAt(sb.length() - 1);
            }
            return url + sb.toString();
        }
        return url;
    }
}
