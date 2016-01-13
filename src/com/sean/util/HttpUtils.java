package com.sean.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class HttpUtils {
	public static HttpClient client;
	static {
		client = HttpClientBuilder.create().build();

	}

	public static String post(String url, Map<String, String> map)
			throws Exception {
		// 处理请求地址
		URI uri = new URI(url);
		HttpPost post = new HttpPost(uri);
		// 添加参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String str : map.keySet()) {
			params.add(new BasicNameValuePair(str, map.get(str)));
		}
		post.setEntity(new UrlEncodedFormEntity(params));
		// 执行请求
		HttpResponse response = client.execute(post);

		if (response.getStatusLine().getStatusCode() == 200) {
			// 处理请求结果
			StringBuffer buffer = new StringBuffer();
			InputStream in = null;
			try {
				in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				String line = null;
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}

			} finally {
				// 关闭流
				if (in != null)
					in.close();
			}

			return buffer.toString();
		} else {
			return null;
		}

	}

	public static String get(String url) throws Exception {
		URI uri = new URI(url);
		HttpGet get = new HttpGet(uri);
		HttpResponse response = client.execute(get);
		if (response.getStatusLine().getStatusCode() == 200) {
			StringBuffer buffer = new StringBuffer();
			InputStream in = null;
			try {
				in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				String line = null;
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}

			} finally {
				if (in != null)
					in.close();
			}

			return buffer.toString();
		} else {
			return null;
		}

	}

}
