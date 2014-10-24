package com.broov.player.masgb.entity;

public class BBSListDetailOrigEntity {
	/**
	 * 标题
	 */
	private String Title = "";
	/**
	 * 帖子内容
	 */
	private String Subject = "";
	/**
	 * 帖子时间
	 */
	private String date = "";
	/**
	 * 发帖人
	 */
	private String StudentInfo_ActualName = "";

	public BBSListDetailOrigEntity() {
		super();
	}

	public BBSListDetailOrigEntity(String title, String subject, String date,
			String studentInfo_ActualName) {
		super();
		Title = title;
		Subject = subject;
		this.date = date;
		StudentInfo_ActualName = studentInfo_ActualName;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getSubject() {
		return Subject;
	}

	public void setSubject(String subject) {
		Subject = subject;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStudentInfo_ActualName() {
		return StudentInfo_ActualName;
	}

	public void setStudentInfo_ActualName(String studentInfo_ActualName) {
		StudentInfo_ActualName = studentInfo_ActualName;
	}

}
