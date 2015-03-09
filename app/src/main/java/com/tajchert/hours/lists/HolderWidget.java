package com.tajchert.hours.lists;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.ImageView;
import android.widget.TextView;

import com.tajchert.hours.R;
import com.tajchert.hours.ui.ActivityWidgetSettings;


public class HolderWidget extends RecyclerView.ViewHolder {
    protected TextView calendars;
    protected ImageView clockBacground;
    protected ImageView underFlow;
    protected ImageView overFlow;
    protected Context context;
    protected int widgetId;

    public HolderWidget(View v) {
        super(v);
        calendars =  (TextView) v.findViewById(R.id.labelCalNumber);
        clockBacground = (ImageView) v.findViewById(R.id.imageViewClockBackground);
        underFlow = (ImageView)  v.findViewById(R.id.imageViewClockUnderflow);
        overFlow = (ImageView)  v.findViewById(R.id.imageViewClockOverflow);
        final AnalogClock analogClock = (AnalogClock)  v.findViewById(R.id.analogClockActivity);

        CardView cardView = (CardView) v.findViewById(R.id.card_view_widget);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP){
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(((Activity) context),
                            Pair.create(((View)underFlow), "clockBackground"),
                            Pair.create(((View)analogClock), "clockAnalog"),
                            Pair.create(((View)overFlow), "clockForeground"));
                    Intent intent = new Intent(context, ActivityWidgetSettings.class);

                    Bundle b = new Bundle();
                    b.putString("widgetID", widgetId+""); //Your id
                    intent.putExtras(b);
                    context.startActivity(intent, options.toBundle());
                } else{
                    Intent intent = new Intent(context, ActivityWidgetSettings.class);
                    Bundle b = new Bundle();
                    b.putString("widgetID", widgetId+""); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                    context.startActivity(intent);
                }
            }
        });
    }
}
