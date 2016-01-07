package com.realestate.ui.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.realestate.R;
import com.realestate.custom.CustomFragment;
import com.realestate.model.EquipmentPostPayload;
import com.realestate.model.SQLiteNode;
import com.realestate.model.common.Address;
import com.realestate.model.common.Body;
import com.realestate.model.common.DrupalListField;
import com.realestate.model.common.Pojo;
import com.realestate.model.sqlite.DrupalNodes;
import com.realestate.ui.DataRetrieve;
import com.realestate.ui.activities.EquipmentDetail;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.MainService;
import com.realestate.utils.net.args.NewEquipmentArgs;
import com.realestate.utils.net.args.UrlArgs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 21/12/2015
 * Description:
 * submit New Equipment to REST API
 * on successful submit, store new equipment's data to SQLite & navigate to activity EquipmentDetail
 *
 * HELP
 * http://www.theappguruz.com/blog/android-take-photo-camera-gallery-code-sample
 * http://stackoverflow.com/a/21754321/3441616
 * http://developer.android.com/reference/android/graphics/BitmapFactory.Options.html
 */
public class NewEquipment extends CustomFragment implements DataRetrieve {
	private EquipmentPostPayload payload = null;
	private Bitmap equipmentPhoto = null;
	private String equipmentPhotoPath = "";

	@Override
	public void updateUI(Pojo apiResponseData) {
		Common.log("NewEquipment updateUI");
		try {
			com.realestate.model.NewEquipment equipment = (com.realestate.model.NewEquipment) apiResponseData;
			int equipmentId = Integer.parseInt(equipment.getId());
			DrupalNodes drupalNodes = new DrupalNodes(getActivity().getApplicationContext());
			Double[] coords = {this.payload.getAddress().getLatitude(), this.payload.getAddress().getLongitude()};
			//store new equipment's data to SQLite
			drupalNodes.insertNode(
					new SQLiteNode(equipmentId,
							this.payload.getTitle(),
							this.payload.getBody().getValue(),
							coords,
							Common.concatString(this.payload.getImage(), Constants.CONCATDELIMETER),
							(float) -1));
			//navigate to activity EquipmentDetail
			Intent i = new Intent(getActivity(), EquipmentDetail.class);
			i.putExtra(Constants.INTENTVARS.EQUIPMENTID, equipmentId);
			startActivity(i);
		}catch (ClassCastException e){
			Common.logError("ClassCastException @ Search updateUI:" + e.getMessage());
		}
	}

	@Override
	public void startRequestService(UrlArgs urlArgs) {
		Common.log("NewEquipment startRequestService");
		NewEquipmentArgs args = (NewEquipmentArgs) urlArgs;
		String queryString = args.getUrlArgs();
		String apiUrl = Constants.APIENDPOINT + Constants.URI.NEWEQUIPMENT + "?" + queryString;
		String pojoClass = Constants.PojoClass.NEWEQUIPMENT;
		Intent i = new Intent(this.getActivity(), MainService.class);
		i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
		i.putExtra(Constants.INTENTVARS.POJOCLASS, pojoClass);
		this.getActivity().startService(i);
	}

	@Override
	public View setTouchNClick(View v) {
		return super.setTouchNClick(v);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Common.log("NewEquipment onClick");
		if (v.getId() == R.id.btnSubmit) {

			String title = "Equipment posted from mobile app";
			String type = "apartment";
			Body body = new Body();
			body.setValue("Attempt to post equipment from mobile app");
			int promote = 0;
			String lang = "el";
			DrupalListField author = new DrupalListField();
			author.setId("59");
			DrupalListField equipmentType = new DrupalListField();
			equipmentType.setId("3");
			DrupalListField contract = new DrupalListField();
			contract.setId("44");
			DrupalListField cultivation = new DrupalListField();
			cultivation.setId("58");
			List<DrupalListField> cultivationsList = new ArrayList<>();
			cultivationsList.add(0, cultivation);
			DrupalListField location = new DrupalListField();
			location.setId("166");
			Address address = new Address();
			address.setLatitude(38.899583425983);
			address.setLongitude(22.148437500000);
			String imagePath = this.equipmentPhotoPath;
			//String imagePath = Common.bitmap2Base64(this.equipmentPhoto);
			//String imagePath = Common.getImageBase64(this.equipmentPhotoPath);
			List<String> imageList = new ArrayList<>();
			imageList.add(0, imagePath);

			this.payload = new EquipmentPostPayload();
			this.payload.setData(title, body, type, promote, lang, author, equipmentType, contract, cultivationsList, location, address, imageList);

			String payloadStr = Common.pojo2Json(this.payload);
			startRequestService(new NewEquipmentArgs(payloadStr));
		}
		else if(v.getId() == R.id.openCamera){
			if (getActivity().getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
				// Open device's camera, start the image capture Intent
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, Constants.ACTIVITYREQUESTCODES.OPENDEVICECAMERA);
			}
			else{
				Common.displayToast("NO camera in device", getActivity().getApplicationContext());
			}
		}
		else if(v.getId() == R.id.selectImageFile) {
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setType("image/*");
			startActivityForResult(
					Intent.createChooser(intent, getResources().getString(R.string.selectImageFile)),
					Constants.ACTIVITYREQUESTCODES.SELECTFILEFROMCOLLECTION);
		}
	}

	@Override
	/**
	 * http://developer.android.com/training/basics/intents/result.html
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Common.log("NewEquipment onActivityResult");

		this.equipmentPhoto = null;
		this.equipmentPhotoPath = "";
		Uri selectedImageUri;
		ImageView imageView = (ImageView) getActivity().findViewById(R.id.img2Submit);
		if(resultCode == getActivity().RESULT_OK){
			/*
			 * Android saves the images in its own database,
			 * and when we want to access that database,
			 * we have to retrieve the content by URI.
			 * Projection specifies how many columns do you want from the given data, http://developer.android.com/reference/android/provider/MediaStore.MediaColumns.html.
			 * You can add all the columns you want in the projection
			 * and data for all the records for those particular columns would be returned in a cursor.
			 */
			//this.equipmentPhoto = (Bitmap) data.getExtras().get("data");	//get image from intent data
			selectedImageUri = data.getData();
			String[] projection = {MediaStore.Images.Media.DATA};
			int columnIndex = -1;
			Cursor cursor = null;	//Cursor to get image uri to display
			if(requestCode == Constants.ACTIVITYREQUESTCODES.OPENDEVICECAMERA){
				cursor = getActivity().getContentResolver().query(selectedImageUri, projection, null, null, null);
				if(cursor != null && cursor.moveToFirst()) {
					do {
						columnIndex = cursor.getColumnIndex(projection[0]);
						this.equipmentPhotoPath = cursor.getString(columnIndex);
					} while(cursor.moveToNext());
					cursor.close();
				}
			}
			if(requestCode == Constants.ACTIVITYREQUESTCODES.SELECTFILEFROMCOLLECTION){
				CursorLoader cursorLoader = new CursorLoader(getActivity(), selectedImageUri, projection, null, null, null);
				cursor = cursorLoader.loadInBackground();
				if(cursor != null && cursor.moveToFirst()) {
					do {
						columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
						this.equipmentPhotoPath = cursor.getString(columnIndex);
					} while(cursor.moveToNext());
					cursor.close();
				}
			}
			if(!this.equipmentPhotoPath.isEmpty()){
				try {
					this.equipmentPhoto = configureBitmapSamplingRotation(this.equipmentPhotoPath);
				} catch (IOException e) {
					//e.printStackTrace();
					Common.logError("IOException @ NewEquipment onActivityResult:" + e.getMessage());
				}
			}
			if(this.equipmentPhoto != null){
				//display image in view
				Common.log("equipment photo size, getByteCount: " + Integer.toString(this.equipmentPhoto.getByteCount()));
				imageView.setImageBitmap(this.equipmentPhoto);
				imageView.invalidate();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Common.log("NewEquipment onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//return super.onCreateView(inflater, container, savedInstanceState);

		Common.log("NewEquipment onCreateView");
		View v = inflater.inflate(R.layout.new_equipment_form, null);

		setTouchNClick(v.findViewById(R.id.btnSubmit));
		setTouchNClick(v.findViewById(R.id.openCamera));
		setTouchNClick(v.findViewById(R.id.selectImageFile));
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		Common.log("NewEquipment onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Common.log("NewEquipment onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Common.log("NewEquipment onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Common.log("NewEquipment onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Common.log("NewEquipment onDestroy");
	}

	private Bitmap configureBitmapSamplingRotation(String selectedImagePath)throws IOException{
		Common.log("NewEquipment configureBitmapSamplingRotation");
		int MAX_HEIGHT = 256;
		int MAX_WIDTH = 256;
		Bitmap bitmap;
		BitmapFactory.Options options = new BitmapFactory.Options();

		// First decode with inJustDecodeBounds=true to check dimensions
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(selectedImagePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
		bitmap = rotateImageIfRequired(bitmap, selectedImagePath);

		return bitmap;
	}

	/**
	 *
	 * @param img					the image bitmap
	 * @param selectedImagePath 	the image path
	 * @return						The resulted Bitmap after manipulation
	 */
	private Bitmap rotateImageIfRequired(Bitmap img, String selectedImagePath) throws IOException {
		Common.log("NewEquipment rotateImageIfRequired");
		ExifInterface ei = new ExifInterface(selectedImagePath);
		int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

		Common.log("orientation: "+Integer.toString(orientation));
		switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				return rotateImage(img, 90);
			case ExifInterface.ORIENTATION_ROTATE_180:
				return rotateImage(img, 180);
			case ExifInterface.ORIENTATION_ROTATE_270:
				return rotateImage(img, 270);
			default:
				return img;
		}
	}

	private static Bitmap rotateImage(Bitmap img, int degree) {
		Common.log("NewEquipment rotateImage");
		Matrix matrix = new Matrix();
		Common.log("degree: "+Integer.toString(degree));
		matrix.postRotate(degree);
		Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
		img.recycle();
		return rotatedImg;
	}

	/**
	 * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
	 * bitmaps using the decode methods from {@link BitmapFactory}. This implementation calculates
	 * the closest inSampleSize that will result in the final decoded bitmap having a width and
	 * height equal to or larger than the requested width and height. This implementation does not
	 * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
	 * results in a larger bitmap which isn't as useful for caching purposes.
	 *
	 * @param options	An options object with out params already populated (run through a decode
	 *                  method with inJustDecodeBounds==true
	 * @param reqWidth	The requested width of the resulting bitmap
	 * @param reqHeight	The requested height of the resulting bitmap
	 * @return	The value to be used for inSampleSize
	 */
	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		Common.log("NewEquipment calculateInSampleSize");
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will guarantee a final image
			// with both dimensions larger than or equal to the requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger inSampleSize).

			final float totalPixels = width * height;

			// Anything more than 2x the requested pixels we'll sample down further
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		Common.log("inSampleSize: "+Integer.toString(inSampleSize));
		return inSampleSize;
	}
}
