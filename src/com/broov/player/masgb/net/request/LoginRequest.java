package com.broov.player.masgb.net.request;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.bean.FlagBean;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class LoginRequest extends JSONRequest {

	private Handler handler;
	private String username;
	private String pwd;
	private Context context;
	private boolean isRequestOk;
	private FlagBean flagBean = FlagBean.getInstance();

	public LoginRequest(Handler handler) {
		super(handler);
		this.handler = handler;
	}

	public void setParams(String username, String pwd, Context context,
			boolean isRequestOk) {
		this.username = username;
		this.pwd = pwd;
		this.context = context;
		this.isRequestOk = isRequestOk;
	}

	@Override
	protected void onHttpSuccess(String str) {
		String code = "";
		String records = "";
		try {
			if (str != null && str.startsWith("\ufeff")) {
				str = str.substring(1);
			}

			JSONObject json = new JSONObject(str);
			code = json.getString("code");

			if (json.has("records")) {
				if ("1".equals(code)) {
					handler.sendEmptyMessage(HandleMessageState.LOGIN_SUCCESS);
				} else if ("0".equals(code)) {
					handler.sendEmptyMessage(HandleMessageState.LOGIN_FAILURE);
				} else {
					handler.sendMessage(handler
							.obtainMessage(HandleMessageState.SERVERLT_ERROR));
				}
				records = json.getString("records");
				JSONObject josnRecords = new JSONObject(records);
				SharedPreferences sharedPreferences = context
						.getSharedPreferences("userData", Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();// 获取编辑器
				editor.putString("Id", josnRecords.getString("Id"));
				editor.putString("SessionId",
						josnRecords.getString("sessionId"));
				editor.commit();// 提交修改
				// user.setId(josnRecords.getString("Id"));
				// user.setSessionId(josnRecords.getString("sessionId"));
			} else {
				flagBean.setRequestOk(false);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onHttpFailure(int errorCode, String why) {
		flagBean.setRequestOk(false);
		if (TextUtils.isEmpty(why)) {
		} else {
			handler.sendMessage(handler
					.obtainMessage(HandleMessageState.SERVERLT_ERROR));
		}
	}

	@Override
	public String getJsonData() {
		if (!isRequestOk) {
			flagBean.setRequestOk(true);
			JSONObject json = new JSONObject();
			try {
				json.put("pwd", pwd);
				json.put("username", username);
				json.put("a", "login");
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
			return json.toString();
		} else
		return "";
	}

}
