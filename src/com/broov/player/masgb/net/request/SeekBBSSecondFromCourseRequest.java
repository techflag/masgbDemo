package com.broov.player.masgb.net.request;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.entity.BBSSecondFromCourseEntity;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class SeekBBSSecondFromCourseRequest extends JSONRequest {

	private Handler handler;
	private BBSSecondFromCourseEntity fromCourseEntity;
	private List<BBSSecondFromCourseEntity> list = new ArrayList<BBSSecondFromCourseEntity>();
	private String courseId;

	public SeekBBSSecondFromCourseRequest(Handler handler, String courseId) {
		super(handler);
		this.handler = handler;
		this.courseId = courseId;
	}

	private void addData(JSONObject objData) {
		JSONObject obj = objData;
		try {
			fromCourseEntity = new BBSSecondFromCourseEntity(
					obj.getString("Id"), obj.getString("RefID"),
					obj.getString("Type"), obj.getString("BoardName"),
					obj.getString("MasterNameList"), obj.getString("Creator"));
			list.add(fromCourseEntity);
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
				HandleMessageState.GET_BBS_SECOND_TOTAL_LIST_FROM_COURSE_SUCCESS, list));
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
			json.put("courseId", courseId);
			json.put("a", "boardByRef");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
