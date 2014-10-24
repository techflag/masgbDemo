package com.broov.player.masgb.net.request;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class CheckIsLoginRequest extends JSONRequest {

	private Handler handler;
	private Context context;

	public CheckIsLoginRequest(Handler handler,Context context) {
		super(handler);
		this.handler = handler;
		this.context=context;
	}

	@Override
	protected void onHttpSuccess(String str) {
		String json = str.replace("\\", "");
		json = json.replace("\"[", "[");
		json = json.replace("]\"", "]");
		String records="";
		try {
			if (json != null && json.startsWith("\ufeff")) {
				json = json.substring(1);
			}
			JSONObject jsonObject = new JSONObject(json);
			Log.d("checkLogin", jsonObject.toString());
			if(jsonObject.has("records")){
				records=jsonObject.getString("records");
				if(records.equals("0")){
					handler.sendEmptyMessage(HandleMessageState.NEED_LOGIN_AGAIN);
				}else {
					handler.sendEmptyMessage(HandleMessageState.NO_NEED_LOGIN_AGAIN);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onHttpFailure(int errorCode, String why) {
		if (TextUtils.isEmpty(why)) {
		} else {
			handler.sendEmptyMessage(HandleMessageState.SERVERLT_ERROR);
		}
	}

	@Override
	public String getJsonData() {
		SharedPreferences sharedPreferences = context.getSharedPreferences("userData",
				Context.MODE_PRIVATE);
		String id = sharedPreferences.getString("Id", "");
		String sessionId = sharedPreferences.getString("SessionId", "");
		Log.d("checkLogin", "sessionId====="+sessionId);
		JSONObject json = new JSONObject();
		try {
			json.put("studentId", id);
			json.put("a", "isLogin");
			json.put("sessionId", sessionId);
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
