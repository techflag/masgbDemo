package com.broov.player.masgb.net;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.Handler;

public abstract class JSONRequest extends Request {

	public JSONRequest(Handler handler) {
		super(handler);
	}

	/**
	 * 获得jsonData
	 * 
	 * @return
	 */
	public abstract String getJsonData();

	protected void httpConnect() {
		final String json = getJsonData();
		StringEntity se;
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpPost post = new HttpPost("http://61.191.180.3/masgb/api.php");
//		HttpPost post = new HttpPost("http://10.20.34.220/masgb/api.php");
		post.setHeader("accept", "application/json");
		try {
			se = new StringEntity(json,"UTF-8");
			post.setEntity(se);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		HttpResponse response = null;
		HttpEntity httpEntity = null;
		int responseCode = -1;
		try {
			response = httpClient.execute(post);
			responseCode = response.getStatusLine().getStatusCode();
			if (responseCode == 200) {
				httpEntity = response.getEntity();
				InputStream is = httpEntity.getContent();
				byte[] buf = new byte[1024 * 200];
				int len = 0;
				StringBuffer msg = new StringBuffer();
				while ((len = is.read(buf)) != -1) {
					msg.append(new String(buf, 0, len));
				}
				onHttpSuccess(msg.toString());
			} else {
				onHttpFailure(responseCode, "");
			}
		} catch (Exception e) {
			onHttpFailure(responseCode, e.getMessage());
			e.printStackTrace();
		}

	}

	protected abstract void onHttpSuccess(String str);

	protected abstract void onHttpFailure(int errorCode, String why);

}
