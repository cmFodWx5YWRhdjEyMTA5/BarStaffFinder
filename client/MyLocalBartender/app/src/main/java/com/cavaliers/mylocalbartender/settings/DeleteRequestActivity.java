package com.cavaliers.mylocalbartender.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cavaliers.mylocalbartender.MyLocalBartender;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.activity.MLBActivity;
import com.cavaliers.mylocalbartender.signup.SignUp;

/**
 * Created by JamesRich on 09/03/2017.
 */

public class DeleteRequestActivity extends MLBActivity {

    private Intent intent;


    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        Toast.makeText(this, "deleting....", Toast.LENGTH_LONG).show();
        intent = getIntent();
        // navigate to main....


    }

}
