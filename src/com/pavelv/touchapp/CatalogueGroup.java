package com.pavelv.touchapp;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class CatalogueGroup extends ActivityGroup {

	public static CatalogueGroup group;

	private ArrayList<View> history;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.history = new ArrayList<View>();
		group = this;

		// Start the root activity within the group and get its view
		View view = getLocalActivityManager().startActivity(
				"CatalogueList",
				new Intent(this, CatalogueList.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();

		// Replace the view of this ActivityGroup
		replaceView(view);

	}

	public void replaceView(View v) {
		// Adds the old one to history
		history.add(v);
		// Changes this Groups View to the new View
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
		CatalogueGroup.group.back();
		return;
	}

}
