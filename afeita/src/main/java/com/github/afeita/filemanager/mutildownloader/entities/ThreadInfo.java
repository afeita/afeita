
package com.github.afeita.filemanager.mutildownloader.entities;

import java.io.File;

/**
 * 线程
 * <br /> author: chenshufei
 * <br /> date: 15/10/4
 * <br /> email: chenshufei2@sina.com
 */
public class ThreadInfo extends DLInfo {
    public String id;
    public int start, end;

    public ThreadInfo(File dlLocalFile, String baseUrl, String realUrl, int start, int end, String id) {
        super(dlLocalFile, baseUrl, realUrl);
        this.start = start;
        this.end = end;
        this.id = id;
    }
}
