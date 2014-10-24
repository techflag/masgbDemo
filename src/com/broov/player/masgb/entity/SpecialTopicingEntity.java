package com.broov.player.masgb.entity;

public class SpecialTopicingEntity {
	private String name;
	/**
	 * 总学分
	 */
	private String creditHour;
	/**
	 * 已学学分
	 */
	private String creator;

	/**
	 * 学习天数
	 */
	private String periods;
	/**
	 * 选课时间
	 */
	private String date;
	/**
	 * 专题Id
	 */
	private String studyClassId;

	public SpecialTopicingEntity() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreditHour() {
		return creditHour;
	}

	public void setCreditHour(String creditHour) {
		this.creditHour = creditHour;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getPeriods() {
		return periods;
	}

	public void setPeriods(String periods) {
		this.periods = periods;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStudyClassId() {
		return studyClassId;
	}

	public void setStudyClassId(String studyClassId) {
		this.studyClassId = studyClassId;
	}

}
