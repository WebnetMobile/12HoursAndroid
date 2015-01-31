package com.tajchert.hours.lists;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tajchert.hours.R;


public class HolderWidget extends RecyclerView.ViewHolder {
    protected TextView calendars;
    protected ImageView underFlow;
    protected ImageView overFlow;

    public HolderWidget(View v) {
        super(v);
        calendars =  (TextView) v.findViewById(R.id.labelCalNumber);
        underFlow = (ImageView)  v.findViewById(R.id.imageViewClockUnderflow);
        overFlow = (ImageView)  v.findViewById(R.id.imageViewClockOverflow);
    }
}
