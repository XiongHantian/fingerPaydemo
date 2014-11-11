package com.yiwen.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.yiwen.fingerpay.R;
import com.yiwen.util.PhoneNumChecker;

public class SignActivity3 extends Activity {
	// 控件
	EditText IdEditText;
	Button IdclearButton;

	// 常量
	public static final String TAG = "SignActivity1";

	// 变量
	public String mCode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign3);

		// 屏幕禁止休眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		// 初始化变量
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mCode = bundle.getString("code");

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
				if (IdEditText.getText().length() == 0) {
					showdialog(0);
					return false;
				} else if (!IdEditText.getText().toString().equals(mCode)) {
					showdialog(1);
					return false;
				}
				Intent intent = new Intent(SignActivity3.this,
						IndexActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				return false;
			}
		});

	}

	@Override
	protected void onStart() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 500);
		super.onStart();
	};

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

	public void showdialog(int f) {
		if (f == 0)
			new AlertDialog.Builder(this)
					.setTitle("提醒")
					.setMessage("请填写验证码")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
											.toggleSoftInput(
													0,
													InputMethodManager.HIDE_NOT_ALWAYS);
								}
							}).show();
		else
			new AlertDialog.Builder(this)
					.setTitle("提醒")
					.setMessage("验证码有误，请重新填写")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
											.toggleSoftInput(
													0,
													InputMethodManager.HIDE_NOT_ALWAYS);
								}
							}).show();

	}

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
