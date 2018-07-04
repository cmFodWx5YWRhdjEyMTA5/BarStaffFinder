package com.cavaliers.mylocalbartender.signup;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cavaliers.mylocalbartender.MyLocalBartender;
import com.cavaliers.mylocalbartender.R;

/**
 * SignUp pop up dialog controller
 * When an instance of it is created it takes a view to be used as a holder, and
 * a context which is then passed in the SignupHandler.
 * It initialises each view(dialog) component in the method initSignupComponents()
 * and sets onSignupListener which is triggered when SIGNUP IS PRESSED
 */

public class SignUp{

    //SIGN-UP WINDOW COMPONENTS
    private TextView tv_signup_alertMessage;
    private EditText et_signup_emailField, et_signup_passwordField1, et_signup_passwordField2;
    private EditText et_signup_dboField;
    private RadioButton rb_signup_organiserRadio, rb_signup_staffRadio;
    private Button bt_signup_registerButton;
    private MyLocalBartender mlb;
    private SignupHandler signupHandler;

    /**
     * Istantiates class
     * @param popupView
     */
    public SignUp(View popupView, MyLocalBartender mlb){
        initSignupComponents(popupView);
        this.mlb = mlb;
    }

    //SignUp componens initialisation
    private void initSignupComponents(View popupView){

        tv_signup_alertMessage = (TextView) popupView.findViewById(R.id.signup_alert_message);
        et_signup_emailField = (EditText) popupView.findViewById(R.id.signup_email_field);
        et_signup_passwordField1 = (EditText) popupView.findViewById(R.id.signup_password_field1);
        et_signup_passwordField2 = (EditText) popupView.findViewById(R.id.signup_password_field2);
        et_signup_dboField = (EditText) popupView.findViewById(R.id.signup_dbo_field);
        rb_signup_organiserRadio = (RadioButton) popupView.findViewById(R.id.signup_organiser_radio);
        rb_signup_staffRadio = (RadioButton) popupView.findViewById(R.id.signup_staff_radio);
        bt_signup_registerButton = (Button) popupView.findViewById(R.id.signup_register_button);
    }


    /**
     * Assign listener to SIGN UP button
     */
    public void onSignupListener(){
        this.signupHandler = new SignupHandler(mlb, et_signup_emailField, et_signup_passwordField1,
                et_signup_passwordField2, et_signup_dboField, rb_signup_organiserRadio, rb_signup_staffRadio, tv_signup_alertMessage);
       bt_signup_registerButton.setOnClickListener(this.signupHandler);
    }


    public SignupHandler getSignupHandler(){
        return this.signupHandler;
    }

}