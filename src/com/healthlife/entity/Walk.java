package com.healthlife.entity;

public class Walk extends Sports {

	public Walk() {
		// TODO �Զ����ɵĹ��캯�����
	}
	
	final static int sportsType = WALK;
	
	private int steps;

	protected int getSteps() {
		return steps;
	}

	protected void setSteps(int steps) {
		this.steps = steps;
	}

}
