package com.yiwen.activity;

import com.yiwen.fingerpay.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class IndexActivity extends Activity {

	// ³£Á¿
	public static final String TAG = "IndexActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);

	}

	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.sign_imageButton:
			intent = new Intent(IndexActivity.this, SignActivity.class);
			startActivity(intent);
			break;
		case R.id.auth_imageButton:
			break;
		default:
			break;
		}
	}
}
