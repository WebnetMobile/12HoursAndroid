package com.tajchert.hours.calendar;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

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
			if (cursor != null && cursor.getCount() > 0) {
				CalendarObject newOne;
				while (cursor.moveToNext()) {
                    newOne = getCalendarObject(cursor);
					calendars.add(newOne);
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

    private static CalendarObject getCalendarObject(Cursor cursor) {
        CalendarObject newOne;
        newOne = new CalendarObject();
        newOne.id = cursor.getInt(0);
        newOne.name = cursor.getString(1);
        newOne.isSync = !cursor.getString(3).equals("0");
        newOne.isVisible = !cursor.getString(2).equals("0");
        newOne.color  = cursor.getInt(4);
        newOne.owner = cursor.getString(5);
        return newOne;
    }

    public void clear(){
		events.clear();
	}

    public ArrayList<Event> getEventList(Context context, int calendarId, int color){
        return getEventList(context, calendarId, color, false, true);
    }


	public ArrayList<Event> getEventList(Context context, int calendarId, int color, boolean showNotGoing, boolean showFullDay){
        long nowTime = Calendar.getInstance().getTimeInMillis();

        long nextDay = nowTime + (1000 * 60 * 60 * 24);// 24h
        long halfday = nowTime + (1000 * 60 * 60 * 12);//12h

        long prevTimeTwoDays = nowTime - (1000 * 60 * 60 * 24 * 10);// -10days
        long nextTimeTwoDays = nowTime + (1000 * 60 * 60 * 24 * 10);// 10 days ahead
		//TODO clean up this method!
        Cursor calCursor;
        Event eventTemp;
        String[] projection = new String[] { "calendar_id", "title", "begin", "end", "description", "event_id", "selfAttendeeStatus"};

        if(showFullDay){
            calCursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/instances/when/"+prevTimeTwoDays+"/"+nextTimeTwoDays), projection,"calendar_id = "+calendarId, null,null);
            if (calCursor.moveToFirst()) {
                do {
                    Calendar eventBegin = Calendar.getInstance();
                    eventBegin.setTimeInMillis(calCursor.getLong(2));
                    Calendar eventEnd = Calendar.getInstance();
                    eventEnd.setTimeInMillis(calCursor.getLong(3));
                    if(eventBegin.getTimeInMillis() < nowTime && eventEnd.getTimeInMillis() > nowTime) {
                        //Is full day or longer
                        eventTemp = new Event();
                        eventTemp.isfullDay = true;
                        if(eventBegin.getTimeInMillis() < nowTime) {
                            Calendar tmpStartDate = Calendar.getInstance();
                            tmpStartDate.setTimeInMillis(halfday);
                            eventTemp.dateStart = tmpStartDate;
                        } else {
                            eventTemp.dateStart = eventBegin;
                        }
                        if(eventEnd.getTimeInMillis() > halfday) {
                            Calendar tmpEndDate = Calendar.getInstance();
                            tmpEndDate.setTimeInMillis(halfday - 240000);//TODO fixme as this is due to problem of starting in 0 angle and ending at 0 angle - so we don't know if this is full day or 0 angle to mark
                            eventTemp.dateEnd = tmpEndDate;
                        } else {
                            eventTemp.dateEnd = eventEnd;
                        }
                        eventTemp.calendarId = calCursor.getInt(0);
                        eventTemp.eventId = calCursor.getInt(5);
                        eventTemp.title = calCursor.getString(1);
                        eventTemp.description = calCursor.getString(4);
                        int colorTmp = color;
                        int colorEvent = getEventColor(context, Integer.parseInt(calCursor.getString(5)));
                        if (colorTmp != 0 && colorEvent != 0) {
                            colorTmp = colorEvent;

                        }
                        eventTemp.color = colorTmp;
                        addToEventList(showNotGoing, eventTemp);
                    }
                } while (calCursor.moveToNext());
            }
        }
        //no fullday events
        calCursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/instances/when/"+nowTime+"/"+halfday), projection,"calendar_id = "+calendarId, null,null);
        if (calCursor.moveToFirst()) {
            do {
                Calendar eventBegin = Calendar.getInstance();
                eventBegin.setTimeInMillis(calCursor.getLong(2));
                Calendar eventEnd = Calendar.getInstance();
                eventEnd.setTimeInMillis(calCursor.getLong(3));
                addEvent12Hours(context, color, showNotGoing, nowTime, nextDay, halfday, calCursor, eventBegin, eventEnd);
            } while (calCursor.moveToNext());
        }

        try{
			if(!calCursor.isClosed()){
				calCursor.close();
			}
		} catch (Exception e) {
        }
		return events;
	}

    private void addEvent12Hours(Context context, int color, boolean showNotGoing, long nowTime, long nextDay, long halfday, Cursor calCursor, Calendar eventBegin, Calendar eventEnd) {
        Event eventTemp;
        if (eventEnd.getTimeInMillis() - nextDay < 0 && eventEnd.getTimeInMillis() - eventBegin.getTimeInMillis() < (1000 * 60 * 60 * 12)) {
            eventTemp = new Event();
            eventTemp.status = calCursor.getInt(6);
            eventTemp.dateStart = eventBegin;
            if (eventBegin.getTimeInMillis() < nowTime) {
                eventTemp.dateStart.setTimeInMillis(nowTime);
            }
            eventTemp.dateEnd = eventEnd;
            if (eventEnd.getTimeInMillis() > halfday) {
                eventEnd.setTimeInMillis(halfday);
                eventTemp.dateEnd = eventEnd;
            }
            eventTemp.calendarId = calCursor.getInt(0);
            eventTemp.eventId = calCursor.getInt(5);
            eventTemp.title = calCursor.getString(1);
            eventTemp.description = calCursor.getString(4);
            //Log.e(Tools.AWESOME_TAG, "STATUS: " + calCursor.getInt(6));//TODO

            int colorTmp = color;
            int colorEvent = getEventColor(context, Integer.parseInt(calCursor.getString(5)));
            if (colorTmp != 0 && colorEvent != 0) {
                colorTmp = colorEvent;

            }
            eventTemp.color = colorTmp;
            addToEventList(showNotGoing, eventTemp);
        }
    }

    private void addToEventList(boolean showNotGoing, Event eventTemp) {
        if(events.contains(eventTemp)) {
            return;
        }
        if (eventTemp.status != 2) {
            events.add(eventTemp);
        } else if (showNotGoing) {
            events.add(eventTemp);
        }
    }

    public int getEventColor(Context context, int eventid) {
		int color = 0;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"),
                new String[]{"calendar_id", "eventColor", "_id"}, "_id = " + eventid, null, null);
        String CNames[] = new String[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < CNames.length; i++) {
            color = cursor.getInt(1);
        }
        try {
            if (!cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {}
        return color;
	}
}