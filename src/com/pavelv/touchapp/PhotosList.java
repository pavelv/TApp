package com.pavelv.touchapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TabWidget;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.OnScrollSmartOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PhotosList extends Activity {

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	OnScrollSmartOptions smartOptions;

	String API_KEY = "dcb74491ec5cbe64deb98b18df1125a9";
	static public FlickrPhoto[] photos;
	
	Button play;
	Button pause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.photos);

		findViewById(R.id.loader).setVisibility(View.VISIBLE);
		findViewById(R.id.ListView).setVisibility(View.GONE);

		Touch.showTabs();
		
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(getBaseContext()));

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.photos_icon)
				.showImageForEmptyUri(R.drawable.item).cacheInMemory()
				.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
		smartOptions = new OnScrollSmartOptions(options);

		final Handler handler = new Handler();

		new Thread(new Runnable() {
			@Override
			public void run() {

				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(
						"http://api.flickr.com/services/rest/?method=flickr.photosets.getPhotos&format=json&photoset_id=72157627750718372&api_key=dcb74491ec5cbe64deb98b18df1125a9&nojsoncallback=1");

				HttpResponse response;

				try {
					response = httpclient.execute(httpget);
					HttpEntity entity = response.getEntity();
					InputStream inputstream = entity.getContent();
					if (entity != null) {

						BufferedReader bufferedreader = new BufferedReader(
								new InputStreamReader(inputstream));
						StringBuilder stringbuilder = new StringBuilder();
						String currentline = null;
						try {
							while ((currentline = bufferedreader.readLine()) != null) {
								stringbuilder.append(currentline + "\n");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						String result = stringbuilder.toString();

						JSONObject thedata = new JSONObject(result);
						JSONObject thephotosdata = thedata
								.getJSONObject("photoset");
						JSONArray thephotodata = thephotosdata
								.getJSONArray("photo");

						photos = new FlickrPhoto[thephotodata.length()];
						for (int i = 0; i < thephotodata.length(); i++) {
							JSONObject photodata = thephotodata
									.getJSONObject(i);
							photos[i] = new FlickrPhoto(photodata
									.getString("id"), photodata
									.getString("secret"), photodata
									.getString("server"), photodata
									.getString("farm"));
						}
						inputstream.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				handler.post(new Runnable() {
					@Override
					public void run() {

						findViewById(R.id.loader).setVisibility(View.GONE);
						findViewById(R.id.ListView).setVisibility(View.VISIBLE);

						GridView gridView = (GridView) findViewById(R.id.ListView);
//						gridView.setVerticalSpacing(0);
//						gridView.setHorizontalSpacing(-20);
						gridView.setAdapter(new ImageAdapter(PhotosList.this,
								photos));
						gridView.setOnItemClickListener(itemClickListener);
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

	public class ImageAdapter extends BaseAdapter {

		private Context context;
		private FlickrPhoto[] photos;

		public ImageAdapter(Context _context, FlickrPhoto[] _items) {
			context = _context;
			photos = _items;
		}

		@Override
		public int getCount() {
			return photos.length;
		}

		@Override
		public Object getItem(int position) {
			return photos[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ImageView imageView;
			if (convertView == null) {
				imageView = (ImageView) getLayoutInflater().inflate(
						R.layout.row, parent, false).findViewById(
						R.id.ImageView);
			} else {
				imageView = (ImageView) convertView;
			}

			imageLoader.displayImage(photos[position].makeURL(), imageView,
					smartOptions.getOptions());

			return imageView;
		}
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			Intent i = new Intent(PhotosList.this, PhotosItem.class);
			i.putExtra("url", photos[position].makeURL());
			i.putExtra("position", position);
//			View view = PhotosGroup.group
//					.getLocalActivityManager()
//					.startActivity("PhotosItem",
//							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//					.getDecorView();
//
//			PhotosGroup.group.replaceView(view);

			startActivity(i);
			
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
		
		Touch.showTabs();
	}

//	@Override
//	public void onBackPressed() {
//
//		return;
//	}
}