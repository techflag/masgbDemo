package com.broov.player.masgb.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.broov.player.masgb.entity.CourseListEntity;
import com.broov.player.masgb.handler.ExitAppUtils;
import com.broov.player.masgb.net.request.GetCourseDocListRequest;
import com.broov.player.masgb.utils.HandleMessageState;
import com.broov.player.masgb.utils.MyIntents;

public class CourseDocListActivity extends BaseActivity implements
		OnClickListener {
	private GetCourseDocListRequest getCourseDocListRequest;
	private CoursingAdapter coursingAdapter;
	private List<CourseListEntity> courseDocList = new ArrayList<CourseListEntity>();
	private ListView courseDocLV;
	private View headView;
	private Button updateCourseDocBtn;
	private Button finishBtn;
	private TextView courseFinishWarningTV;
	// 用于显示每列5个Item项。
	private int VIEW_COUNT = 4;
	private int index = 0;
	private Bundle bundle;
	private int DISMISSGAP = 30;
	private String course_id;
	/**
	 * 总时长
	 */
	private String course_time_length;
	/**
	 * 已学时长
	 */
	private String course_length;
	private String course_name;
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
			case HandleMessageState.GET_COURSE_DOC_LIST_SUCCESS:
				courseDocList.clear();
				courseDocList = (List<CourseListEntity>) msg.obj;
				isButtonOrTextViewShow(courseDocList);
				adapterChangeListener();
				dismissProgressDialog();
				handlerTimer.removeCallbacks(runnableTimer);
				break;
			case HandleMessageState.ADD_DOWNLOAD_SUCCESS:
				Map<String, String> map = (Map<String, String>) msg.obj;
				Intent downloadIntent = new Intent(
						"com.broov.player.masgb.service.IDownloadService");
				downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
				downloadIntent.putExtra(MyIntents.URL, map.get("uri"));
				downloadIntent.putExtra(MyIntents.FILE_NAME,
						map.get("filename"));

				startService(downloadIntent);

				// //
				// // SHAREDPREFERENCES = GETSHAREDPREFERENCES("DOWNDATA",
				// // CONTEXT.MODE_PRIVATE);
				// // // GETSTRING()第二个参数为缺省值，如果PREFERENCE中不存在该KEY，将返回缺省值
				// // STRING NAME = SHAREDPREFERENCES.GETSTRING("NAME", "");
				//
				// // /
				// // CREATEDOWNLOADLIST();
				//
				// // SHAREDPREFERENCES = GETSHAREDPREFERENCES(
				// // "DOWNDATA", CONTEXT.MODE_PRIVATE);
				// // EDITOR EDITOR = SHAREDPREFERENCES.EDIT();// 获取编辑器
				// // EDITOR.PUTSTRING("NAME", "");
				// // EDITOR.COMMIT();// 提交修改
				Intent intent2 = new Intent(getApplicationContext(),
						SecondActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("from", "courseDocListActivity");
				intent2.putExtras(bundle);
				intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent2);
				finish();
				break;
			default:
				break;
			}
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.couring_doc_layout);
		setSubPageTitle("课件列表", true);
		mContext = CourseDocListActivity.this;
		ExitAppUtils.getInstance().addActivity(this);
		initHolder();
		initData();
	}

	protected void isButtonOrTextViewShow(List<CourseListEntity> courseDocList2) {
		if (bundle.get("from").equals("coursingActivity")) {
			// TODO 加入一个时间判断
			if (Integer.valueOf(course_length) < Integer
					.valueOf(course_time_length)) {
				return;
			}
			for (int i = 0; i < courseDocList2.size(); i++) {
				if (courseDocList2.get(i).getCourseStatus().equals("未读")) {
					return;
				}
			}
			if (bundle.getString("course_type").equals("必修")
					&& bundle.getString("from").equals("coursingActivity")) {
				courseFinishWarningTV.setVisibility(View.VISIBLE);
			} else if (bundle.getString("course_type").equals("选修")
					&& bundle.getString("from").equals("coursingActivity")) {
				// finishBtn.setVisibility(View.VISIBLE);
				courseFinishWarningTV.setVisibility(View.VISIBLE);
				return;
			}
		}
		// finishBtn.setVisibility(View.VISIBLE);
		return;
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
		coursingAdapter.setData(courseDocList);
		coursingAdapter.notifyDataSetChanged();
		courseDocLV.setAdapter(coursingAdapter);
	}

	private void initHolder() {
		courseFinishWarningTV = (TextView) findViewById(R.id.courseFinishWarningTV);
		updateCourseDocBtn = (Button) findViewById(R.id.updateCourseDocBtn);
		finishBtn = (Button) findViewById(R.id.finishBtn);
		courseDocLV = (ListView) findViewById(R.id.courseDocLV);
		headView = LayoutInflater.from(this).inflate(
				R.layout.view_coursing_doc_listview_item_head, null);
		courseDocLV.addHeaderView(headView);
		initLayoutParams();
	}

	private void initLayoutParams() {
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
				(int) (230 * BaseActivity.widthScale),
				(int) (80 * BaseActivity.heightScale));
		buttonParams.setMargins((int) (15 * BaseActivity.widthScale),
				(int) (5 * BaseActivity.heightScale), 0, 0);
		finishBtn.setLayoutParams(buttonParams);
		updateCourseDocBtn.setLayoutParams(buttonParams);

	}

	public void initData() {
		bundle = getIntent().getExtras();
		// if (bundle.getString("course_type").equals("必修")
		// && bundle.getString("from").equals("coursingActivity")) {
		// courseFinishWarningTV.setVisibility(View.VISIBLE);
		// } else if (bundle.getString("course_type").equals("选修")
		// && bundle.getString("from").equals("coursingActivity")) {
		// finishBtn.setVisibility(View.VISIBLE);
		// }
		course_id = bundle.getString("course_id");
		course_time_length = bundle.getString("course_time_length");
		course_length = bundle.getString("course_length");
		course_name = bundle.getString("CourseName");
		updateCourseDocBtn.setOnClickListener(this);
		finishBtn.setOnClickListener(this);
		coursingAdapter = new CoursingAdapter(this);
		getCourseDocListRequest = new GetCourseDocListRequest(handler,
				CourseDocListActivity.this);
		getCourseDocListRequest.setCourse_id(course_id);
		getCourseDocListRequest.sendRequest();
		playProgressDialog(mContext);
		handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
	}

	public void onClick(View v) {
		if (v == updateCourseDocBtn) {
			getCourseDocListRequest = new GetCourseDocListRequest(handler,
					CourseDocListActivity.this);
			getCourseDocListRequest.setCourse_id(course_id);
			getCourseDocListRequest.sendRequest();
			playProgressDialog(mContext);
			handlerTimer.postDelayed(runnableTimer, 1000 * DISMISSGAP);
		} else if (v == finishBtn) {
			Intent intent = new Intent(getApplicationContext(),
					FinishCourseActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}
	}

	class CoursingAdapter extends BaseAdapter {

		private Context mContext;

		private LayoutInflater inflater;

		private List<CourseListEntity> list = new ArrayList<CourseListEntity>();

		private String filename;

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
						R.layout.view_coursing_doc_listview_item, null);
				holder.layoutCourseItem = (LinearLayout) convertview
						.findViewById(R.id.layoutCourseItem);
				holder.tvCourseName = (TextView) convertview
						.findViewById(R.id.tvCourseName);
				holder.tvCourseStatus = (TextView) convertview
						.findViewById(R.id.tvCourseStatus);
				holder.btnCourseDocLearn = (Button) convertview
						.findViewById(R.id.btnCourseDocLearn);
				holder.btnCourseDocLearnLocal = (Button) convertview
						.findViewById(R.id.btnCourseDocLearnLocal);
				holder.btnCourseDocDownload = (Button) convertview
						.findViewById(R.id.btnCourseDocDownLoad);
				AbsListView.LayoutParams layoutCourseParams = new AbsListView.LayoutParams(
						LayoutParams.FILL_PARENT,
						(int) (120 * BaseActivity.heightScale));
				holder.layoutCourseItem.setLayoutParams(layoutCourseParams);
				// holder.btnCourseDocLearnLocal.setTextSize((int) (25 *
				// BaseActivity.heightScale));
				convertview.setTag(holder);
			} else {
				holder = (ViewHolder) convertview.getTag();
			}
			holder.tvCourseName.setText(list.get(position + index * VIEW_COUNT)
					.getCourseName());
			holder.tvCourseStatus.setText(list.get(
					position + index * VIEW_COUNT).getCourseStatus());
			if ("00000000".equals(list.get(position + index * VIEW_COUNT)
					.getCourse_doc_id())) {
				holder.btnCourseDocLearn.setVisibility(View.INVISIBLE);
				holder.btnCourseDocLearnLocal.setVisibility(View.INVISIBLE);
				holder.btnCourseDocDownload.setVisibility(View.INVISIBLE);
			} else {
				holder.btnCourseDocLearn.setVisibility(View.VISIBLE);
				holder.btnCourseDocLearnLocal.setVisibility(View.VISIBLE);
				holder.btnCourseDocDownload.setVisibility(View.VISIBLE);
			}
			// if(isFileExists(position)){
			// holder.btnCourseDocLearnLocal.setText("离线学习已下载");
			// }else {
			// holder.btnCourseDocLearnLocal.setText("离线学习未下载");
			// }

			holder.btnCourseDocLearn
					.setOnClickListener(new ButtonClickListener(position
							+ index * VIEW_COUNT, holder));
			holder.btnCourseDocLearnLocal
					.setOnClickListener(new ButtonClickListener(position
							+ index * VIEW_COUNT, holder));
			holder.btnCourseDocDownload
					.setOnClickListener(new ButtonClickListener(position
							+ index * VIEW_COUNT, holder));
			return convertview;
		}

		/**
		 * 文件是否存在
		 */
		@SuppressWarnings("unused")
		private boolean isFileExists(int position) {

			// TODO 获得filename。
			String suffix = list.get(position + index * VIEW_COUNT).getUri();
			suffix = suffix.substring(suffix.lastIndexOf(".") + 1);
			// String temp[] = fileName.replaceAll("\\\\", "/").split("/");
			// if (temp.length > 1) {
			// filename = temp[temp.length - 1];
			// }
			// filename=list.get(position).getCourseName()+(position+1)+"."+suffix;
			filename = course_name + "@@@"
					+ list.get(position).getCourse_doc_id() + "." + suffix;
			File file = new File("/storage/sdcard0/干部在线/" + filename);
			if (file.exists()) {
				return true;
			} else {
				return false;
			}

		}

		class ViewHolder {
			TextView tvCourseName;
			TextView tvCourseStatus;
			LinearLayout layoutCourseItem;
			Button btnCourseDocLearn;
			Button btnCourseDocLearnLocal;
			Button btnCourseDocDownload;

		}

		public void setData(List<CourseListEntity> list) {
			this.list = list;
		}

		class ButtonClickListener implements OnClickListener {
			private int position;
			private ViewHolder holder;

			// private String filename;
			private String filename2;
			private boolean isCanShow = true;

			public ButtonClickListener(int position, ViewHolder holder) {
				this.position = position;
				this.holder = holder;
			}

			/**
			 * 判断是否可以播放
			 */
			private void isCanShow(String uri) {
				if (uri.endsWith(".htm+") || uri.endsWith(".html+")
						|| uri.endsWith(".html") || uri.endsWith(".htm")
						|| uri.equals("")) {
					isCanShow = false;
				}
			}

			/**
			 * 获取视频名称
			 */
			private void getFileName(String uri) {

				// TODO 获得filename。
				String suffix = list.get(position).getUri();
				suffix = suffix.substring(suffix.lastIndexOf(".") + 1);
				// String temp[] = fileName.replaceAll("\\\\", "/").split("/");
				// if (temp.length > 1) {
				// filename = temp[temp.length - 1];
				// }
				// filename=list.get(position).getCourseName()+(position+1)+"."+suffix;
				filename2 = course_name + "@@@"
						+ list.get(position).getCourse_doc_id() + "." + suffix;
			}

			@Override
			public void onClick(View arg0) {
				final String uri = makeUri(list.get(
						position + index * VIEW_COUNT).getUri());
				if (arg0 == holder.btnCourseDocLearn) {
					/**
					 * 在线学习
					 */
					isCanShow(uri);
					if (isCanShow) {
						/**
						 * 判断当前网络是否是wifi网络
						 * if(activeNetInfo.getType()==ConnectivityManager
						 * .TYPE_MOBILE) { //判断3G网
						 * 
						 * @param context
						 * @return boolean
						 */

						ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
								.getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo activeNetInfo = connectivityManager
								.getActiveNetworkInfo();
						if (activeNetInfo != null
								&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							Intent intent = new Intent(
									CourseDocListActivity.this,
									VideoPlayerOnLineActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("wareid", list.get(position)
									.getCourse_doc_id());
							bundle.putString("uri", uri);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtras(bundle);
							startActivity(intent);
						} else {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									CourseDocListActivity.this);
							builder.setCancelable(false);
							builder.setTitle("提示：");
							builder.setMessage("当前网络环境不是Wifi，继续观看将扣除手机流量，是否继续观看？");
							builder.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = new Intent(
													CourseDocListActivity.this,
													VideoPlayerOnLineActivity.class);
											Bundle bundle = new Bundle();
											bundle.putString("wareid", list
													.get(position)
													.getCourse_doc_id());
											bundle.putString("uri", uri);
											intent.putExtras(bundle);
											startActivity(intent);
										}
									});
							builder.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									});
							builder.show();
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"对不起，该课件只能用电脑播放！", Toast.LENGTH_SHORT).show();
					}
				} else if (arg0 == holder.btnCourseDocLearnLocal) {

					isCanShow(uri);
					if (isCanShow) {
						getFileName(uri);
						/**
						 * 本地播放
						 */
						File file = new File("/storage/sdcard0/干部在线/"
								+ filename2);
						if (file.exists()) {
							Intent intent1 = new Intent(
									getApplicationContext(),
									VideoPlayerActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("wareid", list.get(position)
									.getCourse_doc_id());
							intent1.putExtras(bundle);
							intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							intent1.putExtra("videofilename",
									"/storage/sdcard0/干部在线/" + filename2);
							startActivity(intent1);
						} else {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									CourseDocListActivity.this);
							builder.setCancelable(false);
							builder.setTitle("提示：");
							builder.setMessage("请确定指定文件夹：干部在线 中是否有此课程视频文件，再重试！");
							builder.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									});
							builder.show();
						}
					} else {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								CourseDocListActivity.this);
						builder.setCancelable(false);
						builder.setTitle("提示：");
						builder.setMessage("对不起，该课件只能用电脑播放！");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
						builder.show();
					}

				} else if (arg0 == holder.btnCourseDocDownload) {
					isCanShow(uri);
					if (isCanShow) {
						getFileName(uri);
						File file = new File("/storage/sdcard0/干部在线/"
								+ filename2);
						if (!file.exists()) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("filename", filename2);
							map.put("uri", uri);
							handler.sendMessage(handler.obtainMessage(
									HandleMessageState.ADD_DOWNLOAD_SUCCESS,
									map));
						}else {
							Toast.makeText(getApplicationContext(),
									"该课件已经存在，不用再次下载.", Toast.LENGTH_SHORT)
									.show();
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"对不起，该课件不是视频格式文件，无法下载！", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}

			private String makeUri(String uri) {
				String strTest = "";
				try {
					strTest = URLEncoder.encode(uri, "UTF-8");
					strTest = strTest.replace("%3A%2F%2F", "://");
					strTest = strTest.replace("%2F", "/");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return strTest;
			}
		}

	}

}
