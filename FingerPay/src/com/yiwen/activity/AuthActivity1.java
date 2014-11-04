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
import android.widget.TextView;

public class AuthActivity1 extends Activity implements OnClickListener{
	// 控件
	private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0,btn00;
	private Button btnadd, btnsub, btnmul, btndiv, btneq, btnpoint, btndel, btnc,btnfin;
	private TextView btnt1;
	
	
	//变量
	private String string = "0";
	private GetValue getValue = new GetValue();
	private Judge judge = new Judge();
	private boolean flag = false;

	// 常量
	public static final String TAG = "AuthActivity1";

	private void init() {
		btn1 = (Button) this.findViewById(R.id.btn_1);
		btn2 = (Button) this.findViewById(R.id.btn_2);
		btn3 = (Button) this.findViewById(R.id.btn_3);
		btn4 = (Button) this.findViewById(R.id.btn_4);
		btn5 = (Button) this.findViewById(R.id.btn_5);
		btn6 = (Button) this.findViewById(R.id.btn_6);
		btn7 = (Button) this.findViewById(R.id.btn_7);
		btn8 = (Button) this.findViewById(R.id.btn_8);
		btn9 = (Button) this.findViewById(R.id.btn_9);
		btn0 = (Button) this.findViewById(R.id.btn_0);
		btn00 = (Button) this.findViewById(R.id.btn_00);
		btnc = (Button) this.findViewById(R.id.btn_c);
		btnadd = (Button) this.findViewById(R.id.btn_add);
		btnsub = (Button) this.findViewById(R.id.btn_subtract);
		btnmul = (Button) this.findViewById(R.id.btn_multiple);
		btndiv = (Button) this.findViewById(R.id.btn_division);
		btneq = (Button) this.findViewById(R.id.btn_eq);
		btnpoint = (Button) this.findViewById(R.id.btn_point);
		btndel = (Button) this.findViewById(R.id.btn_del);
		btnfin = (Button) this.findViewById(R.id.btn_finish);
		btnt1 = (TextView) this.findViewById(R.id.money_text);
		this.btn00.setOnClickListener(this);
		this.btn0.setOnClickListener(this);
		this.btn1.setOnClickListener(this);
		this.btn2.setOnClickListener(this);
		this.btn3.setOnClickListener(this);
		this.btn4.setOnClickListener(this);
		this.btn5.setOnClickListener(this);
		this.btn6.setOnClickListener(this);
		this.btn7.setOnClickListener(this);
		this.btn8.setOnClickListener(this);
		this.btn9.setOnClickListener(this);
		this.btnc.setOnClickListener(this);
		this.btnadd.setOnClickListener(this);
		this.btndel.setOnClickListener(this);
		this.btndiv.setOnClickListener(this);
		this.btneq.setOnClickListener(this);
		this.btnmul.setOnClickListener(this);
		this.btnpoint.setOnClickListener(this);
		this.btnsub.setOnClickListener(this);
		this.btnfin.setOnClickListener(this);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth1);

		// 屏幕禁止休眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


		// 初始化控件
		this.init();
	}


	@Override
	public void onClick(View v) {
		if ("error".equals(btnt1.getText().toString()) || "∞".equals(btnt1.getText().toString())) {
			string = "0";
		}
		
		if (v == this.btn0) {
			string = judge.digit_judge(string, "0", flag);
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btn00) {
			string = judge.digit_judge(string, "00", flag);
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btn1) {
			string = judge.digit_judge(string, "1", flag);
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btn2) {
			string = judge.digit_judge(string, "2", flag);
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btn3) {
			string = judge.digit_judge(string, "3", flag);
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btn4) {
			string = judge.digit_judge(string, "4", flag);
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btn5) {
			string = judge.digit_judge(string, "5", flag);
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btn6) {
			string = judge.digit_judge(string, "6", flag);
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btn7) {
			string = judge.digit_judge(string, "7", flag);
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btn8) {
			string = judge.digit_judge(string, "8", flag);
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btn9) {
			string = judge.digit_judge(string, "9", flag);
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btneq) {
			string = getValue.alg_dispose(string);
			string = judge.digit_dispose(string);
			flag = true;
			btnt1.setText(string);
		} else if (v == this.btnc) {
			string = "";
			string = "0";
			btnt1.setText(string);
			flag = false;
		} else if (v == this.btnpoint) {
			string = judge.judge1(string);
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btndel) {
			if (!"0".equals(string)) {
				string = string.substring(0, string.length() - 1);
				if (0 == string.length())
					string = "0";
			}
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btnadd) {
			string = judge.judge(string, "+");
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btnsub) {
			string = judge.judge(string, "-");
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btnmul) {
			string = judge.judge(string, "×");
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btndiv) {
			string = judge.judge(string, "÷");
			flag = false;
			btnt1.setText(string);
		} else if (v == this.btnfin) {
			Intent intent = new Intent(AuthActivity1.this,
					AuthActivity2.class);
			Bundle bundle = new Bundle();
			bundle.putString("money", btnt1.getText().toString());
			intent.putExtras(bundle);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right,
					R.anim.out_to_left);
		}
		
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
