package com.tajchert.hours.config;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.tajchert.hours.R;
import com.tajchert.hours.Tools;
import com.tajchert.hours.ui.TextAwesomeClicable;
import com.tajchert.hours.widgets.WidgetInstance;
import com.tajchert.hours.widgets.WidgetListManager;

public class ConfigStyleSelect extends Fragment {
	private ImageView imageViewClockOverflow;
	private TextView styleNumber;
	
	private SharedPreferences prefs;
	
	private WidgetInstance widget;
	private int layoutPos = 0;
	
	
	public ConfigStyleSelect() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v =  inflater.inflate(R.layout.fragment_config_style_select, container, false);
		prefs = getActivity().getSharedPreferences("com.tajchert.hours", Context.MODE_PRIVATE);
		setThings(v);
		getID();
        return v;
	}
	
	private int getID() {
		final Intent intent = getActivity().getIntent();
        final Bundle extras = intent.getExtras();
        int mAppWidgetId = 0;
		if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.d(Tools.AWESOME_TAG, "ID:" + mAppWidgetId);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            getActivity().finish();
        }
        return mAppWidgetId;
        
	}
	public void saveStyle(){
		new SaveStyle().execute("");
	}
	
	private void setThings(View v){
        TextAwesomeClicable designArrowRight;
        TextAwesomeClicable designArrowLeft;

		imageViewClockOverflow = (ImageView) v.findViewById(R.id.imageViewClockOverflow);
    	designArrowRight= (TextAwesomeClicable) v.findViewById(R.id.designArrowRight);
    	designArrowLeft= (TextAwesomeClicable) v.findViewById(R.id.designArrowLeft);
    	styleNumber = (TextView) v.findViewById(R.id.textViewStyleNumber);
    	setStyleNumber();
    	
    	imageViewClockOverflow.setImageDrawable(getResources().getDrawable(Tools.clock_layouts[layoutPos]));
    	designArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(layoutPos < (Tools.clock_layouts.length-1)){
            		layoutPos++;
                	imageViewClockOverflow.setImageDrawable(getResources().getDrawable(Tools.clock_layouts[layoutPos]));
                	setStyleNumber();
            	}
            }
        });
    	designArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(layoutPos > 0){
            		layoutPos--;
                	imageViewClockOverflow.setImageDrawable(getResources().getDrawable(Tools.clock_layouts[layoutPos]));
                	setStyleNumber();
            	}
            }
        });
    }
	
	private void setStyleNumber(){
		if(styleNumber != null){
			styleNumber.setText((layoutPos+1)+"/"+(Tools.clock_layouts.length));
		}
	}
	private class SaveStyle extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
        	WidgetListManager.updateWidget(widget.id, prefs, widget);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        	widget = WidgetListManager.getWidgetInstance(prefs, getID()+"");
        	Log.d(Tools.AWESOME_TAG, "CAL size in Style: " + widget.calendarNames.length);
        	widget.style = layoutPos;
        }
    }
}

