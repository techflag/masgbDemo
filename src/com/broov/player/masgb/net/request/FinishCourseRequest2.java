package com.broov.player.masgb.net.request;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class FinishCourseRequest2 extends JSONRequest {
	private Handler handler;
	private String examStartId;
	private String answerPackage;
	private String totalScore;
	private String clerk_Wdpf;
	private String examTime;
	private String moveOutTimes;

	public FinishCourseRequest2(Handler handler) {
		super(handler);
		this.handler = handler;
	}

	public void setData(String examStartId, String answerPackage,
			String totalScore, String clerk_Wdpf, String examTime,
			String moveOutTimes) {
		this.examStartId = examStartId;
		this.answerPackage = answerPackage;
		this.totalScore = totalScore;
		this.clerk_Wdpf = clerk_Wdpf;
		this.examTime = examTime;
		this.moveOutTimes = moveOutTimes;
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
		JSONObject json;
		try {
			if (str != null && str.startsWith("\ufeff")) {
				str = str.substring(1);
			}
			json = new JSONObject(str);
			if (json.has("records")) {
				records = json.getString("records");
				JSONObject obj=new JSONObject(records);
				if(Integer.valueOf(obj.get("clerk_kscj_id").toString()) != null&&Integer.valueOf(obj.get("clerk_kscj_id").toString())>0){
					handler.sendEmptyMessage(HandleMessageState.FINISH_SUCCESS);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
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
			json.put("a", "examSubmit");
			json.put("examStartId", examStartId);
			json.put("answerPackage", answerPackage);
			json.put("totalScore", totalScore);
			json.put("clerk_Wdpf", clerk_Wdpf);
			json.put("examTime", examTime);
			json.put("moveOutTimes", moveOutTimes);
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
