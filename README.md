# afeita
android quickly development framework
##简介
afeita 打造Android快速开发框架 <br/>
主要分以下几个组成部分：<br/>
* com.github.afeita.net   
    ---网络及简单图片访问模块（基于Volley）
* com.github.afeita.db  
    ---数据库模块
* com.github.afeita.filemanager  
    ---文件管理模块（提供文件的crud及文件下载等）
* com.github.afeita.threedpool  
    ---线程调试模块(线程池 队列，统一管理线程创建及销毁)
* com.github.afeita.log  
    ---日志模块
* com.github.afeita.precentlayout  
    ---布局适配模块(参考web开发的百分比，达到任何尺寸与分辨率屏幕都适配的布局)
* com.github.afeita.glide  
    ---图片加载模块（基于glide）
* com.github.afeita.utils  
    ---实用工具模块 （比如fastjson、日期时间格式工具、手机设置年份、网络连接判断等等）
* com.github.afeita.bus  
    ---总线控制模块，点到面的通信或点到点的通信
* com.github.afeita.security  
    ---安全模块 （webview安全、数据的加密、SQL注入、防二次打包、界面劫持检查等）
* com.github.afeita.viewinject  
    ---View注入模块(简化findView,setListern...)
* com.github.afeita.widget  
    ---自定义的View(比如自定义的智能Toast显示时长)
* com.github.afeita.plug  
    ---动态插件式加载模块




*******
##权限及最小API版本要求
###权限
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
###最小API版本要求
android:minSdkVersion="11"  Anroid 3.0.x以上版本




*******
##混淆要求
项目中使用到的所有AfeitaDb save的bean与AfeitaNet json请求的bean 需要keep,不能混淆
```
-keep class com.github.afeita.net.ext.cookie.persistent.CookieBean { *; }
```




*******
##net
AfeitaNet是基于Volley的快速开发网络框架，主要有以下特点：
* 支持智能增加减少网络请求队列数量  
    --基于facebook手机设备分析算法，性能优越手机更多网络请求队列，满足请求高并发更快适应。  
* 支持Resfult  
    --GET/PUT/POST/DELETE，同时支持请求进度状态监听
* 支持缓存（瞬时缓存时间与二次缓存时间设置）  
    --服务端不支持未设置Cache策略下也支持缓存
* 支持大文件上传  
    --同时上传多文件，文件上传进度等支持
* 支持会话Cookie自动处理  
    --持久化Cookie的支持
* 支持请求响应json自动转实体类对象返回
* 支持请求自动重定向  
    --重定向Cookie的自动处理（HttpUrlConnection内部实现类自动重定向处理不了Cookie）
* 支持请求及响应详细报文日志打印  
    --需要执行adb shell setprop log.tag.VolleyTag命令即可，默认不打印。（发布代码不用管日志的打印）
* 支持请求自动请求加载中提示  
    --软引用...FragmentDialog避免窗口泄露,非Activity中调用中自动关进度加载提示,支持自定义加载中提示样式
* 支持网络请求自动取消  
    --在Activity中的请求，支持在activity关闭退出时网络请求自动cancel取消。


###快速使用简介
```
//初始化AfeitaNet
AfeitaNet afeitaNet =  new AfeitaNet(NetSampleActivity.this);

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

```


*******
更多用法见
Sample 项目中



*******
联系:chenshufei2@sina.com



*******
##License
```
Copyright 2015 chenshufei

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```




