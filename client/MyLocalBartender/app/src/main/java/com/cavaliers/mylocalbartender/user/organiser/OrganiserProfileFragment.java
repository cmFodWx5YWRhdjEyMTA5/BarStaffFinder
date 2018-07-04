package com.cavaliers.mylocalbartender.user.organiser;

import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.fragment.MLBFragment;

import com.cavaliers.mylocalbartender.server.SocketIO;
import com.cavaliers.mylocalbartender.stripe.StripeConfig;

import java.util.HashMap;

import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;
import com.cavaliers.mylocalbartender.user.UserFragment;
import com.cavaliers.mylocalbartender.user.organiser.model.ProfileOrganiserData;
import com.stripe.android.Stripe;
import com.stripe.android.exception.AuthenticationException;

import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;

public class OrganiserProfileFragment extends UserFragment {
    private EditText et_professional_pos;
    private EditText[] editTextArray = new EditText[8];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_organiser_profile, container, false);

        initWidget();

        initChksToValidate(et_first_name, et_last_name, et_DOB, et_post_code,
                et_professional_pos);

        //loadDetails();

        setSocketListener();
        setSaveButtonListener();
        fillProfilePage();
        setEditTextListener();
        //initStripeValidate(et_card_number, et_expire_month, et_expire_year, et_cvc);
        return rootView;
    }

    @Override
    protected void setSocketListener(){
        MLBService.setSocketEvent(new SocketIO.ProfileChangeListener(){

            @Override
            public void onResponse(JSONObject object) {
                Toast.makeText(OrganiserProfileFragment.this.rootView.getContext(), object.toString(), Toast.LENGTH_LONG).show();

                //TODO UPDATE DATA OBJECT
            }
        });
    }


    public String getProfPos(){
        return this.et_professional_pos.getText().toString();
    }


    /* =========== Used for testing purposes ============
    public String loadJSONFromAsset() {

        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("jsonprofile.json");
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
    } // =========== End of test*/



    @Override
    protected void initWidget() {

        editTextArray[0] = this.et_first_name = (EditText) rootView.findViewById(R.id.et_first_name);
        editTextArray[1] = this.et_last_name = (EditText) rootView.findViewById(R.id.et_last_name);
        editTextArray[2] = this.et_DOB = (EditText) rootView.findViewById(R.id.et_dob);
        editTextArray[3] = this.et_post_code = (EditText) rootView.findViewById(R.id.et_postcode);
        editTextArray[4] = this.et_card_number = (EditText) rootView.findViewById(R.id.et_card_number);
        editTextArray[5] = this.et_expire_month = (EditText) rootView.findViewById(R.id.et_expire_month);
        editTextArray[6] = this.et_expire_year = (EditText) rootView.findViewById(R.id.et_expire_year);
        editTextArray[7] = this.et_cvc = (EditText) rootView.findViewById(R.id.et_cvc);
        this.et_professional_pos = (EditText) rootView.findViewById(R.id.et_professional_pos);

        this.spn_gender = (Spinner) rootView.findViewById(R.id.spn_gender);

        // Professional Information
        et_professional_pos = (EditText) rootView.findViewById(R.id.et_professional_pos); //optional field

        btn_save = (Button) rootView.findViewById(R.id.btn_save);

        initCommonWidgets();
        setSaveButtonListener();

    }

    @Override
    protected void setSaveButtonListener(){
        btn_save.setOnClickListener(new OrganiserProfileHandler(this){



            public void onClick(View v) {

                if (AccessVerificationTools.isEmpty(editTextArray, spn_gender) != 0) {

                    System.out.println("Can not Save.");

                }else{
                //TODO: Send information to sever
                System.out.println("Can Save.");
                }


            }
        });

    }


    private void eventTypes(){
        //TODO
        //CHECK BOXES SAVED DATA
    }

    @Override
    public void fillProfilePage(){
        this.et_first_name.setText(ProfileOrganiserData.get(MLBService.JSONKey.FIRST_NAME));
        this.et_last_name.setText(ProfileOrganiserData.get(MLBService.JSONKey.SURNAME));
        this.spn_gender.setSelection((ProfileOrganiserData.get(MLBService.JSONKey.GENDER)== "F")? 1 : 2);
        this.et_DOB.setText(ProfileOrganiserData.get(MLBService.JSONKey.DOB));
        this.et_post_code.setText(ProfileOrganiserData.get(MLBService.JSONKey.POSTCODE));
        this.et_professional_pos.setText((ProfileOrganiserData.get(MLBService.JSONKey.PROF_POS) == null ?
                "" : ProfileOrganiserData.get(MLBService.JSONKey.PROF_POS)));
        eventTypes();


    }
}