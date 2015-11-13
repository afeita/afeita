
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
    public interface AccessModes {
        String ACCESS_MODE_R = "r";
        String ACCESS_MODE_RW = "rw";
        String ACCESS_MODE_RWS = "rws";
        String ACCESS_MODE_RWD = "rwd";
    }

    /**
     * 数据库常量
     */
    public interface DBCons {
        String TB_TASK = "task_info";
        String TB_TASK_URL_BASE = "base_url";
        String TB_TASK_URL_REAL = "real_url";
        String TB_TASK_FILE_PATH = "file_path";
        String TB_TASK_PROGRESS = "onThreadProgress";
        String TB_TASK_FILE_LENGTH = "file_length";

        String TB_THREAD = "thread_info";
        String TB_THREAD_URL_BASE = "base_url";
        String TB_THREAD_URL_REAL = "real_url";
        String TB_THREAD_FILE_PATH = "file_path";
        String TB_THREAD_START = "start";
        String TB_THREAD_END = "end";
        String TB_THREAD_ID = "id";

        String TB_TASK_SQL_CREATE = "CREATE TABLE " +
                PublicCons.DBCons.TB_TASK + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PublicCons.DBCons.TB_TASK_URL_BASE + " CHAR, " +
                PublicCons.DBCons.TB_TASK_URL_REAL + " CHAR, " +
                PublicCons.DBCons.TB_TASK_FILE_PATH + " CHAR, " +
                PublicCons.DBCons.TB_TASK_PROGRESS + " INTEGER, " +
                PublicCons.DBCons.TB_TASK_FILE_LENGTH + " INTEGER)";
        String TB_THREAD_SQL_CREATE = "CREATE TABLE " +
                PublicCons.DBCons.TB_THREAD + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PublicCons.DBCons.TB_THREAD_URL_BASE + " CHAR, " +
                PublicCons.DBCons.TB_THREAD_URL_REAL + " CHAR, " +
                PublicCons.DBCons.TB_THREAD_FILE_PATH + " CHAR, " +
                PublicCons.DBCons.TB_THREAD_START + " INTEGER, " +
                PublicCons.DBCons.TB_THREAD_END + " INTEGER, " +
                PublicCons.DBCons.TB_THREAD_ID + " CHAR)";

        String TB_TASK_SQL_UPGRADE = "DROP TABLE IF EXISTS " +
                PublicCons.DBCons.TB_TASK;
        String TB_THREAD_SQL_UPGRADE = "DROP TABLE IF EXISTS " +
                PublicCons.DBCons.TB_THREAD;
    }

    /**
     * 网络类型
     */
    public interface NetType {
        int INVALID = 0;
        int WAP = 1;
        int G2 = 2;
        int G3 = 3;
        int WIFI = 4;
        int NO_WIFI = 5;
    }
}
