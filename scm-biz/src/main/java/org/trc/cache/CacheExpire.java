package org.trc.cache;

public class CacheExpire {
	public static final int VERY_LONG = 31536000; //1年
	public static final int LONG = 		2592000;  //30天
	public static final int MEDIUM = 86400;       //1天
	public static final int SHORT = 3600;         //1小时
	public static final int VERY_SHORT = 300;     //5分
	
	public static final int DEFAULT = 300;   //默认5分
}
