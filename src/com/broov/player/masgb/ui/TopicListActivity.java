package com.broov.player.masgb.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.entity.SpecialTopicingDetailEntity;
import com.broov.player.masgb.net.request.GetTopicListRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class TopicListActivity extends BaseActivity implements OnClickListener {
	private GetTopicListRequest getTopicListRequest;
	private TopicAdapter adapter;
	private List<SpecialTopicingDetailEntity> topicList = new ArrayList<SpecialTopicingDetailEntity>();
	private ListView topicLV;
	private View headView;
	private Button updatetopicDocBtn;
	// 用于显示每列5个Item项。
	private int VIEW_COUNT = 4;
	private int index = 0;
	private Bundle bundle;
	private String id;
	private ProgressDialog progressDialog;
	private Context mContext;
	/**
	 * Dialog存在时间设置
	 */
	private Handler handlerTimer = new Handler();
	private Runnable runnableTimer = new Runnable() {
		@Override
		public void run() {
			dismissProgressDialog();
			Toast.makeText(getApplicationContext(), "信号弱，请重试...",
					Toast.LENGTH_SHORT).show();
			handlerTimer.removeCallbacks(runnableTimer);
		}
	};
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleMessageState.GET_TOPIC_LIST_SUCCESS:
				topicList.clear();
				topicList = (List<SpecialTopicingDetailEntity>) msg.obj;
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
		setContentView(R.layout.topic_list_layout);
		setSubPageTitle("专题课程列表", true);
		mContext = TopicListActivity.this;
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

	/**
	 * 消失等待加载对话框
	 */
	public void dismissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissProgressDialog();
	}

	protected void adapterChangeListener() {
		adapter.setData(topicList);
		adapter.notifyDataSetChanged();
		topicLV.setAdapter(adapter);
	}

	private void initHolder() {
		updatetopicDocBtn = (Button) findViewById(R.id.updatetopicDocBtn);
		topicLV = (ListView) findViewById(R.id.topicLV);
		headView = LayoutInflater.from(this).inflate(
				R.layout.view_topic_listview_item, null);
		topicLV.addHeaderView(headView);
		initLayoutParams();
	}

	private void initLayoutParams() {
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
				(int) (230 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams.setMargins((int) (15 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		updatetopicDocBtn.setLayoutParams(buttonParams);

	}

	public void initData() {
		bundle = getIntent().getExtras();
		id = bundle.getString("id");
		updatetopicDocBtn.setOnClickListener(this);
		adapter = new TopicAdapter(this);
		getTopicListRequest = new GetTopicListRequest(handler);
		getTopicListRequest.setId(id);
		getTopicListRequest.sendRequest();
		playProgressDialog(mContext);
		handlerTimer.postDelayed(runnableTimer, 1000 * 10);
	}

	public void onClick(View v) {
		if (v == updatetopicDocBtn) {
			getTopicListRequest = new GetTopicListRequest(handler);
			getTopicListRequest.setId(id);
			getTopicListRequest.sendRequest();
			playProgressDialog(mContext);
			handlerTimer.postDelayed(runnableTimer, 1000 * 10);
		}
	}

	class TopicAdapter extends BaseAdapter {

		private Context mContext;

		private LayoutInflater inflater;

		private List<SpecialTopicingDetailEntity> list = new ArrayList<SpecialTopicingDetailEntity>();

		public TopicAdapter(Context context) {
			this.mContext = context;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			int ori = VIEW_COUNT * index;
			if (list.size() - ori < VIEW_COUNT) {
				return list.size() - ori;
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
						R.layout.view_topic_listview_item, null);
				holder.layoutTopicListItem = (LinearLayout) convertview
						.findViewById(R.id.layoutTopicListItem);
				holder.tvTopicName = (TextView) convertview
						.findViewById(R.id.tvTopicName);
				holder.tvLecture = (TextView) convertview
						.findViewById(R.id.tvTopicLecture);
				holder.tvTime = (TextView) convertview
						.findViewById(R.id.tvTopicTime);
				holder.tvStudyTime = (TextView) convertview
						.findViewById(R.id.tvTopicStudyTime);
				holder.tvScore = (TextView) convertview
						.findViewById(R.id.tvTopicScore);
				holder.tvStatus = (TextView) convertview
						.findViewById(R.id.tvTopicStatus);
				holder.btnTopicLearn = (Button) convertview
						.findViewById(R.id.btnTopicLearn);
				AbsListView.LayoutParams layoutCourseParams = new AbsListView.LayoutParams(
						LayoutParams.FILL_PARENT, 52);
				holder.layoutTopicListItem.setLayoutParams(layoutCourseParams);
				convertview.setTag(holder);
			} else {
				holder = (ViewHolder) convertview.getTag();
			}
			holder.tvTopicName.setText("中国科学主义发展大全");
			holder.tvLecture.setText("王老师");
			holder.tvTime.setText("66");
			holder.tvStudyTime.setText("56");
			holder.tvScore.setText("6");
			holder.tvStatus.setText("已学");
			holder.btnTopicLearn.setOnClickListener(new ButtonClickListener(
					position + VIEW_COUNT * index, holder));
			return convertview;
		}

		class ViewHolder {
			TextView tvTopicName;
			TextView tvLecture;
			TextView tvTime;
			TextView tvStudyTime;
			TextView tvScore;
			TextView tvStatus;
			Button btnTopicLearn;
			LinearLayout layoutTopicListItem;

		}

		public void setData(List<SpecialTopicingDetailEntity> list) {
			this.list = list;
		}

		class ButtonClickListener implements OnClickListener {

			public ButtonClickListener(int position, ViewHolder holder) {
			}

			@Override
			public void onClick(View arg0) {
			}

		}

	}

}
