package com.pavelv.touchapp;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsItem extends Activity {
	
	Button play;
	Button pause;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Bundle extras = getIntent().getExtras();
		String title = extras.getString("title");
		String subtitle = extras.getString("subtitle");
		String description = extras.getString("description");
		
		setContentView(R.layout.news_item);
		
		TextView tv1 = (TextView) findViewById(R.id.textView1);

		tv1.setText(title);

		TextView tv2 = (TextView) findViewById(R.id.textView2);

		tv2.setText(subtitle);

		TextView tv3 = (TextView) findViewById(R.id.textView3);

		tv3.setText(Html.fromHtml(description, imgGetter, null));
		tv3.setMovementMethod(LinkMovementMethod.getInstance());
		
		play = (Button) findViewById(R.id.button1);
		play.setVisibility(View.INVISIBLE);
		pause = (Button) findViewById(R.id.button2);
		pause.setVisibility(View.INVISIBLE);

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

	private Html.ImageGetter imgGetter = new Html.ImageGetter() {

		@Override
		public Drawable getDrawable(String source) {
			URL url = null;
			Drawable bmp = null;
			try {
				url = new URL(source);
				URLConnection connection = url.openConnection();
				InputStream stream = connection.getInputStream();
				bmp = Drawable.createFromStream(stream, "src");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (Throwable t) {
				t.printStackTrace();
			}

			if (bmp != null) {
				bmp.setBounds(0, 0, bmp.getMinimumWidth() / 3,
						bmp.getMinimumHeight() / 3);
			}
			return bmp;
		}
	};

	@Override
	protected void onResume() {

		super.onResume();
		
		if (Stream.getPlayerStatus() == true) {
			play.setVisibility(View.GONE);
			pause.setVisibility(View.VISIBLE);
		}
		
		if (Stream.mpState == 2) {
			pause.setVisibility(View.GONE);
			play.setVisibility(View.VISIBLE);
		}
		
	}

	@Override
	public void onBackPressed() {
		RecipesGroup.group.back();
		return;
	}

}