package com.broov.player.masgb.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.entity.BBSListDetailOrigEntity;
import com.broov.player.masgb.entity.BBSReplyEntity;
import com.broov.player.masgb.handler.ExitAppUtils;
import com.broov.player.masgb.net.request.GetBBSListDetailOrigRequest;
import com.broov.player.masgb.net.request.GetBBSListReplyRequest;
import com.broov.player.masgb.net.request.SendReplyRequest;
import com.broov.player.masgb.utils.HandleMessageState;

/**
 * 帖子
 * 
 * @author Nate Robinson
 * 
 */
public class BBSListDetailActivity extends BaseActivity implements
		OnClickListener {

	private Context mContext;
	/**
	 * 帖子内容
	 */
	private TextView tvBBSListDetailOrig;
	/**
	 * 回复帖子
	 */
	private TextView tvBBSListDetailReply;
	/**
	 * 写回复
	 */
	private TextView tvBBSListDetailSend;
	private ListView lvBBSListDetail;
	private LinearLayout llLvBBSListDetail;
	private LinearLayout llLvBBSListDetailOrig;
	private LinearLayout llSendBBSContent;
	private String topicId;
	private List<BBSListDetailOrigEntity> list = new ArrayList<BBSListDetailOrigEntity>();
	private View Unreply_img_01, Reply_img_01, Send_img_01;
	private List<BBSReplyEntity> listReply = new ArrayList<BBSReplyEntity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bbs_list_detail);
		ExitAppUtils.getInstance().addActivity(this);
		mContext = BBSListDetailActivity.this;
		setSubPageTitle("", true);
		initHolder();
		initData();
	}

	private void initHolder() {
		btnSend = (Button) findViewById(R.id.btnSend);
		btnClear = (Button) findViewById(R.id.btnClear);
		edtSendContent = (EditText) findViewById(R.id.edtSendContent);
		tvSendTitle = (TextView) findViewById(R.id.tvSendTitle);
		pageReplyBtn = (Button) findViewById(R.id.pageReplyBtn);
		upReplyBtn = (Button) findViewById(R.id.upReplyBtn);
		nextReplyBtn = (Button) findViewById(R.id.nextReplyBtn);
		tvTitleInDetail = (TextView) findViewById(R.id.tvTitleInDetail);
		tvNameInDetail = (TextView) findViewById(R.id.tvNameInDetail);
		tvNameInDetailData = (TextView) findViewById(R.id.tvNameInDetailData);
		tvContentInDetail = (TextView) findViewById(R.id.tvContentInDetail);
		llLvBBSListDetail = (LinearLayout) findViewById(R.id.llLvBBSListDetail);
		llLvBBSListDetailOrig = (LinearLayout) findViewById(R.id.llLvBBSListDetailOrig);
		llSendBBSContent = (LinearLayout) findViewById(R.id.llSendBBSContent);
		tvBBSListDetailOrig = (TextView) findViewById(R.id.tvBBSListDetailOrig);
		tvBBSListDetailReply = (TextView) findViewById(R.id.tvBBSListDetailReply);
		tvBBSListDetailSend = (TextView) findViewById(R.id.tvBBSListDetailSend);
		Unreply_img_01 = findViewById(R.id.Unreply_img_01);
		Reply_img_01 = findViewById(R.id.Reply_img_01);
		Send_img_01 = findViewById(R.id.Send_img_01);
		lvBBSListDetail = (ListView) findViewById(R.id.lvBBSListDetail);
		initLayoutParams();
	}

	public void initLayoutParams() {
	}

	@Override
	protected void onResume() {
		super.onResume();
		setSelected(0);
	}

	public void initData() {
		topicId = getIntent().getExtras().getString("topicId");
		View headView = LayoutInflater.from(this).inflate(
				R.layout.reply_bbs_cell_head, null);
		lvBBSListDetail.addHeaderView(headView);
		bindListener();
	}

	public void bindListener() {
		tvBBSListDetailOrig.setOnClickListener(this);
		tvBBSListDetailReply.setOnClickListener(this);
		tvBBSListDetailSend.setOnClickListener(this);
		upReplyBtn.setOnClickListener(this);
		nextReplyBtn.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		btnClear.setOnClickListener(this);
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

	/**
	 * 消失等待加载对话框
	 */
	public void dismissProgressDialog() {
		if (progressDialog != null &&

		progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
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

	private void setSelected(int index) {
		Unreply_img_01.setVisibility(View.INVISIBLE);
		Reply_img_01.setVisibility(View.INVISIBLE);
		Send_img_01.setVisibility(View.INVISIBLE);
		llSendBBSContent.setVisibility(View.GONE);
		llLvBBSListDetail.setVisibility(View.GONE);
		llLvBBSListDetailOrig.setVisibility(View.GONE);
		switch (index) {
		case 0:
			llLvBBSListDetailOrig.setVisibility(View.VISIBLE);
			Unreply_img_01.setVisibility(View.VISIBLE);
			doGetBBSListDetailOrig();
			break;
		case 1:
			llLvBBSListDetail.setVisibility(View.VISIBLE);
			Reply_img_01.setVisibility(View.VISIBLE);
			// if (lvBBSListDetail != null) {
			// lvBBSListDetail.removeAllViews();
			// }
			setPage(index);
			doBBSListDetailReply();
			break;
		case 2:
			llSendBBSContent.setVisibility(View.VISIBLE);
			Send_img_01.setVisibility(View.VISIBLE);
			tvSendTitle.setText(getIntent().getExtras().getString("title"));
			break;
		default:
			break;
		}
	}

	private void doBBSListDetailReply() {
		GetBBSListReplyRequest bbsListReplyRequest = new GetBBSListReplyRequest(
				mHandler, topicId, index + "");
		bbsListReplyRequest.sendRequest();
		playProgressDialog(mContext);
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

	private void doGetBBSListDetailOrig() {
		GetBBSListDetailOrigRequest getBBSListDetailOrigRequest = new GetBBSListDetailOrigRequest(
				mHandler, topicId);
		getBBSListDetailOrigRequest.sendRequest();
		playProgressDialog(mContext);
	}

	private void setPage(int page) {
		pageReplyBtn.setText("第" + page + "页");
	}

	@Override
	public void onClick(View v) {
		if (v == tvBBSListDetailOrig) {
			setSelected(0);
		} else if (v == tvBBSListDetailReply) {
			setSelected(1);
		} else if (v == tvBBSListDetailSend) {
			setSelected(2);
		} else if (v == nextReplyBtn) {
			if (listReply.size() == 4) {
				index++;
				setPage(index);
				GetBBSListReplyRequest bbsListReplyRequest = new GetBBSListReplyRequest(
						mHandler, topicId, index + "");
				bbsListReplyRequest.sendRequest();
				playProgressDialog(mContext);
			} else {
				return;
			}
		} else if (v == upReplyBtn) {
			if (index > 1) {
				index--;
				setPage(index);
				GetBBSListReplyRequest bbsListReplyRequest = new GetBBSListReplyRequest(
						mHandler, topicId, index + "");
				bbsListReplyRequest.sendRequest();
				playProgressDialog(mContext);
			} else {
				return;
			}
		} else if (v == btnSend) {
			SendReplyRequest sendReplyRequest = new SendReplyRequest(mHandler,
					mContext, topicId, edtSendContent.getText().toString(),
					getLocalIpAddress());
			sendReplyRequest.sendRequest();
			playProgressDialog(mContext);
		} else if (v == btnClear) {
			edtSendContent.setText("");
		}
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HandleMessageState.GET_BBS_LIST_DETAIL_ORIG_SUCCESS:
				list = (List<BBSListDetailOrigEntity>) msg.obj;
				doBBSOrigData(list);
				dismissProgressDialog();
				break;
			case HandleMessageState.GET_BBS_LIST_REPLY_SUCCESS:
				listReply = (List<BBSReplyEntity>) msg.obj;
				doBBSReplyData(listReply);
				dismissProgressDialog();
				break;

			case HandleMessageState.ADD_REPLY_SUCCESS:
				Toast.makeText(mContext, "添加回复成功", Toast.LENGTH_SHORT).show();
				dismissProgressDialog();
				edtSendContent.setText("");
				setSelected(1);
				break;
			case HandleMessageState.ADD_REPLY_FAILURE:
				dismissProgressDialog();
				Toast.makeText(mContext, "添加回复失败", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	private TextView tvTitleInDetail;
	private TextView tvNameInDetail;
	private TextView tvContentInDetail;
	private TextView tvNameInDetailData;
	private ProgressDialog progressDialog;
	private int index = 1;
	private Button pageReplyBtn;
	private Button upReplyBtn;
	private Button nextReplyBtn;
	private Button btnSend;
	private Button btnClear;
	private EditText edtSendContent;
	private TextView tvSendTitle;

	protected void doBBSOrigData(List<BBSListDetailOrigEntity> list) {
		if (list.size() >= 1) {
			tvTitleInDetail.setText(list.get(0).getTitle());
			tvNameInDetail.setText(list.get(0).getStudentInfo_ActualName());
			tvNameInDetailData.setText(list.get(0).getDate());
			tvContentInDetail.setText(list.get(0).getSubject());
		}
	}

	protected void doBBSReplyData(List<BBSReplyEntity> listReply) {
		BBSListDetailAdapter adapter = new BBSListDetailAdapter(mContext);
		adapter.setData(listReply);
		adapter.notifyDataSetChanged();
		lvBBSListDetail.setAdapter(adapter);
	}

	class BBSListDetailAdapter extends BaseAdapter {

		private Context mContext;

		private LayoutInflater inflater;
		private List<BBSReplyEntity> list = new ArrayList<BBSReplyEntity>();

		public BBSListDetailAdapter(Context context) {
			this.mContext = context;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertview, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertview == null) {
				holder = new ViewHolder();
				convertview = inflater.inflate(R.layout.reply_bbs_cell, null);
				holder.tvReplyName = (TextView) convertview
						.findViewById(R.id.tvReplyName);
				holder.tvReplyData = (TextView) convertview
						.findViewById(R.id.tvReplyData);
				holder.tvReplyContent = (TextView) convertview
						.findViewById(R.id.tvReplyContent);
				convertview.setTag(holder);
			} else {
				holder = (ViewHolder) convertview.getTag();
			}
			holder.tvReplyName
					.setText(list.get(position).getStudentInfo_Name());
			holder.tvReplyData.setText(list.get(position).getDate());
			holder.tvReplyContent.setText(list.get(position).getSubject());
			return convertview;

		}

		class ViewHolder {
			TextView tvReplyName;
			TextView tvReplyData;
			TextView tvReplyContent;
		}

		public void setData(List<BBSReplyEntity> list) {
			this.list = list;
		}
	}
}
