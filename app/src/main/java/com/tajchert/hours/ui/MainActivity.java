package com.tajchert.hours.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.tajchert.hours.R;
import com.tajchert.hours.lists.WidgetListRecyclerAdapter;
import com.tajchert.hours.widgets.WidgetInstance;
import com.tajchert.hours.widgets.WidgetListManager;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private RecyclerView widgetList;
    private WidgetListRecyclerAdapter adapter;
    private ArrayList<WidgetInstance> widgetInstances;
    private AddFloatingActionButton fab;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        widgetList = (RecyclerView) findViewById(R.id.widgetList);
        fab = (AddFloatingActionButton) findViewById(R.id.normal_plus);

        widgetList.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        widgetList.setLayoutManager(llm);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(prefs == null){
            prefs = this.getSharedPreferences("com.tajchert.hours", Context.MODE_PRIVATE);
        }

        widgetInstances = WidgetListManager.getWidgets(prefs);
        adapter = new WidgetListRecyclerAdapter(widgetInstances, MainActivity.this);
        widgetList.setAdapter(adapter);
    }
}
