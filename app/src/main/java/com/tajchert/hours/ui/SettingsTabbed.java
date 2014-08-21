package com.tajchert.hours.ui;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tajchert.hours.calendar.CalendarContentResolver;
import com.tajchert.hours.calendar.CalendarObject;
import com.tajchert.hours.calendar.Event;
import com.tajchert.hours.R;
import com.tajchert.hours.widgets.WidgetListManager;
import com.tajchert.hours.changelog.ChangeLog;
import com.tajchert.hours.lists.PickCalendars;
import com.tajchert.hours.lists.WidgetsListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SettingsTabbed extends FragmentActivity implements ActionBar.TabListener{
	CollectionPagerAdapter mCollectionPagerAdapter;
	ViewPager mViewPager;

	// UI stuff
	
	private static ListView listView;
	private static TextView addWidgetsInfo;
	private int pagerPosition = 0;
	
	// for spinners
	private static TreeMap<Long, Long> eventsTogether = new TreeMap<Long, Long>();

	// SHARED PREFS
	private static SharedPreferences prefs;

	private static ArrayList<CalendarObject> listCalendars;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());

		final ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ChangeLog cl = new ChangeLog(this);
        if (cl.isFirstRun()) {
            cl.getLogDialog().show();
        }
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mCollectionPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
						pagerPosition = position;
						invalidateOptionsMenu();
					}
				});
		for (int i = 0; i < mCollectionPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mCollectionPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		// CUSTOM STUFF
		prefs = this.getSharedPreferences("com.tajchert.hours", Context.MODE_PRIVATE);
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(listView != null){
			WidgetsListAdapter adapter = new WidgetsListAdapter(WidgetListManager.getWidgets(prefs), this);
			listView.setAdapter(adapter);
			if(listView.getAdapter().getCount() > 0){
				addWidgetsInfo.setVisibility(View.GONE);
			}else{
				addWidgetsInfo.setVisibility(View.VISIBLE);
			}
		}
	}

	// SWIPE VIEWS
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public class CollectionPagerAdapter extends FragmentPagerAdapter {

		final int NUM_ITEMS = 2;

		public CollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new TabFragment();
			Bundle args = new Bundle();
			args.putInt(TabFragment.ARG_OBJECT, i);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String tabLabel = null;
			switch (position) {
			case 0:
				tabLabel = getString(R.string.tab_label_main);
				break;
			case 1:
				tabLabel = getString(R.string.tab_label_style);
				break;
			}
			return tabLabel;
		}
	}
	

	public static class TabFragment extends Fragment {

		public static final String ARG_OBJECT = "object";
		private static int pos;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			Bundle args = getArguments();
			int position = args.getInt(ARG_OBJECT);
			listCalendars = new ArrayList<CalendarObject>();
			listCalendars.clear();
			try {
				listCalendars = CalendarContentResolver.getCalendars(getActivity());
			} catch (Exception e) {
				// No calendar?
			}

			int tabLayout = 0;
			switch (position) {
			case 0:
				tabLayout = R.layout.tab_main;
				break;
			case 1:
				tabLayout = R.layout.tab_list_widgets;
				break;
			}
			pos = tabLayout;
            return inflater.inflate(tabLayout, container, false);
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			viewThings();
		}

		public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        	if (requestCode == 1) {
	        		ClockDrawFreeTime clockSurface = new ClockDrawFreeTime(getView(), getActivity(), prefs, eventsTogether);
					if (resultCode == RESULT_OK) {
						String calendars = data.getStringExtra("calendars");
						String[] calendarsNames = calendars.split("<;;>");
							CalendarContentResolver calRevolver = new CalendarContentResolver(getActivity());
							calRevolver.clear();
							eventsTogether.clear();
							for (int i = 0; i < calendarsNames.length; i++) {
								try {
                                    ArrayList<Event> listEventsFriend = new ArrayList<Event>();
									listEventsFriend = calRevolver.getEventList(getActivity(), Integer.parseInt(calendarsNames[i]), 0, false, false);
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
					if (resultCode == RESULT_CANCELED) {
						Log.d("24Hours", "RESULT_CANCELED");
						clockSurface.drawEmpty();
					}
	            super.onActivityResult(requestCode, resultCode, data);
	        } else {
	        	Log.d("24Hours", "onActivityResult handled by IABUtil.");//TODO most likely remove it
	        }
			
			}
		}
		

		private void viewThings() {
			switch (pos) {
			case R.layout.tab_main:
                List<String> spinCalendars = new ArrayList<String>();
				spinCalendars.clear();
				for (CalendarObject obj : listCalendars) {
					spinCalendars.add(obj.name);
				}
				Button selectCalendarsButton = (Button) getView().findViewById(R.id.selectCalendarsButton);
				selectCalendarsButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CalendarContentResolver calRevolver = new CalendarContentResolver(getActivity());
						calRevolver.clear();
						eventsTogether.clear();
						Intent i = new Intent(getActivity(), PickCalendars.class);
						startActivityForResult(i, 1);
					}
				});

				Button buttonCalendar = (Button) getView().findViewById(R.id.buttonCalendar);
				buttonCalendar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(CalendarContract.CONTENT_URI+ "/time"));
						i.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						try {
							startActivity(i);
						} catch (Exception e) {
							//No calendar to handle it
						}
					}
				});
				break;
			case R.layout.tab_list_widgets:
				listView = (ListView) getActivity().findViewById(R.id.listViewWidgets);
				addWidgetsInfo = (TextView) getActivity().findViewById(R.id.textViewAddWidgets);
				//addWidgetsInfo.setVisibility(View.GONE);
				
				WidgetsListAdapter adapter = new WidgetsListAdapter(WidgetListManager.getWidgets(prefs), getActivity());
				listView.setAdapter(adapter);
				if(listView.getAdapter().getCount() > 0){
					addWidgetsInfo.setVisibility(View.GONE);
				}else{
					addWidgetsInfo.setVisibility(View.VISIBLE);
				}
				
				break;
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem itemColor = menu.findItem(R.id.action_colors);
		switch(pagerPosition){
		case 0:
			itemColor.setVisible(false);
			break;
		case 1:
			itemColor.setVisible(true);
			break;
		}
		return true;
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
		case R.id.action_about:
			intent = new Intent(this, ActivityAbout.class);
			startActivity(intent);
			break;
		case R.id.action_settings:
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+ this.getPackageName().toString())));
			break;
		case R.id.action_bug_report:
			final Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setType("text/plain");
			emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "thetajchert@gmail.com" });
			emailIntent.putExtra(Intent.EXTRA_SUBJECT,"12Hours");
			emailIntent.putExtra(Intent.EXTRA_TEXT, "");

			this.startActivity(Intent.createChooser(emailIntent,this.getResources().getString(R.string.tab_extras_select_one_title)));
			break;
		}

		return false;
	}

}
