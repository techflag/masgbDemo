package com.broov.player.masgb.ui;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.bean.FlagBean;
import com.broov.player.masgb.entity.NetworkProber;
import com.broov.player.masgb.net.request.LoginRequest;
import com.broov.player.masgb.net.request.OffLineOpenCourseRequest;
import com.broov.player.masgb.utils.HandleMessageState;
import com.umeng.update.UmengUpdateAgent;

public class LoginActivity extends Activity implements OnClickListener {

	private ProgressDialog progressDialog;
	private Context mContext;
	private Button btnLogin;
	private LoginRequest loginRequest;
	public final float WIDTH = 540;

	public final float HEIGHT = 960;

	private static float screenWidth;

	private static float screenHeight;

	@SuppressWarnings("unused")
	private static float dendity;

	public static float widthScale;

	public static float heightScale;

	private DisplayMetrics dm;

	private WindowManager manager;

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
			Toast.makeText(getApplicationContext(), "信号弱，请重试...",
					Toast.LENGTH_SHORT).show();
		}
	};
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		private SharedPreferences sharedPreferences;
		private Editor editor;

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleMessageState.LOGIN_SUCCESS:
				sharedPreferences = getSharedPreferences("userData",
						Context.MODE_PRIVATE);
				editor = sharedPreferences.edit();
				editor.putString("name", usernameET.getText().toString().trim());
				editor.putString("pwd", pwdET.getText().toString().trim());
				editor.commit();// 提交修改
				handlerTimer.removeCallbacks(runnableTimer);
				dismissProgressDialog();
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
				break;
			case HandleMessageState.LOGIN_FAILURE:
				handlerTimer.removeCallbacks(runnableTimer);
				dismissProgressDialog();
				Toast.makeText(getApplicationContext(), "帐号或密码出错，请核对重新输入！",
						Toast.LENGTH_LONG).show();
				break;
			case HandleMessageState.SERVERLT_ERROR:
				handlerTimer.removeCallbacks(runnableTimer);
				dismissProgressDialog();
				Toast.makeText(getApplicationContext(), "服务器异常！",
						Toast.LENGTH_LONG).show();
				break;
			case HandleMessageState.UPDATE_TIEM_SUCCESS:
				sharedPreferences = getSharedPreferences("upTime",
						Context.MODE_PRIVATE);
				editor = sharedPreferences.edit();// 获取编辑器
				// editor.putString("uptimejson", "");
				editor.remove("uptimejson");// 上传成功，清除保持的xml记录
				editor.commit();// 提交修改
				break;
			default:
				break;
			}
		}

	};
	private EditText usernameET;
	private EditText pwdET;
	private LinearLayout linearLayoutBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		UmengUpdateAgent.update(this);
		manager = this.getWindowManager();
		dm = getResources().getDisplayMetrics();
		dendity = dm.density;
		screenWidth = manager.getDefaultDisplay().getWidth();
		screenHeight = manager.getDefaultDisplay().getHeight();
		widthScale = screenWidth / 540;
		heightScale = screenHeight / 960;
		mContext = LoginActivity.this;
		initHolder();
		initData();
		createSDCardDir();
	}

	// 在SD卡上创建一个文件夹
	public void createSDCardDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// 创建一个文件夹对象，赋值为外部存储器的目录
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String path = sdcardDir.getPath() + "/干部在线";
			File path1 = new File(path);
			if (!path1.exists()) {
				// 若不存在，创建目录，可以在应用启动的时候创建
				path1.mkdirs();
			} else {
				// Toast.makeText(getApplicationContext(), "文件夹已经存在！",
				// Toast.LENGTH_LONG).show();
			}
		} else {
			return;
		}

	}

	private void initData() {
		SharedPreferences sharedPreferences = getSharedPreferences("userData",
				Context.MODE_PRIVATE);
		// getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
		String name = sharedPreferences.getString("name", "");
		String pwd = sharedPreferences.getString("pwd", "");
		usernameET.setText(name);
		pwdET.setText(pwd);
		CharSequence text = pwdET.getText();
		if (text instanceof Spannable) {
			Spannable spanText = (Spannable) text;
			Selection.setSelection(spanText, text.length());
		}
		CharSequence text2 = usernameET.getText();
		if (text2 instanceof Spannable) {
			Spannable spanText2 = (Spannable) text2;
			Selection.setSelection(spanText2, text2.length());
		}
		btnLogin.setOnClickListener(this);
		uploadStudyTime();

	}

	public void uploadStudyTime() {
		JSONArray array = null;
		SharedPreferences sharedPreferences = getSharedPreferences("upTime",
				Context.MODE_PRIVATE);
		// getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
		final String jsonArrayStart = sharedPreferences.getString("uptimejson",
				"");
		try {
			array = new JSONArray(jsonArrayStart);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (NetworkProber.isNetworkAvailable(getApplicationContext())) {
			if (array != null) {
				OffLineOpenCourseRequest offLineOpenCourseRequest = new OffLineOpenCourseRequest(
						handler);
				offLineOpenCourseRequest.setCourseId(array);
				offLineOpenCourseRequest.sendRequest();
			}
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
			progressDialog.setMessage("正在登入中...");
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
		if (progressDialog != null &&

		progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	private void initHolder() {
		btnLogin = (Button) findViewById(R.id.btnLogin);
		usernameET = (EditText) findViewById(R.id.usernameET);
		pwdET = (EditText) findViewById(R.id.pwdET);
		linearLayoutBack = (LinearLayout) findViewById(R.id.linearLayoutBack);
		initLayoutParams();
	}

	private void initLayoutParams() {
		LinearLayout.LayoutParams editBackParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		editBackParams.setMargins((int) (48 * LoginActivity.widthScale),
				(int) (399 * LoginActivity.heightScale),
				(int) (48 * LoginActivity.widthScale),
				(int) (90 * LoginActivity.heightScale));
		linearLayoutBack.setLayoutParams(editBackParams);

		LinearLayout.LayoutParams editorParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				(int) (56 * LoginActivity.heightScale));
		editorParams.setMargins((int) (50 * LoginActivity.widthScale),
				(int) (170 * LoginActivity.heightScale),
				(int) (50 * LoginActivity.widthScale), 0);
		usernameET.setLayoutParams(editorParams);

		LinearLayout.LayoutParams otherEditorParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				(int) (56 * LoginActivity.heightScale));
		otherEditorParams.setMargins((int) (50 * LoginActivity.widthScale),
				(int) (14 * LoginActivity.heightScale),
				(int) (50 * LoginActivity.widthScale), 0);
		pwdET.setLayoutParams(otherEditorParams);

		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				(int) (80 * LoginActivity.heightScale));
		buttonParams.setMargins((int) (40 * LoginActivity.widthScale),
				(int) (23 * LoginActivity.heightScale),
				(int) (40 * LoginActivity.widthScale), 0);
		btnLogin.setLayoutParams(buttonParams);
	}

	@Override
	public void onClick(View v) {
		if (v == btnLogin) {
			if (NetworkProber.isNetworkAvailable(getApplicationContext())) {
				loginRequest = new LoginRequest(handler);
				loginRequest.setParams(usernameET.getText().toString().trim(),
						pwdET.getText().toString().trim(), LoginActivity.this,
						flagBean.isRequestOk());
				loginRequest.sendRequest();
				playProgressDialog(mContext);
				handlerTimer.postDelayed(runnableTimer, 1000 * 30);
			} else {
				SharedPreferences sharedPreferences = getSharedPreferences(
						"userData", Context.MODE_PRIVATE);
				// getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
				String name = sharedPreferences.getString("name", "");
				if (usernameET.getText().toString().trim().equals(name)) {
					Toast.makeText(getApplicationContext(),
							"没有网络连接，将进入离线模式...", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(LoginActivity.this,
							SecondActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("from", "LoginActivity");
					intent.putExtras(bundle);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
					finish();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							LoginActivity.this);
					builder.setCancelable(false);
					builder.setTitle("提示：");
					builder.setMessage("此帐号与上次登入帐号不统一，请联网重新登陆！");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
					builder.show();
				}
			}
		}
	}

}
