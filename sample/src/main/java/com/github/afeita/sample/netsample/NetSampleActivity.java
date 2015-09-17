package com.github.afeita.sample.netsample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Toast;

import com.github.afeita.log.L;
import com.github.afeita.net.ext.AfeitaNet;
import com.github.afeita.net.ext.NetCallback;
import com.github.afeita.net.ext.RequestInfo;
import com.github.afeita.net.ext.TipsingNetCallback;
import com.github.afeita.sample.R;
import com.github.afeita.sample.netsample.bean.LoginAccount;
import com.github.afeita.sample.netsample.bean.UserInfo;
import com.github.afeita.sample.netsample.bean.Weather;
import com.github.afeita.tools.fastjson.JSON;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用AfeitaNet 访问网络示例
 * <br /> author: chenshufei
 * <br /> date: 15/9/11
 * <br /> email: chenshufei2@sina.com
 */
public class NetSampleActivity extends Activity implements View.OnClickListener {

    private AfeitaNet afeitaNet;
    private UploadFileProgreeDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netsample);
        initView();
        afeitaNet = new AfeitaNet(NetSampleActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放网络资源，注意：最佳实践是afeitaNet.stop()时应确保不再使用访问网络，比如放在app退出时调用。
        afeitaNet.stop();
    }

    private void initView() {
        findViewById(R.id.btn_get_string).setOnClickListener(NetSampleActivity.this);
        findViewById(R.id.btn_get_json).setOnClickListener(NetSampleActivity.this);
        findViewById(R.id.btn_post_string).setOnClickListener(NetSampleActivity.this);
        findViewById(R.id.btn_post_json).setOnClickListener(NetSampleActivity.this);
        findViewById(R.id.btn_post_json_body).setOnClickListener(NetSampleActivity.this);
        findViewById(R.id.btn_post_json_file).setOnClickListener(NetSampleActivity.this);
        findViewById(R.id.btn_delete_string).setOnClickListener(NetSampleActivity.this);
        findViewById(R.id.btn_delete_json).setOnClickListener(NetSampleActivity.this);
        findViewById(R.id.btn_put_string).setOnClickListener(NetSampleActivity.this);
        findViewById(R.id.btn_put_json).setOnClickListener(NetSampleActivity.this);
        findViewById(R.id.btn_put_json_body).setOnClickListener(NetSampleActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get_string:
                getString();
                break;
            case R.id.btn_get_json:
                getJson();
                break;
            case R.id.btn_post_string:
                postString();
                break;
            case R.id.btn_post_json:
                postJson();
                break;
            case R.id.btn_post_json_body:
                postJsonBody();
                break;
            case R.id.btn_post_json_file:
                postJsonFile();
                break;
            case R.id.btn_delete_string:
                deleteString();
                break;
            case R.id.btn_delete_json:
                deleteJson();
                break;
            case R.id.btn_put_string:
                putString();
                break;
            case R.id.btn_put_json:
                putJson();
            case R.id.btn_put_json_body:
                putJsonBody();
                break;
        }
    }

    //get请求，以String字符串返回请求结果
    private void getString() {
        String url = "http://v.juhe.cn/weather/index?format=2&cityname=%E8%8B%8F%E5%B7%9E&key=f5f4c68c9ad92f5a226835d88e1a478f";
        //TipsingNetCallback也可以用NetCallback代替...
        afeitaNet.get(url, null, new NetCallback<String>() {
            @Override
            public void onResult(String response) {
                Toast.makeText(NetSampleActivity.this, "getString 调用成功:" + response, Toast.LENGTH_LONG).show();
            }
        });
    }

    //get请求响应是json,以指定类的实例对象返回请求结果
    private void getJson() {
        String url = "http://v.juhe.cn/weather/index?dtype=json&format=2&cityname=%E8%8B%8F%E5%B7%9E&key=f5f4c68c9ad92f5a226835d88e1a478f";
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.isShowLoadingDialog = false;
        afeitaNet.setIsShowLoadingDialog(false);
        //--- 设置缓存时间
        requestInfo.instantExpire = 3 * 60 * 1000; //瞬时缓存3分钟
        requestInfo.finalExpire = 1 * 60 * 60 * 1000; //二次缓存时间 1小时，这两个参数成队出现，要不都不设置，要不都设置
        afeitaNet.getForJson(requestInfo, new NetCallback<Weather>() {
            @Override
            public void onResult(Weather weather) {
                Toast.makeText(NetSampleActivity.this, "getJson 调用成功:" + weather, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Exception error) {
                Toast.makeText(NetSampleActivity.this, "--->调用失败 error原因：" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, Weather.class);
    }

    //post请求带入参，以String字符串返回请求结果
    private void postString() {
        String url = "http://192.168.1.102:8080/Test/HelloServlet";
        Map<String,String> params = new HashMap<String,String>();
        params.put("username", "zhangsa");
        params.put("password","123456");
        afeitaNet.post(url, params, new NetCallback<String>() {
            @Override
            public void onResult(String response) {
                Toast.makeText(NetSampleActivity.this, "get HelloServlet post 调用成功:" + response, Toast.LENGTH_LONG).show();
            }
        });
    }

    //post请求带入参响应是json,以指定类的实例对象返回请求结果
    private void postJson() {
        String url = "http://192.168.1.102:8080/Test/HelloServlet";
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.setParams("username", "zhangsa");
        requestInfo.setParams("password", "123456");
        requestInfo.isShowLoadingDialog = false;
        afeitaNet.postJson(requestInfo, new TipsingNetCallback<UserInfo>(this) {
            @Override
            public void onResult(UserInfo response) {
                Toast.makeText(NetSampleActivity.this, "get HelloServlet post 调用成功:" + response, Toast.LENGTH_LONG).show();
            }
        }, UserInfo.class);

    }

    //post请求,以json形式封装请求参数放在请求体中。响应是json,以指定类的实例对象返回请求结果
    private void postJsonBody() {
        LoginAccount la = new LoginAccount("zhangsa","123456");
        String url = "http://192.168.1.102:8080/Test/HelloServlet";
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.contentType = RequestInfo.ContentType.CT_JSON;
        requestInfo.bodyContent = JSON.toJSONString(la); //将LoginAccount实例属性，转成json字符串
        afeitaNet.postJson(requestInfo, new NetCallback<UserInfo>() {
            @Override
            public void onResult(UserInfo response) {
                Toast.makeText(NetSampleActivity.this, "get HelloServlet post 调用成功:" + response, Toast.LENGTH_LONG).show();
            }
        },UserInfo.class);
    }

    //post请求，参数有文件流（文件上传）与普通字符串参数。响应是json,以指定类的实例对象返回请求结果
    private void postJsonFile() {
        //简化，实际要判断Environment.getExternalStorageState()状态，及sdcard中是否存在111.png与222.png
        File file1 = new File(Environment.getExternalStorageDirectory(),"111.jpg");
        File file2 = new File(Environment.getExternalStorageDirectory(),"222.jpg");
        if (file1.exists()){
            L.e("file1存在，大小："+file1.length());
        }else {
            L.e("file1不存在，大小：");
        }

        if (file2.exists()){
            L.e("file2存在，大小："+file2.length());
        }else{
            L.e("file2不存在，大小：");
        }

        String url = "http://192.168.1.102:8080/Test/UploadHandleServlet";
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.setParams("eamil", "chenshufei2@sina.com");
        requestInfo.setParams("mobileNum", "18688888888"); //普通字符串参数
        requestInfo.setParams("file1",file1); //上传的文件1
        requestInfo.setParams("file2",file2); //上传的文件2
        requestInfo.isShowLoadingDialog = false;
        afeitaNet.post(requestInfo, new NetCallback<String>() {
            @Override
            public void onResult(String response) {
                Toast.makeText(NetSampleActivity.this, "get HelloServlet post 调用成功:" + response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish(boolean isCancel) {
                if (null != dialog) {
                    dialog.dismiss();
                    dialog = null;
                }
            }

            //-----可选监听文件上传进度,并显示进度
            @Override
            public void onUpload(int fileNum, String currentUploadingFilename, long sumSize, long sumDonedSize, long sumSpendedTime) {
                if (null == dialog) {
                    dialog = new UploadFileProgreeDialog(NetSampleActivity.this);
                    dialog.show();
                }
                dialog.setFileTip(currentUploadingFilename);
                int precent = (int) ((sumDonedSize * 1.0 / sumSize) * 100);
                String strSumSize = Formatter.formatFileSize(NetSampleActivity.this, sumSize);
                String strSumDonedSize = Formatter.formatFileSize(NetSampleActivity.this, sumDonedSize);
                String strProgress = strSumDonedSize + "/" + strSumSize;
                dialog.setProgress(precent, strProgress, precent + "%");
            }
        });
    }

    //delete请求，以String字符串返回请求结果
    private void deleteString() {
        String url = "http://v.juhe.cn/weather/index?format=2&cityname=%E8%8B%8F%E5%B7%9E&key=f5f4c68c9ad92f5a226835d88e1a478f";
        //TipsingNetCallback也可以用NetCallback代替...
        afeitaNet.delete(url, null, new NetCallback<String>() {
            @Override
            public void onResult(String response) {
                Toast.makeText(NetSampleActivity.this, "getString 调用成功:" + response, Toast.LENGTH_LONG).show();
            }
        });
    }

    //delete请求响应是json,以指定类的实例对象返回请求结果
    private void deleteJson() {
        String url = "http://v.juhe.cn/weather/index?dtype=json&format=2&cityname=%E8%8B%8F%E5%B7%9E&key=f5f4c68c9ad92f5a226835d88e1a478f";
        afeitaNet.deleteJson(url, null, new NetCallback<Weather>() {
            @Override
            public void onResult(Weather weather) {
                Toast.makeText(NetSampleActivity.this, "getJson 调用成功:" + weather, Toast.LENGTH_LONG).show();
            }
        }, Weather.class);
    }

    //put请求带入参，以String字符串返回请求结果
    private void putString() {
        String url = "http://192.168.1.102:8080/Test/HelloServlet";
        Map<String,String> params = new HashMap<String,String>();
        params.put("username","zhangsa");
        params.put("password","123456");
        afeitaNet.put(url, params, new NetCallback<String>() {
            @Override
            public void onResult(String response) {
                Toast.makeText(NetSampleActivity.this, "get HelloServlet post 调用成功:" + response, Toast.LENGTH_LONG).show();
            }
        });
    }

    //put请求带入参响应是json,以指定类的实例对象返回请求结果
    private void putJson() {
        String url = "http://192.168.1.102:8080/Test/HelloServlet";
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.setParams("username", "zhangsa");
        requestInfo.setParams("password", "123456");
        afeitaNet.putJson(requestInfo, new NetCallback<UserInfo>() {
            @Override
            public void onResult(UserInfo response) {
                Toast.makeText(NetSampleActivity.this, "get HelloServlet post 调用成功:" + response, Toast.LENGTH_LONG).show();
            }
        }, UserInfo.class);
    }

    //put请求,以json形式封装请求参数放在请求体中。响应是json,以指定类的实例对象返回请求结果
    private void putJsonBody() {
        LoginAccount la = new LoginAccount("zhangsa","123456");
        String url = "http://192.168.1.102:8080/Test/HelloServlet";
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = url;
        requestInfo.contentType = RequestInfo.ContentType.CT_JSON;
        requestInfo.bodyContent = JSON.toJSONString(la); //将LoginAccount实例属性，转成json字符串
        afeitaNet.putJson(requestInfo, new NetCallback<UserInfo>() {
            @Override
            public void onResult(UserInfo response) {
                Toast.makeText(NetSampleActivity.this, "get HelloServlet post 调用成功:" + response, Toast.LENGTH_LONG).show();
            }
        },UserInfo.class);
    }
}
