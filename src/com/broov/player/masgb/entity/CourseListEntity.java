package com.broov.player.masgb.entity;

public class CourseListEntity {
	/**
	 * 课件名
	 */
	private String courseName="";
	/**
	 * 课件状态 已读/未读
	 */
	private String courseStatus="";
	/**
	 * 课件id
	 */
	private String course_doc_id="";
	/**
	 * 课件uri
	 * @return
	 */
	private String uri="";
	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseStatus() {
		return courseStatus;
	}

	public void setCourseStatus(String courseStatus) {
		this.courseStatus = courseStatus;
	}

	public String getCourse_doc_id() {
		return course_doc_id;
	}

	public void setCourse_doc_id(String course_doc_id) {
		this.course_doc_id = course_doc_id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}


}
