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

import com.broov.player.masgb.entity.CourseListEntity;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class GetCourseDocListRequest extends JSONRequest {
	private List<CourseListEntity> list = new ArrayList<CourseListEntity>();
	private Handler handler;
	private String course_id;
	private CourseListEntity courseListEntity;
	private int VIEW_COUNT = 4;
	private Context context;

	public GetCourseDocListRequest(Handler handler,Context context) {
		super(handler);
		this.handler = handler;
		this.context=context;
	}

	public void setCourse_id(String course_id) {
		this.course_id = course_id;
	}

	private void addData(JSONObject objData) {
		JSONObject obj = objData;
		courseListEntity = new CourseListEntity();
		try {
			courseListEntity.setUri(obj.getString("DownLoadUrl"));
			courseListEntity.setCourse_doc_id(obj.getString("Id"));
			courseListEntity.setCourseName(obj.getString("Name"));
			if ("yesview".equals(obj.getString("isViewed"))) {
				courseListEntity.setCourseStatus("完成");
			} else {
				courseListEntity.setCourseStatus("未读");
			}

			list.add(courseListEntity);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onHttpSuccess(String str) {
		str = str.replace("\\", "");
		str = str.replace("\"[", "[");
		str = str.replace("]\"", "]");
		if (TextUtils.isEmpty(str)) {
			handler.sendEmptyMessage(HandleMessageState.SERVERLT_ERROR);
		}
		if (list != null) {
			list.clear();
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
					int size = (jsonArray.length() / VIEW_COUNT + 1)
							* VIEW_COUNT - jsonArray.length();

					if (size != VIEW_COUNT) {
						for (i = 0; i < (jsonArray.length() / VIEW_COUNT + 1)
								* VIEW_COUNT; i++) {
							if (i < jsonArray.length()) {
								JSONObject obj = new JSONObject(
										jsonArray.getString(i));
								addData(obj);
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
		} catch (JSONException e) {
			e.printStackTrace();
		}

		handler.sendMessage(handler.obtainMessage(
				HandleMessageState.GET_COURSE_DOC_LIST_SUCCESS, list));

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
		JSONObject json = new JSONObject();
		try {
			json.put("courseid", course_id);
			json.put("a", "coursewarelist");
			json.put("stuid", id);
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
