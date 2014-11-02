package com.ivsign.fingertest;

public class MainActivity {

	public static native int jniInitFinger190();

	public static native int jniFinger190CapInit();

	public static native int jniFinger190CapProcess(String sFileName);

	public static native int jniFingerTCSCapStatus();

	public static native void jniFingerTCSCapStop(int isstop);

	public static native void jniFingerTCSCapContinue(int iscontinue);

	public static native int jniFingerTCSCapFingers(String sFileName);

	static {
		System.loadLibrary("fx2_finger190");
		// System.loadLibrary("fp190bulkasync");
	}

}
