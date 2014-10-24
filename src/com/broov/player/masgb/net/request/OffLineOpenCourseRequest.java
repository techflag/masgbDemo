package com.broov.player.masgb.net.request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class OffLineOpenCourseRequest extends JSONRequest {

	private Handler handler;
	private JSONArray updateTimejson;

	public OffLineOpenCourseRequest(Handler handler) {
		super(handler);
		this.handler = handler;
	}

	public void setCourseId(JSONArray updateTimejson) {
		this.updateTimejson = updateTimejson;
	}

	@Override
	protected void onHttpSuccess(String str) {
		String code = "";
		String json = str.replace("\\", "");
		json = json.replace("\"[", "[");
		json = json.replace("]\"", "]");
		try {
			if (json != null && json.startsWith("\ufeff")) {
				json = json.substring(1);
			}
			JSONObject jsonObject = new JSONObject(json);
			code = jsonObject.getString("code");
			if (Integer.valueOf(code) >= 1) {
				handler.sendEmptyMessage(HandleMessageState.UPDATE_TIEM_SUCCESS);
			} else {
				handler.sendEmptyMessage(HandleMessageState.UPDATE_TIME_FAILURE);
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
		JSONObject json = new JSONObject();
		try {
			json.put("a", "uploadstudytime");
			json.put("studytime", updateTimejson);
			Log.d("jobj", "json=" + json.toString());
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
