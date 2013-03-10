package com.pavelv.touchapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;

public class PhotosItem extends Activity {
	
	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected ImageViewPager pager;
	String url = null;
	int pagerPosition;
	int position;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			   
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		Bundle extras = getIntent().getExtras();
		url = extras.getString("url");
		position = extras.getInt("position");
		url = url.replace("_s.jpg", "_m.jpg");
	    
		setContentView(R.layout.ac_image_pager);			
		
		Touch.hideTabs();
		
		pagerPosition = position;

		options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.icon)
			.cacheOnDisc()
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();
		
		pager = (ImageViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(PhotosList.photos));
		pager.setCurrentItem(pagerPosition);
		pager.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				File sdCardDirectory = Environment.getExternalStorageDirectory();
				File image = new File(sdCardDirectory, "test.png");
			    FileOutputStream outStream;
			    try {

			        outStream = new FileOutputStream(image);

					InputStream in = new java.net.URL(url).openStream();
					Bitmap mIcon11 = BitmapFactory.decodeStream(in);
					mIcon11.compress(Bitmap.CompressFormat.JPEG, 85, outStream);
			        
			        outStream.flush();
			        outStream.close();

			    } catch (FileNotFoundException e) {
			        e.printStackTrace();
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			}
		});
	}
	
	private class ImagePagerAdapter extends PagerAdapter {

		private FlickrPhoto[] images;
		private LayoutInflater inflater;

		ImagePagerAdapter(FlickrPhoto[] images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(View view, final int position) {

			final View imageLayout = inflater.inflate(R.layout.item_pager_image, null);
			final TouchImageView imageView = (TouchImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			final Button saveImage = (Button) findViewById(R.id.button1);
			
			saveImage.setVisibility(View.INVISIBLE);
			
			saveImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				
//				final Handler handler = new Handler();

				new Thread(new Runnable() {
					@Override
					public void run() {
						
						File sdCardDirectory = Environment.getExternalStorageDirectory();
						final File dir = new File (sdCardDirectory.getAbsolutePath() + "/TouchPhotos");
						dir.mkdirs();
						InputStream in;
						Bitmap bm = null;
						
						try {
							
							in = new java.net.URL(images[position].makeURL3()).openStream();
							bm = BitmapFactory.decodeStream(in);
					        
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						File image = new File(dir, System.currentTimeMillis() + ".jpg");
					    FileOutputStream outStream;
						
				        try {
							outStream = new FileOutputStream(image);
							bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
					        outStream.flush();
					        outStream.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				}
			});
			
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					saveImage.setVisibility(View.VISIBLE);
				}
			});

			imageLoader.displayImage(images[position].makeURL2(), imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted() {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(FailReason failReason) {
					String message = null;
					
					switch (failReason) {
						case IO_ERROR:
							message = "I/O Error";
							break;
						case OUT_OF_MEMORY:
							message = "Out of Memory";
							break;
						case UNKNOWN:
							message = "Unknown Error";
							break;
					}
					
					Toast.makeText(PhotosItem.this, message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
					imageView.setImageResource(R.drawable.photos);
					
				}

				@Override
				public void onLoadingComplete(Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			((ImageViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}
	
//	public void onSaveInstanceState(Bundle toSave) {
//		  super.onSaveInstanceState(toSave);
//		  toSave.putString("currentTab", TabHost.TabSpec.get getTabHost());
//		}
	
//	@Override
//	public void onRestoreInstanceState(Bundle savedInstanceState) {
//	  super.onRestoreInstanceState(savedInstanceState);
//	  // Restore UI state from the savedInstanceState.
//	  // This bundle has also been passed to onCreate.
//	  pager.restoreHierarchyState(null);
//	  int position = savedInstanceState.getInt("position");
//	  String url = savedInstanceState.getString("url");
//	}
	
//	private URLPosition collectData() {
//			String new_url = url;
//			int new_position = position;
//		return null;
//	}

//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		
//	    super.onConfigurationChanged(newConfig);
//
//	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//	    	Toast.makeText(this, "ORIENTATION_LANDSCAPE", Toast.LENGTH_SHORT).show();
////	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
////	    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		    setContentView(R.layout.ac_image_pager);
//	    }
//	    else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//	    	Toast.makeText(this, "ORIENTATION_PORTRAIT", Toast.LENGTH_SHORT).show();
////	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
////	    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		    setContentView(R.layout.ac_image_pager);
//	    }
//
//	}
//	
//	@Override
//	public void onBackPressed() {
//		//PhotosGroup.group.back();
//		return;
//	}

}