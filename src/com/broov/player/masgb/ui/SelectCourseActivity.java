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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.bean.FlagBean;
import com.broov.player.masgb.entity.SelectCourseEntity;
import com.broov.player.masgb.handler.ExitAppUtils;
import com.broov.player.masgb.net.request.AddCourseRequest;
import com.broov.player.masgb.net.request.GetCourseListRequest;
import com.broov.player.masgb.net.request.LoginOutRequest;
import com.broov.player.masgb.utils.ButtonClickUtils;
import com.broov.player.masgb.utils.HandleMessageState;

public class SelectCourseActivity extends BaseActivity implements
		OnClickListener {
	private GetCourseListRequest getCourseListRequest;
	private SelectCourseAdapter selectCourseAdapter;
	private List<SelectCourseEntity> selectCourseList = new ArrayList<SelectCourseEntity>();
	private ListView selectCourseLV;
	private View headView;
	private Button upSelectCourseBtn;
	private Button nextSelectCourseBtn;
	private int page = 0;
	private ProgressDialog progressDialog;
	private Context mContext;
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
			Toast.makeText(getApplicationContext(), "信号弱，请点击刷新重试！",
					Toast.LENGTH_SHORT).show();
			handlerTimer.removeCallbacks(runnableTimer);
		}
	};

	// 用于显示每列5个Item项。
	private int VIEW_COUNT = 4;

	// 用于显示页号的索引
	private int index = 0;

	private Spinner spCourseType;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleMessageState.GET_COURSE_LIST_SUCCESS:
				selectCourseList = (List<SelectCourseEntity>) msg.obj;
				if (selectCourseList.size() == 0) {
					nextSelectCourseBtn.setEnabled(false);
				} else {
					nextSelectCourseBtn.setEnabled(true);
				}
				adapterChangeListener();
				dismissProgressDialog();
				handlerTimer.removeCallbacks(runnableTimer);
				break;
			case HandleMessageState.ADD_COURSE_SUCCESS:
				handlerTimer.removeCallbacks(runnableTimer);
				Toast.makeText(getApplicationContext(), "添加课程成功！",
						Toast.LENGTH_SHORT).show();
				page = 0;
				setPage(page + 1);
				getCourseListRequest = new GetCourseListRequest(handler,
						SelectCourseActivity.this);
				getCourseListRequest.setType(String.valueOf(spCourseType
						.getSelectedItemPosition() - 1), contentET.getText()
						.toString().trim());
				getCourseListRequest.setPage(page + 1);
				getCourseListRequest.sendRequest();
				playProgressDialog(mContext);
				break;
			case HandleMessageState.ADD_COURSE_FAILURE:
				handlerTimer.removeCallbacks(runnableTimer);
				Toast.makeText(getApplicationContext(), "添加课程失败！",
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};

	};
	private Button searchBT;
	private EditText contentET;
	private TextView tvKeyWords;
	private TextView tvCourseType;
	private Button pageSelectCourseBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectcourse_layout);
		mContext = SelectCourseActivity.this;
		ExitAppUtils.getInstance().addActivity(this);
		initHolder();
		initData();
		// checkButton();
	}

	protected void adapterChangeListener() {
		selectCourseAdapter.setData(selectCourseList);
		selectCourseAdapter.notifyDataSetChanged();
		selectCourseLV.setAdapter(selectCourseAdapter);
		// checkButton();
	}

	@Override
	protected void onResume() {
		super.onResume();
		flagBean.setIsFromSelectCourse("1");
	}

	private void setPage(int index) {
		pageSelectCourseBtn.setText("第" + index + "页");
	}

	/**
	 * 监听返回按键事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(SelectCourseActivity.this)
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

	/**
	 * 消失等待加载对话框
	 */
	public void dismissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	private void initHolder() {
		pageSelectCourseBtn = (Button) findViewById(R.id.pageSelectCourseBtn);
		tvKeyWords = (TextView) findViewById(R.id.tvKeyWords);
		tvCourseType = (TextView) findViewById(R.id.tvCourseType);
		contentET = (EditText) findViewById(R.id.contentET);
		searchBT = (Button) findViewById(R.id.searchBT);
		spCourseType = (Spinner) findViewById(R.id.spCourseType);
		upSelectCourseBtn = (Button) findViewById(R.id.upSelectCourseBtn);
		nextSelectCourseBtn = (Button) findViewById(R.id.nextSelectCourseBtn);
		selectCourseLV = (ListView) findViewById(R.id.selectCourseLV);
		headView = LayoutInflater.from(this).inflate(
				R.layout.view_select_course_listview_item_head, null);
		selectCourseLV.addHeaderView(headView);
		initLayoutParams();
	}

	private void initLayoutParams() {
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
				(int) (170 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams.setMargins((int) (30 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		upSelectCourseBtn.setLayoutParams(buttonParams);
		nextSelectCourseBtn.setLayoutParams(buttonParams);
		LinearLayout.LayoutParams buttonParams2 = new LinearLayout.LayoutParams(
				(int) (80 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams2.setMargins((int) (10 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		pageSelectCourseBtn.setLayoutParams(buttonParams2);
		LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				(int) (80 * BaseActivity.heightScale));
		searchParams.setMargins((int) (3 * BaseActivity.widthScale), 0, 0, 0);
		tvKeyWords.setLayoutParams(searchParams);
		tvCourseType.setLayoutParams(searchParams);

		LinearLayout.LayoutParams searchbuttonParams = new LinearLayout.LayoutParams(
				(int) (40 * BaseActivity.widthScale),
				(int) (58 * BaseActivity.heightScale));
		searchbuttonParams.setMargins((int) (5 * BaseActivity.widthScale),
				(int) (1 * BaseActivity.heightScale), 0,
				(int) (0.5 * BaseActivity.heightScale));
		searchBT.setLayoutParams(searchbuttonParams);

		LinearLayout.LayoutParams searchETParams = new LinearLayout.LayoutParams(
				(int) (120 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		searchETParams.setMargins((int) (2 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		contentET.setLayoutParams(searchETParams);
	}

	private SpinnerAdapter creatSpinnerAdapter(int arraysRes) {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, arraysRes, android.R.layout.simple_spinner_dropdown_item);
		return adapter;
	}

	public void initData() {
		spCourseType.setAdapter(creatSpinnerAdapter(R.array.classes));
		spCourseType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				getCourseListRequest = new GetCourseListRequest(handler,
						SelectCourseActivity.this);
				getCourseListRequest.setType(String.valueOf(position - 1),
						contentET.getText().toString().trim());
				getCourseListRequest.setPage(page + 1);
				getCourseListRequest.sendRequest();
				playProgressDialog(mContext);
				handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		upSelectCourseBtn.setOnClickListener(this);
		nextSelectCourseBtn.setOnClickListener(this);
		searchBT.setOnClickListener(this);
		selectCourseAdapter = new SelectCourseAdapter(this);
		selectCourseAdapter.notifyDataSetChanged();
		page = 0;
		getCourseListRequest = new GetCourseListRequest(handler,
				SelectCourseActivity.this);
		getCourseListRequest.setType("-1", "");
		getCourseListRequest.setPage(page + 1);
		getCourseListRequest.sendRequest();
		playProgressDialog(mContext);
		handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
		// checkButton();
		setPage(page + 1);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissProgressDialog();
	}

	public void onClick(View v) {
		if (ButtonClickUtils.isFastDoubleClick()) {
			return;
		}
		if (v == upSelectCourseBtn) {
			if (page >= 1) {
				page--;
				setPage(page + 1);
				getCourseListRequest = new GetCourseListRequest(handler,
						SelectCourseActivity.this);
				getCourseListRequest.setType(String.valueOf(spCourseType
						.getSelectedItemPosition() - 1), contentET.getText()
						.toString().trim());
				getCourseListRequest.setPage(page + 1);
				getCourseListRequest.sendRequest();
				playProgressDialog(mContext);
				handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
			} else {
				return;
			}
			// checkButton();
			// upView();
		} else if (v == nextSelectCourseBtn) {
			if (selectCourseList.size() == 4) {
				page++;
				setPage(page + 1);
				getCourseListRequest = new GetCourseListRequest(handler,
						SelectCourseActivity.this);
				getCourseListRequest.setType(String.valueOf(spCourseType
						.getSelectedItemPosition() - 1), contentET.getText()
						.toString().trim());
				getCourseListRequest.setPage(page + 1);
				getCourseListRequest.sendRequest();
				playProgressDialog(mContext);
				handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
			} else {
				return;
			}
			// checkButton();
			// nextView();
		} else if (v == searchBT) {
			page = 0;
			setPage(page + 1);
			getCourseListRequest = new GetCourseListRequest(handler,
					SelectCourseActivity.this);
			getCourseListRequest.setType(
					String.valueOf(spCourseType.getSelectedItemPosition() - 1),
					contentET.getText().toString().trim());
			getCourseListRequest.setPage(page + 1);
			getCourseListRequest.sendRequest();
			playProgressDialog(mContext);
			handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
			// checkButton();
		}
	}

	// public void checkButton() {
	// // 索引值小于等于0，表示不能向前翻页了，以经到了第一页了。
	// // 将向前翻页的按钮设为不可用。
	// if (page <= 0) {
	// if (Integer.parseInt(user.getCourse_total_number()) <= VIEW_COUNT) {
	// upSelectCourseBtn.setEnabled(false);
	// nextSelectCourseBtn.setEnabled(false);
	// } else {
	// upSelectCourseBtn.setEnabled(false);
	// nextSelectCourseBtn.setEnabled(true);
	// }
	// }
	// // 值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
	// // 将向后翻页的按钮设为不可用。
	// else if (Integer.parseInt(user.getCourse_total_number()) - page
	// * VIEW_COUNT <= VIEW_COUNT) {
	// nextSelectCourseBtn.setEnabled(false);
	// upSelectCourseBtn.setEnabled(true);
	// }
	// // 否则将2个按钮都设为可用的。
	// else {
	// upSelectCourseBtn.setEnabled(true);
	// nextSelectCourseBtn.setEnabled(true);
	// }
	// }

	class SelectCourseAdapter extends BaseAdapter {

		private Context mContext;

		private LayoutInflater inflater;

		private List<SelectCourseEntity> list = new ArrayList<SelectCourseEntity>();

		public SelectCourseAdapter(Context context) {
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
						R.layout.view_select_course_listview_item, null);
				holder.layoutCourseItem = (LinearLayout) convertview
						.findViewById(R.id.layoutCourseItem);
				holder.tvCourseName = (TextView) convertview
						.findViewById(R.id.tvCourseName);
				holder.tvCoursePerson = (TextView) convertview
						.findViewById(R.id.tvCoursePerson);
				holder.tvCourseStyle = (TextView) convertview
						.findViewById(R.id.tvCourseStyle);
				holder.tvCourseScore = (TextView) convertview
						.findViewById(R.id.tvCourseScore);
				holder.tvCourseType = (TextView) convertview
						.findViewById(R.id.tvCourseType);
				holder.btnCourseChoose = (Button) convertview
						.findViewById(R.id.btnCourseChoose);
				holder.btnCourseIntroduce = (Button) convertview
						.findViewById(R.id.btnCourseIntroduce);
				AbsListView.LayoutParams layoutCourseParams = new AbsListView.LayoutParams(
						LayoutParams.FILL_PARENT,
						(int) (100 * BaseActivity.heightScale));
				holder.layoutCourseItem.setLayoutParams(layoutCourseParams);
				convertview.setTag(holder);
			} else {
				holder = (ViewHolder) convertview.getTag();
			}

			holder.tvCourseName.setText(list.get(position).getCourse_name());
			holder.tvCoursePerson
					.setText(list.get(position).getCourse_person());
			holder.tvCourseStyle.setText(list.get(position).getCourse_style());
			if (list.get(position).getCourse_score() != null) {
				if (list.get(position).getCourse_score().startsWith(".")) {
					holder.tvCourseScore.setText("0"
							+ list.get(position).getCourse_score());
				} else {
					holder.tvCourseScore.setText(list.get(position)
							.getCourse_score());
				}
			} else {
				holder.tvCourseScore.setText(list.get(position)
						.getCourse_score());
			}
			holder.tvCourseType.setText(list.get(position).getCourse_type());
			if ("00000000".equals(list.get(position + index * VIEW_COUNT)
					.getCourse_id())) {
				holder.btnCourseChoose.setVisibility(View.INVISIBLE);
				holder.btnCourseIntroduce.setVisibility(View.INVISIBLE);
			} else {
				holder.btnCourseChoose.setVisibility(View.VISIBLE);
				holder.btnCourseIntroduce.setVisibility(View.VISIBLE);
			}
			holder.btnCourseChoose.setOnClickListener(new ButtonClickListener(
					position + index * VIEW_COUNT, holder));

			holder.btnCourseIntroduce
					.setOnClickListener(new ButtonClickListener(position
							+ index * VIEW_COUNT, holder));
			return convertview;
		}

		class ViewHolder {
			LinearLayout layoutCourseItem;
			TextView tvCourseName;
			TextView tvCoursePerson;
			TextView tvCourseStyle;
			TextView tvCourseScore;
			TextView tvCourseType;
			Button btnCourseChoose;
			Button btnCourseIntroduce;
		}

		public void setData(List<SelectCourseEntity> list) {
			this.list = list;
		}

		class ButtonClickListener implements OnClickListener {

			private int position;

			private ViewHolder holder;

			private AddCourseRequest addCourseRequest;

			public ButtonClickListener(int position, ViewHolder holder) {
				this.position = position;
				this.holder = holder;
			}

			@Override
			public void onClick(View arg0) {
				if (arg0 == holder.btnCourseChoose) {

					AlertDialog.Builder builder = new AlertDialog.Builder(
							SelectCourseActivity.this);
					builder.setCancelable(false);
					builder.setTitle("提示：");
					builder.setMessage("确定选修此课程？");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									flagBean.setIsFromSelectCourse("2");
									addCourseRequest = new AddCourseRequest(
											handler);
									addCourseRequest.setCourseId(
											list.get(position).getCourse_id(),
											SelectCourseActivity.this);
									addCourseRequest.sendRequest();
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

				} else if (arg0 == holder.btnCourseIntroduce) {
					Bundle bundle = new Bundle();
					Intent intent = new Intent(getApplicationContext(),
							CourseDescriptionActivity.class);
					bundle.putString("course_id", list.get(position)
							.getCourse_id());
					intent.putExtras(bundle);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}
			}
		}

	}

}
