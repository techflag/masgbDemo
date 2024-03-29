package com.broov.player.masgb.net.request;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.entity.PlanCategoryListEntity;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class GetStudentPlanListRequest extends JSONRequest {
	private List<PlanCategoryListEntity> list = new ArrayList<PlanCategoryListEntity>();
	private Handler handler;
	private PlanCategoryListEntity planCategoryListEntity;

	public GetStudentPlanListRequest(Handler handler) {
		super(handler);
		this.handler = handler;
	}


	private void addData(JSONObject objData) {
		JSONObject obj = objData;
		planCategoryListEntity = new PlanCategoryListEntity();
		try {
			planCategoryListEntity.setName(obj.getString("Name"));
			planCategoryListEntity.setId(obj.getString("Id"));
			list.add(planCategoryListEntity);
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
			records = json.getString("records");
			if (!"null".equals(records)) {
				JSONArray jsonArray = new JSONArray(records);
				for (i = 0; i < jsonArray.length(); i++) {
					JSONObject obj = new JSONObject(jsonArray.getString(i));
					addData(obj);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		handler.sendMessage(handler.obtainMessage(
				HandleMessageState.GET_STUDENT_PLAN_LIST_SUCCESS, list));

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
			json.put("a", "studentPlanCategoryList");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
