package com.tajchert.hours.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.tajchert.hours.WidgetUpdateService;

public class WidgetProviderSmall extends AppWidgetProvider{
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		SharedPreferences prefs = context.getSharedPreferences("com.tajchert.hours", Context.MODE_PRIVATE);
		for(int num: appWidgetIds){
			WidgetListManager.removeWidget(num, prefs);
		}
	    super.onDeleted(context, appWidgetIds);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {  
		super.onUpdate(context, appWidgetManager, appWidgetIds); 
		Intent serviceIntent = new Intent(context, WidgetUpdateService.class);
		context.startService(serviceIntent);
  }
}