package com.healthlife.util;

import java.text.SimpleDateFormat;

import com.healthlife.entity.GlobalVariables;
import com.healthlife.entity.Sports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
 
/**
 * ����һ��ʵ�����źż����ļǲ�����
 * ���Ǵӹȸ�������һ���ǲ����㷨������̫��
 * @author Liyachao Date:2015-1-6
 *
 */
public class StepAnalyser implements SensorEventListener {
 
    private int mStep = 0;
    private float SENSITIVITY = 10; // SENSITIVITY������
    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;
    private static long end = 0;
    private static long start = 0;
    private long mStartTime;
    private boolean mStartFlag;
    private long mEndTime;
    private String mDate;
    private String mDuration;
    private Context context;
    private int mPace;
    private long mPaceStartTime;
    private int mPaceSamples;
    private long mCurrentTime;
    
    private Sports walk;
    /**
     * �����ٶȷ���
     */
    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;
 
    /**
     * ���������ĵĹ��캯��
     * 
     * @param context
     */
    @SuppressLint("SimpleDateFormat")
	public StepAnalyser(Context context) {
        super();
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd a hh:mm:ss");
        mDate=formatter.format(new java.util.Date());
        mStartFlag=false;      
        this.context = context;
    }
 
    //����������⵽����ֵ�����仯ʱ�ͻ�����������
    @SuppressLint("SimpleDateFormat")
	public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            	
            	if(mStartFlag){
        			SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        			mDate = simpleFormatter.format( new java.util.Date());
        			mStartTime = mCurrentTime;
        			mPaceStartTime = mCurrentTime;
        			mStartFlag = false;
        		}
				
                float vSum = 0;
                for (int i = 0; i < 3; i++) {
                    final float v = mYOffset + event.values[i] * mScale[1];//���㴹ֱ�ٶ�
                    vSum += v;
                }
                int k = 0;
                float v = vSum / 3;
 
                float direction = (v > mLastValues[k] ? 1
                        : (v < mLastValues[k] ? -1 : 0));//����ǰ�ٶȱ��ϴ��ٶȴ�����Ϊ��������Ϊ����0
                if (direction == -mLastDirections[k]) {
                    // Direction changed
                    int extType = (direction > 0 ? 0 : 1); // minumum or
                                                            // maximum?
                    mLastExtremes[extType][k] = mLastValues[k];
                    float diff = Math.abs(mLastExtremes[extType][k]
                            - mLastExtremes[1 - extType][k]);
 
                    if (diff > SENSITIVITY) {
                        boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                        boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                        boolean isNotContra = (mLastMatch != 1 - extType);
 
                        if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough
                                && isNotContra) {
                            end = System.currentTimeMillis();
                            if (end - start > 500) {// ��ʱ�ж�Ϊ����һ��
 
                                mStep++;
                                
                                mPaceAnalyse();
                                Intent intent = new Intent("com.healthlife.activity.WalkActivity.MotionAdd");  
           					 	intent.putExtra("motionNum", mStep);
           					 	context.sendBroadcast(intent);
           					 	
                                mLastMatch = extType;
                                start = end;
                                mEndTime = System.currentTimeMillis();
                            }
                        } else {
                            mLastMatch = -1;
                        }
                    }
                    mLastDiff[k] = diff;
                }
                mLastDirections[k] = direction;
                mLastValues[k] = v;
            }
 
        }
    }
    private void mPaceAnalyse() {
		// TODO Auto-generated method stub
    	if(mEndTime-mPaceStartTime<120000)
    		mPaceSamples+=1;
    	
    	else if(mEndTime-mPaceStartTime>=120000){
    		if(mPaceSamples>=0&&mPaceSamples<=50)
    			mPace=1;
    		else if(mPaceSamples>50&&mPaceSamples<=100)
    			mPace=2;
    		else if(mPaceSamples>100&&mPaceSamples<=150)
    			mPace=3;
    		else if(mPaceSamples<150&&mPaceSamples<=200)
    			mPace=4;
    		else if(mPaceSamples>200)
    			mPace=5;
    		
    		mPaceStartTime=System.currentTimeMillis();
    		mPaceSamples=0;
    		Intent intent = new Intent("com.healthlife.activity.MusicService");
    		intent.putExtra("pace", mPace);
    		context.startService(intent);
    	}
    		
		
	}

	//���������ľ��ȷ����仯ʱ�ͻ�������������������û����
    public void onAccuracyChanged(Sensor arg0, int arg1) {
 
    }
    
    @SuppressLint("SimpleDateFormat")
	public Sports getWalk(){
    	
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		mDuration = formatter.format(mEndTime-mStartTime);
		walk = new Sports();
		
    	walk.setType(GlobalVariables.SPORTS_TYPE_WALK);
    	walk.setNum(mStep);
    	walk.setDate(mDate);
    	walk.setDuration(mDuration);
    	return walk;
    }
 
}
