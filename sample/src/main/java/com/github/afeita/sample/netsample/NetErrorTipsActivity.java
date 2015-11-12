/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.afeita.sample.netsample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.github.afeita.log.L;
import com.github.afeita.net.VolleyLog;
import com.github.afeita.net.ext.AfeitaNet;
import com.github.afeita.net.ext.NetCallback;
import com.github.afeita.sample.R;

import java.util.HashMap;
import java.util.Map;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/21
 * <br /> email: chenshufei2@sina.com
 */
public class NetErrorTipsActivity extends Activity{

    private AfeitaNet afeitaNet;
    private int count = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netsample);
        initView();
        VolleyLog.DEBUG = true;
        afeitaNet = new AfeitaNet(NetErrorTipsActivity.this);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                postString();
                if (count > 0){
                    handler.postDelayed(this,200);
                    count -- ;
                }
            }
        },200);


    }

    private void initView() {
        //TODO
    }

    private void postString() {
        String url = "http://192.168.1.102:8080/Test/HelloServlet";
        Map<String,String> params = new HashMap<String,String>();
        params.put("username", "zhangsa");
        params.put("password","123456");
        afeitaNet.post(url, params, new NetCallback<String>() {
            @Override
            public void onResult(String response) {
                L.e("------++++++get HelloServlet post 调用成功:" + response);
            }

            @Override
            public void onError(Exception error) {
                L.e("------++++++get HelloServlet post 调用失败:" + error.getMessage());
            }
        });
    }


}
