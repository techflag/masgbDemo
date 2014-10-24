package com.broov.player.masgb.entity;

public class BBSTotalEntity {
	private String Id = "";
	/**
	 * 名称 （讨论区）
	 */
	private String BoardName = "";
	/**
	 * 管理员
	 */
	private String MasterNameList = "";
	/**
	 * 最后发表
	 */
	private String date = "";
	/**
	 * 主题数
	 */
	private String TopicAmount = "";
	/**
	 * 说明（讨论区）
	 */
	private String Notes = "";

	public BBSTotalEntity() {
		super();
	}

	public BBSTotalEntity(String id, String boardName, String masterNameList,
			String date, String topicAmount, String notes) {
		super();
		Id = id;
		BoardName = boardName;
		MasterNameList = masterNameList;
		this.date = date;
		TopicAmount = topicAmount;
		Notes = notes;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getBoardName() {
		return BoardName;
	}

	public void setBoardName(String boardName) {
		BoardName = boardName;
	}

	public String getMasterNameList() {
		return MasterNameList;
	}

	public void setMasterNameList(String masterNameList) {
		MasterNameList = masterNameList;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTopicAmount() {
		return TopicAmount;
	}

	public void setTopicAmount(String topicAmount) {
		TopicAmount = topicAmount;
	}

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}

}
