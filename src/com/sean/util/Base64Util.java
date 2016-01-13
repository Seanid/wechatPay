package com.sean.util;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * base64编码解码工具
 * @author Sean
 *
 */
public class Base64Util {

	

	public static byte[] decryptBASE64(String key) throws Exception {
		return (new BASE64Decoder()).decodeBuffer(key);
	}


	public static String encryptBASE64(byte[] key) throws Exception {
		return (new BASE64Encoder()).encodeBuffer(key);
	}

	
}
