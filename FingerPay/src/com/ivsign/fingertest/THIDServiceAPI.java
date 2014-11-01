package com.ivsign.fingertest;

import android.content.Intent;

public class THIDServiceAPI {

	/*
	 * AlgType		�㷨����
	 */
	public static final int ALGTYPE_FACEID = 6;		//����ʶ��
	public static final int ALGTYPE_PLATEID = 21;	//����ʶ��
	public static final int ALGTYPE_FINGERID = 13;	//ָ��ʶ��

	/*
	 * ServiceName	��������
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
		int nMinPlateWidth=0;			// ������С���ƿ�ȣ�������Ϊ��λ��>=40����, 0Ϊ����Ӧ
		int nMaxPlateWidth=0;			// ��������ƿ�ȣ�������Ϊ��λ�����Ϊ  nMinPlateWidth*10
		String szProvince="";			// Ĭ�ϳ���ʡ�� ��GBK����
		
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
		String sDBPath = "";				// ���ؿ�·�������·��
		int nThrd = 600;					// ��ֵ [0,1000]��Ĭ��Ϊ600
		int nMaxCan = 5;				// ����ѡ����[1~20]��Ĭ��Ϊ5 
		
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
		int    nCmd;			//nCmd = 0���ȶ�/������أ�1�����ؿ⣻2�����ȶԣ�-1���ͷ�		
		String sDBPath;			// ���ؿ�·��������·��
		int nThrd = 200;		// ��ֵ [0,1000]��Ĭ��Ϊ200
		int nMaxCan = 5;		// ����ѡ����[1~20]��Ĭ��Ϊ5 
		
		//ָλ���壺probeFiles1��probeFiles10 ��Ӧ ָλ 1��10������Ĵָ��ʳָ��СĴָ������Ĵָ��СĴָ
		String probeFiles1, probeFiles2, probeFiles3, probeFiles4, probeFiles5;  //����Ĵָ��ʳָ��СĴָ
		String probeFiles6, probeFiles7, probeFiles8, probeFiles9, probeFiles10; //����Ĵָ��СĴָ
		
		String probeFiles11 = null;//δָ֪��
	}

	//-----------------------------------------------------------------------------------------------------
	/**����THIDʶ�����intent
	 * @param algType	�㷨����
	 * @param taskID	������
	 * @param inPath	ָ��·��
	 * @param jsonArg	ָ������
	 * @return
	 */
	static public Intent GenTHIDServiceIntent( int algType, String taskID, String inPath, String jsonArg ){
		Intent msgIntent = new Intent( );
		
		if( ALGTYPE_FINGERID == algType ){		//ָ��v3�㷨
			msgIntent.setAction( SERIVCE_FINGERID );
		}else if( ALGTYPE_FACEID == algType ){	//����v65�㷨
			msgIntent.setAction( SERIVCE_FACEID );
		}else if( ALGTYPE_PLATEID == algType ){	//����v4�㷨
			msgIntent.setAction( SERIVCE_PLATEID );
		}else
			return null;
		
	    msgIntent.putExtra( MESSAGE_AlgType, algType );	
        msgIntent.putExtra( MESSAGE_ID, taskID );		//���ؽ��ʱ���Դ˱������
	    msgIntent.putExtra( MESSAGE_IN, inPath );		//���빤��·��
	    msgIntent.putExtra( MESSAGE_ARG, jsonArg );	//ָ������
	    
	    return msgIntent;
	}
	
}
