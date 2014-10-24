package com.broov.player.masgb.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.entity.SpecialTopicingEntity;
import com.broov.player.masgb.net.request.GetSpecialTopicingRequest;
import com.broov.player.masgb.utils.ButtonClickUtils;
import com.broov.player.masgb.utils.HandleMessageState;

public class SpecialTopicingActivity extends BaseActivity implements
		OnClickListener {
	private List<SpecialTopicingEntity> specialTopicList = new ArrayList<SpecialTopicingEntity>();
	private Button pageSpecialTopicingBtn;
	private Button updateSpecialTopicingBT;
	private Button upSpecialTopicingBtn;
	private Button nextSpecialTopicingBtn;
	private ListView specialtopicingLV;
	private ProgressDialog progressDialog;
	private int VIEW_COUNT = 3;
	private int index = 0;
	private SpecialTopicingAdapter adapter;
	private Context mContext;
	private GetSpecialTopicingRequest getSpecialTopicingRequest;
	private View headView;
	/**
	 * Dialog存在时间设置
	 */
	private Handler handlerTimer = new Handler();
	private Runnable runnableTimer = new Runnable() {
		@Override
		public void run() {
			dismissProgressDialog();
			handlerTimer.removeCallbacks(runnableTimer);
			Toast.makeText(getApplicationContext(), "信号弱，请重试...",
					Toast.LENGTH_SHORT).show();
		}
	};
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleMessageState.GET_SPECIAL_TOPICING_SUCCESS:
				setPage(index + 1);
				specialTopicList = (List<SpecialTopicingEntity>) msg.obj;
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
		setContentView(R.layout.specialtopicing);
		mContext = SpecialTopicingActivity.this;
		initHolder();
		initData();
		checkButton();
	}

	protected void adapterChangeListener() {
		adapter.setData(specialTopicList);
		adapter.notifyDataSetChanged();
		specialtopicingLV.setAdapter(adapter);
		checkButton();
	}

	private void initData() {
		setPage(index + 1);
		updateSpecialTopicingBT.setOnClickListener(this);
		upSpecialTopicingBtn.setOnClickListener(this);
		nextSpecialTopicingBtn.setOnClickListener(this);
		adapter = new SpecialTopicingAdapter(this);
		adapter.notifyDataSetChanged();
		getSpecialTopicingRequest = new GetSpecialTopicingRequest(handler);
		getSpecialTopicingRequest.sendRequest();
		playProgressDialog(mContext);
		handlerTimer.postDelayed(runnableTimer, 1000 * 10);
	}

	private void initHolder() {
		pageSpecialTopicingBtn = (Button) findViewById(R.id.pageSpecialTopicingBtn);
		updateSpecialTopicingBT = (Button) findViewById(R.id.updateSpecialTopicingBT);
		upSpecialTopicingBtn = (Button) findViewById(R.id.upSpecialTopicingBtn);
		nextSpecialTopicingBtn = (Button) findViewById(R.id.nextSpecialTopicingBtn);
		specialtopicingLV = (ListView) findViewById(R.id.specialtopicingLV);
		headView = LayoutInflater.from(this).inflate(
				R.layout.view_special_topicing_listview_item_head, null);
		specialtopicingLV.addHeaderView(headView);
		initLayoutParams();
	}

	private void initLayoutParams() {
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
				(int) (130 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams.setMargins((int) (15 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		upSpecialTopicingBtn.setLayoutParams(buttonParams);
		nextSpecialTopicingBtn.setLayoutParams(buttonParams);
		LinearLayout.LayoutParams buttonParams2 = new LinearLayout.LayoutParams(
				(int) (80 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams2.setMargins((int) (15 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		updateSpecialTopicingBT.setLayoutParams(buttonParams2);
		pageSpecialTopicingBtn.setLayoutParams(buttonParams2);
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

	private void setPage(int index) {
		pageSpecialTopicingBtn.setText("第" + index + "页");
	}

	/**
	 * 监听返回按键事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(SpecialTopicingActivity.this)
					.setTitle("注意!")
					.setMessage("确定退出应用？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									android.os.Process
											.killProcess(android.os.Process
													.myPid()); // 获取PID
									System.exit(0); // 常规java、c#的标准退出法，返回值为0代表正常退出
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

	public void checkButton() {
		// 索引值小于等于0，表示不能向前翻页了，以经到了第一页了。
		// 将向前翻页的按钮设为不可用。
		if (index <= 0) {
			if (specialTopicList.size() <= VIEW_COUNT) {
				upSpecialTopicingBtn.setEnabled(false);
				nextSpecialTopicingBtn.setEnabled(false);
			} else {
				upSpecialTopicingBtn.setEnabled(false);
				nextSpecialTopicingBtn.setEnabled(true);
			}
		}
		// 值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
		// 将向后翻页的按钮设为不可用。
		else if (specialTopicList.size() - index * VIEW_COUNT <= VIEW_COUNT) {
			nextSpecialTopicingBtn.setEnabled(false);
			upSpecialTopicingBtn.setEnabled(true);
		}
		// 否则将2个按钮都设为可用的。
		else {
			upSpecialTopicingBtn.setEnabled(true);
			nextSpecialTopicingBtn.setEnabled(true);
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

	@Override
	public void onClick(View v) {
		if (ButtonClickUtils.isFastDoubleClick()) {
			return;
		}
		if (v == upSpecialTopicingBtn) {
			upView();
		} else if (v == nextSpecialTopicingBtn) {
			nextView();
		} else if (v == updateSpecialTopicingBT) {
			index = 0;
			getSpecialTopicingRequest = new GetSpecialTopicingRequest(handler);
			getSpecialTopicingRequest.sendRequest();
			playProgressDialog(mContext);
			handlerTimer.postDelayed(runnableTimer, 1000 * 10);
		}
	}

	class SpecialTopicingAdapter extends BaseAdapter {

		private Context mContext;

		private LayoutInflater inflater;

		private List<SpecialTopicingEntity> list = new ArrayList<SpecialTopicingEntity>();

		private Date fromDate;
		private int count;
		private int countScore;

		public SpecialTopicingAdapter(Context context) {
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
			return list.get(arg0 + index * VIEW_COUNT);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0 + index * VIEW_COUNT;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		public View getView(int position, View convertview, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertview == null) {
				holder = new ViewHolder();
				convertview = inflater.inflate(
						R.layout.view_special_topicing_listview_item, null);
				holder.layoutCourseItem = (LinearLayout) convertview
						.findViewById(R.id.layoutCourseItem_topic);
				holder.name = (TextView) convertview.findViewById(R.id.name);
				holder.require = (TextView) convertview
						.findViewById(R.id.require);
				holder.content = (TextView) convertview
						.findViewById(R.id.content);
				holder.goToTopic = (Button) convertview
						.findViewById(R.id.goToTopic);
				AbsListView.LayoutParams layoutCourseParams = new AbsListView.LayoutParams(
						LayoutParams.FILL_PARENT,
						(int) (150 * BaseActivity.heightScale));
				holder.layoutCourseItem.setLayoutParams(layoutCourseParams);
				convertview.setTag(holder);
			} else {
				holder = (ViewHolder) convertview.getTag();
			}
			if (!list.get(position + index * VIEW_COUNT).getStudyClassId()
					.equals("00000000")) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String dstr = list.get(position + index * VIEW_COUNT)
							.getDate();
					fromDate = sdf.parse(dstr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if ((Integer.parseInt(list.get(position + index * VIEW_COUNT)
						.getPeriods()) - getGapCount(fromDate)) > 0) {
					count = Integer.parseInt(list.get(
							position + index * VIEW_COUNT).getPeriods())
							- getGapCount(fromDate);
				} else {
					count = 0;
				}
				if ((Integer.parseInt(list.get(position + index * VIEW_COUNT)
						.getCreditHour()) - Integer.parseInt(list.get(
						position + index * VIEW_COUNT).getCreator())) > 0) {
					countScore = Integer.parseInt(list.get(
							position + index * VIEW_COUNT).getCreditHour())
							- Integer
									.parseInt(list.get(
											position + index * VIEW_COUNT)
											.getCreator());
				} else {
					countScore = 0;
				}

				holder.name.setText(list.get(position + index * VIEW_COUNT)
						.getName());
				holder.require.setText(",要求"
						+ list.get(position + index * VIEW_COUNT).getPeriods()
						+ "天内获得"
						+ list.get(position + index * VIEW_COUNT)
								.getCreditHour() + "学分");
				holder.content.setText("您于["
						+ list.get(position + index * VIEW_COUNT).getDate()
						+ "]参加学习班（还有" + count + "天时间），已获得"
						+ list.get(position + index * VIEW_COUNT).getCreator()
						+ "学分（还需" + countScore + "学分）。");
				holder.goToTopic.setVisibility(View.VISIBLE);
				holder.goToTopic.setOnClickListener(new ButtonClickListener(
						position + index * VIEW_COUNT, holder));
			} else {
				holder.goToTopic.setVisibility(View.INVISIBLE);
			}
			return convertview;
		}

		/**
		 * 获取两个日期之间的间隔天数
		 * 
		 * @return
		 */
		public int getGapCount(Date startDate) {
			Calendar fromCalendar = Calendar.getInstance();
			fromCalendar.setTime(startDate);
			fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
			fromCalendar.set(Calendar.MINUTE, 0);
			fromCalendar.set(Calendar.SECOND, 0);
			fromCalendar.set(Calendar.MILLISECOND, 0);

			long time = System.currentTimeMillis();
			final Calendar toCalendar = Calendar.getInstance();
			toCalendar.setTimeInMillis(time);
			return (int) ((toCalendar.getTime().getTime() - fromCalendar
					.getTime().getTime()) / (1000 * 60 * 60 * 24));
		}

		class ViewHolder {
			LinearLayout layoutCourseItem;
			TextView name;
			TextView require;
			TextView content;
			Button goToTopic;

		}

		public void setData(List<SpecialTopicingEntity> list) {
			this.list = list;
		}

		class ButtonClickListener implements OnClickListener {

			private int position;

			private ViewHolder holder;

			public ButtonClickListener(int position, ViewHolder holder) {
				this.position = position;
				this.holder = holder;
			}

			@Override
			public void onClick(View arg0) {
				if (arg0 == holder.goToTopic) {
					Bundle bundle = new Bundle();
					Intent intent = new Intent(getApplicationContext(),
							TopicListActivity.class);
					String id = list.get(position).getStudyClassId();
					bundle.putString("id", id);
					intent.putExtras(bundle);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}
			}
		}

	}

}
