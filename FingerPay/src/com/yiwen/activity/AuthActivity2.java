package com.yiwen.activity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hisign.AS60xSDK.AS60xIO;
import com.hisign.AS60xSDK.SDKUtilty;
import com.ivsign.fingerdatas.FingerdatasAPI;
import com.ivsign.fingertest.MainActivity;
import com.ivsign.fingertest.THIDServiceAPI;
import com.yiwen.fingerpay.R;
import com.yiwen.network.HttpUtil;
import com.yiwen.util.GetValue;
import com.yiwen.util.Judge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AuthActivity2 extends Activity {
	// �ؼ�
	TextView mMoneyTv;
	TextView mAccountTv;
	ImageView mQrLineView1;
	ImageView mQrLineView2;
	ImageView mLfingerView;
	ImageView mRfingerView;
	LinearLayout mCropLayout1;
	LinearLayout mCropLayout2;

	// ����
	String mMoney;
	FingerMatchTask matchTask_5;
	FingerMatchTask matchTask_6;

	// ����
	public static final String TAG = "AuthActivity2";

	/*------------˫ָ�ɼ�ʾ��--------------*/
	private boolean isStop = false;
	private boolean isContinue = true;
	private boolean isAutoUp = true;
	private static int FingerCnt;
	private static int FingerCntOld;
	private int mDoubleFingers = 2;
	private boolean isMatchIdle = true;

	boolean capFinger1 = false;
	boolean capFinger2 = false;
	private int mFingerPos = 0; // ָλ��Ŀǰֻ�� ��һö���ڶ�ö
	private int mSensorType = 1; // ���������ͣ�0:���� 1:��ѧ190 2:����TCS1S 3��AS602
	private boolean SensorInited = false;// ��ǲɼ��豸�Ƿ��ʼ��
	private boolean isCryptoed = false; // ����Ƿ����
	private int UseSTDOUT = 0;
	private UsbManager mUsbManager;
	private UsbDeviceConnection mConnection;
	private UsbDevice deviceInit190 = null;
	private UsbDevice deviceTSC1S = null;
	private boolean usbJavaControl = false;
	private UsbEndpoint mEndpointIn = null, mEndpointOut = null;
	private int Sdkver = android.os.Build.VERSION.SDK_INT;
	ByteBuffer Imgbuffer = ByteBuffer.allocate(655360);
	byte[] recvbuffer = new byte[65536];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth2);

		// ��Ļ��ֹ����
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// ��ʼ������
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mMoney = bundle.getString("money");

		// ��ʼ���ؼ�
		mMoneyTv = (TextView) findViewById(R.id.auth_money_tv);
		mMoneyTv.setText(mMoney + " Ԫ");
		mLfingerView = (ImageView) findViewById(R.id.sign_lfinger_iv);
		mRfingerView = (ImageView) findViewById(R.id.sign_rfinger_iv);
		mCropLayout1 = (LinearLayout) findViewById(R.id.capture_crop_layout1);
		mCropLayout2 = (LinearLayout) findViewById(R.id.capture_crop_layout2);

		// ��ʼ������
		mQrLineView1 = (ImageView) findViewById(R.id.capture_scan_line1);
		mQrLineView2 = (ImageView) findViewById(R.id.capture_scan_line2);
		ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
		animation.setRepeatCount(-1);
		animation.setRepeatMode(Animation.RESTART);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(1200);
		mQrLineView1.startAnimation(animation);
		mQrLineView2.startAnimation(animation);

		MainActivity.jniFingerTCSCapContinue(1);

		isAutoUp = true;

		// ע��㲥���մ������ڽ��� IntentService �Ľ��
		RegistMessage();

		// BroadcastReceiver when remove the device USB plug from a USB port
		BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
					print("mUsbReceiver==ACTION_USB_DEVICE_DETACHED");
					SensorInited = false;
					printHint("ע�⣺USB�ɼ��豸�Ѱγ���");
				}
			}
		};
		// listen for new devices
		try {
			IntentFilter filter = new IntentFilter();
			filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
			registerReceiver(mUsbReceiver, filter);

		} catch (Exception e) {
			// TODO: handle exception
		}

		// ��ʼ���豸
		initDevice();

		// ��ʼ�ɼ�ָ��
		startGetFinger();
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

	// ������׽ָ�ƵĽ���
	public void stopFingerCap() {
		if (matchTask_5 != null
				&& matchTask_5.getStatus() == AsyncTask.Status.RUNNING)
			matchTask_5.onCancelled(); // ���Task�������У�����ȡ����
		if (matchTask_6 != null
				&& matchTask_6.getStatus() == AsyncTask.Status.RUNNING)
			matchTask_6.onCancelled(); // ���Task�������У�����ȡ����
	}

	// ��ʼ�ɼ�ָ��
	public void startGetFinger() {
		isStop = false;
		MainActivity.jniFingerTCSCapStop(0);

		mDoubleFingers = 2;
		capFinger1 = true;
		capFinger2 = true;

		Log.i(TAG, "startGetFinger");
		matchTask_5 = new FingerMatchTask();
		matchTask_5.execute(3);

		matchTask_6 = new FingerMatchTask();
		matchTask_6.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
	}

	private void sendPayRequest() {
		if (isNetworkAvailable()) {
			HttpTask task = new HttpTask();
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else
			setNetwork();
	}

	// ��������
	public void setNetwork() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("����״̬");
		builder.setMessage("��ǰ���粻���ã��Ƿ���������?");
		builder.setPositiveButton("����", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent;
				if (android.os.Build.VERSION.SDK_INT > 10) {
					intent = new Intent(
							android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				} else {
					intent = new Intent();
					ComponentName component = new ComponentName(
							"com.android.settings",
							"com.android.settings.WirelessSettings");
					intent.setComponent(component);
					intent.setAction("android.intent.action.VIEW");
				}
				startActivity(intent);
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create();
		builder.show();
	}

	// �ж�����״̬
	public boolean isNetworkAvailable() {
		Context context = getApplicationContext();
		ConnectivityManager connect = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connect == null) {
			return false;
		} else// get all network info
		{
			NetworkInfo[] info = connect.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private class HttpTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			Log.i(TAG, "onPreExecute() called");
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i(TAG, "doInBackground(Params... params) called");
			String result = HttpUtil.uploadFinger(HttpUtil.AUTH_URL);
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... progresses) {
			Log.i(TAG, "onProgressUpdate(Progress... progresses) called");
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i(TAG, "onPostExecute(Result result) called");
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "UnregisterReceiver");
		stopFingerCap();
		this.unregisterReceiver(receiver3);
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	// //////ע��һ���㲥�¼�������/////////////////////
	public void RegistMessage() {
		// ָ�Ʒ���
		IntentFilter filter3 = new IntentFilter(
				THIDServiceAPI.ACTION_RECV_MSG_FINGER);
		filter3.addCategory(Intent.CATEGORY_DEFAULT);

		receiver3 = new MessageReceiver1();

		registerReceiver(receiver3, filter3);// ע��һ���㲥�¼�������

		Log.d(TAG, "RegistMessage");
	}

	// �㲥���� ������ָ�� ���񷵻ص���Ϣ
	private MessageReceiver1 receiver3;

	public class MessageReceiver1 extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {// ���ܵ����ص���Ϣ

			isMatchIdle = true;
			String message = intent.getStringExtra(THIDServiceAPI.MESSAGE_OUT);
			String imgPath = "";
			String info = "";
			int score = 0;
			try {

				JSONArray jsonArray = new JSONObject(message)
						.getJSONArray("candidateList");
				if (jsonArray.length() == 0) {
					// printHint("���޷�����Ϣ��");
					// imgView3.setVisibility(View.INVISIBLE);
					// textView1.setText("���޷�����Ϣ��");
				} else {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
						imgPath = jsonObject.getString("ImgPath");
						print("fingerImgPath=>" + imgPath);
						if (imgPath == "") {
							// imgView3.setVisibility(View.INVISIBLE);
						} else {
							// imgView3.setVisibility(View.VISIBLE);
							Bitmap facebmp = BitmapFactory.decodeFile(imgPath);
							// facebmp =
							// FingerdatasAPI.scaleImg(facebmp,180,240);
							facebmp = FingerdatasAPI.scaleImg(facebmp, 90, 120);
							// imgView3.setImageBitmap(facebmp);
							// imgView3.invalidate();
						}

						info = jsonObject.getString("Info");
						score = Integer.parseInt(jsonObject.getString("Score"));
						score = score > 1000 ? 1000 : score;

						if (info == null) {
							// textView1.setText("���޷�����Ϣ��");
						} else {
							// textView1.setText("������Ϣ��" + info + "\n" + "�ȶԷ�ֵ��"
							// + score);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// print("message=="+message);
		}
	}

	/**** ��̬��ʾͼƬ ****/
	private Bitmap facebmp1, facebmp2;

	private class FingerMatchTask extends AsyncTask<Integer, Integer, Integer> {

		private boolean isCancelled = false;
		private boolean isLCap = false;
		private boolean isRCap = false;

		@Override
		protected void onProgressUpdate(Integer... progress) {

			if (isCancelled)
				return;

			Log.i(TAG, "onProgress:" + progress[0]);
			switch (progress[0]) {

			case 0:
				// printHint("USB�豸��Ȩ�ޣ������²��USB�豸��");
				break;

			case 1:
				mLfingerView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				// mLfingerView.setImageBitmap(facebmp1);
				mLfingerView.setImageResource(R.drawable.finger_ok_bg);
				mQrLineView1.clearAnimation();
				mQrLineView1.setVisibility(View.GONE);
				mCropLayout1.setVisibility(View.GONE);
				mLfingerView.invalidate();
				isLCap = true;
				if (isLCap && isRCap)
				{
					isCancelled = true;
					sendPayRequest();
				}
				break;

			case 2:
				mRfingerView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				// mRfingerView.setImageBitmap(facebmp2);
				mRfingerView.setImageResource(R.drawable.finger_ok_bg);
				mQrLineView2.clearAnimation();
				mQrLineView2.setVisibility(View.GONE);
				mCropLayout2.setVisibility(View.GONE);
				mRfingerView.invalidate();
				isRCap = true;
				if (isLCap && isRCap)
				{
					isCancelled = true;
					sendPayRequest();
				}
				break;

			case 3:
				break;
			case 4:
				printHint("ָ���ʧ�ܣ�");
				break;
			case 5:
				break;

			default:
				break;
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Integer result) {
			Log.i(TAG, "FingerMatchTask Canceled!");
		}

		@Override
		protected void onCancelled() {
			Log.i(TAG, "onCanceled");
			isCancelled = true;
			super.onCancelled();
		}

		/* The Task body */
		@Override
		protected Integer doInBackground(Integer... params) {
			if (isCancelled)
				return null;

			int nType = params[0];
			int ret = 0;
			if (nType == 1 || nType == 2) {
				String Filename = "/sdcard/DCIM/fingerCapAS602_" + nType
						+ ".bmp";
				String cmdString = "libfx2_finger190.so 2109 7638 tcs_finger "
						+ nType + " 73728 4096 " + Filename;
				FingerCntOld = MainActivity.jniFingerTCSCapStatus();
				ret = MainActivity.jniFinger190CapProcess(cmdString);
			} else if (nType == 3)// ˫ָ�ɼ�
			{
				if (isCancelled)
					return null;
				String cmdString = "libfx2_finger190.so 2109 7638 tcs_finger "
						+ mDoubleFingers + " 73728 4096 ";

				FingerCntOld = MainActivity.jniFingerTCSCapStatus();
				ret = MainActivity.jniFingerTCSCapFingers(cmdString);
			} else if (nType == 0) {
				int delCount = 0;// ��ʱһ��ʱ���ɾ����ָ��
				while (!isStop) {
					if (isCancelled)
						return null;
					for (int i = 0; i < 10; i++)// 800ms
					{
						// ��ȡָ�Ʋɼ���״̬���Ƿ����
						FingerCnt = MainActivity.jniFingerTCSCapStatus();

						if (FingerCnt > FingerCntOld)// ����ָ���������
						{

							delCount = 0;
							FingerCntOld = FingerCnt;
							if (capFinger1) {
								FINGEREROLL1[2] = "/sdcard/DCIM/fingerCapAS602_1.bmp";

								facebmp1 = BitmapFactory
										.decodeFile(FINGEREROLL1[2]);
								facebmp1 = FingerdatasAPI.scaleImg(facebmp1,
										320, 440);

								if (facebmp1 != null) {
									capFinger1 = true;
									publishProgress(1);
								}

							}

							if (capFinger2) {

								FINGEREROLL2[2] = "/sdcard/DCIM/fingerCapAS602_2.bmp";

								facebmp2 = BitmapFactory
										.decodeFile(FINGEREROLL2[2]);
								facebmp2 = FingerdatasAPI.scaleImg(facebmp2,
										320, 440);

								if (facebmp2 != null) {
									capFinger2 = true;
									publishProgress(2);
								}
							}

							break;
						} else {

							delCount++;
							if (delCount > 10) {
								delCount = 0;
							}

						}
					}
				}

				capFinger1 = false;
				capFinger2 = false;
				FingerCntOld = 0;
				FingerCnt = 0;
			}
			return nType;
		}
	};

	// �ɼ�ָ��ͼ��������
	private void capFinger() {
		if (mFingerPos > 0) {

			String Filename = "/sdcard/DCIM/fingerCap190_" + mFingerPos
					+ ".bmp";

			if (1 == mSensorType)//
			{
				Filename = "/sdcard/DCIM/fingerCap190_" + mFingerPos + ".bmp";
				if (!usbJavaControl) {
					{
						String cmdString = "libfx2_finger190.so 8018 1190 bulk_finger 6 655360 16384 "
								+ Filename;
						MainActivity.jniFinger190CapProcess(cmdString);
					}
				}
			}

			if (mFingerPos == 1) {
				Bitmap facebmp = BitmapFactory.decodeFile(Filename);
				facebmp = FingerdatasAPI.scaleImg(facebmp, 320, 440);
				mLfingerView.setImageBitmap(facebmp);
				capFinger1 = true;
				FINGEREROLL1[2] = Filename;
			} else if (mFingerPos == 2) {
				Bitmap facebmp = BitmapFactory.decodeFile(Filename);
				facebmp = FingerdatasAPI.scaleImg(facebmp, 320, 440);
				mRfingerView.setImageBitmap(facebmp);
				capFinger2 = true;
				FINGEREROLL2[2] = Filename;
			}

		}
	}

	// Call shell command
	public static final String[] FINGEREVN = { "LD_LIBRARY_PATH=/vendor/lib:/system/lib:/data/data/com.ivsign.fingertest/lib" };
	public static final String[] FINGEREROLL1 = {
			"/data/data/com.ivsign.fingertest/lib/libFingerV3Test.so", "1",
			"/sdcard/DCIM/fingerCap190_1.bmp", // fingerCap_1.bmp",
			"/sdcard/DCIM/fingerCap_1.ftn" };

	public static final String[] FINGEREROLL2 = {
			"/data/data/com.ivsign.fingertest/lib/libFingerV3Test.so", "1",
			"/sdcard/DCIM/fingerCap190_2.bmp", // fingerCap_2.bmp",
			"/sdcard/DCIM/fingerCap_2.ftn" };

	public static final File execpath = new File(
			"/data/data/com.ivsign.fingertest/lib");

	public synchronized String runCmd(String[] cmd) {
		String resStd = "";
		String resErr = "";
		String line = "";
		InputStream is = null;
		InputStream es = null;
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec(cmd, FINGEREVN, execpath);

			is = proc.getInputStream();
			es = proc.getErrorStream();
			// ����BufferedReader
			BufferedReader buf = new BufferedReader(new InputStreamReader(is));
			do {
				line = buf.readLine();
				if (line == null)
					break;
				resStd += line;
				Log.i(TAG, "runCmd Std:" + line);
			} while (true);
			buf.close();

			buf = new BufferedReader(new InputStreamReader(es));
			do {
				line = buf.readLine();
				if (line == null)
					break;
				resErr += line;
				Log.i(TAG, "runCmd Err:" + line);
			} while (true);
			buf.close();

			is.close();
			es.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i(TAG, "runCmd End.");

		if (UseSTDOUT == 1) {
			UseSTDOUT = 0;
			return resStd;
		} else
			return resErr;
	}

	// ִ�п���̨�������Ϊ�������ַ�����ʽ
	public String runCmd(String command) {
		String cmdStrings[] = command.split(" ", 20);
		return runCmd(cmdStrings);
	}

	public static boolean RootCommandOnce(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("cd " + execpath + "\n");
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		Log.d("*** DEBUG ***", "Root SUC ");
		return true;
	}

	static Process mSU_Process = null;
	static DataOutputStream mSU_os = null;
	static DataInputStream mSU_is = null;

	public static boolean RootCommand(String command) {
		boolean result = true;
		boolean isClose = false;
		try {
			if (null == mSU_Process) {
				mSU_Process = Runtime.getRuntime().exec("su");
				mSU_is = new DataInputStream(mSU_Process.getInputStream());
				mSU_os = new DataOutputStream(mSU_Process.getOutputStream());
				mSU_os.writeBytes("cd " + execpath + "\n");
				Log.d(TAG, "Root Start... ");
			}

			if (null != command) {
				mSU_os.writeBytes(command + " >/dev/null\n");
				mSU_os.writeBytes("echo RootCommand Finished\n");
				Log.d(TAG, "RootCmd SUC:" + mSU_is.readLine());
			} else {
				mSU_os.writeBytes("exit\n");
				isClose = true;
			}

			mSU_os.flush();
			// mSU_Process.waitFor();
		} catch (Exception e) {
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			command = null;
			result = false;
			isClose = true;
		}

		if (isClose) {
			try {
				if (mSU_os != null) {
					mSU_os.close();
				}
				mSU_Process.destroy();
			} catch (Exception e2) {
			}
		}

		return result;
	}

	int MAXIMGSIZE = 655360;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		System.out.println("pwvgpio: ready to disable the usb port!!");
		// this.gpctrl.SetValue(32, 0);
		// this.gpctrl.GetValue(32);
		finish();
		System.exit(0);
	}

	public boolean initDevice() {

		Log.i(TAG, "Product Model: " + android.os.Build.MODEL + " SDKver: "
				+ Sdkver); // �ֻ��ͺ�+�汾�� (-Android 4.1=16.)
		if (Sdkver <= 12 // <Android 3.1
				|| android.os.Build.MODEL.equals("aigopad") // for aigo M609
															// Only
				|| android.os.Build.MODEL.equals("aigoPad") // for aigo M60 Only
				|| android.os.Build.MODEL.equals("Lenovo A3000-H")
				|| android.os.Build.MODEL.equals("HIKe 706")
				|| android.os.Build.MODEL.equals("W17PRO(Dualcore)")
				|| android.os.Build.MODEL.equals("PC-003")) {

			// this.gpctrl.SetValue(32, 1);
			// this.gpctrl.GetValue(32);

			String usbRoot = "chmod 666 /dev/bus/usb/001/*";
			RootCommand(usbRoot);
			String usbRoot1 = "chmod 666 /dev/bus/usb/002/*";
			RootCommand(usbRoot1);
			/*
			 * String setTimeCmd = "/system/bin/date -s 20140401.000000";
			 * RootCommand(setTimeCmd);
			 */

			// runCmd(usbRoot);
			mSensorType = 3;
			SensorInited = true;
		}
		return SensorInited;
	}

	// ���������Ϣ
	public void print(String info) {
		Log.d(TAG, info);
		Log.e("HIKE", info); // print for HIKE
	}

	// ��Ļ��ʾ��Ϣ
	public void printHint(String info) {
		Toast toast = Toast.makeText(getApplicationContext(), info,
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
		// Toast.makeText(MainActivity.this, info,
		// Toast.LENGTH_SHORT).show();//������ʽ
	}

	// ���ԶԻ���
	/*
	 * String resStr = ""; EditText resultET = new EditText(MainActivity.this);
	 * resultET.setText(message); new AlertDialog.Builder(MainActivity.this)
	 * .setTitle("���Խ��") .setIcon(android.R.drawable.ic_dialog_info)
	 * .setView(resultET)//����ȶԽ�� .setNegativeButton("ȷ��", null) .show();
	 */

}
