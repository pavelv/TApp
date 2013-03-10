package com.pavelv.touchapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.splash);
		
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo == null) {
			Toast.makeText(Splash.this, "No Internet Connection Available", Toast.LENGTH_LONG).show();
		}

		// new NewsBackground().execute();
		Thread timer = new Thread() {
			@Override
			public void run() {
				try {
					sleep(1000);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				} finally {
					Intent i = new Intent("com.pavelv.touchapp.TOUCH");
					startActivity(i);
				}
			}
		};
		timer.start();
	}

}
