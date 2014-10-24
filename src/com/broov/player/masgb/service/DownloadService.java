package com.broov.player.masgb.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.broov.player.masgb.entity.DownloadInfo;
import com.broov.player.masgb.entity.JsonUtil;
import com.broov.player.masgb.utils.MyIntents;

public class DownloadService extends Service {

	private DownloadManager mDownloadManager;
	// private Dao dao;
	private SharedPreferences sharedPreferences;

	@Override
	public IBinder onBind(Intent intent) {

		return new DownloadServiceImpl();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mDownloadManager = new DownloadManager(this);
	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);

		if (mDownloadManager == null) {
			mDownloadManager = new DownloadManager(this);
		}
		if (intent != null) {
			if ("com.broov.player.masgb.service.IDownloadService".equals(intent
					.getAction())) {
				String url = intent.getStringExtra(MyIntents.URL);
				String fileName = intent.getStringExtra(MyIntents.FILE_NAME);
				int type = intent.getIntExtra(MyIntents.TYPE, -1);
				switch (type) {
				case MyIntents.Types.START:
					if (!mDownloadManager.isRunning()) {
						mDownloadManager.startManage();
					} else {
						mDownloadManager.reBroadcastAddAllTask();
					}
					break;
				case MyIntents.Types.ADD:
					if (!TextUtils.isEmpty(url)
							&& !mDownloadManager.hasTask(url)) {
						mDownloadManager.addTask(url, fileName);
						if (!isHaveDown(url)) {
							makeDownloadData(url, fileName);
						}else {
						}
					}
					break;
				case MyIntents.Types.CONTINUE:
					if (!TextUtils.isEmpty(url)) {
						mDownloadManager.continueTask(url);
					}
					break;
				case MyIntents.Types.DELETE:
					if (!TextUtils.isEmpty(url)) {
						mDownloadManager.deleteTask(url);
					}
					break;
				case MyIntents.Types.PAUSE:
					if (!TextUtils.isEmpty(url)) {
						mDownloadManager.pauseTask(url);
					}
					break;
				case MyIntents.Types.STOP:
					mDownloadManager.close();
					break;

				default:
					break;
				}
			}
		}

	}

	private void makeDownloadData(String url, String fileName) {
		ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();

		sharedPreferences = getSharedPreferences("downData",
				Context.MODE_PRIVATE);
		// getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
		String jsonArrayStart = sharedPreferences.getString("json", "");
		if (!jsonArrayStart.equals(null)) {
			list = JsonUtil.changeJsonToArray(jsonArrayStart);
		}

		
		DownloadInfo dl1 = new DownloadInfo(url, fileName);
		list.add(dl1);
		String jsonArrayEnd = JsonUtil.changeArrayDateToJson(list);

		sharedPreferences = getSharedPreferences("downData",
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putString("json", jsonArrayEnd);
		editor.commit();// 提交修改
	}

	private boolean isHaveDown(String url) {
		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
		sharedPreferences = getSharedPreferences("downData",
				Context.MODE_PRIVATE);
		// getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
		String jsonArrayStart = sharedPreferences.getString("json", "");
		if (!jsonArrayStart.equals("")) {
			list = JsonUtil.changeJsonToArray(jsonArrayStart);
		}
		for (DownloadInfo info : list) {
			if (info.getUrl().equals(url)) {
				return true;
			}
		}
		return false;
	}

	private class DownloadServiceImpl extends IDownloadService.Stub {

		@Override
		public void startManage() throws RemoteException {

			mDownloadManager.startManage();
		}

		@Override
		public void addTask(String url, String fileName) throws RemoteException {

			mDownloadManager.addTask(url, fileName);
		}

		@Override
		public void pauseTask(String url) throws RemoteException {

		}

		@Override
		public void deleteTask(String url) throws RemoteException {

		}

		@Override
		public void continueTask(String url) throws RemoteException {

		}

	}

}
