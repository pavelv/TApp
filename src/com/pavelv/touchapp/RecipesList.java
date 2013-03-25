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

public class RecipesList extends ListActivity {

	public ListAdapter adapter = null;
	
	Button play;
	Button pause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.recipes);

		findViewById(R.id.loader).setVisibility(View.VISIBLE);

		View header = getLayoutInflater().inflate(R.layout.listview_header,
				null);

		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.recipes_header);
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
						.getXML("http://www.touchmusic.org.uk/recipebook/categories.xml");
				Document doc = GetXMLMethods.XMLfromString(xml);

				NodeList children = doc.getElementsByTagName("category");

				for (int i = 0; i < children.getLength(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					Element e = (Element) children.item(i);
					if ("left".compareToIgnoreCase(GetXMLMethods.getValue(e,
							"title").toString()) != 0) {
						if ("conceptual, historical, literary etc"
								.compareToIgnoreCase(GetXMLMethods.getValue(e,
										"title").toString()) == 0) {
							map.put("title", "Conceptual, Historical, Literary");
						} else {
							map.put("title", GetXMLMethods.getValue(e, "title"));
						}
						for (int j = 0; j <= (GetXMLMethods
								.getValue(e, "title").toString().length() - "Vegetarian"
								.length()); j++) {
							if (GetXMLMethods
									.getValue(e, "title")
									.toString()
									.regionMatches(j, "Vegetarian", 0,
											"Vegetarian".length())) {
								map.put("title", "Vegetarian & Vegan");
							}
						}
						for (int j = 0; j <= (GetXMLMethods
								.getValue(e, "title").toString().length() - "Soups"
								.length()); j++) {
							if (GetXMLMethods
									.getValue(e, "title")
									.toString()
									.regionMatches(j, "Soups", 0,
											"Soups".length())) {
								map.put("title", "Soups & Starters");
							}
						}					
						map.put("id", GetXMLMethods.getValue(e, "id"));
						mylist.add(map);
					}
				}

				handler.post(new Runnable() {
					@Override
					public void run() {

						findViewById(R.id.loader).setVisibility(View.GONE);
						adapter = new SimpleAdapter(RecipesList.this, mylist,
								R.layout.list_layout, new String[] { "title" },
								new int[] { R.id.title });
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

		Intent i = new Intent(this, RecipesSecondList.class);
		ListView lv = getListView();
		HashMap<String, String> o = (HashMap<String, String>) lv
				.getItemAtPosition(position);
		String title = o.get("title").toLowerCase();
		if (title.compareToIgnoreCase("baby food") == 0) {
			title = "babyfood";
		} else if (title.compareToIgnoreCase("conceptual, historical, literary") == 0) {
			title = "conceptual";
		} else if (title.compareToIgnoreCase("prison food") == 0) {
			title = "prisonfood";
		} else if (title.compareToIgnoreCase("soups & starters") == 0) {
			title = "soupsandstarters";
		} else if (title.compareToIgnoreCase("vegetarian & vegan") == 0) {
			title = "vegetarianandvegan";
		}

		i.putExtra("title", title);

		View view = RecipesGroup.group
				.getLocalActivityManager()
				.startActivity("RecipesSecondList",
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();

		RecipesGroup.group.replaceView(view);
	}

}