package com.broov.player.masgb.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.entity.PlanCategoryListEntity;
import com.broov.player.masgb.net.request.GetStudentPlanContentRequest;
import com.broov.player.masgb.net.request.GetStudentPlanListRequest;
import com.broov.player.masgb.utils.HandleMessageState;

public class StudyPlanActivity extends BaseActivity implements OnClickListener {
	private Spinner planSpinner;
	private Button searchPlanBtn;
	private List<PlanCategoryListEntity> planCategoryList = new ArrayList<PlanCategoryListEntity>();
	private ArrayAdapter<String> arrayAdapter;
	private List<Object> allPlans;
	private Map<String, String> planMap = new HashMap<String, String>();
	private static Map<String, String> plans = new HashMap<String, String>();
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleMessageState.GET_STUDENT_PLAN_LIST_SUCCESS:
				planCategoryList.clear();
				planCategoryList = (List<PlanCategoryListEntity>) msg.obj;
				for (PlanCategoryListEntity planCategoryListEntity : planCategoryList) {
					allPlans.add(planCategoryListEntity);
					plans.put(planCategoryListEntity.getName(),
							planCategoryListEntity.getId());
				}
				adapterChangeListener();
				break;
			case HandleMessageState.GET_STUDENT_PLAN_CONTENT_SUCCESS:
				planMap = (Map<String, String>) msg.obj;
				setData(planMap);
			default:
				break;
			}
		};

	};
	private GetStudentPlanListRequest getStudentPlanListRequest;
	private GetStudentPlanContentRequest getStudentPlanContentRequest;
	private TextView planDesc;
	private TextView startTimeTv;
	private TextView endTimeTv;
	private TextView requireCredithourTv;
	private TextView credithoursTv;
	private TextView provinceCredithourTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.studyplan);
		initHolder();
		initData();
	}

	protected void setData(Map<String, String> planMap2) {
		if (!(planMap2.get("PlanDesc").equals("null"))) {

			planDesc.setText(planMap2.get("PlanDesc"));
		}
		if (!(planMap2.get("startTime").equals("null"))) {

			startTimeTv.setText(planMap2.get("startTime"));
		}
		if (!(planMap2.get("endTime").equals("null"))) {

			endTimeTv.setText(planMap2.get("endTime"));
		}
		if (!(planMap2.get("RequireCredithour").equals("null"))) {

			requireCredithourTv.setText(planMap2.get("RequireCredithour"));
		}
		if (!(planMap2.get("Credithours").equals("null"))) {

			credithoursTv.setText(planMap2.get("Credithours"));
		}
		if (!(planMap2.get("ProvinceCredithour").equals("null"))) {

			provinceCredithourTv.setText(planMap2.get("ProvinceCredithour"));
		}
	}

	private void initData() {
		allPlans = new ArrayList<Object>();
		getStudentPlanListRequest = new GetStudentPlanListRequest(handler);
		getStudentPlanListRequest.sendRequest();
		searchPlanBtn.setOnClickListener(this);
	}

	private void initHolder() {
		planDesc = (TextView) findViewById(R.id.planDesc);
		searchPlanBtn = (Button) findViewById(R.id.searchPlanBtn);
		planSpinner = (Spinner) findViewById(R.id.planSpinner);
		startTimeTv = (TextView) findViewById(R.id.startTimeTv);
		endTimeTv = (TextView) findViewById(R.id.endTimeTv);
		requireCredithourTv = (TextView) findViewById(R.id.requireCredithourTv);
		credithoursTv = (TextView) findViewById(R.id.credithoursTv);
		provinceCredithourTv = (TextView) findViewById(R.id.provinceCredithourTv);
	}

	protected void adapterChangeListener() {
		List<String> listPlan = new ArrayList<String>();
		for (int i = 0; i < allPlans.size(); i++) {
			listPlan.add(((PlanCategoryListEntity) allPlans.get(i)).getName());
		}
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listPlan);
		planSpinner.setAdapter(arrayAdapter);
	}

	/**
	 * 监听返回按键事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(StudyPlanActivity.this)
					.setTitle("注意!")
					.setMessage("确定退出应用？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									android.os.Process
											.killProcess(android.os.Process
													.myPid()); // 获取PID
									System.exit(0); // 常规java、c#的标准退出法，返回值为0代表正常退出
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

	@Override
	public void onClick(View v) {
		if (v == searchPlanBtn) {
			if (!(planSpinner.getSelectedItem() == null)) {
				String stuplyid = plans.get(planSpinner.getSelectedItem()
						.toString());
				getStudentPlanContentRequest = new GetStudentPlanContentRequest(
						handler);
				getStudentPlanContentRequest.setStuplyid(stuplyid);
				getStudentPlanContentRequest.sendRequest();
			} else {
				Toast.makeText(getApplicationContext(), "网络状况不好，请重试！",
						Toast.LENGTH_LONG).show();
//				if ((planSpinner.getSelectedItem() == null)) {
//					getStudentPlanListRequest = new GetStudentPlanListRequest(
//							handler);
//					getStudentPlanListRequest.sendRequest();
//				}

			}
		}
	}

}
