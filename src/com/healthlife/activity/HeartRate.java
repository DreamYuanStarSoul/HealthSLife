package com.healthlife.activity;

import java.math.RoundingMode;
import java.util.ArrayList;

import com.healthlife.db.DBManager;
import com.healthlife.entity.Beats;
import com.healthlife.util.Mallat;
import com.healthlife.util.RoundProgressBar;
import com.healthlife.util.YuvToRGB;
import com.healthlife.R;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class HeartRate extends Activity {

	private static final int NUM = 64;
	private Button historyButton = null ;
	private static Camera mCamera = null ;
	private CameraView myCV = null ;
	private TextView lastText= null;
	private TextView helpText = null;
	private TextView resultText = null;
	private double [] red = new double [NUM];
	private int point = 0;
	//��ʱ����/
	private long []time = new long [NUM];
	private RoundProgressBar progressBar  = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.heartrate_main);
		historyButton = (Button) findViewById(R.id.historybt);
		lastText = (TextView) findViewById(R.id.hrlast);
		resultText = (TextView) findViewById(R.id.hrresulttext);
		lastText.setText("�޼�¼"+"\n");
		//�������ͷ
//		if(checkCameraHardware(this)){
//			Log.e("==========", "����ͷ����");
//		}
		helpText = new TextView(this);
		ImageButton cameraButton = new ImageButton(this);
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.cameraview);
		myCV = new CameraView(HeartRate.this);
		progressBar = (RoundProgressBar) findViewById(R.id.roundbar);

		helpText.setText("�����ʼ����");
		helpText.setGravity(Gravity.CENTER);
		progressBar.setMax(NUM);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		cameraButton.setLayoutParams(lp);
		frameLayout.addView(cameraButton);
		helpText.setLayoutParams(lp);
		frameLayout.addView(helpText);
		//�趨�Զ���surfaceview��С
		lp = new FrameLayout.LayoutParams(1, 1);
		lp.gravity = Gravity.CENTER;
		myCV.setLayoutParams(lp);
		frameLayout.addView(myCV);  
//		myCV = (CameraView) findViewById(R.id.cameraview);


		//��ʾ���һ�μ�¼
		showLastHistory();
		//��ť��Ӧ
		cameraButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(null == mCamera)
				{
					startPreview(myCV.getSurfaceHolder());
					helpText.setText("�뽫��ָ�����"+"\n"+"����ͷ��");
				}
				else
				{
					helpText.setText("�����ʼ����");
					stopCamera();
					point = 0;
					progressBar.setProgress(point);
					resultText.setText("00");
				}
			}
		});
		
		historyButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(HeartRate.this,HeartHistory.class);
				startActivity(intent);
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
		stopCamera();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		helpText.setText("�����ʼ����");
		progressBar.setProgress(point);
		resultText.setText("00");
		//��ʾ���һ�μ�¼
		showLastHistory();
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
	private PreviewCallback mPriviewCallBack = new PreviewCallback(){

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
	        	 helpText.setText("�뽫��ָ�����"+"\n"+"����ͷ��");
	        	 point =0;
	        	progressBar.setProgress(point);//��ʼ��������
	         }
	         else
	         {
	        	 if(point == 0)
	        	 {
	        		 
	        		 helpText.setText("���ڲ�������"+"\n"+"�ƶ���ָ");
	        	 }
	        	 progressBar.setProgress(point); //����Բ�ν���������
		         red[point]=redAvg;
//		         Log.v("rrrrrrrred",""+redAvg);
		         point ++;
		         time[point] = System.currentTimeMillis();
		         if(point==31|| point == 47)

		        	 getHeartRate();
		         if(point==63)
		         {
		        	 getHeartRate();
		        	 
		        	 stopCamera();
		         }
	         }
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
	private void getHeartRate()
	{
		double [] output = Mallat.Analyze(red,point+1);
		int num = 0;
		int start = 0;
		int end =0;
		//Ѱ�ҷ���NUM
		for(int i =1;i<point;i++)
		{
//			Log.v("RESULT",""+output[i]);
			//2����Ϊlong�����ںܶ�λ������λ����ȵ����
			if((output[i]>output[i-1])&&(output[i]>output[i+1]))
			{
				if(num == 0)
				{
					start = i;
				}
				end = i;

				num++;
			}
		}
//
//		Log.v("time",""+startTime);
		double timeFinal = (double)(time[end]-time[start])/60000;
		//Ѱ���������
		num = (int) ((num-1)/timeFinal);
		num = num-20;
		resultText.setText(num+"");
		if(point == 63)
		{
			Intent intent = new Intent();
			intent.putExtra("heartrate", num);
			intent.setClass(HeartRate.this,HeartResult.class);
			startActivity(intent);
			point = 0;
			resultText.setText("00");
			progressBar.setProgress(point);//��ʼ��������
		}
	}
	
	//�Ի���

	
	//�������ͷ�Ƿ����
//	private boolean checkCameraHardware(Context context) {
//        if (context.getPackageManager().hasSystemFeature(
//                PackageManager.FEATURE_CAMERA)) {
//            // ����ͷ����
//            return true;
//        } else {
//            // ����ͷ������
//            return false;
//        }
//    }
//    
	//��ʾ���һ�μ�¼
	void showLastHistory(){
		DBManager myDB = new DBManager(this);
		ArrayList<Beats> lastHR = myDB.getBeatsList();
		TextView typeText = (TextView) findViewById(R.id.hrlasttype);
		if(lastHR.size()>0){
			lastText.setText(lastHR.get(lastHR.size()-1).getBeats()+
				"\n"+lastHR.get(lastHR.size()-1).getDate());
			switch(lastHR.get(lastHR.size()-1).getType())
			{
			case 1:
				typeText.setText("��Ϣ����");break;
			case 2:
				typeText.setText("�˶�������");break;
			case 3:
				typeText.setText("�������");break;
			default:
				;
			}
			
		}
		else
		{
			lastText.setText("�޼�¼"+"\n");
			typeText.setText("");
		}
	}
}
