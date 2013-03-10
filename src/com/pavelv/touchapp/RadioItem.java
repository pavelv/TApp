package com.pavelv.touchapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RadioItem extends Activity {
	
	String mp3;
	Button play;
	Button pause;
	TextView tv4 = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Bundle extras = getIntent().getExtras();

		String subtitle = extras.getString("itunes:subtitle");
		String title = extras.getString("title");
		String description = extras.getString("description");
		String link = extras.getString("link");
		String image = link.replace("mp3", "jpg");
		image = image.replace("radio/", "radio/images/");
		
		mp3 = link.replace("touchradio", "touchiphoneradio");
		
		System.out.println(mp3);

		System.out.println(image);

		setContentView(R.layout.radio_item);
		
		play = (Button) findViewById(R.id.button1);
		play.setVisibility(View.INVISIBLE);
		pause = (Button) findViewById(R.id.button2);
		pause.setVisibility(View.INVISIBLE);

		TextView tv1 = (TextView) findViewById(R.id.textView1);

		tv1.setText(subtitle);

		TextView tv2 = (TextView) findViewById(R.id.textView2);

		tv2.setText(title);

		new AsyncCoverDownload(
				(ImageView) findViewById(R.id.imageView1)).execute(image);

		TextView tv3 = (TextView) findViewById(R.id.textView3);
		tv3.setText(description);

		tv4 = (TextView) findViewById(R.id.textView4);
//		tv4.setMovementMethod(LinkMovementMethod.getInstance());
//		link = "<a href=\"" + link
//				+ "\"><big><b>Play</b></big><br /><small>Tap here to listen to the track</small></a>";
		link = "<big><b>Play</b></big><br /><small>Tap here to listen to the track</small>";
		tv4.setText(Html.fromHtml(link));	
		tv4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tv4.setText(Html.fromHtml("<big><b>Streaming</b></big><br /><small>Please wait</small></a>"));
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
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
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