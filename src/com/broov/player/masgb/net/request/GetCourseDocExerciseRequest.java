package com.broov.player.masgb.net.request;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class GetCourseDocExerciseRequest extends JSONRequest {

	private Handler handler;
	private String wareid;

	public GetCourseDocExerciseRequest(Handler handler) {
		super(handler);
		this.handler = handler;
	}

	public void setWareid(String wareid) {
		this.wareid = wareid;
	}

	@Override
	protected void onHttpSuccess(String str) {
		String json = str.replace("\\", "");
		json = json.replace("\"[", "[");
		json = json.replace("]\"", "]");
		handler.sendMessage(handler.obtainMessage(
				HandleMessageState.GET_COURSE_DOC_EXERCISE_SUCCESS, json));
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
			json.put("wareid", wareid);
			json.put("a", "getwareexam");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
