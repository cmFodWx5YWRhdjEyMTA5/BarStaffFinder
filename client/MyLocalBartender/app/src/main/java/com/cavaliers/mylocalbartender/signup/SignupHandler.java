package com.cavaliers.mylocalbartender.signup;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.MyLocalBartender;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;
import com.cavaliers.mylocalbartender.tools.cavs_widgets.date_picker.CavsDatePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Is triggered when SIGN-UP button is clicked, verifies all entries and send data to server
 */

public class SignupHandler implements View.OnClickListener{

    private EditText et_email;
    private EditText et_password1;
    private EditText et_password2;
    private TextView et_dboField;
    private TextView tv_alertMessage;
    private RadioButton rb_organiser;
    private RadioButton rb_staff;
    private String formattedDateOfBirth;
    MLBService.AccountType accountType;

    private boolean isEmailVerified;
    private boolean isPasswordVerified;
    private boolean isDboVerified;
    private boolean isProfileVerified;

    private String checkedOne;
    private MyLocalBartender mlb;
    private View view;
    private String email;
    private String password;

    public String getFormattedDateOfBirth(){
        return this.formattedDateOfBirth;
    }

    public String getEmail(){
        return this.et_email.getText().toString();
    }

    public String getPassword(){
        return this.et_password1.getText().toString();
    }

    public MLBService.AccountType getAccountType(){
        return this.accountType;
    }

    public SignupHandler(MyLocalBartender mlb, EditText et_email, EditText et_password1, EditText et_password2, EditText et_dboField, RadioButton rb_organiser, RadioButton rb_staff, TextView alertMessage){

        this.et_email = et_email;
        this.et_password1 = et_password1;
        this.et_password2 = et_password2;
        this.et_dboField = et_dboField;
        this.tv_alertMessage = alertMessage;
        this.rb_organiser = rb_organiser;
        this.rb_staff = rb_staff;

        this.isEmailVerified = false;
        this.isPasswordVerified = false;
        this.isDboVerified = false;
        this.isProfileVerified = false;


        this.mlb = mlb;

    }
    @Override
    public void onClick(View v) {
        //TESTING
        //mlb.dismissSignup();
        // end testing

        this.view = v;
        String _email = this.et_email.getText().toString();
        String _password1 = this.et_password1.getText().toString();
        String _password2 = this.et_password2.getText().toString();
        String _dateOfBirth = CavsDatePicker.getFormattedDateOfBirth();
        System.out.println("this is DATE OF B="+_dateOfBirth);


        //verify et_email
        if (!AccessVerificationTools.isEmailFormatValid(_email)) {
            errorAlert("Email is not valid");
            isEmailVerified = false;
            return;
        } else {
            isEmailVerified = true;
            this.email = _email;
        }

        //verify password
        if (!AccessVerificationTools.isSamePassword(_password1, _password2)) {
            errorAlert("Passwords do not match");
            isPasswordVerified = false;
            return;
        } else {
            isPasswordVerified = true;
            this.password = _password1;
        }

        //verify date of birth
        if (!AccessVerificationTools.isAdult(_dateOfBirth)) {
            errorAlert("You must be 18+");
            isDboVerified = false;
            return;
        } else {
            formattedDateOfBirth = AccessVerificationTools.dateFormatter(_dateOfBirth);
            isDboVerified = true;
        }

        //verify which radio button is checked
        if (rb_organiser.isChecked())
            checkedOne = "ORGANISER IS CHECKED";
        else if (rb_staff.isChecked())
            checkedOne = "STAFF IS CHECKED";
        else {
            checkedOne = "NONE IS CHECKED";
            errorAlert("Please choose type of account to register with");
            return;
        }

        //verifies if all fields have valid entries and the runs server
        if (isEmailVerified && isPasswordVerified && isDboVerified && (rb_organiser.isChecked() || rb_staff.isChecked())) {
            if (rb_organiser.isChecked()) {
                this.accountType = MLBService.AccountType.ORGANISER;
            } else {
                this.accountType = MLBService.AccountType.BARSTAFF;
            }
            Log.i("SIGNUP", "got here: " + _email);
            sendRequest();
        }
    }


    public void sendRequest(){
        try{
            MLBService.signUp(this.view.getContext(), this.email, this.password, formattedDateOfBirth, accountType, new MLBService.MLBResponseListener() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        //mlb dismissSignup....
                        mlb.dismissSignup();
                        String successMsg = response.getString("message");
                        Toast.makeText(mlb, successMsg, Toast.LENGTH_LONG).show();
                        passAlert(successMsg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new MLBService.MLBErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    errorAlert(error.getMessage());
                }
            });
    } catch (VolleyError e) {

        errorAlert(e.getMessage());

    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }
    }

    /**
     * display test error message on pop up window
     */
    private void errorAlert(String msg){
        tv_alertMessage.setTextColor(Color.RED);
        tv_alertMessage.setText(msg);
    }

    /**
     * display test pass message on pop up window
     */
    private void passAlert(String msg){
        tv_alertMessage.setTextColor(Color.GREEN);
        tv_alertMessage.setText(msg);
    }
}
