package com.tajchert.hours;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CalendarProviderListener extends BroadcastReceiver{
	@Override
    public void onReceive(Context context, Intent intent) {
		Intent serviceIntent = new Intent(context, WidgetUpdateService.class);
		context.startService(serviceIntent);
    }
}
