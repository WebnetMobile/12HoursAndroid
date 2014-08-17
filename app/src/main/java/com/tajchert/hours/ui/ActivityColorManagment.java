package com.tajchert.hours.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tajchert.hours.ColorManager;
import com.tajchert.hours.R;
import com.tajchert.hours.Tools;
import com.tajchert.hours.WidgetInstance;
import com.tajchert.hours.WidgetListManager;
import com.tajchert.hours.WidgetUpdateService;

import java.util.ArrayList;

public class ActivityColorManagment extends FragmentActivity {

	private ImageButton mNextButton;
	private ImageButton mPrevButton;
	private ViewPager mViewPager;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        MyPagerAdapter mSectionsPagerAdapter;


		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color_managment);
		mSectionsPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mNextButton = (ImageButton) findViewById(R.id.imageViewNextColor);
		mPrevButton = (ImageButton) findViewById(R.id.imageViewPrevColor);
		mPrevButton.setVisibility(View.GONE);
		

		prefs = getSharedPreferences("com.tajchert.hours", MODE_PRIVATE);
		mViewPager.setOffscreenPageLimit(3);
		int listType = prefs.getInt(Tools.COLOR_LIST_TYPE, 0);
		mViewPager.setCurrentItem(listType);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				
				if(position == (mViewPager.getAdapter().getCount()-1)){
					mNextButton.setVisibility(View.GONE);
				}else{
					mNextButton.setVisibility(View.VISIBLE);
				}
				if(position == 0){
					mPrevButton.setVisibility(View.GONE);
				}else{
					mPrevButton.setVisibility(View.VISIBLE);
				}
				
			}
		});
		if(mViewPager.getCurrentItem() == (mViewPager.getAdapter().getCount()-1)){
			mNextButton.setVisibility(View.GONE);
			
		}
		if(mViewPager.getCurrentItem() !=0){
			mPrevButton.setVisibility(View.VISIBLE);
		}
		
		mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mViewPager.getCurrentItem()<(mViewPager.getAdapter().getCount()-1)){
					mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
				}
			}
		});
		mPrevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mViewPager.getCurrentItem()>0){
					mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
				}
			}
		});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
	}
	@Override
	protected void onPause() {
	    super.onPause();
	    new SaveWidgetData().execute("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_color_managment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if( id == R.id.action_done){
			new SaveWidgetData().execute("");
		}
		if(id == android.R.id.home){
			new SaveWidgetData().execute("");
		}
		return super.onOptionsItemSelected(item);
	}

	private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int i) {
        	Bundle bundle = new Bundle();
        	bundle.putInt("colorNumber", i);
        	FragmentColorList fragobj = new FragmentColorList();
        	fragobj.setArguments(bundle);
        	return fragobj;
        }
		@Override
		public int getCount() {
			return 4;
		}
    }
	private class SaveWidgetData extends AsyncTask<String, Void, String> {
		ArrayList<WidgetInstance> widgets;
        @Override
        protected String doInBackground(String... params) {
        	for(WidgetInstance widget: widgets){
        		widget.colorPallete = mViewPager.getCurrentItem();
        		WidgetListManager.updateWidget(widget.id, prefs, widget);
        	}
    		
            return "Executed";
        }
        @Override
        protected void onPostExecute(String result) {
        	ActivityColorManagment.this.startService(new Intent(ActivityColorManagment.this, WidgetUpdateService.class));
        	ActivityColorManagment.this.finish();
        	
        }
        @Override
        protected void onPreExecute() {
        	if(mViewPager.getCurrentItem() == 3 && ColorManager.getArray(prefs).length<3){
        		Toast.makeText(getApplicationContext(), getResources().getString(R.string.tab_extras_colors_needed_more), Toast.LENGTH_SHORT).show();
        		this.cancel(true);
        	}
        	if(prefs == null){
        		prefs = getSharedPreferences("com.tajchert.hours", MODE_PRIVATE);
        	}
        	prefs.edit().putInt(Tools.COLOR_LIST_TYPE, mViewPager.getCurrentItem()).commit();
        	widgets = WidgetListManager.getWidgets(prefs);
        }
    }

}
