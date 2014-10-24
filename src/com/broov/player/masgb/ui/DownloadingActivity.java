package com.broov.player.masgb.ui;

import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.entity.DownloadInfo;
import com.broov.player.masgb.entity.JsonUtil;
import com.broov.player.masgb.handler.ExitAppUtils;
import com.broov.player.masgb.net.request.LoginOutRequest;
import com.broov.player.masgb.utils.MyIntents;
import com.broov.player.masgb.utils.StorageUtils;
import com.broov.player.masgb.widgets.DownloadListAdapter;
import com.broov.player.masgb.widgets.ViewHolder;

public class DownloadingActivity extends BaseActivity {
	private ListView downloadList;
	private DownloadListAdapter downloadListAdapter;
	private MyReceiver mReceiver;
	private View headView;
	public static DownloadingActivity downloadingActivity;
	private Handler handler = new Handler() {
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_downloading_list);
		downloadingActivity = this;
		if (!StorageUtils.isSDCardPresent()) {
			Toast.makeText(this, "未发现SD卡", Toast.LENGTH_LONG).show();
			return;
		}

		if (!StorageUtils.isSdCardWrittenable()) {
			Toast.makeText(this, "SD卡不能读写", Toast.LENGTH_LONG).show();
			return;
		}
		try {
			StorageUtils.mkdir();
		} catch (IOException e) {
			e.printStackTrace();
		}
		initHolder();
		initData();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * 监听返回按键事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (getIntent().getStringExtra("from").equals("main")
					|| getIntent().getStringExtra("from").equals(
							"LoginActivity")) {
				new AlertDialog.Builder(DownloadingActivity.this)
						.setTitle("注意!")
						.setMessage("确定退出应用？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										LoginOutRequest loginOutRequest = new LoginOutRequest(
												handler);
										loginOutRequest.sendRequest();
										// android.os.Process
										// .killProcess(android.os.Process
										// .myPid()); // 获取PID
										ExitAppUtils.getInstance().exit();
										// System.exit(0); //
										// 常规java、c#的标准退出法，返回值为0代表正常退出
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			} else if (getIntent().getStringExtra("from").equals(
					"courseDocListActivity")) {
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}
		}
		return false;
	}

	private void initData() {

		downloadListAdapter = new DownloadListAdapter(this);
		// downloadListAdapter.setDataList(getDataList(getData()));
		mReceiver = new MyReceiver();
		Intent downloadIntent = new Intent(
				"com.broov.player.masgb.service.IDownloadService");
		downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.START);
		startService(downloadIntent);
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.broov.player.masgb.ui.DownloadingActivity");
		registerReceiver(mReceiver, filter);

		if (!getData().equals(null)) {
			for (int i = 0; i < getData().size(); i++) {
				// downloadListAdapter.addItem(getData().get(i)
				// .getUrl(), getData().get(i)
				// .getFileName());
				Intent downloadlistIntent = new Intent(
						"com.broov.player.masgb.service.IDownloadService");
				downloadlistIntent
						.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
				downloadlistIntent.putExtra(MyIntents.URL, getData().get(i)
						.getUrl());
				downloadlistIntent.putExtra(MyIntents.FILE_NAME,
						getData().get(i).getFileName());
				startService(downloadlistIntent);

			}
			// downloadListAdapter.notifyDataSetChanged();
		}
		downloadList.setAdapter(downloadListAdapter);
	}

	private ArrayList<DownloadInfo> getData() {
		ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
		SharedPreferences sharedPreferences = getSharedPreferences("downData",
				Context.MODE_PRIVATE);
		// getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
		String jsonArrayStart = sharedPreferences.getString("json", "");
		if (!jsonArrayStart.equals(null)) {
			list = JsonUtil.changeJsonToArray(jsonArrayStart);
			return list;
		}
		return null;
	}

	// private ArrayList<HashMap<Integer, String>>
	// getDataList(ArrayList<DownloadInfo> list){
	// ArrayList<HashMap<Integer, String>> dataList=new
	// ArrayList<HashMap<Integer,String>>() ;
	// for(DownloadInfo info:list){
	// HashMap<Integer, String> item = ViewHolder.getItemDataMap(info.getUrl(),
	// info.getFileName(), null, null, false + "");
	// dataList.add(item);
	// }
	// return dataList;
	// }
	//

	private void initHolder() {
		downloadList = (ListView) findViewById(R.id.listview);
		if (!(downloadList.getHeaderViewsCount() > 0)) {
			headView = LayoutInflater.from(this).inflate(
					R.layout.current_item_head, null);
			downloadList.addHeaderView(headView);
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	public class MyReceiver extends BroadcastReceiver {
		private ArrayList<DownloadInfo> list;

		@Override
		public void onReceive(Context context, Intent intent) {

			handleIntent(intent);

		}

		public void removeDownList(ArrayList<DownloadInfo> list, String url) {
			String tmp;
			for (int i = 0; i < list.size(); i++) {
				tmp = list.get(i).getUrl();
				if (tmp.equals(url)) {
					list.remove(i);
				}
			}

			String jsonArrayEnd = JsonUtil.changeArrayDateToJson(list);

			SharedPreferences sharedPreferences = DownloadingActivity.this
					.getSharedPreferences("downData", Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();// 获取编辑器
			editor.putString("json", jsonArrayEnd);
			editor.commit();// 提交修改
		}

		private ArrayList<DownloadInfo> getData() {
			ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
			SharedPreferences sharedPreferences = DownloadingActivity.this
					.getSharedPreferences("downData", Context.MODE_PRIVATE);
			// getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
			String jsonArrayStart = sharedPreferences.getString("json", "");
			if (!jsonArrayStart.equals(null)) {
				list = JsonUtil.changeJsonToArray(jsonArrayStart);
				return list;
			}
			return null;
		}

		private void handleIntent(Intent intent) {
			if (intent != null
					&& intent.getAction().equals(
							"com.broov.player.masgb.ui.DownloadingActivity")) {
				int type = intent.getIntExtra(MyIntents.TYPE, -1);
				String url = intent.getStringExtra(MyIntents.URL);
				String fileName = intent.getStringExtra(MyIntents.FILE_NAME);
				switch (type) {
				case MyIntents.Types.ADD:

					// url = intent.getStringExtra(MyIntents.URL);
					// fileName=intent.getStringExtra(MyIntents.FILE_NAME);
					boolean isPaused = intent.getBooleanExtra(
							MyIntents.IS_PAUSED, false);
					if (!TextUtils.isEmpty(url)) {
						downloadListAdapter.addItem(url, fileName, isPaused);
					}
					break;
				case MyIntents.Types.COMPLETE:
					// url = intent.getStringExtra(MyIntents.URL);
					if (!TextUtils.isEmpty(url)) {

						downloadListAdapter.removeItem(url);
						list = getData();
						removeDownList(list, url);
					}
					break;
				case MyIntents.Types.PROCESS:
					// url = intent.getStringExtra(MyIntents.URL);
					// fileName=intent.getStringExtra(MyIntents.FILE_NAME);
					View taskListItem = downloadList.findViewWithTag(url);
					ViewHolder viewHolder = new ViewHolder(taskListItem);
					viewHolder.setData(url, fileName,
							intent.getStringExtra(MyIntents.PROCESS_SPEED),
							intent.getStringExtra(MyIntents.PROCESS_PROGRESS));
					break;
				case MyIntents.Types.ERROR:
					// url = intent.getStringExtra(MyIntents.URL);
					break;
				default:
					break;
				}
			}
		}

	}

}
