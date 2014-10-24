package com.broov.player.masgb.entity;

public class PlanCategoryListEntity {
	/**
	 * 计划名字
	 */
	private String name = "";
	/**
	 * 计划id
	 */
	private String id = "";

	public PlanCategoryListEntity() {
		super();
	}

	public PlanCategoryListEntity(String name, String id) {
		this.name = name;
		this.id = id;
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

}
