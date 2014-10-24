package com.broov.player.masgb.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConfigUtils {

	public static final String PREFERENCE_NAME = "com.yyxu.download";

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_WORLD_WRITEABLE);
	}

	public static Map<String, String> getString(Context context, String key1,
			String key2) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Map<String, String> dataMap = new HashMap<String, String>();
			dataMap.put(key1, preferences.getString(key1, ""));
			dataMap.put(key2, preferences.getString(key2, ""));
			return dataMap;
		} else
			return null;
	}

	/**
	 * @param context
	 * @param key1
	 *            url键
	 * @param key2
	 *            fileName键
	 * @param value1
	 *            url值
	 * @param value2
	 *            fileName值
	 */
	public static void setString(Context context, String key1, String key2,
			String value1, String value2) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putString(KEY_URI, value1);
			editor.putString(KEY_NAME, value2);
			editor.commit();
		}
	}

	public static final int URL_COUNT = 3;
	public static final String KEY_URI = "uri";
	public static final String KEY_URL = "url";
	public static final String KEY_NAME = "name";
	public static final String KEY_FILENAME = "fileName";

	public static void storeURL(Context context, int index, String url,
			String fileName) {
		setString(context, KEY_URL + index, KEY_FILENAME + index, url, fileName);
	}

	public static void clearURL(Context context, int index) {
		setString(context, KEY_URL + index, KEY_FILENAME + index, "", "");
	}

	public static Map<String, String> getURL(Context context, int index) {
		return getString(context, KEY_URL + index, KEY_FILENAME + index);
	}

	public static List<Map<String, String>> getURLArray(Context context) {
		// List<String> urlList = new ArrayList<String>();
		List<Map<String, String>> urlList = new ArrayList<Map<String, String>>();
		for (int i = 0; i < URL_COUNT; i++) {
			if ((getURL(context, i)) != null) {
				urlList.add(getString(context, KEY_URL + i, KEY_FILENAME + i));
			}
		}
		return urlList;
	}

	public static final String KEY_RX_WIFI = "rx_wifi";
	public static final String KEY_TX_WIFI = "tx_wifi";
	public static final String KEY_RX_MOBILE = "tx_mobile";
	public static final String KEY_TX_MOBILE = "tx_mobile";
	public static final String KEY_Network_Operator_Name = "operator_name";

	public static int getInt(Context context, String key) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null)
			return preferences.getInt(key, 0);
		else
			return 0;
	}

	public static void setInt(Context context, String key, int value) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putInt(key, value);
			editor.commit();
		}
	}

	public static long getLong(Context context, String key) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null)
			return preferences.getLong(key, 0L);
		else
			return 0L;
	}

	public static void setLong(Context context, String key, long value) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putLong(key, value);
			editor.commit();
		}
	}

	public static void addLong(Context context, String key, long value) {
		setLong(context, key, getLong(context, key) + value);
	}
}
