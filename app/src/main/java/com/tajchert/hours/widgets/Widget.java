package com.tajchert.hours.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.tajchert.hours.R;
import com.tajchert.hours.Tools;
import com.tajchert.hours.calendar.CalendarContentResolver;
import com.tajchert.hours.calendar.Event;
import com.tajchert.hours.ui.ClockDraw;

import java.util.Calendar;

public class Widget {
	
	
	private static int transparencyInnerColor = Tools.innerTransparency;
	private static int transparencyOutColor = Tools.outerTransparency;
	private static boolean useCalendarColor = false;

	
	public static Bitmap addStaticWidget(Context context, WidgetInstance widget){
		
		ClockDraw clock = new ClockDraw();
       
        
        String [] calendarsNames;
        String [] calendarsColors;
        int size = 250;
        if(widget != null){
        	 transparencyInnerColor = widget.transparencyInner;
             transparencyOutColor = widget.transparencyOuter;
             useCalendarColor = widget.useCalendarColor;
             calendarsNames =  widget.calendars.split("<;;>");
             calendarsColors = widget.calendarColors.split("<;;>");
             clock.radiusIn = widget.radiusIn;
             clock.radiusOut = widget.radiusOut;
             clock.widthIn = widget.widthIn;
             clock.widthOut = widget.widthOut;
             clock.prefs = context.getSharedPreferences("com.tajchert.hours", Context.MODE_PRIVATE);
             size = (int) widget.size;
        }else{
        	return null;
        }
        
        Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);  
		Canvas canvas = new Canvas(bitmap);
		try {
			CalendarContentResolver calRevolver = new CalendarContentResolver(context);
			calRevolver.clear();
			if(calendarsNames.length>0){
				if(calendarsColors.length != calendarsNames.length || !useCalendarColor){
					for(int i=0; i < calendarsNames.length; i++){
						calRevolver.getEventList(context, Integer.parseInt(calendarsNames[i]), 0, widget.showNotGoing, widget.showFullDay);
					}
				}else if(calendarsColors.length == calendarsNames.length && useCalendarColor){
					
					for(int i=0; i < calendarsNames.length; i++){
						calRevolver.getEventList(context, Integer.parseInt(calendarsNames[i]), Integer.parseInt(calendarsColors[i]), widget.showNotGoing, widget.showFullDay);
					}
				}
			}
			float opInner = transparencyInnerColor;
			for(int i=0; i<calRevolver.events.size(); i++){
				clock.drawEvent(dateToDegrees(calRevolver.events.get(i).dateStart), dateToDegrees(calRevolver.events.get(i).dateEnd), canvas, opInner, transparencyOutColor, calRevolver.events.get(i).color, calRevolver.events.get(i).dateStart.getTimeInMillis(), size, widget);
			}
		} catch (Exception e) {
			//Clock not added - no room or something
		}

        return bitmap;
	}
	
	public static void updateAppWidget(SharedPreferences prefs, Context context, AppWidgetManager appWidgetManager, int appWidgetId, WidgetInstance widget){
		PendingIntent pendingIntent;
		RemoteViews views;
		views = new RemoteViews(context.getPackageName(), R.layout.clock_widget);
		
		if(widget == null){
			appWidgetManager.updateAppWidget(appWidgetId, views);
			return;
		}
		Log.d(Tools.AWESOME_TAG, "Second:" +widget.actionSecond);
		if (widget.actionSecond != null && !widget.actionSecond.equals("")) {
			Intent tmp = context.getPackageManager().getLaunchIntentForPackage(
					widget.actionSecond);
			if (tmp != null) {
				pendingIntent = PendingIntent.getActivity(context, 0, tmp, 0);
				views.setOnClickPendingIntent(R.id.analogClockActivity, pendingIntent);
			}
		}else{
			pendingIntent = PendingIntent.getActivity(context, 0, getCalendar(context), 0);
			views.setOnClickPendingIntent(R.id.analogClockActivity, pendingIntent);
		}
		if (widget.action != null && !widget.action.equals("")) {
			Intent tmp = context.getPackageManager().getLaunchIntentForPackage(
					widget.action);
			if (tmp != null) {
				pendingIntent = PendingIntent.getActivity(context, 0, tmp, 0);
				views.setOnClickPendingIntent(R.id.buttonAlarm, pendingIntent);
			}
		}else{
			pendingIntent = PendingIntent.getActivity(context, 0, getCalendar(context), 0);
			views.setOnClickPendingIntent(R.id.buttonAlarm, pendingIntent);
		}
		Log.d(Tools.AWESOME_TAG, "style: "+ widget.style + ", size: " + widget.size+", res: " + widget.resolution);
		if(widget.style == 1){
			//CLASSIC NO DIGITS
			views.setImageViewResource(R.id.imageViewClockUnderflow, R.drawable.hand_nodigits_dial);
			if(widget.resolution  == 1){
    			widget.setDimensions(Tools.clock_layouts_dimensions_small[1]);
	   		}else if(widget.resolution  == 2){
	   			widget.setDimensions(Tools.clock_layouts_dimensions_medium[1]);
	   		}else if(widget.resolution  == 3){
	   			widget.setDimensions(Tools.clock_layouts_dimensions_big[1]);
	   		}
		}else if(widget.style == 0){
			//CLASSIC
			views.setImageViewResource(R.id.imageViewClockUnderflow, R.drawable.hand_dial);
			if(widget.resolution  == 1){
    			widget.setDimensions(Tools.clock_layouts_dimensions_small[0]);//TODO problem
	   		}else if(widget.resolution  == 2){
	   			widget.setDimensions(Tools.clock_layouts_dimensions_medium[0]);
	   		}else if(widget.resolution  == 3){
	   			widget.setDimensions(Tools.clock_layouts_dimensions_big[0]);
	   		}
		}else if(widget.style == 2){
			//KITKAT
			views.setImageViewResource(R.id.imageViewClockUnderflow, R.drawable.kit_kat_hand_dial);
			if(widget.resolution  == 1){
    			widget.setDimensions(Tools.clock_layouts_dimensions_small[2]);
	   		}else if(widget.resolution  == 2){
	   			widget.setDimensions(Tools.clock_layouts_dimensions_medium[2]);
	   		}else if(widget.resolution  == 3){
	   			widget.setDimensions(Tools.clock_layouts_dimensions_big[2]);
	   		}
		}else if(widget.style == 3){
			//KITKAT WHITEFull
			views.setImageViewResource(R.id.imageViewClockUnderflow, R.drawable.hand_whitefull_dial);
			if(widget.resolution  == 1){
    			widget.setDimensions(Tools.clock_layouts_dimensions_small[3]);
	   		}else if(widget.resolution  == 2){
	   			widget.setDimensions(Tools.clock_layouts_dimensions_medium[3]);
	   		}else if(widget.resolution  == 3){
	   			widget.setDimensions(Tools.clock_layouts_dimensions_big[3]);
	   		}
		}else{
			//CLASSIC
			views.setImageViewResource(R.id.imageViewClockUnderflow, R.drawable.hand_dial);
			if(widget.resolution  == 1){
    			widget.setDimensions(Tools.clock_layouts_dimensions_small[0]);
	   		}else if(widget.resolution  == 2){
	   			widget.setDimensions(Tools.clock_layouts_dimensions_medium[0]);
	   		}else if(widget.resolution  == 3){
	   			widget.setDimensions(Tools.clock_layouts_dimensions_big[0]);
	   		}
		}
		int size = 250;
		
		ClockDraw clock = new ClockDraw();
        
        String [] calendarsNames = null;
        String [] calendarsColors = null;
		transparencyInnerColor = widget.transparencyInner;
		transparencyOutColor = widget.transparencyOuter;
		useCalendarColor = widget.useCalendarColor;
		calendarsNames = widget.calendars.split("<;;>");
		calendarsColors = widget.calendarColors.split("<;;>");
		clock.radiusIn = widget.radiusIn;
		clock.radiusOut = widget.radiusOut;
		clock.widthIn = widget.widthIn;
		clock.widthOut = widget.widthOut;
		clock.prefs = prefs;
		size = (int) widget.size;

        Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);  
		Canvas canvas = new Canvas(bitmap);
		try {
			CalendarContentResolver calRevolver = new CalendarContentResolver(context);
			calRevolver.clear();
			if(calendarsNames.length>0){
				if(calendarsColors.length != calendarsNames.length || !useCalendarColor){
					for(int i=0; i < calendarsNames.length; i++){
						calRevolver.getEventList(context, Integer.parseInt(calendarsNames[i]), 0, widget.showNotGoing, widget.showFullDay);
					}
				}else if(calendarsColors.length == calendarsNames.length && useCalendarColor){
					
					for(int i=0; i < calendarsNames.length; i++){
						calRevolver.getEventList(context, Integer.parseInt(calendarsNames[i]), Integer.parseInt(calendarsColors[i]), widget.showNotGoing, widget.showFullDay);
					}
				}
			}
			float opInner = transparencyInnerColor;
			//Two passes to make whole day events go on the bottom
			for(int i=0; i<calRevolver.events.size(); i++){
				Event ev = calRevolver.events.get(i);
				if(ev.isfullDay){
					clock.drawEvent(dateToDegrees(ev.dateStart), (dateToDegrees(ev.dateEnd, dateToDegrees(ev.dateStart)) - 1), canvas, 0, transparencyOutColor, ev.color, ev.dateStart.getTimeInMillis(), size, widget);
				}
			}
			for(int i=0; i<calRevolver.events.size(); i++){
				Event ev = calRevolver.events.get(i);
				if(!ev.isfullDay){
					clock.drawEvent(dateToDegrees(ev.dateStart), dateToDegrees(ev.dateEnd), canvas, opInner, transparencyOutColor, ev.color, ev.dateStart.getTimeInMillis(), size, widget);
				}
			}
		} catch (Exception e) {
			//Clock not added - no room or something
		}
		views.setImageViewBitmap(R.id.imageViewClockOverflow, bitmap);
        appWidgetManager.updateAppWidget(appWidgetId, views);
        bitmap.recycle();
	}

	public static int adaptResolution(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay();
	    DisplayMetrics metrics = new DisplayMetrics();
	    display.getMetrics(metrics);
	    int width = metrics.widthPixels;
	    int height = metrics.heightPixels;
	    if(width < 490 || height < 490){
	    	//250x250
	    	return 1;
	    }else if((width >= 490 && width <900 )|| (height < 490 && height <900 )){
	    	//350x350
	    	return 2;
	    }else{
	    	//Should be 500x500 but due to HTC ONE is 350x350
	    	return 2;
	    }
		
	}
	
	private static Intent getCalendar(Context context){
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		i.setData(Uri.parse(CalendarContract.CONTENT_URI + "/time"));
		i.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}
	
	public static int dateToDegrees(Calendar in){
		int angle = 0;
		int hour = in.get(Calendar.HOUR);
		int minutes = in.get(Calendar.MINUTE);
		angle = hour * 30;
		angle += minutes * 0.5;
		if(in.getTimeInMillis()-Calendar.getInstance().getTimeInMillis()>43200000){//12hours
			Log.d(Tools.AWESOME_TAG, "Angle: " + angle);
			angle = 360 -angle;
		}
		return angle;
	}
	
	public static int dateToDegrees(Calendar in, int beg){
		Log.d(Tools.AWESOME_TAG, "beg: " + beg);
		int angle = 0;
		int hour = in.get(Calendar.HOUR);
		int minutes = in.get(Calendar.MINUTE);
		angle = hour * 30;
		angle += minutes * 0.5;
		if(in.getTimeInMillis()-Calendar.getInstance().getTimeInMillis()>43200000 && in.getTimeInMillis()-Calendar.getInstance().getTimeInMillis()<86400000){//12hours and 24h
			//TODO check if statement
			Log.d(Tools.AWESOME_TAG, "Distance" + (in.getTimeInMillis()-Calendar.getInstance().getTimeInMillis()));
			Log.d(Tools.AWESOME_TAG, "Angle: " + angle);
			Log.d(Tools.AWESOME_TAG, "beg: " + beg);
			angle =  beg;
		}
		return angle;
	}

}
