package com.pavelv.touchapp;

//import android.app.Activity;
//import android.os.Bundle;
//import android.widget.TextView;
//
//public class SecondActivityGroup extends Activity {
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        TextView textview = new TextView(this);
//        textview.setText("This is the Artists tab");
//        setContentView(textview);
//    }
//
//}

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TabWidget;

public class PhotosGroup extends ActivityGroup {

	// Keep this in a static variable to make it accessible for all the nested
	// activities, lets them manipulate the view
	public static PhotosGroup group;

	// Need to keep track of the history if you want the back-button to work
	// properly, don't use this if your activities requires a lot of memory.
	private ArrayList<View> history;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.history = new ArrayList<View>();
		group = this;

		// Start the root activity withing the group and get its view
		View view = getLocalActivityManager().startActivity(
				"PhotosList",
				new Intent(this, PhotosList.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();

		// Replace the view of this ActivityGroup
		replaceView(view);
		
		Integer lastTab = (Integer) getLastNonConfigurationInstance();
		
		   if(lastTab != null) {
			   
		      Touch.tabHost.setCurrentTab(lastTab);
		   }

	}

	public void replaceView(View v) {
		// Adds the old one to history
		history.add(v);
		// Changes this Groups View to the new View.
		setContentView(v);
	}

	public void back() {
		if (history.size() > 0) {
			history.remove(history.size() - 1);
			setContentView(history.get(history.size() - 1));
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		PhotosGroup.group.back();
		Touch.showTabs();
		return;
	}

}