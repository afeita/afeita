/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.afeita.tools;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <br /> author: chenshufei
 * <br /> date: 15/9/15
 * <br /> email: chenshufei2@sina.com
 */
public class DateTimeUtil {

    private static String DEFALUT_DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
    private static String DEFALUT_DATEFORMAT = "yyyy-MM-dd";

    /**
     * 以指定的dateTimeFormat 格式化date日期时间
     * @param date
     * @param dateTimeFormat
     * @return
     */
    public static String getDateTimeString(Date date, String dateTimeFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
        return sdf.format(date);
    }

    /**
     * 以yyyy-MM-dd hh:mm:ss 格式化date日期时间
     * @param date
     * @return
     */
    public static String getDateTimeString(Date date){
        return getDateTimeString(date, DEFALUT_DATETIMEFORMAT);
    }

    /**
     * 以指定的dateTimeFormat 解析dateTimeStr日期时间字符串
     * @param dateTimeStr
     * @param dateTimeFormat
     * @return dateTimeFormat格式不规范，将返回null
     */
    public static Date parseDateTime(String dateTimeStr, String dateTimeFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat);
        try {
            return sdf.parse(dateTimeStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 以yyyy-MM-dd hh:mm:ss 解析获取date日期时间
     * @param dateTimeStr
     * @return
     */
    public static Date parseDateTime(String dateTimeStr){
        return parseDateTime(dateTimeStr, DEFALUT_DATETIMEFORMAT);
    }


    public static Date stringToDateTime(String strDate) {
        return parseDateTime(strDate);
    }

    public static String dateTimeToString(Object dateTime) {
        if (dateTime != null) {
            return new SimpleDateFormat(DEFALUT_DATETIMEFORMAT).format(dateTime);
        }
        return null;
    }

    public static String dateTimeToYMD(Object dateTime) {
        String sourceString = null;
        if (dateTime != null) {
            sourceString = new SimpleDateFormat(DEFALUT_DATETIMEFORMAT).format(dateTime);
            sourceString = sourceString.substring(0, 10);
            sourceString = sourceString.replace("-", "");
            return sourceString;
        }
        return null;
    }

    /**
     * 返回yyyy-MM-dd 格式日期
     * @param strDate
     * @return
     */
    public static String getDatetimeStr(String strDate) {
        if (!TextUtils.isEmpty(strDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat(DEFALUT_DATEFORMAT);
            return sdf.format(stringToDateTime(strDate));
        }
        return "";
    }

    /***
     * 将yyyy-MM-dd日期 转成格式MMdd日期
     * @param strDate
     * @return
     */
    public static String getDateNoYear(String strDate) {
        if (!TextUtils.isEmpty(strDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMdd");

            try {
                return sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(strDate));
            } catch (ParseException e) {
            }
        }
        return strDate;
    }

    // 标准时间格式

    /**
     * 返回yyyy年MM月dd日格式日期
     * @param strDate
     * @return
     */
    public static String getChinaDatetimeStr(String strDate) {
        if (!TextUtils.isEmpty(strDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            return sdf.format(stringToDateTime(strDate));
        }
        return "";
    }

    // 根据分钟数显示时长
    /**
     * 返回分钟数，如如果超过1小时就是 x小时 x分 不超过一小时就是 xx分钟
     * @param mins
     * @return
     */
    public static String getTimeStrFormSMins(Double mins) {//
        String sourceString = (int) (mins % 60) + "分钟";
        if (mins >= 60) {
            sourceString = (int) (mins % 60) + "分";
            String hourString = (int) (mins / 60) + "小时";
            return hourString + sourceString;
        }
        return sourceString;
    }

    public static String getCurrentTimeWithoutYear() {
        return new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date());
    }

    public static String formatDateTime(long millionseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFALUT_DATETIMEFORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millionseconds);
        return sdf.format(calendar.getTime());
    }

    public static String formatDateTimeWithoutYear(long millionseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millionseconds);
        return sdf.format(calendar.getTime());
    }

}
