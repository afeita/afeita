package com.github.afeita.log;

import android.util.Log;

/**
 *  日志输出类，
 *  控制 debug false 全部不显示日志
 *  控制 defaultLogLevel  显示日志的级别
 *  @author afei
 *
 */
public class L {

	private static boolean debug = true;

	private static int defaultLogLevel = Log.VERBOSE;
	
	
	public static boolean canLog(int level){
		return debug&&level>=defaultLogLevel;
	}

	public static void v(String msg) {
		if (canLog(Log.VERBOSE)) {
			Log.v("--->>", msg);
		}
	}

	public static void v(String tag, String msg) {
		if (canLog(Log.VERBOSE)) {
			Log.v(tag, msg);
		}
	}
	
	public static void d(String msg) {
		if (canLog(Log.DEBUG)) {
			Log.d("--->>", msg);
		}
	}

	public static void d(String tag, String msg) {
		if (canLog(Log.DEBUG)) {
			Log.d(tag, msg);
		}
	}
	
	public static void i(String msg) {
		if (canLog(Log.INFO)) {
			Log.d("--->>", msg);
		}
	}

	public static void i(String tag, String msg) {
		if (canLog(Log.INFO)) {
			Log.d(tag, msg);
		}
	}
	
	public static void w(String msg) {
		if (canLog(Log.WARN)) {
			Log.d("--->>", msg);
		}
	}

	public static void w(String tag, String msg) {
		if (canLog(Log.WARN)) {
			Log.d(tag, msg);
		}
	}

	public static void e(String msg) {
		if (canLog(Log.ERROR)) {
			Log.e("--->>", msg);
		}
	}

	public static void e(String tag, String msg) {
		if (canLog(Log.ERROR)) {
			Log.e(tag, msg);
		}
	}
	
	public static void e(String tag, String msg, Throwable thr) { 
		if (canLog(Log.ERROR)) {
			Log.e(tag, msg,thr);
		}
	}
	
	public static void a(String msg) {
		if (canLog(Log.ASSERT)) {
			Log.e("--->>", msg);
		}
	}

	public static void a(String tag, String msg) {
		if (canLog(Log.ASSERT)) {
			Log.e(tag, msg);
		}
	}
}