package com.broov.player.masgb.entity;

public class UpLoadTimeInfo {
	private String studentId;
	private String startTime;
	private String endTime;
	private String wareId;
	
	
	public UpLoadTimeInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public UpLoadTimeInfo(String studentId, String startTime, String endTime,
			String wareId) {
		super();
		this.studentId = studentId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.wareId = wareId;
	}


	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getWareId() {
		return wareId;
	}
	public void setWareId(String wareId) {
		this.wareId = wareId;
	}
	
}
