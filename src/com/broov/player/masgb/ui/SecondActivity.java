package com.broov.player.masgb.ui;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.entity.NetworkProber;
import com.broov.player.masgb.handler.ExitAppUtils;
import com.broov.player.masgb.view.SlideMenu;

@SuppressWarnings("deprecation")
public class SecondActivity extends TabActivity implements OnClickListener {
	private SlideMenu slideMenu;
	private TabHost tabHost;
	private Intent intent1, intent2;
	public static TabActivity MainActivity2;
	private TextView tvLearning;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	@SuppressWarnings("unused")
	private View.OnTouchListener gestureListener;
	private int currentView = 0;
	private static int maxTabIndex = 5;
	private TextView tvTitle;
	private ImageView imgTitleBack;
	private TextView tvName;
	public final float WIDTH = 540;
	public final float HEIGHT = 960;
	private static float screenWidth;
	private static float screenHeight;
	private static float dendity;
	public static float widthScale;
	public static float heightScale;
	private DisplayMetrics dm;
	private WindowManager manager;
	private TextView tvDeleteVideo;
	private TextView tvPlan;
	private TextView tvSpecialTopic;
	private TextView tvBBS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.secondmain);
		ExitAppUtils.getInstance().addActivity(this);
		MainActivity2 = this;
		manager = this.getWindowManager();
		dm = getResources().getDisplayMetrics();
		dendity = dm.density;
		screenWidth = manager.getDefaultDisplay().getWidth();
		screenHeight = manager.getDefaultDisplay().getHeight();
		widthScale = screenWidth / 540;
		heightScale = screenHeight / 960;
		initHolder();
		initData();
		Bundle bundle = getIntent().getExtras();
		intent1 = new Intent(SecondActivity.this, DownloadingActivity.class);
		// intent2 = new Intent(SecondActivity.this, FinishActivity.class);
		intent2 = new Intent(SecondActivity.this, DeleteDocActivity.class);

		if (bundle.getString("from") != null) {
			intent1.putExtra("from", bundle.getString("from"));
			intent2.putExtra("from", bundle.getString("from"));
		}
		LayoutInflater inflater = LayoutInflater.from(SecondActivity.this);
		View view1 = inflater.inflate(R.layout.tabitem_4, null);
		// View view2 = inflater.inflate(R.layout.tabitem_5, null);
		View view2 = inflater.inflate(R.layout.tabitem_6, null);

		tabHost = getTabHost();
		TabSpec tabSpec1 = tabHost.newTabSpec("tab1").setIndicator(view2)
				.setContent(intent2);
		tabHost.addTab(tabSpec1);
		// TabSpec tabSpec2 = tabHost.newTabSpec("tab2").setIndicator(view2)
		// .setContent(intent2);
		// tabHost.addTab(tabSpec2);
		TabSpec tabSpec2 = tabHost.newTabSpec("tab2").setIndicator(view1)
				.setContent(intent1);
		tabHost.addTab(tabSpec2);
		bindListener();

	}

	private void initData() {
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};
		tvTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (slideMenu.isMainScreenShowing()) {
					slideMenu.openMenu();
				} else {
					slideMenu.closeMenu();
				}
			}
		});

		imgTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (slideMenu.isMainScreenShowing()) {
					slideMenu.openMenu();
				} else {
					slideMenu.closeMenu();
				}
			}
		});
	}

	private void initHolder() {
		tvBBS = (TextView) findViewById(R.id.tvBBS);
		tvSpecialTopic = (TextView) findViewById(R.id.tvSpecialTopic);
		tvPlan = (TextView) findViewById(R.id.tvPlan);
		tvDeleteVideo = (TextView) findViewById(R.id.tvDeleteVideo);
		gestureDetector = new GestureDetector(new MyGestureDetector());
		tvLearning = (TextView) findViewById(R.id.tvLearning);
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu2);
		tvTitle = (TextView) findViewById(R.id.tvTitle2);
		imgTitleBack = (ImageView) findViewById(R.id.imgTitleBack2);
		tvName = (TextView) findViewById(R.id.tvName2);
		//initLayoutParams();
	}

	private void initLayoutParams() {
		RelativeLayout.LayoutParams imgTitleBackParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		imgTitleBackParams.setMargins((int) (10 * SecondActivity.widthScale),
				(int) (13 * SecondActivity.heightScale), 0, 0);
		imgTitleBack.setLayoutParams(imgTitleBackParams);

		RelativeLayout.LayoutParams tvTitleParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tvTitleParams.setMargins((int) (30 * SecondActivity.widthScale),
				(int) (2 * SecondActivity.heightScale), 0, 0);
		tvTitle.setLayoutParams(tvTitleParams);

		RelativeLayout.LayoutParams tvNameParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tvNameParams.setMargins((int) (238 * SecondActivity.widthScale),
				(int) (2 * SecondActivity.heightScale), 0,
				(int) (10 * SecondActivity.heightScale));
		tvName.setLayoutParams(tvNameParams);
	}

	private void bindListener() {
		tvSpecialTopic.setOnClickListener(this);
		tvLearning.setOnClickListener(this);
		tvDeleteVideo.setOnClickListener(this);
		tvPlan.setOnClickListener(this);
		tvBBS.setOnClickListener(this);
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			TabHost tabHost = getTabHost();
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					if (currentView == maxTabIndex) {
						currentView = 0;
					} else {
						currentView++;
					}
					tabHost.setCurrentTab(currentView);
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					if (currentView == 0) {
						currentView = maxTabIndex;
					} else {
						currentView--;
					}
					tabHost.setCurrentTab(currentView);
				}
			} catch (Exception e) {
			}
			return false;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			event.setAction(MotionEvent.ACTION_CANCEL);
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		if (v == tvLearning) {
			if (!NetworkProber.isNetworkAvailable(getApplicationContext())) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SecondActivity.this);
				builder.setCancelable(false);
				builder.setTitle("提示：");
				builder.setMessage("没有网络连接，是否观看离线视频？");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								slideMenu.closeMenu();
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(getApplicationContext(),
										"请开启网络连接，并重新进入应用！", Toast.LENGTH_LONG)
										.show();
							}
						});
				builder.show();
			} else {
				slideMenu.closeMenu();
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}

		} else if (v == tvDeleteVideo) {
			slideMenu.closeMenu();
		} else if (v == tvPlan) {
			slideMenu.closeMenu();
			Intent intent = new Intent(getApplicationContext(),
					ThirdActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
		} else if (v == tvSpecialTopic) {
			slideMenu.closeMenu();
			Intent intent = new Intent(getApplicationContext(),
					SpecialTopicActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
		} else if (v == tvBBS) {
			if (!NetworkProber.isNetworkAvailable(getApplicationContext())) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SecondActivity.this);
				builder.setCancelable(false);
				builder.setTitle("提示：");
				builder.setMessage("没有网络连接，是否观看离线视频？");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								slideMenu.closeMenu();
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(getApplicationContext(),
										"请开启网络连接，并重新进入应用！", Toast.LENGTH_LONG)
										.show();
							}
						});
				builder.show();
			} else {
				slideMenu.closeMenu();
				Intent intent = new Intent(getApplicationContext(),
						FourthActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}
		}
	}

	public static float formatTextSize(float textSize) {
		if (screenWidth <= screenHeight) {
			return (textSize * widthScale) / dendity;
		} else {
			return (textSize * heightScale) / dendity;
		}
	}

}
