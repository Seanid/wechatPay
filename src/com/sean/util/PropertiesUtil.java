package com.sean.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件读取工具
 */
public class PropertiesUtil {

	// private static InputStreamReader inputStream = null;
	// private static OutputStreamWriter outputStream = null;
	private final static String ENCODE = "utf-8";
	private final String CLASSPATH_PERFIX = "classpath:";
	public static Properties properties = new Properties();

	/*默认构照*/
	public PropertiesUtil() {
	}

	/*带参构造，此处结合了spring，在项目启动时从xml加载*/
	public PropertiesUtil(String location) {
		if (location.trim().equals("")) {
			throw new RuntimeException("The path of Properties File is need");
		}
		if (location.toUpperCase().startsWith(CLASSPATH_PERFIX.toUpperCase())) {
			properties = load(location.substring(CLASSPATH_PERFIX.length()));
		} else {
			properties = load(location);
		}
	}
	/*带参构造，此处结合了spring，在项目启动时从xml加载*/
	public PropertiesUtil(List<String> location) {
		for (String src : location) {
			if (src.trim().equals("")) {
				throw new RuntimeException(
						"The path of Properties File is need");
			}
			if (src.toUpperCase().startsWith(CLASSPATH_PERFIX.toUpperCase())) {
				Properties p = load(src.substring(CLASSPATH_PERFIX.length()));
				properties.putAll(p);
			} else {
				Properties p = load(src);
				properties.putAll(p);
			}

		}
	}

	/*加载配置文件*/
	public Properties load(String location) {
		if (location.trim().equals("")) {
			throw new RuntimeException("The path of Properties File is need");
		}
		InputStreamReader inputStream = null;
		Properties p = new Properties();
		try {
			inputStream = new InputStreamReader(getClass().getClassLoader()
					.getResourceAsStream(location), ENCODE);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			p.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}

	/*获取配置文件的值*/
	public String getValueByKey(String key) {
		String val = properties.getProperty(key.trim());
		return val;

	}

	public String getValueByKey(String key, String defaultValue) {
		String val = properties.getProperty(key.trim(), defaultValue.trim());
		return val;
	}

	/*获取配置文件中所有的值*/
	public Map<String, String> getAllProperties() {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration enumeration = properties.propertyNames();
		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			String value = getValueByKey(key);
			map.put(key, value);
		}
		return map;
	}

	/*传入配置文件地址和键，获取值*/
	public static String getValue(String location, String key) {

		if (location.trim().equals("")) {
			throw new RuntimeException("The path of Properties File is need");
		}
		InputStreamReader inputStream = null;
		Properties p = new Properties();
		try {
			inputStream = new InputStreamReader(PropertiesUtil.class
					.getClassLoader().getResourceAsStream(location), ENCODE);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			p.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p.getProperty(key);

	}

}
