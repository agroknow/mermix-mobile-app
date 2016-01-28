package com.realestate.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.realestate.ApplicationVars;
import com.realestate.R;
import com.realestate.custom.CustomFragment;
import com.realestate.model.EquipmentPostPayload;
import com.realestate.model.ListOfTerms;
import com.realestate.model.PojoTerm;
import com.realestate.model.SQLiteNode;
import com.realestate.model.SQLiteTerm;
import com.realestate.model.common.Address;
import com.realestate.model.common.Body;
import com.realestate.model.common.DrupalListField;
import com.realestate.model.common.Pojo;
import com.realestate.model.sqlite.DrupalNodes;
import com.realestate.model.sqlite.DrupalTerms;
import com.realestate.ui.DataRetrieve;
import com.realestate.ui.activities.EquipmentDetail;
import com.realestate.ui.adapters.SpinnerTermAdapter;
import com.realestate.ui.components.multispinner.MultiSpinner;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.ImageUtils;
import com.realestate.utils.MainService;
import com.realestate.utils.net.args.NewEquipmentArgs;
import com.realestate.utils.net.args.TermArgs;
import com.realestate.utils.net.args.UrlArgs;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	private boolean spinnersInitialized;
	private Map<String, Spinner> spinners;
	private int noneVocabularyId;
	private int vocabularyId;
	private HashMap<Integer, Integer> obligatoryTextFields;
	private HashMap<Integer, Integer> obligatorySpinnerFields;

	private int[] multiValueSpinnerFields = {R.id.cultivation};
	private int[] singleValueSpinnerFields = {R.id.machine_type, R.id.location, R.id.contract_type};
	private boolean resetOnResume;
	private Map<String, SQLiteTerm> sqliteTermsRefMap;

	@Override
	public void updateUI(Pojo apiResponseData) {
		Common.log("NewEquipment updateUI");
		progress.dismiss();
		String dataType = "";
		ListOfTerms termsList = null;
		com.realestate.model.NewEquipment equipment = null;
		if(apiResponseData == null){
			Common.displayToast(getResources().getString(R.string.upload_failed), getActivity().getApplicationContext());
			return;
		}
		try {
			equipment = (com.realestate.model.NewEquipment) apiResponseData;
			dataType = "com.realestate.model.NewEquipment";
		}catch (ClassCastException e){
			try {
				termsList = (ListOfTerms) apiResponseData;
				dataType = "ListOfTerms";
			}
			catch (ClassCastException err){
				Common.logError("ClassCastException @ NewEquipment updateUI:" + err.getMessage());
			}
		}

		switch(dataType){
			case "com.realestate.model.NewEquipment":
				Common.log("NewEquipment updateUI com.realestate.model.NewEquipment");
				int equipmentId = Integer.parseInt(equipment.getId());
				DrupalNodes drupalNodes = new DrupalNodes(getActivity().getApplicationContext());
				Double[] coords = {this.payload.getAddress().getLatitude(), this.payload.getAddress().getLongitude()};
				//store new equipment's dataType to SQLite
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
				this.resetOnResume = true;
				break;
			case "ListOfTerms":
				Common.log("NewEquipment updateUI ListOfTerms");
				List<PojoTerm> terms = termsList.getTerms();
				String vocabulary;
				if (terms.size() > 0) {
					vocabulary = terms.get(0).getVocabulary();
					ApplicationVars.updateTermsCache(terms, getActivity().getApplicationContext());
					ApplicationVars.dataRetrieved.put(vocabulary, true);
					updateSpinnerWithVocabularyTerms(vocabulary, spinners.get(vocabulary));
				}
				break;
			default:
		}
	}

	@Override
	public void startRequestService(UrlArgs urlArgs) {
		NewEquipmentArgs newEquipmentArgs = null;
		TermArgs termArgs = null;
		String dataType = "";
		String apiUrl;
		String pojoClass;
		Intent i;
		try {
			newEquipmentArgs = (NewEquipmentArgs) urlArgs;
			dataType = "NewEquipmentArgs";
		} catch (ClassCastException e) {
			try {
				termArgs = (TermArgs) urlArgs;
				dataType = "TermArgs";
			} catch (ClassCastException err) {
				Common.logError("ClassCastException @ NewEquipment startRequestService:" + err.getMessage());
			}
		}

		switch(dataType) {
			case "NewEquipmentArgs":
				Common.log("NewEquipment startRequestService NewEquipmentArgs");
				String queryString = newEquipmentArgs.getUrlArgs();
				apiUrl = Constants.APIENDPOINT + ApplicationVars.restApiLocale + "/" + Constants.URI.NEWEQUIPMENT + "?" + queryString;
				pojoClass = Constants.PojoClass.NEWEQUIPMENT;
				i = new Intent(this.getActivity(), MainService.class);
				i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
				i.putExtra(Constants.INTENTVARS.POJOCLASS, pojoClass);
				this.getActivity().startService(i);
				progress.show();
				break;
			case "TermArgs":
				Common.log("NewEquipment startRequestService TermArgs");
				apiUrl = Constants.APIENDPOINT + ApplicationVars.restApiLocale + "/" + Constants.URI.LISTOFTERMS + "?" + termArgs.getUrlArgs();
				pojoClass = Constants.PojoClass.LISTOFTERMS;
				i = new Intent(this.getActivity(), MainService.class);
				i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
				i.putExtra(Constants.INTENTVARS.POJOCLASS, pojoClass);
				this.getActivity().startService(i);
				break;
			default:
		}
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
			getFormData();
			validateFormData();
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
				//imageView.invalidate();
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
		this.spinnersInitialized = false;
		this.spinners = new HashMap<>();
		this.noneVocabularyId = -1;
		this.vocabularyId = this.noneVocabularyId;
		this.resetOnResume = false;
		this.sqliteTermsRefMap = new HashMap<>();

		this.obligatoryTextFields = new HashMap<>();
		this.obligatoryTextFields.put(R.id.title, R.id.title_lbl);
		this.obligatoryTextFields.put(R.id.address, R.id.address_lbl);

		this.obligatorySpinnerFields = new HashMap<>();
		this.obligatorySpinnerFields.put(R.id.machine_type, R.id.machine_type_lbl);
		this.obligatorySpinnerFields.put(R.id.cultivation, R.id.cultivation_lbl);
		this.obligatorySpinnerFields.put(R.id.contract_type, R.id.contract_type_lbl);

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

	/**
	 * Called when the Fragment is visible to the user.  This is generally
	 * tied to {@link Activity#onStart() Activity.onStart} of the containing
	 * Activity's lifecycle.
	 */
	@Override
	public void onStart() {
		super.onStart();
		Common.log("NewEquipment onStart");
		if(!spinnersInitialized) {
			spinners.put(Constants.VOCABULARYNAMES.LOCATION, (Spinner) getActivity().findViewById(R.id.location));
			spinners.put(Constants.VOCABULARYNAMES.MACHINETYPE, (Spinner) getActivity().findViewById(R.id.machine_type));
			spinners.put(Constants.VOCABULARYNAMES.CULTIVATION, (Spinner) getActivity().findViewById(R.id.cultivation));
			spinners.put(Constants.VOCABULARYNAMES.CONTRACTTYPE, (Spinner) getActivity().findViewById(R.id.contract_type));
			setSpinnersData();
			spinnersInitialized = true;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Common.log("NewEquipment onResume");
		updateFormView();
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
		Common.log("Build.VERSION.SDK_INT:" + Integer.toString(Build.VERSION.SDK_INT));
		Common.log("Build.VERSION_CODES.KITKAT:" + Integer.toString(Build.VERSION_CODES.KITKAT));
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

	private void submitFormData() {
		Common.log("NewEquipment submitFormData");
		if(this.payload != null) {
			String payloadStr = Common.pojo2Json(this.payload);
			startRequestService(new NewEquipmentArgs(payloadStr));
		}
	}

	/**
	 * get data from form elements and create instance of object EquipmentPostPayload
	 */
	private void getFormData() {
		Common.log("NewEquipment getFormData");
		String type = "apartment";
		int promote = 0;

		DrupalListField author = new DrupalListField();
		author.setId(ApplicationVars.User.id);

		EditText titleTxtBox = (EditText) getActivity().findViewById(R.id.title);
		String title = titleTxtBox.getText().toString();

		Body body = new Body();
		EditText textTxtBox = (EditText) getActivity().findViewById(R.id.text);
		body.setValue(textTxtBox.getText().toString());

		DrupalListField machineType = new DrupalListField();
		SQLiteTerm machineTypeTerm = (SQLiteTerm) spinners.get(Constants.VOCABULARYNAMES.MACHINETYPE).getSelectedItem();
		if(machineTypeTerm.getTid() != Constants.SPINNERITEMS.EMPTYTERM.TID)
			machineType.setId(Integer.toString(machineTypeTerm.getTid()));
		else
			machineType = null;

		DrupalListField contract = new DrupalListField();
		SQLiteTerm contractTerm = (SQLiteTerm) spinners.get(Constants.VOCABULARYNAMES.CONTRACTTYPE).getSelectedItem();
		if(contractTerm.getTid() != Constants.SPINNERITEMS.EMPTYTERM.TID)
			contract.setId(Integer.toString(contractTerm.getTid()));
		else
			contract = null;

		List<DrupalListField> cultivationsList = new ArrayList<>();
		String selection = spinners.get(Constants.VOCABULARYNAMES.CULTIVATION).getSelectedItem().toString();
		if(!selection.equals(getResources().getString(R.string.selectItems))) {
			String[] selectedTerms = spinners.get(Constants.VOCABULARYNAMES.CULTIVATION).getSelectedItem().toString().split(Constants.CONCATDELIMETER);
			for (int i = 0; i < selectedTerms.length; i++) {
				if (!selectedTerms[i].isEmpty()) {
					SQLiteTerm cultivationTerm = sqliteTermsRefMap.get(selectedTerms[i]);
					if (cultivationTerm != null) {
						DrupalListField cultivation = new DrupalListField();
						cultivation.setId(Integer.toString(cultivationTerm.getTid()));
						cultivationsList.add(i, cultivation);
					}
				}
			}
		}
		else
			cultivationsList = null;

		DrupalListField location = new DrupalListField();
		SQLiteTerm locationTerm = (SQLiteTerm) spinners.get(Constants.VOCABULARYNAMES.LOCATION).getSelectedItem();
		if(locationTerm.getTid() != Constants.SPINNERITEMS.EMPTYTERM.TID)
			location.setId(Integer.toString(locationTerm.getTid()));
		else
			location = null;

		EditText addressTxtBox = (EditText) getActivity().findViewById(R.id.address);
		String addressTxt = addressTxtBox.getText().toString();
		Address address = Common.getAddressFromLocation(addressTxt, getActivity().getApplicationContext());

		List<String> imageList = new ArrayList<>();
		imageList.add(0, this.equipmentPhotoPath);

		this.payload = new EquipmentPostPayload();
		this.payload.setData(title, body, type, promote, ApplicationVars.restApiLocale, author, machineType, contract, cultivationsList, location, address, imageList);
	}

	/**
	 * validates form data
	 * on validation success, invokes submitFormData
	 * on validation fail, display message
	 */
	private void validateFormData() {
		Common.log("NewEquipment validateFormData");
		Boolean validationSuccess = true;
		EditText editText;
		TextView txtView;
		Iterator fieldsIterator;
		Spinner spinner;
		Object lblId = null;
		Boolean missingTextFeild = false;
//TextFields
		fieldsIterator = this.obligatoryTextFields.entrySet().iterator();
		while(fieldsIterator.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry)fieldsIterator.next();
			editText = (EditText) getActivity().findViewById((Integer) pair.getKey());
			if(editText.getText().toString().isEmpty()){
				lblId = pair.getValue();
				validationSuccess = false;
				missingTextFeild = true;
				break;
			}
		}
//SpinnerFields
		if(validationSuccess) {
			fieldsIterator = this.obligatorySpinnerFields.entrySet().iterator();
			while (fieldsIterator.hasNext()) {
				HashMap.Entry pair = (HashMap.Entry) fieldsIterator.next();
				spinner = (Spinner) getActivity().findViewById((Integer) pair.getKey());
				//singleValueSpinnerFields
				if (Common.isInArrayInteger(this.singleValueSpinnerFields, spinner.getId())) {
					SQLiteTerm sqLiteTerm = (SQLiteTerm) spinner.getSelectedItem();
					if (sqLiteTerm.getTid() == Constants.SPINNERITEMS.EMPTYTERM.TID) {
						lblId = pair.getValue();
						validationSuccess = false;
						break;
					}
				}
				//multiValueSpinnerFields
				if (Common.isInArrayInteger(this.multiValueSpinnerFields, spinner.getId())) {
					if (spinner.getSelectedItem().toString() == getResources().getString(R.string.selectItems)) {
						lblId = pair.getValue();
						validationSuccess = false;
						break;
					}
				}
			}
		}

		if(validationSuccess)
			submitFormData();
		else{
			txtView = (TextView) getActivity().findViewById((Integer) lblId);
			String promptMsg = missingTextFeild ?
					getResources().getString(R.string.complete_obligatory_field):
					getResources().getString(R.string.select_obligatory_field);
			Common.displayToast(
					promptMsg + " " + txtView.getText().toString(),
					getActivity().getApplicationContext());
		}
	}

	private void setSpinnersData() {
		Common.log("NewEquipment setSpinnersData");
		updateSpinnerWithVocabularyTerms(Constants.VOCABULARYNAMES.LOCATION, spinners.get(Constants.VOCABULARYNAMES.LOCATION));
		updateSpinnerWithVocabularyTerms(Constants.VOCABULARYNAMES.MACHINETYPE, spinners.get(Constants.VOCABULARYNAMES.MACHINETYPE));
		updateSpinnerWithVocabularyTerms(Constants.VOCABULARYNAMES.CULTIVATION, spinners.get(Constants.VOCABULARYNAMES.CULTIVATION));
		updateSpinnerWithVocabularyTerms(Constants.VOCABULARYNAMES.CONTRACTTYPE, spinners.get(Constants.VOCABULARYNAMES.CONTRACTTYPE));
	}

	/**
	 * add items in spinners programmatically
	 * @ first check if spinner data have been retrieved from REST API
	 * if data have been retrieved spinner is updated
	 * if data have NOT been retrieved startRequestService is invoked
	 */
	private void updateSpinnerWithVocabularyTerms(String vocabulary, Spinner spinner) {
		Common.log("NewEquipment updateSpinnerWithVocabularyTerms");

		List<SQLiteTerm> SQLiteTerms = new ArrayList<>();
		Boolean termsDataRetrieved = ApplicationVars.dataRetrieved.get(vocabulary);
		if(termsDataRetrieved != null && termsDataRetrieved){
			DrupalTerms drupalTerms = new DrupalTerms(getActivity());
			if(!(this.obligatorySpinnerFields.containsKey(spinner.getId()))){
				//empty selection for NON obligatory spinner fields
				SQLiteTerms.add(0, new SQLiteTerm(
						Constants.SPINNERITEMS.EMPTYTERM.TID,
						Constants.SPINNERITEMS.EMPTYTERM.NAME,
						Constants.VOCABULARYNAMES.EMPTY));
			}
			SQLiteTerms.addAll(drupalTerms.getVocabularyTerms(vocabulary));
			drupalTerms.closeConnection();
			if(Common.isInArrayInteger(this.multiValueSpinnerFields, spinner.getId())){
				updateMultiSpinner(SQLiteTerms);
			}
			else{
			/*
			 * more android.R.layout. options in
			 * http://developer.android.com/reference/android/R.layout.html
			 */
				SpinnerTermAdapter spinAdapter = new SpinnerTermAdapter(getActivity(),
						android.R.layout.simple_list_item_checked,
						SQLiteTerms);
				spinner.setAdapter(spinAdapter);
			}
		}
		else{
			vocabularyId = Constants.TERMVOCABULARIES.containsKey(vocabulary) ? Constants.TERMVOCABULARIES.get(vocabulary) :
					noneVocabularyId;
			startRequestService(new TermArgs(Integer.toString(vocabularyId)));
		}
	}

	private void updateMultiSpinner(List<SQLiteTerm> SQLiteTerms){
		Common.log("NewEquipment updateMultiSpinner");
		TreeMap<String, Boolean> termsBooleanTreeMap = new TreeMap<>();
		for(int idx=0;idx<SQLiteTerms.size();idx++) {
			termsBooleanTreeMap.put(SQLiteTerms.get(idx).getName(), false);
			sqliteTermsRefMap.put(SQLiteTerms.get(idx).getName(), SQLiteTerms.get(idx));
		}
		MultiSpinner multiSpinner = (MultiSpinner) getActivity().findViewById(R.id.cultivation);
		String multiSpinnerTitle = Constants.SPINNERITEMS.EMPTYTERM.NAME;
		multiSpinner.setItems(termsBooleanTreeMap, multiSpinnerTitle, new MultiSpinner.MultiSpinnerListener() {
			@Override
			public void onItemsSelected(boolean[] selected) {
				// your operation with code...
//				for (int i = 0; i < selected.length; i++) {
//					if (selected[i]) {
//						Common.log(Integer.toString(i));
//					}
//				}
			}
		});
	}

	/**
	 * reset values in Form fields as long as flag resetOnResume is set
	 * sets dummy values when in development mode
	 */
	private void updateFormView() {
		Activity v = getActivity();
		if(this.resetOnResume){
			Common.log("NewEquipment updateFormView resetOnResume");
//EditText
			EditText editText = (EditText) v.findViewById(R.id.title);
			editText.setText("");
			editText = (EditText) v.findViewById(R.id.text);
			editText.setText("");
			editText = (EditText) v.findViewById(R.id.address);
			editText.setText("");
//singleValueSpinnerFields
			Spinner spinner;
			for(int idx=0;idx<this.singleValueSpinnerFields.length;idx++){
				spinner = (Spinner) v.findViewById(this.singleValueSpinnerFields[idx]);
				spinner.setSelection(0, true);
			}
//multiValueSpinnerFields
			MultiSpinner multiSpinner;
			for(int idx=0;idx<this.multiValueSpinnerFields.length;idx++){
				multiSpinner = (MultiSpinner) v.findViewById(this.multiValueSpinnerFields[idx]);
				multiSpinner.resetSelections();
			}
//ImageViews
			ImageView imageView = (ImageView) getActivity().findViewById(R.id.img2Submit);
			imageView.setImageBitmap(null);
			this.resetOnResume = false;
		}

		if(Constants.devMode){
			EditText editText = (EditText) v.findViewById(R.id.title);
			editText.setText("Equipment submitted from mobile app");
			editText = (EditText) v.findViewById(R.id.text);
			editText.setText("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia");
			editText = (EditText) v.findViewById(R.id.address);
			editText.setText("Leof. Athinon 110");
		}
	}
}
