package com.cavaliers.mylocalbartender.jobadvert.update;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.jobadvert.modify.JobModifyAdapter;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

import org.json.JSONException;
import org.json.JSONObject;

public class JobUpdateActivity extends AppCompatActivity {

    static Advert the_advert;
    //city missing
    //speciality missing
    //status missing
    //jobID,staffID,orgID

    String startDBDate;

    String startDBTime;
    String endDBDate;
    String endDBTime;

    String job_title;
    String build_number;
    String street_name;
    String postcode;

    String duration;
    String payrate;
    String event_type;
    String description;

    public static void setAdvert(Advert advert) {
        the_advert = advert;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_update_jobadvert);

        getSupportActionBar().setTitle("Back");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText et_job_title = (EditText) findViewById(R.id.et_job_title);

        final EditText et_number = (EditText) findViewById(R.id.et_number);
        final EditText et_street = (EditText) findViewById(R.id.et_street);
        final EditText et_postcode = (EditText) findViewById(R.id.et_postcode);


        EditText et_start_year = (EditText) findViewById(R.id.et_start_year);
        EditText et_start_month = (EditText) findViewById(R.id.et_start_month);
        EditText et_start_day = (EditText) findViewById(R.id.et_start_day);

        EditText et_start_hr = (EditText) findViewById(R.id.et_start_hr);
        EditText et_start_min = (EditText) findViewById(R.id.et_start_min);
        Spinner spn_start = (Spinner) findViewById(R.id.spn_start); //AM/PM


        EditText et_end_year = (EditText) findViewById(R.id.et_end_year);
        EditText et_end_month = (EditText) findViewById(R.id.et_end_month);
        EditText et_end_day = (EditText) findViewById(R.id.et_end_day);

        EditText et_end_hr = (EditText) findViewById(R.id.et_end_hr);
        EditText et_end_min = (EditText) findViewById(R.id.et_end_min);
        Spinner spn_end = (Spinner) findViewById(R.id.spn_end); //AM/PM

        final EditText et_hour_duration = (EditText) findViewById(R.id.et_hour_duration);
        EditText et_payrate = (EditText) findViewById(R.id.et_payrate);
        final EditText et_city = (EditText) findViewById(R.id.et_city);
        final EditText et_event_type = (EditText) findViewById(R.id.et_event_type);
        final Spinner spn_speciality = (Spinner) findViewById(R.id.spn_speciality);

        final EditText et_summary = (EditText) findViewById(R.id.et_summary);

        Button updateButton = (Button)findViewById(R.id.btn_update);


        setETListenerH(et_start_hr);
        setETListenerM(et_start_min);

        setETListenerH(et_end_hr);
        setETListenerM(et_end_min);


         job_title = the_advert.getValue(MLBService.JSONKey.EVENT_TITLE);
        System.out.println("__________THIS IS EVENT=" + the_advert.getValue(MLBService.JSONKey.TYPE));
         build_number = the_advert.getValue(MLBService.JSONKey.BUILDING_NUMBER);
         street_name = the_advert.getValue(MLBService.JSONKey.STREET);
         postcode = the_advert.getValue(MLBService.JSONKey.POSTCODE);

        String startDate = the_advert.getValue(MLBService.JSONKey.JOB_START);
        String startDay = AccessVerificationTools.getParsedDay(startDate);
        String startMonth = AccessVerificationTools.getParsedMonth(startDate);
        String startYear = AccessVerificationTools.getParsedYear(startDate);

        System.out.println("__________THIS IS DATE=" + startDate);
        System.out.println("__________THIS IS YEAR=" + startYear);
        System.out.println("__________THIS IS MONTH=" + startMonth);
        System.out.println("__________THIS IS DAY=" + startDay);

        String startHour = AccessVerificationTools.getParsedHour(startDate);
        String startMinute = AccessVerificationTools.getParsedMin(startDate);
        String startAmPm = startDate;


        String endDate = the_advert.getValue(MLBService.JSONKey.JOB_END);
        String endDay = AccessVerificationTools.getParsedDay(endDate);
        String endMonth = AccessVerificationTools.getParsedMonth(endDate);
        String endYear = AccessVerificationTools.getParsedYear(endDate);
        String endHour = AccessVerificationTools.getParsedHour(endDate);
        String endMinute = AccessVerificationTools.getParsedMin(endDate);
        String endAmPm = AccessVerificationTools.getAmPmfromDate(endDate);

        startDBDate = AccessVerificationTools.dateFormatter(Integer.valueOf(startDay), Integer.valueOf(startMonth), Integer.valueOf(startYear), true);
        startDBTime = AccessVerificationTools.getParsedTime(startDate,true);

        endDBDate = AccessVerificationTools.dateFormatter(Integer.valueOf(startDay), Integer.valueOf(startMonth), Integer.valueOf(startYear), true);
        endDBTime = AccessVerificationTools.getParsedTime(endDate,true);

        duration = the_advert.getValue(MLBService.JSONKey.DURATION);
        payrate = the_advert.getValue(MLBService.JSONKey.JOB_RATE);
        event_type = the_advert.getValue(MLBService.JSONKey.TYPE);
        description = the_advert.getValue(MLBService.JSONKey.DESCRIPTION);


        et_job_title.setText(job_title);
        et_number.setText(build_number);
        et_street.setText(street_name);
        et_postcode.setText(postcode);

        et_start_day.setText(startDay);
        et_start_month.setText(startMonth);
        et_start_year.setText(startYear);

        et_start_hr.setText(startHour);
        et_start_min.setText(startMinute);

        int startSpinnerPosition = ((ArrayAdapter) spn_start.getAdapter()).getPosition(startAmPm);

        spn_start.setSelection(startSpinnerPosition);

        int endSpinnerPosition = ((ArrayAdapter) spn_end.getAdapter()).getPosition(endAmPm);

        spn_end.setSelection(endSpinnerPosition);
        //SPINNER HERE

        et_end_day.setText(endDay);
        et_end_month.setText(endMonth);
        et_end_year.setText(endYear);

        et_end_hr.setText(endHour);
        et_end_min.setText(endMinute);

        //SPINNER HERE

        et_hour_duration.setText(duration);
        et_payrate.setText(payrate);
        et_event_type.setText(event_type);
        et_summary.setText(description);


//        spn_speciality.setSelection(3);

        // If the selection iso


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                the_advert.put(MLBService.JSONKey.TITLE, et_job_title.getText().toString());
                the_advert.put(MLBService.JSONKey.JOB_START, getStartDateTime());
                the_advert.put(MLBService.JSONKey.JOB_END, getEndDateTime());
                the_advert.put(MLBService.JSONKey.DURATION, et_hour_duration.getText().toString());
                the_advert.put(MLBService.JSONKey.POSTCODE, et_postcode.getText().toString());
                the_advert.put(MLBService.JSONKey.STREET, et_street.getText().toString());
                the_advert.put(MLBService.JSONKey.BUILDING_NUMBER, et_number.getText().toString());
                the_advert.put(MLBService.JSONKey.DESCRIPTION, et_summary.getText().toString());
                the_advert.put(MLBService.JSONKey.SPECIALITY, spn_speciality.getSelectedItem().toString());
                the_advert.put(MLBService.JSONKey.TYPE, et_event_type.getText().toString());

                System.out.println("TITLE=" + the_advert.getValue(MLBService.JSONKey.TITLE));//, et_job_title.getText().toString());
                System.out.println("JOB START=" + the_advert.getValue(MLBService.JSONKey.JOB_START));//, getStartDateTime());
                System.out.println("JOB END=" + the_advert.getValue(MLBService.JSONKey.JOB_END));//, getEndDateTime());
                System.out.println("DURATION=" + the_advert.getValue(MLBService.JSONKey.DURATION));//, et_hour_duration.getText().toString());
                System.out.println("POSTCODE=" + the_advert.getValue(MLBService.JSONKey.POSTCODE));//, et_postcode.getText().toString());
                System.out.println("STREET=" + the_advert.getValue(MLBService.JSONKey.STREET));// et_street.getText().toString());
                System.out.println("BUILDING N=" + the_advert.getValue(MLBService.JSONKey.BUILDING_NUMBER));//, et_number.getText().toString());
                System.out.println("DESCRIPTION=" + the_advert.getValue(MLBService.JSONKey.DESCRIPTION));//, et_job_title.getText().toString());
                System.out.println("SPECIALITY=" + the_advert.getValue(MLBService.JSONKey.SPECIALITY));//, spn_speciality.getSelectedItem().toString());
                System.out.println("TYPE=" + the_advert.getValue(MLBService.JSONKey.TYPE));//, et_event_type.getText().toString());

                JSONObject j = null;
                try {
                    j = MLBService.RequestBuilder.buildJSONRequest(
                            new Pair<>(MLBService.JSONKey.TITLE, the_advert.getValue(MLBService.JSONKey.TITLE)),
                            new Pair<>(MLBService.JSONKey.JOB_END, the_advert.getValue(MLBService.JSONKey.JOB_END)),
                            new Pair<>(MLBService.JSONKey.JOB_START, the_advert.getValue(MLBService.JSONKey.JOB_START)),
                            new Pair<>(MLBService.JSONKey.DURATION, the_advert.getValue(MLBService.JSONKey.DURATION)),
                            new Pair<>(MLBService.JSONKey.POSTCODE, the_advert.getValue(MLBService.JSONKey.POSTCODE)),
                            new Pair<>(MLBService.JSONKey.STREET, the_advert.getValue(MLBService.JSONKey.POSTCODE)),
                            new Pair<>(MLBService.JSONKey.BUILDING_NUMBER, the_advert.getValue(MLBService.JSONKey.BUILDING_NUMBER)),
                            new Pair<>(MLBService.JSONKey.CITY, the_advert.getValue(MLBService.JSONKey.CITY)),
                            new Pair<>(MLBService.JSONKey.DESCRIPTION, the_advert.getValue(MLBService.JSONKey.DESCRIPTION)),
                            new Pair<>(MLBService.JSONKey.SPECIALITY, the_advert.getValue(MLBService.JSONKey.SPECIALITY)),
                            new Pair<>(MLBService.JSONKey.TYPE, the_advert.getValue(MLBService.JSONKey.TYPE)),
                            new Pair<>(MLBService.JSONKey.RATE, the_advert.getValue(MLBService.JSONKey.RATE)),
                            new Pair<>(MLBService.JSONKey.JOB_ID, the_advert.getValue(MLBService.JSONKey.JOB_ID))
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    MLBService.sendRequest(
                            getBaseContext(),
                            MLBService.ACTION.UPDATE_ORGANISER_PUBLIC_JOB,
                            new MLBService.MLBResponseListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Toast.makeText(getBaseContext(), response.getString(MLBService.JSONKey.MESSAGE), Toast.LENGTH_LONG).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new MLBService.MLBErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }, j, MLBService.getLoggedInUserID(), the_advert.getValue(MLBService.JSONKey.JOB_ID));
                } catch (VolleyError volleyError) {
                    volleyError.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void setETListenerH(final EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }


            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ((charSequence.length() > 0) && (charSequence.toString() != "")) {
                    int value = Integer.parseInt(charSequence.toString());
                    if (value > 12) {
                        editText.setText("12");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if ((editable.length() > 0) && (editable.toString() != "")) {
                    int value = Integer.parseInt(editable.toString());
                    if (value > 12) {
                        editText.setText("12");

                    }
                }
            }
        });
    }

    private void setETListenerM(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ((charSequence.length() > 0) && (charSequence.toString() != "")) {
                    int value = Integer.parseInt(charSequence.toString());
                    if (value > 60) {
                        editText.setText("59");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if ((editable.length() > 0) && (editable.toString() != "")) {
                    int value = Integer.parseInt(editable.toString());
                    if (value > 60) {
                        editText.setText("59");
                    }
                }
            }
        });
    }


    private String getStartDateTime(){
        return startDBDate+" "+startDBTime;
    }

    private String getEndDateTime(){
        return endDBDate+" "+endDBTime;
    }

        public String getPostcode() {
        return postcode;
    }

    public String getDuration() {
        return duration;
    }

    public String getPayrate() {
        return payrate;
    }

    public String getEvent_type() {
        return event_type;
    }

    public String getDescription() {
        return description;
    }

}
