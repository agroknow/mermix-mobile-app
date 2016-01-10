package com.realestate.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.realestate.utils.ImageUtils;
import com.realestate.utils.MainService;
import com.realestate.utils.net.args.NewEquipmentArgs;
import com.realestate.utils.net.args.UrlArgs;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created on 21/12/2015
 * Description:
 * submit New Equipment to REST API
 * on successful submit, store new equipment's data to SQLite & navigate to activity EquipmentDetail
 *
 * HELP
 * http://developer.android.com/guide/topics/media/camera.html
 * http://developer.android.com/guide/topics/data/data-storage.html#filesExternal
 * http://developer.android.com/training/basics/intents/result.html
 * http://developer.android.com/reference/android/graphics/BitmapFactory.Options.html
 *
 * ISSUE
 * When camera causes orientation change in Activity then Activity is destroyed and recreated.
 * SOLUTION
 * http://stackoverflow.com/a/10411504
 */
public class NewEquipment extends CustomFragment implements DataRetrieve {
	private EquipmentPostPayload payload;
	private Bitmap equipmentPhoto;
	private String equipmentPhotoPath;
	private ProgressDialog progress;

	@Override
	public void updateUI(Pojo apiResponseData) {
		Common.log("NewEquipment updateUI");
		progress.dismiss();
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
		progress.show();
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
			List<String> imageList = new ArrayList<>();
			imageList.add(0, this.equipmentPhotoPath);

			this.payload = new EquipmentPostPayload();
			this.payload.setData(title, body, type, promote, lang, author, equipmentType, contract, cultivationsList, location, address, imageList);

			String payloadStr = Common.pojo2Json(this.payload);
			startRequestService(new NewEquipmentArgs(payloadStr));
		}
		else if(v.getId() == R.id.openCamera){
			this.equipmentPhotoPath = "";	//assigned value in createImageFile
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// Ensure that there's a camera activity to handle the intent
			if (takePictureIntent.resolveActivity(getActivity().getApplicationContext().getPackageManager()) != null) {
				// Create the File where the image should go
				File imageFile = null;
				try {
					imageFile = createImageFile();
				} catch (IOException ex) {
					Common.logError("IOException @ NewEquipment.onClick :" + ex.getMessage());
				}
				if (imageFile != null) {
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
					startActivityForResult(takePictureIntent, Constants.ACTIVITYREQUESTCODES.OPENDEVICECAMERA);
				}
				else{
					Common.logError("image file creation failed");
				}
			}
		}
		else if(v.getId() == R.id.selectImageFile) {
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setType("image/*");
			startActivityForResult(
					Intent.createChooser(intent, getResources().getString(R.string.selectImageFile)),
					Constants.ACTIVITYREQUESTCODES.SELECTFILEFROMGALLERY);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Common.log("NewEquipment onActivityResult");
		this.equipmentPhoto = null;
		if(resultCode == getActivity().RESULT_OK){
			if(data == null){
				Common.log("null intent returned in onActivityResult because of use of MediaStore.EXTRA_OUTPUT URI");
			}
			else{
				//get image from intent data
				//this.equipmentPhoto = (Bitmap) data.getExtras().get("data");
			}
			if(requestCode == Constants.ACTIVITYREQUESTCODES.OPENDEVICECAMERA){
				addImage2Gallery();
			}
			if(requestCode == Constants.ACTIVITYREQUESTCODES.SELECTFILEFROMGALLERY){
				Uri selectedImageUri = data.getData();
				this.equipmentPhotoPath = Common.getGalleryImagePathFromUri(selectedImageUri, getActivity());
			}
			/**
			 * equipmentPhotoPath value assigned in METHODS
			 * Common.getGalleryImagePathFromUri OR createImageFile()
			 */
			if(!this.equipmentPhotoPath.isEmpty()){
				try {
					this.equipmentPhoto = ImageUtils.configureBitmapSamplingRotation(this.equipmentPhotoPath);
				} catch (IOException e) {
					//e.printStackTrace();
					Common.logError("IOException @ NewEquipment onActivityResult:" + e.getMessage());
				}
			}
			if(this.equipmentPhoto != null){
				//display image in view
				Common.log("equipment photo size, getByteCount: " + Integer.toString(this.equipmentPhoto.getByteCount()));
				ImageView imageView = (ImageView) getActivity().findViewById(R.id.img2Submit);
				imageView.setImageBitmap(this.equipmentPhoto);
				imageView.invalidate();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Common.log("NewEquipment onCreate");
		this.equipmentPhotoPath = "";
		this.payload = null;
		this.equipmentPhoto = null;
		progress = new ProgressDialog(getActivity());
		progress.setTitle(getResources().getString(R.string.progress_dialog_title));
		progress.setMessage(getResources().getString(R.string.progress_dialog_upload_msg));
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

	private File createImageFile()throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String appImageDir = getResources().getString(R.string.app_name)+"Images";
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), appImageDir);
		// Create the storage directory if it does not exist
		if(!Common.isExternalStorageWritable()){
			Common.logError("device's external storage not writable");
			return null;
		}
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Common.logError("failed to create directory " + appImageDir);
				return null;
			}
		}
		File image = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
		// Save a file: path for use with ACTION_VIEW intents
		this.equipmentPhotoPath = image.getAbsolutePath();
		Common.log("Current image path: " + this.equipmentPhotoPath);
		return image;
	}

	private void addImage2Gallery() {
		File f = new File(this.equipmentPhotoPath);
		Uri contentUri = Uri.fromFile(f);

		/*//insert a image into the MediaStore manually
		try {
			MediaStore.Images.Media.insertImage(getActivity().getApplicationContext().getContentResolver(),
					f.getAbsolutePath(), f.getName(), null);
		} catch (FileNotFoundException e) {
			Common.logError("FileNotFoundException @ NewEquipment.addImage2Gallery: "+e.getMessage());
		}*/
		//Broadcasting a Media Scanner intent to make the image show up in the gallery
		Common.log("Build.VERSION.SDK_INT:"+Integer.toString(Build.VERSION.SDK_INT));
		Common.log("Build.VERSION_CODES.KITKAT:"+Integer.toString(Build.VERSION_CODES.KITKAT));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			mediaScanIntent.setData(contentUri);
			getActivity().sendBroadcast(mediaScanIntent);
		}
		else {
			//getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, contentUri));
			getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri));
		 }
	}
}
