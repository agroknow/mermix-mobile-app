package com.mermix.utils;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by teo on 5/9/2015.
 *
 * key value structure for holding image bitmap and its corresponding uri
 * limit image bitmaps in structure due to OutOfMemory app crash
 * Once cache max available size is reached the most distant uri(regarding position in listView)
 * is removed from cache.
 */
public class ImageBitmapCacheMap {

	private static HashMap<String, ImageBitmapCacheItem> imageCacheMap = null;
	private static final int imageCacheMaxSize = 30;
	private static String lastUriAccessed = "";

	public ImageBitmapCacheMap() {
		if(imageCacheMap == null){
			imageCacheMap = new HashMap<>();
		}
	}

	public void addBitmap(String uri, Bitmap bitmap, int viewPos){
		if(!imageCacheMap.containsKey(uri)){
			if(imageCacheMap.size() == imageCacheMaxSize){
				String mostDistantUri = getMostDistant(lastUriAccessed);
				if(!mostDistantUri.isEmpty()) {
					imageCacheMap.remove(mostDistantUri);
					Common.log("ImageBitmapCacheMap addBitmap REMOVE " + Common.getFileNameFromUri(mostDistantUri));
				}
				else
					Common.logError("ImageBitmapCacheMap is full but NO distant item detected");
			}
			imageCacheMap.put(uri, new ImageBitmapCacheItem(bitmap, viewPos));
			lastUriAccessed = uri;
			Common.log("ImageBitmapCacheMap addBitmap ADD " + Common.getFileNameFromUri(uri));
		}
		else
			Common.log("ImageBitmapCacheMap addBitmap ALREADY EXISTS " + Common.getFileNameFromUri(uri));
	}

	public Bitmap getBitmap(String uri){
		Bitmap bitmap = null;
		if(imageCacheMap.containsKey(uri)){
			bitmap = imageCacheMap.get(uri).getBitmap();
			lastUriAccessed = uri;
			Common.log("ImageBitmapCacheMap getBitmap RETRIEVED " + Common.getFileNameFromUri(uri));
		}
		else
			Common.log("ImageBitmapCacheMap getBitmap NOT FOUND " + Common.getFileNameFromUri(uri));
		return bitmap;
	}

	public void debug(){
		Common.log("ImageBitmapCacheMap debug size:"+Integer.toString(imageCacheMap.size()));
		Iterator it = imageCacheMap.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry)it.next();
			Common.log("ImageBitmapCacheMap debug key:" + pair.getKey());
		}
	}

	/**
	 * get from cache uri of bitmap that is the most distant (in ListView) from provided one
	 * @param uri
	 */
	public String getMostDistant(String uri){
		String mostDistantUri = "";
		int uriPos = imageCacheMap.get(uri).getViewPos();
		int uriDistantPos = uriPos;

		Iterator it = imageCacheMap.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry)it.next();
			ImageBitmapCacheItem item = (ImageBitmapCacheItem) pair.getValue();
			String tempUri = (String) pair.getKey();
			if(Math.abs(item.getViewPos() - uriPos) > Math.abs(uriDistantPos - uriPos)) {
				uriDistantPos = item.getViewPos();
				mostDistantUri = tempUri;
			}
		}
		Common.log("ImageBitmapCacheMap most DISTANT from " + Integer.toString(uriPos) + " is " + Integer.toString(uriDistantPos));
		return mostDistantUri;
	}

	public class ImageBitmapCacheItem {
		private Bitmap bitmap;
		private int viewPos;

		public ImageBitmapCacheItem(Bitmap bitmap, int viewPos) {
			this.bitmap = bitmap;
			this.viewPos = viewPos;
		}

		public Bitmap getBitmap() {
			return bitmap;
		}

		public int getViewPos() {
			return viewPos;
		}
	}

}
