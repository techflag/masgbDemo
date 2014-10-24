package com.broov.player.masgb.entity;

public class BBSSecondFromCourseEntity {

	/**
	 * 话题id
	 */
	private String Id;
	/**
	 * 学生id
	 */
	private String RefID;
	/**
	 * type
	 */
	private String Type;
	/**
	 * 板块名
	 */
	private String BoardName;
	/**
	 * 发起人
	 */
	private String MasterNameList;
	/**
	 * 回复数
	 */
	private String Creator;

	public BBSSecondFromCourseEntity() {
		super();
	}

	public BBSSecondFromCourseEntity(String id, String refID, String type,
			String boardName, String masterNameList, String Creator) {
		super();
		this.Id = id;
		this.RefID = refID;
		this.Type = type;
		this.BoardName = boardName;
		this.MasterNameList = masterNameList;
		this.Creator = Creator;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getRefID() {
		return RefID;
	}

	public void setRefID(String refID) {
		RefID = refID;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
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

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

}
