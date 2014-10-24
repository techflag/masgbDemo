package com.broov.player.masgb.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

	/**
	 * 将数组转换为JSON格式的数据。
	 * @param stoneList 数据源
	 * @return JSON格式的数据
	 */
	public static String changeArrayDateToJson(List<DownloadInfo> stoneList){
		try {
			JSONArray array = new JSONArray();
			JSONObject object = new JSONObject();
			int length = stoneList.size();
			for (int i = 0; i < length; i++) {
				DownloadInfo stone = stoneList.get(i);
				String url = stone.getUrl();
				String fileName = stone.getFileName();
				JSONObject stoneObject = new JSONObject();
				stoneObject.put("url", url);
				stoneObject.put("filename", fileName);
				array.put(stoneObject);
			}
			object.put("stones", array);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将JSON转化为数组并返回。
	 * @param Json
	 * @return ArrayList<Stone>
	 */
	public static ArrayList<DownloadInfo> changeJsonToArray(String Json){
		ArrayList<DownloadInfo> gameList = new ArrayList<DownloadInfo>();
		try {
			JSONObject jsonObject = new JSONObject(Json);
			if (!jsonObject.isNull("stones")) {
				String aString = jsonObject.getString("stones");
				JSONArray aJsonArray = new JSONArray(aString);
				int length = aJsonArray.length();
				for (int i = 0; i < length; i++) {
					JSONObject stoneJson = aJsonArray.getJSONObject(i);
					String fileName = stoneJson.getString("filename");
					String url = stoneJson.getString("url");
					DownloadInfo stone = new DownloadInfo();
					stone.setFileName(fileName);
					stone.setUrl(url);
					gameList.add(stone);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gameList;
	}
}
