package com.broov.player.masgb.entity;

public class SpecialTopicingDetailEntity {
	/**
	 * 课程名字
	 */
	private String name;
	/**
	 * Id
	 */
	private String id;
	/**
	 * 课程Id
	 */
	private String courseId;
	/**
	 * 课程状态 0：未学 1：已学
	 */
	private String status;
	/**
	 * 已学时长
	 */
	private String StudyTime;
	/**
	 * 应学时长
	 */
	private String Time;
	/**
	 * 老师
	 */
	private String lecture;
	/**
	 * 学分
	 */
	private String score;

	public SpecialTopicingDetailEntity() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStudyTime() {
		return StudyTime;
	}

	public void setStudyTime(String studyTime) {
		String tenmp_course_length = "0";
		if ("null" != studyTime) {
			long xcourse_length = Integer.parseInt(studyTime);
			tenmp_course_length = String.valueOf(xcourse_length / 60);
		}
		this.StudyTime = tenmp_course_length;

	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		String tenmp_course_length = "0";
		if ("null" != time) {
			long xcourse_length = Integer.parseInt(time);
			tenmp_course_length = String.valueOf(xcourse_length / 60);
		}
		this.Time = tenmp_course_length;
	}

	public String getLecture() {
		return lecture;
	}

	public void setLecture(String lecture) {
		this.lecture = lecture;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

}
