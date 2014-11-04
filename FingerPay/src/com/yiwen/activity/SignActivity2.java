package com.yiwen.activity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hisign.AS60xSDK.AS60xIO;
import com.hisign.AS60xSDK.SDKUtilty;
import com.ivsign.fingerdatas.FingerdatasAPI;
import com.ivsign.fingertest.MainActivity;
import com.ivsign.fingertest.THIDServiceAPI;
import com.yiwen.fingerpay.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class SignActivity2 extends Activity implements OnClickListener {

	// �ؼ�
	ImageView mLfingerView;
	ImageView mRfingerView;
	TextView mLfingerText;
	TextView mRfingerText;

	// ����
	public static final String TAG = "SignActivity2";

	// ����
	public String mPhonenum;

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
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ��������
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// ȥ����Ϣ��
		setContentView(R.layout.activity_sign2);

		// ��ʼ������
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mPhonenum = bundle.getString("phonenum");

		// ��ʼ���ؼ�
		mLfingerView = (ImageView) findViewById(R.id.sign_lfinger_iv);
		mRfingerView = (ImageView) findViewById(R.id.sign_rfinger_iv);
		mLfingerText = (TextView) findViewById(R.id.sign_lfinger_tv);
		mRfingerText = (TextView) findViewById(R.id.sign_rfinger_tv);

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

	// ��ʼ�ɼ�ָ��
	public void startGetFinger() {
		isStop = false;
		MainActivity.jniFingerTCSCapStop(0);

		mDoubleFingers = 2;
		capFinger1 = true;
		capFinger2 = true;
		setButtonStatus(false);

		final FingerMatchTask matchTask_5 = new FingerMatchTask();
		matchTask_5.execute(3);

		final FingerMatchTask matchTask_6 = new FingerMatchTask();
		matchTask_6.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
		// matchTask_6.execute(0);
	}

	@Override
	protected void onDestroy() {

		Log.d(TAG, "UnregisterReceiver");
		this.unregisterReceiver(receiver3);

		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
		 * Intent intent = getIntent(); // Log.d(TAG, "intent: " + intent);
		 * String action = intent.getAction();// ����USB����
		 * 
		 * // action�ַ�==android.hardware.usb.action.USB_DEVICE_ATTACHED if
		 * (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
		 * if (!SensorInited) { initDevice(); } }
		 */

	}

	public void setButtonStatus(Boolean Value) {
		// findViewById(R.id.button1).setEnabled(Value);
		// findViewById(R.id.button2).setEnabled(Value);
		// findViewById(R.id.button6).setEnabled(Value);
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

	/**
	 * ����ָ��ʶ�����
	 */
	public int CallFingerIDService(String taskID, String inPath, String jsonArg) {
		Intent msgIntent = THIDServiceAPI.GenTHIDServiceIntent(
				THIDServiceAPI.ALGTYPE_FINGERID, taskID, inPath, jsonArg);
		startService(msgIntent);// ��������
		return 0;
	}

	/**
	 * ��дָ�ƱȶԲ���
	 * 
	 * @return ���json��ʽ�ַ���
	 */
	private String SetTHIDFingerIDArg() {
		String retjson = "";

		try {
			JSONObject jsonArg = new JSONObject();
			jsonArg.put("nCmd", 0); // nCmd = 0���ȶ�/������أ�1�����ؿ⣻2�����ȶԣ�-1���ͷ�
			jsonArg.put("sDBPath", "/sdcard/MobileFARS/eabisldb_96");// 1����ʮָ����Ա��12��
			jsonArg.put("nThrd", 600); // ��ֵ [0,1000]��Ĭ��Ϊ600
			jsonArg.put("nMaxCan", 2); // ����ѡ������Ĭ��Ϊ5

			// ָλ���壺probeFiles1��probeFiles10 ��Ӧ ָλ 1��10������Ĵָ��ʳָ��СĴָ������Ĵָ��СĴָ
			jsonArg.put("probeFiles2", "/sdcard/DCIM/fingerCapAS602_" + 1
					+ ".bmp");
			jsonArg.put("probeFiles3", "/sdcard/DCIM/fingerCapAS602_" + 2
					+ ".bmp");

			// ����Ϊһ�����յ�JSON�ַ���
			retjson = jsonArg.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retjson;
	}

	@SuppressWarnings("static-access")
	public void delay(int time) {
		try {
			Thread.currentThread().sleep(time);// ��ʱ��ʱ�䣬����
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * ��ȡָ��ͼ����������
	 * 
	 * @Params��fileInPath:ָ��Bmp·��
	 * 
	 * @Return��inScore[0]:00H~64H
	 */
	private String getBmpScore(String bmpfilePath) {

		byte[] pImageData = new byte[256 * 360];
		byte[] inScore = new byte[2];
		String score = "";
		/* ��ȡ��256*360��Bmp��ʽͼ����Ҫ���·�תTrue */
		SDKUtilty.ReadBmpToRaw(pImageData, bmpfilePath, true);
		int nRet = AS60xIO.FCV_GetQualityScore(pImageData, inScore);
		if (1 == nRet) {
			score += inScore[0];
		} else {
			score = "ָ��ͼ��������ȡʧ��";
		}
		return score;
	}

	/**** ��̬��ʾͼƬ ****/
	private Bitmap facebmp1, facebmp2;
	private String Score1, Score2;

	private class FingerMatchTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected void onProgressUpdate(Integer... progress) {

			if (isCancelled())
				return;

			switch (progress[0]) {

			case 0:
				// printHint("USB�豸��Ȩ�ޣ������²��USB�豸��");
				break;

			case 1:
				mLfingerView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				mLfingerView.setImageBitmap(facebmp1);
				if (Integer.parseInt(Score1) < 60)
					mLfingerText.setTextColor(Color.RED);
				else
					mLfingerText.setTextColor(Color.BLACK);

				mLfingerText.setText("ͼ��������" + Score1);
				mLfingerView.invalidate();

				break;

			case 2:
				mRfingerView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				mRfingerView.setImageBitmap(facebmp2);
				if (Integer.parseInt(Score2) < 60)
					mLfingerText.setTextColor(Color.RED);
				else
					mLfingerText.setTextColor(Color.BLACK);
				mRfingerText.setText("ͼ��������" + Score2);
				mRfingerView.invalidate();
				break;

			case 3:
				setButtonStatus(true);
				break;
			case 4:
				printHint("ָ���ʧ�ܣ�");
				break;
			case 5:
				// mLfingerView.setImageResource(R.drawable.finger_bg);
				// mRfingerView.setImageResource(R.drawable.finger_bg);
				// scoreLeft.setText("");
				// scoreRight.setText("");
				break;

			default:
				// waitdialog.setMessage("default");//....
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Integer result) {
		}

		@Override
		protected void onCancelled() {
		}

		/* The Task body */
		@Override
		protected Integer doInBackground(Integer... params) {

			int nType = params[0];
			int ret = 0;
			if (nType == 1 || nType == 2) {
				String Filename = "/sdcard/DCIM/fingerCapAS602_" + nType
						+ ".bmp";
				String cmdString = "libfx2_finger190.so 2109 7638 tcs_finger "
						+ nType + " 73728 4096 " + Filename;
				FingerCntOld = MainActivity.jniFingerTCSCapStatus();
				// long nTimeStart = System.currentTimeMillis();
				ret = MainActivity.jniFinger190CapProcess(cmdString);
				// int nTimeCost = (int)((System.currentTimeMillis() -
				// nTimeStart));
				// print("��ʱ1/2=��"+nTimeCost+"����");

				// print("FingerCntOld12==="+FingerCntOld);
			} else if (nType == 3)// ˫ָ�ɼ�
			{
				String cmdString = "libfx2_finger190.so 2109 7638 tcs_finger "
						+ mDoubleFingers + " 73728 4096 ";

				FingerCntOld = MainActivity.jniFingerTCSCapStatus();
				// print("FingerCntOld3==="+FingerCntOld);
				// long nTimeStart = System.currentTimeMillis();
				ret = MainActivity.jniFingerTCSCapFingers(cmdString);
				// int nTimeCost = (int)((System.currentTimeMillis() -
				// nTimeStart));
				// print("��ʱ3=��"+nTimeCost+"����");
			} else if (nType == 0) {
				int delCount = 0;// ��ʱһ��ʱ���ɾ����ָ��
				while (!isStop) {
					if (isCancelled())
						return null;
					for (int i = 0; i < 10; i++)// 800ms
					{
						// ��ȡָ�Ʋɼ���״̬���Ƿ����
						FingerCnt = MainActivity.jniFingerTCSCapStatus();

						// print("FingerCnt==="+FingerCnt);
						if (FingerCnt > FingerCntOld)// ����ָ���������
						{

							delCount = 0;
							FingerCntOld = FingerCnt;
							if (capFinger1) {
								// print("publish capFinger1!!!!");
								FINGEREROLL1[2] = "/sdcard/DCIM/fingerCapAS602_1.bmp";
								Score1 = getBmpScore(FINGEREROLL1[2]);

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
								// print("publish capFinger2!!!!");
								FINGEREROLL2[2] = "/sdcard/DCIM/fingerCapAS602_2.bmp";
								Score2 = getBmpScore(FINGEREROLL2[2]);

								facebmp2 = BitmapFactory
										.decodeFile(FINGEREROLL2[2]);
								facebmp2 = FingerdatasAPI.scaleImg(facebmp2,
										320, 440);

								if (facebmp2 != null) {
									capFinger2 = true;
									publishProgress(2);
								}
							}

							if (isAutoUp && isMatchIdle && capFinger1
									&& capFinger2
									&& Integer.parseInt(Score1) >= 50
									&& Integer.parseInt(Score2) >= 50) {

								isMatchIdle = false;
								Log.d(TAG, "CallFingerIDService--isAutoUp:Ture");
								CallFingerIDService("fingeridtest001",
										"/sdcard/MobileFARS/",
										SetTHIDFingerIDArg());
								delay(100);
							}
							break;
						} else {

							delCount++;
							if (delCount > 10) {
								delCount = 0;
								// deleteFile("/sdcard/DCIM/fingerCapAS602_1.bmp");
								// deleteFile("/sdcard/DCIM/fingerCapAS602_2.bmp");
								publishProgress(5);
							}

							delay(80);// ˫ָһ��ɼ���Լ700���Һ���
						}
					}
					if (!isContinue) {
						publishProgress(3);
						MainActivity.jniFingerTCSCapStop(1);
						isStop = true;
						break;
					}
					if (ret == -311 || ret == -312 || ret == -313)// ���βɼ���ɼ�ָ��ͽ���ʧ��
					{
						publishProgress(4);
						MainActivity.jniFingerTCSCapStop(1);
						isStop = true;
						break;
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
			String setTimeCmd = "/system/bin/date -s 20140401.000000";
			RootCommand(setTimeCmd);

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
