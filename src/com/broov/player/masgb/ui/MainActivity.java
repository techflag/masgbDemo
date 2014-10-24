package com.broov.player.masgb.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

import com.broov.player.R;
import com.broov.player.masgb.handler.ExitAppUtils;
import com.broov.player.masgb.view.SlideMenu;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnClickListener {
	private SlideMenu slideMenu;
	private TabHost tabHost;
	private Intent intent1, intent2, intent3;
	public static TabActivity MainActivity2;
	private TextView tvLearning;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	int currentView = 0;
	private static int maxTabIndex = 5;
	private TextView tvTitle;
	private ImageView imgTitleBack;
	private TextView tvName;
	public final float WIDTH = 552;

	public final float HEIGHT = 1024;

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
	
	
	private TextView tvName_main;
	private TextView tvTitle_main;
	//private TextView tvBBS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mymain);
		ExitAppUtils.getInstance().addActivity(this);
		MainActivity2 = this;
		
		
		
		manager = this.getWindowManager();
		dm = getResources().getDisplayMetrics();
		dendity = dm.density;
		screenWidth = manager.getDefaultDisplay().getWidth();
		screenHeight = manager.getDefaultDisplay().getHeight();
		
		Log.i("Metrisc", screenWidth+"---"+screenHeight);
		
		widthScale = screenWidth / 552;
		heightScale = screenHeight / 1024;
		
		//widthScale = screenWidth / 540;
		//heightScale = screenHeight / 960;
		//1024 552

		initHolder();
		initData();

		intent1 = new Intent(MainActivity.this, CoursingActivity.class);
		intent2 = new Intent(MainActivity.this, CoursedActivity.class);
		intent3 = new Intent(MainActivity.this, SelectCourseActivity.class);

		LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
		View view1 = inflater.inflate(R.layout.tabitem_1, null);
		View view2 = inflater.inflate(R.layout.tabitem_2, null);
		View view3 = inflater.inflate(R.layout.tabitem_3, null);

		tabHost = getTabHost();
		TabSpec tabSpec1 = tabHost.newTabSpec("tab1").setIndicator(view1)
				.setContent(intent1);
		tabHost.addTab(tabSpec1);
		TabSpec tabSpec2 = tabHost.newTabSpec("tab2").setIndicator(view2)
				.setContent(intent2);
		tabHost.addTab(tabSpec2);
		TabSpec tabSpec3 = tabHost.newTabSpec("tab3").setIndicator(view3)
				.setContent(intent3);
		tabHost.addTab(tabSpec3);
		bindListener();

	}

	/**
	 * 设置头部文字大小、高度等
	 * @param widthScale
	 */
	private void setHeadFontSize(int widthScale){
		
		
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
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
		tvTitle = (TextView) findViewById(R.id.tvTitle_main);
		imgTitleBack = (ImageView) findViewById(R.id.imgTitleBack_main);
		tvName = (TextView) findViewById(R.id.tvName_main);
		//initLayoutParams();
	}

	private void initLayoutParams() {

		RelativeLayout.LayoutParams imgTitleBackParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		imgTitleBackParams.setMargins((int) (10 * MainActivity.widthScale),
				(int) (13 * MainActivity.heightScale), 0, 0);
		imgTitleBack.setLayoutParams(imgTitleBackParams);

		RelativeLayout.LayoutParams tvTitleParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tvTitleParams.setMargins((int) (30 * MainActivity.widthScale),
				(int) (5 * MainActivity.heightScale), 0, 0);
		tvTitle.setLayoutParams(tvTitleParams);

		RelativeLayout.LayoutParams tvNameParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tvNameParams.setMargins((int) (238 * MainActivity.widthScale),
				(int) (5 * MainActivity.heightScale), 0, 0);
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
			slideMenu.closeMenu();
		} else if (v == tvDeleteVideo) {
			slideMenu.closeMenu();
			Intent intent = new Intent(getApplicationContext(),
					SecondActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			Bundle bundle = new Bundle();
			bundle.putString("from", "main");
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
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
			slideMenu.closeMenu();
			Intent intent = new Intent(getApplicationContext(),
					FourthActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
		}
	}

	public static float formatTextSize(float textSize) {
		if (screenWidth <= screenHeight) {
			return (textSize * widthScale) / dendity;
		} else {
			return (textSize * heightScale) / dendity;
		}
		// return (textSize / scaledDensity + 0.5f) * widthScale;
	}

}
