package com.healthlife.activity;

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
import android.widget.Button;
import android.widget.FrameLayout;

public class HeartRate extends Activity {

	private Button cameraButton = null ;
	private Camera mCamera = null ;
	private CameraView myCV = null ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.heartrate_main);
		cameraButton = (Button) findViewById(R.id.button);
		
		//�������ͷ
		if(checkCameraHardware(this)){
			Log.e("==========", "����ͷ����");
		}
		
		//��ť��Ӧ
		cameraButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FrameLayout frameLayout = (FrameLayout) findViewById(R.id.cameraview);
				myCV = new CameraView(HeartRate.this);
				frameLayout.addView(myCV);
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
	         
	         
		}
		
	};
	
	//�Զ��������ͼ  ���������Ԥ��ͼ��
	class CameraView extends SurfaceView {
		
		private SurfaceHolder mHolder = null;
		
		public CameraView(Context context) {
			super(context);
			
			mHolder = this.getHolder();
			
			mHolder.addCallback(new SurfaceHolder.Callback() {
				
				@Override
				public void surfaceDestroyed(SurfaceHolder arg0) {
					// TODO Auto-generated method stub
					mCamera.stopPreview();
					mCamera.release();
			        mCamera = null;
				}
				
				@Override
				public void surfaceCreated(SurfaceHolder arg0) {
					// TODO Auto-generated method stub
					try{
						mCamera = Camera.open();
						//���������
			            Camera.Parameters param = mCamera.getParameters(); 
			            param.setFlashMode(Parameters.FLASH_MODE_TORCH);
			            //���Ԥ���ص�
			            mCamera.setPreviewCallback(mPriviewCallBack);
			            mCamera.setParameters(param); 
			            mCamera.setPreviewDisplay(mHolder); 
					}
					catch(Exception e){
					//���ʧ���ͷ�����ͷ
						mCamera.release();
				        mCamera = null;
					}
					//��ʼԤ��
					mCamera.startPreview();
				}
				
				@Override
				public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					
				}
			});
				
			
			
		}
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
