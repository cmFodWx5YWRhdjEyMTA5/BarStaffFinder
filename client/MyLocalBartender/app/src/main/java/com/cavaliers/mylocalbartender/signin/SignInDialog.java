package com.cavaliers.mylocalbartender.signin;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.MyLocalBartender;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.home.search.model.AdvertData;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;
import com.cavaliers.mylocalbartender.tools.cavs_widgets.availability_calendar.AvailabilityCalendarData;
import com.cavaliers.mylocalbartender.user.barstaff.model.ProfileBarstaffData;
import com.cavaliers.mylocalbartender.user.organiser.model.ProfileOrganiserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 *  Sing in class to handle the sing in logic
 */
public class SignInDialog {

    private View signInView; // xml sign in layout
    private MyLocalBartender mlb; // Instance of the parent class(MyLocalBartender) so as to call its methods when needed

    private Button signInBtn; // sing in button
    private EditText emailEditTxt; // emial text field
    private EditText passwordEditTxt; // password text field
    private TextView msgTxtView; // Please holder for the user feedback messages
    /**
     * Constructor to initialise the view and set a reference
     * the reference to the MLB class
     *
     * @param mlb Parent class cotaining the singin layout
     * @param view the xml view
     *
     *
     */
    public SignInDialog(MyLocalBartender mlb, View view) {
        //add layout of sign_in instead of activity_signup
        this.signInView = view;
        this.mlb = mlb;
    }

    private Bundle setExtras(String DOB){
        Bundle bundle =  new Bundle();
        bundle.putString(MLBService.JSONKey.DOB, DOB);
        return bundle;
    }

    /**
     * Define the click listener for the sing in event button
     */
    public void setListener(){
        this.signInBtn = (Button) this.signInView.findViewById(R.id.btn_sign_in);
        this.emailEditTxt = (EditText) this.signInView.findViewById(R.id.et_email_address);
        this.passwordEditTxt = (EditText) this.signInView.findViewById(R.id.et_password);
        this.msgTxtView = (TextView) this.signInView.findViewById(R.id.tv_signInMsg);

        this.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(SignInDialog.this.emailValidation()) {
                    try {

                        MLBService.signIn(view.getContext(), emailEditTxt.getText().toString(), passwordEditTxt.getText().toString(), new MLBService.MLBResponseListener() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                try {

                                    JSONObject user = response.getJSONObject(MLBService.JSONKey.USER);
                                    MLBService.AccountType accountType = SignInDialog.this.getType(user.getString(MLBService.JSONKey.ACCOUNT_TYPE));
                                    boolean isActive = getIsActive(user.getInt(MLBService.JSONKey.ACTIVE));

                                    // Fill User profile page with data


                                    Tools.accountType = accountType;

                                    //UPDATE JOBLISTS
                                    System.out.println("JSON: " + response);
                                    if(isActive) {
                                        AvailabilityCalendarData.setUpAvailabilityCalendar();
                                        AdvertData.load(response.getJSONObject("signin_json"));
                                        userProfileData(accountType, response);
                                        /*AnswerData.updateAnswers(response);
                                        RequestData.updateRequests(response);
                                        PrivateJobsData.updateAdverts(response);
                                        JobModifyData.updateAdverts(response);*/

                                        JSONObject test_advert = new JSONObject();

                                        test_advert.put("eventTitle","Test advert");

                                        //adverts.put(test_advert);

                                        //AdvertData.updateAdverts(adverts);
                                    }else{
                                        dobProfileData(accountType, response);
                                    }

                                    System.out.println("ACTIVE:::::" + user);

                                    //MLBService.connectSocket();

                                    //UPDATE JOBLISTS

                                    //Start either the organiser or barstaff activity
                                    SignInDialog.this.startProfile(accountType, isActive);
                                }catch (JSONException e){

                                    e.printStackTrace();
                                }
                                }
                        }, new MLBService.MLBErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                setErrorMsg(error.getMessage());
                            }
                        });

                    } catch (VolleyError e){

                        setErrorMsg(e.getMessage());

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void dobProfileData(MLBService.AccountType accountType, JSONObject object) throws JSONException{
        if(accountType == MLBService.AccountType.ORGANISER){
            if(!(object.has(MLBService.JSONKey.SIGNIN_JSON) && object.getJSONObject(MLBService.JSONKey.SIGNIN_JSON).has(MLBService.JSONKey.PROFILE))) return;
            JSONObject profile = object.getJSONObject(MLBService.JSONKey.SIGNIN_JSON).getJSONObject(MLBService.JSONKey.PROFILE);
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.DOB, profile.getString(MLBService.JSONKey.DOB));
        }else{
            if(!(object.has(MLBService.JSONKey.SIGNIN_JSON) && object.getJSONObject(MLBService.JSONKey.SIGNIN_JSON).has(MLBService.JSONKey.PROFILE))) return;
            JSONObject profile = object.getJSONObject(MLBService.JSONKey.SIGNIN_JSON).getJSONObject(MLBService.JSONKey.PROFILE);
            ProfileBarstaffData.setProfileData(MLBService.JSONKey.DOB, profile.getString(MLBService.JSONKey.DOB));
        }

    }

    private  void userProfileData(MLBService.AccountType accountType, JSONObject object) throws JSONException {
        if(accountType == MLBService.AccountType.ORGANISER){
            if(!(object.has(MLBService.JSONKey.SIGNIN_JSON) && object.getJSONObject(MLBService.JSONKey.SIGNIN_JSON).has(MLBService.JSONKey.PROFILE))) return;
            JSONObject profile = object.getJSONObject(MLBService.JSONKey.SIGNIN_JSON).getJSONObject(MLBService.JSONKey.PROFILE);
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.FIRST_NAME,  profile.getString(MLBService.JSONKey.FIRST_NAME));
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.SURNAME, profile.getString(MLBService.JSONKey.SURNAME));
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.POSTCODE, profile.getString(MLBService.JSONKey.POSTCODE));
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.DOB, profile.getString(MLBService.JSONKey.DOB));
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.GENDER, profile.getString(MLBService.JSONKey.GENDER));
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.PROF_POS, profile.getString(MLBService.JSONKey.PROF_POS));
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.EVENT_TYPE, profile.getString(MLBService.JSONKey.EVENT_TYPE));

        }else if(accountType == MLBService.AccountType.BARSTAFF){

            if(!(object.has(MLBService.JSONKey.SIGNIN_JSON) && object.getJSONObject(MLBService.JSONKey.SIGNIN_JSON).has(MLBService.JSONKey.PROFILE))) return;
            JSONObject profile = object.getJSONObject(MLBService.JSONKey.SIGNIN_JSON).getJSONObject(MLBService.JSONKey.PROFILE);
            ProfileBarstaffData.setProfileData(MLBService.JSONKey.FIRST_NAME,  profile.getString(MLBService.JSONKey.FIRST_NAME));
            ProfileBarstaffData.setProfileData(MLBService.JSONKey.SURNAME, profile.getString(MLBService.JSONKey.SURNAME));
            ProfileBarstaffData.setProfileData(MLBService.JSONKey.POSTCODE, profile.getString(MLBService.JSONKey.POSTCODE));
            ProfileBarstaffData.setProfileData(MLBService.JSONKey.DOB, profile.getString(MLBService.JSONKey.DOB));
            ProfileBarstaffData.setProfileData(MLBService.JSONKey.GENDER, profile.getString(MLBService.JSONKey.GENDER));
            String imagePath = profile.getString(MLBService.JSONKey.IMAGE_PATH);
            ProfileBarstaffData.setProfileData(MLBService.JSONKey.IMAGE_PATH ,imagePath);
        }else if(accountType == MLBService.AccountType.ADMIN){

        }
    }



    /**
     *  Function to transfrom active value to boolean
     * @param isActive
     * @return
     */
    public boolean getIsActive (int isActive){
        if( isActive <= 0) return false;
        else return true;
    }

    /**
     *
     *  Check for email validation
     *
     * @return Whether the email has a right format or not
     */
    private boolean emailValidation(){
        if(!AccessVerificationTools.isEmailFormatValid(this.emailEditTxt.getText().toString())){
           this.setErrorMsg("Email not valid");
            return false;
        }
        // Clean msg
        this.clearMsg();
        return true;
    }

    /**
     *  Set the error message to be displayed in the user feedback place holder
     *
     * @param msg The error message
     *
     */
    private void setErrorMsg(String msg){
        this.msgTxtView.setTextColor(Color.RED);
        this.msgTxtView.setText(msg);
    }


    /**
     *
     * Set the success message to be displayed in the user feedback place holder
     *
     * @param msg The success message
     */
    private void setOkMsg(String msg){
        this.msgTxtView.setTextColor(Color.GREEN);
        this.msgTxtView.setText(msg);
    }


    /**
     *  Reset the feedback place holde to an empty string
     */
    private void clearMsg(){
        this.msgTxtView.setText("");
    }

    /**
     *
     * @param type the string equivalent of the Account type ENUM ( case insensitive)
     * @return the ENUM type for the user type
     */
    private MLBService.AccountType getType(String type){
        if(type.equalsIgnoreCase(MLBService.AccountType.BARSTAFF.toString())){
            return MLBService.AccountType.BARSTAFF;
        }else if(type.equalsIgnoreCase(MLBService.AccountType.ORGANISER.toString())) {
            return MLBService.AccountType.ORGANISER;
        }else if(type.equalsIgnoreCase(MLBService.AccountType.ADMIN.toString())){
            return MLBService.AccountType.ADMIN;
        }else{
            return MLBService.AccountType.UNKNOWN;
        }
    }

    /**
     *
     * @param accountType The type of account to be instantiated
     * @param isActive Whether the account has already be filled with data or not
     */
    private void startProfile(MLBService.AccountType accountType, boolean isActive){
        if(accountType == MLBService.AccountType.ORGANISER){
            Log.i("TEST", "starting organiser");
            this.mlb.startOrganiserActiviy(isActive);
        }else if(accountType == MLBService.AccountType.BARSTAFF) {
            Log.i("TEST","starting barender");
            this.mlb.startBarStaffActivity(isActive);
        }else if(accountType == MLBService.AccountType.ADMIN){
            //TODO
            Log.e("UNKNOWN TYPE", MLBService.AccountType.UNKNOWN.toString());
        }
    }

    public String loadJSONFromAsset() {

        String json = null;
        try {
            InputStream is = mlb.getBaseContext().getAssets().open("jsontest.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}