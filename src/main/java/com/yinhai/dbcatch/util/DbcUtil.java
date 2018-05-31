package com.yinhai.dbcatch.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class DbcUtil {
	public static Properties properties = new Properties();
	private static final Logger logger = LoggerFactory.getLogger(DbcUtil.class);
	static {
		try {
			File file = new File(".");
			String currentDir = file.getCanonicalPath();
			File configFile = new File(currentDir+ File.separator+"dbcatch.properties");
			if (!configFile.exists()) {
				throw new Exception("配置文件不存在："+configFile);
			}
			InputStream is = new FileInputStream(configFile);
			properties.load(is);
		} catch (Exception e) {
			logger.error("dbcatch.properties出错："+e.getMessage());
		}
	}
	
	public static String getProperty(String propertyKey) {
		try{
		   return properties.getProperty(propertyKey).trim();
		}catch(Exception e){
			throw new RuntimeException("读取配置文件参数失败!");
		}
	}

	public static int getIntPrp(String propertyKey) {
		return Integer.valueOf(properties.getProperty(propertyKey).trim());
	}
}
