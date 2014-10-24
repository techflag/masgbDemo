package com.broov.player.masgb.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.entity.BBSTotalEntity;
import com.broov.player.masgb.handler.ExitAppUtils;
import com.broov.player.masgb.net.request.LoginOutRequest;
import com.broov.player.masgb.net.request.SeekBBSRequest;
import com.broov.player.masgb.utils.ButtonClickUtils;
import com.broov.player.masgb.utils.HandleMessageState;

public class BBSListActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	private SeekBBSRequest seekBBSRequest;
	private BBSTotalAdapter totalAdapter;
	private List<BBSTotalEntity> totalList = new ArrayList<BBSTotalEntity>();
	private ListView bbsLV;
	private View headView;
	private Button upBBSBtn;
	private Button nextBBSBtn;
	private Button pageBBSBtn;
	private ProgressDialog progressDialog;
	private Context mContext;
	// 用于显示每列5个Item项。
	private int VIEW_COUNT = 3;
	// 用于显示页号的索引
	private int index = 0;
	private int DISMISSGAP = 30;
	private Button updateBBSBT;
	/**
	 * Dialog存在时间设置
	 */
	private Handler handlerTimer = new Handler();
	private Runnable runnableTimer = new Runnable() {
		@Override
		public void run() {
			dismissProgressDialog();
			Toast.makeText(getApplicationContext(), "信号弱，请点击刷新重试！",
					Toast.LENGTH_SHORT).show();
			handlerTimer.removeCallbacks(runnableTimer);
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleMessageState.GET_BBS_TOTAL_LIST_SUCCESS:
				totalList = (List<BBSTotalEntity>) msg.obj;
				index = 0;
				setPage(index + 1);
				adapterChangeListener();
				dismissProgressDialog();
				handlerTimer.removeCallbacks(runnableTimer);
				break;
			default:
				break;
			}
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bbs_layout);
		mContext = BBSListActivity.this;
		ExitAppUtils.getInstance().addActivity(this);
		initHolder();
		initData();
		checkButton();
	}

	/**
	 * 监听返回按键事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(BBSListActivity.this)
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
									// 常规java、c#的标准退出法，返回值为0代表正常退出
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
		}
		return false;
	}

	/**
	 * 弹出等待加载对话框
	 * 
	 * @param context
	 */
	public void playProgressDialog(Context context) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("正在加载数据...");
			progressDialog.setCancelable(true);
		}
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 消失等待加载对话框
	 */
	public void dismissProgressDialog() {
		if (progressDialog != null &&

		progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	private void initHolder() {
		updateBBSBT = (Button) findViewById(R.id.updateBBSBT);
		upBBSBtn = (Button) findViewById(R.id.upBBSBtn);
		nextBBSBtn = (Button) findViewById(R.id.nextBBSBtn);
		pageBBSBtn = (Button) findViewById(R.id.pageBBSBtn);
		bbsLV = (ListView) findViewById(R.id.bbsLV);
		headView = LayoutInflater.from(this).inflate(
				R.layout.view_bbs_total_listview_item_head, null);
		bbsLV.addHeaderView(headView);
		initLayoutParams();
	}

	private void initLayoutParams() {
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
				(int) (130 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams.setMargins((int) (15 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		upBBSBtn.setLayoutParams(buttonParams);
		nextBBSBtn.setLayoutParams(buttonParams);
		LinearLayout.LayoutParams buttonParams2 = new LinearLayout.LayoutParams(
				(int) (80 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams2.setMargins((int) (15 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		updateBBSBT.setLayoutParams(buttonParams2);
		pageBBSBtn.setLayoutParams(buttonParams2);
	}

	public void initData() {
		bbsLV.setOnItemClickListener(this);
		setPage(index + 1);
		updateBBSBT.setOnClickListener(this);
		upBBSBtn.setOnClickListener(this);
		nextBBSBtn.setOnClickListener(this);
		totalAdapter = new BBSTotalAdapter(this);
		totalAdapter.notifyDataSetChanged();
		seekBBSRequest = new SeekBBSRequest(handler, BBSListActivity.this);
		seekBBSRequest.sendRequest();
		playProgressDialog(mContext);
		handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
	}

	private void setPage(int index) {
		pageBBSBtn.setText("第" + index + "页");
	}

	protected void adapterChangeListener() {
		totalAdapter.setData(totalList);
		totalAdapter.notifyDataSetChanged();
		bbsLV.setAdapter(totalAdapter);
		checkButton();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissProgressDialog();
	}

	@Override
	public void onClick(View v) {
		if (ButtonClickUtils.isFastDoubleClick()) {
			return;
		}
		if (v == upBBSBtn) {
			upView();
		} else if (v == nextBBSBtn) {
			nextView();
		} else if (v == updateBBSBT) {
			seekBBSRequest = new SeekBBSRequest(handler, BBSListActivity.this);
			seekBBSRequest.sendRequest();
			playProgressDialog(mContext);
			handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position > 0) {
			Intent intent = new Intent(getApplicationContext(),
					BBSSecondListActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("boardId",
					totalList.get(position + index * VIEW_COUNT - 1).getId());
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}
	}

	public void upView() {
		index--;
		setPage(index + 1);
		totalAdapter.notifyDataSetChanged();
		checkButton();
	}

	public void nextView() {
		index++;
		setPage(index + 1);
		totalAdapter.notifyDataSetChanged();
		checkButton();
	}

	public void checkButton() {
		// 索引值小于等于0，表示不能向前翻页了，以经到了第一页了。
		// 将向前翻页的按钮设为不可用。
		if (index <= 0) {
			if (totalList.size() <= VIEW_COUNT) {
				upBBSBtn.setEnabled(false);
				nextBBSBtn.setEnabled(false);
			} else {
				upBBSBtn.setEnabled(false);
				nextBBSBtn.setEnabled(true);
			}
		}
		// 值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
		// 将向后翻页的按钮设为不可用。
		else if (totalList.size() - index * VIEW_COUNT <= VIEW_COUNT) {
			nextBBSBtn.setEnabled(false);
			upBBSBtn.setEnabled(true);
		}
		// 否则将2个按钮都设为可用的。
		else {
			upBBSBtn.setEnabled(true);
			nextBBSBtn.setEnabled(true);
		}

	}

	// 所有帖子：马鞍山发展论坛 Adapter
	class BBSTotalAdapter extends BaseAdapter {

		private Context mContext;

		private LayoutInflater inflater;

		private List<BBSTotalEntity> list = new ArrayList<BBSTotalEntity>();

		public BBSTotalAdapter(Context context) {
			this.mContext = context;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {

			int ori = VIEW_COUNT * index;

			if (list.size() - ori < VIEW_COUNT) {
				return list.size() - ori;
				// return VIEW_COUNT;
			} else {
				return VIEW_COUNT;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertview, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertview == null) {
				holder = new ViewHolder();
				convertview = inflater.inflate(
						R.layout.view_bbs_total_listview_item, null);
				holder.tvBoardName = (TextView) convertview
						.findViewById(R.id.tvBoardName);
				holder.tvNotes = (TextView) convertview
						.findViewById(R.id.tvNotes);
				holder.tvTopicAmount = (TextView) convertview
						.findViewById(R.id.tvTopicAmount);
				holder.tvDate = (TextView) convertview
						.findViewById(R.id.tvDate);
				holder.tvMasterNameList = (TextView) convertview
						.findViewById(R.id.tvMasterNameList);
				holder.llBBSTotal = (LinearLayout) convertview
						.findViewById(R.id.llBBSTotal);
				AbsListView.LayoutParams layoutCourseParams = new AbsListView.LayoutParams(
						LayoutParams.FILL_PARENT,
						(int) (183 * BaseActivity.heightScale));
				holder.llBBSTotal.setLayoutParams(layoutCourseParams);
				convertview.setTag(holder);
			} else {
				holder = (ViewHolder) convertview.getTag();
			}

			holder.tvBoardName.setText(list.get(position + index * VIEW_COUNT)
					.getBoardName());
			holder.tvNotes.setText(list.get(position + index * VIEW_COUNT)
					.getNotes());
			holder.tvTopicAmount.setText(list
					.get(position + index * VIEW_COUNT).getTopicAmount());
			holder.tvDate.setText(list.get(position + index * VIEW_COUNT)
					.getDate());
			holder.tvMasterNameList.setText(list.get(
					position + index * VIEW_COUNT).getMasterNameList());
			return convertview;
		}

		class ViewHolder {
			LinearLayout llBBSTotal;
			TextView tvBoardName;
			TextView tvNotes;
			TextView tvTopicAmount;
			TextView tvDate;
			TextView tvMasterNameList;
		}

		public void setData(List<BBSTotalEntity> list) {
			this.list = list;
		}

	}

}
