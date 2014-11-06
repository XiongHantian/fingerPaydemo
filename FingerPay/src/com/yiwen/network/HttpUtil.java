package com.yiwen.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

public class HttpUtil {

	public static final String TAG = "HttpUtil";
	public static final String SIGN_URL = "http://166.111.72.71:60406/fingerprint/register";
	public static final String AUTH_URL = "http://166.111.72.71:60406/fingerprint/verify";
	public static final String access_url = "https://oauth.api.189.cn/emp/oauth2/v3/access_token";
	public static final String send_url = "http://api.189.cn/v2/emp/templateSms/sendSms";
	public static final String app_id = "622966130000038222";
	public static final String template_id = "91003045";
	public static final String app_secret = "6e576726bf3ac8302f8ee2ffe70b26b7";
	public static final String grant_type = "client_credentials";
	public static final String redirect_uri = "https://oauth.api.189.cn/emp/oauth2/default.html";
	public static final String access_token = "3e922b641aee90a5c168756a5cfe21761415275293964";
	
	/**
	 * http Post����
	 * 
	 * @param url
	 * @param pairList
	 * @return
	 */
	public static String httpPost(String url, List<NameValuePair> pairList) {
		try {
			HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList);
			HttpPost httpPost = new HttpPost(url);
			// �����������ݼ���������
			httpPost.setEntity(requestHttpEntity);
			// ��Ҫ�ͻ��˶�������������
			HttpClient httpClient = new DefaultHttpClient();
			// ��������
			HttpResponse response = httpClient.execute(httpPost);
			// ��ʾ��Ӧ
			return getResponseStr(response);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ��ȡָ��URL����Ӧ�ַ���
	 * 
	 * @return urlString
	 */
	public static String getURLResponse(String urlString) {
		HttpURLConnection conn = null; // ���Ӷ���
		InputStream is = null;
		String resultData = "";
		try {
			URL url = new URL(urlString); // URL����
			conn = (HttpURLConnection) url.openConnection(); // ʹ��URL��һ������
			conn.setDoInput(true); // ����������������������
			// conn.setDoOutput(true); // ������������������ϴ�
			conn.setUseCaches(false); // ��ʹ�û���
			conn.setRequestMethod("GET"); // ʹ��get����
			conn.setRequestProperty("Connection", "close");
			is = conn.getInputStream(); // ��ȡ����������ʱ��������������
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader bufferReader = new BufferedReader(isr);
			String inputLine = "";
			while ((inputLine = bufferReader.readLine()) != null) {
				resultData += inputLine + "\n";
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return resultData;
	}

	public static String getResponseStr(HttpResponse response) {
		if (null == response) {
			return null;
		}
		HttpEntity httpEntity = response.getEntity();
		try {
			InputStream inputStream = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			String result = "";
			String line = "";
			while (null != (line = reader.readLine())) {
				result += line;

			}
			return result;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String uploadFingerAndId(String url, String id, String path) {
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter("http.socket.timeout", 90000); // 90
																			// second
		HttpPost httppost = new HttpPost(url);
		File file = new File(path);
		MultipartEntity mpEntity = new MultipartEntity();
		mpEntity.addPart("image", new FileBody(file, "image/jpeg"));// �ϴ�ͼƬ
		try {
			mpEntity.addPart("user_id", new StringBody(id));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}// �ϴ����֤��
		httppost.setEntity(mpEntity);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getResponseStr(response);
	}

}
