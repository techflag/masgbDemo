package com.broov.player.masgb.entity;

public class BBSSecondTotalEntity {
	/**
	 * Id 话题ID
	 */
	private String Id = "";
	/**
	 * BoardID 板块ID
	 */
	private String BoardID = "";
	/**
	 * Title 题目
	 */
	private String Title = "";
	/**
	 * ReplayCount 回复数
	 */
	private String ReplayCount = "";
	/**
	 * StudentInfo_ActualName 发起人
	 */
	private String StudentInfo_ActualName = "";

	public BBSSecondTotalEntity() {
		super();
	}

	public BBSSecondTotalEntity(String id, String boardID, String title,
			String replayCount, String studentInfo_ActualName) {
		super();
		Id = id;
		BoardID = boardID;
		Title = title;
		ReplayCount = replayCount;
		StudentInfo_ActualName = studentInfo_ActualName;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getBoardID() {
		return BoardID;
	}

	public void setBoardID(String boardID) {
		BoardID = boardID;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getReplayCount() {
		return ReplayCount;
	}

	public void setReplayCount(String replayCount) {
		ReplayCount = replayCount;
	}

	public String getStudentInfo_ActualName() {
		return StudentInfo_ActualName;
	}

	public void setStudentInfo_ActualName(String studentInfo_ActualName) {
		StudentInfo_ActualName = studentInfo_ActualName;
	}

}
