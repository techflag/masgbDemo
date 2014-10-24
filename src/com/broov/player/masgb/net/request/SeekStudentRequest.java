package com.broov.player.masgb.net.request;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.net.JSONRequest;

public class SeekStudentRequest extends JSONRequest {

	private Handler handler;

	public SeekStudentRequest(Handler handler) {
		super(handler);
		this.handler = handler;
	}

	@Override
	protected void onHttpSuccess(String str) {
		String json = str.replace("\\", "");
		json = json.replace("\"[", "[");
		json = json.replace("]\"", "]");
		handler.sendMessage(handler.obtainMessage(2, json));
	}

	@Override
	protected void onHttpFailure(int errorCode, String why) {
		if (TextUtils.isEmpty(why)) {
		} else {
		}
	}

	@Override
	public String getJsonData() {
		JSONObject json = new JSONObject();
		try {
			json.put("username", "masgaj");
			json.put("a", "student");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
