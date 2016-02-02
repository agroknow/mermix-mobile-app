package com.realestate.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.realestate.ApplicationVars;
import com.realestate.R;
import com.realestate.custom.CustomFragment;
import com.realestate.model.common.Pojo;
import com.realestate.model.common.User;
import com.realestate.ui.DataRetrieve;
import com.realestate.ui.activities.MainActivity;
import com.realestate.ui.activities.RegisterActivity;
import com.realestate.utils.Common;
import com.realestate.utils.Constants;
import com.realestate.utils.MainService;
import com.realestate.utils.net.args.UrlArgs;

/**
 * Created on 25/01/2016
 * Description:
 */
public class Login extends CustomFragment implements DataRetrieve {
	private ProgressDialog progress;
	private String username;
	private String password;

	@Override
	public View setTouchNClick(View v) {
		return super.setTouchNClick(v);
	}

	@Override
	public void onClick(View v) {
		Common.log("Login onClick");
		super.onClick(v);

		if (v.getId() == R.id.btnLogin) {
			EditText usrTxtBox = (EditText)  this.getActivity().findViewById(R.id.username);
			this.username = usrTxtBox.getText().toString();

			EditText pwdTxtBox = (EditText) this.getActivity().findViewById(R.id.password);
			this.password = pwdTxtBox.getText().toString();

			ApplicationVars.User.credentials = this.username+":"+this.password;
			startRequestService(null);
		}
		else if(v.getId() == R.id.btnRegister) {
			Intent i = new Intent(this.getActivity(), RegisterActivity.class);
			startActivity(i);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Common.log("Login onCreateView");
		View v = inflater.inflate(R.layout.login, null);
		setTouchNClick(v.findViewById(R.id.btnRegister));
		setTouchNClick(v.findViewById(R.id.btnLogin));

		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.log("Login onCreate");
		super.onCreate(savedInstanceState);

		progress = new ProgressDialog(this.getActivity());
		progress.setTitle(getResources().getString(R.string.progress_dialog_title));
		progress.setMessage(getResources().getString(R.string.progress_dialog_user_login));
	}

	@Override
	public void onStart() {
		Common.log("Login onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		Common.log("Login onResume");
		super.onResume();
		if(Constants.devMode) {
			EditText usrTxtBox = (EditText)  this.getActivity().findViewById(R.id.username);
			usrTxtBox.setText("tkosmopoulos_159");
			EditText pwdTxtBox = (EditText)  this.getActivity().findViewById(R.id.password);
			pwdTxtBox.setText("te0k0sm@");
		}
	}

	@Override
	public void onDestroy() {
		Common.log("Login onDestroy");
		super.onDestroy();
	}

	@Override
	public void onPause() {
		Common.log("Login onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		Common.log("Login onStop");
		super.onStop();
	}

	@Override
	public void updateUI(Pojo apiResponseData) {
		Common.log("Login updateUI");
		Boolean userLoggedIn = false;
		progress.dismiss();
		if(apiResponseData != null) {
			try {
				User user = (User) apiResponseData;
				ApplicationVars.User.id = user.getUid();
				userLoggedIn = true;

				MainActivity activity = (MainActivity) this.getActivity();
				activity.goToFragmentAfterLogon();
			} catch (ClassCastException e) {
				Common.logError("ClassCastException @ Login updateUI:" + e.getMessage());
			}
		}
		if(!userLoggedIn) {
			Common.displayToast(getResources().getString(R.string.user_login_failed), getActivity().getApplicationContext());
			ApplicationVars.User.credentials = "";
		}
	}

	@Override
	public void startRequestService(UrlArgs urlArgs) {
		String apiUrl = Constants.APIENDPOINT + ApplicationVars.restApiLocale + "/" + Constants.URI.USERINFO;
		String pojoClass = Constants.PojoClass.USER;

		Intent i = new Intent(this.getActivity(), MainService.class);
		i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
		i.putExtra(Constants.INTENTVARS.POJOCLASS, pojoClass);
		this.getActivity().startService(i);
		progress.show();
	}
}
