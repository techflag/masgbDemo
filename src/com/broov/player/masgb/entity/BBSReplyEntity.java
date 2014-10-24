package com.broov.player.masgb.entity;

public class BBSReplyEntity {
	private String Id;
	/**
	 * TopicId
	 */
	private String TopicId;
	/**
	 * Subject
	 */
	private String Subject;
	/**
	 * StudentInfo_Name
	 */
	private String StudentInfo_Name;
	/**
	 * date
	 */
	private String date;

	public BBSReplyEntity() {
		super();
	}

	public BBSReplyEntity(String id, String topicId, String subject,
			String studentInfo_Name, String date) {
		super();
		Id = id;
		TopicId = topicId;
		Subject = subject;
		StudentInfo_Name = studentInfo_Name;
		this.date = date;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getTopicId() {
		return TopicId;
	}

	public void setTopicId(String topicId) {
		TopicId = topicId;
	}

	public String getSubject() {
		return Subject;
	}

	public void setSubject(String subject) {
		Subject = subject;
	}

	public String getStudentInfo_Name() {
		return StudentInfo_Name;
	}

	public void setStudentInfo_Name(String studentInfo_Name) {
		StudentInfo_Name = studentInfo_Name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
