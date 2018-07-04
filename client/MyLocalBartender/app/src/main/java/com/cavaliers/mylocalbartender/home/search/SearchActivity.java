package com.cavaliers.mylocalbartender.home.search;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.activity.MLBActivity;

import java.util.ArrayList;


public class SearchActivity extends MLBActivity {

    //Advanced Filter
    private CheckBox checkbox_birthday;
    private CheckBox checkbox_wedding;
    private CheckBox checkbox_dinnerparty;
    private CheckBox checkbox_other;
    private CheckBox[] checkboxArray = new CheckBox[4];
    private ArrayList<String> selectedCheckbox;
    private EditText location_input;
    private EditText distance_input;
    private SeekBar skb_payrate;
    private TextView minimum_pay_display;

    private EditText dayDate;
    private TextView date_divider1;
    private EditText monthDate;
    private TextView date_divider2;
    private EditText yearDate;

    private Button btn_apply;
    private Button btn_reset;
    private String inputValue = "";
    public static Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_seach);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbaro);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Back");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SearchView searchView = (SearchView) findViewById(R.id.search_toolbar);
        searchView.setIconifiedByDefault(false);


    }
}
