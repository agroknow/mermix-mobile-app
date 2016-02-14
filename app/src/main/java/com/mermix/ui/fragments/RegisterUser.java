package com.mermix.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.mermix.ApplicationVars;
import com.mermix.R;
import com.mermix.custom.CustomFragment;
import com.mermix.model.BookEquipment;
import com.mermix.model.common.Pojo;
import com.mermix.ui.DataRetrieve;
import com.mermix.utils.Common;
import com.mermix.utils.Constants;
import com.mermix.utils.MainService;
import com.mermix.utils.net.args.NewEquipmentArgs;
import com.mermix.utils.net.args.UrlArgs;

/**
 * Created by vasilis on 2/6/16.
 */
public class RegisterUser extends CustomFragment implements DataRetrieve {
    private ProgressDialog progress;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;

    @Override
    public View setTouchNClick(View v) {
        return super.setTouchNClick(v);
    }

    @Override
    public void onClick(View v) {
        Common.log("Login onClick");
        super.onClick(v);

        if (v.getId() == R.id.btnRegister) {
            EditText usrTxtBox = (EditText)  this.getActivity().findViewById(R.id.username);
            this.username = usrTxtBox.getText().toString();

            EditText pwdTxtBox = (EditText) this.getActivity().findViewById(R.id.password);
            this.password = pwdTxtBox.getText().toString();

            EditText fnTxtBox = (EditText)  this.getActivity().findViewById(R.id.firstname);
            this.firstname = fnTxtBox.getText().toString();

            EditText lnTxtBox = (EditText) this.getActivity().findViewById(R.id.lastname);
            this.lastname = lnTxtBox.getText().toString();

            EditText emailTxtBox = (EditText) this.getActivity().findViewById(R.id.email);
            this.email = emailTxtBox.getText().toString();

            String roles = "";
            roles += ((CheckBox) this.getActivity().findViewById(R.id.role_owner)).isChecked() ? "Owner," : "";
            roles += ((CheckBox) this.getActivity().findViewById(R.id.role_professional)).isChecked() ? "Professional," : "";
            roles += ((CheckBox) this.getActivity().findViewById(R.id.role_renter)).isChecked() ? "Renter" : "";
            roles = roles.replaceAll(",$", "");
            if(roles.equals("")) {
                Common.displayToast(getString(R.string.select_role_error), getActivity().getApplicationContext());
                return;
            }
            //ApplicationVars.User.credentials = this.username+":"+this.password;
            String jsonString = "{\"username\":\"" + this.username + "\",\"password\":\"" + this.password + "\",\"firstname\":\""
                    +  this.firstname + "\",\"lastname\":\"" +  this.lastname + "\",\"email\":\"" +  this.email + "\", \"roles\":\""+ roles +"\"}";
            Common.log(jsonString);
            startRequestService(new NewEquipmentArgs(jsonString, true));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Common.log("Login onCreateView");
        View v = inflater.inflate(R.layout.register_user, null);
        //setTouchNClick(v.findViewById(R.id.btnRegister));
        setTouchNClick(v.findViewById(R.id.btnRegister));

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
            usrTxtBox.setText("newuser1");
            EditText pwdTxtBox = (EditText)  this.getActivity().findViewById(R.id.password);
            pwdTxtBox.setText("123123");
            EditText fnTxtBox = (EditText)  this.getActivity().findViewById(R.id.firstname);
            fnTxtBox.setText("nufirstname");
            EditText lnTxtBox = (EditText)  this.getActivity().findViewById(R.id.lastname);
            lnTxtBox.setText("nulastname");
            EditText emailTxtBox = (EditText)  this.getActivity().findViewById(R.id.email);
            emailTxtBox.setText("newuser1@example.com");
        }
    }

    @Override
    public void updateUI(Pojo apiResponseData) {
        Common.log("Register user updateUI");
        Boolean userLoggedIn = false;
        progress.dismiss();
        if(apiResponseData != null) {
            try {
                BookEquipment user = (BookEquipment) apiResponseData;
                Common.displayToast(user.getMessage(), getActivity().getApplicationContext());
                Common.log(user.getStatus());
                if(user.getStatus().equals("success")) {
                    ApplicationVars.User.id = user.getCreated_user();
                    userLoggedIn = true;
                    Fragment f = null;
                    f = new Login();

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, f).addToBackStack(getResources().getString(R.string.login))
                            .commit();
//                MainActivity activity = new MainActivity();
//                activity.goToFragmentAfterLogon();
                }
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
        NewEquipmentArgs newEquipmentArgs = (NewEquipmentArgs) urlArgs;
        String queryString = newEquipmentArgs.getUrlArgs();
        Common.log("RegisterUserFragment startRequestService");
        //equipmentId = getActivity().getIntent().getIntExtra(Constants.INTENTVARS.EQUIPMENTID, defaultEquipmentId);
        //String apiUrl = Constants.APIENDPOINT + ApplicationVars.restApiLocale + "/" + Constants.URI.SINGLEEQUIPMENT.replace("NID", Integer.toString(equipmentId));
        String apiUrl = Constants.APIENDPOINT + ApplicationVars.restApiLocale + "/" + Constants.URI.USERREGISTER + "?" + queryString;

        Intent i = new Intent(getActivity(), MainService.class);
        i.putExtra(Constants.INTENTVARS.APIURL, apiUrl);
        i.putExtra(Constants.INTENTVARS.POJOCLASS, Constants.PojoClass.BOOKEQUIPMENT);
        getActivity().startService(i);
    }

}
