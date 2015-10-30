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
            imageCacheMap = new HashMap<String, Bitmap>();
        }
    }

    public void addBitmap(String uri, Bitmap bitmap){
        if(!imageCacheMap.containsKey(uri)){
            imageCacheMap.put(uri, bitmap);
        }
        else{
            Common.log("ImageBitmapCacheMap addBitmap uri " + uri + " already exists in HashMap imageCacheMap");
        }
    }

    public Bitmap getBitmap(String uri){
        Bitmap bitmap = null;
        if(imageCacheMap.containsKey(uri)){
            bitmap = imageCacheMap.get(uri);
        }
        else{
            Common.log("ImageBitmapCacheMap getBitmap uri "+uri+" NOT exists in HashMap imageCacheMap");
        }
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
