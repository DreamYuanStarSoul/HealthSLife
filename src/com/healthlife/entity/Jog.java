package com.healthlife.entity;
import java.util.Date;

public class Jog extends Sports {

	public Jog() {
		// TODO �Զ����ɵĹ��캯�����
	}
	
	final static int sportsType = JOG;
	
	private Date duration;
	private int  distance;
	private int  AVGSpeed;
	private int  AVGPace;
	private float[][] position; 
	
	//----------------------setters---------------------------------
	
	protected void setDuration(Date duration) {
		this.duration = duration;
	}
	protected void setDistance(int distance) {
		this.distance = distance;
	}
	protected void setAVGSpeed(int aVGSpeed) {
		AVGSpeed = aVGSpeed;
	}
	protected void setAVGPace(int aVGPace) {
		AVGPace = aVGPace;
	}
	protected float[][] getPosition() {
		return position;
	}
	
	
//----------------------getters--------------------------------
	
	protected int getSportsType() {
		return sportsType;
	}
	protected Date getDuration() {
		return duration;
	}
	protected int getDistance() {
		return distance;
	}
	protected int getAVGSpeed() {
		return AVGSpeed;
	}
	protected int getAVGPace() {
		return AVGPace;
	}
	protected void setPosition(float[][] position) {
		this.position = position;
	}
}

