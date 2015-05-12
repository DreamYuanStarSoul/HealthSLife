package com.healthlife.activity;

import java.math.BigDecimal;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.healthlife.R;
import com.healthlife.db.DBManager;
import com.healthlife.entity.Record;


public class ShowRecordActivity extends Activity {
	
	private DBManager db;
	private Record record;
	private Button btnClear;
	private TextView calOfPushUp,calOfSitUp,calOfJog,totalCal,durationPushUp,durationJog,durationSitUp,durationPerDay,totalDuration,totalDistance,totalNumPushUp,totalNumSitUp,totalSteps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_records);
		
		btnClear = (Button)findViewById(R.id.button_record_clear);
		
		
		calOfPushUp=(TextView)findViewById(R.id.text_push_cal);
		calOfSitUp=(TextView)findViewById(R.id.text_situp_cal);	
		calOfJog=(TextView)findViewById(R.id.text_jog_cal);
		totalCal=(TextView)findViewById(R.id.text_total_cal);
		
		durationPushUp=(TextView)findViewById(R.id.text_pushup_duration);	
		durationJog=(TextView)findViewById(R.id.text_jog_duration);		
		durationSitUp=(TextView)findViewById(R.id.text_situp_duration);		
		durationPerDay	=(TextView)findViewById(R.id.text_daily_duration);
		totalDuration=(TextView)findViewById(R.id.text_total_duration);
		
		totalDistance=(TextView)findViewById(R.id.text_total_distance);		
		totalNumPushUp=(TextView)findViewById(R.id.text_total_pushup);		
		totalNumSitUp=(TextView)findViewById(R.id.text_total_situp);		
		totalSteps=(TextView)findViewById(R.id.text_total_steps);		
		
		db = new DBManager(this);
		record = db.queryRecord();
		
		String hours,minutes,seconds;
		
		BigDecimal  bd = new BigDecimal(record.getCalOfPushUp());
		float f = bd.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue(); 
		calOfPushUp.setText("���Գ�����: "+String.valueOf(f)+" ��·��");
		
		bd = new BigDecimal(record.getCalOfSitUp());
		f = bd.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue(); 
		calOfSitUp.setText("������������: "+String.valueOf(f)+" ��·��");
		
		bd = new BigDecimal(record.getCalOfJog());
		f = bd.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue(); 
		calOfJog.setText("��������: "+String.valueOf(f)+" ��·��");
		
		bd = new BigDecimal(record.getTotalCal());
		f = bd.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue(); 
		totalCal.setText("������: "+String.valueOf(f)+" ��·��");

		bd = new BigDecimal(record.getDurationPushUp()/1000/60);
		f = bd.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
		durationPushUp.setText("���Գź�ʱ: "+String.valueOf(f)+"Min");
		
		bd = new BigDecimal(record.getDurationJog()/1000/60);
		f = bd.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
		durationJog.setText("���ܺ�ʱ: "+String.valueOf(f)+"Min");
		
		bd = new BigDecimal(record.getDurationSitUp()/1000/60);
		f = bd.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
		durationSitUp.setText("����������ʱ: "+String.valueOf(f)+"Min");

		//durationPerDay.setText("�վ��˶���ʱ: "+String.valueOf(record.getCalOfPushUp())+"h");
		bd = new BigDecimal(record.getTotalDuration()/1000/60);
		f = bd.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
		totalDuration.setText("�˶��ܺ�ʱ: "+String.valueOf(f)+"Min");
		
		bd = new BigDecimal(record.getTotalSteps());
		f = bd.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
		totalDistance.setText("�ܲ���: "+String.valueOf(f)+" ǧ��");
		
		totalNumPushUp.setText("�����ܾ���: "+String.valueOf(record.getTotalDistance())+" ��");
		totalNumSitUp.setText("���Գ�����: "+String.valueOf(record.getTotalNumPushUp())+" ��");
		totalSteps.setText("������������: "+String.valueOf(record.getTotalNumSitUp())+" ��");
			
		btnClear.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				db.clearRecord(record.getRecordId());
			}		
		});
		Log.i("dd", "aaa");
	}
}
