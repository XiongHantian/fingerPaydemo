package com.yiwen.activity;


import com.yiwen.fingerpay.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class AuthActivity1 extends Activity {
	// �ؼ�


	// ����
	public static final String TAG = "AuthActivity1";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth1);

		// ��Ļ��ֹ����
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


		// ��ʼ���ؼ�
	
	}



	public void onClick(View v) {
		switch (v.getId()) {
		
		default:
			break;
		}
	}
}
