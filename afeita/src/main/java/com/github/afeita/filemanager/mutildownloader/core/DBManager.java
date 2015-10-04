
package com.github.afeita.filemanager.mutildownloader.core;

import android.content.Context;

import com.github.afeita.filemanager.mutildownloader.dao.TaskDao;
import com.github.afeita.filemanager.mutildownloader.dao.ThreadDao;
import com.github.afeita.filemanager.mutildownloader.entities.TaskInfo;
import com.github.afeita.filemanager.mutildownloader.entities.ThreadInfo;

import java.util.List;

/**
 * 数据库管理器
 * 封装各种业务数据操作
 * <br /> author: chenshufei
 * <br /> date: 15/10/4
 * <br /> email: chenshufei2@sina.com
 */
public class DBManager {
    private static DBManager sManager = null;

    private TaskDao daoTask;
    private ThreadDao daoThread;

    private DBManager(Context context) {
        daoTask = new TaskDao(context);
        daoThread = new ThreadDao(context);
    }

    /**
     * 获取数据库管理器单例对象
     *
     * @param context ...
     * @return 数据库管理器单例对象
     */
    public static DBManager getInstance(Context context) {
        if (null == sManager) {
            sManager = new DBManager(context);
        }
        return sManager;
    }

    /**
     * 插入一条下载任务数据信息
     *
     * @param info 下载任务对象
     */
    public synchronized void insertTaskInfo(TaskInfo info) {
        daoTask.insertInfo(info);
    }

    /**
     * 根据下载地址删除一条下载任务数据信息
     *
     * @param url 下载地址
     */
    public synchronized void deleteTaskInfo(String url) {
        daoTask.deleteInfo(url);
    }

    /**
     * 更新一条下载任务数据信息
     *
     * @param info 下载任务对象
     */
    public synchronized void updateTaskInfo(TaskInfo info) {
        daoTask.updateInfo(info);
    }

    /**
     * 根据下载地址查询一条下载任务数据信息
     *
     * @param url 下载地址
     * @return 下载任务对象
     */
    public synchronized TaskInfo queryTaskInfoByUrl(String url) {
        return (TaskInfo) daoTask.queryInfo(url);
    }

    /**
     * 插入一条线程数据信息
     *
     * @param info 线程对象
     */
    public synchronized void insertThreadInfo(ThreadInfo info) {
        daoThread.insertInfo(info);
    }

    /**
     * 根据线程ID删除一条线程数据信息
     *
     * @param id 线程ID
     */
    public synchronized void deleteThreadInfoById(String id) {
        daoThread.deleteInfo(id);
    }

    /**
     * 根据下载地址删除所有线程数据信息
     *
     * @param url 下载地址
     */
    public synchronized void deleteThreadInfos(String url) {
        daoThread.deleteInfo(url);
    }

    /**
     * 更新一条线程数据信息
     *
     * @param info 线程对象
     */
    public synchronized void updateThreadInfo(ThreadInfo info) {
        daoThread.updateInfo(info);
    }

    /**
     * 根据线程ID查询一条线程数据信息
     *
     * @param id 线程ID
     * @return 线程对象
     */
    public synchronized ThreadInfo queryThreadInfoById(String id) {
        return (ThreadInfo) daoThread.queryInfo(id);
    }

    /**
     * 根据下载地址查询所有线程数据信息
     *
     * @param url 下载地址
     * @return 所有该地址下对应的线程信息
     */
    public synchronized List<ThreadInfo> queryThreadInfos(String url) {
        return daoThread.queryInfos(url);
    }

    /**
     * 释放资源 暂无用
     */
    public void release() {
        daoTask.close();
    }
}
