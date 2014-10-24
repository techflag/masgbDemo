package com.broov.player.masgb.bean;

public class FlagBean {
	/*
	 * 1:no 2:yes
	 */
	private String isFromSelectCourse = "1";
	private boolean isRequestOk = false;
	private static FlagBean user = null;

	private FlagBean() {

	}

	public static FlagBean getInstance() {
		if (user == null) {
			user = new FlagBean();
		}
		return user;
	}

	public String getIsFromSelectCourse() {
		return isFromSelectCourse;
	}

	public void setIsFromSelectCourse(String isFromSelectCourse) {
		this.isFromSelectCourse = isFromSelectCourse;
	}

	public boolean isRequestOk() {
		return isRequestOk;
	}

	public void setRequestOk(boolean isRequestOk) {
		this.isRequestOk = isRequestOk;
	}

}
