package com.broov.player.masgb.net.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.entity.FinishEntity;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class FinishCourseRequest extends JSONRequest {
	private Handler handler;
	private List<FinishEntity> finishList = new ArrayList<FinishEntity>();
	private Map<String, String> finishMap = new HashMap<String, String>();
	private Map<String, String> finishMap2 = new HashMap<String, String>();
	private FinishEntity finishEntity;

	public FinishCourseRequest(Handler handler) {
		super(handler);
		this.handler = handler;
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
		int j = 0;
		try {
			if (str != null && str.startsWith("\ufeff")) {
				str = str.substring(1);
			}
			json = new JSONObject(str);
			if (json.has("records")) {
				records = json.getString("records");
				if (!"null".equals(records)) {
					JSONArray array = new JSONArray(records);
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = new JSONObject(array.getString(i));
						finishEntity = new FinishEntity();
						finishEntity.setColCount(obj.getString("ColCount"));
						finishEntity.setId(obj.getString("Id"));
						finishEntity.setIsCombination(obj
								.getString("IsCombination"));
						finishEntity.setPaperSuitInfoId(obj
								.getString("PaperSuitInfoId"));
						finishEntity.setTm_bzda(obj.getString("Tm_bzda"));
						finishEntity.setTm_da(obj.getString("Tm_da"));
						finishEntity.setTm_dasm(obj.getString("Tm_dasm"));
						finishEntity.setTm_Nr(obj.getString("Tm_Nr"));
						finishEntity.setTm_Tx_Id(obj.getString("Tm_Tx_Id"));
						if (obj.getString("Tm_Tx_Id").equals("2")) {
							finishMap.put(j + "", obj.getString("Tm_bzda"));
							j = j + 1;
						}
						finishList.add(finishEntity);
					}
				}
			}
			if (json.has("clerk_Wdpf")) {
				finishMap2.put("clerk_Wdpf", json.getString("clerk_Wdpf"));
			}
			if (json.has("examStartId")) {
				finishMap2.put("examStartId", json.getString("examStartId"));
			}
			if (json.has("moveOutTimes")) {
				finishMap2.put("moveOutTimes", json.getString("moveOutTimes"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		handler.sendMessage(handler.obtainMessage(
				HandleMessageState.GET_FINISH_COURSE_LIST_SUCCESS, finishList));
		handler.sendMessage(handler.obtainMessage(
				HandleMessageState.GET_FINISH_DA_LIST_SUCCESS, finishMap));
		handler.sendMessage(handler.obtainMessage(
				HandleMessageState.GET_FINISH_DATA_SUCCESS, finishMap2));
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
			json.put("a", "suitDetailInfo");
			json.put("studentid", 1);
			json.put("courseId", 1272);
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
