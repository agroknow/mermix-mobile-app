package com.realestate.ui.adapters;

/**
 * Created on 16/08/2015
 * Description:
 * The Class FeedAdapter is the adapter class for Feed ListView.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realestate.R;
import com.realestate.model.Equipment;
import com.realestate.model.common.Body;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.ImageBitmapCacheMap;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedAdapter extends BaseAdapter
{

	private ArrayList<Equipment> adapterEquipmentsList = new ArrayList<>();

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount()
	{
		return adapterEquipmentsList.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Equipment getItem(int position)
	{
		return adapterEquipmentsList.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ListViewHolder listViewHolder;
		Equipment equipment = getItem(position);
		Common.log("FeedAdapter getView pos:" + Integer.toString(position) +
					", img:"+Common.getFileNameFromUri(equipment.getImage()));
		String dbgStr = "";
		if(Constants.devMode)
			dbgStr = " (" + Integer.toString(position) + ": " + Common.getFileNameFromUri(equipment.getImage()) + ")";

		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.feed_item, null);

			listViewHolder = new ListViewHolder();
			listViewHolder.imageView = (ImageView) convertView.findViewById(R.id.img1);
			listViewHolder.lbl1View = (TextView) convertView.findViewById(R.id.lbl1);
			listViewHolder.lbl2View = (TextView) convertView.findViewById(R.id.lbl2);
			convertView.setTag(listViewHolder);
		}
		else{
			listViewHolder = (ListViewHolder) convertView.getTag();
		}

		listViewHolder.imageUrl = equipment.getImage();

		listViewHolder.lbl1View.setText(equipment.getTitle());

		List<Body> bodyList = equipment.getBody();
		if(bodyList != null && bodyList.size() > 0)
			listViewHolder.lbl2View.setText(Html.fromHtml(bodyList.get(0).getValue()) + dbgStr);
		else
			listViewHolder.lbl2View.setText(dbgStr);

		ImageBitmapCacheMap imageBitmapCacheMap = new ImageBitmapCacheMap();
		Bitmap cachedBitmap = imageBitmapCacheMap.getBitmap(listViewHolder.imageUrl);
		if(cachedBitmap == null) {
			listViewHolder.imageView.setImageDrawable(null);
			//listViewHolder.imageView.setImageResource(R.drawable.feed2);
/**
 * IMAGE DOWNLOAD METHODS
 */
//1.
//		img.setImageURI(Uri.parse(equipment.getImage().getUri()));
//2.
//		try {
//			img.setImageBitmap(BitmapFactory.decodeStream(new URL(equipment.getImage().getUri()).openConnection().getInputStream()));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//3.
			if(listViewHolder.imageUrl != null && !listViewHolder.imageUrl.isEmpty())
				new ImageDownload().execute(listViewHolder);
		}
		else {
			listViewHolder.imageView.setImageBitmap(cachedBitmap);
		}

		return convertView;
	}

	public void clear(){
		adapterEquipmentsList.clear();
	}

	public void addItem(Equipment equipment){
		adapterEquipmentsList.add(equipment);
	}

	/**
	 * use of static class, check following links to see why
	 * http://www.itworld.com/article/2705632/development/how-to-make-smooth-scrolling-listviews-in-android.html
	 * http://jmsliu.com/1431/download-images-by-asynctask-in-listview-android-example.html
	 */
	static class ListViewHolder {
		TextView lbl1View;
		TextView lbl2View;
		ImageView imageView;
		Bitmap bitmap;
		String imageUrl;
	}

	private class ImageDownload extends AsyncTask<ListViewHolder, Void, ListViewHolder> {

		@Override
		protected ListViewHolder doInBackground(ListViewHolder... params) {
			ListViewHolder listViewHolder = params[0];
			String imageUrl;
			Bitmap bitmap;
			listViewHolder.bitmap = null;
			ImageBitmapCacheMap imageBitmapCacheMap = new ImageBitmapCacheMap();
			try {
				imageUrl = listViewHolder.imageUrl;
				Common.log("ImageDownload doInBackground REQUESTING img:" + Common.getFileNameFromUri(imageUrl));
				InputStream in = new URL(imageUrl).openStream();
				bitmap = BitmapFactory.decodeStream(in);
				imageBitmapCacheMap.addBitmap(imageUrl, bitmap);
				listViewHolder.bitmap = bitmap;
			} catch (Exception e) {
				Common.logError("Exception @ ImageDownload doInBackground:" + e.getMessage());
				e.printStackTrace();
			}
			return listViewHolder;
		}

		@Override
		protected void onPostExecute(ListViewHolder result) {
			super.onPostExecute(result);
			if (result.bitmap != null && result.imageUrl != null && !result.imageUrl.isEmpty()) {
				Common.log("ImageDownload onPostExecute img:" + Common.getFileNameFromUri(result.imageUrl));
				result.imageView.setImageBitmap(result.bitmap);
			}
		}
	}
}