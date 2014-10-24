package com.broov.player.masgb.net.request;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;

import com.broov.player.masgb.entity.SpecialTopicingDetailEntity;
import com.broov.player.masgb.net.JSONRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class GetTopicListRequest extends JSONRequest {
	private List<SpecialTopicingDetailEntity> list = new ArrayList<SpecialTopicingDetailEntity>();
	private Handler handler;
	private String id;
	private SpecialTopicingDetailEntity detailEntity;
	private int VIEW_COUNT = 4;
//	private UserBean bean = UserBean.getInstance();

	public GetTopicListRequest(Handler handler) {
		super(handler);
		this.handler = handler;
	}

	public void setId(String id) {
		this.id = id;
	}

	private void addData(JSONObject objData) {
		JSONObject obj = objData;
		detailEntity = new SpecialTopicingDetailEntity();
		try {
			detailEntity.setName(obj.getString("Name"));
			detailEntity.setCourseId(obj.getString("CourseId"));
			detailEntity.setId(obj.getString("Id"));
			if ("1".equals(obj.getString("Status"))) {
				detailEntity.setStatus("已学");
			} else {
				detailEntity.setStatus("未学");
			}

			list.add(detailEntity);
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
				HandleMessageState.GET_TOPIC_LIST_SUCCESS, list));

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
			json.put("studyClassId", id);
			json.put("a", "studyClassList");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		return json.toString();
	}

}
