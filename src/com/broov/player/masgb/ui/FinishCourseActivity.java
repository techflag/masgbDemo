package com.broov.player.masgb.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.broov.player.R;
import com.broov.player.masgb.entity.FinishEntity;
import com.broov.player.masgb.entity.GetBaseType;
import com.broov.player.masgb.net.request.FinishCourseRequest;
import com.broov.player.masgb.net.request.FinishCourseRequest2;
import com.broov.player.masgb.utils.HandleMessageState;

public class FinishCourseActivity extends BaseActivity {
	@SuppressWarnings("unused")
	private Context mContext;
	private List<FinishEntity> finishList = new ArrayList<FinishEntity>();
	private Map<String, String> finishMap = new HashMap<String, String>();
	private Map<String, String> finishMap2 = new HashMap<String, String>();
	private ListView questionLV;
	private MyAdapter myAdapter;
	private View headView;
	@SuppressWarnings("unused")
	private String judgment;
	// private View footView;
	private Map<String, String> checkMap = new HashMap<String, String>();
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleMessageState.GET_FINISH_COURSE_LIST_SUCCESS:
				finishList = (List<FinishEntity>) msg.obj;
				adapterChangeListener();
				break;
			case HandleMessageState.GET_FINISH_DA_LIST_SUCCESS:
				finishMap = (Map<String, String>) msg.obj;
				break;
			case HandleMessageState.GET_FINISH_DATA_SUCCESS:
				finishMap2 = (Map<String, String>) msg.obj;
				break;
			case HandleMessageState.FINISH_SUCCESS:
				Toast.makeText(getApplicationContext(), "完成课程成功！",
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};

	};
	private FinishCourseRequest finishCourseRequest;
	private FinishCourseRequest2 finishCourseRequest2;
	private Button finishBtn;
	private int score = 0;
	private long startMili;
	private long endMili;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finish_course_layout);
		setSubPageTitle("在线调查", true);
		mContext = FinishCourseActivity.this;
		initHolder();
		initData();
	}

	private void initData() {
		questionLV.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				return;
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				return;
			}
		});
		startMili = System.currentTimeMillis();
		finishBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String get = "";
				String data = "";
				endMili = System.currentTimeMillis();
				for (int i = 0; i < finishList.size() - 1; i++) {
					get = get + checkMap.get(i + "");
				}
				if (get.contains("null")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							FinishCourseActivity.this);
					builder.setCancelable(false);
					builder.setTitle("提示：");
					builder.setMessage("请完成所有的选择题目，再提交！");
					builder.setPositiveButton("确定，继续答题",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
					builder.show();
				} else {
					for (int i = 0; i < finishList.size() - 1; i++) {
						data = data + checkMap.get(i + "") + 4 + " ";
					}
					data = "<ROOTSTUB global=" + "\"" + "true" + "\"" + "  "
							+ data + "/>";
					finishCourseRequest2 = new FinishCourseRequest2(handler);
					finishCourseRequest2.setData(finishMap2.get("examStartId"),
							data, String.valueOf(7),
							finishMap2.get("clerk_Wdpf"),
							String.valueOf((endMili - startMili) / 1000),
							finishMap2.get("moveOutTimes"));
					finishCourseRequest2.sendRequest();
				}
			}
		});
		myAdapter = new MyAdapter(this);
		finishCourseRequest = new FinishCourseRequest(handler);
		finishCourseRequest.sendRequest();
	}

	// protected void doCheck() {
	// checkDa();
	// }

	private void initHolder() {
		finishBtn = (Button) findViewById(R.id.finishBtn);
		questionLV = (ListView) findViewById(R.id.questionLV);
		headView = LayoutInflater.from(this).inflate(
				R.layout.finish_course_item_head, null);
		questionLV.addHeaderView(headView);
	}

	protected void adapterChangeListener() {
		myAdapter.setData(finishList);
		myAdapter.notifyDataSetChanged();
		questionLV.setAdapter(myAdapter);
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

	public int checkDa(int position) {
		for (int i = 0; i < finishMap.size(); i++) {
			if (finishMap.get(position).equals(checkMap.get(position))) {
				score = score + 1;
			}
		}
		return 4;
	}

	private class MyAdapter extends BaseAdapter {
		@SuppressLint("UseSparseArrays")
		HashMap<Integer, View> lmap = new HashMap<Integer, View>();
		private Context mContext;
		private LayoutInflater inflater;
		private List<FinishEntity> list = new ArrayList<FinishEntity>();
		@SuppressWarnings("unused")
		private Map<String, String> finishMap = new HashMap<String, String>();

		public MyAdapter(Context context) {
			this.mContext = context;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("unused")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (lmap.get(position) == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.finish_course_item,
						null);
				holder.finishLinearLayout = (LinearLayout) convertView
						.findViewById(R.id.finishLinearLayout);
				holder.questionTv = (TextView) convertView
						.findViewById(R.id.questionTv);
				holder.radioGroup = (RadioGroup) convertView
						.findViewById(R.id.RadioGroup01);
				holder.editText = (EditText) convertView
						.findViewById(R.id.feedbackTV);
				AbsListView.LayoutParams layoutCourseParams = new AbsListView.LayoutParams(
						LayoutParams.FILL_PARENT,
						(int) (480 * BaseActivity.heightScale));
				holder.finishLinearLayout.setLayoutParams(layoutCourseParams);
				lmap.put(position, convertView);
				convertView.setTag(holder);
			} else {
				convertView = lmap.get(position);
				holder = (ViewHolder) convertView.getTag();
			}
			holder.questionTv.setText(list.get(position).getTm_Nr());
			if (GetBaseType.getBaseType(list.get(position).getTm_Tx_Id())
					.equals("1")) {
				holder.editText.setVisibility(View.GONE);
				holder.radioGroup.setVisibility(View.VISIBLE);
				if (holder.radioGroup.getChildCount() == 0) {
					for (int i = 0; i < Integer.parseInt(list.get(position)
							.getTm_dasm()); i++) {
						int j = i;
						CheckBox newRadio = new CheckBox(
								FinishCourseActivity.this);
						// RadioButton newRadio = new RadioButton(
						// FinishCourseActivity.this);
						newRadio.setId(i);
						newRadio.setHeight((int) (60 * BaseActivity.heightScale));
						if (i == 0) {
							newRadio.setText((i + 1) + "(非常不好)");
						} else if (i > 0
								&& i < Integer.parseInt(list.get(position)
										.getTm_dasm()) - 1) {
							newRadio.setText((i + 1) + "");
						} else {
							newRadio.setText((i + 1) + "(非常好)");
						}
						holder.radioGroup.addView(newRadio);
						// holder.radioGroup
						// .setOnCheckedChangeListener(new
						// MyOnCheckedChangListener(
						// position, holder));
					}
				
					   
				}

			} else if (GetBaseType
					.getBaseType(list.get(position).getTm_Tx_Id()).equals("4")) {
				holder.editText.setVisibility(View.VISIBLE);
				holder.radioGroup.setVisibility(View.GONE);
				holder.editText.addTextChangedListener(new MyTextWatcher(
						position, holder));
			}

			return convertView;
		}

		class ViewHolder {
			TextView questionTv;
			RadioGroup radioGroup;
			EditText editText;
			LinearLayout finishLinearLayout;
		}

		public void setData(List<FinishEntity> list) {
			this.list = list;
		}

		
		private class MyTextWatcher implements TextWatcher {
			private ViewHolder holder;

			public MyTextWatcher(int position, ViewHolder holder) {
				this.holder = holder;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				judgment = holder.editText.getText().toString();
			}

		}

		@SuppressWarnings("unused")
		private class MyOnCheckedChangListener implements
				RadioGroup.OnCheckedChangeListener {
			private int position;

			public MyOnCheckedChangListener(int position, ViewHolder holder) {
				this.position = position;
			}

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton radioButton = (RadioButton) findViewById(checkedId);
				String data = "prefix_" + list.get(position).getId() + "="
						+ "\""
						+ String.valueOf(radioButton.getText().toString())
						+ "\"" + "  " + "mark_" + list.get(position).getId()
						+ "=";
				checkMap.put(position + "", data);
			}
		}

	}

}
