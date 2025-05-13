package com.dzkj.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/3/5
 * @description 日期工具类
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public class DateUtil {
	/** 日期格式(yyyy-MM-dd) */
	public static final String yyyy_MM_dd_EN = "yyyy-MM-dd";
	/** 日期格式(yyyy/MM/dd) */
	public static final String yyyy_MM_dd_decline = "yyyy/MM/dd";
	/** 日期格式(yyyy/MM/dd HH:mm:ss) */
	public static final String yyyy_MM_dd_HH_mm_ss = "yyyy/MM/dd HH:mm:ss";
	/** 日期格式(yyyyMMdd) */
	public static final String yyyyMMdd_EN = "yyyyMMdd";
	/** 日期格式(yyyy-MM) */
	public static final String yyyy_MM_EN = "yyyy-MM";
	/** 日期格式(yyyyMM) */
	public static final String yyyyMM_EN = "yyyyMM";
	/** 日期格式(yyyy-MM-dd HH:mm:ss) */
	public static final String yyyy_MM_dd_HH_mm_ss_EN = "yyyy-MM-dd HH:mm:ss";
	/** 日期格式(yyyy-MM-dd HH:mm:ss.S) */
	public static final String yyyy_MM_dd_HH_mm_ss_S_EN = "yyyy-MM-dd HH:mm:ss.S";
	/** 日期格式(yyyyMMddHHmmss) */
	public static final String yyyyMMddHHmmss_EN = "yyyyMMddHHmmss";
	/** 日期格式(yyyy年MM月dd日) */
	public static final String yyyy_MM_dd_CN = "yyyy年MM月dd日";
	/** 日期格式(yyyy年MM月dd日HH时mm分ss秒) */
	public static final String yyyy_MM_dd_HH_mm_ss_CN = "yyyy年MM月dd日HH时mm分ss秒";
	/** 日期格式(yyyy年MM月dd日HH时mm分) */
	public static final String yyyy_MM_dd_HH_mm_CN = "yyyy年MM月dd日HH时mm分";
	/** 北京boss订购接口报文头日期格式 */
	public static final String BJBOSS_DATE = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	/** 日期格式(HH:mm:ss) */
	public static final String HH_mm_ss_EN = "HH:mm:ss";
	/** 日期格式(HH:mm) */
	public static String HH_mm_EN ="HH:mm";
	/** DateFormat缓存 */
	private static final Map<String, DateFormat> dateFormatMap = new HashMap<String, DateFormat>();

	public static Date getDate() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * 按照默认formatStr的格式，转化dateTimeStr为Date类型 dateTimeStr必须是formatStr的形式
	 * @param dateTimeStr
	 * @param formatStr
	 * @return
	 */
	public static Date getDate(String dateTimeStr, String formatStr) {
		try {
			if (dateTimeStr == null || "".equals(dateTimeStr)) {
				return null;
			}
			DateFormat sdf = new SimpleDateFormat(formatStr);
			return sdf.parse(dateTimeStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 按照默认显示日期时间的格式"yyyy-MM-dd"，转化dateTimeStr为Date类型
	 * dateTimeStr必须是"yyyy-MM-dd"的形式
	 * @param dateTimeStr
	 * @return
	 */
	public static Date getDate(String dateTimeStr) {
		return getDate(dateTimeStr, yyyy_MM_dd_EN);
	}


	/**
	 * 将Date转换成字符串“yyyy-mm-dd hh:mm:ss”的字符串
	 * @param date
	 * @return
	 */
	public static String dateToDateString(Date date) {
		return dateToDateString(date, yyyy_MM_dd_HH_mm_ss_EN);
	}

	/**
	 * 将Date转换成formatStr格式的字符串
	 * @param date
	 * @param formatStr
	 * @return
	 */
	public static String dateToDateString(Date date, String formatStr) {
		DateFormat df = new SimpleDateFormat(formatStr);
		return df.format(date);
	}

	/**
	 * 比较任意格式时间相差毫秒数
	 * @param time1
	 * @param time2
	 * @param format
	 * @return
	 */
	public static long compareDateStr(String time1, String time2, String format){
		Date d1 = getDate(time1, format);
		Date d2 = getDate(time2, format);
		return d2.getTime() - d1.getTime();
	}

	public static Date getDate(Date beginDate,int ds){
		if(ds == 0){
			return new Date();
		}
		try {
			SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
			Calendar date = Calendar.getInstance();
			date.setTime(beginDate);
			date.set(Calendar.DATE, date.get(Calendar.DATE) - ds);
			Date endDate = dft.parse(dft.format(date.getTime()));
			return endDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	/**
	 * 获取指定日期monthNum月前的一个日期
	 *
	 * @description
	 * @author jing.fang
	 * @date 2022/7/7 9:44
	 * @param date date
	 * @param monthNum monthNum
	 * @return java.util.Date
	**/
	public static Date getDateOfMonth(Date date, int monthNum) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(date);
		now.add(Calendar.MONTH, monthNum);
		return now.getTime();
	}

	/**
	 * 获取指定日期dayNum日前的一个日期
	 *
	 * @description
	 * @author jing.fang
	 * @date 2022/7/19 11:10
	 * @param date date
	 * @param dayNum dayNum
	 * @return java.util.Date
	**/
	public static Date getDateOfDay(Date date, int dayNum) {
		Calendar now = Calendar.getInstance(TimeZone.getDefault());
		now.setTime(date);
		now.add(Calendar.DATE, dayNum);
		return now.getTime();
	}

}
