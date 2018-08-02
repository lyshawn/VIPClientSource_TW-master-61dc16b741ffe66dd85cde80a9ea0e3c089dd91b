package com.nsplay.vip;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class NPVIPDownLoadImageTask extends AsyncTask<String, Void, Bitmap> {

	ImageView bmImage;

	public NPVIPDownLoadImageTask(ImageView bmImage) {

		this.bmImage = bmImage;
	}

	@Override
	protected Bitmap doInBackground(String... params) {

		Bitmap mIcon11 = null;
		String url = params[0];
		try {
			InputStream in = new URL(url).openStream();
			mIcon11 = BitmapFactory.decodeStream(in);
		} catch (IOException e) {
			Log.e("NPVIP", e.getMessage());
		}
		return mIcon11;
	}

	@Override
	protected void onPostExecute(Bitmap result) {

		bmImage.setImageBitmap(result);
		bmImage.setVisibility(View.VISIBLE);
	}

}
