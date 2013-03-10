package com.pavelv.touchapp;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//import com.pavelv.tapp.ParseXMLNews;
//import com.pavelv.tapp.ParseXMLNewsMethods;
//import com.pavelv.tapp.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class NewsList extends ListActivity {

	public ListAdapter adapter = null;
	
	Button play;
	Button pause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.news);

		findViewById(R.id.loader).setVisibility(View.VISIBLE);

		View header = getLayoutInflater().inflate(R.layout.listview_header,
				null);

		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.news_header);
		header.findViewById(R.id.header).setBackgroundDrawable(drawable);

		final ListView listView = getListView();
		listView.addHeaderView(header);
		listView.setAdapter(adapter);

		final Handler handler = new Handler();

		new Thread(new Runnable() {
			@Override
			public void run() {

				final ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
				String xml = GetXMLMethods
						.getXML("http://www.touchmusic.org.uk/iphone.xml");
				final Document doc = GetXMLMethods.XMLfromString(xml);

				NodeList children = doc.getElementsByTagName("item");

				for (int i = 0; i < children.getLength(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					Element e = (Element) children.item(i);
					map.put("title", GetXMLMethods.getValue(e, "title"));
					map.put("pubDate", GetXMLMethods.getValue(e, "pubDate"));
					map.put("description",
							GetXMLMethods.getValue(e, "description"));
					mylist.add(map);
				}

				handler.post(new Runnable() {
					@Override
					public void run() {

						findViewById(R.id.loader).setVisibility(View.GONE);
						adapter = new SimpleAdapter(NewsList.this, mylist,
								R.layout.list_layout, new String[] { "title",
										"pubDate" }, new int[] { R.id.title,
										R.id.subtitle });

						listView.setAdapter(adapter);
					}
				});
			}
		}).start();
		
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent i = new Intent(this, NewsItem.class);
		ListView lv = getListView();
		HashMap<String, String> o = (HashMap<String, String>) lv
				.getItemAtPosition(position);

		i.putExtra("title", o.get("title"));
		i.putExtra("subtitle", o.get("pubDate"));
		i.putExtra("description", o.get("description"));

		View view = NewsGroup.group
				.getLocalActivityManager()
				.startActivity("NewsItem",
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();

		NewsGroup.group.replaceView(view);
	}

	@Override
	public void onBackPressed() {

		return;
	}
}
