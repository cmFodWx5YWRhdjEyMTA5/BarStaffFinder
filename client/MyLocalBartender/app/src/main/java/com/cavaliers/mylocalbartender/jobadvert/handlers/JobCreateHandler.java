package com.cavaliers.mylocalbartender.jobadvert.handlers;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;

import org.json.JSONException;
import org.json.JSONObject;

import static com.cavaliers.mylocalbartender.server.MLBService.JSONKey.BUILDING_NUMBER;
import static com.cavaliers.mylocalbartender.server.MLBService.JSONKey.CITY;
import static com.cavaliers.mylocalbartender.server.MLBService.JSONKey.DESCRIPTION;
import static com.cavaliers.mylocalbartender.server.MLBService.JSONKey.DURATION;
import static com.cavaliers.mylocalbartender.server.MLBService.JSONKey.TYPE;
import static com.cavaliers.mylocalbartender.server.MLBService.JSONKey.JOB_END;
import static com.cavaliers.mylocalbartender.server.MLBService.JSONKey.STREET;
import static com.cavaliers.mylocalbartender.server.MLBService.JSONKey.JOB_RATE;
import static com.cavaliers.mylocalbartender.server.MLBService.JSONKey.JOB_START;
import static com.cavaliers.mylocalbartender.server.MLBService.JSONKey.POSTCODE;
import static com.cavaliers.mylocalbartender.server.MLBService.JSONKey.SPECIALITY;
import static com.cavaliers.mylocalbartender.server.MLBService.JSONKey.TITLE;
import static com.cavaliers.mylocalbartender.server.MLBService.JSONKey.USER_ID;

/**
 * Created by SimoneJRSharpe on 16/03/2017.
 */

public class JobCreateHandler implements View.OnClickListener{

    private EditText et_job_title;
    // Location
    private EditText et_number;
    private EditText et_street;
    private EditText et_city;
    private EditText et_postcode;

    // Start Date and Time
    private EditText et_start_day_year;
    private EditText et_start_day_month;
    private EditText et_start_day_day;
    private EditText et_start_hr;
    private EditText et_start_min;

    // End Date and Time
    private EditText et_end_day_year;
    private EditText et_end_day_month;
    private EditText et_end_day_day;
    private EditText et_end_hr;
    private EditText et_end_min;

    // Duration and Pay Rate
    private EditText et_hour_duration;
    private EditText et_payrate;
    private EditText et_summary;

    //Event Type
    private  EditText et_event_type;

    // Speciality
    private Spinner spn_speciality;
    private Spinner spn_startAmPm;
    private Spinner spn_endAmPm;


    private EditText[] editTextArray;



    private Context context;


    public JobCreateHandler(Context context, EditText et_job_title, EditText et_number, EditText et_street, EditText et_city, EditText et_postcode,
                            EditText et_start_day_year,EditText et_start_day_month,EditText et_start_day_day,
                            EditText et_end_day_year, EditText et_end_day_month, EditText et_end_day_day,
                            EditText et_start_hr,EditText et_start_min, Spinner spn_startAmPm, EditText et_end_hr, EditText et_end_min,Spinner spn_endAmPm,
                            EditText et_hour_duration, EditText et_payrate,
                            EditText et_event_type,Spinner spn_speciality, EditText et_summary){

        this.editTextArray = new EditText[15];
        // Job Titile
        this.editTextArray[0] = this.et_job_title = et_job_title;

        // Location
        this.editTextArray[1] = this.et_number = et_number;
        this.editTextArray[2] = this.et_street = et_street;
        this.editTextArray[3] = this.et_city = et_city;
        this.editTextArray[4] = this.et_postcode = et_postcode;

        // Date and Time
        this.et_start_day_year = et_start_day_year;
        this.et_start_day_month = et_start_day_month;
        this.et_start_day_day = et_start_day_day;
        this.et_start_hr = et_start_hr;
        this.et_start_min = et_start_min;
        this.spn_startAmPm = spn_startAmPm;

        this.editTextArray[5] = this.et_start_day_year;
        this.editTextArray[6] = this.et_start_day_month;
        this.editTextArray[7] = this.et_start_day_day;

        // End Date and Time

        this.et_end_day_year = et_end_day_year;
        this.et_end_day_month = et_end_day_month;
        this.et_end_day_day = et_end_day_day;

        this.editTextArray[8] = this.et_end_day_year;
        this.editTextArray[9] = this.et_end_day_month;
        this.editTextArray[10] = this.et_end_day_day;
        this.et_end_hr = et_end_hr;
        this.et_end_min = et_end_min;
        this.spn_endAmPm = spn_endAmPm;


        // Duration and Pay Rate
        this.et_hour_duration = et_hour_duration;
        this.et_payrate = et_payrate;
        this.et_summary = et_summary;
        this.editTextArray[11] = this.et_hour_duration;
        this.editTextArray[12] = this.et_payrate;
        this.editTextArray[13] = this.et_summary;

        //Event Type
        this.et_event_type = et_event_type;
        this.editTextArray[14] = this.et_event_type;

        this.context = context;

        this.spn_speciality = spn_speciality;



        setEditTextListener();
    }



    private void setEditTextListener(){
        for(EditText entry : editTextArray){ //loops through all entries of the array
            if(entry != null) {
                AccessVerificationTools.setEditTextListener(entry); //calls method in AccessVerificationTools
            }
        }
    }

    private String buildStartDateTime(){
        return AccessVerificationTools.dateZeroAdder(et_start_day_year.getText().toString(),et_start_day_month.getText().toString(),et_start_day_day.getText().toString(),
                et_start_hr.getText().toString(),et_start_min.getText().toString(),spn_startAmPm.getSelectedItem().toString());
    }

    private String buildEndDateTime(){
        return AccessVerificationTools.dateZeroAdder(et_end_day_year.getText().toString(),et_end_day_month.getText().toString(),et_end_day_day.getText().toString(),
                et_end_hr.getText().toString(),et_end_min.getText().toString(),spn_endAmPm.getSelectedItem().toString());
    }

    @Override
    public void onClick(View view) {

        if (AccessVerificationTools.isEmpty(editTextArray, null) != 0) {
                    Toast.makeText(context, "Job from is not completed", Toast.LENGTH_SHORT).show();
        }else {
            try {
                JSONObject obj = MLBService.RequestBuilder.buildJSONRequest(
                        new Pair<>(USER_ID, MLBService.getLoggedInUserID()),
                        new Pair<>(TITLE, et_job_title.getText().toString()),
                        new Pair<>(POSTCODE, et_postcode.getText().toString()),
                        new Pair<>(BUILDING_NUMBER, et_number.getText().toString()),
                        new Pair<>(STREET, et_street.getText().toString()),
                        new Pair<>(CITY, et_city.getText().toString()),
                        new Pair<>(JOB_START, buildStartDateTime()), // start time
                        new Pair<>(JOB_END, buildEndDateTime()),// end time
                        new Pair<>(DURATION, et_hour_duration.getText().toString()),
                        new Pair<>(JOB_RATE, et_payrate.getText().toString()),
                        new Pair<>(TYPE, et_event_type.getText().toString()),
                        new Pair<>(SPECIALITY, spn_speciality.getSelectedItem().toString()),
                        new Pair<>(DESCRIPTION, et_summary.getText().toString())
                );

                try {

                    System.out.println(obj);
                    MLBService.sendRequest(context, MLBService.ACTION.CREATE_PUBLIC_JOB, new MLBService.MLBResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Toast.makeText(context, response.getString(MLBService.JSONKey.MESSAGE), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new MLBService.MLBErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }, obj);
                } catch (VolleyError volleyError) {
                    volleyError.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }




}
