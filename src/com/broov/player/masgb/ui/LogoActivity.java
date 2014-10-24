package com.broov.player.masgb.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.broov.player.R;

public class LogoActivity extends Activity {
	private Handler mHandler = new Handler();

	private static float dendity;

	public static float widthScale;

	public static float heightScale;

	private DisplayMetrics dm;

	private WindowManager manager;
	
	private static float screenWidth;

	private static float screenHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		manager = this.getWindowManager();
		dm = getResources().getDisplayMetrics();
		dendity = dm.density;
		screenWidth = manager.getDefaultDisplay().getWidth();
		screenHeight = manager.getDefaultDisplay().getHeight();
		Log.i("screenWidth", screenWidth+"---"+screenHeight);
		View view = View.inflate(this, R.layout.logo, null);
		setContentView(view);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		view.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if ((android.os.Build.MODEL).contains("FONEE")) {
							goHome();
						} else {
							//goOther();
							 goHome();
						}
						finish();
					}
				}, 500);
			}
		});

	}

	private void goHome() {
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	/**
	 * 如果不是丰云手机就跳转到这个页面，让他用丰云手机登入
	 */
	private void goOther() {
		Intent intent = new Intent(getApplicationContext(), OtherActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
}
