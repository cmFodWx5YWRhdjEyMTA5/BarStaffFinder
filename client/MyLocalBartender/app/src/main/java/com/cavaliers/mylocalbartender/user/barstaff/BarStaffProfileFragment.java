package com.cavaliers.mylocalbartender.user.barstaff;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;
import com.cavaliers.mylocalbartender.tools.cavs_widgets.availability_calendar.AvailabilityCalendarFragment;
import com.cavaliers.mylocalbartender.user.UserFragment;
import com.cavaliers.mylocalbartender.user.barstaff.model.ProfileBarstaffData;
import com.cavaliers.mylocalbartender.user.organiser.OrganiserProfileHandler;

import static android.app.Activity.RESULT_OK;

/**
 * Team Cavaliers
 */

public class BarStaffProfileFragment extends UserFragment implements View.OnClickListener {

    private ImageView iv_profile_picture;

    private EditText et_hourly_input;
    private EditText et_nightly_input;
    private EditText et_experience_input;
    private EditText et_speciality_input;
    private EditText et_shortsummary_input;
    private EditText et_proximity;
    private EditText et_description;

    private EditText[] editTextArray = new EditText[8];

    private Uri selectedImage;

    SharedPreferences savedBarDetails;
    private boolean isReloading;

    private static final int RESULT_LOAD_IMAGE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setRetainInstance(true);
        rootView = inflater.inflate(R.layout.fragment_bartender_profile, container, false);

        initWidget();


        initChksToValidate(et_first_name, et_last_name, et_DOB, et_post_code,
                et_nightly_input, et_hourly_input);

        setEditTextListener();
        setSaveButtonListener();

        savedBarDetails = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        isReloading = savedBarDetails.getBoolean("rState", false);

        iv_profile_picture = (ImageView) rootView.findViewById(R.id.iv_profile_picture); //casting to ImageView


        iv_profile_picture.setOnClickListener(new BarStaffProfileHandler(this));

        fillProfilePage();
        displayAvailability();

        return rootView;

    }


    private void displayAvailability() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        AvailabilityCalendarFragment availabilityCalendarFragment = new AvailabilityCalendarFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.profile_availability_frame, availabilityCalendarFragment)
                .commit();
    }

    @Override
    protected void setSocketListener() {
    }

    @Override
    protected void fillProfilePage() {
        MLBService.loadImage(ProfileBarstaffData.get(MLBService.JSONKey.IMAGE_PATH), new MLBService.MLBImageResponseListener() {
            @Override
            public void onResponse(Bitmap response) {
                BarStaffProfileFragment.this.iv_profile_picture.setImageBitmap(response);
            }

        }, new MLBService.MLBErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BarStaffProfileFragment.this.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        this.et_first_name.setText(ProfileBarstaffData.get(MLBService.JSONKey.FIRST_NAME));
        System.out.println("_________NAME ISSSS="+ProfileBarstaffData.get(MLBService.JSONKey.FIRST_NAME));
        this.et_last_name.setText(ProfileBarstaffData.get(MLBService.JSONKey.SURNAME));
        this.et_DOB.setText(ProfileBarstaffData.get(MLBService.JSONKey.DOB));
        this.spn_gender.setSelection((ProfileBarstaffData.get(MLBService.JSONKey.GENDER) == "F") ? 1 : 2);
        this.et_post_code.setText(ProfileBarstaffData.get(MLBService.JSONKey.POSTCODE));
        this.et_hourly_input.setText(ProfileBarstaffData.get(MLBService.JSONKey.HOUR_RATE));
        this.et_nightly_input.setText(ProfileBarstaffData.get(MLBService.JSONKey.NIGHT_RATE));
        this.et_experience_input.setText(ProfileBarstaffData.get(MLBService.JSONKey.EXPERIENCE));
        this.et_speciality_input.setText(ProfileBarstaffData.get(MLBService.JSONKey.SPECIALITY));
        this.et_proximity.setText(ProfileBarstaffData.get(MLBService.JSONKey.PROXIMITY));
        this.et_description.setText(ProfileBarstaffData.get(MLBService.JSONKey.DESCRIPTION));
    }


    /**
     * Initalise widgets on create.
     * <p>
     * Casts to their appropriate widget e.g. EditText, Spinner, Button
     * Adds releveant EditText fields to array.
     */
    @Override
    protected void initWidget() {

        et_first_name = (EditText) rootView.findViewById(R.id.et_first_name);
        et_last_name = (EditText) rootView.findViewById(R.id.et_last_name);
        et_DOB = (EditText) rootView.findViewById(R.id.et_dob);
        et_post_code = (EditText) rootView.findViewById(R.id.et_postcode);
        spn_gender = (Spinner) rootView.findViewById(R.id.spn_gender);
        et_proximity = (EditText) rootView.findViewById(R.id.et_proximity);

        et_experience_input = (EditText) rootView.findViewById(R.id.et_experience_input);
        et_hourly_input = (EditText) rootView.findViewById(R.id.et_hourly_input);
        et_nightly_input = (EditText) rootView.findViewById(R.id.et_nightly_input);
        et_speciality_input = (EditText) rootView.findViewById(R.id.et_speciality_input);

       // et_hourly_input = (EditText) rootView.findViewById(R.id.et_hourly_input);
       // et_nightly_input = (EditText) rootView.findViewById(R.id.et_nightly_input);
       // et_experience_input = (EditText) rootView.findViewById(R.id.et_experience_input);
       // et_speciality_input = (EditText) rootView.findViewById(R.id.et_speciality_input); //optional field so does not need to be added to array
        et_shortsummary_input = (EditText) rootView.findViewById(R.id.et_shortsummary_input); //optional field
        et_description = (EditText) rootView.findViewById(R.id.et_shortsummary_input);


        btn_save = (Button) rootView.findViewById(R.id.btn_save);
    }

    @Override
    protected void setSaveButtonListener(){
        btn_save.setOnClickListener(new BarStaffProfileHandler(this){

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

    //getters

    public Bitmap getImage() {
        return ((BitmapDrawable) this.iv_profile_picture.getDrawable()).getBitmap();
    }

    public String getHourlyRate() {
        return et_hourly_input.getText().toString();
    }

    public String getNightlyRate() {
        return et_nightly_input.getText().toString();
    }

    public String getExperience() {
        return et_experience_input.getText().toString();
    }

    public String getSpeciality() {
        return et_speciality_input.getText().toString();
    }

    public String getShortSummary() {
        return et_shortsummary_input.getText().toString();
    }

    public String getProximity() {
        return et_proximity.getText().toString();
    }
    //End of Getters


    /**
     * To allow the user to change their profile picture.
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */

    @Override
    public void onClick(View v) {

        /*switch(v.getId()){
>>>>>>> frontEndRefactor

            case R.id.iv_profile_picture:

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);

                break;

        }*/
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && !isReloading) {

            selectedImage = data.getData();

            iv_profile_picture.setImageURI(selectedImage);

        }
    }
}
