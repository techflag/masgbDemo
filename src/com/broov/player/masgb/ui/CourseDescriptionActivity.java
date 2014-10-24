package com.broov.player.masgb.ui;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.broov.player.R;
import com.broov.player.masgb.net.request.GetCourseDescriRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class CourseDescriptionActivity extends BaseActivity implements
		OnClickListener {
	private TextView descriptionTV;
	private Bundle bundle;
	private ImageView searchback;
	private TextView textView2;
	private String course_id;
	private Map<String, String> descriptionMap = new HashMap<String, String>();
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleMessageState.GET_COURSE_DESCRI_SUCCESS:
				descriptionMap = (Map<String, String>) msg.obj;
				setData(descriptionMap);
				break;
			default:
				break;
			}
		};

	};
	private GetCourseDescriRequest getCourseDescriRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coursedescription);
		bundle = getIntent().getExtras();
		course_id = bundle.getString("course_id");
		initHolder();
		initData();

	}

	protected void setData(Map<String, String> descriptionMap2) {
		descriptionTV.setText(descriptionMap2.get("Description"));
	}

	private void initData() {
		textView2.setOnClickListener(this);
		searchback.setOnClickListener(this);
		getCourseDescriRequest = new GetCourseDescriRequest(handler);
		getCourseDescriRequest.setStuplyid(course_id);
		getCourseDescriRequest.sendRequest();
	}

	private void initHolder() {
		descriptionTV = (TextView) findViewById(R.id.descriptionTV_descrip);
		searchback = (ImageView) findViewById(R.id.searchback_descrip);
		textView2 = (TextView) findViewById(R.id.textView_descrip2);
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

	@Override
	public void onClick(View v) {
		if (v == searchback) {
			finish();
		} else if (v == textView2) {
			finish();
		}
	}
}
