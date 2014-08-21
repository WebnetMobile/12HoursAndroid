package com.tajchert.hours.widgets;

import java.util.Arrays;

public class WidgetInstance {
	public int id;
	public String calendarColors;
	public String calendars;
	public int style;
	public String[] calendarNames; 
	public String action;
	public String actionSecond;
	public int resolution;
	
	public float radiusIn;
	public float radiusOut;
	public float widthIn;
	public float widthOut;
	public float size;
	
	public int colorPallete;
	public boolean useCalendarColor;
	public boolean showNotGoing;
	public boolean showFullDay;
	
	public int transparencyOuter = 210;
	public int transparencyInner = 60;
	public int transparencyCenter = 120;//Gradient
	
	public void setDimensions(float [] arr){
		radiusIn = arr[0];
		radiusOut = arr[1];
		widthIn = arr[2];
		widthOut = arr[3];
		size = arr[4];
	}
	
	
	@Override
	public String toString() {
		return "WidgetInstance [id=" + id + ", calendarColors="
				+ calendarColors + ", calendars=" + calendars + ", style="
				+ style
				+ ", calendarNames=" + Arrays.toString(calendarNames)
				+ ", action=" + action + ", actionSecond=" + actionSecond
				+ ", resolution=" + resolution + ", radiusIn=" + radiusIn
				+ ", radiusOut=" + radiusOut + ", widthIn=" + widthIn
				+ ", widthOut=" + widthOut + ", size=" + size
				+ ", colorPallete=" + colorPallete + ", useCalendarColor="
				+ useCalendarColor + ", showNotGoing=" + showNotGoing
				+ ", showFullDay=" + showFullDay + ", transparencyOuter="
				+ transparencyOuter + ", transparencyInner="
				+ transparencyInner + ", transparencyCenter="
				+ transparencyCenter + "]";
	}
}
