package org.trc.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 严格的日期转换setLenient(false);
 * setLenient
 * public void setLenient(boolean lenient)指定日期/时间解析是否不严格。进行不严格解析时，解析程序可以使用启发式的方法来解释与此对象的格式不精确匹配的输入。进行严格解析时，输入必须匹配此对象的格式。 
 * 参数：
 * lenient - 为 true 时，解析过程是不严格的
 * 不会自动将错误日期转换为正确的日期
 * 例如:19450000,使用原DateUtil会转换为19441130
 * @author liuzh
 */
public class DateUtils {

	private final static Logger log = LoggerFactory.getLogger(DateUtils.class);
	
	public static final String COMPACT_DATE_FORMAT = "yyyyMMdd";
	public static final String YM = "yyyyMM";
	public static final String NORMAL_DATE_FORMAT = "yyyy-MM-dd";
	public static final String NORMAL_DATE_FORMAT_NEW = "yyyy-mm-dd hh24:mi:ss";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_ALL = "yyyyMMddHHmmssS";
	public static final String DATE_UNION_QUOT = "-";

	public static Long strDateToNum(String paramString) {
		if (paramString == null)
			return null;
		String[] arrayOfString = null;
		String str = "";
		if (paramString.indexOf("-") >= 0) {
			arrayOfString = paramString.split("-");
			for (int i = 0; i < arrayOfString.length; ++i)
				str = str + arrayOfString[i];
			return Long.valueOf(Long.parseLong(str));
		}
		return Long.valueOf(Long.parseLong(paramString));
	}

	public static Long strDateToNum1(String paramString){
		if (paramString == null)
			return null;
		String[] arrayOfString = null;
		String str = "";
		if (paramString.indexOf("-") >= 0) {
			arrayOfString = paramString.split("-");
			for (int i = 0; i < arrayOfString.length; ++i)
				if (arrayOfString[i].length() == 1)
					str = str + "0" + arrayOfString[i];
				else
					str = str + arrayOfString[i];
			return Long.valueOf(Long.parseLong(str));
		}
		return Long.valueOf(Long.parseLong(paramString));
	}

	public static String numDateToStr(Long paramLong) {
		if (paramLong == null)
			return null;
		String str = null;
		str = paramLong.toString().substring(0, 4) + "-"
				+ paramLong.toString().substring(4, 6) + "-"
				+ paramLong.toString().substring(6, 8);
		return str;
	}

	public static Long sysDateToNum() {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		localSimpleDateFormat.setLenient(false);
		return strDateToNum(localSimpleDateFormat.format(new Date()));
	}

	public static Date stringToDate(String paramString1,
			String paramString2) {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				paramString2);
		localSimpleDateFormat.setLenient(false);
		try {
			return localSimpleDateFormat.parse(paramString1);
		} catch (ParseException localParseException) {
			log.error("解析日期字符串时出错！");
		}
		return null;
	}

	public static String dateToString(Date paramDate,
			String paramString) {
		if(null == paramDate){
			return "";
		}
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				paramString);
		localSimpleDateFormat.setLenient(false);
		String dateStr = "";
		try {
			dateStr = localSimpleDateFormat.format(paramDate);
		} catch (Exception e) {
			log.error("日期格式化异常", e);
		}
		return dateStr;
	}

	public static Date compactStringToDate(String paramString) {
		return stringToDate(paramString, COMPACT_DATE_FORMAT);
	}

	public static String dateToCompactString(Date paramDate) {
		return dateToString(paramDate, COMPACT_DATE_FORMAT);
	}

	public static String dateToNormalString(Date paramDate) {
		return dateToString(paramDate, NORMAL_DATE_FORMAT);
	}

	public static String dateToNormalFullString(Date paramDate) {
		return dateToString(paramDate, DATETIME_FORMAT);
	}

	public static String compactStringDateToNormal(String paramString)
			throws Exception {
		return dateToNormalString(compactStringToDate(paramString));
	}

	public static int getDaysBetween(Date paramDate1,
			Date paramDate2){
		Calendar localCalendar1 = Calendar.getInstance();
		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar1.setTime(paramDate1);
		localCalendar2.setTime(paramDate2);
		if (localCalendar1.after(localCalendar2))
			log.error("起始日期小于终止日期!");
		int i = localCalendar2.get(6) - localCalendar1.get(6);
		int j = localCalendar2.get(1);
		if (localCalendar1.get(1) != j) {
			localCalendar1 = (Calendar) localCalendar1.clone();
			do {
				i += localCalendar1.getActualMaximum(6);
				localCalendar1.add(1, 1);
			} while (localCalendar1.get(1) != j);
		}
		return i;
	}

	public static Date addDays(Date paramDate, int paramInt){
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		int i = localCalendar.get(6);
		localCalendar.set(6, i + paramInt);
		return localCalendar.getTime();
	}

	public static Date addDays(String paramString1,
			String paramString2, int paramInt){
		Calendar localCalendar = Calendar.getInstance();
		Date localDate = stringToDate(paramString1, paramString2);
		localCalendar.setTime(localDate);
		int i = localCalendar.get(6);
		localCalendar.set(6, i + paramInt);
		return localCalendar.getTime();
	}

	public static java.sql.Date getSqlDate(Date paramDate) {
		java.sql.Date localDate = new java.sql.Date(paramDate.getTime());
		return localDate;
	}

	public static String formatDate(Date paramDate) {
		if (paramDate == null)
			return null;
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		localSimpleDateFormat.setLenient(false);
		return localSimpleDateFormat.format(paramDate);
	}

	public static String formatDateTime(Date paramDate) {
		if (paramDate == null)
			return null;
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		localSimpleDateFormat.setLenient(false);
		return localSimpleDateFormat.format(paramDate);
	}

	/**
	 *
	* @Title: parseDate
	* @Description: 字符串转日期
	* @param @param paramString yyyy-MM-dd格式
	* @param @return
	* @param @throws Exception
	* @return java.util.Date
	* @throws
	 */
	public static Date parseDate(String paramString) {
		if ((paramString == null) || (paramString.trim().equals("")))
			return null;
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		localSimpleDateFormat.setLenient(true);
		try {
			return localSimpleDateFormat.parse(paramString);
		} catch (ParseException localParseException) {
			log.error(CommonUtil.joinStr("日期转换出错，异常信息：",localParseException.getMessage()).toString());
		}
		return null;
	}

	/**
	 *
	* @Title: parseDate2
	* @Description: 字符串转日期
	* @param @param paramString  yyyyMMdd格式
	* @param @return
	* @param @throws Exception
	* @return java.util.Date
	* @throws
	 */
	public static Date parseDate2(String paramString){
		if ((paramString == null) || (paramString.trim().equals("")))
			return null;
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		localSimpleDateFormat.setLenient(true);
		try {
			return localSimpleDateFormat.parse(converDateStr(paramString));
		} catch (ParseException localParseException) {
			log.error(CommonUtil.joinStr("日期转换出错，异常信息：",localParseException.getMessage()).toString());
		}
		return null;
	}

	public static Date parseDateTime(String paramString) {
		if ((paramString == null) || (paramString.trim().equals("")))
			return null;
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		localSimpleDateFormat.setLenient(false);
		try {
			return localSimpleDateFormat.parse(paramString);
		} catch (ParseException localParseException) {
			log.error(CommonUtil.joinStr("日期转换出错，异常信息：",localParseException.getMessage()).toString());
		}
		return null;
	}

	public static Integer getYM(String paramString) {
		if (paramString == null)
			return null;
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		localSimpleDateFormat.setLenient(false);
		try {
			return getYM(localSimpleDateFormat.parse(paramString));
		} catch (ParseException localParseException) {
			log.error(CommonUtil.joinStr("日期转换出错，异常信息：",localParseException.getMessage()).toString());
		}
		return null;
	}

	public static Integer getYM(Date paramDate) {
		if (paramDate == null)
			return null;
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		int i = localCalendar.get(1);
		int j = localCalendar.get(2) + 1;
		return new Integer(i * 100 + j);
	}

	public static int addMonths(int paramInt1, int paramInt2) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.set(1, paramInt1 / 100);
		localCalendar.set(2, paramInt1 % 100 - 1);
		localCalendar.set(5, 1);
		localCalendar.add(2, paramInt2);
		return getYM(localCalendar.getTime()).intValue();
	}

	public static Date addMonths(Date paramDate,
			int paramInt) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		localCalendar.add(2, paramInt);
		return localCalendar.getTime();
	}

	public static int monthsBetween(int paramInt1, int paramInt2) {
		int i = paramInt2 / 100 * 12 + paramInt2 % 100
				- (paramInt1 / 100 * 12 + paramInt1 % 100);
		return i;
	}

	public static int monthsBetween(Date paramDate1,
			Date paramDate2) {
		return monthsBetween(getYM(paramDate1).intValue(), getYM(paramDate2).intValue());
	}

	public static String getChineseDate(Calendar paramCalendar) {
		int i = paramCalendar.get(1);
		int j = paramCalendar.get(2);
		int k = paramCalendar.get(5);
		StringBuffer localStringBuffer = new StringBuffer();
		localStringBuffer.append(i);
		localStringBuffer.append("年");
		localStringBuffer.append(j + 1);
		localStringBuffer.append("月");
		localStringBuffer.append(k);
		localStringBuffer.append("日");
		return localStringBuffer.toString();
	}

	public static String getChineseWeekday(Calendar paramCalendar) {
		switch (paramCalendar.get(7)) {
		case 2:
			return "星期一";
		case 3:
			return "星期二";
		case 4:
			return "星期三";
		case 5:
			return "星期四";
		case 6:
			return "星期五";
		case 7:
			return "星期六";
		case 1:
			return "星期日";
		}
		return "未知";
	}
	
	/**
	 * 
	* @Title: converDateStr 
	* @Description: 日期格式字符串转换
	* @param @param dateStr yyyyMMdd格式，如20100908
	* @param @return    yyyy-MM-dd格式，如2010-09-08
	* @return String
	* @throws
	 */
	public static String converDateStr(String dateStr){
		if(StringUtils.isEmpty(dateStr)){
			return null;
		}
		if(dateStr.indexOf("-") > 0 || dateStr.indexOf("/") > 0){
			return dateStr;
		}
		String year = "";//获取年
		String month = "";//获取月
		String day = "";//获取日
		String hour = "";//获取时
		String minite = "";//获取分
		String secord = "";//获取秒
		if(dateStr.length() >= 8 && dateStr.length() < 14){
			year = dateStr.substring(0,4);
			month = dateStr.substring(4,6);
			day = dateStr.substring(6,8);
			return year+DATE_UNION_QUOT+month+DATE_UNION_QUOT+day;
		}else if(dateStr.length() >= 14){
			year = dateStr.substring(0,4);
			month = dateStr.substring(4,6);
			day = dateStr.substring(6,8);
			hour = dateStr.substring(8,10);
			minite = dateStr.substring(10,12);
			secord = dateStr.substring(12,14);
			return year+DATE_UNION_QUOT+month+DATE_UNION_QUOT+day+" "+hour+":"+minite+":"+secord;
		}
		return null;
	}
	
	/**
	 * 日期格式字符串转换成指定格式字符串
	 * @param dateStr 格式化日期字符串或者日期数字拼接起来的数字字符串串
	 * @param dateFormate 日期格式，如：yyyy-MM-dd
	 * @return 
	 */
	public static String converDateStrToFormateStr(String dateStr, String dateFormate){
		if(StringUtils.isEmpty(dateStr)){
			return null;
		}
		/*if(dateStr.indexOf("-") > 0 || dateStr.indexOf("/") > 0){
			return dateStr;
		}*/
		dateStr = dateStr.replaceAll("-", "").replaceAll("/", "").replaceAll(":", "").replaceAll(" ", "");
		String year = "";//获取年
		String month = "";//获取月
		String day = "";//获取日
		String hour = "";//获取时
		String minite = "";//获取分
		String secord = "";//获取秒
		if(dateStr.length() >= 8 && dateStr.length() < 14){
			year = dateStr.substring(0,4);
			month = dateStr.substring(4,6);
			day = dateStr.substring(6,8);
			if(StringUtils.equals(dateFormate, NORMAL_DATE_FORMAT)){
				return year+DATE_UNION_QUOT+month+DATE_UNION_QUOT+day;
			}else if(StringUtils.equals(dateFormate, COMPACT_DATE_FORMAT)){
				return year+month+day;
			}else if(StringUtils.equals(dateFormate, DATETIME_FORMAT)){
				return year+DATE_UNION_QUOT+month+DATE_UNION_QUOT+day +"00:00:00";
			}else if(StringUtils.equals(dateFormate, DATE_ALL)){
				return year+month+day +"000000";
			}
		}else if(dateStr.length() >= 14){
			year = dateStr.substring(0,4);
			month = dateStr.substring(4,6);
			day = dateStr.substring(6,8);
			hour = dateStr.substring(8,10);
			minite = dateStr.substring(10,12);
			secord = dateStr.substring(12,14);
			if(StringUtils.equals(dateFormate, NORMAL_DATE_FORMAT)){
				return year+DATE_UNION_QUOT+month+DATE_UNION_QUOT+day;
			}else if(StringUtils.equals(dateFormate, COMPACT_DATE_FORMAT)){
				return year+month+day;
			}else if(StringUtils.equals(dateFormate, DATETIME_FORMAT)){
				return year+DATE_UNION_QUOT+month+DATE_UNION_QUOT+day +" "+hour+":"+minite+":"+secord;
			}else if(StringUtils.equals(dateFormate, DATE_ALL)){
				return year+month+day+hour+minite+secord;
			}
		}
		return null;
	}
	
	/**
	 * 2个日期相差的年份
	 * @param date1 较大日期
	 * @param date2 较小日期
	 * @return
	 */
	public static int YearBetween(Date date1, Date date2){
		int montyBetween = monthsBetween(date1, date2);
		int extNum = montyBetween/12;//相差月份除12取余
		return (montyBetween-extNum)/12;
	}
	
	/**
	 * 获取时间戳之间的毫秒
	* @Title: getMilliSecondBetween 
	* @param @param start 
	* @param @param end
	* @param @return    
	* @return long
	* @throws
	 */
	public static long getMilliSecondBetween(long start, long end){
		return (end-start)/(1000*1000);
	}
	
	/**
	 * 日期格式化
	 * @param date 日期
	 * @param format 日期格式
	 * @return
	 */
	public static Date dateFormat(Date date, String format){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return stringToDate(dateFormat.format(date), format);
	}

	
	public static void main(String[] paramArrayOfString) {
		try {
			System.out.println(dateToNormalFullString(dateFormat(new Date(), DATE_FORMAT)));
		} catch (Exception localException) {
			System.out.println(localException);
		}
	}
	
	
	
	
	
}
