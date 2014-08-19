package com.tajchert.hours.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tajchert.hours.R;

public class ActivityAbout extends Activity{
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        
        Button buttonShare = (Button) this.findViewById(R.id.buttonShare);
		buttonShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent shareIntent = new Intent();
			    shareIntent.setAction(Intent.ACTION_SEND);
			    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "12Hours");
			    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareStart) +" https://play.google.com/store/apps/details?id=com.tajchert.hours");
			    shareIntent.setType("text/plain");
			    startActivity(Intent.createChooser(shareIntent, getString(R.string.shareWhere)));
			}
		});
		Button buttonGoogle = (Button) this.findViewById(R.id.buttonGoogle);
		buttonGoogle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = "https://plus.google.com/u/0/+MichalTajchert/posts";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		
		
		Button buttonRate = (Button) this.findViewById(R.id.buttonRate);
		buttonRate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ActivityAbout.this.getPackageName().toString())));
			}
		});
		Button buttonContact = (Button) this.findViewById(R.id.buttonContact);
		buttonContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent emailIntent = new Intent(Intent.ACTION_SEND);

	    		emailIntent.setType("text/plain");
	    		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"thetajchert@gmail.com"});
	    		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "12Hours");
	    		emailIntent.putExtra(Intent.EXTRA_TEXT, "");

	    		startActivity(Intent.createChooser(emailIntent, ActivityAbout.this.getResources().getString(R.string.tab_extras_select_one_title)));
			}
		});

        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            try {
                getActionBar().setDisplayHomeAsUpEnabled(true);
            } catch (NullPointerException e) {
                Log.d("com.tajchert.hours.ui.ActivityAbout", "actionBar NullPointerException: " + e.getMessage());
            }

        }
    }

}
