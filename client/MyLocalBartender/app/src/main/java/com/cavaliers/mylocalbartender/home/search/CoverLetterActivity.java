package com.cavaliers.mylocalbartender.home.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.activity.MLBActivity;
import com.cavaliers.mylocalbartender.home.search.handlers.ApplyButtonHandler;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

public class CoverLetterActivity extends MLBActivity{


    public static final String TITLE = "title";
    public static final String ORGANISER = "organiser";
    public static final String PAYRATE = "payrate";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String DURATION= "duration";
    public static final String LOCATION= "location";
    public static final String ID = "id";
    AlertDialog dialog;
    private static Advert advert;

    TextView tv_title, tv_organiser, tv_payrate
            , tv_date, tv_time, tv_location, tv_duration;
    EditText et_message;
    Button bt_submit, bt_cancel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cover_letter);
        getSupportActionBar().setTitle("Back");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initWidgets();



        if (getIntent() != null && getIntent().getExtras() != null) {
            System.out.println("NOT NULL_________");

            if (getIntent().getExtras().containsKey(TITLE)) { //value
                tv_title.setText(getIntent().getExtras().getString(TITLE));
                System.out.println("+++++++++++++++++INSIDE TITLE IS= "+getIntent().getExtras().getString(TITLE));
            }
            if (getIntent().getExtras().containsKey(ORGANISER)) { //value
                tv_organiser.setText(getIntent().getExtras().getString(ORGANISER));
                System.out.println("+++++++++++++++++INSIDE ORGANISER IS= "+getIntent().getExtras().getString(ORGANISER));
            }
            if (getIntent().getExtras().containsKey(PAYRATE)) { //value
                tv_payrate.setText(getIntent().getExtras().getString(PAYRATE));
                System.out.println("+++++++++++++++++INSIDE PAYRATE IS= "+getIntent().getExtras().getString(PAYRATE));
            }
            if (getIntent().getExtras().containsKey(DATE)) { //value
                tv_date.setText(getIntent().getExtras().getString(DATE));
                System.out.println("+++++++++++++++++INSIDE DATE IS= "+getIntent().getExtras().getString(DATE));
            }
            if (getIntent().getExtras().containsKey(TIME)) { //value
                tv_time.setText(getIntent().getExtras().getString(TIME));
                System.out.println("+++++++++++++++++INSIDE TIME IS= "+getIntent().getExtras().getString(TIME));
            }
            if (getIntent().getExtras().containsKey(DURATION)) { //value
                tv_duration.setText(getIntent().getExtras().getString(DURATION));
                System.out.println("+++++++++++++++++INSIDE DURATION IS= "+getIntent().getExtras().getString(DURATION));
            }

            if (getIntent().getExtras().containsKey(LOCATION)) { //value
                tv_location.setText(getIntent().getExtras().getString(LOCATION));
                System.out.println("+++++++++++++++++INSIDE DURATION IS= "+getIntent().getExtras().getString(DURATION));
            }
            if (getIntent().getExtras().containsKey(ID)) { //value
                bt_submit.setTag(getIntent().getExtras().getString(ID));
                System.out.println("+++++++++++++++++INSIDE ID IS= "+getIntent().getExtras().getString(ID));
            }
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CoverLetterActivity.this);
        dialog = alertBuilder.create();

        et_message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_submit.setOnClickListener(new ApplyButtonHandler(advert, CoverLetterActivity.this, et_message));

    }



    private void initWidgets(){
        tv_title = (TextView) findViewById(R.id.tv_submit_title);

        tv_organiser = (TextView) findViewById(R.id.tv_submit_organiser);

        tv_payrate = (TextView) findViewById(R.id.tv_submit_payrate);

        tv_date = (TextView) findViewById(R.id.tv_submit_date);

        tv_time = (TextView) findViewById(R.id.tv_submit_time);

        tv_duration = (TextView) findViewById(R.id.tv_submit_duration);

        tv_location = (TextView) findViewById(R.id.tv_submit_location);

        et_message = (EditText) findViewById(R.id.et_submit_message);

        bt_submit = (Button) findViewById(R.id.bt_submit_send);

        bt_cancel = (Button) findViewById(R.id.bt_submit_cancel);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }


    public static void getAdvert(Advert adverto){
        advert = adverto;
    }


}
