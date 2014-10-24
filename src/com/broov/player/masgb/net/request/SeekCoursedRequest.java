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

import com.broov.player.masgb.entity.CoursedEntity;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class SeekCoursedRequest extends JSONRequest {

	private Handler handler;
	private CoursedEntity coursedEntity;
	private String type = "";
	private List<CoursedEntity> list = new ArrayList<CoursedEntity>();
	private int VIEW_COUNT = 4;
	private Context context;

	public SeekCoursedRequest(Handler handler,Context context) {
		super(handler);
		this.handler = handler;
		this.context=context;
	}

	private void addData(JSONObject objData) {
		JSONObject obj = objData;
		coursedEntity = new CoursedEntity();
		try {
			coursedEntity.setCourse_name(obj.getString("CourseName"));
			coursedEntity.setCourse_length(obj.getString("StudyTime"));
			coursedEntity.setCourse_person(obj.getString("Lectuer"));
			coursedEntity.setCourse_score(obj.getString("CreditHour"));
			coursedEntity.setCourse_id(obj.getString("ID"));
			coursedEntity.setCourse_time_length(obj.getString("Hour"));

			if ("1".equals(obj.getString("Type"))) {
				type = "选修";
			} else if ("0".equals(obj.getString("Type"))) {
				type = "必修";
			} else if ("2".equals(obj.getString("Type"))) {
				type = "主修";
			}
			coursedEntity.setCourse_type(type);
			list.add(coursedEntity);
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
								coursedEntity = new CoursedEntity();
								coursedEntity.setCourse_id("00000000");
								list.add(coursedEntity);
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
				HandleMessageState.GET_COURSED_SUCCESS, list));
	}

	@Override
	protected void onHttpFailure(int errorCode, String why) {
		if (TextUtils.isEmpty(why)) {
		} else {
		}
	}

	@Override
	public String getJsonData() {
		SharedPreferences sharedPreferences = context.getSharedPreferences("userData",
				Context.MODE_PRIVATE);
		String id = sharedPreferences.getString("Id", "");
		JSONObject json = new JSONObject();
		try {
			json.put("studentid", id);
			json.put("a", "studentcourseexamlist");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
