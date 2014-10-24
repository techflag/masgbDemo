package com.broov.player.masgb.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.entity.BBSSecondFromCourseEntity;
import com.broov.player.masgb.entity.BBSSecondTotalEntity;
import com.broov.player.masgb.handler.ExitAppUtils;
import com.broov.player.masgb.net.request.SeekBBSSecondFromCourseRequest;
import com.broov.player.masgb.net.request.SeekBBSSecondOfMyBBSRequest;
import com.broov.player.masgb.net.request.SeekBBSSecondRequest;
import com.broov.player.masgb.net.request.SendNewBBSRequest;
import com.broov.player.masgb.utils.ButtonClickUtils;
import com.broov.player.masgb.utils.HandleMessageState;

public class BBSSecondListFromCourseActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	@SuppressWarnings("unused")
	private SeekBBSSecondFromCourseRequest seekBBSSecondFromCourseRequest;
	private BBSTotalAdapter totalAdapter;
	private List<BBSSecondTotalEntity> totalList = new ArrayList<BBSSecondTotalEntity>();
	private List<BBSSecondFromCourseEntity> totalList2 = new ArrayList<BBSSecondFromCourseEntity>();
	private ListView bbsLV;
	private SeekBBSSecondRequest seekBBSRequest;
	private SeekBBSSecondOfMyBBSRequest bbsSecondOfMyBBSRequest;
	private View headView;
	private Button upBBSBtn;
	private Button nextBBSBtn;
	private Button pageBBSBtn;
	private Button btnAllBBS;
	private Button btnMyBBS;
	private Button btnAddBBS;
	private EditText edtSendTitle;
	private EditText edtSendContentInSecond;
	private Button btnSendInSecond;
	private Button btnClearInSecond;
	private LinearLayout llEditBBS;
	private LinearLayout llListView;
	private ProgressDialog progressDialog;
	private Context mContext;
	private int DISMISSGAP = 30;
	private Button updateBBSBT;
	private String boardId;
	// 用于显示页号的索引
	private int index = 0;
	// 标识
	private String ALLBBS = "allbbs";
	private boolean getBoarId = false;
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
			case HandleMessageState.GET_BBS_SECOND_TOTAL_LIST_FROM_COURSE_SUCCESS:
				index = 0;
				totalList2 = (List<BBSSecondFromCourseEntity>) msg.obj;
				// dismissProgressDialog();
				// handlerTimer.removeCallbacks(runnableTimer);
				if (totalList2.size() > 0) {
					setButtonBackGround(0);
					boardId = totalList2.get(0).getId();
					getBoarId = true;
					seekBBSRequest = new SeekBBSSecondRequest(handler, boardId,
							index + 1 + "", 20 + "");
					seekBBSRequest.sendRequest();
					playProgressDialog(mContext);
					handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
				}

				break;
			case HandleMessageState.GET_BBS_SECOND_TOTAL_LIST_SUCCESS:
				totalList = (List<BBSSecondTotalEntity>) msg.obj;
				adapterChangeListener();
				dismissProgressDialog();
				handlerTimer.removeCallbacks(runnableTimer);
				break;
			case HandleMessageState.ADD_NEW_BBS_SUCCESS:
				dismissProgressDialog();
				edtSendContentInSecond.setText("");
				edtSendTitle.setText("");
				setButtonBackGround(0);
				llEditBBS.setVisibility(View.GONE);
				llListView.setVisibility(View.VISIBLE);
				ALLBBS = "allbbs";
				index = 0;
				if (getBoarId) {
					seekBBSRequest = new SeekBBSSecondRequest(handler, boardId,
							index + 1 + "", 20 + "");
					seekBBSRequest.sendRequest();
					playProgressDialog(mContext);
					setPage(index + 1);
					handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
				}
			default:
				break;
			}
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bbs_second_layout);
		mContext = BBSSecondListFromCourseActivity.this;
		ExitAppUtils.getInstance().addActivity(this);
		setSubPageTitle("咨询答疑", true);
		initHolder();
		initData();
	}

	/**
	 * 监听返回按键事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

		} else if (keyCode == KeyEvent.KEYCODE_HOME) {

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
		llEditBBS = (LinearLayout) findViewById(R.id.llEditBBS);
		llListView = (LinearLayout) findViewById(R.id.llListView);
		btnSendInSecond = (Button) findViewById(R.id.btnSendInSecond);
		btnClearInSecond = (Button) findViewById(R.id.btnClearInSecond);
		edtSendTitle = (EditText) findViewById(R.id.edtSendTitle);
		edtSendContentInSecond = (EditText) findViewById(R.id.edtSendContentInSecond);
		btnAllBBS = (Button) findViewById(R.id.btnAllBBS);
		btnMyBBS = (Button) findViewById(R.id.btnMyBBS);
		btnAddBBS = (Button) findViewById(R.id.btnAddBBS);
		updateBBSBT = (Button) findViewById(R.id.updateBBSBT2);
		upBBSBtn = (Button) findViewById(R.id.upBBSBtn2);
		nextBBSBtn = (Button) findViewById(R.id.nextBBSBtn2);
		pageBBSBtn = (Button) findViewById(R.id.pageBBSBtn2);
		bbsLV = (ListView) findViewById(R.id.bbsSecondLV);
		headView = LayoutInflater.from(this).inflate(
				R.layout.view_bbs_second_total_listview_item_head, null);
		bbsLV.addHeaderView(headView);
		initLayoutParams();
	}

	private void setPage(int index) {
		pageBBSBtn.setText("第" + index + "页");
	}

	private void setButtonBackGround(int index) {
		switch (index) {
		case 0:
			btnAllBBS.setBackgroundResource(R.drawable.button_normal);
			btnMyBBS.setBackgroundResource(R.drawable.button_press);
			btnAddBBS.setBackgroundResource(R.drawable.button_press);
			break;
		case 1:
			btnAllBBS.setBackgroundResource(R.drawable.button_press);
			btnMyBBS.setBackgroundResource(R.drawable.button_normal);
			btnAddBBS.setBackgroundResource(R.drawable.button_press);
			break;
		case 2:
			btnAllBBS.setBackgroundResource(R.drawable.button_press);
			btnMyBBS.setBackgroundResource(R.drawable.button_press);
			btnAddBBS.setBackgroundResource(R.drawable.button_normal);
			break;
		default:
			break;
		}

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
		btnSendInSecond.setOnClickListener(this);
		btnClearInSecond.setOnClickListener(this);
		btnAllBBS.setOnClickListener(this);
		btnMyBBS.setOnClickListener(this);
		btnAddBBS.setOnClickListener(this);
		bbsLV.setOnItemClickListener(this);
		bbsLV.setOnItemClickListener(this);
		updateBBSBT.setOnClickListener(this);
		upBBSBtn.setOnClickListener(this);
		nextBBSBtn.setOnClickListener(this);
		setButtonBackGround(0);
		setPage(index + 1);
		totalAdapter = new BBSTotalAdapter(this);
		totalAdapter.notifyDataSetChanged();
		SeekBBSSecondFromCourseRequest bbsSecondFromCourseReq = new SeekBBSSecondFromCourseRequest(
				handler, getIntent().getExtras().getString("courseId"));
		bbsSecondFromCourseReq.sendRequest();
		// playProgressDialog(mContext);
		// handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
	}

	// private void setPage(int index) {
	// pageBBSBtn.setText("第" + index + "页");
	// }

	protected void adapterChangeListener() {
		totalAdapter.setData(totalList);
		totalAdapter.notifyDataSetChanged();
		bbsLV.setAdapter(totalAdapter);
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
			if (getBoarId) {
				if (index >= 1) {
					index--;
					setPage(index + 1);
					if (ALLBBS.equals("allbbs")) {
						seekBBSRequest = new SeekBBSSecondRequest(handler,
								boardId, index + 1 + "", 4 + "");
						seekBBSRequest.sendRequest();
						playProgressDialog(mContext);
						handlerTimer.postDelayed(runnableTimer,
								1000 * DISMISSGAP);
					} else {
						bbsSecondOfMyBBSRequest = new SeekBBSSecondOfMyBBSRequest(
								getApplicationContext(), handler, boardId,
								index + 1 + "", 4 + "");
						bbsSecondOfMyBBSRequest.sendRequest();
						playProgressDialog(mContext);
						handlerTimer.postDelayed(runnableTimer,
								1000 * DISMISSGAP);
					}
				} else {
					return;
				}
			} else {
				SeekBBSSecondFromCourseRequest bbsSecondFromCourseReq = new SeekBBSSecondFromCourseRequest(
						handler, getIntent().getExtras().getString("courseId"));
				bbsSecondFromCourseReq.sendRequest();
				// playProgressDialog(mContext);
				// handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
			}

		} else if (v == nextBBSBtn) {
			if (getBoarId) {
				if (totalList.size() == 4) {
					index++;
					setPage(index + 1);
					if (ALLBBS.equals("allbbs")) {
						seekBBSRequest = new SeekBBSSecondRequest(handler,
								boardId, index + 1 + "", 4 + "");
						seekBBSRequest.sendRequest();
						playProgressDialog(mContext);
						handlerTimer.postDelayed(runnableTimer,
								1000 * DISMISSGAP);
					} else {
						bbsSecondOfMyBBSRequest = new SeekBBSSecondOfMyBBSRequest(
								getApplicationContext(), handler, boardId,
								index + 1 + "", 4 + "");
						bbsSecondOfMyBBSRequest.sendRequest();
						playProgressDialog(mContext);
						handlerTimer.postDelayed(runnableTimer,
								1000 * DISMISSGAP);
					}
				} else {
					return;
				}
			} else {
				SeekBBSSecondFromCourseRequest bbsSecondFromCourseReq = new SeekBBSSecondFromCourseRequest(
						handler, getIntent().getExtras().getString("courseId"));
				bbsSecondFromCourseReq.sendRequest();
				// playProgressDialog(mContext);
				// handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
			}

		} else if (v == updateBBSBT) {
			if (getBoarId) {
				index = 0;
				setPage(index + 1);
				if (ALLBBS.equals("allbbs")) {
					seekBBSRequest = new SeekBBSSecondRequest(handler, boardId,
							index + 1 + "", 20 + "");
					seekBBSRequest.sendRequest();
					playProgressDialog(mContext);
					handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
				} else {
					bbsSecondOfMyBBSRequest = new SeekBBSSecondOfMyBBSRequest(
							getApplicationContext(), handler, boardId, index
									+ 1 + "", 20 + "");
					bbsSecondOfMyBBSRequest.sendRequest();
					playProgressDialog(mContext);
					handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
				}
			} else {
				SeekBBSSecondFromCourseRequest bbsSecondFromCourseReq = new SeekBBSSecondFromCourseRequest(
						handler, getIntent().getExtras().getString("courseId"));
				bbsSecondFromCourseReq.sendRequest();
				// playProgressDialog(mContext);
				// handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
			}
		} else if (v == btnAllBBS) {
			if (getBoarId) {
				index = 0;
				setPage(index + 1);
				setButtonBackGround(0);
				llEditBBS.setVisibility(View.GONE);
				llListView.setVisibility(View.VISIBLE);
				ALLBBS = "allbbs";
				seekBBSRequest = new SeekBBSSecondRequest(handler, boardId,
						index + 1 + "", 20 + "");
				seekBBSRequest.sendRequest();
				playProgressDialog(mContext);
				handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
			} else {
				Toast.makeText(getApplicationContext(), "请求失败，请点击刷新按钮重新加载.",
						Toast.LENGTH_LONG).show();
			}
		} else if (v == btnMyBBS) {
			if (getBoarId) {
				index = 0;
				setPage(index + 1);
				setButtonBackGround(1);
				ALLBBS = "mybbs";
				llEditBBS.setVisibility(View.GONE);
				llListView.setVisibility(View.VISIBLE);
				bbsSecondOfMyBBSRequest = new SeekBBSSecondOfMyBBSRequest(
						getApplicationContext(), handler, boardId, index + 1
								+ "", 20 + "");
				bbsSecondOfMyBBSRequest.sendRequest();
				playProgressDialog(mContext);
				handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
			} else {
				Toast.makeText(getApplicationContext(), "信号弱，请点击刷新按钮重新加载.",
						Toast.LENGTH_LONG).show();
			}
		} else if (v == btnAddBBS) {
			setButtonBackGround(2);
			llEditBBS.setVisibility(View.VISIBLE);
			llListView.setVisibility(View.GONE);
		} else if (v == btnSendInSecond) {
			if (getBoarId) {
				SendNewBBSRequest sendNewBBSRequest = new SendNewBBSRequest(
						handler, getApplicationContext(), boardId,
						edtSendContentInSecond.getText().toString(),
						edtSendTitle.getText().toString(), getLocalIpAddress());
				sendNewBBSRequest.sendRequest();
				playProgressDialog(mContext);
				handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
			} else {
				Toast.makeText(getApplicationContext(), "信号弱，请点击刷新按钮重新加载.",
						Toast.LENGTH_LONG).show();
			}
		} else if (v == btnClearInSecond) {
			edtSendContentInSecond.setText("");
			edtSendTitle.setText("");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position > 0) {
			Intent intent = new Intent(getApplicationContext(),
					BBSListDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("topicId", totalList.get(position - 1).getId());
			bundle.putString("title", totalList.get(position - 1).getTitle());
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}
	}

	private String intToIp(int i) {
		return (i & 0xFF) + "." +

		((i >> 8) & 0xFF) + "." +

		((i >> 16) & 0xFF) + "." +

		(i >> 24 & 0xFF);

	}

	/**
	 * 获取IP地址
	 */
	public String getLocalIpAddress() {
		// 获取wifi服务

		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		// 判断wifi是否开启

		if (!wifiManager.isWifiEnabled()) {

			wifiManager.setWifiEnabled(true);

		}

		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		int ipAddress = wifiInfo.getIpAddress();

		return intToIp(ipAddress);

	}

	// 所有帖子：马鞍山发展论坛 Adapter
	class BBSTotalAdapter extends BaseAdapter {

		private Context mContext;

		private LayoutInflater inflater;

		private List<BBSSecondTotalEntity> list = new ArrayList<BBSSecondTotalEntity>();

		public BBSTotalAdapter(Context context) {
			this.mContext = context;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {

			return list.size();
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
						R.layout.view_bbs_second_total_listview_item, null);
				holder.tvTitle = (TextView) convertview
						.findViewById(R.id.tvTitle);
				holder.tvStudentInfo_ActualName = (TextView) convertview
						.findViewById(R.id.tvStudentInfo_ActualName);
				holder.tvReplayCount = (TextView) convertview
						.findViewById(R.id.tvReplayCount);
				holder.llBBSSecondTotal = (LinearLayout) convertview
						.findViewById(R.id.llBBSSecondTotal);
				AbsListView.LayoutParams layoutCourseParams = new AbsListView.LayoutParams(
						LayoutParams.FILL_PARENT,
						(int) (120 * BaseActivity.heightScale));
				holder.llBBSSecondTotal.setLayoutParams(layoutCourseParams);
				convertview.setTag(holder);
			} else {
				holder = (ViewHolder) convertview.getTag();
			}
			holder.tvTitle.setText(list.get(position).getTitle());
			holder.tvStudentInfo_ActualName.setText(list.get(position)
					.getStudentInfo_ActualName());
			holder.tvReplayCount.setText(list.get(position).getReplayCount());
			return convertview;
		}

		class ViewHolder {
			LinearLayout llBBSSecondTotal;
			TextView tvTitle;
			TextView tvStudentInfo_ActualName;
			TextView tvReplayCount;

		}

		public void setData(List<BBSSecondTotalEntity> list) {
			this.list = list;
		}

	}

}
