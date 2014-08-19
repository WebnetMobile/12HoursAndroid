package com.tajchert.hours.lists;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.tajchert.hours.CalendarContentResolver;
import com.tajchert.hours.CalendarObject;
import com.tajchert.hours.R;

import java.util.ArrayList;

public class PickCalendars extends Activity {
	private CalendarListAdapter marketListAdapter;
	private ArrayList<CalendarObject> listCalendars;

	private ListView calendarList;
	
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.calendar_chooser_friends);
        calendarList = (ListView) findViewById(R.id.listViewCalendars);
        calendarList.setDividerHeight(1);
        
       
		Button buttonSelect = (Button) findViewById(R.id.buttonSelect);
		buttonSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO return results
				if(marketListAdapter.data != null && marketListAdapter.data.size()>0){
					String arr = "";
					for (CalendarObject cal : marketListAdapter.data) {
						if(cal.isChecked){
							arr += cal.id + "<;;>";
						}
					}
					try {
						arr = arr.substring(0, arr.length() - 4);
					} catch (Exception e) {
					}
					Intent returnIntent = new Intent();
					returnIntent.putExtra("calendars", arr);
					setResult(RESULT_OK, returnIntent);
					finish();
				}else{
					Intent returnIntent = new Intent();
					setResult(RESULT_CANCELED, returnIntent);
					finish();
				}
			}
		});
		
    }
    @Override
    protected void onResume() {
        super.onResume();
        listCalendars = new ArrayList<CalendarObject>();
        listCalendars.clear();
        listCalendars = CalendarContentResolver.getCalendars(this);
        setList();
    }
    
    
    
    private void setList(){
		marketListAdapter = new CalendarListAdapter((ArrayList<CalendarObject>) listCalendars, PickCalendars.this);
		calendarList.setAdapter(marketListAdapter);
		marketListAdapter.notifyDataSetChanged();
	}
}