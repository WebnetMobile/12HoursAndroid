package com.tajchert.hours.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Event {
	SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" , java.util.Locale.getDefault());
	public int eventId;
    public int calendarId;
	public int color = 0;
	public String title;
	public Calendar dateStart;
	public Calendar dateEnd;
	public String description;
	public boolean isfullDay = false;
	public int status;
	
	@Override
	public String toString(){
		return title + "<<;;>>" + form.format(dateStart.getTime()) + "<<;;>>" + form.format(dateEnd.getTime()) + "<<;;>>" + description;
		
	}
	public Event(){
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		if (((Event) obj).eventId == this.eventId) {
			return true;
		}
		return false;
	}

}
