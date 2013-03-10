package com.pavelv.touchapp;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class RecipesGroup extends ActivityGroup {

	// Keep this in a static variable to make it accessible for all the nested
	// activities, lets them manipulate the view
	public static RecipesGroup group;

	// Need to keep track of the history if you want the back-button to work
	// properly, don't use this if your activities requires a lot of memory
	private ArrayList<View> history;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.history = new ArrayList<View>();
		group = this;

		// Start the root activity within the group and get its view
		View view = getLocalActivityManager().startActivity(
				"RecipesList",
				new Intent(this, RecipesList.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();

		// Replace the view of this ActivityGroup
		replaceView(view);

		// setContentView(view);
	}

	public void replaceView(View v) {
		// Adds the old one to history
		history.add(v);
		// Changes this Groups View to the new View
		setContentView(v);
	}

	public void back() {
		System.out.println(history.size());
		if (history.size() > 0) {
			history.remove(history.size() - 1);
			setContentView(history.get(history.size() - 1));
		} else {
			finish();
		}
		System.out.println("SS " + history.size());
	}

	@Override
	public void onBackPressed() {
		RecipesGroup.group.back();
		return;
	}

}
