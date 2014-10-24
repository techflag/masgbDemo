package com.broov.player.masgb.entity;

public class GetBaseType {
	// 填空题 1
	private static String type_0 = "0";
	// 单选题 2 听力题6
	private static String type_1 = "1";
	// 多选题 3
	private static String type_2 = "2";
	// 判断题 4
	private static String type_3 = "3";
	// 问答题 5 7 8 9 10 11
	private static String type_4 = "4";
	// 匹配失败
	private static String type_error = "error";

	public static String getBaseType(String num) {
		if (num.equals("1")) {
			return type_0;
		} else if (num.equals("2") || num.equals("6")) {
			return type_1;
		} else if (num.equals("3")) {
			return type_2;
		} else if (num.equals("4")) {
			return type_3;
		} else if (num.equals("5") || num.equals("7") || num.equals("8")
				|| num.equals("9") || num.equals("10") || num.equals("11")) {
			return type_4;
		}
		return type_error;
	}
}
