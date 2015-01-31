package com.tajchert.hours.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.ImageView;

import com.tajchert.hours.Tools;
import com.tajchert.hours.widgets.Widget;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class ClockDrawFreeTime {
	private TreeMap<Long, Long> eventsTogether = new TreeMap<Long, Long>();
	private Context cont;
	private SharedPreferences prefs;
	private ImageView imageView;
	
	public ClockDrawFreeTime(ImageView imageView, Context cont, SharedPreferences pref, TreeMap<Long, Long> eventsTogether){
		this.cont = cont;
		this.eventsTogether = eventsTogether;
		this.prefs = pref;
		this.imageView = imageView;
	}
	
	void drawFull() {
		float radiusIn = 0;
		float radiusOut = 0;
		float widthIn = 0;
		float widthOut = 0;
		int size = 0;

		int res = prefs.getInt(Tools.RESULUTION_GOT, 0);
		if (res == 0) {
			res = Widget.adaptResolution(cont);
			prefs.edit().putInt(Tools.RESULUTION_GOT, res).apply();
		}
		if (res == 1) {
			radiusIn = 52.5f;
			radiusOut = 113f;
			widthIn = 111f;
			widthOut = 11f;
			size = 250;
		} else if (res == 2) {
			radiusIn = 73.5f;
			radiusOut = 158.2f;
			widthIn = 155.4f;
			widthOut = 15.4f;
			size = 350;
		} else if (res == 3) {
			radiusIn = 105f;
			radiusOut = 226f;
			widthIn = 222f;
			widthOut = 22f;
			size = 500;
		}
		ClockDraw clock = new ClockDraw();

		clock.prefs = prefs;
		clock.radiusIn = radiusIn;
		clock.radiusOut = radiusOut;
		clock.widthIn = widthIn;
		clock.widthOut = widthOut;
		int transparencyInnerColor = prefs.getInt(Tools.TRANSPARENCY_INNER,
				Tools.innerTransparency);
		int transparencyOutColor = prefs.getInt(Tools.TRANSPARENCY_OUTER,
				Tools.outerTransparency);

		Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		long lastEnd = Calendar.getInstance().getTimeInMillis();

		clock.drawEvent(0, 361, canvas, transparencyInnerColor,
                transparencyOutColor, Color.GREEN, lastEnd, size, null);
		imageView.setImageBitmap(bitmap);
	}
	void drawEmpty(){
		Bitmap bitmap = Bitmap.createBitmap(10, 10,Config.ARGB_8888);
        imageView.setImageBitmap(bitmap);
	}

	void drawEventsFromTogether() {
		float radiusIn = 0;
		float radiusOut = 0;
		float widthIn = 0;
		float widthOut = 0;
		int size = 0;

		int res = prefs.getInt(Tools.RESULUTION_GOT, 0);
		if (res == 0) {
			res = Widget.adaptResolution(cont);
			prefs.edit().putInt(Tools.RESULUTION_GOT, res).apply();
		}
		if (res == 1) {
			radiusIn = 52.5f;
			radiusOut = 113f;
			widthIn = 111f;
			widthOut = 11f;
			size = 250;
		} else if (res == 2) {
			radiusIn = 73.5f;
			radiusOut = 158.2f;
			widthIn = 155.4f;
			widthOut = 15.4f;
			size = 350;
		} else if (res == 3) {
			radiusIn = 105f;
			radiusOut = 226f;
			widthIn = 222f;
			widthOut = 22f;
			size = 500;
		}
		ClockDraw clock = new ClockDraw();

		clock.prefs = prefs;
		clock.radiusIn = radiusIn;
		clock.radiusOut = radiusOut;
		clock.widthIn = widthIn;
		clock.widthOut = widthOut;
		int transparencyInnerColor = prefs.getInt(Tools.TRANSPARENCY_INNER,
				Tools.innerTransparency);
		int transparencyOutColor = prefs.getInt(Tools.TRANSPARENCY_OUTER,
				Tools.outerTransparency);

		Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		long lastEnd = Calendar.getInstance().getTimeInMillis();
		long timeForNow = lastEnd;
		int degreesLastEnd = dateInMilisecondsToDegrees(lastEnd);

		for (Map.Entry<Long, Long> entry : eventsTogether.entrySet()) {
			Long entryStart = entry.getKey();
			Long entryEnd = entry.getValue();
			Date dateSt = new Date();
			Date dateEn = new Date();
			dateSt.setTime(entryStart);
			dateEn.setTime(entryEnd);
				clock.drawEvent(degreesLastEnd,
                        dateInMilisecondsToDegrees(entryStart), canvas,
                        transparencyInnerColor, transparencyOutColor,
                        Color.GREEN, lastEnd, size, null);
			
			lastEnd = entryEnd;
			degreesLastEnd = dateInMilisecondsToDegrees(entryEnd);
		}
		clock.drawEvent(degreesLastEnd, dateInMilisecondsToDegrees(timeForNow),
                canvas, transparencyInnerColor, transparencyOutColor,
                Color.GREEN, lastEnd, size, null);
        imageView.setImageBitmap(bitmap);
	}
	
	private int dateInMilisecondsToDegrees(long timeInMiliseconds) {
		Calendar in = Calendar.getInstance();
		in.setTimeInMillis(timeInMiliseconds);
		int hour = in.get(Calendar.HOUR);
		int minutes = in.get(Calendar.MINUTE);
		int angle = hour * 30;
		angle += minutes * 0.5;
		return angle;
	}

}
