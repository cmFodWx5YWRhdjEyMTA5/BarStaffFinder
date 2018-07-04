package com.cavaliers.mylocalbartender.jobadvert.create;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.fragment.MLBFragment;
import com.cavaliers.mylocalbartender.jobadvert.handlers.JobCreateHandler;


/**
 * A simple {@link Fragment} subclass.
 */
public class JobCreateFragment extends MLBFragment {

    private View rootView;

    private EditText et_job_title;
    private EditText et_number;
    private EditText et_street;
    private EditText et_city;
    private EditText et_postcode;
    private EditText et_start_day_year;
    private EditText et_start_day_month;
    private EditText et_start_day_day;
    private EditText et_end_day_year;
    private EditText et_end_day_month;
    private EditText et_end_day_day;
    private EditText et_start_hr;
    private EditText et_start_min;
    private EditText et_end_hr;
    private EditText et_end_min;
    private EditText et_hour_duration;
    private EditText et_payrate;
    private EditText et_summary;
    private EditText et_event_type;
    private Spinner spn_speciality;
    private Spinner spn_startAmPm;
    private Spinner spn_endAmPm;


    private Button btn_post;
    private Context context;

    public JobCreateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView =  inflater.inflate(R.layout.fragment_create_jobadvert, container, false);


        et_job_title = (EditText) rootView.findViewById(R.id.et_job_title);

        // Location
        et_number = (EditText) rootView.findViewById(R.id.et_number);
        et_street = (EditText) rootView.findViewById(R.id.et_street);
        et_city = (EditText) rootView.findViewById(R.id.et_city);
        et_postcode = (EditText) rootView.findViewById(R.id.et_postcode);

        // Start Date and Time
        et_start_day_year = (EditText) rootView.findViewById(R.id.et_start_year);
        et_start_day_month = (EditText) rootView.findViewById(R.id.et_start_month);
        et_start_day_day = (EditText) rootView.findViewById(R.id.et_start_day);
        et_start_hr = (EditText) rootView.findViewById(R.id.et_start_hr);
        et_start_min = (EditText) rootView.findViewById(R.id.et_start_min);
        spn_startAmPm = (Spinner) rootView.findViewById(R.id.spn_start);

        // End Date and Time
        et_end_day_year = (EditText) rootView.findViewById(R.id.et_end_year);
        et_end_day_month = (EditText) rootView.findViewById(R.id.et_end_month);
        et_end_day_day = (EditText) rootView.findViewById(R.id.et_end_day);
        et_end_hr = (EditText) rootView.findViewById(R.id.et_end_hr);
        et_end_min = (EditText) rootView.findViewById(R.id.et_end_min);
        spn_endAmPm = (Spinner) rootView.findViewById(R.id.spn_end);


        // Duration and Pay Rate
        et_hour_duration = (EditText) rootView.findViewById(R.id.et_hour_duration);
        et_payrate = (EditText) rootView.findViewById(R.id.et_payrate);

        // Event Type
        et_event_type = (EditText) rootView.findViewById(R.id.et_event_type);

        // Speciality spinner should go here

        // Summary
        et_summary = (EditText) rootView.findViewById(R.id.et_summary);


        setETListenerH(et_start_hr);
        setETListenerM(et_start_min);

        setETListenerH(et_end_hr);
        setETListenerM(et_end_min);

        // Digit limit - Start
        et_start_day_day.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
        et_start_day_month.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
        et_start_day_year.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
        // Digit limit - End
        et_end_day_day.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
        et_end_day_month.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
        et_end_day_year.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });


        spn_speciality = (Spinner) rootView.findViewById(R.id.spn_speciality);


        initWidget();
        setPostButtonListener();
        // Inflate the layout for this fragment
        return rootView;
    }

    private void setETListenerH(final EditText editText){

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }


            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if((charSequence.length() > 0) && (charSequence.toString()  != "")) {
                    int value = Integer.parseInt(charSequence.toString());
                    if (value > 12) {
                        editText.setText("12");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if((editable.length() > 0) && (editable.toString()  != "")) {
                    int value = Integer.parseInt(editable.toString());
                    if (value > 12) {
                        editText.setText("12");

                    }
                }
            }
        });
    }

    private void setETListenerM(final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if((charSequence.length() > 0) && (charSequence.toString()  != "")) {
                    int value = Integer.parseInt(charSequence.toString());
                    if (value > 60) {
                        editText.setText("59");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if((editable.length() > 0) && (editable.toString()  != "")) {
                    int value = Integer.parseInt(editable.toString());
                    if (value > 60) {
                        editText.setText("59");
                    }
                }
            }
        });
    }

    private void initWidget(){
        et_job_title = (EditText) rootView.findViewById(R.id.et_job_title);
        et_number = (EditText) rootView.findViewById(R.id.et_number);
        et_street = (EditText) rootView.findViewById(R.id.et_street);
        et_city = (EditText) rootView.findViewById(R.id.et_city);
        et_postcode = (EditText) rootView.findViewById(R.id.et_postcode);

        // Start Date
        et_start_day_year = (EditText) rootView.findViewById(R.id.et_start_year);
        et_start_day_month = (EditText) rootView.findViewById(R.id.et_start_month);
        et_start_day_day = (EditText) rootView.findViewById(R.id.et_start_day);

        // Duration and Pay Rate
        et_hour_duration = (EditText) rootView.findViewById(R.id.et_hour_duration);
        et_payrate = (EditText) rootView.findViewById(R.id.et_payrate);

        // Event Type
        et_event_type = (EditText) rootView.findViewById(R.id.et_event_type);

        // Summary
        et_summary = (EditText) rootView.findViewById(R.id.et_summary);

        // Post
        btn_post = (Button) rootView.findViewById(R.id.btn_post);

    }

    private void setPostButtonListener(){
        btn_post.setOnClickListener(new JobCreateHandler(getContext(),et_job_title,et_number,et_street, et_city,et_postcode,
                        et_start_day_year,et_start_day_month,et_start_day_day, et_end_day_year, et_end_day_month, et_end_day_day,
                        et_start_hr,et_start_min, spn_startAmPm, et_end_hr,et_end_min, spn_endAmPm,
                        et_hour_duration,et_payrate,et_event_type,spn_speciality,et_summary));



    }



}
