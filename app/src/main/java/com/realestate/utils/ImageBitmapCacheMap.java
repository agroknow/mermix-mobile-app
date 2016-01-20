package com.realestate.utils;

import android.graphics.Bitmap;

import com.realestate.utils.Common;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by teo on 5/9/2015.
 *
 * key value structure for holding image bitmap and its corresponding uri
 */
public class ImageBitmapCacheMap {

	private static HashMap<String, Bitmap> imageCacheMap = null;

	public ImageBitmapCacheMap() {
		if(imageCacheMap == null){
			imageCacheMap = new HashMap<>();
		}
	}

	public void addBitmap(String uri, Bitmap bitmap){
		if(!imageCacheMap.containsKey(uri)){
			imageCacheMap.put(uri, bitmap);
			Common.log("ImageBitmapCacheMap addBitmap ADDED img:" + Common.getFileNameFromUri(uri));
		}
		else
			Common.log("ImageBitmapCacheMap addBitmap ALREADY EXISTS img:" + Common.getFileNameFromUri(uri));
	}

	public Bitmap getBitmap(String uri){
		Bitmap bitmap = null;
		if(imageCacheMap.containsKey(uri)){
			bitmap = imageCacheMap.get(uri);
			Common.log("ImageBitmapCacheMap getBitmap RETRIEVED img:" + Common.getFileNameFromUri(uri));
		}
		else
			Common.log("ImageBitmapCacheMap getBitmap NOT FOUND img:" + Common.getFileNameFromUri(uri));
		return bitmap;
	}

	public void debug(){
		Common.log("ImageBitmapCacheMap debug size:"+Integer.toString(imageCacheMap.size()));
		Iterator it = imageCacheMap.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry)it.next();
			Common.log("ImageBitmapCacheMap debug key:"+pair.getKey());
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

}
