package com.cavaliers.mylocalbartender.tools.cavs_widgets.date_picker;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TabHost;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class CavsDateTimePicker extends Dialog implements View.OnClickListener {

    private NumberPicker yearPicker;
    private NumberPicker monthPicker;
    private NumberPicker dayPicker;

    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker amPmPicker;

    private Button save;
    private Button cancel;
    private EditText dboField;

    private EditText year;
    private EditText month;
    private EditText day;
    private EditText hour;
    private EditText minute;

    private Fragment fragment;

    private static String formattedDateOfBirth = "";


    public CavsDateTimePicker(Context context, @Nullable EditText dboField, EditText year, EditText month, EditText day, EditText hour, EditText minute) {
        super(context);

        this.dboField = dboField;
        this.year =year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cavs_date_time_picker);

        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);

        tabHost.setup();

        TabHost.TabSpec spec=tabHost.newTabSpec("DATE");

        spec.setContent(R.id.firstOne);
        spec.setIndicator("DATE");
        tabHost.addTab(spec);

        spec=tabHost.newTabSpec("TIME");
        spec.setContent(R.id.secondOne);
        spec.setIndicator("TIME");
        tabHost.addTab(spec);

        setUp();
        show();
    }


    public void setUp(){
        //INITIALISE
        save = (Button) findViewById(R.id.bt_dialog_cavs_datepicker_save);
        cancel = (Button) findViewById(R.id.bt_dialog_cavs_datepicker_cancel);

        yearPicker = (NumberPicker) findViewById(R.id.np_dialog_cavs_datepicker_year);
        monthPicker = (NumberPicker) findViewById(R.id.np_dialog_cavs_datepicker_month);
        dayPicker = (NumberPicker) findViewById(R.id.np_dialog_cavs_datepicker_day);

        hourPicker = (NumberPicker) findViewById(R.id.np_dialog_cavs_datepicker_hour);
        minutePicker = (NumberPicker) findViewById(R.id.np_dialog_cavs_datepicker_minute);
        amPmPicker = (NumberPicker) findViewById(R.id.np_dialog_cavs_datepicker_amPm);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);

        //SET VALUES
        yearPicker.setMinValue(1942);
        yearPicker.setMaxValue(Calendar.getInstance().get(Calendar.YEAR));

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);

        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(getDays(yearPicker.getValue(),monthPicker.getValue()));

        monthPicker.setDisplayedValues( new String[] { "January", "February", "March" , "April", "May",
                "June", "July", "August", "September", "October", "November", "December"} );


        String[] hours = new String[13];
        String[] minutes = new String[60];

        for(int i=0;i<hours.length;++i){
            hours[i] = AccessVerificationTools.zeroAdder(String.valueOf(i));
        }
        for(int i=0;i<minutes.length;++i){
            minutes[i] = AccessVerificationTools.zeroAdder(String.valueOf(i));
        }



        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(12);
        hourPicker.setDisplayedValues(hours);

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setDisplayedValues(minutes);

        amPmPicker.setMinValue(0);
        amPmPicker.setMaxValue(1);

        amPmPicker.setDisplayedValues(new String[]{"AM","PM"});


//        hourPicker.setValue();


        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                dayPicker.setMaxValue(getDays(newVal,monthPicker.getValue()));
            }
        });

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                dayPicker.setMaxValue(getDays(yearPicker.getValue(),newVal));
            }
        });

    }

    private int getDays(int year, int month){
        Calendar calendar = new GregorianCalendar(year, month-1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onClick(View v) {

        Button button = (Button)v;

        if(button.getText().toString().equals("OK")){
//            dboField.setText(AccessVerificationTools.dateOrdiner(dayPicker.getValue(),monthPicker.getValue()-1, yearPicker.getValue()));
            formattedDateOfBirth = AccessVerificationTools.stringifyDate(dayPicker.getValue(),monthPicker.getValue(), yearPicker.getValue());
            System.out.println("SAVED="+AccessVerificationTools.dateFormatter(Integer.valueOf(dayPicker.getValue()),Integer.valueOf(monthPicker.getValue()), Integer.valueOf(yearPicker.getValue()),true)+" "+
                    AccessVerificationTools.timeFormatter(hourPicker.getValue(),minutePicker.getValue(),amPmPicker.getValue()));

            year.setText(yearPicker.getValue());
            month.setText(monthPicker.getValue());
            day.setText(dayPicker.getValue());
            hour.setText(hourPicker.getValue());
            minute.setText(minutePicker.getValue());

            dismiss();

        }else{
            System.out.println("DISMISSED");
            dismiss();
        }
    }


    public static String getFormattedDateOfBirth(){
        return formattedDateOfBirth;
    }
}
