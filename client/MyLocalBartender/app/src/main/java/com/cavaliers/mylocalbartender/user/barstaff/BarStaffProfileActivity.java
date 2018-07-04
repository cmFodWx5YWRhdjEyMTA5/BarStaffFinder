package com.cavaliers.mylocalbartender.user.barstaff;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.home.search.model.AdvertData;
import com.cavaliers.mylocalbartender.menu.bartender_menu.BartenderMenuActivity;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.server.SocketIO;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.user.UserActivity;
import com.cavaliers.mylocalbartender.user.barstaff.model.ProfileBarstaffData;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by CG on 16/03/2017.
 */

public class BarStaffProfileActivity extends UserActivity  {

    private static final int RESULT_LOAD_IMAGE = 1;

    private CircleImageView iv_profile_picture;

    private EditText init_et_hourly_input;
    private EditText init_et_nightly_input;
    private EditText init_et_experience_input;
    private EditText init_et_speciality_input;
    private EditText init_et_shortsummary_input;

    private Uri selectedImage;
    private boolean isReloading;


    public static void loadProfile(){
        System.out.println( "___________THE OBJECT IS==="+ProfileBarstaffData.get(MLBService.JSONKey.FIRST_NAME));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_bartender_profile);
        super.onCreate(savedInstanceState);

        System.out.println("CURRENT ACTIVITY:  "+ Tools.GetCurrentActivityName());

        setDOB(ProfileBarstaffData.get(MLBService.JSONKey.DOB));

        MLBService.setSocketEvent(new SocketIO.ProfileChangeListener(){
            @Override
            public void onResponse(JSONObject object) {
                Toast.makeText(BarStaffProfileActivity.this, "Profile has changed", Toast.LENGTH_LONG).show();
            }
        });

        MLBService.setSocketEvent(new SocketIO.CardUpdatedListener() {
            @Override
            public void onResponse(JSONObject object) {
                Toast.makeText(BarStaffProfileActivity.this, "ALL DONE", Toast.LENGTH_LONG).show();
            }
        });

        MLBService.setSocketEvent(new SocketIO.CompletedProfileListener(){
            @Override
            public void onResponse(JSONObject object) {
                try {
                    Tools.accountType = MLBService.AccountType.BARSTAFF;
                    AdvertData.load(object.getJSONObject("signin_json"));
                    Tools.loadUserData(object);
                    BarStaffProfileActivity.this.startIntent();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        setImageViewListener();
        loadProfile();
    }

    private void setDOB(String DOB){
        this.init_et_DOB.setText(DOB);
        this.init_et_DOB.setEnabled(false);
    }

    @Override
    protected void initWidget(){
        init_et_first_name = (EditText) findViewById(R.id.et_first_name);
        init_et_last_name = (EditText) findViewById(R.id.et_last_name);
        init_et_DOB = (EditText) findViewById(R.id.et_dob);
        init_et_postcode = (EditText) findViewById(R.id.et_postcode);
        init_spn_gender = (Spinner) findViewById(R.id.spn_gender);


        init_et_hourly_input = (EditText) findViewById(R.id.et_hourly_input);
        init_et_nightly_input = (EditText) findViewById(R.id.et_nightly_input);
        init_et_experience_input = (EditText) findViewById(R.id.et_experience_input);

        init_et_speciality_input = (EditText) findViewById(R.id.et_speciality_input); //optional field so does not need to be added to array
        init_et_shortsummary_input = (EditText) findViewById(R.id.et_shortsummary_input); //optional field

        init_btn_next = (Button) findViewById(R.id.btn_bartender_next);

        et_card_number = (EditText) findViewById(R.id.et_card_number);
        et_expire_month = (EditText) findViewById(R.id.et_expire_month);
        et_expire_year = (EditText) findViewById(R.id.et_expire_year);
        et_cvc  = (EditText) findViewById(R.id.et_cvc);


        init_editTextArray = editTextsValidation(init_et_first_name,
                init_et_last_name, init_et_postcode,
                init_et_hourly_input, init_et_nightly_input,
                et_card_number, et_cvc,
                et_expire_month, et_expire_year);
    }

    @Override
    protected void startIntent() {
        Intent menu = new Intent(BarStaffProfileActivity.this, BartenderMenuActivity.class);
        startActivity(menu);
    }

    private void setImageViewListener(){


        System.out.println("test");
       iv_profile_picture = (CircleImageView) findViewById(R.id.iv_profile_picture);

        iv_profile_picture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {


            selectedImage = data.getData();
            System.out.println("image loaded");
            iv_profile_picture.setImageURI(selectedImage);

        }
    }


    public Bitmap getBitmap(){
        if(selectedImage != null){
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(
                        selectedImage);
                System.out.println("imageStream " + imageStream);
                return BitmapFactory.decodeStream(imageStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("null image");
            return null;
        }
        return null;
    }

    @Override
    protected void completeFields(){

        JSONObject completeProfileObj = null;

        try {
            completeProfileObj = MLBService.RequestBuilder.buildJSONRequest(
                    new Pair<>(MLBService.JSONKey.FIRST_NAME, init_et_first_name.getText().toString()),
                    new Pair<>(MLBService.JSONKey.SURNAME, init_et_last_name.getText().toString()),
                    new Pair<>(MLBService.JSONKey.DOB, getDOB()),
                    new Pair<>(MLBService.JSONKey.IMAGE_PATH, MLBService.ImageUploader.encode(getBitmap())),
                    new Pair<>(MLBService.JSONKey.POSTCODE, init_et_postcode.getText().toString()),
                    new Pair<>(MLBService.JSONKey.HOUR_RATE, init_et_hourly_input.getText().toString()),
                    new Pair<>(MLBService.JSONKey.NIGHT_RATE, init_et_nightly_input.getText().toString()),
                    new Pair<>(MLBService.JSONKey.EXPERIENCE, init_et_experience_input.getText().toString()),
                    new Pair<>(MLBService.JSONKey.SPECIALITY, init_et_speciality_input.getText().toString()),
                    new Pair<>(MLBService.JSONKey.DESCRIPTION, init_et_shortsummary_input.getText().toString()),
                    new Pair<>(MLBService.JSONKey.GENDER, BarStaffProfileActivity.this.getGender()),
                    new Pair<>(MLBService.JSONKey.STRIPE_TOKEN, "")
            );

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            MLBService.sendRequest(this, MLBService.ACTION.COMPLETE_BARTENDER_PROFILE, this, this, completeProfileObj, MLBService.getLoggedInUserID());
        } catch (VolleyError volleyError) {
            volleyError.printStackTrace();
        }

    }
}
