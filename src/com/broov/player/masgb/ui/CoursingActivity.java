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
import com.broov.player.masgb.entity.CoursingEntity;
import com.broov.player.masgb.handler.ExitAppUtils;
import com.broov.player.masgb.net.request.DeleteCourseRequest;
import com.broov.player.masgb.net.request.LoginOutRequest;
import com.broov.player.masgb.net.request.SeekCourseingRequest;
import com.broov.player.masgb.utils.ButtonClickUtils;
import com.broov.player.masgb.utils.HandleMessageState;

public class CoursingActivity extends BaseActivity implements OnClickListener {
	private SeekCourseingRequest courseingRequest;
	private CoursingAdapter coursingAdapter;
	private List<CoursingEntity> coursingList = new ArrayList<CoursingEntity>();
	private ListView coursingLV;
	private View headView;
	private Button upCoursingBtn;
	private Button nextCoursingBtn;
	private int VIEW_COUNT = 4;
	private int index = 0;
	private ProgressDialog progressDialog;
	private Context mContext;
	private Button updateCoursingBT;
	private Button pageCoursingBtn;
	private int DISMISSGAP = 30;
	private FlagBean flagBean = FlagBean.getInstance();

	/**
	 * Dialog存在时间设置
	 */
	private Handler handlerTimer = new Handler();
	private Runnable runnableTimer = new Runnable() {
		@Override
		public void run() {
			dismissProgressDialog();
			handlerTimer.removeCallbacks(runnableTimer);
			Toast.makeText(getApplicationContext(), "信号弱，请点击刷新重试！",
					Toast.LENGTH_SHORT).show();
		}
	};
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleMessageState.GET_COURSING_SUCCESS:
				index = 0;
				setPage(index + 1);
				coursingList = (List<CoursingEntity>) msg.obj;
				adapterChangeListener();
				dismissProgressDialog();
				handlerTimer.removeCallbacks(runnableTimer);
				break;
			case HandleMessageState.DELETE_COURSE_SUCCESS:
				handlerTimer.removeCallbacks(runnableTimer);
				Toast.makeText(getApplicationContext(), "取消课程成功！",
						Toast.LENGTH_SHORT).show();
				courseingRequest = new SeekCourseingRequest(handler,
						CoursingActivity.this);
				courseingRequest.sendRequest();
				playProgressDialog(getApplicationContext());
				break;
			default:
				break;
			}
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.couring_layout);
		mContext = CoursingActivity.this;
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
			new AlertDialog.Builder(CoursingActivity.this)
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

	//
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// dismissProgressDialog();
	// }

	private void setPage(int index) {
		pageCoursingBtn.setText("第" + index + "页");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (flagBean.getIsFromSelectCourse().equals("2")) {
			courseingRequest = new SeekCourseingRequest(handler,
					CoursingActivity.this);
			courseingRequest.sendRequest();
			playProgressDialog(mContext);
			handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
		}
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

	protected void adapterChangeListener() {
		coursingAdapter.setData(coursingList);
		coursingAdapter.notifyDataSetChanged();
		coursingLV.setAdapter(coursingAdapter);
		checkButton();
	}

	private void initHolder() {
		pageCoursingBtn = (Button) findViewById(R.id.pageCoursingBtn);
		updateCoursingBT = (Button) findViewById(R.id.updateCoursingBT);
		upCoursingBtn = (Button) findViewById(R.id.upCoursingBtn);
		nextCoursingBtn = (Button) findViewById(R.id.nextCoursingBtn);
		coursingLV = (ListView) findViewById(R.id.coursingLV);
		headView = LayoutInflater.from(this).inflate(
				R.layout.view_coursing_listview_item_head, null);
		
		coursingLV.addHeaderView(headView);
		initLayoutParams();
	}

	private void initLayoutParams() {
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
				(int) (130 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams.setMargins((int) (15 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		upCoursingBtn.setLayoutParams(buttonParams);
		nextCoursingBtn.setLayoutParams(buttonParams);
		LinearLayout.LayoutParams buttonParams2 = new LinearLayout.LayoutParams(
				(int) (80 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams2.setMargins((int) (15 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		updateCoursingBT.setLayoutParams(buttonParams2);
		pageCoursingBtn.setLayoutParams(buttonParams2);

	}

	public void initData() {
		setPage(index + 1);
		updateCoursingBT.setOnClickListener(this);
		upCoursingBtn.setOnClickListener(this);
		nextCoursingBtn.setOnClickListener(this);
		coursingAdapter = new CoursingAdapter(this);
		coursingAdapter.notifyDataSetChanged();
		courseingRequest = new SeekCourseingRequest(handler,
				CoursingActivity.this);
		courseingRequest.sendRequest();
		playProgressDialog(mContext);
		handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
	}

	public void onClick(View v) {
		if (ButtonClickUtils.isFastDoubleClick()) {
			return;
		}
		if (v == upCoursingBtn) {
			upView();
		} else if (v == nextCoursingBtn) {
			nextView();
		} else if (v == updateCoursingBT) {
			index = 0;
			courseingRequest = new SeekCourseingRequest(handler,
					CoursingActivity.this);
			courseingRequest.sendRequest();
			playProgressDialog(mContext);
			handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
			// 测试坚查登入
			// CheckIsLoginRequest checkIsLoginRequest =new
			// CheckIsLoginRequest(handler, CoursingActivity.this);
			// checkIsLoginRequest.sendRequest();
			// 测试完成试卷界面
			// Intent intent = new Intent(getApplicationContext(),
			// FinishCourseActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// startActivity(intent);
		}
	}

	public void upView() {
		index--;
		setPage(index + 1);
		coursingAdapter.notifyDataSetChanged();
		checkButton();
	}

	public void nextView() {
		index++;
		setPage(index + 1);
		coursingAdapter.notifyDataSetChanged();
		checkButton();
	}

	public void checkButton() {
		// 索引值小于等于0，表示不能向前翻页了，以经到了第一页了。
		// 将向前翻页的按钮设为不可用。
		if (index <= 0) {
			if (coursingList.size() <= VIEW_COUNT) {
				upCoursingBtn.setEnabled(false);
				nextCoursingBtn.setEnabled(false);
			} else {
				upCoursingBtn.setEnabled(false);
				nextCoursingBtn.setEnabled(true);
			}
		}
		// 值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
		// 将向后翻页的按钮设为不可用。
		else if (coursingList.size() - index * VIEW_COUNT <= VIEW_COUNT) {
			nextCoursingBtn.setEnabled(false);
			upCoursingBtn.setEnabled(true);
		}
		// 否则将2个按钮都设为可用的。
		else {
			upCoursingBtn.setEnabled(true);
			nextCoursingBtn.setEnabled(true);
		}

	}

	class CoursingAdapter extends BaseAdapter {

		private Context mContext;

		private LayoutInflater inflater;

		private List<CoursingEntity> list = new ArrayList<CoursingEntity>();

		public CoursingAdapter(Context context) {
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

		@Override
		public View getView(int position, View convertview, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertview == null) {
				holder = new ViewHolder();
				convertview = inflater.inflate(
						R.layout.view_coursing_listview_item, null);
				holder.layoutCourseItem = (LinearLayout) convertview
						.findViewById(R.id.layoutCourseItem);
				holder.tvCourseName = (TextView) convertview
						.findViewById(R.id.tvCourseName);
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
				holder.btnCourseCancel = (Button) convertview
						.findViewById(R.id.btnCourseCancel);
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
			if ("00000000".equals(list.get(position + index * VIEW_COUNT)
					.getCourse_id())) {
				holder.btnCourseCancel.setVisibility(View.INVISIBLE);
				holder.btnCourseIn.setVisibility(View.INVISIBLE);
				holder.btnCourseAsk.setVisibility(View.INVISIBLE);
			} else {
				holder.btnCourseCancel.setVisibility(View.VISIBLE);
				holder.btnCourseIn.setVisibility(View.VISIBLE);
				holder.btnCourseAsk.setVisibility(View.VISIBLE);
			}

			holder.btnCourseIn.setOnClickListener(new ButtonClickListener(
					position + index * VIEW_COUNT, holder));
			holder.btnCourseCancel.setOnClickListener(new ButtonClickListener(
					position + index * VIEW_COUNT, holder));
			holder.btnCourseAsk.setOnClickListener(new ButtonClickListener(
					position + index * VIEW_COUNT, holder));
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
			Button btnCourseCancel;
			Button btnCourseAsk;
		}

		public void setData(List<CoursingEntity> list) {
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
					bundle.putString("CourseName", list.get(position)
							.getCourse_name());
					bundle.putString("course_time_length", list.get(position)
							.getCourse_time_length());
					bundle.putString("course_type", list.get(position)
							.getCourse_type());
					bundle.putString("course_length", list.get(position)
							.getCourse_length());
					bundle.putString("from", "coursingActivity");
					intent.putExtras(bundle);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				} else if (arg0 == holder.btnCourseCancel) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CoursingActivity.this);
					builder.setCancelable(false);
					builder.setTitle("提示：");
					builder.setMessage("确定取消此课程？");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// index=0;
									// setPage(index+1);
									DeleteCourseRequest deleteCourseRequest = new DeleteCourseRequest(
											handler, CoursingActivity.this);
									deleteCourseRequest.setCourseId(list.get(
											position).getCourse_id());
									deleteCourseRequest.sendRequest();
									handlerTimer.postDelayed(runnableTimer,
											1000 * DISMISSGAP);
								}
							});
					builder.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
					builder.show();
				} else if (arg0 == holder.btnCourseAsk) {
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
