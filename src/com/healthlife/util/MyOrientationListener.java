package com.healthlife.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MyOrientationListener implements SensorEventListener {

	private OnOrientationListener onOrientationListener;
	private SensorManager sensorManager;
	private Context context;
	private Sensor sensor;
	private float lastX;
	
	public MyOrientationListener(Context context){
		this.context = context;
	}
	
	public void start(){
		//��ô�����������
		sensorManager = (SensorManager)context
				.getSystemService(Context.SENSOR_SERVICE);
		if(sensorManager != null){
			//��÷��򴫸���
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		}
		if(sensor != null){
			sensorManager.registerListener(this, sensor, 
					SensorManager.SENSOR_DELAY_UI);
		}
	}
	
	//ֹͣ���
	public void stop(){
		sensorManager.unregisterListener(this);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			float x = event.values[SensorManager.DATA_X];
			if(Math.abs(x - lastX) > 1.0){
				if(onOrientationListener != null){
					onOrientationListener.onOrientationChanged(x);
				}
			}
			lastX = x;
		}
	}
	
	public interface OnOrientationListener{
		void onOrientationChanged(float x);
	}
	
	public void setOnOrientationListener(
			OnOrientationListener onOrientationListener) {
		this.onOrientationListener = onOrientationListener;
	}
}
