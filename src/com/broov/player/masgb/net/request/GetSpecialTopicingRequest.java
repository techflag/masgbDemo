package com.broov.player.masgb.net.request;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.entity.SpecialTopicingEntity;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class GetSpecialTopicingRequest extends JSONRequest {
	private Handler handler;
	private List<SpecialTopicingEntity> specialTopicList = new ArrayList<SpecialTopicingEntity>();
//	private String stuplyid;
//	private UserBean bean = UserBean.getInstance();
	private SpecialTopicingEntity specialTopicingEntity;
	private int VIEW_COUNT = 3;

	public GetSpecialTopicingRequest(Handler handler) {
		super(handler);
		this.handler = handler;
	}

	private void addData(JSONObject objData) {
		JSONObject obj = objData;
		try {
			specialTopicingEntity = new SpecialTopicingEntity();
			specialTopicingEntity.setCreator(obj.getString("Creator"));
			specialTopicingEntity.setCreditHour(obj.getString("CreditHour"));
			JSONObject objDate = new JSONObject(obj.getString("CreatedTime"));
			specialTopicingEntity.setDate(objDate.getString("date"));
			specialTopicingEntity.setName(obj.getString("Name"));
			specialTopicingEntity.setPeriods(obj.getString("Periods"));
			specialTopicingEntity.setStudyClassId(obj.getString("Id"));
			specialTopicList.add(specialTopicingEntity);
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
								specialTopicingEntity = new SpecialTopicingEntity();
								specialTopicingEntity
										.setStudyClassId("00000000");
								specialTopicList.add(specialTopicingEntity);
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
				HandleMessageState.GET_SPECIAL_TOPICING_SUCCESS,
				specialTopicList));

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
			json.put("a", "onStudyClassList");
			// json.put("studentid",bean.getId() );
			json.put("studentid", 4);
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
