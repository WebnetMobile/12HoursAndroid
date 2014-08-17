package com.tajchert.hours;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarContentResolver {
	public static ArrayList<CalendarObject> calendars = new ArrayList<CalendarObject>();
	public  ArrayList<Event> events = new ArrayList<Event>();

	public static final String[] FIELDS = { "_id", "calendar_displayName", "visible", "sync_events", "calendar_color", "ownerAccount"};

	public static final Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");

	static ContentResolver contentResolver;

	public CalendarContentResolver(Context ctx) {
		contentResolver = ctx.getContentResolver();
		events.clear();
	}

	public static ArrayList<CalendarObject> getCalendars(Context context) {
		// Fetch a list of all calendars sync'd with the device and their
		// display names
		calendars = new ArrayList<CalendarObject>();
		contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(CALENDAR_URI, FIELDS, null, null, null);
		try {
			if (cursor.getCount() > 0) {
				CalendarObject newOne;
				while (cursor.moveToNext()) {
					newOne = new CalendarObject();
					// String name = cursor.getString(0);
					// This is actually a better pattern:
					//String color = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR));
					//Boolean selected = !cursor.getString(2).equals("0");
					newOne.id = cursor.getInt(0);
					newOne.name = cursor.getString(1);
					newOne.isSync = !cursor.getString(3).equals("0");
					newOne.isVisible = !cursor.getString(2).equals("0");
					newOne.color  = cursor.getInt(4);
					newOne.owner = cursor.getString(5);
					calendars.add(newOne);
					//getEvents(context, "Tajchert");
					//Log.d("24hours", "newOne.name: " + newOne.name);
					//Log.d("24hours", "newOne.color: " + cursor.getString(4));
				}
			}
		} catch (AssertionError ex) {
			// TODO: log exception and bail
		}
		try {
			if( cursor != null && !cursor.isClosed() ){
				cursor.close();
			}
		} catch (Exception e) {
		}
		return calendars;
		
	}
	public void clear(){
		events.clear();
		//nameOfEvent.clear();
		//startDates.clear();
		//endDates.clear();
		//descriptions.clear();
	}
	public ArrayList<Event> testGet(Context context, int calendarId, int color, boolean showNotGoing, boolean showFullDay){
		//Log.d("24hours", "calendarName: " + calendarName +", color: "+color );
		long nowTime = Calendar.getInstance().getTimeInMillis();
		long nextDay = nowTime + (1000 * 60 * 60 * 24);// 24h
		long halfday = nowTime + (1000 * 60 * 60 * 12);//12h
		String[] projection = new String[] { "calendar_id", "title", "begin", "end", "description", "event_id", "selfAttendeeStatus"};
		//Cursor calCursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/instances/when/"+nowTime+"/"+halfday), projection,"calendar_displayName = \""+calendarName+"\"", null,null);
		Cursor calCursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/instances/when/"+nowTime+"/"+halfday), projection,"calendar_id = "+calendarId, null,null);
		
		//calendar_id = 8 AND 
		Event tmpOne = new Event();
		if (calCursor.moveToFirst()) {
			do {
				//long id = calCursor.getLong(0);
				//nameOfEvent.add(calCursor.getString(1));
				Calendar eventBegin = Calendar.getInstance();
				eventBegin.setTimeInMillis(calCursor.getLong(2));
				//startDates.add(i, tmp);
				Calendar eventEnd = Calendar.getInstance();
				eventEnd.setTimeInMillis(calCursor.getLong(3));
				//endDates.add(i, tmpE);
				//descriptions.add(calCursor.getString(4));
				if(eventEnd.getTimeInMillis()- nextDay < 0 && eventEnd.getTimeInMillis() - eventBegin.getTimeInMillis()< (1000 * 60 * 60 * 12)){
					tmpOne = new Event();
					tmpOne.status = calCursor.getInt(6);
					tmpOne.dateStart = eventBegin;
					if(eventBegin.getTimeInMillis()<nowTime){
						tmpOne.dateStart.setTimeInMillis(nowTime);
					}
					tmpOne.dateEnd = eventEnd;
					if(eventEnd.getTimeInMillis()>halfday){
						eventEnd.setTimeInMillis(halfday);
						tmpOne.dateEnd = eventEnd;
					}
					tmpOne.calendarId = calCursor.getInt(0);
					tmpOne.title = calCursor.getString(1);
					tmpOne.description = calCursor.getString(4);
					//Log.e(Tools.AWESOME_TAG, "STATUS: " + calCursor.getInt(6));//TODO
					
					int colorTmp = color;
					int colorEvent = getEventColor(context, Integer.parseInt(calCursor.getString(5)));
					if(colorTmp != 0 && colorEvent != 0){
						colorTmp = colorEvent;
						
					}
					tmpOne.color = colorTmp;
					if(tmpOne.status != 2){
						events.add(tmpOne);
					}else if(showNotGoing){
						events.add(tmpOne);
					}
					//Log.d("24hours", "events size: " + events.size());
					/*nameOfEvent.add(calCursor.getString(1));
					startDates.add(tmp);
					endDates.add(tmpE);
					descriptions.add(calCursor.getString(4));*/
				}else if(showFullDay && (eventEnd.getTimeInMillis() - eventBegin.getTimeInMillis()>= (1000 * 60 * 60 * 12))){
					//Fullday event is in next 24h
					
					if(eventBegin.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
						//eventEnd.setTimeInMillis(halfday);
						//eventBegin.setTimeInMillis(nowTime);
						eventBegin = Calendar.getInstance();
						
						eventEnd.set(Calendar.HOUR_OF_DAY, 24);
						eventEnd.set(Calendar.MINUTE, 0);
						
						tmpOne = new Event();
						tmpOne.isfullDay = true;
						tmpOne.dateStart = eventBegin;
						tmpOne.dateEnd = eventEnd;
						tmpOne.calendarId = calCursor.getInt(0);
						tmpOne.title = calCursor.getString(1);
						tmpOne.description = calCursor.getString(4);
						int colorTmp = color;
						int colorEvent = getEventColor(context, Integer.parseInt(calCursor.getString(5)));
						if(colorTmp != 0 && colorEvent != 0){
							colorTmp = colorEvent;
							
						}
						tmpOne.color = colorTmp;
						if(tmpOne.status != 2){
							events.add(tmpOne);
						}else if(showNotGoing){
							events.add(tmpOne);
						}
					}
					
					
				}
				//Log.d("24hours", "startMinute: " + tmp);
				//Log.d("24hours", "endMinute: " + dateFormat.format(tmpE.getTime()));
				//Log.d("24hours", "title: " + calCursor.getString(1));
			} while (calCursor.moveToNext());
			
		}
		try{
			if( calCursor != null && !calCursor.isClosed() ){
				calCursor.close();
			}
		} catch (Exception e) {
		}
		return events;
	}
	public int getEventColor(Context context, int eventid) {
		int color = 0;
		Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"),
				new String[] { "calendar_id", "eventColor", "_id" }, "_id = " + eventid , null, null);
		//+" AND calendar_displayName = \"" + calendarName+"\""
		String CNames[] = new String[cursor.getCount()];
		cursor.moveToFirst();
		for (int i = 0; i < CNames.length; i++) {
			color = cursor.getInt(1);
		}
		try{
			if( cursor != null && !cursor.isClosed() ){
				cursor.close();
			}
		} catch (Exception e) {
		}
		return color;
	}
	
	
	//NOT USED
	public void getEvents(Context context, String calendarName, int color) {
		Event tmpOne = new Event();
		//Log.d("24hours", "Calendar: " +calendarName);
		long nowTime = Calendar.getInstance().getTimeInMillis();
		long nextDay = nowTime + (1000 * 60 * 60 * 24);// 24h
		long halfday = nowTime + (1000 * 60 * 60 * 12);//12h
		Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"),
				new String[] { "calendar_id", "title", "description","dtstart", "dtend", "eventLocation", "selfAttendeeStatus",
						"calendar_displayName"}, "dtstart >= " + nowTime + " AND dtend <= " + nextDay +" AND calendar_displayName = \""+calendarName+"\"", null, null);
		//+" AND calendar_displayName = \"" + calendarName+"\""
		String CNames[] = new String[cursor.getCount()];
		//Log.d("24hours", "CNames.length: " + cursor.getCount());
		cursor.moveToFirst();
		for (int i = 0; i < CNames.length; i++) {
			//Log.d("24hours", "calendar_displayName: " + cursor.getString(7));
			if (cursor.getInt(6) != 2) { //2= not going to attend event
				tmpOne = new Event();
				Calendar tmp = Calendar.getInstance();
				tmp.setTimeInMillis(cursor.getLong(3));
				Calendar tmpp = Calendar.getInstance();
				tmpp.setTimeInMillis(cursor.getLong(4));
				tmpOne.dateStart = tmp;
				if(tmp.getTimeInMillis()<nowTime){
					tmpOne.dateStart.setTimeInMillis(nowTime);
				}
				if(tmpp.getTimeInMillis()>halfday){
					tmpp.setTimeInMillis(halfday);
					tmpOne.dateEnd = tmpp;
				}else{
					tmpOne.dateEnd = tmpp;
				}
				tmpOne.title = cursor.getString(1);
				tmpOne.description = cursor.getString(2);
				tmpOne.color = color;
				events.add(tmpOne);
			}
			cursor.moveToNext();
		}
		try{
			if( cursor != null && !cursor.isClosed() ){
				cursor.close();
			}
		} catch (Exception e) {
		}
		//Log.d("24hours", "nameOfEvent.size: " + nameOfEvent.size());

	}

	public static String getDate(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a",java.util.Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
}