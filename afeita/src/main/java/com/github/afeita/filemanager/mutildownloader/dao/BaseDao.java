
package com.github.afeita.filemanager.mutildownloader.dao;

import android.content.Context;

import com.github.afeita.filemanager.mutildownloader.entities.DLInfo;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/10/4
 * <br /> email: chenshufei2@sina.com
 */
public abstract class BaseDao {
    protected DBOpenHelper dbHelper;

    public BaseDao(Context context) {
        dbHelper = new DBOpenHelper(context);
    }

    public abstract void insertInfo(DLInfo info);

    public abstract void deleteInfo(String url);

    public abstract void updateInfo(DLInfo info);

    public abstract DLInfo queryInfo(String str);

    public void close() {
        dbHelper.close();
    }
}
