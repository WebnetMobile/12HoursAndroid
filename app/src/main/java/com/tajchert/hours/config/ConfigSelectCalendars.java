package com.tajchert.hours.config;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.tajchert.hours.R;
import com.tajchert.hours.Tools;
import com.tajchert.hours.calendar.CalendarContentResolver;
import com.tajchert.hours.calendar.CalendarObject;
import com.tajchert.hours.lists.CalendarListAdapter;
import com.tajchert.hours.widgets.WidgetInstance;
import com.tajchert.hours.widgets.WidgetListManager;

import java.util.ArrayList;

public class ConfigSelectCalendars extends Fragment {
	private CalendarListAdapter marketListAdapter;
	private CheckBox checkBoxCalendarColors;
	private CheckBox checkBoxCalendarFullDay;
	private CheckBox checkBoxCalendarGoing;
	private ListView calendarList;
	private SharedPreferences prefs;
	private int mAppWidgetId = 0;
	private WidgetInstance widget;
	
	public ConfigSelectCalendars() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_calendar_chooser, container, false);
		setThings(v);
		getID();
		
        prefs = getActivity().getSharedPreferences("com.tajchert.hours", Context.MODE_PRIVATE);
        widget = WidgetListManager.getWidgetInstance(prefs, mAppWidgetId+"");
        setList();
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		if(calendarList!= null){
			setList();
		}
		super.onAttach(activity);
	}

	private void getID() {
		final Intent intent = getActivity().getIntent();
        final Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.d(Tools.AWESOME_TAG, "ID:" + mAppWidgetId);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            getActivity().finish();
        }
	}
	
	private void setThings(View v){
		checkBoxCalendarFullDay = (CheckBox) v.findViewById(R.id.checkBoxCalendarFullDay);
    	checkBoxCalendarColors = (CheckBox) v.findViewById(R.id.checkBoxCalendarColors);
    	checkBoxCalendarGoing = (CheckBox) v.findViewById(R.id.checkBoxCalendarGoing);
        calendarList = (ListView) v.findViewById(R.id.listViewCalendars);
        calendarList.setDividerHeight(1);
    }
	private void setList(){
        ArrayList<CalendarObject> listCalendars;
		listCalendars = new ArrayList<CalendarObject>();
        listCalendars.clear();
        listCalendars = CalendarContentResolver.getCalendars(getActivity());
		marketListAdapter = new CalendarListAdapter((ArrayList<CalendarObject>) listCalendars, getActivity(), widget);
		calendarList.setAdapter(marketListAdapter);
		marketListAdapter.notifyDataSetChanged();
	}
	public void saveThings(){
		Log.d(Tools.AWESOME_TAG, "saveThings!");
		new SaveAndClose().execute("");
	}
	
	private class SaveAndClose extends AsyncTask<String, Void, String> {
    	String arr ="";
    	String colArr = "";
    	ArrayList<String> calNames = new ArrayList<String>();
        @Override
        protected String doInBackground(String... params) {
        	arr ="";
        	colArr = "";
        	if(marketListAdapter == null ||  marketListAdapter.data == null ||  marketListAdapter.data.size() == 0){
        		Log.e(Tools.AWESOME_TAG, "Widget is null or some in doInBackground!");
        		try {
					getActivity().finish();
				} catch (Exception e) {
					// No activity
				}
        		return "";
        	}
			for (CalendarObject cal : marketListAdapter.data) {
				if(cal.isChecked){
					Log.d(Tools.AWESOME_TAG, "CHECKED: " + cal.name);
					calNames.add(cal.name);
					arr += cal.id + "<;;>";
					colArr += cal.color+ "<;;>";
				}
			}
			try {
				colArr = colArr.substring(0, colArr.length() - 4);
				arr = arr.substring(0, arr.length() - 4);
			} catch (Exception e) {
			}
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
        	if(marketListAdapter == null ||  marketListAdapter.data == null ||  marketListAdapter.data.size() == 0 || widget == null){
        		Log.e(Tools.AWESOME_TAG, "Widget is null or some in onPostExecute!");
        		try {
					getActivity().finish();
                    Toast.makeText(getActivity(), "Cannot find any calendars...", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					//Activity was already closed
				}
        		return;
        	}
        	
        	Log.d(Tools.AWESOME_TAG, "arr.length: " + arr.length());
        	widget.calendars = arr;
        	widget.calendarColors = colArr;
        	widget.calendarNames = calNames.toArray(new String[calNames.size()]);

            widget.useCalendarColor = checkBoxCalendarColors.isChecked();
            widget.showFullDay = checkBoxCalendarFullDay.isChecked();
            widget.showNotGoing = checkBoxCalendarGoing.isChecked();
        	Log.d(Tools.AWESOME_TAG, "SelectCal, style is: " + widget.style);
        	WidgetInstance tmpWidget = WidgetListManager.getWidgetInstance(prefs, mAppWidgetId+"");
        	if(tmpWidget != null && widget.style != tmpWidget.style){
        		widget.style = tmpWidget.style;
        	}
        	WidgetListManager.updateWidget(widget.id, prefs, widget);
        	
        	Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

        }

        @Override
        protected void onPreExecute() {}

    }
}
