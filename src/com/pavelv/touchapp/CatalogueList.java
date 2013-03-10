package com.pavelv.touchapp;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

@SuppressLint({ "NewApi", "NewApi" })
public class CatalogueList extends ListActivity {

	public ListAdapter adapter = null;
	
	Button play;
	Button pause;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.catalogue);

		findViewById(R.id.loader).setVisibility(View.VISIBLE);

		View header = getLayoutInflater().inflate(R.layout.listview_header,
				null);

		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.catalogue_header);
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
						.getXML("http://electric-window-7475.herokuapp.com/releases/publisher/Touch.xml");
				Document doc = GetXMLMethods.XMLfromString(xml);

				NodeList children = doc.getElementsByTagName("release");

				for (int i = 0; i < children.getLength(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					Element e = (Element) children.item(i);
					map.put("artist", GetXMLMethods.getValue(e, "artist"));
					map.put("title", GetXMLMethods.getValue(e, "title"));
					map.put("release_date",
							GetXMLMethods.getValue(e, "release_date"));
					map.put("catalogue_number",
							GetXMLMethods.getValue(e, "catalogue_number"));
					map.put("cover_art_url",
							GetXMLMethods.getValue(e, "cover_art_url"));
					map.put("track_listing",
							GetXMLMethods.getValue(e, "track_listing"));
					map.put("mp3_sample_url",
							GetXMLMethods.getValue(e, "mp3_sample_url"));
					map.put("description",
							GetXMLMethods.getValue(e, "description"));
					map.put("release_url",
							GetXMLMethods.getValue(e, "release_url"));
					mylist.add(map);
				}

				handler.post(new Runnable() {
					@Override
					public void run() {

						findViewById(R.id.loader).setVisibility(View.GONE);

						adapter = new SimpleAdapter(CatalogueList.this, mylist,
								R.layout.list_layout, new String[] { "artist",
										"title" }, new int[] { R.id.title,
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

		Intent i = new Intent(this, CatalogueItem.class);
		ListView lv = getListView();
		HashMap<String, String> o = (HashMap<String, String>) lv
				.getItemAtPosition(position);

		i.putExtra("artist", o.get("artist"));
		i.putExtra("title", o.get("title"));
		i.putExtra("release_date", o.get("release_date"));
		i.putExtra("catalogue_number", o.get("catalogue_number"));
		i.putExtra("cover_art_url", o.get("cover_art_url"));
		i.putExtra("track_listing", o.get("track_listing"));
		i.putExtra("description", o.get("description"));
		i.putExtra("mp3_sample_url", o.get("mp3_sample_url"));
		i.putExtra("release_url", o.get("release_url"));

		View view = CatalogueGroup.group
				.getLocalActivityManager()
				.startActivity("CatalogueItem",
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();

		CatalogueGroup.group.replaceView(view);
	}

	@Override
	public void onBackPressed() {

		return;
	}
}