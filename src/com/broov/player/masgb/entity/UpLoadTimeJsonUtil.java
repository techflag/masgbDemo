package com.broov.player.masgb.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UpLoadTimeJsonUtil {

	/**
	 * 将数组转换为JSON格式的数据。
	 * 
	 * @param stoneList
	 *            数据源
	 * @return JSON格式的数据
	 */
	public static String changeArrayDateToJson(
			List<UpLoadTimeInfo> studytimeList) {
		try {
			JSONArray array = new JSONArray();
			int length = studytimeList.size();
			for (int i = 0; i < length; i++) {
				UpLoadTimeInfo studytime = studytimeList.get(i);
				String studentId = studytime.getStudentId();
				String wareID = studytime.getWareId();
				String startTime = studytime.getStartTime();
				String endTime = studytime.getEndTime();
				JSONObject studytimeObject = new JSONObject();
				studytimeObject.put("studentid", studentId);
				studytimeObject.put("wareid", wareID);
				studytimeObject.put("starttime", startTime);
				studytimeObject.put("endtime", endTime);
				array.put(studytimeObject);
			}
			return array.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将JSON转化为数组并返回。
	 * 
	 * @param Json
	 * @return ArrayList<Stone>
	 */
	public static ArrayList<UpLoadTimeInfo> changeJsonToArray(String Json) {
		ArrayList<UpLoadTimeInfo> gameList = new ArrayList<UpLoadTimeInfo>();
		JSONArray aJsonArray = null;
		try {
			aJsonArray = new JSONArray(Json);
			if (aJsonArray != null) {
				int length = aJsonArray.length();
				for (int i = 0; i < length; i++) {
					JSONObject stoneJson = aJsonArray.getJSONObject(i);
					String studentId = stoneJson.getString("studentid");
					String wareId = stoneJson.getString("wareid");
					String startTime = stoneJson.getString("starttime");
					String endTime = stoneJson.getString("endtime");
					UpLoadTimeInfo stone = new UpLoadTimeInfo();
					stone.setStudentId(studentId);
					stone.setWareId(wareId);
					stone.setStartTime(startTime);
					stone.setEndTime(endTime);
					gameList.add(stone);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gameList;
	}
}
