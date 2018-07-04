package com.cavaliers.mylocalbartender.home.review;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.activity.MLBActivity;

/**
 * Created by DrenediesInk on 09/03/2017.
 */

public class ProfileDescriptionActivity extends MLBActivity {


    public static final String USER_ID = "user_Id";
    public static final String IMAGE = "image";
    public static final String USERNAME = "username";

    public static final String DAYRATE = "dayrate";
    public static final String NIGHTRATE = "nightrate";

    public static final String SPECIALITY1 = "speciality1";
    public static final String SPECIALITY2 = "speciality2";
    public static final String SPECIALITY3 = "speciality3";
    public static final String SPECIALITY4 = "speciality4";

    public static final String AVAIL_MONDAY = "monday";
    public static final String AVAIL_TUESDAY = "tuesday";
    public static final String AVAIL_WEDNESDAY = "wednesday";
    public static final String AVAIL_THURSDAY = "thursday";
    public static final String AVAIL_FRIDAY = "friday";
    public static final String AVAIL_SATURDAY = "saturday";
    public static final String AVAIL_SUNDAY = "sunday";

    public static final String DESCRIPTION = "description";

    public static final String LOCATIION = "location";

    public static final String MESSAGE = "message";


    private ImageView thumbnail;
    private TextView dayrate, message, nightrate, speciality1, speciality2, speciality3, speciality4,
            avail_monday, avail_tuesday, avail_wednesday, avail_thursday,
            avail_friday, avail_saturday, avail_sunday, location, description;
    private CollapsingToolbarLayout collapsingToolbarLayout;


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_description_profile);
        getSupportActionBar().setTitle("Back");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.tv_full_profile_collapse_title);

        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        dayrate = (TextView) findViewById(R.id.tv_full_profile_dayrate);
        nightrate = (TextView) findViewById(R.id.tv_full_profile_nightrate);

        message = (TextView) findViewById(R.id.tv_description_message);


        speciality1 = (TextView) findViewById(R.id.tv_full_profile_speciality1);
        speciality2 = (TextView) findViewById(R.id.tv_full_profile_speciality2);
        speciality3 = (TextView) findViewById(R.id.tv_full_profile_speciality3);
        speciality4 = (TextView) findViewById(R.id.tv_full_profile_speciality4);

        avail_monday = (TextView) findViewById(R.id.tv_full_profile_avail_monday);
        avail_tuesday = (TextView) findViewById(R.id.tv_full_profile_avail_tuesday);
        avail_wednesday = (TextView) findViewById(R.id.tv_full_profile_avail_wednesday);
        avail_thursday = (TextView) findViewById(R.id.tv_full_profile_avail_thursday);
        avail_friday = (TextView) findViewById(R.id.tv_full_profile_avail_friday);
        avail_saturday = (TextView) findViewById(R.id.tv_full_profile_avail_saturday);
        avail_sunday = (TextView) findViewById(R.id.tv_full_profile_avail_sunday);

//        location = (TextView) findViewById(R.id.tv_full_profile_location);
        description = (TextView) findViewById(R.id.tv_full_profile_description);


        if (getIntent() != null && getIntent().getExtras() != null) {
            System.out.println("NOT NULL_________");

            if (getIntent().getExtras().containsKey(IMAGE)) { //value
                //LOAD
            }
            if (getIntent().getExtras().containsKey(USERNAME)) { //value
                System.out.println("USERNAME EXISTS_________ as" + getIntent().getExtras().getString(USERNAME));
                collapsingToolbarLayout.setTitle(getIntent().getExtras().getString(USERNAME));
            }
            if (getIntent().getExtras().containsKey(DAYRATE)) { //value
                dayrate.setText(getIntent().getExtras().getString(DAYRATE));
            }
            if (getIntent().getExtras().containsKey(NIGHTRATE) && getIntent().getExtras().getString(NIGHTRATE) != null) { //value
                nightrate.setText(getIntent().getExtras().getString(NIGHTRATE));
            } else {
                nightrate.setText(getIntent().getExtras().getString(DAYRATE));
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

            if (getIntent().getExtras().containsKey(AVAIL_MONDAY)) { //value
                avail_monday.setText(getIntent().getExtras().getString(AVAIL_MONDAY));
            }
            if (getIntent().getExtras().containsKey(AVAIL_TUESDAY)) { //value
                avail_tuesday.setText(getIntent().getExtras().getString(AVAIL_TUESDAY));
            }
            if (getIntent().getExtras().containsKey(AVAIL_WEDNESDAY)) { //value
                avail_wednesday.setText(getIntent().getExtras().getString(AVAIL_WEDNESDAY));
            }
            if (getIntent().getExtras().containsKey(AVAIL_THURSDAY)) { //value
                avail_thursday.setText(getIntent().getExtras().getString(AVAIL_THURSDAY));
            }
            if (getIntent().getExtras().containsKey(AVAIL_FRIDAY)) { //value
                avail_friday.setText(getIntent().getExtras().getString(AVAIL_FRIDAY));
            }
            if (getIntent().getExtras().containsKey(AVAIL_SATURDAY)) { //value
                avail_saturday.setText(getIntent().getExtras().getString(AVAIL_SATURDAY));
            }
            if (getIntent().getExtras().containsKey(AVAIL_SUNDAY)) { //value
                avail_sunday.setText(getIntent().getExtras().getString(AVAIL_SUNDAY));
            }
            if (getIntent().getExtras().containsKey(MESSAGE)) { //value
                message.setText(getIntent().getExtras().getString(MESSAGE));
            }



            if (getIntent().getExtras().containsKey(DESCRIPTION) && !getIntent().getExtras().getString(DESCRIPTION).equals("")) { //value
                description.setText(getIntent().getExtras().getString(DESCRIPTION));
            }
//            if (getIntent().getExtras().containsKey(LOCATIION)) { //value
//                location.setText(getIntent().getExtras().getString(LOCATIION));
//            }

        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }


}



