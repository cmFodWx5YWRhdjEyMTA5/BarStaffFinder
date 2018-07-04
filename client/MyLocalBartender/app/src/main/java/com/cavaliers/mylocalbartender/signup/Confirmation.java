package com.cavaliers.mylocalbartender.signup;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.cavaliers.mylocalbartender.MyLocalBartender;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.server.MLBService;

/**
 * Created by JamesRich on 02/03/2017.
 */

public class Confirmation {

    private Button bt_resend;
    private Button bt_signIn;
    private MyLocalBartender mlb;
    //private Activity activity;
    private AlertDialog confirmationDialog;
    private AlertDialog signupDialog;

    private SignupHandler signupHandler;
    /**
     * Confirmation object created from line
     * @param popupView
     * @param mlb
     * @param confirmationDialog
     * @param signupDialog
     */
    public Confirmation(View popupView, MyLocalBartender mlb, AlertDialog confirmationDialog, AlertDialog signupDialog,
                        final SignupHandler signupHandler){
        initConfirmationComponents(popupView);
        this.confirmationDialog = confirmationDialog;
        this.signupDialog = signupDialog;
        //this.activity;
        this.mlb = mlb;
        this.signupHandler = signupHandler;

        handleClicks();
    }

    public void initConfirmationComponents(View view){
        bt_resend = (Button) view.findViewById(R.id.resendButton);
        bt_signIn = (Button) view.findViewById(R.id.signinButton);
    }

    /**
     * method for handling clicks, this class may be reusable by multiple different classes.
     */
    private void handleClicks(){

        bt_resend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //THE SIGN UP SECTION, email send.
                Confirmation.this.signupHandler.sendRequest();
            }
        });
        bt_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (confirmationDialog != null && signupDialog != null)
                {
                    confirmationDialog.dismiss();
                    mlb.displaySignin();

                }

            }
        });
    }

}
