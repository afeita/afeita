
package com.github.afeita.filemanager.mutildownloader.entities;

import java.io.File;
import java.io.Serializable;

/**
 * 下载实体类
 * <br /> author: chenshufei
 * <br /> date: 15/10/4
 * <br /> email: chenshufei2@sina.com
 */
public class DLInfo implements Serializable{
    public File dlLocalFile;
    public String baseUrl, realUrl;

    public DLInfo(File dlLocalFile, String baseUrl, String realUrl) {
        this.dlLocalFile = dlLocalFile;
        this.baseUrl = baseUrl;
        this.realUrl = realUrl;
    }

}
