package com.broov.player.masgb.service;

interface IDownloadService {
	
	void startManage();
	
	void addTask(String url,String fileName);
	
	void pauseTask(String url);
	
	void deleteTask(String url);
	
	void continueTask(String url);
}
