package com.broov.player.masgb.net.request;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.entity.BBSListDetailOrigEntity;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class GetBBSListDetailOrigRequest extends JSONRequest {
	private Handler handler;
	private String topicId;
	private BBSListDetailOrigEntity bbsListDetailOrigEntity;
	private List<BBSListDetailOrigEntity> list = new ArrayList<BBSListDetailOrigEntity>();

	public GetBBSListDetailOrigRequest(Handler handler, String topicId) {
		super(handler);
		this.handler = handler;
		this.topicId = topicId;
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
		int i = 0;
		JSONObject json;
		try {
			if (str != null && str.startsWith("\ufeff")) {
				str = str.substring(1);
			}
			json = new JSONObject(str);
			if (json.has("records")) {
				records = json.getString("records");
				if (!"null".endsWith(records)) {
					JSONArray jsonArray = new JSONArray(records);
					for (i = 0; i < jsonArray.length(); i++) {
						JSONObject obj = new JSONObject(jsonArray.getString(i));
						bbsListDetailOrigEntity = new BBSListDetailOrigEntity(
								obj.getString("Title"),
								obj.getString("Subject"),
								new JSONObject(obj.getString("CreateTime"))
										.getString("date"),
								obj.getString("StudentInfo_ActualName"));
						list.add(bbsListDetailOrigEntity);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		handler.sendMessage(handler.obtainMessage(
				HandleMessageState.GET_BBS_LIST_DETAIL_ORIG_SUCCESS, list));

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
			json.put("topicId", topicId);
			json.put("a", "topicDetail");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
