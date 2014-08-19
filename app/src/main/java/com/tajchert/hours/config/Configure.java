package com.tajchert.hours.config;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import com.tajchert.hours.R;
import com.tajchert.hours.Tools;
import com.tajchert.hours.WidgetInstance;
import com.tajchert.hours.WidgetListManager;
import com.tajchert.hours.WidgetUpdateService;
import com.tajchert.hours.lists.StepPagerStrip;

import java.util.ArrayList;
import java.util.List;

public class Configure extends FragmentActivity {
	private ViewPager mPager;
	private MyPagerAdapter mAdapter;

	private boolean mEditingAfterReview;
	private boolean done = false;
	
	private boolean mConsumePageSelectedEvent;
	private List<Fragment> mCurrentPageSequence;
	private boolean isCal;

	
	private Button mNextButton;
	private Button mPrevButton;
	private StepPagerStrip mStepPagerStrip;
	
	private SharedPreferences prefs;
	private boolean isFirstRun = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
		setContentView(R.layout.activity_config);
		
		initWidget();
		prefs = this.getSharedPreferences("com.tajchert.hours", Context.MODE_PRIVATE);
		isCal = (appInstalledOrNot("com.google.android.calendar") || appInstalledOrNot("com.android.calendar"));
		setFragments();
		
		mAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
		mStepPagerStrip
				.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
					@Override
					public void onPageStripSelected(int position) {
						position = Math.min(mAdapter.getCount() - 1, position);
						if (mPager.getCurrentItem() != position) {
							mPager.setCurrentItem(position);
						}
					}
				});

		mNextButton = (Button) findViewById(R.id.next_button);
		mPrevButton = (Button) findViewById(R.id.prev_button);
		
		
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionOnChange();
				mStepPagerStrip.setCurrentPage(position);

				if (mConsumePageSelectedEvent) {
					mConsumePageSelectedEvent = false;
				}else{
					mStepPagerStrip.setPageCount(mAdapter.getCount());
					mEditingAfterReview = false;
					updateBottomBar();
				}
			}
		});

		mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!isCal){
					try {
		        	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.calendar")));
		        	} catch (android.content.ActivityNotFoundException anfe) {
		        	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.calendar")));
		        	}
					return;
				}
				if (mPager.getCurrentItem() == (mPager.getAdapter().getCount()-1)) {
					//Add widget
					done = true;
					finito();
				} else {
					actionOnChange();
					if (mEditingAfterReview) {
						mPager.setCurrentItem(mAdapter.getCount() - 1);
					} else {
						mPager.setCurrentItem(mPager.getCurrentItem() + 1);
					}
					
				}
			}
		});

		mPrevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!isCal){
					isCal = true;
					setFragments();
					mNextButton.setText(R.string.configure_next);
					mStepPagerStrip.setVisibility(View.VISIBLE);
					mAdapter = new MyPagerAdapter(getSupportFragmentManager());
					mPager = (ViewPager) findViewById(R.id.pager);
					mPager.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
					mPrevButton.setText(R.string.configure_prev);
					mPrevButton.setVisibility(View.INVISIBLE);
					return;
				}
				mPager.setCurrentItem(mPager.getCurrentItem() - 1);
			}
		});
		mStepPagerStrip.setPageCount(mAdapter.getCount());
		updateBottomBar();
		
	}
	@Override
    public void onPause() {
        super.onPause();
        if(!done){
        	prefs = this.getSharedPreferences("com.tajchert.hours", Context.MODE_PRIVATE);
        	WidgetListManager.removeWidget(getID(), prefs);
        }else{
        	//done
        	WidgetInstance widget = WidgetListManager.getWidgetInstance(prefs, getID()+"");
        	if(widget!= null){
        		Log.d(Tools.AWESOME_TAG, "Configure, style is: " + widget.style);
        		WidgetListManager.updateWidget(widget.id, prefs, widget);
        	}
        }
    }
	private void initWidget(){
		WidgetInstance widget = new WidgetInstance();
    	widget.id = getID();
    	widget.calendarColors ="";
    	widget.calendarNames = new String[0];
    	widget.calendars = "";
    	widget.style = 0;
    	prefs = this.getSharedPreferences("com.tajchert.hours", Context.MODE_PRIVATE);
    	WidgetListManager.addWidget(widget, prefs);
	}
	private void finito(){
    	int id = getID();
    	Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
		setResult(RESULT_OK, resultValue);
		prefs.edit().putBoolean(Tools.WIDGET_FIRSTADD, false).apply();
		
		
		Intent serviceIntent = new Intent(Configure.this, WidgetUpdateService.class);
		serviceIntent.putExtra("widgetID", id+"");
		Configure.this.startService(serviceIntent);
    	finish();
	}
	private int getID() {
		final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        int mAppWidgetId = 0;
		if (extras != null) {
            mAppWidgetId  = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Log.d(Tools.AWESOME_TAG, "ID:" + mAppWidgetId);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        return mAppWidgetId;
	}
	private void setFragments() {
		mCurrentPageSequence = new ArrayList<Fragment>();
		if(!isCal){
			mCurrentPageSequence.add(new ConfigWarning());
    	}else{
    		if(prefs.getBoolean(Tools.WIDGET_FIRSTADD, true)){
    			isFirstRun = true;
    			mCurrentPageSequence.add(new ConfigInfo());
    		}
    		mCurrentPageSequence.add(new ConfigSelectCalendars());
    		mCurrentPageSequence.add(new ConfigStyleSelect());
    		mCurrentPageSequence.add(new ConfigReady());
    	}
	}
	
	
	private void actionOnChange(){
		
		if(isFirstRun){
			switch(mPager.getCurrentItem()){
			case 0:
				break;
			case 1:
				break;
			case 2:
				try {
					((ConfigSelectCalendars) mCurrentPageSequence.get(1)).saveThings();
				} catch (Exception e) {
				}
				break;
			case 3:
				try {
					((ConfigStyleSelect) mCurrentPageSequence.get(2)).saveStyle();
				} catch (Exception e) {
				}
				break;
			}
		}else{
			switch(mPager.getCurrentItem()){
			case 0:
				break;
			case 1:
				try {
					((ConfigSelectCalendars) mCurrentPageSequence.get(0)).saveThings();
				} catch (Exception e) {
				}
				break;
			case 2:
				try {
					((ConfigStyleSelect) mCurrentPageSequence.get(1)).saveStyle();
				} catch (Exception e) {
				}
				break;
			}
		}
	}

	private void updateBottomBar() {
		
		int position = mPager.getCurrentItem();
		//Log.d(Tools.AWESOME_TAG, "mPager: " + position);
		//Log.d(Tools.AWESOME_TAG, "size: " + mPager.getAdapter().getCount());
		if (position == (mPager.getAdapter().getCount()-1)) {
			
			mNextButton.setText(R.string.configure_add);
			// mNextButton.setBackgroundResource(R.drawable.finish_background);
			// mNextButton.setTextAppearance(this,
			// R.style.TextAppearanceFinish);
		} else {
			mNextButton.setText(R.string.configure_next);
			mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
			TypedValue v = new TypedValue();
			getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v,
					true);
			mNextButton.setTextAppearance(this, v.resourceId);
			// mNextButton.setEnabled(position != mAdapter.getCutOffPage());
		}
		mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
		if(!isCal){
			mNextButton.setText(R.string.configure_install_cal);
			mPrevButton.setText(R.string.configure_continue_cal);
			mPrevButton.setVisibility(View.VISIBLE);
			mStepPagerStrip.setVisibility(View.GONE);
		}
	}
	private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
               pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
               app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
               app_installed = false;
        }
        return app_installed;
    }
	public class MyPagerAdapter extends FragmentStatePagerAdapter {
		
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment frag;
        	if(!isCal){
        		frag =  mCurrentPageSequence.get(0);
        		return frag;
        	}
        	frag = mCurrentPageSequence.get(i);
        	if(frag == null){
        		return new Fragment();
        	}
        	return frag;
        }

		@Override
		public int getCount() {
			if(!isCal){
				return 1;
    		}
			return mCurrentPageSequence.size();
		}

    }
}