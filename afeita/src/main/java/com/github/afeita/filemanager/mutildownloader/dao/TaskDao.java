
package com.github.afeita.filemanager.mutildownloader.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.afeita.filemanager.mutildownloader.cons.PublicCons;
import com.github.afeita.filemanager.mutildownloader.entities.DLInfo;
import com.github.afeita.filemanager.mutildownloader.entities.TaskInfo;

import java.io.File;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/10/4
 * <br /> email: chenshufei2@sina.com
 */
public class TaskDao extends BaseDao {
    public TaskDao(Context context) {
        super(context);
    }

    @Override
    public void insertInfo(DLInfo info) {
        TaskInfo i = (TaskInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO " + PublicCons.DBCons.TB_TASK + "(" +
                        PublicCons.DBCons.TB_TASK_URL_BASE + ", " +
                        PublicCons.DBCons.TB_TASK_URL_REAL + ", " +
                        PublicCons.DBCons.TB_TASK_FILE_PATH + ", " +
                        PublicCons.DBCons.TB_TASK_PROGRESS + ", " +
                        PublicCons.DBCons.TB_TASK_FILE_LENGTH + ") values (?,?,?,?,?)",
                new Object[]{i.baseUrl, i.realUrl, i.dlLocalFile.getAbsolutePath(), i.progress,
                        i.length});
        db.close();
    }

    @Override
    public void deleteInfo(String url) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + PublicCons.DBCons.TB_TASK + " WHERE " +
                PublicCons.DBCons.TB_TASK_URL_BASE + "=?", new String[]{url});
        db.close();
    }

    @Override
    public void updateInfo(DLInfo info) {
        TaskInfo i = (TaskInfo) info;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + PublicCons.DBCons.TB_TASK + " SET " +
                PublicCons.DBCons.TB_TASK_PROGRESS + "=? WHERE " +
                PublicCons.DBCons.TB_TASK_URL_BASE + "=?", new Object[]{i.progress, i.baseUrl});
        db.close();
    }

    @Override
    public DLInfo queryInfo(String url) {
        TaskInfo info = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " +
                PublicCons.DBCons.TB_TASK_URL_BASE + ", " +
                PublicCons.DBCons.TB_TASK_URL_REAL + ", " +
                PublicCons.DBCons.TB_TASK_FILE_PATH + ", " +
                PublicCons.DBCons.TB_TASK_PROGRESS + ", " +
                PublicCons.DBCons.TB_TASK_FILE_LENGTH + " FROM " +
                PublicCons.DBCons.TB_TASK + " WHERE " +
                PublicCons.DBCons.TB_TASK_URL_BASE + "=?", new String[]{url});
        if (c.moveToFirst()) {
            info = new TaskInfo(new File(c.getString(2)), c.getString(0),c.getString(1),
                    c.getInt(3), c.getInt(4));
        }
        c.close();
        db.close();
        return info;
    }
}
