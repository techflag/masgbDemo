package com.broov.player.masgb.ui;

import android.app.TabActivity;
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

import com.broov.player.R;
import com.broov.player.masgb.view.SlideMenu;

@SuppressWarnings("deprecation")
public class ThirdActivity extends TabActivity implements OnClickListener {
	private SlideMenu slideMenu;
	private TabHost tabHost;
	private Intent intent1;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thirdmain);
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

		intent1 = new Intent(ThirdActivity.this, StudyPlanActivity.class);

		LayoutInflater inflater = LayoutInflater.from(ThirdActivity.this);
		View view1 = inflater.inflate(R.layout.tabitem_7, null);

		tabHost = getTabHost();
		TabSpec tabSpec1 = tabHost.newTabSpec("tab1").setIndicator(view1)
				.setContent(intent1);
		tabHost.addTab(tabSpec1);
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
		tvSpecialTopic = (TextView) findViewById(R.id.tvSpecialTopic);
		tvPlan = (TextView) findViewById(R.id.tvPlan);
		tvDeleteVideo = (TextView) findViewById(R.id.tvDeleteVideo);
		gestureDetector = new GestureDetector(new MyGestureDetector());
		tvLearning = (TextView) findViewById(R.id.tvLearning);
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu3);
		tvTitle = (TextView) findViewById(R.id.tvTitle3);
		imgTitleBack = (ImageView) findViewById(R.id.imgTitleBack3);
		tvName = (TextView) findViewById(R.id.tvName3);
		initLayoutParams();
	}

	private void initLayoutParams() {
		RelativeLayout.LayoutParams imgTitleBackParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		imgTitleBackParams.setMargins((int) (10 * ThirdActivity.widthScale),
				(int) (13 * ThirdActivity.heightScale), 0, 0);
		imgTitleBack.setLayoutParams(imgTitleBackParams);

		RelativeLayout.LayoutParams tvTitleParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tvTitleParams.setMargins((int) (30 * ThirdActivity.widthScale),
				(int) (2 * ThirdActivity.heightScale), 0, 0);
		tvTitle.setLayoutParams(tvTitleParams);

		RelativeLayout.LayoutParams tvNameParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tvNameParams.setMargins((int) (238 * ThirdActivity.widthScale),
				(int) (2 * ThirdActivity.heightScale), 0,
				(int) (10 * ThirdActivity.heightScale));
		tvName.setLayoutParams(tvNameParams);
	}

	private void bindListener() {
		tvSpecialTopic.setOnClickListener(this);
		tvLearning.setOnClickListener(this);
		tvDeleteVideo.setOnClickListener(this);
		tvPlan.setOnClickListener(this);
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
			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
		} else if (v == tvDeleteVideo) {
			slideMenu.closeMenu();
			Intent intent = new Intent(getApplicationContext(),
					SecondActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
		} else if (v == tvPlan) {
			slideMenu.closeMenu();
		} else if (v == tvSpecialTopic) {
			slideMenu.closeMenu();
			Intent intent = new Intent(getApplicationContext(),
					SpecialTopicActivity.class);
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
	}

}
