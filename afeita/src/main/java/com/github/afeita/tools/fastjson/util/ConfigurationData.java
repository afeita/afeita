package com.github.afeita.tools.fastjson.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

/**
 * 通过SharedPreferences配置文件进行数据存取
 * 〈功能详细描述〉
 *
 * @author zhoub
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ConfigurationData {
    /**
     * sharedpreference默认文件名
     */
	private static String SYS_CONF = "Configuration";

	/**
	 * 用户配置
	 */
	public static ConfigurationData instance;

	public String SERVER_IP;
	public String SERVER_PORT;

	private ConfigurationData() {
	}

	/**
	 * 
	 * 单例模式：初始化ConfigurationData对象
	 *
	 * @param sysName  SharedPreferences文件名，如果为空，则使用默认文件名
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static ConfigurationData getInstance() {
		return getInstance("");
	}
	
	   /**
     * 
     * 单例模式：初始化ConfigurationData对象
     *
     * @param sysName  SharedPreferences文件名，如果为空，则使用默认文件名
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static ConfigurationData getInstance(String sysName) {
        if (instance == null) {
            instance = new ConfigurationData();
        }
        if(!TextUtils.isEmpty(sysName)){
            SYS_CONF = sysName;
        }
        return instance;
    }


	public boolean containKey(Context context, String key) {
		SharedPreferences preferences = context.getSharedPreferences(SYS_CONF,
				Context.MODE_PRIVATE);
		return preferences.contains(key);
	}

	public void clear(Context context) {
	}

	public void saveMe(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(SYS_CONF, Context.MODE_PRIVATE);
		Editor preferencesEditor = preferences.edit();

		preferencesEditor.putString("serverip", SERVER_IP);
		preferencesEditor.putString("serverport", SERVER_PORT);

		preferencesEditor.commit();
	}

	public void readMe(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(SYS_CONF, Context.MODE_PRIVATE);

		SERVER_IP = preferences.getString("serverip", "");
		SERVER_PORT = preferences.getString("serverport", "");
	}

	/**
	 * 读取int型数据
	 * @param context 
	 * @param strKey 对应数据键
	 * @param defaultInt 默认值
	 * @return
	 */
	public int readSpDataInt(Context context, String strKey,int defaultInt){
		SharedPreferences preferences = context.getSharedPreferences(SYS_CONF, Context.MODE_PRIVATE);
		return preferences.getInt(strKey, defaultInt);
	}

	/**
	 * 保存int型数据
	 * @param context
	 * @param strKey 对应数据键
	 * @param intValue 默认值
	 */
	public void saveSpDataInt(Context context, String strKey,int intValue){
		
		SharedPreferences preferences = context.getSharedPreferences(SYS_CONF, Context.MODE_PRIVATE);
		Editor preferencesEditor = preferences.edit();
		preferencesEditor.putInt(strKey, intValue);
		preferencesEditor.commit();
		
	}
	
	/**
	 * 读取long型数据
	 * @param context 
	 * @param strKey 对应数据键
	 * @param defaultLong 默认值
	 * @return
	 */
	public long readSpDataLong(Context context, String strKey,long defaultLong){
		SharedPreferences preferences = context.getSharedPreferences(SYS_CONF, Context.MODE_PRIVATE);
		return preferences.getLong(strKey, defaultLong);
	}

	/**
	 * 保存long型数据
	 * @param context
	 * @param strKey 对应数据键
	 * @param intValue 默认值
	 */
	public void saveSpDataLong(Context context, String strKey,long longValue){
		
		SharedPreferences preferences = context.getSharedPreferences(SYS_CONF, Context.MODE_PRIVATE);
		Editor preferencesEditor = preferences.edit();
		preferencesEditor.putLong(strKey, longValue);
		preferencesEditor.commit();
		
	}


	/**
	 * 读取String型数据
	 * @param context 
	 * @param strKey 对应数据键
	 * @param defaultStr 默认值
	 * @return
	 */
	public String readSpDataString(Context context, String strKey,String defaultStr){
		if (null == context) {
			return null;
		}
		SharedPreferences preferences = context.getSharedPreferences(SYS_CONF, Context.MODE_PRIVATE);
		return preferences.getString(strKey,defaultStr);
	}

	/**
	 * 保存String型数据
	 * @param context
	 * @param strKey 对应数据键
	 * @param strValue 保存的数值
	 */
	public void saveSpDataString(Context context, String strKey,String strValue){
		
		SharedPreferences preferences = context.getSharedPreferences(SYS_CONF, Context.MODE_PRIVATE);
		Log.i("saveSpDataString", "context: "+context);
		Editor preferencesEditor = preferences.edit();
		preferencesEditor.putString(strKey, strValue);
		preferencesEditor.commit();
		
	}
	
	/**
	 * 读取boolean型数据
	 * @param context 
	 * @param strKey 对应数据键
	 * @param defaultStr 默认值
	 * @return
	 */
	public boolean readSpDataBoolean(Context context, String strKey,boolean defaultStr){
		SharedPreferences preferences = context.getSharedPreferences(SYS_CONF, Context.MODE_PRIVATE);
		return preferences.getBoolean(strKey,defaultStr);
	}

	/**
	 * 保存boolean型数据
	 * @param context
	 * @param strKey 对应数据键
	 * @param strValue 保存的数值
	 */
	public void saveSpDataBoolean(Context context, String strKey,boolean strValue){
		
		SharedPreferences preferences = context.getSharedPreferences(SYS_CONF, Context.MODE_PRIVATE);
		Editor preferencesEditor = preferences.edit();
		preferencesEditor.putBoolean(strKey, strValue);
		preferencesEditor.commit();
		
	}


}
