package com.broov.player.masgb.net.request;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class GetCourseDescriRequest extends JSONRequest {
	private Handler handler;
	private Map<String, String> map = new HashMap<String, String>();
	private String courseid;

	public GetCourseDescriRequest(Handler handler) {
		super(handler);
		this.handler = handler;
	}

	public void setStuplyid(String courseid) {
		this.courseid = courseid;
	}

	@Override
	protected void onHttpSuccess(String str) {
		str = str.replace("\\", "");
		str = str.replace("\"[", "[");
		str = str.replace("]\"", "]");
		if (TextUtils.isEmpty(str)) {
			handler.sendEmptyMessage(HandleMessageState.SERVERLT_ERROR);
		}
		String records = "";
		JSONObject json;
		try {
			if (str != null && str.startsWith("\ufeff")) {
				str = str.substring(1);
			}
			json = new JSONObject(str);
			records = json.getString("records");
			if (!"null".equals(records)) {
				if (records != null && records.startsWith("\ufeff")) {
					records = records.substring(1);
				}
				JSONObject obj = new JSONObject(records);
				map.put("Description", obj.getString("Description"));

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		handler.sendMessage(handler.obtainMessage(
				HandleMessageState.GET_COURSE_DESCRI_SUCCESS, map));

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
			json.put("a", "courseview");
			json.put("courseid", courseid);
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
