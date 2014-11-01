package com.ivsign.fingertest;

import android.content.Intent;

public class THIDServiceAPI {

	/*
	 * AlgType		算法类型
	 */
	public static final int ALGTYPE_FACEID = 6;		//人脸识别
	public static final int ALGTYPE_PLATEID = 21;	//车牌识别
	public static final int ALGTYPE_FINGERID = 13;	//指纹识别

	/*
	 * ServiceName	服务名称
	 */
	public static final String SERIVCE_FACEID = "com.THID.FaceSDK.FaceIDService";
	public static final String SERIVCE_PLATEID = "com.THID.FaceSDK.FaceIDService";
	public static final String SERIVCE_FINGERID = "com.THID.FingerSDK.FingerIDService";
	
	/*
	 * Action		
	 */
	public static final String ACTION_RECV_MSG_FACE = "com.THID.FaceSDK.intent.action.RESULT_MESSAGE";
	public static final String ACTION_RECV_MSG_PLATE = "com.THID.FaceSDK.intent.action.PLATEID_MESSAGE";
	public static final String ACTION_RECV_MSG_FINGER = "com.THID.FingerSDK.intent.action.RESULT_MESSAGE";
	
	/*
	 * Message
	 */
	public final static String MESSAGE_ID="MSG_reqid";
	public final static String MESSAGE_IN="MSG_input";
	public final static String MESSAGE_ARG="MSG_arg";
	public final static String MESSAGE_AlgType="MSG_algtype";
	public final static String MESSAGE_OUT="MSG_output";
	

	//-----------------------------------------------------------------------------------------------------	
	static public class THIDPlateIDArg
	{
		int nMinPlateWidth=0;			// 检测的最小车牌宽度，以像素为单位，>=40像素, 0为自适应
		int nMaxPlateWidth=0;			// 检测的最大车牌宽度，以像素为单位，最大为  nMinPlateWidth*10
		String szProvince="";			// 默认车牌省份 ，GBK编码
		
		THIDPlateIDArg(){
		}
		
		THIDPlateIDArg(int minPlateWidth, int maxPlateWidth, String province){
			nMinPlateWidth = minPlateWidth;
			nMaxPlateWidth = maxPlateWidth;
			szProvince = province;
		}
	}
	
	static public class THIDFaceIDArg1vN
	{
		String sDBPath = "";				// 本地库路径，相对路径
		int nThrd = 600;					// 阈值 [0,1000]，默认为600
		int nMaxCan = 5;				// 最大候选人数[1~20]，默认为5 
		
		THIDFaceIDArg1vN(){
		}
		
		THIDFaceIDArg1vN(String dbPath, int thrd, int maxcan){
			sDBPath = dbPath;
			nThrd = thrd;
			nMaxCan = maxcan;
		}
	}
	
	public class THIDFingerIDArg
	{
		int    nCmd;			//nCmd = 0：比对/按需加载；1：加载库；2，仅比对；-1，释放		
		String sDBPath;			// 本地库路径，绝对路径
		int nThrd = 200;		// 阈值 [0,1000]，默认为200
		int nMaxCan = 5;		// 最大候选人数[1~20]，默认为5 
		
		//指位定义：probeFiles1～probeFiles10 对应 指位 1～10，右手拇指、食指～小拇指，左手拇指～小拇指
		String probeFiles1, probeFiles2, probeFiles3, probeFiles4, probeFiles5;  //右手拇指、食指～小拇指
		String probeFiles6, probeFiles7, probeFiles8, probeFiles9, probeFiles10; //左手拇指～小拇指
		
		String probeFiles11 = null;//未知指纹
	}

	//-----------------------------------------------------------------------------------------------------
	/**生成THID识别服务intent
	 * @param algType	算法类型
	 * @param taskID	任务编号
	 * @param inPath	指定路径
	 * @param jsonArg	指定参数
	 * @return
	 */
	static public Intent GenTHIDServiceIntent( int algType, String taskID, String inPath, String jsonArg ){
		Intent msgIntent = new Intent( );
		
		if( ALGTYPE_FINGERID == algType ){		//指纹v3算法
			msgIntent.setAction( SERIVCE_FINGERID );
		}else if( ALGTYPE_FACEID == algType ){	//人脸v65算法
			msgIntent.setAction( SERIVCE_FACEID );
		}else if( ALGTYPE_PLATEID == algType ){	//车牌v4算法
			msgIntent.setAction( SERIVCE_PLATEID );
		}else
			return null;
		
	    msgIntent.putExtra( MESSAGE_AlgType, algType );	
        msgIntent.putExtra( MESSAGE_ID, taskID );		//返回结果时可以此标记区分
	    msgIntent.putExtra( MESSAGE_IN, inPath );		//输入工作路径
	    msgIntent.putExtra( MESSAGE_ARG, jsonArg );	//指定参数
	    
	    return msgIntent;
	}
	
}
