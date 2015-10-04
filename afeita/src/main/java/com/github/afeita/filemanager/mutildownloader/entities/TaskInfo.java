
package com.github.afeita.filemanager.mutildownloader.entities;

import java.io.File;

/**
 * 任务
 * <br /> author: chenshufei
 * <br /> date: 15/10/4
 * <br /> email: chenshufei2@sina.com
 */
public class TaskInfo extends DLInfo {
    public int progress, length;

    public TaskInfo(File dlLocalFile, String baseUrl, String realUrl, int progress, int length) {
        super(dlLocalFile, baseUrl, realUrl);
        this.progress = progress;
        this.length = length;
    }
}
