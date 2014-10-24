package com.broov.player.masgb.bean;

public class UserBean {
	/**
	 * 学生Id
	 */
	private String id;

	private String username;
	/**
	 * 演示地址ip
	 */
	private String uri;
	/**
	 * 课程总的数目
	 */
	private String course_total_number = "0";
	/**
	 * 返回的时间id
	 */
	private String logid = "";
	private String sessionId="";
	private static UserBean user = null;
	

	private UserBean() {

	}


	public static UserBean getInstance() {
		if (user == null) {
			user = new UserBean();
		}
		return user;
	}

	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getCourse_total_number() {
		return course_total_number;
	}

	public void setCourse_total_number(String course_total_number) {
		this.course_total_number = course_total_number;
	}

	public String getLogid() {
		return logid;
	}

	public void setLogid(String logid) {
		this.logid = logid;
	}

}
