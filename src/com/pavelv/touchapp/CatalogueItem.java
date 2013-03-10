package com.pavelv.touchapp;

import java.io.IOException;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CatalogueItem extends Activity {

	String mp3;
	Button play;
	Button pause;
	TextView tv6 = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Bundle extras = getIntent().getExtras();

		String title = extras.getString("title");
		String artist = extras.getString("artist");
		String release_date = extras.getString("release_date");
		String catalogue_number = extras.getString("catalogue_number");
		String cover = extras.getString("cover_art_url");
		String track_listing = extras.getString("track_listing");
		String description = extras.getString("description");
		String mp3_sample = extras.getString("mp3_sample_url");
		mp3 = mp3_sample;
		String release_url = extras.getString("release_url");
		String released = "Released: " + release_date + " | Catalogue #:"
				+ catalogue_number;

		setContentView(R.layout.catalogue_item);
		
		play = (Button) findViewById(R.id.button1);
		play.setVisibility(View.INVISIBLE);
		pause = (Button) findViewById(R.id.button2);
		pause.setVisibility(View.INVISIBLE);

		TextView tv1 = (TextView) findViewById(R.id.textView1);

		tv1.setText(title);

		TextView tv2 = (TextView) findViewById(R.id.textView2);

		tv2.setText(artist);

		TextView tv3 = (TextView) findViewById(R.id.textView3);

		tv3.setText(released);

		AsyncCoverDownload dit = (AsyncCoverDownload) new AsyncCoverDownload(
				(ImageView) findViewById(R.id.imageView1)).execute(cover);

		TextView tv4 = (TextView) findViewById(R.id.textView4);

		tv4.setText(track_listing);

		TextView tv5 = (TextView) findViewById(R.id.textView5);
		tv5.setText(description);

		tv6 = (TextView) findViewById(R.id.textView6);
//		mp3_sample = "<a href=\"" + mp3_sample
//				+ "\"><big><b>Play</b></big><br /><small>Tap here to listen to the track</small></a>";
		mp3_sample = "<big><b>Play</b></big><br /><small>Tap here to listen to the track</small></a>";
		tv6.setText(Html.fromHtml(mp3_sample));
//		tv6.setMovementMethod(LinkMovementMethod.getInstance());
		tv6.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tv6.setText(Html.fromHtml("<big><b>Streaming</b></big><br /><small>Please wait</small></a>"));
				
				Stream.playSong(mp3);
				
				play.setVisibility(View.VISIBLE);
				
				play.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						play.setVisibility(View.GONE);
						pause.setVisibility(View.VISIBLE);
						Stream.setResume();
					}
				});

				pause.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						pause.setVisibility(View.GONE);
						play.setVisibility(View.VISIBLE);
						Stream.setOnPause();
					}
				});
			}
		});

		TextView tv7 = (TextView) findViewById(R.id.textView7);
		release_url = "<a href=\"" + release_url
				+ "\"><big><b>Buy</b></big><br /><small>Tap here to get more info</small></a>";
		tv7.setText(Html.fromHtml(release_url));
		tv7.setMovementMethod(LinkMovementMethod.getInstance());
		stripUnderlines(tv7);
	}

	@Override
	protected void onResume() {

		super.onResume();
		
		play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				play.setVisibility(View.GONE);
				pause.setVisibility(View.VISIBLE);
				Stream.setResume();
			}
		});

		pause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pause.setVisibility(View.GONE);
				play.setVisibility(View.VISIBLE);
				Stream.setOnPause();
			}
		});
		
		if (Stream.getPlayerStatus() == true) {
			play.setVisibility(View.GONE);
			pause.setVisibility(View.VISIBLE);
		}
		
		if (Stream.mpState == 2) {
			pause.setVisibility(View.GONE);
			play.setVisibility(View.VISIBLE);
		}
	}
	
	private void stripUnderlines(TextView textView) {
	    Spannable s = (Spannable)textView.getText();
	    URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
	    for (URLSpan span: spans) {
	        int start = s.getSpanStart(span);
	        int end = s.getSpanEnd(span);
	        s.removeSpan(span);
	        span = new URLSpanNoUnderline(span.getURL());
	        s.setSpan(span, start, end, 0);
	    }
	    textView.setText(s);
	}	

	@Override
	public void onBackPressed() {
		CatalogueGroup.group.back();
		return;
	}

}
