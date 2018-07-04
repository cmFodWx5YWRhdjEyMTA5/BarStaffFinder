package com.cavaliers.mylocalbartender.user.organiser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.home.search.model.AdvertData;
import com.cavaliers.mylocalbartender.menu.organiser_menu.OrganiserMenuActivity;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.server.SocketIO;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;
import com.cavaliers.mylocalbartender.user.UserActivity;
import com.cavaliers.mylocalbartender.user.barstaff.BarStaffProfileActivity;
import com.cavaliers.mylocalbartender.user.organiser.model.ProfileOrganiserData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CG on 17/03/2017.
 */

public class OrganiserProfileActivity extends UserActivity {

    private ImageView userProfilePicture;

    private EditText init_et_professional_pos;

    private CheckBox init_chk_wedding; //Need method to validate checkboxes
    private CheckBox init_chk_birthday_party;
    private CheckBox init_chk_dinner_party;
    private CheckBox init_chk_other;


    // Unique identifiers for asynchronous requests:
    private static final int LOAD_MASKED_WALLET_REQUEST_CODE = 1000;
    private static final int LOAD_FULL_WALLET_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_organiser_profile);
        super.onCreate(savedInstanceState);

        setDOB(ProfileOrganiserData.get(MLBService.JSONKey.DOB));

        MLBService.setSocketEvent(new SocketIO.ProfileChangeListener(){
            @Override
            public void onResponse(JSONObject object) {
                Toast.makeText(OrganiserProfileActivity.this, "Profile has changed", Toast.LENGTH_LONG).show();
            }
        });

        MLBService.setSocketEvent(new SocketIO.CardUpdatedListener() {
            @Override
            public void onResponse(JSONObject object) {
                Toast.makeText(OrganiserProfileActivity.this, "ALL DONE", Toast.LENGTH_LONG).show();

            }
        });

        MLBService.setSocketEvent(new SocketIO.CompletedProfileListener(){
            @Override
            public void onResponse(JSONObject object) {
                try {
                    Tools.accountType = MLBService.AccountType.ORGANISER;
                    AdvertData.load(object.getJSONObject("signin_json"));
                    Tools.loadUserData(object);
                    OrganiserProfileActivity.this.startIntent();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

        init_et_professional_pos = (EditText) findViewById(R.id.et_professional_pos); //optional field
        init_chk_wedding = (CheckBox) findViewById(R.id.chk_wedding);
        init_chk_birthday_party = (CheckBox) findViewById(R.id.chk_birthday_party);
        init_chk_dinner_party = (CheckBox) findViewById(R.id.chk_dinner_party);
        init_chk_other = (CheckBox) findViewById(R.id.chk_other);

        et_card_number = (EditText) findViewById(R.id.et_card_number);
        et_expire_month = (EditText) findViewById(R.id.et_expire_month);
        et_expire_year = (EditText) findViewById(R.id.et_expire_year);
        et_cvc  = (EditText) findViewById(R.id.et_cvc);

        init_btn_next = (Button) findViewById(R.id.btn_organiser_next);


        init_editTextArray = editTextsValidation(init_et_first_name,
                init_et_last_name, init_et_postcode,
                et_card_number, et_cvc,
                et_expire_month, et_expire_year);

        init_checkBoxes = checkBoxesArray(init_chk_wedding, init_chk_birthday_party, init_chk_dinner_party, init_chk_other);
    }

    @Override
    protected void completeFields() {

        JSONObject completeProfileObj = null;
        try {
            completeProfileObj = MLBService.RequestBuilder.buildJSONRequest(
                    new Pair<>(MLBService.JSONKey.FIRST_NAME, getValue(init_et_first_name)),
                    new Pair<>(MLBService.JSONKey.SURNAME, getValue(init_et_last_name)),
                    new Pair<>(MLBService.JSONKey.GENDER, getGender()),
                    new Pair<>(MLBService.JSONKey.POSTCODE, getValue(init_et_postcode)),
                    new Pair<>(MLBService.JSONKey.PROF_POS, optionalGetValue(init_et_professional_pos)), // field
                    new Pair<>(MLBService.JSONKey.EVENT_TYPE, init_chk_wedding.getText().toString()),
                    new Pair<>(MLBService.JSONKey.STRIPE_TOKEN, "")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            MLBService.sendRequest(this, MLBService.ACTION.COMPLETE_ORGANISER_PROFILE, this, this, completeProfileObj, MLBService.getLoggedInUserID());
        } catch (VolleyError volleyError) {
            volleyError.printStackTrace();
        }
    }

    @Override
    protected void startIntent(){
        Intent menu = new Intent(OrganiserProfileActivity.this, OrganiserMenuActivity.class);
        startActivity(menu);
    }
    

}
