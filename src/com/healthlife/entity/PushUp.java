package com.healthlife.entity;

public class PushUp extends Sports {

	public PushUp() {
		// TODO �Զ����ɵĹ��캯�����
	}
	
	final static int sportsType = PUSHUP;
	
	private int num;
	private int validNum;
	private float validRate;
	private float perfectRate;
	private int grade;
	
	/*
	 * getters
	 */
	protected int getNum() {
		return num;
	}
	protected float getValidRate() {
		return validRate;
	}
	protected float getPerfectRate() {
		return perfectRate;
	}
	protected int getGrade() {
		return grade;
	}
	protected int getValidNum() {
		return validNum;
	}

	
	/*
	 * setters
	 */
	protected void setNum(int num) {
		this.num = num;
	}
	protected void setValidRate(float validRate) {
		this.validRate = validRate;
	}
	protected void setPerfectRate(float perfectRate) {
		this.perfectRate = perfectRate;
	}
	protected void setGrade(int grade) {
		this.grade = grade;
	}
	protected void setValidNum(int validNum) {
		this.validNum = validNum;
	}

}
