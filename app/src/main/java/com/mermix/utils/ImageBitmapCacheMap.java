package com.mermix.utils;

import android.graphics.Bitmap;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by teo on 5/9/2015.
 *
 * key value structure for holding image bitmap and its corresponding uri
 * limit image bitmaps in structure due to OutOfMemory app crash
 * Once cache max available size is reached, the most distant uri(regarding position in listView)
 * is removed from cache. Also the uri that will be removed from tha cache must not belong to
 * the same nid with the lastUriAccessed.
 *
 */
public class ImageBitmapCacheMap {

	private static HashMap<String, ImageBitmapCacheItem> imageCacheMap = null;
	private static final int imageCacheMaxSize = 10;
	private static String lastUriAccessed = "";

	public ImageBitmapCacheMap() {
		if(imageCacheMap == null){
			imageCacheMap = new HashMap<>();
		}
	}

	public void addBitmap(String uri, Bitmap bitmap, int viewPos, int nid){
		if(!imageCacheMap.containsKey(uri)){
			if(imageCacheMap.size() >= imageCacheMaxSize){
				String uri2Remove = getMostDistant(lastUriAccessed);
				if(uri2Remove.isEmpty())
					uri2Remove = getLRU(lastUriAccessed);

				if(!uri2Remove.isEmpty()) {
					imageCacheMap.remove(uri2Remove);
					Common.log("ImageBitmapCacheMap addBitmap REMOVE " + Common.getFileNameFromUri(uri2Remove));
				}
				else
					Common.logError("ImageBitmapCacheMap is full but NO distant OR LRU item detected");
			}
			imageCacheMap.put(uri, new ImageBitmapCacheItem(bitmap, viewPos, nid));
			lastUriAccessed = uri;
			Common.log("ImageBitmapCacheMap addBitmap ADD " + Common.getFileNameFromUri(uri));
		}
	}

	public Bitmap getBitmap(String uri){
		Bitmap bitmap = null;
		if(imageCacheMap.containsKey(uri)){
			bitmap = imageCacheMap.get(uri).getBitmap();
			lastUriAccessed = uri;
			imageCacheMap.get(uri).updateAccessedTime();
			Common.log("ImageBitmapCacheMap getBitmap RETRIEVED " + Common.getFileNameFromUri(uri));
		}
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
	 * as long as it has not the same nid with provided uri (lastUriAccessed)
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
			if(Math.abs(item.getViewPos() - uriPos) > Math.abs(uriDistantPos - uriPos) && imageCacheMap.get(uri).getNid() != item.getNid()) {
				uriDistantPos = item.getViewPos();
				mostDistantUri = tempUri;
			}
		}
		if(!mostDistantUri.isEmpty())
			Common.log("ImageBitmapCacheMap MOST distant from " + Integer.toString(uriPos) +
					"(nid:" + Integer.toString(imageCacheMap.get(uri).getNid()) + ") is " +
					Integer.toString(uriDistantPos) +
					"(nid:" + Integer.toString(imageCacheMap.get(mostDistantUri).getNid())+")"+
					"");
		return mostDistantUri;
	}

	/**
	 * get LRU bitmap from the cache
	 * as long as it has not the same nid with provided uri (lastUriAccessed)
	 * @return
	 */
	public String getLRU(String uri){
		String lruUri = uri;
		Iterator it = imageCacheMap.entrySet().iterator();
		HashMap.Entry pair;
		ImageBitmapCacheItem item;
		String tempUri;
		while (it.hasNext()) {
			pair = (HashMap.Entry)it.next();
			item = (ImageBitmapCacheItem) pair.getValue();
			tempUri = (String) pair.getKey();
			if(item.getAccessedTime().compareTo(imageCacheMap.get(lruUri).getAccessedTime()) < 0 && imageCacheMap.get(lruUri).getNid() != item.getNid())
			//item's timestamp(tempUri) is before lruUri
				lruUri = tempUri;
		}
		if(!lruUri.isEmpty())
			Common.log("ImageBitmapCacheMap LRU is "+Common.getFileNameFromUri(lruUri)+
					"(nid:"+Integer.toString(imageCacheMap.get(lruUri).getNid())+")"+
					"");
		return lruUri;
	}

	public class ImageBitmapCacheItem {
		private Bitmap bitmap;
		private int viewPos;
		private int nid;
		private Timestamp accessedTime;

		public ImageBitmapCacheItem(Bitmap bitmap, int viewPos, int nid) {
			this.bitmap = bitmap;
			this.viewPos = viewPos;
			this.nid = nid;
			this.accessedTime = Common.getCurrentTimestamp();
		}

		public Bitmap getBitmap() { return bitmap; }

		public int getViewPos() {
			return viewPos;
		}

		public int getNid() {
			return nid;
		}

		public Timestamp getAccessedTime() {
			return accessedTime;
		}

		public void updateAccessedTime() {
			this.accessedTime = Common.getCurrentTimestamp();
		}
	}

}
