package com.ivsign.fingerdatas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class FingerdatasAPI {
	private static final String TAG = "FingerTest";

//	public static UsbDevice device;
	public static UsbEndpoint mEndpointIn;
	public static UsbEndpoint mEndpointOut;
	
	
	//保存图片
	public static int SaveRawToBmp(byte[] Rawdata, String fileName, int sensorType)
	{
		byte[] bmp_head_640x480 = {
    			0x42, 0x4D, 0x38, (byte)0xC3, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x04, 0x00, 0x00, 0x28, 0x00, 
    			0x00, 0x00, (byte)0x80, 0x02, 0x00, 0x00, (byte)0xE0, 0x01, 0x00, 0x00, 0x01, 0x00, 0x08, 0x00, 0x00, 0x00, 
    			0x00, 0x00, 0x02, (byte)0xB0, 0x04, 0x00, 0x12, 0x0B, 0x00, 0x00, 0x12, 0x0B, 0x00, 0x00, 0x00, 0x00};
		
		byte[] bmp_head_256x360 = {
    			0x42, 0x4D, 0x36, 0x6C, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x04, 0x00, 0x00, 0x28, 0x00, 
    			0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x68, 0x01, 0x00, 0x00, 0x01, 0x00, 0x08, 0x00, 0x00, 0x00, 
    			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};
		
    	byte[] bmp_colmap = {00, 00, 00, 00};
    	byte[] fixarray = new byte[1024];
    	
    	for(int i=0; i<1024; i++)
    		fixarray[i] = 0;
		try{
				FileOutputStream stream = new FileOutputStream(fileName);
				if( sensorType == 1 )  // 190原始数据,无同步定位，640×480
				{
					stream.write(bmp_head_640x480);
					stream.write(bmp_colmap, 0, 3);
					stream.write(bmp_colmap, 0, 3);
					for(int i=0; i<480; i++)
					{
						bmp_colmap[0] = bmp_colmap[1] = bmp_colmap[2] = (byte)i;
						stream.write(bmp_colmap);
					}
					
					stream.write(Rawdata);  //直接写入即可
				}
				else if( sensorType == 2 )// TSC1ST 原始数据，256×360
				{

					stream.write(bmp_head_256x360);
					stream.write(bmp_colmap, 0, 3);
					stream.write(bmp_colmap, 0, 3);
					for(int i=0; i<256; i++)
					{
						bmp_colmap[0] = bmp_colmap[1] = bmp_colmap[2] = (byte)i;
						stream.write(bmp_colmap);
					}
				
					//BMP颠倒顺序写入即可
					for(int i=0; i<360; i++)
					{
						stream.write(Rawdata, (360-1-i)*256, 256);  
					}
				}
				stream.close();
			} 
		catch (FileNotFoundException e)
		{
			return -2;
		}
		catch (IOException e)
		{
			return -1;
		}
		return 0;
	}
	
	
    public static Bitmap scaleImg(Bitmap bm, int newWidth, int newHeight)
    {// 图片源
    	if (bm == null)
    		return null;
         int width =bm.getWidth();
         int height = bm.getHeight();
         // 设置想要的大小
         int newWidth1 = newWidth;
         int newHeight1 =newHeight;
         // 计算缩放比例
         float scaleWidth = ((float) newWidth1) / width;
         float scaleHeight = ((float) newHeight1) /height;
         // 取得想要缩放的matrix参数
         Matrix matrix = new Matrix();
         matrix.postScale(scaleWidth, scaleHeight);
         
         // 得到新的图片
         Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
         return newbm;
    }
	
}
