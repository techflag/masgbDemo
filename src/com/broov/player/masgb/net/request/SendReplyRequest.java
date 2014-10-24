package com.broov.player.masgb.net.request;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class SendReplyRequest extends JSONRequest {

	private Handler handler;
	private String id;
	private Context context;
	private String topicId;
	private String content;
	private String ip;

	public SendReplyRequest(Handler handler, Context context, String topicId,
			String content, String ip) {
		super(handler);
		this.handler = handler;
		this.context = context;
		this.topicId = topicId;
		this.content = content;
		this.ip = ip;
	}

	@Override
	protected void onHttpSuccess(String str) {

		String json = str.replace("\\", "");
		json = json.replace("\"[", "[");
		json = json.replace("]\"", "]");
		try {
			if (json != null && json.startsWith("\ufeff")) {
				json = json.substring(1);
			}
			JSONObject jsonObject = new JSONObject(json);
			String records = jsonObject.getString("records");
			if (Integer.valueOf(records) > 0) {
				handler.sendEmptyMessage(HandleMessageState.ADD_REPLY_SUCCESS);
			} else {
				handler.sendEmptyMessage(HandleMessageState.ADD_REPLY_FAILURE);
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
		id = sharedPreferences.getString("Id", "");
		JSONObject json = new JSONObject();
		try {
			json.put("creator", id);
			json.put("a", "addTopicReplay");
			json.put("topicId", topicId);
			json.put("subject", content);
			json.put("iPAddress", ip);
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
