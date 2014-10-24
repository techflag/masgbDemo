package com.broov.player.masgb.net.request;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.entity.BBSSecondTotalEntity;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class SeekBBSSecondOfMyBBSRequest extends JSONRequest {

	private Handler handler;
	private BBSSecondTotalEntity totalEntity;
	private List<BBSSecondTotalEntity> list = new ArrayList<BBSSecondTotalEntity>();
	private Context context;
	private String boardId;
	private String index;
	@SuppressWarnings("unused")
	private String count;

	public SeekBBSSecondOfMyBBSRequest(Context context,Handler handler, String boardId, String index,String count) {
		super(handler);
		this.context=context;
		this.handler = handler;
		this.boardId = boardId;
		this.index = index;
		this.count=count;
	}

	private void addData(JSONObject objData) {
		JSONObject obj = objData;

		try {
			totalEntity = new BBSSecondTotalEntity(obj.getString("Id"),
					obj.getString("BoardID"), obj.getString("Title"),
					obj.getString("ReplayCount"),
					obj.getString("StudentInfo_ActualName"));
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
					for (i = 0; i < jsonArray.length(); i++) {
						JSONObject obj = new JSONObject(jsonArray.getString(i));
						addData(obj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		handler.sendMessage(handler.obtainMessage(
				HandleMessageState.GET_BBS_SECOND_TOTAL_LIST_SUCCESS, list));
	}

	@Override
	protected void onHttpFailure(int errorCode, String why) {
		if (TextUtils.isEmpty(why)) {
		} else {
		}
	}

	@Override
	public String getJsonData() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"userData", Context.MODE_PRIVATE);
		String id = sharedPreferences.getString("Id", "");
		JSONObject json = new JSONObject();
		try {
			json.put("boardId", boardId);
			json.put("studentId", id);
			json.put("a", "myTopicList");
			json.put("pageIndex", index);
			json.put("pageSize",4);
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
