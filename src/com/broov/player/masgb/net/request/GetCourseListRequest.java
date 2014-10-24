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
import android.util.Log;

import com.broov.player.masgb.entity.SelectCourseEntity;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class GetCourseListRequest extends JSONRequest {
	private int VIEW_COUNT = 4;
	private SelectCourseEntity selectCourseEntity;
	private Handler handler;
	private String type;
	private String content = "";
	private int page;
	private Context context;
	private List<SelectCourseEntity> list = new ArrayList<SelectCourseEntity>();

	public GetCourseListRequest(Handler handler,Context context) {
		super(handler);
		this.handler = handler;
		this.context=context;
	}

	public void setType(String type, String content) {
		this.type = type;
		this.content = content;
	}

	public void setPage(int page) {
		this.page = page;
	}

	private void addData(JSONObject objData) {

		JSONObject obj = objData;
		selectCourseEntity = new SelectCourseEntity();
		try {
			// selectCourseEntity.setDescription(obj.getString("Description"));
			selectCourseEntity.setCourse_style(obj.getString("TypeName"));
			selectCourseEntity.setCourse_name(obj.getString("Name"));
			selectCourseEntity.setCourse_person(obj.getString("Lectuer"));
			selectCourseEntity.setCourse_score(obj.getString("CreditHour"));
			selectCourseEntity.setCourse_id(obj.getString("ID"));
			if ("1".equals(obj.getString("Type"))) {
				type = "选修";
			} else if ("0".equals(obj.getString("Type"))) {
				type = "必修";
			}
			selectCourseEntity.setCourse_type(type);
			list.add(selectCourseEntity);

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
			handler.sendEmptyMessage(HandleMessageState.GET_COURSE_LIST_FAILURE);
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
								selectCourseEntity = new SelectCourseEntity();
								selectCourseEntity.setCourse_id("00000000");
								list.add(selectCourseEntity);
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
				HandleMessageState.GET_COURSE_LIST_SUCCESS, list));
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
		String id = sharedPreferences.getString("Id", "");
		JSONObject json2 = new JSONObject();
		// JSONObject jsonSearch = new JSONObject();
		try {
			json2.put("coursename", content);
			if (!type.equals("-1")) {
				json2.put("typeid", type);
			} else {
				json2.put("typeid", "");
			}
			json2.put("trainTrpeId", 0);
			json2.put("studentid", id);
			json2.put("a", "searchStudentCourseList");
			// json2.put("where", jsonSearch);
			json2.put("pagesize", 4);
			json2.put("page", page);
			System.out.println("***************"+json2.toString());
			Log.i("json2", json2.toString());
		} catch (JSONException e2) {
			e2.printStackTrace();
			System.out.println("*********e2.printStackTrace():"+e2.toString());
		}
		return json2.toString();
	}

}
