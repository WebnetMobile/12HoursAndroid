package com.tajchert.hours.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.melnykov.fab.FloatingActionButton;
import com.tajchert.hours.R;
import com.tajchert.hours.lists.PickCalendars;
import com.tajchert.hours.lists.WidgetListRecyclerAdapter;
import com.tajchert.hours.widgets.WidgetInstance;
import com.tajchert.hours.widgets.WidgetListManager;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private RecyclerView widgetList;
    private WidgetListRecyclerAdapter adapter;
    private ArrayList<WidgetInstance> widgetInstances;
    private SharedPreferences prefs;
    private ShowcaseView showcaseView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        widgetList = (RecyclerView) findViewById(R.id.widgetList);
        fab = (FloatingActionButton) findViewById(R.id.normal_plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PickCalendars.class);
                startActivityForResult(i, 1);
                if(showcaseView!= null) {
                    showcaseView.hide();
                }
            }
        });
        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(fab))
                .setContentTitle("Free time finder!")
                .setContentText("Use it to find free time between calendars.")
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme)
                .singleShot(2222)
                .build();
        showcaseView.hideButton();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent intent;
        switch(item.getItemId()){
            case R.id.action_colors:
                intent = new Intent(this, ActivityColorManagment.class);
                startActivity(intent);
                break;
        }
        return false;
    }
}
