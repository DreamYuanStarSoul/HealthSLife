package com.healthlife.activity;

import java.util.ArrayList;
import com.healthlife.db.DBManager;
import com.healthlife.entity.Beats;
import com.healthlife.util.Mallat;
import com.healthlife.util.RoundProgressBar;
import com.healthlife.util.YuvToRGB;
import com.healthlife.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HeartRate extends Activity {

	private static final int NUM = 64;
	private Button historyButton = null ;
	private static Camera mCamera = null ;
	private CameraView myCV = null ;
	private TextView lastText= null;
	private Button helpButton = null;
	private TextView resultText = null;
	private TextView lastBpmText = null;
	private double [] red = new double [NUM];
	private int point = 0;
	private ImageView heartImage = null;
	AnimationDrawable heartBeat = null;
	//��ʱ����/
	private long []time = new long [NUM];
	private RoundProgressBar progressBar  = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//init
		setContentView(R.layout.heartrate_main);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		historyButton = (Button) findViewById(R.id.historybt);
		lastText = (TextView) findViewById(R.id.hrlast);
		resultText = (TextView) findViewById(R.id.hrresulttext);
		heartImage = (ImageView) findViewById(R.id.heartimg);
		lastBpmText = (TextView)findViewById(R.id.lastbpm);
		//�������ͷ
//		if(checkCameraHardware(this)){
//			Log.e("==========", "����ͷ����");
//		}
		helpButton = (Button)findViewById(R.id.hrhelpbt);
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.cameraview);
		myCV = new CameraView(HeartRate.this);
		progressBar = (RoundProgressBar) findViewById(R.id.roundbar);

		//�趨�Զ���surfaceview��С
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(1, 1);

		lp.leftMargin = 70;
		lp.topMargin = 90;
		myCV.setLayoutParams(lp);
		relativeLayout.addView(myCV);  
		heartImage.setBackgroundResource(R.drawable.heart_beats);
		heartBeat= (AnimationDrawable) heartImage.getBackground(); 
		
		helpButton.setText("���ҿ�ʼ����");
		progressBar.setMax(NUM);
		lastText.setText("�޼�¼"+"\n");
		lastBpmText.setText("");
		

//		myCV = (CameraView) findViewById(R.id.cameraview);


		//��ʾ���һ�μ�¼
		showLastHistory();
		//��ť��Ӧ
		helpButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(null == mCamera)
				{
					startPreview(myCV.getSurfaceHolder());
					helpButton.setText("�뽫��ָ���"+"\n"+"������ͷ��");
				}
				else
				{
					helpButton.setText("���ҿ�ʼ����");
					stopCamera();
					point = 0;
					progressBar.setProgress(point);
					resultText.setText("00");
					heartBeat.stop();
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
		helpButton.setText("���ҿ�ʼ����");
		progressBar.setProgress(point);
		resultText.setText("00");
		heartBeat.stop();
		stopCamera();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
	        	 helpButton.setText("�뽫��ָ���"+"\n"+"������ͷ��");
	        	 point =0;
	        	 progressBar.setProgress(point);//��ʼ��������
	        	 resultText.setText("00");
	        	 heartBeat.stop();
	         }
	         else
	         {
	        	 if(point == 0)
	        	 {
	        		 heartBeat.start();
	        		 helpButton.setText("���ڲ�������"+"\n"+"�ƶ���ָ");
	        	 }
	        	 progressBar.setProgress(point); //����Բ�ν���������
		         red[point]=redAvg;
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
//            List<int[]> range=param.getSupportedPreviewFpsRange();  
//            Log.d("TAG", "range:"+range.size());  
//            for(int j=0;j<range.size();j++) {  
//                int[] r=range.get(j);  
//                for(int k=0;k<r.length;k++) {  
//                    Log.d("TAG", "TAG"+r[k]);  
//                }  
//            }  
            param.setFlashMode(Parameters.FLASH_MODE_TORCH);
            param.setPreviewFpsRange(7500,10000);
            //���Ԥ���ص�
            mCamera.setPreviewCallback(mPriviewCallBack);
            mCamera.setParameters(param); 
            mCamera.setPreviewDisplay(holder); 
		}
		catch(Exception e){
		//���ʧ���ͷ�����ͷ
			Toast.makeText(getApplicationContext(), "�������ʧ��",
				     Toast.LENGTH_SHORT).show();
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
		double timeFinal = (double)(time[end]-time[start])/60000;
		//Ѱ���������
		num = (int) ((num-1)/timeFinal);
		num = num-10;
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
			heartBeat.stop();
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
		ImageView typeImage = (ImageView) findViewById(R.id.hrlasttype);
		TextView lastDateText = (TextView) findViewById(R.id.hrlastdate);;
		if(lastHR.size()>0){
			lastText.setText(lastHR.get(lastHR.size()-1).getBeats()+"");
			lastDateText.setText(lastHR.get(lastHR.size()-1).getDate());
			lastBpmText.setText("BPM");
			switch(lastHR.get(lastHR.size()-1).getType())
			{
			case 1:
				typeImage.setImageResource(R.drawable.heartres_static_down);break;
			case 2:
				typeImage.setImageResource(R.drawable.heartres_run_down);break;
			case 3:
				typeImage.setImageResource(R.drawable.heartres_max_down);break;
			default:
				;
			}
			
		}
		else
		{
			lastText.setText("�޼�¼"+"\n");
			lastText.setText("");
			typeImage.setImageBitmap(null);
			lastDateText.setText("");
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return true;
	}
	
}
