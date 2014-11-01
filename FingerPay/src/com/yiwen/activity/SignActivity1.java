package com.yiwen.activity;

import com.yiwen.fingerpay.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SignActivity1 extends Activity {
	// 控件
	EditText IdEditText;
	Button IdclearButton;

	// 常量
	public static final String TAG = "SignActivity1";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign1);

		// 屏幕禁止休眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// 初始化控件
		IdEditText = (EditText) findViewById(R.id.sign_idtext_et);
		IdclearButton = (Button) findViewById(R.id.sign_iddelete_btn);
		IdEditText.addTextChangedListener(mTextWatcher);
		IdEditText.setFocusable(true);
		IdEditText.setFocusableInTouchMode(true);
		IdEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				Intent intent = new Intent(SignActivity1.this, SignActivity2.class);
				Bundle bundle = new Bundle();
				bundle.putString("phonenum", IdEditText.getText().toString());
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				return false;
			}
		});
	}

	TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			if (IdEditText.getText().toString() != null
					&& !IdEditText.getText().toString().equals("")) {
				IdclearButton.setVisibility(View.VISIBLE);
			} else {
				IdclearButton.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
		}
	};

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sign_titleback_btn:
			this.finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			break;
		case R.id.sign_iddelete_btn:
			IdEditText.setText("");
			break;
		default:
			break;
		}
	}
}
