package com.tajchert.hours.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.melnykov.fab.FloatingActionButton;
import com.tajchert.hours.R;
import com.tajchert.hours.calendar.CalendarContentResolver;
import com.tajchert.hours.calendar.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

public class ActivityFreeTime extends ActionBarActivity {
    private static final String TAG = "FreeTimeActivity";
    private TextView calendarNamesText;
    private ImageView imageView;
    private SharedPreferences prefs;
    private RippleView rippleCalendarButton;
    private static TreeMap<Long, Long> eventsTogether = new TreeMap<Long, Long>();

    private Animation animationFadeIn;
    private Animation animationFadeOut;
    private boolean isFadeOut = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_time);
        calendarNamesText = (TextView) findViewById(R.id.freeTimeCalendarNames);
        imageView = (ImageView) findViewById(R.id.imageViewActivity);
        prefs = this.getSharedPreferences("com.tajchert.hours", Context.MODE_PRIVATE);
        setButtons();
        setAnimations();

        Intent intent = getIntent();
        if(intent != null){
            drawFreeTimeAnimation(intent);
        }
    }

    private void setAnimations() {
        animationFadeIn = new AlphaAnimation(0.0f, 1.0f);
        animationFadeIn.setDuration(150);
        animationFadeIn.setFillEnabled(true);
        animationFadeIn.setFillAfter(true);
        animationFadeOut = new AlphaAnimation(1.0f, 0.0f);
        animationFadeOut.setDuration(150);
        animationFadeOut.setFillEnabled(true);
        animationFadeOut.setFillAfter(true);
    }


    private void setButtons() {
        FloatingActionButton calendarChange = (FloatingActionButton) findViewById(R.id.fab_calendar);
        rippleCalendarButton = (RippleView) findViewById(R.id.buttonCalendar);
        rippleCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(CalendarContract.CONTENT_URI + "/time"));
                i.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    startActivity(i);
                } catch (Exception e) {
                    //No calendar to handle it
                }
            }
        });
        calendarChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFadeOut){
                    imageView.startAnimation(animationFadeOut);
                    isFadeOut = true;
                }
                Intent i = new Intent(ActivityFreeTime.this, ActivityChangeWidgetCalendars.class);
                i.putExtra("forResult", true);
                startActivityForResult(i, 1);
            }
        });
    }

    private void drawFreeTimeAnimation(final Intent data) {
        if(rippleCalendarButton != null){
            rippleCalendarButton.animateRipple(rippleCalendarButton.getWidth(), rippleCalendarButton.getHeight());
        }
        calendarNamesText.setText("");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawFreeTime(data);
            }
        }, 250);
    }

    private void drawFreeTime(Intent data) {
        ClockDrawFreeTime clockSurface = new ClockDrawFreeTime(imageView, ActivityFreeTime.this, prefs, eventsTogether, getResources().getColor(R.color.app_accent_color));
        if (data == null) {
            clockSurface.drawEmpty();
            return;
        }
        String calendars = data.getStringExtra("calendars");
        String[] calendarsNames = calendars.split("<;;>");
        setCalendarNamesText(calendarsNames);

        if (calendars.length() == 0  || calendarsNames.length == 0) {
            clockSurface.drawEmpty();
            return;
        }

        getEvents(clockSurface, calendarsNames);

        if (eventsTogether.size() > 0){
            clockSurface.drawEventsFromTogether();
        } else {
            clockSurface.drawFull();
        }

        imageView.startAnimation(animationFadeIn);
        isFadeOut = false;
    }

    private void setCalendarNamesText(String[] calendarsNames) {
        if(calendarsNames.length > 0 && calendarsNames[0] != null && calendarsNames[0].length() > 0) {
            StringBuilder builder = new StringBuilder();
            for(String s : calendarsNames) {
                builder.append(s);
                builder.append(", ");
            }
            String titles = builder.toString();
            titles = titles.substring(0, titles.length()-2);
            titles += ".";
            calendarNamesText.setText(getResources().getText(R.string.configure_calendarselection_title) +": " + titles);
        }
    }

    private void getEvents(ClockDrawFreeTime clockSurface, String[] calendarsNames) {
        CalendarContentResolver calRevolver = new CalendarContentResolver(ActivityFreeTime.this);
        calRevolver.clear();
        eventsTogether.clear();
        for (String calendarName : calendarsNames) {
            try {
                ArrayList<Event> listEventsFriend = calRevolver.getEventList(ActivityFreeTime.this, Integer.parseInt(calendarName), 0);
                if (listEventsFriend.size() > 0) {
                    for (Event ev : listEventsFriend) {
                        addEventToTogether(ev);
                    }
                }
            } catch (NumberFormatException e) {
                clockSurface.drawEmpty();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            drawFreeTimeAnimation(data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void addEventToTogether(Event ev) {
        Long nowTime = (Calendar.getInstance().getTimeInMillis() / 1000) * 1000;
        Long newStart = (ev.dateStart.getTimeInMillis() / 1000) * 1000;
        Long newEnd = (ev.dateEnd.getTimeInMillis() / 1000) * 1000;
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
