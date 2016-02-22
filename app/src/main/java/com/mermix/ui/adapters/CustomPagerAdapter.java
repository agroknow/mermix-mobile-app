package com.mermix.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mermix.R;
import com.mermix.model.Equipment;
import com.mermix.utils.Common;
import com.mermix.utils.ImageBitmapCacheMap;
import com.mermix.utils.ImageUtils;

import java.io.IOException;
import java.util.Arrays;

public class CustomPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    String[] mResources ;

    public CustomPagerAdapter(Context context, Equipment equipment) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResources = equipment.getImage();
        Common.log(Arrays.toString(mResources));
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        if(Common.hasUrlFormat(mResources[position]))
            imageView.setImageBitmap(new ImageBitmapCacheMap().getBitmap(mResources[position]));
        else{
            try {
                imageView.setImageBitmap(ImageUtils.configureBitmapSamplingRotation(mResources[position]));
            } catch (IOException e) {
                //e.printStackTrace();
                Common.logError("IOException @ EquipmentDetailActivity.onCreate:" + e.getMessage());
            }
        }
        //imageView.setImageResource(mResources[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}