package com.mermix.utils.net;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;
import com.mermix.custom.CustomFragment;
import com.mermix.ui.fragments.EquipmentDetail;
import com.mermix.utils.Common;
import com.mermix.utils.ImageBitmapCacheMap;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

/**
 * Created on 30/11/2015
 * Description:
 */
public class InfoWindowImageDownload extends AsyncTask<String, Void, Bitmap> {

	private ImageView imageView;
	private Marker marker;
	private CustomFragment fragment;

	public InfoWindowImageDownload(ImageView imageView, Marker marker, CustomFragment fragment){
		this.imageView = imageView;
		this.marker = marker;
		this.fragment = fragment;
	}

	@Override
	protected Bitmap doInBackground(String... strings) {
		String imageUrl;
		Bitmap mIcon11 = null;
		ImageBitmapCacheMap imageBitmapCacheMap = new ImageBitmapCacheMap();
		for (int i = 0 ; i < strings.length ; i++) {
			imageUrl = strings[i];
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
		}
		return mIcon11;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		super.onPostExecute(bitmap);
		if(this.fragment != null) {
			try {
				EquipmentDetail detailsFragm = (EquipmentDetail) this.fragment;
				detailsFragm.imagesDownloaded();
			} catch (ClassCastException e) {
				Common.logError("ClassCastException @ EquipmentDetailActivity updateUI:" + e.getMessage());
			}
		}
		if(bitmap != null && this.imageView != null) {
			this.imageView.setImageBitmap(bitmap);
			if (this.marker.isInfoWindowShown()) {
				//redraw InfoWindow to display bitmap
				this.marker.hideInfoWindow();
				this.marker.showInfoWindow();
			}
		}
	}
}