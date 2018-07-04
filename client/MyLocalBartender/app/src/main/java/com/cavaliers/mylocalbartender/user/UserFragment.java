package com.cavaliers.mylocalbartender.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.fragment.MLBFragment;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Robert on 23/03/2017.
 */

public abstract class UserFragment extends MLBFragment {

    protected View rootView;

    protected EditText et_first_name;
    protected EditText et_last_name;
    protected EditText et_DOB;
    protected EditText et_post_code;
    protected Spinner spn_gender;
    protected EditText et_proximity;
    protected EditText et_card_number;
    protected EditText et_expire_month;
    protected EditText et_expire_year;
    protected EditText et_cvc;

    protected ImageView userProfilePicture;

    protected EditText[] editTextArray;
    protected Button btn_save;

    protected abstract void setSocketListener();
    protected abstract void fillProfilePage();
    protected abstract void initWidget();
    protected abstract void setSaveButtonListener();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void initChksToValidate(EditText ... editTexts){
        this.editTextArray = editTexts;
        setEditTextListener();
    }

    protected void setEditTextListener() {

        for (EditText entry : editTextArray) { //loops through all entries of the array
            if(entry != null) {
                AccessVerificationTools.setEditTextListener(entry); //calls method in AccessVerificationTools
            }
        }
    }

    //GETTERS
    public Context getContext(){
        return this.rootView.getContext();
    }

    public String getFirstName(){
        return this.et_first_name.getText().toString();
    }

    public String getLastName(){
        return this.et_last_name.getText().toString();
    }

    public String getDOB(){
        return this.et_DOB.getText().toString();
    }

    public String getPostCode(){
        return this.et_post_code.getText().toString();
    }

    public String getGender(){
        if(spn_gender.getSelectedItem().toString().equals("Female")) return "F";
        else if(spn_gender.getSelectedItem().toString().equals("Male")) return "M";
        else return "Gender";
    }

    public String getProximity(){
        return this.et_proximity.getText().toString();
    }
    public String getCardNumber(){
        return this.et_card_number.getText().toString();
    }

    public String getExpMonth(){
        return this.et_expire_month.getText().toString();
    }

    public String getExpYear(){
        return this.et_expire_year.getText().toString();
    }

    public String getCVC(){
        return this.et_cvc.getText().toString();
    }

    //Setters


    public boolean stripeValidator(){
        if(et_card_number.getText().toString().equals("") &&
                et_expire_month.getText().toString().equals("")&&
                et_expire_year.getText().toString().equals("") &&
                et_cvc.getText().toString().equals("")){

            return false;
        }
        return true;
    }


    protected void initCommonWidgets(){

        // Payment Details
        et_card_number = (EditText) rootView.findViewById(R.id.et_card_number);
        et_expire_month = (EditText) rootView.findViewById(R.id.et_expire_month);
        et_expire_year = (EditText) rootView.findViewById(R.id.et_expire_year);
        et_cvc = (EditText) rootView.findViewById(R.id.et_cvc);

    }

    public boolean validator(){
        return (AccessVerificationTools.isEmpty(editTextArray, spn_gender) != 0);
    }
}
