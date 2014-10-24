package com.broov.player.masgb.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.broov.player.R;
import com.broov.player.masgb.handler.ExitAppUtils;
import com.broov.player.masgb.net.request.CheckIsLoginRequest;
import com.broov.player.masgb.utils.HandleMessageState;
import com.igexin.slavesdk.MessageManager;

public class BaseActivity extends Activity {
	public final float WIDTH = 540;

	public final float HEIGHT = 960;

	private static float screenWidth;

	private static float screenHeight;

	private static float dendity;

	public static float widthScale;

	public static float heightScale;

	private DisplayMetrics dm;

	private WindowManager manager;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleMessageState.NEED_LOGIN_AGAIN:
				AlertDialog.Builder builder = new AlertDialog.Builder(
						BaseActivity.this);
				builder.setCancelable(false);
				builder.setTitle("提示：");
				builder.setMessage("账号已在其他地方使用，需要重新登陆!");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										BaseActivity.this,
										LoginActivity.class);
								startActivity(intent);
								ExitAppUtils.getInstance().exit();
								finish();
							}
						});
//				builder.setNegativeButton("取消",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int which) {
////								android.os.Process
////										.killProcess(android.os.Process.myPid()); // 获取PID
////								System.exit(0); // 常规java、c#的标准退出法，返回值为0代表正常退出
//								finish();
//							}
//						});
				builder.show();
				break;
			case HandleMessageState.NO_NEED_LOGIN_AGAIN:
				break;
			default:
				break;
			}
		};
	};

	private CheckIsLoginRequest checkIsLoginRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MessageManager.getInstance().initialize(this.getApplicationContext());
		manager = this.getWindowManager();
		dm = getResources().getDisplayMetrics();
		dendity = dm.density;
		screenWidth = manager.getDefaultDisplay().getWidth();
		screenHeight = manager.getDefaultDisplay().getHeight();
		widthScale = screenWidth / 540;
		heightScale = screenHeight / 960;
		checkIsLoginRequest = new CheckIsLoginRequest(handler,BaseActivity.this);
		checkIsLoginRequest.sendRequest();
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
	}

//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		ExitAppUtils.getInstance().exit();
//	}

	public void setSubPageTitle(int resId, boolean isShowBack) {
		setSubPageTitle(getResources().getString(resId), isShowBack);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkIsLoginRequest = new CheckIsLoginRequest(handler,BaseActivity.this);
		checkIsLoginRequest.sendRequest();
	}
	public void setSubPageTitle(CharSequence title, boolean isShowBack) {
		TextView tvName = ((TextView) findViewById(R.id.tvName));
		TextView tvBack = ((TextView) findViewById(R.id.tvBack));
		ImageView imgTitleBack = (ImageView) findViewById(R.id.imgTitleBack);
		ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
//		RelativeLayout titlebarRelativeLayout = (RelativeLayout) findViewById(R.id.titlebar);
//		tvBack.setLayoutParams(new RelativeLayout.LayoutParams(
//				LayoutParams.FILL_PARENT,
//				(int) (140 * BaseActivity.heightScale)));
//		tvBack.setTextSize(BaseActivity.formatTextSize(60));
//		tvName.setTextSize(BaseActivity.formatTextSize(60));
		if (tvName != null) {
			tvName.setText(title);
		}

//		RelativeLayout.LayoutParams titleBackImgParams = new RelativeLayout.LayoutParams(
//				android.widget.RelativeLayout.LayoutParams.FILL_PARENT,
//				(int) (88 * BaseActivity.heightScale));
//		titleBackImgParams.setMargins(0, 0, 0, 0);
//		titlebarRelativeLayout.setLayoutParams(titleBackImgParams);
//
//		RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(
//				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		backParams.setMargins((int) (5 * BaseActivity.widthScale),
//				(int) (15 * BaseActivity.heightScale), 0, 0);
//		imgTitleBack.setLayoutParams(backParams);
//
//		RelativeLayout.LayoutParams backImgParams = new RelativeLayout.LayoutParams(
//				LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
//		backImgParams.setMargins((int) (30 * BaseActivity.widthScale), 0, 0, 0);
//		imageView2.setLayoutParams(backImgParams);

//		RelativeLayout.LayoutParams backTextParams = new RelativeLayout.LayoutParams(
//				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		backTextParams.setMargins((int) (35 * BaseActivity.widthScale),
//				(int) (5 * BaseActivity.heightScale), 0, 0);
//		tvBack.setLayoutParams(backTextParams);
//
//		RelativeLayout.LayoutParams backTitleParams = new RelativeLayout.LayoutParams(
//				android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT,
//				android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
//		backTitleParams.setMargins((int) (100 * BaseActivity.widthScale),
//				(int) (5 * BaseActivity.heightScale), 0, 0);
//		tvName.setLayoutParams(backTitleParams);
		if (isShowBack) {
			imgTitleBack.setVisibility(View.VISIBLE);
			tvBack.setVisibility(View.VISIBLE);
		} else {
			imgTitleBack.setVisibility(View.GONE);
			tvBack.setVisibility(View.GONE);
		}
		imgTitleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tvBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void setSubPageTitle(CharSequence title, boolean isShowBack,
			OnClickListener listener) {
		TextView tv = ((TextView) findViewById(R.id.tvName));
		ImageView back = (ImageView) findViewById(R.id.imgTitleBack);
//		tv.setLayoutParams(new RelativeLayout.LayoutParams(
//				LayoutParams.FILL_PARENT,
//				(int) (140 * BaseActivity.heightScale)));
//		tv.setTextSize(BaseActivity.formatTextSize(70));
		if (tv != null) {
			tv.setText(title);
		}

//		RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(
//				(int) (140 * BaseActivity.widthScale),
//				(int) (140 * BaseActivity.heightScale));
//		backParams.setMargins((int) (10 * BaseActivity.heightScale), 0,
//				(int) (10 * BaseActivity.heightScale), 0);
//		back.setLayoutParams(backParams);
		if (isShowBack) {
			back.setVisibility(View.VISIBLE);
		} else {
			back.setVisibility(View.GONE);

		}
		back.setOnClickListener(listener);
	}

	public static float formatTextSize(float textSize) {
		if (screenWidth <= screenHeight) {
			return (textSize * widthScale) / dendity;
		} else {
			return (textSize * heightScale) / dendity;
		}
	}
}
