package com.broov.player.masgb.net.request;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.entity.BBSTotalEntity;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class SeekBBSRequest extends JSONRequest {

	private Handler handler;
	private BBSTotalEntity totalEntity;
	private List<BBSTotalEntity> list = new ArrayList<BBSTotalEntity>();
	private int VIEW_COUNT = 3;
	@SuppressWarnings("unused")
	private Context context;

	public SeekBBSRequest(Handler handler, Context context) {
		super(handler);
		this.handler = handler;
		this.context = context;
	}

	private void addData(JSONObject objData) {
		JSONObject obj = objData;

		try {
			totalEntity = new BBSTotalEntity(obj.getString("Id"),
					obj.getString("BoardName"),
					obj.getString("MasterNameList"), new JSONObject(
							obj.getString("LastPostTime")).getString("date"),
					obj.getString("TopicAmount"), obj.getString("Notes"));
			list.add(totalEntity);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onHttpSuccess(String str) {
		String jsonData = str.replace("\\", "");
		jsonData = jsonData.replace("\"[", "[");
		jsonData = jsonData.replace("]\"", "]");
		if (TextUtils.isEmpty(str)) {
			handler.sendEmptyMessage(HandleMessageState.GET_COURSED_FAILURE);
			return;
		}
		if (list != null) {
			list.clear();
		}
		String records = "";
		int i = 0;
		try {
			if (jsonData != null && jsonData.startsWith("\ufeff")) {
				jsonData = jsonData.substring(1);
			}
			JSONObject json = new JSONObject(jsonData);
			if (json.has("records")) {
				records = json.getString("records");
				if (!"null".equals(records)) {
					JSONArray jsonArray = new JSONArray(records);
					int size = (jsonArray.length() / VIEW_COUNT + 1)
							* VIEW_COUNT - jsonArray.length();
					if (size != VIEW_COUNT) {
						for (i = 0; i < (jsonArray.length() / VIEW_COUNT + 1)
								* VIEW_COUNT; i++) {
							if (i < jsonArray.length()) {
								JSONObject obj = new JSONObject(
										jsonArray.getString(i));
								addData(obj);
							} else {
								totalEntity = new BBSTotalEntity();
								totalEntity.setId("00000000");
								list.add(totalEntity);
							}

						}
					} else {
						for (i = 0; i < jsonArray.length(); i++) {
							JSONObject obj = new JSONObject(
									jsonArray.getString(i));
							addData(obj);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		handler.sendMessage(handler.obtainMessage(
				HandleMessageState.GET_BBS_TOTAL_LIST_SUCCESS, list));
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
			json.put("userId", 1);
			json.put("a", "boardListByUserId");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
