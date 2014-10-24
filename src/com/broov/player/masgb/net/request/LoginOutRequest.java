package com.broov.player.masgb.net.request;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.bean.UserBean;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class LoginOutRequest extends JSONRequest {

	private Handler handler;
	private UserBean user = UserBean.getInstance();

	public LoginOutRequest(Handler handler) {
		super(handler);
		this.handler = handler;
	}

	@SuppressWarnings("unused")
	@Override
	protected void onHttpSuccess(String str) {
		String json = str.replace("\\", "");
		json = json.replace("\"[", "[");
		json = json.replace("]\"", "]");
//		String records="";
		try {
			if (json != null && json.startsWith("\ufeff")) {
				json = json.substring(1);
			}
			JSONObject jsonObject = new JSONObject(json);

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
			json.put("studentId", user.getId());
			json.put("a", "logOut");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
