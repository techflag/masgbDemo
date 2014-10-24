package com.broov.player.masgb.entity;

public class FinishEntity {
	/**
	 * 题目题型
	 */
	private String Tm_Tx_Id = "";
	/**
	 * 题目Id 数据库自增长的
	 */
	private String Id = "";
	/**
	 * 题目内容
	 */
	private String Tm_Nr = "";
	/**
	 * 题目可选项数目
	 */
	private String Tm_dasm = "";
	/**
	 * 题目答案
	 */
	private String Tm_bzda = "";

	/**
	 * 题目可选项
	 */
	private String Tm_da = "";

	/**
	 * 题目是否是组合题
	 */
	private String IsCombination = "";

	/**
	 * 候选项
	 */
	private String ColCount = "";
	/**
	 * 题目组Id
	 */
	private String PaperSuitInfoId = "";

	public FinishEntity() {
		super();
	}

	public String getTm_Tx_Id() {
		return Tm_Tx_Id;
	}

	public void setTm_Tx_Id(String tm_Tx_Id) {
		Tm_Tx_Id = tm_Tx_Id;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getTm_Nr() {
		return Tm_Nr;
	}

	public void setTm_Nr(String tm_Nr) {
		Tm_Nr = tm_Nr;
	}

	public String getTm_dasm() {
		return Tm_dasm;
	}

	public void setTm_dasm(String tm_dasm) {
		Tm_dasm = tm_dasm;
	}

	public String getTm_bzda() {
		return Tm_bzda;
	}

	public void setTm_bzda(String tm_bzda) {
		Tm_bzda = tm_bzda;
	}

	public String getTm_da() {
		return Tm_da;
	}

	public void setTm_da(String tm_da) {
		Tm_da = tm_da;
	}

	public String getIsCombination() {
		return IsCombination;
	}

	public void setIsCombination(String isCombination) {
		IsCombination = isCombination;
	}

	public String getColCount() {
		return ColCount;
	}

	public void setColCount(String colCount) {
		ColCount = colCount;
	}

	public String getPaperSuitInfoId() {
		return PaperSuitInfoId;
	}

	public void setPaperSuitInfoId(String paperSuitInfoId) {
		PaperSuitInfoId = paperSuitInfoId;
	}

}
