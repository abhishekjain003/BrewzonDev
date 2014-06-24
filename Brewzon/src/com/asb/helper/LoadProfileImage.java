package com.asb.helper;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * Background Async task to load user profile picture from url
 * */
public class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {

	Context context;
	ImageView bmImage;

	public LoadProfileImage(Context context, ImageView bmImage) {
		this.context = context;
		this.bmImage = bmImage;
	}

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

	protected void onPostExecute(Bitmap result) {
		try {

			Bitmap bitmap = MethodUtills.getRoundedShape(result);
			bmImage.setImageBitmap(bitmap);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
