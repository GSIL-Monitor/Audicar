package com.beautyyan.beautyyanapp.utils; /**
 *
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 类名称：DateUtil
 * 作者： wujingdong
 * 创建时间：2011-5-8
 * 类描述：时间工具
 * 版权声明 : Copyright (C) 2008-2010 华为技术有限公司(Huawei Tech.Co.,Ltd)
 * 修改时间：
 *
 */
public final class DateUtil
{
    /**
     * im消息时间开始显示位置
     */
    public static final byte IM_TIME_SHOW_START = 2;

    /**
     * 时间格式(yyyy-MM-dd    HH:mm:ss)
     */
    public static final String FMT_YMDHMS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间格式(yyyy-MM-dd HH:mm)
     */
    public static final String FMT_YMDHM = "yyyy-MM-dd HH:mm";

    /**
     * 时间格式（yy-mm-dd HH:mm）
     */
    public static final String FMT_YMDHM_2 = "yy-MM-dd HH:mm";

    /**
     * 时间格式(yyyy/MM/dd HH:mm)
     */
    public static final String FMT_YMDHM_3 = "yyyy/MM/dd HH:mm";

    /**
     * 时间格式(yyyy-MM-dd)
     */
    public static final String FMT_YMD = "yyyy-MM-dd";

    /**
     * 时间格式(yyyy-MM-dd)
     */
    public static final String FMT_YMD_1 = "MM-dd";

    /**
     * 时间格式（HH:mm:ss）
     */
    public static final String FMT_HMS = "HH:mm:ss";
    /**
     * 时间格式（mm:ss）
     */
    public static final String FMT_MS = "mm:ss";

    /**
     * 时间格式（yyyy/MM/dd）
     */
    public static final String FMT_YMD_2 = "yyyy/MM/dd";

    /**
     * 时间格式（HH:mm）
     */
    public static final String FMT_HM = "HH:mm";

    /**
     * 时间格式（EEE MM/dd/yyyy HH:mm）
     */
    public static final String WEEK_DAY_FORMAT = "EEE MM/dd/yyyy HH:mm";

    /**
     * 时间格式（EEE MM/dd/yyyy）
     */
    public static final String WEEK_DAY_AND_YEAR = "EEE MM/dd/yyyy";

    /**
     * 时间格式（EEE）
     */
    public static final String WEEK_FORMAT = "EEE";

    /**
     * 时间格式（MM:dd @ HH:mm .eSpace）
     */
    public static final String FMT_MDHM_ESPACE = "MM/dd' @ 'HH:mm' .eSpace'";

    /**
     * 年月日时分秒毫秒  - 字符串连接（yyyyMMddHHmmssSSS）
     */
    public static final String FMT_YMDHMSMS_STRING = "yyyyMMddHHmmssSSS";

    /**
     * 时间转换formater
     */
    private static SimpleDateFormat formatter;

    /**
     * 日期
     */
    private static Date date;

    private DateUtil()
    {
        // 私有构造
    }

    /**
     * 格式化毫秒
     * @param timeMillis 毫秒数
     * @param format 格式化时间
     * @return 字符串
     */
    public static String formatMillis(Long timeMillis, String format)
    {
        return new SimpleDateFormat(format).format(new Date(timeMillis));

    }

    /**
     * 获取毫秒时间
     * @param time 时间
     * @param pattern 格式化时间
     * @return long
     */
    public static long getTimeInMillis(String time, String pattern)
    {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date datetime = null;
        try
        {
            datetime = format.parse(time);
        }
        catch (ParseException e)
        {
            LogUtil.e("get time in mills error.");
        }
        if (datetime == null)
        {
            return 0;
        }
        return datetime.getTime();
    }

}
