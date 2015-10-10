
package com.github.afeita.log;

import android.util.Log;

/**
 * Log输出工具类
 * <br /> author: chenshufei
 * <br /> date: 15/10/4
 * <br /> email: chenshufei2@sina.com
 */
public class ThreadLogUtil {

    public final static String TAG = "Afeita_DL";
    public final static String MATCH = "%s->%s->%d";
    public final static String CONNECTOR = ":<--->:";

    public final static boolean SWITCH = LogSwitch.OPEN_LOG;

    public static String buildHeader() {
        StackTraceElement stack = Thread.currentThread().getStackTrace()[4];
        return String.format(MATCH, stack.getClassName(), stack.getMethodName(),
                stack.getLineNumber()) + CONNECTOR;
    }

    public static void v(Object msg) {
        if (SWITCH) {
            Log.v(TAG, buildHeader() + msg.toString());
        }
    }

    public static void d(Object msg) {
        if (SWITCH) {
            Log.d(TAG, buildHeader() + msg.toString());
        }
    }

    public static void i(Object msg) {
        if (SWITCH) {
            Log.i(TAG, buildHeader() + msg.toString());
        }
    }

    public static void w(Object msg) {
        if (SWITCH) {
            Log.w(TAG, buildHeader() + msg.toString());
        }
    }

    public static void e(Object msg) {
        if (SWITCH) {
            Log.e(TAG, buildHeader() + msg.toString());
        }
    }

}
