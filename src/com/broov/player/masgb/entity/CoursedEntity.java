package com.broov.player.masgb.entity;

public class CoursedEntity {
	/**
	 * 课程名称
	 */
	private String course_name;
	/**
	 * 主讲人
	 */
	private String course_person;
	/**
	 * 完成日期
	 */
	private String course_time;
	/**
	 * 应学时长
	 */
	private String course_time_length;
	/**
	 * 已学时长
	 */
	private String course_length;
	/**
	 * 课程学分
	 */
	private String course_score;
	/**
	 * 类型
	 */
	private String course_type;

	/**
	 * 课程网址
	 */
	private String course_uri;
	/**
	 * 课程答疑
	 */
	private String course_ask;
	/**
	 * 课程ID
	 */
	private String course_id;

	public CoursedEntity() {
		super();
	}

	public String getCourse_name() {
		return course_name;
	}

	public void setCourse_name(String course_name) {
		this.course_name = course_name;
	}

	public String getCourse_person() {
		return course_person;
	}

	public void setCourse_person(String course_person) {
		this.course_person = course_person;
	}

	public String getCourse_time() {
		return course_time;
	}

	public void setCourse_time(String course_time) {
		this.course_time = course_time;
	}

	public String getCourse_time_length() {
		return course_time_length;
	}

	public void setCourse_time_length(String course_time_length) {
		this.course_time_length = course_time_length;
	}

	public String getCourse_length() {
		return course_length;
	}

	public void setCourse_length(String course_length) {
		// String tenmp_course_length="0";
		// if("null"!=course_length){
		// long xcourse_length = Integer.parseInt(course_length);
		// tenmp_course_length = String.valueOf(xcourse_length/60);
		// }
		//
		// this.course_length = tenmp_course_length;
		this.course_length = course_length;

	}

	public String getCourse_score() {
		return course_score;
	}

	public void setCourse_score(String course_score) {
		// if(".5"==course_score){
		// this.course_score = "0"+course_score;
		// }else{
		// this.course_score = course_score;
		// }
		this.course_score = course_score;
	}

	// public String isPoint(String xx){
	//
	// String[] xx.split(".");
	// return null;
	// }

	public String getCourse_type() {
		return course_type;
	}

	public void setCourse_type(String course_type) {
		this.course_type = course_type;
	}

	public String getCourse_uri() {
		return course_uri;
	}

	public void setCourse_uri(String course_uri) {
		this.course_uri = course_uri;
	}

	public String getCourse_ask() {
		return course_ask;
	}

	public void setCourse_ask(String course_ask) {
		this.course_ask = course_ask;
	}

	public String getCourse_id() {
		return course_id;
	}

	public void setCourse_id(String course_id) {
		this.course_id = course_id;
	}

}
