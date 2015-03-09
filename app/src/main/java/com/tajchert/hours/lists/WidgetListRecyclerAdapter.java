package com.tajchert.hours.lists;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tajchert.hours.R;
import com.tajchert.hours.Tools;
import com.tajchert.hours.widgets.Widget;
import com.tajchert.hours.widgets.WidgetInstance;

import java.util.ArrayList;

public class WidgetListRecyclerAdapter extends RecyclerView.Adapter<HolderWidget> {
    public ArrayList<WidgetInstance> data = new ArrayList<WidgetInstance>();
    private Context context;

    public WidgetListRecyclerAdapter(ArrayList<WidgetInstance> widgetInstances, Context context) {
        this.data = widgetInstances;
        this.context = context;
    }

    public void setCommentList(ArrayList<WidgetInstance> widgetInstances) {
        this.data = widgetInstances;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(HolderWidget contactViewHolder, int i) {
        WidgetInstance widget = data.get(i);

        setCalendarNames(contactViewHolder, widget);
        setCalendarThumbnail(contactViewHolder, widget);
        contactViewHolder.context = context;
        contactViewHolder.widgetId = widget.id;
    }

    private void setCalendarThumbnail(HolderWidget contactViewHolder, WidgetInstance widget) {
        Bitmap clockBackground = null;
        if(widget.style == 0){
            clockBackground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hand_dial), 250, 250, false);
            widget.setDimensions(Tools.clock_layouts_dimensions_small[0]);
        }else if(widget.style == 1){
            clockBackground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hand_nodigits_dial), 250, 250, false);
            widget.setDimensions(Tools.clock_layouts_dimensions_small[1]);
        }else if(widget.style == 2){
            clockBackground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.kit_kat_hand_dial), 250, 250, false);
            widget.setDimensions(Tools.clock_layouts_dimensions_small[2]);
        }else if(widget.style == 3){
            clockBackground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hand_whitefull_dial), 250, 250, false);
            widget.setDimensions(Tools.clock_layouts_dimensions_small[3]);
        }
        contactViewHolder.overFlow.setImageBitmap(Widget.addStaticWidget(context, widget));
        contactViewHolder.underFlow.setImageBitmap(clockBackground);
    }

    private void setCalendarNames(HolderWidget contactViewHolder, WidgetInstance widget) {
        if(widget.calendarNames != null){
            String cNames="";
            for(int calNum=0; calNum < widget.calendarNames.length;calNum++){
                cNames += widget.calendarNames[calNum] +", ";
            }
            if( cNames.length() > 2){
                cNames = cNames.substring(0, cNames.length()-2);
                contactViewHolder.calendars.setText(cNames +"");
            }else{
                contactViewHolder.calendars.setText("");
            }
        }else{
            contactViewHolder.calendars.setText("");
        }
    }

    @Override
    public HolderWidget onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.widget_list_card, viewGroup, false);
        return new HolderWidget(itemView);
    }
}
