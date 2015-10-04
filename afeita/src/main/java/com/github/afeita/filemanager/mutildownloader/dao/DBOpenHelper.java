
package com.github.afeita.filemanager.mutildownloader.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.afeita.filemanager.mutildownloader.cons.PublicCons;

/**
 * 数据库
 * <br /> author: chenshufei
 * <br /> date: 15/10/4
 * <br /> email: chenshufei2@sina.com
 */
public class DBOpenHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "afeita_downloader.db";
    private static final int DB_VERSION = 1;

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PublicCons.DBCons.TB_TASK_SQL_CREATE);
        db.execSQL(PublicCons.DBCons.TB_THREAD_SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PublicCons.DBCons.TB_TASK_SQL_UPGRADE);
        db.execSQL(PublicCons.DBCons.TB_THREAD_SQL_UPGRADE);
        onCreate(db);
    }

}
