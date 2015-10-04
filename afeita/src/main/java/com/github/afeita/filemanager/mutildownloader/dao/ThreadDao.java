/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.afeita.filemanager.mutildownloader.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.afeita.filemanager.mutildownloader.cons.PublicCons;
import com.github.afeita.filemanager.mutildownloader.entities.DLInfo;
import com.github.afeita.filemanager.mutildownloader.entities.ThreadInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/10/4
 * <br /> email: chenshufei2@sina.com
 */
public class ThreadDao extends  BaseDao {
    public ThreadDao(Context context) {
        super(context);
    }

    @Override
    public void insertInfo(DLInfo info) {
        ThreadInfo i = (ThreadInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO " + PublicCons.DBCons.TB_THREAD + "(" +
                        PublicCons.DBCons.TB_THREAD_URL_BASE + ", " +
                        PublicCons.DBCons.TB_THREAD_URL_REAL + ", " +
                        PublicCons.DBCons.TB_THREAD_FILE_PATH + ", " +
                        PublicCons.DBCons.TB_THREAD_START + ", " +
                        PublicCons.DBCons.TB_THREAD_END + ", " +
                        PublicCons.DBCons.TB_THREAD_ID + ") VALUES (?,?,?,?,?,?)",
                new Object[]{i.baseUrl, i.realUrl, i.dlLocalFile.getAbsolutePath(), i.start,
                        i.end, i.id});
        db.close();
    }

    @Override
    public void deleteInfo(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + PublicCons.DBCons.TB_THREAD + " WHERE " +
                PublicCons.DBCons.TB_THREAD_ID + "=?", new String[]{id});
        db.close();
    }

    public void deleteInfos(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + PublicCons.DBCons.TB_THREAD + " WHERE " +
                PublicCons.DBCons.TB_THREAD_URL_BASE + "=?", new String[]{url});
        db.close();
    }

    @Override
    public void updateInfo(DLInfo info) {
        ThreadInfo i = (ThreadInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + PublicCons.DBCons.TB_THREAD + " SET " +
                PublicCons.DBCons.TB_THREAD_START + "=? WHERE " +
                PublicCons.DBCons.TB_THREAD_URL_BASE + "=? AND " +
                PublicCons.DBCons.TB_THREAD_ID + "=?", new Object[]{i.start, i.baseUrl, i.id});
        db.close();
    }

    @Override
    public DLInfo queryInfo(String id) {
        ThreadInfo info = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                PublicCons.DBCons.TB_THREAD_URL_BASE + ", " +
                PublicCons.DBCons.TB_THREAD_URL_REAL + ", " +
                PublicCons.DBCons.TB_THREAD_FILE_PATH + ", " +
                PublicCons.DBCons.TB_THREAD_START + ", " +
                PublicCons.DBCons.TB_THREAD_END + " FROM " +
                PublicCons.DBCons.TB_THREAD + " WHERE " +
                PublicCons.DBCons.TB_THREAD_ID + "=?", new String[]{id});
        if (c.moveToFirst()) {
            info = new ThreadInfo(new File(c.getString(2)), c.getString(0), c.getString(1),
                    c.getInt(3), c.getInt(4), id);
        }
        c.close();
        db.close();
        return info;
    }

    public List<ThreadInfo> queryInfos(String url) {
        List<ThreadInfo> infos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                PublicCons.DBCons.TB_THREAD_URL_BASE + ", " +
                PublicCons.DBCons.TB_THREAD_URL_REAL + ", " +
                PublicCons.DBCons.TB_THREAD_FILE_PATH + ", " +
                PublicCons.DBCons.TB_THREAD_START + ", " +
                PublicCons.DBCons.TB_THREAD_END + ", " +
                PublicCons.DBCons.TB_THREAD_ID + " FROM " +
                PublicCons.DBCons.TB_THREAD + " WHERE " +
                PublicCons.DBCons.TB_THREAD_URL_BASE + "=?", new String[]{url});
        while (c.moveToNext()) {
            infos.add(new ThreadInfo(new File(c.getString(2)), c.getString(0),c.getString(1),
                    c.getInt(3),  c.getInt(4), c.getString(5)));
        }
        c.close();
        db.close();
        return infos;
    }
}
