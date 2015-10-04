/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.afeita.filemanager.mutildownloader.utils;

import java.io.File;
import java.io.IOException;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/10/4
 * <br /> email: chenshufei2@sina.com
 */
public class FileUtil {

    /**
     * 根据URL路径获取文件名
     *
     * @param url URL路径
     * @return 文件名
     */
    public static String getFileNameFromUrl(String url) {
        int start = url.lastIndexOf("/");
        return start==-1? url:url.substring(start+1);
    }

    /**
     * 创建文件夹
     *
     * @param path 文件夹路径
     * @return 创建了的文件夹File对象
     */
    public static File makeDir(String path) {
        File dir = new File(path);
        if (!isExist(dir)) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 创建文件
     *
     * @param path     文件路径
     * @param fileName 文件名
     * @return 文件File对象
     */
    public static File createFile(String path, String fileName) {
        File file = new File(makeDir(path), fileName);
        if (!isExist(file)) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 判断File对象所指的目录或文件是否存在
     *
     * @param file File对象
     * @return true表示存在 false反之
     */
    public static boolean isExist(File file) {
        return file.exists();
    }
}
