package com.pavelv.touchapp;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ListActivity;
import android.content.Intent;
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

public class RecipesSecondList extends ListActivity {
	
	public ListAdapter adapter = null;
	
	Button play;
	Button pause;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.recipes_2);

		Bundle extras = getIntent().getExtras();
		String title = extras.getString("title");

		if (title == "conceptual, historical, literary etc") {
			title = "conceptual";
		} else if (title == "prison food") {
			title = "prisonfood";
		} else if (title == "soups & starters") {
			title = "soupsandstarters";
		} else if (title == "vegetarian & vegan") {
			title = "vegetarianandvegan";
		}
		
		findViewById(R.id.loader).setVisibility(View.VISIBLE);
		
		View header = getLayoutInflater().inflate(R.layout.listview_header,
				null);

		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.recipes_header);
		header.findViewById(R.id.header).setBackgroundDrawable(drawable);

		final ListView listView = getListView();
		listView.addHeaderView(header);

		listView.setAdapter(adapter);

		final String xml = ("http://www.touchmusic.org.uk/recipebook/" + title + ".xml");

		final Handler handler = new Handler();

		new Thread(new Runnable() {
			@Override
			public void run() {

				final ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

				Document doc = GetXMLMethods.XMLfromString(GetXMLMethods.getXML(xml));

				NodeList children = doc.getElementsByTagName("item");

				for (int i = 0; i < children.getLength(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					Element e = (Element) children.item(i);
					map.put("title", GetXMLMethods.getValue(e, "title"));
					map.put("excerpt", GetXMLMethods.getValue(e, "excerpt"));
					map.put("description",
							GetXMLMethods.getValue(e, "description"));
					mylist.add(map);
				}

				handler.post(new Runnable() {
					@Override
					public void run() {

						findViewById(R.id.loader).setVisibility(View.GONE);
						adapter = new SimpleAdapter(RecipesSecondList.this,
								mylist, R.layout.list_layout, new String[] {
										"title", "excerpt" }, new int[] {
										R.id.title, R.id.subtitle });
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
		// TODO Auto-generated method stub
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

		Intent i = new Intent(this, RecipesItem.class);
		ListView lv = getListView();
		HashMap<String, String> o = (HashMap<String, String>) lv
				.getItemAtPosition(position);

		i.putExtra("title", o.get("title"));
		i.putExtra("excerpt", o.get("excerpt"));
		i.putExtra("description", o.get("description"));

		View view = RecipesGroup.group
				.getLocalActivityManager()
				.startActivity("RecipesItem",
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();

		RecipesGroup.group.replaceView(view);
	}

	@Override
	public void onBackPressed() {
		RecipesGroup.group.back();
		return;
	}

}