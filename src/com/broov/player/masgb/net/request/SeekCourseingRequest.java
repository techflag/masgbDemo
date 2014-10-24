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

import com.broov.player.masgb.entity.CoursingEntity;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class SeekCourseingRequest extends JSONRequest {

	private Handler handler;
	private List<CoursingEntity> list = new ArrayList<CoursingEntity>();
	private CoursingEntity coursingEntity;
	private String type = "";
	private int VIEW_COUNT = 4;
	private Context context;
	private String id;

	public SeekCourseingRequest(Handler handler,Context context) {
		super(handler);
		this.handler = handler;
		this.context=context;
	}

	private void addData(JSONObject objData) {
		JSONObject obj = objData;
		coursingEntity = new CoursingEntity();
		try {
			coursingEntity.setCourse_name(obj.getString("CourseName"));
			coursingEntity.setCourse_length(obj.getString("StudyTime"));
			coursingEntity.setCourse_person(obj.getString("Lectuer"));
			coursingEntity.setCourse_score(obj.getString("CreditHour"));
			coursingEntity.setCourse_id(obj.getString("ID"));
			coursingEntity.setCourse_time_length(obj.getString("Hour"));
			if ("1".equals(obj.getString("Type"))) {
				type = "选修";
			} else if ("0".equals(obj.getString("Type"))) {
				type = "必修";
			} else if ("2".equals(obj.getString("Type"))) {
				type = "主修";
			}
			coursingEntity.setCourse_type(type);
			list.add(coursingEntity);
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
			handler.sendEmptyMessage(HandleMessageState.GET_COURSING_FAILURE);
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
								coursingEntity = new CoursingEntity();
								coursingEntity.setCourse_id("00000000");
								list.add(coursingEntity);
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
				HandleMessageState.GET_COURSING_SUCCESS, list));
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
		SharedPreferences sharedPreferences = context.getSharedPreferences("userData",
				Context.MODE_PRIVATE);
		id = sharedPreferences.getString("Id", "");
		JSONObject json = new JSONObject();
		try {
			json.put("studentid", id);
			json.put("a", "studentcoursestudylist");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}