package com.healthlife.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.healthlife.R;
import com.healthlife.db.DBManager;
import com.healthlife.entity.GlobalVariables;
import com.healthlife.entity.Record;
import com.healthlife.entity.Sports;


public class ShowPushUpOrSitUpActivity extends Activity implements OnClickListener{
	
	private Sports sports;
	private DBManager db;
	private Intent intentToNextActivity;
	private TextView textNum,textPerfectNum,textDate,textDuration;
	private int showMode,sportsType;
	private PieChart mChart; 
	private Record record;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_sports);
		
		db = new DBManager(this);
		sportsType = getIntent().getIntExtra("type", 0);
		showMode = getIntent().getIntExtra("showmode",0);
		
		if(sportsType==GlobalVariables.SPORTS_TYPE_PUSHUP)
			sports = (Sports) getIntent().getSerializableExtra("pushup");
		else if(sportsType==GlobalVariables.SPORTS_TYPE_SITUP)
			sports = (Sports) getIntent().getSerializableExtra("situp");
		else 
			Log.e("healthSlife", "sportsType lost");
		
		intentToNextActivity=new Intent(this,MainActivity.class);
		

		Button btnSave = (Button)findViewById(R.id.button_save_sports);
		btnSave.setOnClickListener(this);			
			
		Button btnDrop = (Button)findViewById(R.id.button_drop_sports);
		btnDrop.setOnClickListener(this);
		
		if(GlobalVariables.MODE_SHOW_SAVED==showMode)
		{
			btnSave.setVisibility(View.GONE);
		}
		
		textNum = (TextView)findViewById(R.id.text_motion_num);
		textPerfectNum = (TextView)findViewById(R.id.text_perfect_motion);
		textDate = (TextView)findViewById(R.id.text_sports_date);
		textDuration = (TextView)findViewById(R.id.text_sports_duration);
		
		textNum.setText("Motion: "+String.valueOf(sports.getNum()));
		textPerfectNum.setText("Perfect: "+String.valueOf((int)sports.getPerfectNum()));
		textDate.setText("Date: "+String.valueOf(sports.getDate()));
		textDuration.setText("Duration: "+String.valueOf(sports.getDuration()));
		
        mChart = (PieChart) findViewById(R.id.piechart_motion);    
        PieData mPieData = getPieData(4, sports.getNum());    
        showChart(mChart, mPieData);		
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){		
		case R.id.button_save_sports:
			if(showMode==GlobalVariables.MODE_SHOW_UNSAVED){
				db.insertSport(sports);
				updateRecord();
			}
			startActivity(intentToNextActivity);
			break;
			
		case R.id.button_drop_sports:
			showDeleteWarning();
			break;	
		}
	}
	
	private void showDeleteWarning(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("ɾ��ȷ��");
		alertDialog.setMessage("�Ƿ�ɾ���˶���¼");
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton("��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if(GlobalVariables.MODE_SHOW_SAVED==showMode)
					db.removeSport(sports.getSportsID());
				startActivity(intentToNextActivity);		
			}
		});
		alertDialog.setNegativeButton("��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if(GlobalVariables.MODE_SHOW_UNSAVED==showMode){
					db.insertSport(sports);
					updateRecord();
				}
				startActivity(intentToNextActivity);
			}
		});		
		alertDialog.show();	
	}
	
	private void showChart(PieChart pieChart, PieData pieData) {    
        pieChart.setHoleColorTransparent(true);    
    
        pieChart.setHoleRadius(30f);  //�뾶    
        pieChart.setTransparentCircleRadius(64f); // ��͸��Ȧ    
    
        pieChart.setDescription("��������ͼ");     
        pieChart.setDrawCenterText(true);  //��״ͼ�м�����������        
        pieChart.setDrawHoleEnabled(true);        
        pieChart.setRotationAngle(90); // ��ʼ��ת�Ƕ�    
        pieChart.setRotationEnabled(true); // �����ֶ���ת    
        pieChart.setUsePercentValues(true);  //��ʾ�ɰٷֱ�    
        pieChart.setCenterText(String.valueOf(sports.getNum()));  //��״ͼ�м������       
        //��������    
        pieChart.setData(pieData);     
        Legend mLegend = pieChart.getLegend();  //���ñ���ͼ    
        mLegend.setPosition(LegendPosition.BELOW_CHART_CENTER);  //���ұ���ʾ    
        mLegend.setForm(LegendForm.CIRCLE);  //���ñ���ͼ����״��Ĭ���Ƿ���    
        mLegend.setXEntrySpace(7f);    
        mLegend.setYEntrySpace(5f);               
        pieChart.animateXY(1000, 1000);  //���ö���      
    }    
     
    private PieData getPieData(int count, float range) {    
            
        ArrayList<String> xValues = new ArrayList<String>();  //xVals������ʾÿ�������ϵ�����     
        
        	xValues.add("Not Bad: "+(int)sports.getValidNum());
        	xValues.add("Good: "+(int)sports.getGoodNum());
        	xValues.add("Perfect: "+(int)sports.getPerfectNum());
        	
    
        ArrayList<Entry> yValues = new ArrayList<Entry>();  //yVals������ʾ��װÿ�������ʵ������    
    
        // ��ͼ����    
        /**  
         * ��һ������ͼ�ֳ��Ĳ��֣� �Ĳ��ֵ���ֵ����Ϊ14:14:34:38  
         * ���� 14����İٷֱȾ���14%   
         */         
    
        yValues.add(new Entry(sports.getValidNum(), 0));    
        yValues.add(new Entry(sports.getGoodNum(), 1));    
        yValues.add(new Entry(sports.getPerfectNum(), 2));      
    
        //y��ļ���    
        PieDataSet pieDataSet = new PieDataSet(yValues, "���ද�������"/*��ʾ�ڱ���ͼ��*/);    
        pieDataSet.setSliceSpace(2f); //���ø���״ͼ֮��ľ���    
    
        ArrayList<Integer> colors = new ArrayList<Integer>();    
    
        // ��ͼ��ɫ    
        colors.add(Color.rgb(205, 205, 205));    
        colors.add(Color.rgb(114, 188, 223));    
        colors.add(Color.rgb(255, 123, 124));    
        colors.add(Color.rgb(57, 135, 200));    
    
        pieDataSet.setColors(colors);    
    
        DisplayMetrics metrics = getResources().getDisplayMetrics();    
        float px = 5 * (metrics.densityDpi / 160f);    
        pieDataSet.setSelectionShift(px); // ѡ��̬����ĳ���    
    
        PieData pieData = new PieData(xValues, pieDataSet);    
            
        return pieData;    
    }

    private void updateRecord(){
    	record = db.queryRecord();
    	if(GlobalVariables.SPORTS_TYPE_PUSHUP==sports.getType()){
    		if(record.getNumPushUp()<sports.getNum())
    			record.setNumPushUp(sports.getNum());
    		if(record.getGoodNumPushUp()<sports.getGoodNum())
    			record.setGoodNumPushUp(sports.getGoodNum());
    		if(record.getValidNumPushUp()<sports.getValidNum())
    			record.setValidNumPushUp(sports.getValidNum());
    		if(record.getPerfectNumPushUp()<sports.getPerfectNum())
    			record.setPerfectNumPushUp(sports.getPerfectNum());
    		
    		record.setTotalNumPushUp(record.getTotalNumPushUp()+sports.getNum());
    	}
    	else if(GlobalVariables.SPORTS_TYPE_SITUP==sports.getType()){
    		if(record.getNumSitUp()<sports.getNum())
    			record.setNumSitUp(sports.getNum());
    		if(record.getValidNumSitUp()<sports.getValidNum())
    			record.setValidNumSitUp(sports.getValidNum());
    		if(record.getPerfectNumSitUp()<sports.getPerfectNum())
    			record.setPerfectNumSitUp(sports.getPerfectNum());
    		
    		record.setTotalNumSitUp(record.getTotalNumSitUp()+sports.getNum());
    		
    	}
    		
    }
}
