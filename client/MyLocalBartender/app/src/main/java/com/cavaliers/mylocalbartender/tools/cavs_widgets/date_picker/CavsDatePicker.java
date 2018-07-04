package com.cavaliers.mylocalbartender.tools.cavs_widgets.date_picker;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class CavsDatePicker extends Dialog implements View.OnClickListener {

    private NumberPicker yearPicker;
    private NumberPicker monthPicker;
    private NumberPicker dayPicker;
    private Button save;
    private Button cancel;
    private EditText dboField;
    private Fragment fragment;

    private static String formattedDateOfBirth = "";


    public CavsDatePicker(Context context, EditText dboField) {
        super(context);

        this.dboField = dboField;


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cavs_date_picker);
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
            dboField.setText(AccessVerificationTools.dateOrdiner(dayPicker.getValue(),monthPicker.getValue()-1, yearPicker.getValue()));
            formattedDateOfBirth = AccessVerificationTools.stringifyDate(dayPicker.getValue(),monthPicker.getValue(), yearPicker.getValue());
            System.out.println("SAVED="+formattedDateOfBirth);
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
