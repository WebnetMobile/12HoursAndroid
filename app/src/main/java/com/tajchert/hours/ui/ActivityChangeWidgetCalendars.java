package com.tajchert.hours.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.tajchert.hours.R;
import com.tajchert.hours.calendar.CalendarContentResolver;
import com.tajchert.hours.calendar.CalendarObject;
import com.tajchert.hours.lists.CalendarListAdapter;
import com.tajchert.hours.widgets.WidgetInstance;
import com.tajchert.hours.widgets.WidgetListManager;

import java.util.ArrayList;

public class ActivityChangeWidgetCalendars extends Activity {
    private static final String TAG = "ActivityChangeWidgetCal";
	private CalendarListAdapter calendarListAdapter;
	private ArrayList<CalendarObject> listCalendars;
    private boolean shouldReturnResult = false;

	private ListView calendarList;
    private Button buttonSelect;
	
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.calendar_chooser_friends);
        calendarList = (ListView) findViewById(R.id.listViewCalendars);
        calendarList.setDividerHeight(1);

        Intent providedIntent = getIntent();
        shouldReturnResult = providedIntent.getBooleanExtra("forResult", false);
		buttonSelect = (Button) findViewById(R.id.buttonSelect);
		buttonSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO return results
				if(calendarListAdapter.data != null && calendarListAdapter.data.size()>0){
					String arrNames = "";
                    String arrIds = "";
                    String arrCol = "";
                    ArrayList<String> calNames = new ArrayList<String>();
					for (CalendarObject cal : calendarListAdapter.data) {
						if(cal.isChecked){
                            calNames.add(cal.name);
							arrNames += cal.name + "<;;>";
                            arrIds += cal.id + "<;;>";
                            arrCol += cal.color + "<;;>";
						}
					}
					try {
						arrNames = arrNames.substring(0, arrNames.length() - 4);
                        arrIds = arrIds.substring(0, arrIds.length() - 4);
                        arrCol = arrCol.substring(0, arrCol.length() - 4);
					} catch (Exception e) {
					}
                    if(shouldReturnResult){
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("calendars", arrNames);
                        returnIntent.putExtra("calendarsIds", arrIds);
                        returnIntent.putExtra("calendarsColors", arrCol);
                        returnIntent.putExtra("calendarsNamesArray", calNames.toArray(new String[calNames.size()]));

                        returnIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        setResult(RESULT_OK, returnIntent);
                        overridePendingTransition(0,0); //0 for no animation
                    } else {
                        Intent returnIntent = new Intent(ActivityChangeWidgetCalendars.this, ActivityFreeTime.class);
                        returnIntent.putExtra("calendars", arrNames);
                        returnIntent.putExtra("calendarsIds", arrIds);
                        returnIntent.putExtra("calendarsColors", arrCol);
                        returnIntent.putExtra("calendarsNamesArray", calNames.toArray(new String[calNames.size()]));

                        returnIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(returnIntent);
                        overridePendingTransition(0,0); //0 for no animation
                    }
					finish();
				}else{
                    if(shouldReturnResult){
                        Intent returnIntent = new Intent();
                        setResult(RESULT_CANCELED, returnIntent);
                    }
                    finish();
				}
			}
		});
		
    }
    @Override
    protected void onResume() {
        super.onResume();
        listCalendars = new ArrayList<>();
        listCalendars.clear();
        listCalendars = CalendarContentResolver.getCalendars(this);
        setList();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            setListFromExisitng(extras);
        }
    }

    private void setListFromExisitng(Bundle extras) {
        String widgetId = extras.getInt("widget_id") + "";
        String buttonText = extras.getString("button_text") + "";
        buttonSelect.setText(buttonText);
        if(widgetId.equals("")){
            return;
        }
        Log.d(TAG, "onCreate calendars:" + widgetId);

        SharedPreferences prefs = ActivityChangeWidgetCalendars.this.getSharedPreferences("com.tajchert.hours", Context.MODE_PRIVATE);
        WidgetInstance widget = WidgetListManager.getWidgetInstance(prefs, widgetId + "");
        calendarListAdapter = new CalendarListAdapter(listCalendars, ActivityChangeWidgetCalendars.this, widget);
        calendarList.setAdapter(calendarListAdapter);
        calendarListAdapter.notifyDataSetChanged();
    }


    private void setList(){
		calendarListAdapter = new CalendarListAdapter(listCalendars, ActivityChangeWidgetCalendars.this);
		calendarList.setAdapter(calendarListAdapter);
		calendarListAdapter.notifyDataSetChanged();
	}
}