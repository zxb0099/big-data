package com.xb.zhang.system;
import java.util.Properties;

/**
 * 
 * Description: 
 * All Rights Reserved.复制的
 * @version 1.0  2015年1月12日 下午2:31:51  by 吕翔  (lvxiang@dangdang.com) 创建
 */
public class ConfigPropertieUtils {   
	public static Properties prop = new Properties();
    
    public static String getString(final String key) {
		return prop.getProperty(key);
	}

	public static String getString(final String key, final String defaultValue) {
		return prop.getProperty(key, defaultValue);
	}
	
	public static Integer getInteger(final String key, final Integer defaultValue) {
		Integer returnValue;
		
		String value = prop.getProperty(key);
		
		if(value != null && !value.trim().equals("")){
			try {
				returnValue = Integer.parseInt(value);
			} catch (Exception e) {
				returnValue = defaultValue;
			}
		}else{
			returnValue = defaultValue;
		}
		
		return returnValue;
	}

	public static Long getLong(final String key, final Long defaultValue) {
		Long returnValue;

		String value = prop.getProperty(key);

		if(value != null && !value.trim().equals("")){
			try {
				returnValue = Long.parseLong(value);
			} catch (Exception e) {
				returnValue = defaultValue;
			}
		}else{
			returnValue = defaultValue;
		}

		return returnValue;
	}
}
