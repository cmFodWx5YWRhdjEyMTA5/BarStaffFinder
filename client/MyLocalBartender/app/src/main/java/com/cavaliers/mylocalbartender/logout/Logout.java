package com.cavaliers.mylocalbartender.logout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;


import com.cavaliers.mylocalbartender.MyLocalBartender;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.menu.organiser_menu.OrganiserMenuActivity;
import com.cavaliers.mylocalbartender.server.MLBService;

/**
 * Logout
 */

public class Logout {

    Activity mlb;
    private View popupView;
    private Dialog dialog;

    public Logout(Dialog dialog, View view, Activity a) {

        this.dialog = dialog;
        this.popupView = view;
        this.mlb = a;
        selectOK();
        selectCancel();

    }

    /**
     * Method to continue the Logout dialog and successfully logout of the account.
     */
    private void selectOK() {

        Button btnOk = (Button) popupView.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                MLBService.signOut();
                startPage();
            }
        });
    }

    /**
     * Method to Cancel the Logout dialog.
     */
    private void selectCancel() {

        Button btnCancel = (Button) popupView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Logout.this.dialog.dismiss();
            }
        });

    }

    /**
     *
     */
    public void startPage() {

        Intent startPage = new Intent(mlb, MyLocalBartender.class);
        mlb.startActivity(startPage);

        MyLocalBartender.state = MyLocalBartender.DEFAULT_PROFILE;

    }
}