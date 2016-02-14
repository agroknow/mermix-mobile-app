package com.realestate.utils.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;
import com.realestate.utils.Common;
import com.realestate.utils.ImageBitmapCacheMap;

import java.io.InputStream;
import java.net.URL;

/**
 * Created on 30/11/2015
 * Description:
 */
public class InfoWindowImageDownload extends AsyncTask<String, Void, Bitmap> {

	private ImageView imageView;
	private Marker marker;
	public InfoWindowImageDownload(ImageView imageView, Marker marker){
		this.imageView = imageView;
		this.marker = marker;
	}

	@Override
	protected Bitmap doInBackground(String... strings) {
		String imageUrl = strings[0];
		Bitmap mIcon11 = null;
		ImageBitmapCacheMap imageBitmapCacheMap = new ImageBitmapCacheMap();
		try {
			if (imageUrl != null && !imageUrl.isEmpty()) {
				Common.log("ImageDownloadForView doInBackground request image url:" + imageUrl);
				InputStream in = new URL(imageUrl).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
				imageBitmapCacheMap.addBitmap(imageUrl, mIcon11, -1);
			}
		} catch (Exception e) {
			Common.logError("Exception @ ImageDownload doInBackground:" + e.getMessage());
			e.printStackTrace();
		}
		return mIcon11;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		super.onPostExecute(bitmap);
		if(bitmap != null) {
			this.imageView.setImageBitmap(bitmap);
			if (this.marker.isInfoWindowShown()) {
				//redraw InfoWindow to display bitmap
				this.marker.hideInfoWindow();
				this.marker.showInfoWindow();
			}
		}
	}
}
