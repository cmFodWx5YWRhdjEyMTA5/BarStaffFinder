package com.cavaliers.mylocalbartender.home.review;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.activity.MLBActivity;

public class JobDescriptionActivity extends MLBActivity {

    public static final String IMAGE = "image";
    public static final String DESCRIPTION = "description";
    public static final String TYPE = "type";
    public static final String TITLE = "title";
    public static final String DURATION = "duration";
    public static final String TIME = "time";
    public static final String DATE = "date";
    public static final String DAYRATE = "dayrate";
    public static final String NIGHTRATE = "nightrate";
    public static final String USERNAME = "username";
    public static final String SPECIALITY1 = "speciality1";
    public static final String SPECIALITY2 = "speciality2";
    public static final String SPECIALITY3 = "speciality3";
    public static final String SPECIALITY4 = "speciality4";
    public static final String LOCATIION = "location";
    public static final String USER_ID = "user_Id";

    private ImageView thumbnail;
    private TextView username, speciality1, speciality2, speciality3, speciality4,
            dayrate, nightrate, date, time, duration, location, description;
    private TextView type;
    private CollapsingToolbarLayout collapsingToolbarLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_description_job);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Back");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setCustomView(R.layout.search_action_bar);
//

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.tv_full_job_collapse_title);

        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        username = (TextView) findViewById(R.id.tv_full_job_username);

        speciality1 = (TextView) findViewById(R.id.tv_full_job_speciality1);
        speciality2 = (TextView) findViewById(R.id.tv_full_job_speciality2);
        speciality3 = (TextView) findViewById(R.id.tv_full_job_speciality3);
        speciality4 = (TextView) findViewById(R.id.tv_full_job_speciality4);

        dayrate = (TextView) findViewById(R.id.tv_full_job_dayrate);
        nightrate = (TextView) findViewById(R.id.tv_full_job_nightrate);

        date = (TextView) findViewById(R.id.tv_full_job_date);
        time = (TextView) findViewById(R.id.tv_full_job_time);
        duration = (TextView) findViewById(R.id.tv_full_job_duration);

        type = (TextView) findViewById(R.id.tv_full_job_type);

        location = (TextView) findViewById(R.id.tv_full_job_location);
        description = (TextView) findViewById(R.id.tv_full_job_description);

        System.out.println("LET'S TRY_________");


        if (getIntent() != null && getIntent().getExtras() != null) {
            System.out.println("NOT NULL_________");

            if (getIntent().getExtras().containsKey(IMAGE)) { //value
                //LOAD
            }
            if (getIntent().getExtras().containsKey(TITLE)) { //value
                collapsingToolbarLayout.setTitle(getIntent().getExtras().getString(TITLE));
            }
            if (getIntent().getExtras().containsKey(USERNAME)) { //value
                System.out.println("USERNAME EXISTS_________ as"+getIntent().getExtras().getString(USERNAME));
                username.setText(getIntent().getExtras().getString(USERNAME));
            }
            if (getIntent().getExtras().containsKey(DAYRATE)) { //value
                dayrate.setText(getIntent().getExtras().getString(DAYRATE));
            }
            if (getIntent().getExtras().containsKey(NIGHTRATE) && getIntent().getExtras().getString(NIGHTRATE) != null) { //value
                nightrate.setText(getIntent().getExtras().getString(NIGHTRATE));
            }else{
                nightrate.setText(getIntent().getExtras().getString(DAYRATE));
            }
            if (getIntent().getExtras().containsKey(DATE)) { //value
                date.setText(getIntent().getExtras().getString(DATE));
            }
            if (getIntent().getExtras().containsKey(TIME)) { //value
                time.setText(getIntent().getExtras().getString(TIME));
            }
            if (getIntent().getExtras().containsKey(DURATION)) { //value
                duration.setText(getIntent().getExtras().getString(DURATION));
            }
            if (getIntent().getExtras().containsKey(SPECIALITY1)) { //value
                speciality1.setText(getIntent().getExtras().getString(SPECIALITY1));
            }
            if (getIntent().getExtras().containsKey(SPECIALITY2)) { //value
                speciality2.setText(getIntent().getExtras().getString(SPECIALITY2));
            }
            if (getIntent().getExtras().containsKey(SPECIALITY3)) { //value
                speciality3.setText(getIntent().getExtras().getString(SPECIALITY3));
            }
            if (getIntent().getExtras().containsKey(SPECIALITY4)) { //value
                speciality4.setText(getIntent().getExtras().getString(SPECIALITY4));
            }
            if (getIntent().getExtras().containsKey(TYPE)) { //value
                 type.setText(getIntent().getExtras().getString(TYPE));
            }
            if (getIntent().getExtras().containsKey(DESCRIPTION) && getIntent().getExtras().getString(DESCRIPTION) != null) { //value
                description.setText(getIntent().getExtras().getString(DESCRIPTION));
            }
            if (getIntent().getExtras().containsKey(LOCATIION)) { //value
                location.setText(getIntent().getExtras().getString(LOCATIION));
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }


    public JobDescriptionActivity() {}

}
