package com.broov.player.masgb.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.broov.player.R;

public class OtherActivity extends BaseActivity  implements OnClickListener{
	private TextView contentTV;
//	private EditText editText;
//	private Button testLoginBT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other);
		initHolder();
		initData();
	}

	private void initData() {
//		testLoginBT.setOnClickListener(this);
		contentTV.setText("\t\t此应用只能在丰云手机上使用，"
				+ "请使用丰云手机下载此应用登入，谢谢！");
		
	}

	private void initHolder() {
		contentTV = (TextView) findViewById(R.id.contentTV);
//		editText = (EditText) findViewById(R.id.editText);
//		testLoginBT = (Button) findViewById(R.id.testLoginBT);
	}

	@Override
	public void onClick(View v) {
//		if(v==testLoginBT){
//			if (editText.getText().toString().endsWith("123456")) {
//				Intent intent = new Intent(getApplicationContext(),
//						LoginActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//				startActivity(intent);
//				finish();
//			} else {
//				Toast.makeText(getApplicationContext(), "验证码错误！", Toast.LENGTH_LONG)
//						.show();
//			}
//		}
	}
}
