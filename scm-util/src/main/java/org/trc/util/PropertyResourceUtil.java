package org.trc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/** 
 * @ClassName: PropertyResourceUtil 
 * @Description: Property 资源文件工具类
 * @author 吴东雄
 * @date 2015年3月23日 下午12:32:03 
 *  
 */
public class PropertyResourceUtil {

	private final static Logger log = LoggerFactory.getLogger(PropertyResourceUtil.class);
	
	private final static String PROPERTY_FILE = "config.properties";
	/**
	 * 环境变量下面的url.properties的绝对路径
	 */
	
	
	private static final String RUL_PATH = Thread.currentThread()
			.getContextClassLoader().getResource(PROPERTY_FILE).getPath()
			.replace("%20", " ");
	
	
	/**
	 * 
	* @Title: getPropertyVal 
	* @Description: 根据propertyKey 获取值
	* @param @param properyKey 属性键值
	* @param @return    
	* @return String
	* @throws
	 */
	public static String getPropertyVal(String properyKey){
		String propertyVal = "";
		Properties prop = new Properties();
		FileInputStream fis;
		try {
			log.warn(RUL_PATH);
			
			fis = new FileInputStream(RUL_PATH);
			// 属性文件输入流
			prop.load(fis);// 将属性文件流装载到Properties对象中
			fis.close();// 关闭流
			propertyVal = prop.getProperty(properyKey);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(),e);
		}catch (IOException e) {
			log.error(e.getMessage(),e);
		}
		return propertyVal;
	}
	
}
