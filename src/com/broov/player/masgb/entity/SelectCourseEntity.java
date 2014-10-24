package com.broov.player.masgb.entity;

public class SelectCourseEntity {
	/**
	 * 课程名称
	 */
	private String course_name;
	/**
	 * 课程主讲人
	 */
	private String course_person;
	/**
	 * 课程学分
	 */
	private String course_score;
	/**
	 * 课程类型 选修/必修
	 */
	private String course_type;
	/**
	 * 课程分类 （三中全会。。。）
	 */
	private String course_style;
	/**
	 * 课程介绍uri
	 */
	private String course_introduce_uri;
	/**
	 * 课程答疑
	 */
	private String course_ask;
	
	/**
	 * 课程ID
	 */
	private String course_id;
	public SelectCourseEntity() {
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

	public String getCourse_score() {
		return course_score;
	}

	public void setCourse_score(String course_score) {
		this.course_score = course_score;
	}

	public String getCourse_type() {
		return course_type;
	}

	public void setCourse_type(String course_type) {
		this.course_type = course_type;
	}

	public String getCourse_style() {
		return course_style;
	}

	public void setCourse_style(String course_style) {
		this.course_style = course_style;
	}

	public String getCourse_introduce_uri() {
		return course_introduce_uri;
	}

	public void setCourse_introduce_uri(String course_introduce_uri) {
		this.course_introduce_uri = course_introduce_uri;
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
