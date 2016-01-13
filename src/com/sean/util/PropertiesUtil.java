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

public class PropertiesUtil {

	// private static InputStreamReader inputStream = null;
	// private static OutputStreamWriter outputStream = null;
	private final static String ENCODE = "utf-8";
	private final String CLASSPATH_PERFIX = "classpath:";
	public static Properties properties = new Properties();

	public PropertiesUtil() {
	}

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

	public String getValueByKey(String key) {
		String val = properties.getProperty(key.trim());
		return val;

	}

	public String getValueByKey(String key, String defaultValue) {
		String val = properties.getProperty(key.trim(), defaultValue.trim());
		return val;
	}

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
