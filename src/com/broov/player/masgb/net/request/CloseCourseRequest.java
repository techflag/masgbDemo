package com.broov.player.masgb.net.request;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.bean.UserBean;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class CloseCourseRequest extends JSONRequest {

	private Handler handler;
	private String wareid;
	private UserBean user = UserBean.getInstance();
	private Context context;

	public CloseCourseRequest(Handler handler, Context context) {
		super(handler);
		this.handler = handler;
		this.context = context;
	}

	public void setWareId(String wareid) {
		this.wareid = wareid;
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
			// logid=jsonObject.getString("logid");
			// user.setLogid(logid);
			if ("1".equals(code)) {
				handler.sendEmptyMessage(HandleMessageState.CLOSE_COURSE_SUCCESS);
			} else if ("-1".equals(code)) {
				handler.sendEmptyMessage(HandleMessageState.CLOSE_COURSE_FAILURE);
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
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"userData", Context.MODE_PRIVATE);
		String id = sharedPreferences.getString("Id", "");
		JSONObject json = new JSONObject();
		try {
			json.put("studentid", id);
			json.put("a", "coursewareend");
			json.put("wareid", wareid);
			json.put("logid", user.getLogid());
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
