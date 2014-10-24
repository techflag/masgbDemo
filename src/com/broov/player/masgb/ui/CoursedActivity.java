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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.bean.FlagBean;
import com.broov.player.masgb.entity.CoursedEntity;
import com.broov.player.masgb.handler.ExitAppUtils;
import com.broov.player.masgb.net.request.LoginOutRequest;
import com.broov.player.masgb.net.request.SeekCoursedRequest;
import com.broov.player.masgb.utils.ButtonClickUtils;
import com.broov.player.masgb.utils.HandleMessageState;

public class CoursedActivity extends BaseActivity implements OnClickListener {
	private SeekCoursedRequest seekCoursedRequest;
	private CoursedAdapter coursedAdapter;
	private List<CoursedEntity> coursedList = new ArrayList<CoursedEntity>();
	private ListView coursedLV;
	private View headView;
	private Button upCoursedBtn;
	private Button nextCoursedBtn;
	private Button pageCoursedBtn;
	private ProgressDialog progressDialog;
	private Context mContext;
	private FlagBean flagBean = FlagBean.getInstance();
	// 用于显示每列5个Item项。
	private int VIEW_COUNT = 4;
	// 用于显示页号的索引
	private int index = 0;
	private int DISMISSGAP=30;
	private Button updateCoursedBT;
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
			case HandleMessageState.GET_COURSED_SUCCESS:
				coursedList = (List<CoursedEntity>) msg.obj;
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
		setContentView(R.layout.coursed_layout);
		mContext = CoursedActivity.this;
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
			new AlertDialog.Builder(CoursedActivity.this)
					.setTitle("注意!")
					.setMessage("确定退出应用？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									LoginOutRequest loginOutRequest=new LoginOutRequest(handler);
									loginOutRequest.sendRequest();
//									android.os.Process
//											.killProcess(android.os.Process
//													.myPid()); // 获取PID
									//System.exit(0); // 常规java、c#的标准退出法，返回值为0代表正常退出
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
		flagBean.setIsFromSelectCourse("1");
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
		updateCoursedBT = (Button) findViewById(R.id.updateCoursedBT);
		upCoursedBtn = (Button) findViewById(R.id.upCoursedBtn);
		nextCoursedBtn = (Button) findViewById(R.id.nextCoursedBtn);
		pageCoursedBtn = (Button) findViewById(R.id.pageCoursedBtn);
		coursedLV = (ListView) findViewById(R.id.coursedLV);
		headView = LayoutInflater.from(this).inflate(
				R.layout.view_coursed_listview_item_head, null);
		coursedLV.addHeaderView(headView);
		initLayoutParams();
	}

	private void initLayoutParams() {
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
				(int) (130 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams.setMargins((int) (15 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		upCoursedBtn.setLayoutParams(buttonParams);
		nextCoursedBtn.setLayoutParams(buttonParams);
		LinearLayout.LayoutParams buttonParams2 = new LinearLayout.LayoutParams(
				(int) (80 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams2.setMargins((int) (15 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		updateCoursedBT.setLayoutParams(buttonParams2);
		pageCoursedBtn.setLayoutParams(buttonParams2);
	}

	public void initData() {
		setPage(index + 1);
		updateCoursedBT.setOnClickListener(this);
		upCoursedBtn.setOnClickListener(this);
		nextCoursedBtn.setOnClickListener(this);
		coursedAdapter = new CoursedAdapter(this);
		coursedAdapter.notifyDataSetChanged();
		seekCoursedRequest = new SeekCoursedRequest(handler,CoursedActivity.this);
		seekCoursedRequest.sendRequest();
		playProgressDialog(mContext);
		handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
	}

	private void setPage(int index) {
		pageCoursedBtn.setText("第" + index + "页");
	}

	protected void adapterChangeListener() {
		coursedAdapter.setData(coursedList);
		coursedAdapter.notifyDataSetChanged();
		coursedLV.setAdapter(coursedAdapter);
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
		if (v == upCoursedBtn) {
			upView();
		} else if (v == nextCoursedBtn) {
			nextView();
		} else if (v == updateCoursedBT) {
			seekCoursedRequest = new SeekCoursedRequest(handler,CoursedActivity.this);
			seekCoursedRequest.sendRequest();
			playProgressDialog(mContext);
			handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
		}
	}

	public void upView() {
		index--;
		setPage(index + 1);
		coursedAdapter.notifyDataSetChanged();
		checkButton();
	}

	public void nextView() {
		index++;
		setPage(index + 1);
		coursedAdapter.notifyDataSetChanged();
		checkButton();
	}

	public void checkButton() {
		// 索引值小于等于0，表示不能向前翻页了，以经到了第一页了。
		// 将向前翻页的按钮设为不可用。
		if (index <= 0) {
			if (coursedList.size() <= VIEW_COUNT) {
				upCoursedBtn.setEnabled(false);
				nextCoursedBtn.setEnabled(false);
			} else {
				upCoursedBtn.setEnabled(false);
				nextCoursedBtn.setEnabled(true);
			}
		}
		// 值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
		// 将向后翻页的按钮设为不可用。
		else if (coursedList.size() - index * VIEW_COUNT <= VIEW_COUNT) {
			nextCoursedBtn.setEnabled(false);
			upCoursedBtn.setEnabled(true);
		}
		// 否则将2个按钮都设为可用的。
		else {
			upCoursedBtn.setEnabled(true);
			nextCoursedBtn.setEnabled(true);
		}

	}

	class CoursedAdapter extends BaseAdapter {

		private Context mContext;

		private LayoutInflater inflater;

		private List<CoursedEntity> list = new ArrayList<CoursedEntity>();

		public CoursedAdapter(Context context) {
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
						R.layout.view_coursed_listview_item, null);
				holder.tvCourseName = (TextView) convertview
						.findViewById(R.id.tvCourseName);
				holder.layoutCourseItem = (LinearLayout) convertview
						.findViewById(R.id.layoutCourseItem);
				holder.tvCoursePerson = (TextView) convertview
						.findViewById(R.id.tvCoursePerson);
				holder.tvCourseTimeLength = (TextView) convertview
						.findViewById(R.id.tvCourseTimeLength);
				holder.tvCourseLength = (TextView) convertview
						.findViewById(R.id.tvCourseLength);
				holder.tvCourseScore = (TextView) convertview
						.findViewById(R.id.tvCourseScore);
				holder.tvCourseType = (TextView) convertview
						.findViewById(R.id.tvCourseType);
				holder.btnCourseIn = (Button) convertview
						.findViewById(R.id.btnCourseIn);
				holder.btnCourseAsk = (Button) convertview
						.findViewById(R.id.btnCourseAsk);
				AbsListView.LayoutParams layoutCourseParams = new AbsListView.LayoutParams(
						LayoutParams.FILL_PARENT,
						(int) (120 * BaseActivity.heightScale));
				holder.layoutCourseItem.setLayoutParams(layoutCourseParams);
				convertview.setTag(holder);
			} else {
				holder = (ViewHolder) convertview.getTag();
			}
			holder.tvCourseName.setText(list.get(position + index * VIEW_COUNT)
					.getCourse_name());
			holder.tvCoursePerson.setText(list.get(
					position + index * VIEW_COUNT).getCourse_person());
			holder.tvCourseTimeLength.setText(list.get(
					position + index * VIEW_COUNT).getCourse_time_length());
			holder.tvCourseLength.setText(list.get(
					position + index * VIEW_COUNT).getCourse_length());
			if (list.get(position + index * VIEW_COUNT).getCourse_score() != null) {
				if (list.get(position + index * VIEW_COUNT).getCourse_score()
						.startsWith(".")) {
					holder.tvCourseScore.setText("0"
							+ list.get(position + index * VIEW_COUNT)
									.getCourse_score());
				} else {
					holder.tvCourseScore.setText(list.get(
							position + index * VIEW_COUNT).getCourse_score());
				}
			} else {
				holder.tvCourseScore.setText(list.get(
						position + index * VIEW_COUNT).getCourse_score());
			}
			holder.tvCourseType.setText(list.get(position + index * VIEW_COUNT)
					.getCourse_type());
			holder.btnCourseIn.setOnClickListener(new ButtonClickListener(
					position + index * VIEW_COUNT, holder));
			holder.btnCourseAsk.setOnClickListener(new ButtonClickListener(
					position + index * VIEW_COUNT, holder));
			if ("00000000".equals(list.get(position + index * VIEW_COUNT)
					.getCourse_id())) {
				holder.btnCourseIn.setVisibility(View.INVISIBLE);
				holder.btnCourseAsk.setVisibility(View.INVISIBLE);
			} else {
				holder.btnCourseIn.setVisibility(View.VISIBLE);
				holder.btnCourseAsk.setVisibility(View.VISIBLE);
			}
			return convertview;
		}

		class ViewHolder {
			LinearLayout layoutCourseItem;
			TextView tvCourseName;
			TextView tvCoursePerson;
			TextView tvCourseTimeLength;
			TextView tvCourseLength;
			TextView tvCourseScore;
			TextView tvCourseType;
			Button btnCourseIn;
			Button btnCourseAsk;
		}

		public void setData(List<CoursedEntity> list) {
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
				if (arg0 == holder.btnCourseIn) {
					Bundle bundle = new Bundle();
					Intent intent = new Intent(getApplicationContext(),
							CourseDocListActivity.class);
					String course_id = list.get(position).getCourse_id();
					bundle.putString("course_id", course_id);
					bundle.putString("course_time_length", list.get(position)
							.getCourse_time_length());
					bundle.putString("course_type", list.get(position)
							.getCourse_type());
					bundle.putString("CourseName", list.get(position)
							.getCourse_name());
					bundle.putString("course_length", list.get(position)
							.getCourse_length());
					bundle.putString("from", "coursedActivity");
					intent.putExtras(bundle);
//					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}else if(arg0 == holder.btnCourseAsk){
					Intent intent = new Intent(getApplicationContext(),
							BBSSecondListFromCourseActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("courseId", list.get(position)
							.getCourse_id());
					intent.putExtras(bundle);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}
			}
		}

	}

}
