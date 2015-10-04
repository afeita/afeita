/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.afeita.net.ext;

import com.github.afeita.net.VolleyLog;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/23
 * <br /> email: chenshufei2@sina.com
 */
public class NetConfig {
    //是否开始开发环境 ssl 证书不做校验
    public static boolean IS_SSL_VALIDATIONENABLED = true;

    //请求失败时，尝试几次（含有第一次的，即共请求几次）
    public static int REQUEST_FAILURE_RETRYTIME = 2;

    // 静态初始化
    static {   //--默认Volley Log开启
        VolleyLog.DEBUG = true;
    }
}
