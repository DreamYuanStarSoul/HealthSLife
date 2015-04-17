package com.healthlife.activity;

import com.healthlife.util.Mallat;
import com.healthlife.util.YuvToRGB;
import com.healthslife.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class HeartRate extends Activity {

	private static final int NUM = 64;
	private ImageButton cameraButton = null ;
	private static Camera mCamera = null ;
	private static CameraView myCV = null ;
	private static TextView testtext= null;
	private static double [] red = new double [NUM];
	private static int point = 0;
	//��ʱ����
	private static long startTime = 0;
	private static long endTime = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.heartrate_main);
		cameraButton = (ImageButton) findViewById(R.id.hrtestib);
		testtext = (TextView) findViewById(R.id.heartratetest);
		testtext.setText("�޼�¼");
		
		//�������ͷ
		if(checkCameraHardware(this)){
			Log.e("==========", "����ͷ����");
		}
		
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.cameraview);
		myCV = new CameraView(HeartRate.this);
		//�趨�Զ���surfaceview��С
		myCV.setLayoutParams(new FrameLayout.LayoutParams(10, 10));
		frameLayout.addView(myCV);  
		
		//��ť��Ӧ
		cameraButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(null == mCamera)
				{
					startPreview(myCV.getSurfaceHolder());
				}
				else
				{
					stopCamera();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		mCamera.stopPreview();
//		mCamera.release();
//        mCamera = null;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	//Ԥ��ͼ������ݴ���
	private static PreviewCallback mPriviewCallBack = new PreviewCallback(){

		@Override
		public void onPreviewFrame(byte[] data, Camera cam) {
			// TODO Auto-generated method stub
			 if (data == null) throw new NullPointerException();
	         Camera.Size size = cam.getParameters().getPreviewSize();
	         if (size == null) throw new NullPointerException();
	         
	         int width = size.width;
	         int height = size.height;
	         
	         double redAvg = YuvToRGB.getRed(data.clone(), width, height);
	         if(redAvg == -1)
	         {
	        	 point =0;
	         }
	         else
	         {
	        	 if(point == 0)
	        	 {
	        		 startTime = System.currentTimeMillis();
	        	 }
		         red[point]=redAvg;
		         point ++;
		         if(point==63)
		         {
		        	 endTime = System.currentTimeMillis();
		        	 getHeartRate();
		        	 
		        	 stopCamera();
		         }
	         }
	         Log.v("point",""+point);
	        
		}
		
	};
	
	//�Զ��������ͼ  ���������Ԥ��ͼ��
	 class CameraView extends SurfaceView implements SurfaceHolder.Callback{
		
		private  SurfaceHolder mHolder = null;
		
		public CameraView(Context context) {
			super(context);
			
			mHolder = this.getHolder();
			mHolder.addCallback(this);
			
			}
		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			// TODO Auto-generated method stub
			stopCamera();
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}
		
		public  SurfaceHolder getSurfaceHolder(){
			return mHolder;
		}
		
	}
    
	private void startPreview(SurfaceHolder holder){
		try{
			mCamera = Camera.open();
			//���������
            Camera.Parameters param = mCamera.getParameters(); 
            param.setFlashMode(Parameters.FLASH_MODE_TORCH);
//            param.setPreviewFpsRange(20,30);
            //���Ԥ���ص�
            mCamera.setPreviewCallback(mPriviewCallBack);
            mCamera.setParameters(param); 
            mCamera.setPreviewDisplay(holder); 
		}
		catch(Exception e){
		//���ʧ���ͷ�����ͷ

			Log.e("==========", "ERROR");
			mCamera.release();
	        mCamera = null;
		}
		//��ʼԤ��
		mCamera.startPreview();
	}
	
	private static void stopCamera(){
		if(null != mCamera)
		{
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
	        mCamera = null;
		}
	}
	
	//ȥ��������Ѱ������õ�����
	private static void getHeartRate()
	{
		Mallat.Analyze(red,NUM);
		double [] output = Mallat.MakeUp(NUM);
		int num = 0;
		int start = 0;
		int end =0;
		//Ѱ�ҷ���NUM
		for(int i =1;i<NUM-1;i++)
		{
			if(output[i]>output[i-1]&&output[i]>output[i+1])
			{
				if(num == 1)
				{
					start = i;
				}
				num++;
				end = i;
			}
		}
		Log.v("time",""+endTime);

		Log.v("time",""+startTime);
		double time = (double)(endTime-startTime)/60000;
		//��ʱ����0
		endTime = startTime = 0;
		//Ѱ���������
		num = (int) ((num-1)*(end-start)/time/NUM);
		testtext.setText(""+num);
		point = 0;
	}
	
	
	//�������ͷ�Ƿ����
	private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // ����ͷ����
            return true;
        } else {
            // ����ͷ������
            return false;
        }
    }
    
}
