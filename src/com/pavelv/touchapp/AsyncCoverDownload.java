package com.pavelv.touchapp;

import java.io.InputStream;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

@TargetApi(3)
class AsyncCoverDownload extends AsyncTask<String, Void, Bitmap> {

	ImageView bmImage;

	public AsyncCoverDownload(ImageView bmImage) {
		this.bmImage = bmImage;
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		String urldisplay = urls[0];
		Bitmap mIcon11 = null;
		try {
			InputStream in = new java.net.URL(urldisplay).openStream();
			mIcon11 = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		return mIcon11;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		bmImage.setImageBitmap(result);
	}
}