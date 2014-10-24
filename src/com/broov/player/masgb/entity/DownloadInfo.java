package com.broov.player.masgb.entity;

/**
 * 创建一个下载信息的实体类,每一个DownloadInfo保存这1个线程的下载信息 1.threadId:下载线程ID
 * 2.startPos：当前线程下载开始点 3.endPos:当前线程下载结束点 4.compeleteSize:当前线程下载了多少数据
 * 5.url:下载地址 6.fileName：文件名称
 */
public class DownloadInfo {
	private String url;// 下载器网络标识
	private String fileName;// 文件名称

	/**
	 * 1.threadId:下载线程ID 2.startPos：当前线程下载开始点 3.endPos:当前线程下载结束点
	 * 4.compeleteSize:当前线程下载了多少数据 5.url:下载地址
	 * 
	 * **/
	public DownloadInfo(String url, String fileName) {
		this.fileName = fileName;
		this.url = url;
	}
	
	

	public DownloadInfo() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}