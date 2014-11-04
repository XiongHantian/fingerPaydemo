package com.yiwen.activity;


import com.yiwen.fingerpay.R;
import com.yiwen.util.GetValue;
import com.yiwen.util.Judge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AuthActivity2 extends Activity{
	// �ؼ�
	TextView mMoneyTv;
	TextView mAccountTv;
	
	
	//����
	String mMoney;

	// ����
	public static final String TAG = "AuthActivity2";

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth2);

		// ��Ļ��ֹ����
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		//��ʼ������
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mMoney = bundle.getString("money");

		// ��ʼ���ؼ�
		mMoneyTv = (TextView) findViewById(R.id.auth_money_tv);
		mMoneyTv.setText(mMoney+" Ԫ");
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sign_titleback_btn:

			this.finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			break;
		default:
			break;
		}
	}
}
