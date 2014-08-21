package com.tajchert.hours.calendar;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tajchert.hours.WidgetUpdateService;

public class CalendarProviderListener extends BroadcastReceiver{
	@Override
    public void onReceive(Context context, Intent intent) {
		Intent serviceIntent = new Intent(context, WidgetUpdateService.class);
		context.startService(serviceIntent);
    }
}
