package com.broov.player.masgb.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.entity.NetworkProber;
import com.broov.player.masgb.handler.ExitAppUtils;
import com.broov.player.masgb.net.request.LoginOutRequest;

public class DeleteDocActivity extends BaseActivity implements OnClickListener {

	private ListView fileLv;// 文件列表显示的ListView
	private List<HashMap<String, Object>> data;// 填充的数据
	private File root;// 文件夹根节点
	private File[] currentFiles; // 根节点下的所有文件（包括文件夹）
	private File currentPosFile;
	private View headView;
	// Adapter中ICON和Filename键值对常量
	private static final String ICON = "icon";
	private static final String FILENAME = "filename";
	private FileListAdapter adapter;
	private Handler handler = new Handler() {
	};
	private Button pageDeleteDocBtn;
	private Button upDeleteDocBtn;
	private Button nextDeleteDocBtn;
	// 用于显示每列5个Item项。
	private int VIEW_COUNT = 4;
	// 用于显示页号的索引
	private int index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deletedocvideo2);
		ExitAppUtils.getInstance().addActivity(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// initDetailInfo();
		initHolder();
		initData();
		checkButton();
	}

	private void setPage(int index) {
		pageDeleteDocBtn.setText("第" + index + "页");
	}

	public void checkButton() {
		// 索引值小于等于0，表示不能向前翻页了，以经到了第一页了。
		// 将向前翻页的按钮设为不可用。
		if (index <= 0) {
			if (data.size() <= VIEW_COUNT) {
				upDeleteDocBtn.setEnabled(false);
				nextDeleteDocBtn.setEnabled(false);
			} else {
				upDeleteDocBtn.setEnabled(false);
				nextDeleteDocBtn.setEnabled(true);
			}
		}
		// 值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
		// 将向后翻页的按钮设为不可用。
		else if (data.size() - index * VIEW_COUNT <= VIEW_COUNT) {
			nextDeleteDocBtn.setEnabled(false);
			upDeleteDocBtn.setEnabled(true);
		}
		// 否则将2个按钮都设为可用的。
		else {
			upDeleteDocBtn.setEnabled(true);
			nextDeleteDocBtn.setEnabled(true);
		}

	}

	private void initData() {
		setPage(index + 1);
		upDeleteDocBtn.setOnClickListener(this);
		nextDeleteDocBtn.setOnClickListener(this);
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			root = getFileSystemRoot();
			root = new File(root.getPath() + "/干部在线");
			// 得到第一屏的信息
			if (root != null) {
				// 从/mnt/sdcard下得到文件列表
				data = getFileListFromSdcard(root);
			} else {
				// 如果没有挂载sdcard，则提示用户
				data = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(ICON, R.drawable.icon);
				map.put(FILENAME, "请插入SDcard！");
				data.add(map);
			}
			adapter = new FileListAdapter(this);
			fileLv.setAdapter(adapter);
		} else {
			Toast.makeText(this, "未发现SD卡", Toast.LENGTH_LONG).show();
			return;
		}
	}

	private void initHolder() {
		pageDeleteDocBtn = (Button) findViewById(R.id.pageDeleteDocBtn);
		upDeleteDocBtn = (Button) findViewById(R.id.upDeleteDocBtn);
		nextDeleteDocBtn = (Button) findViewById(R.id.nextDeleteDocBtn);
		fileLv = (ListView) findViewById(R.id.fileLv);
		if (!(fileLv.getHeaderViewsCount() > 0)) {
			headView = LayoutInflater.from(this).inflate(
					R.layout.file_list_item_head, null);
			fileLv.addHeaderView(headView);
			if (getIntent().getStringExtra("from").equals("LoginActivity")) {
				TextView offLine = (TextView) headView
						.findViewById(R.id.offline);
				offLine.setVisibility(View.VISIBLE);
			}
			if (getIntent().getStringExtra("from").equals("offlinevideo")) {
				Toast.makeText(getApplicationContext(),
						"下次有网络情况下登录时，将更新此课程播放时间", Toast.LENGTH_LONG).show();
			}
		}
		initLayoutParams();
	}

	private void initLayoutParams() {
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
				(int) (130 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams.setMargins((int) (40 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		upDeleteDocBtn.setLayoutParams(buttonParams);
		nextDeleteDocBtn.setLayoutParams(buttonParams);
		LinearLayout.LayoutParams buttonParams2 = new LinearLayout.LayoutParams(
				(int) (80 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams2.setMargins((int) (30 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		pageDeleteDocBtn.setLayoutParams(buttonParams2);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (getIntent().getStringExtra("from").equals("main")
					|| getIntent().getStringExtra("from").equals(
							"LoginActivity")) {
				new AlertDialog.Builder(DeleteDocActivity.this)
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
										// System.exit(0); //
										// 常规java、c#的标准退出法，返回值为0代表正常退出//
										ExitAppUtils.getInstance().exit();
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

	/**
	 * 得到Sdcard中的文件列表
	 * 
	 * @return
	 */
	private List<HashMap<String, Object>> getFileListFromSdcard(File root) {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		currentFiles = root.listFiles();// 列出当前目录下的所有文件和目录
		for (File f : currentFiles) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String fileName = f.getName();
			if (!fileName.endsWith("download")) {
				map.put(FILENAME, fileName);
				list.add(map);
			}
		}
		// 把原来的data list清空，然后把list放进去，再通知adapter
		if (data != null) {
			data.clear();
			data.addAll(list);
			adapter.notifyDataSetChanged();
		}
		return list;
	}

	private File getFileSystemRoot() {
		// 首先得到Sd卡是否加载了
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// 得到sd卡路径 root --> /mnt/sdcard
			root = Environment.getExternalStorageDirectory();
		} else {
			Toast.makeText(this, "请插入SDcard！", Toast.LENGTH_LONG).show();
		}
		return root;
	}

	public void reFreshActivity() {
		if (DeleteDocActivity.this != null) {
			onCreate(null);
		}
	}

	private class FileListAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater inflater;

		public FileListAdapter(Context context) {
			this.mContext = context;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			int ori = VIEW_COUNT * index;

			if (data.size() - ori < VIEW_COUNT) {
				return data.size() - ori;
				// return VIEW_COUNT;
			} else {
				return VIEW_COUNT;
			}
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("unused")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (holder == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.file_list_item, null);
				holder.layoutFileItem = (LinearLayout) convertView
						.findViewById(R.id.layoutFileItem);
				holder.filename = (TextView) convertView
						.findViewById(R.id.filename);
				holder.btnDeleteCourse = (Button) convertView
						.findViewById(R.id.btnDeleteCourse);
				holder.btnWatchCourse = (Button) convertView
						.findViewById(R.id.btnWatchCourse);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.filename.setText(data.get(position + index * VIEW_COUNT)
					.get(FILENAME).toString());
			// if (getIntent().getStringExtra("from").equals("LoginActivity")) {
			// holder.btnWatchCourse.setVisibility(View.VISIBLE);
			// }
			holder.btnDeleteCourse.setOnClickListener(new ButtonClickListener(
					position + index * VIEW_COUNT, holder));
			holder.btnWatchCourse.setOnClickListener(new ButtonClickListener(
					position + index * VIEW_COUNT, holder));
			AbsListView.LayoutParams layoutCourseParams = new AbsListView.LayoutParams(
					LayoutParams.FILL_PARENT,
					(int) (120 * BaseActivity.heightScale));
			holder.layoutFileItem.setLayoutParams(layoutCourseParams);
			return convertView;
		}

		class ViewHolder {
			LinearLayout layoutFileItem;
			TextView filename;
			Button btnDeleteCourse;
			Button btnWatchCourse;
		}

		class ButtonClickListener implements OnClickListener {
			@SuppressWarnings("unused")
			private String wareid;
			private int position;

			private ViewHolder holder;

			public ButtonClickListener(int position, ViewHolder holder) {
				this.position = position;
				this.holder = holder;
			}

			@Override
			public void onClick(View v) {
				if (v == holder.btnDeleteCourse) {
					currentPosFile = currentFiles[position];
					new AlertDialog.Builder(DeleteDocActivity.this)
							.setTitle("注意!")
							.setMessage("确定要删除此文件吗？")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (currentPosFile.delete()) {
												// 更新文件列表
												data = getFileListFromSdcard(root);
												Toast.makeText(
														DeleteDocActivity.this,
														"删除成功！",
														Toast.LENGTH_LONG)
														.show();
												// reFreshActivity();
												adapter.notifyDataSetChanged();
											} else {
												Toast.makeText(
														DeleteDocActivity.this,
														"删除失败！",
														Toast.LENGTH_LONG)
														.show();
											}
										}
									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									}).show();
				} else if (v == holder.btnWatchCourse) {
					if (NetworkProber
							.isNetworkAvailable(getApplicationContext())) {
						Intent intent1 = new Intent(getApplicationContext(),
								VideoPlayerActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("videofilename",
								"/storage/sdcard0/干部在线/"
										+ data.get(position).get(FILENAME)
												.toString());
						bundle.putString("wareid", makeWareId(data
								.get(position).get(FILENAME).toString()));
						intent1.putExtras(bundle);
						intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(intent1);
					} else {
						Intent intent2 = new Intent(getApplicationContext(),
								VideoPlayerOffLineActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("videofilename",
								"/storage/sdcard0/干部在线/"
										+ data.get(position).get(FILENAME)
												.toString());
						intent2.putExtras(bundle);
						intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(intent2);
					}
				}
			}

			private String makeWareId(String fileName) {
				if (fileName != null) {
					fileName = fileName.replace(".", "@@@");
					String[] data = fileName.split("@@@");
					if (data.length > 1) {
						fileName = data[1];
						return fileName;
					} else {
						fileName = "0";
					}
				}
				return "0";

			}
		}

	}

	@Override
	public void onClick(View v) {
		if (v == upDeleteDocBtn) {
			upView();
		} else if (v == nextDeleteDocBtn) {
			nextView();
		}
	}

	public void upView() {
		index--;
		setPage(index + 1);
		adapter.notifyDataSetChanged();
		checkButton();
	}

	public void nextView() {
		index++;
		setPage(index + 1);
		adapter.notifyDataSetChanged();
		checkButton();
	}
}
