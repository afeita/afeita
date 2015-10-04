
package com.github.afeita.filemanager.mutildownloader.cons;

import android.provider.BaseColumns;

/**
 * 公共常量
 * <br /> author: chenshufei
 * <br /> date: 15/10/4
 * <br /> email: chenshufei2@sina.com
 */
public class PublicCons {
    /**
     * 文件访问模式
     */
    public static interface AccessModes {
        public static final String ACCESS_MODE_R = "r";
        public static final String ACCESS_MODE_RW = "rw";
        public static final String ACCESS_MODE_RWS = "rws";
        public static final String ACCESS_MODE_RWD = "rwd";
    }

    /**
     * 数据库常量
     */
    public static interface DBCons {
        public static final String TB_TASK = "task_info";
        public static final String TB_TASK_URL_BASE = "base_url";
        public static final String TB_TASK_URL_REAL = "real_url";
        public static final String TB_TASK_FILE_PATH = "file_path";
        public static final String TB_TASK_PROGRESS = "onThreadProgress";
        public static final String TB_TASK_FILE_LENGTH = "file_length";

        public static final String TB_THREAD = "thread_info";
        public static final String TB_THREAD_URL_BASE = "base_url";
        public static final String TB_THREAD_URL_REAL = "real_url";
        public static final String TB_THREAD_FILE_PATH = "file_path";
        public static final String TB_THREAD_START = "start";
        public static final String TB_THREAD_END = "end";
        public static final String TB_THREAD_ID = "id";

        public static final String TB_TASK_SQL_CREATE = "CREATE TABLE " +
                PublicCons.DBCons.TB_TASK + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PublicCons.DBCons.TB_TASK_URL_BASE + " CHAR, " +
                PublicCons.DBCons.TB_TASK_URL_REAL + " CHAR, " +
                PublicCons.DBCons.TB_TASK_FILE_PATH + " CHAR, " +
                PublicCons.DBCons.TB_TASK_PROGRESS + " INTEGER, " +
                PublicCons.DBCons.TB_TASK_FILE_LENGTH + " INTEGER)";
        public static final String TB_THREAD_SQL_CREATE = "CREATE TABLE " +
                PublicCons.DBCons.TB_THREAD + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PublicCons.DBCons.TB_THREAD_URL_BASE + " CHAR, " +
                PublicCons.DBCons.TB_THREAD_URL_REAL + " CHAR, " +
                PublicCons.DBCons.TB_THREAD_FILE_PATH + " CHAR, " +
                PublicCons.DBCons.TB_THREAD_START + " INTEGER, " +
                PublicCons.DBCons.TB_THREAD_END + " INTEGER, " +
                PublicCons.DBCons.TB_THREAD_ID + " CHAR)";

        public static final String TB_TASK_SQL_UPGRADE = "DROP TABLE IF EXISTS " +
                PublicCons.DBCons.TB_TASK;
        public static final String TB_THREAD_SQL_UPGRADE = "DROP TABLE IF EXISTS " +
                PublicCons.DBCons.TB_THREAD;
    }

    /**
     * 网络类型
     */
    public static interface NetType {
        public static final int INVALID = 0;
        public static final int WAP = 1;
        public static final int G2 = 2;
        public static final int G3 = 3;
        public static final int WIFI = 4;
        public static final int NO_WIFI = 5;
    }
}
