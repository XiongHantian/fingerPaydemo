package com.yiwen.activity;


import com.yiwen.fingerpay.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class AuthActivity1 extends Activity {
	// 控件


	// 常量
	public static final String TAG = "AuthActivity1";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth1);

		// 屏幕禁止休眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


		// 初始化控件
	
	}



	public void onClick(View v) {
		switch (v.getId()) {
		
		default:
			break;
		}
	}
}
