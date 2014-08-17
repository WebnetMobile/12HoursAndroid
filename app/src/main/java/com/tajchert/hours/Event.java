package com.tajchert.hours;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Event {
	SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" , java.util.Locale.getDefault());
	
	public long calendarId;
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
	
	public Event(String input){
		String res[] = input.split("<<;;>>");
		this.title = res[0];
		this.dateStart = Calendar.getInstance();
		try {
			this.dateStart.setTime(form.parse(res[1]));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.dateEnd = Calendar.getInstance();
		try {
			this.dateEnd.setTime(form.parse(res[2]));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
