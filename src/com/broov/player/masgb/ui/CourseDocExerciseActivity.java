package com.broov.player.masgb.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;

import com.broov.player.R;
import com.broov.player.masgb.net.request.GetCourseDocExerciseRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class CourseDocExerciseActivity extends BaseActivity{
	private TextView textTV;
	private GetCourseDocExerciseRequest getCourseDocExerciseRequest;
	private Bundle bundle;
	private String content;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleMessageState.GET_COURSE_DOC_EXERCISE_SUCCESS:
				content = (String) msg.obj;
				notifyData();
				break;
			default:
				break;
			}
		}

		private void notifyData() {
			textTV.setText(content);
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coursedocexercise);
		setSubPageTitle("课件课后练习", true);
		initHolder();
		initData();
	}

	private void initData() {
		bundle = getIntent().getExtras();
		getCourseDocExerciseRequest = new GetCourseDocExerciseRequest(handler);
		getCourseDocExerciseRequest.setWareid(bundle.getString("wareid"));
		getCourseDocExerciseRequest.sendRequest();
	}

	private void initHolder() {
		textTV = (TextView) findViewById(R.id.testTV);
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


}
