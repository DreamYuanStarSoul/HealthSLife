package com.healthlife.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.healthlife.R;
import com.healthlife.db.DBManager;
import com.healthlife.entity.Beats;

/**
 * This example shows a expandable listview
 * with a more button per list item which expands the expandable area.
 *
 * In the expandable area there are two buttons A and B which can be click.
 *
 * The events for these buttons are handled here in this Activity.
 *
 * @author tjerk
 * @date 6/13/12 7:33 AM
 */
public class HeartHistory extends Activity {
	
	private DBManager myDB = null;
	ArrayList <Beats> hrHistory = null;
	ArrayList <Integer> hrBeats = null;
	ArrayList <HashMap<String,Object>> heartData = null;
	View mView = null;
	SimpleAdapter adapter = null;
	@Override
	public void onCreate(Bundle savedData) {

		super.onCreate(savedData);
		this.setContentView(R.layout.hearthistory_main);

		//init
		mView = new View(this);
		Button clearBt = (Button) findViewById(R.id.hr_clearbt);
		myDB = new DBManager(this);
		hrBeats = new ArrayList<Integer>();
		hrHistory = myDB.getBeatsList();
		ListView hrList = (ListView) findViewById(R.id.hr_list);
		BarChart histroyChart = (BarChart) findViewById(R.id.heart_barchart);
		
		 
		
		heartData = getHeartRateData();
		BarData historyData = getBarData(hrBeats); 
		showBarChart(histroyChart, historyData);  
		adapter = new SimpleAdapter(this,heartData,R.layout.hearthistory_listview,
				new String[]{"history","type"},new int[]{R.id.hr_listhistory,R.id.hr_listtype} );
		hrList.setAdapter(adapter);
		//���
		clearBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				for(int i=0;i<hrHistory.size();i++)
				{
					myDB.removeBeat(hrHistory.get(i).getBeatId());
				}
				hrHistory.clear();
				//�ذ�adapterˢ��listviews
				heartData.clear();
				adapter.notifyDataSetChanged();
//				list.setAdapter(buildDummyData());
			}
		});
		 // listviewע�������Ĳ˵�
		this.registerForContextMenu(hrList);
	}
	
	ArrayList <HashMap<String,Object>> getHeartRateData(){
		ArrayList <HashMap<String,Object>> data = new ArrayList <HashMap<String,Object>>();
		int n = hrHistory.size();
		for(int i=0;i<n;i++)
		{
			HashMap <String,Object> map = new HashMap <String,Object>();
			hrBeats.add(hrHistory.get(n-i-1).getBeats());
			Log.v("11111111",""+hrHistory.get(n-i-1).getBeats());
			map.put("history", hrHistory.get(n-i-1).getBeats()+"\n"+hrHistory.get(n-i-1).getDate());
			switch(hrHistory.get(n-i-1).getType())
			{
			case 1:
				map.put("type", "��Ϣ����");break;
			case 2:
				map.put("type", "�˶�������");break;
			case 3:
				map.put("type", "�������");break;
			default:
				;
			}
			data.add(map);
		}
		return data;
	}
	
	
	
	//�����˵�
	public void onCreateContextMenu(ContextMenu menu, View v,  
	        ContextMenuInfo menuInfo) {  
	    // set context menu title  
	    menu.setHeaderTitle("������ʷ");  
	    // add context menu item  
	    menu.add(0, 1, Menu.NONE, "ɾ��");  
	}  
	//�˵�������Ӧ
	public boolean onContextItemSelected(MenuItem item) {  
	    // �õ���ǰ��ѡ�е�item��Ϣ  
	    AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();  
	   // Log.v("TAG", "context item seleted ID="+ menuInfo.id);  
	      
	    switch(item.getItemId()) {  
	    case 1:  
	        // do something  
	    	int n = (int) (hrHistory.size()-menuInfo.id);
	    	myDB.removeBeat(hrHistory.get(n).getBeatId());
	    	hrHistory.remove(n);
	    	heartData.remove(n);
	    	adapter.notifyDataSetChanged();
	    	break;  
	    default:  
	        return super.onContextItemSelected(item);  
	    }  
	    return true;  
	}  
	
	//BarChart
    // ������ʾ����ʽ    
	private void showBarChart(BarChart barChart, BarData barData) {
		barChart.setDrawBorders(false);  ////�Ƿ�������ͼ����ӱ߿� 
	      
		barChart.setDescription("");// ��������    
		
        // ���û�����ݵ�ʱ�򣬻���ʾ���������ListView��EmptyView    
		barChart.setNoDataTextDescription("�޼�¼");    
               
		barChart.setDrawGridBackground(false); // �Ƿ���ʾ�����ɫ    
		barChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // ���ĵ���ɫ�����������Ǹ���ɫ����һ��͸����    
      
		barChart.setTouchEnabled(true); // �����Ƿ���Դ���    
     
		barChart.setDragEnabled(true);// �Ƿ������ק    
		barChart.setScaleEnabled(true);// �Ƿ��������    
    
		barChart.setPinchZoom(false);//     
    
//		barChart.setBackgroundColor();// ���ñ���    
		
		barChart.setDrawBarShadow(true);
       
		barChart.setData(barData); // ��������    

		Legend mLegend = barChart.getLegend(); // ���ñ���ͼ��ʾ
    
        mLegend.setForm(LegendForm.CIRCLE);// ��ʽ    
        mLegend.setFormSize(6f);// ����    
        mLegend.setTextColor(Color.BLACK);// ��ɫ    
        
//      X���趨
//      XAxis xAxis = barChart.getXAxis();
//      xAxis.setPosition(XAxisPosition.BOTTOM);
    
        barChart.animateX(2500); // ����ִ�еĶ���,x�� 	
	}

	private BarData getBarData(ArrayList<Integer> beats) {
		ArrayList<String> xValues = new ArrayList<String>();
		for (int i = 0; i < beats.size(); i++) {
			xValues.add("i");
		}
		
		ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        
		for (int i = 0; i < beats.size(); i++) {    
            int value = beats.get(i);
            yValues.add(new BarEntry(value, i));    
        }
		
		// y������ݼ���    
        BarDataSet barDataSet = new BarDataSet(yValues, "������ʷ"); 
        
        barDataSet.setColor(Color.rgb(114, 188, 223));
    
        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();    
        barDataSets.add(barDataSet); // add the datasets    
    
        BarData barData = new BarData(xValues, barDataSets);
		
		return barData;
	}

}