package com.broov.player.masgb.net.request;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class GetStudentPlanContentRequest extends JSONRequest {
	private Handler handler;
	private Map<String, String> map = new HashMap<String, String>();
	private String stuplyid;

	public GetStudentPlanContentRequest(Handler handler) {
		super(handler);
		this.handler = handler;
	}

	public void setStuplyid(String stuplyid) {
		this.stuplyid = stuplyid;
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
		String startTime = "";
		String endTime = "";
		JSONObject json;
		try {
			if (str != null && str.startsWith("\ufeff")) {
				str = str.substring(1);
			}
			json = new JSONObject(str);
			if (json.has("records")) {
				records = json.getString("records");
				if (!"null".equals(records)) {
					if (records != null && records.startsWith("\ufeff")) {
						records = records.substring(1);
					}
					JSONObject obj = new JSONObject(records);
					map.put("Creator", obj.getString("Creator"));
					map.put("Credithours", obj.getString("Credithours"));
					map.put("PlanDesc", obj.getString("PlanDesc"));
					map.put("ProvinceCredithour",
							obj.getString("ProvinceCredithour"));
					map.put("ClassCredithour", obj.getString("ClassCredithour"));
					map.put("RequireCredithour",
							obj.getString("RequireCredithour"));
					startTime = obj.getString("StartTime");
					JSONObject jsonStartTime = new JSONObject(startTime);
					map.put("startTime", jsonStartTime.getString("date")
							.substring(0, 10));
					endTime = obj.getString("EndTime");
					JSONObject jsonEndTime = new JSONObject(endTime);
					map.put("endTime",
							jsonEndTime.getString("date").substring(0, 10));
				}

			} else {
				map.put("Creator", "null");
				map.put("Credithours", "null");
				map.put("PlanDesc", "null");
				map.put("ProvinceCredithour", "null");
				map.put("ClassCredithour", "null");
				map.put("RequireCredithour", "null");
				map.put("startTime", "null");
				map.put("endTime", "null");

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		handler.sendMessage(handler.obtainMessage(
				HandleMessageState.GET_STUDENT_PLAN_CONTENT_SUCCESS, map));

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
			json.put("a", "studentPlan");
			json.put("stuplyid", stuplyid);
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
