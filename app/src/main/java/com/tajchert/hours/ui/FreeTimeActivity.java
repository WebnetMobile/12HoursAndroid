package com.tajchert.hours.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;

import com.tajchert.hours.R;
import com.tajchert.hours.calendar.CalendarContentResolver;
import com.tajchert.hours.calendar.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

public class FreeTimeActivity extends ActionBarActivity {
    private ImageView imageView;
    private SharedPreferences prefs;
    private static TreeMap<Long, Long> eventsTogether = new TreeMap<Long, Long>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_time);
        imageView = (ImageView) findViewById(R.id.imageViewActivity);
        prefs = this.getSharedPreferences("com.tajchert.hours", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        if(intent != null){
            drawFreeTime(intent);
        }
    }

    private void drawFreeTime(Intent data) {
        ClockDrawFreeTime clockSurface = new ClockDrawFreeTime(imageView, FreeTimeActivity.this, prefs, eventsTogether);
        String calendars = data.getStringExtra("calendars");
        String[] calendarsNames = calendars.split("<;;>");
        CalendarContentResolver calRevolver = new CalendarContentResolver(FreeTimeActivity.this);
        calRevolver.clear();
        eventsTogether.clear();
        for (int i = 0; i < calendarsNames.length; i++) {
            try {
                ArrayList<Event> listEventsFriend = calRevolver.getEventList(FreeTimeActivity.this, Integer.parseInt(calendarsNames[i]), 0, false, false);
                Log.d("24Hours", "listEventsFriend.size(): " + listEventsFriend.size());
                if (listEventsFriend != null && listEventsFriend.size() > 0) {
                    for (Event ev : listEventsFriend) {
                        addEventToTogether(ev);
                    }
                }
            } catch (NumberFormatException e) {
                clockSurface.drawEmpty();
            }
        }
        if (calendars == null || calendars.length() == 0 || calendarsNames == null || calendarsNames.length == 0) {
            clockSurface.drawEmpty();
        }else if(eventsTogether.size()>0){
            clockSurface.drawEventsFromTogether();
        }else{
            clockSurface.drawFull();
        }
    }

    private void addEventToTogether(Event ev) {
        // TODO not important
        Long nowTime = (Calendar.getInstance().getTimeInMillis()/1000) *1000;
        Long newStart = (ev.dateStart.getTimeInMillis()/1000) *1000;
        Long newEnd = (ev.dateEnd.getTimeInMillis()/1000) * 1000;
        // eventsTogether
        if(newStart < nowTime ){
            newStart = nowTime;
        }
        if (eventsTogether.size() == 0) {
            eventsTogether.put(newStart, newEnd);
        } else {
            boolean merge = true;

            try {
                for (Map.Entry<Long, Long> entry : eventsTogether.entrySet()) {
                    merge = true;
                    Long entryStart = entry.getKey();
                    Long entryEnd = entry.getValue();
                    if(entryStart < nowTime ){
                        entryStart = nowTime;
                    }
                    if (newStart < entryStart && newEnd <= entryEnd && newEnd > entryStart) {
                        // new event is before existing one, and they should be
                        // merged
                        eventsTogether.remove(entryStart);
                        eventsTogether.put(newStart, entryEnd);
                        merge = false;
                    } else if (newEnd == entryEnd && newStart == entryStart) {
                        // are the same
                    }else if (newEnd > entryEnd && newStart >= entryStart && newStart < entryEnd) {
                        // new event starts in existing and is longer
                        eventsTogether.put(entryStart, newEnd);
                        merge = false;
                    } else if (newEnd > entryEnd && newStart < entryStart) {
                        // new event starts before and ends after
                        eventsTogether.remove(entryStart);
                        eventsTogether.put(newStart, newEnd);
                        merge = false;
                    } else if (newEnd < entryEnd && newEnd > entryStart
                            && newStart > entryStart && newStart < newEnd) {
                        // do nothing as new event is inside existing one
                        merge = false;
                    }
                }
            } catch (Exception e) {
                Log.d("24Hours", "STRANGE EXCEPTION");//TODO or love it
                //e.printStackTrace();
            }
            if (merge) {
                eventsTogether.put(newStart, newEnd);
            }
        }
    }
}
