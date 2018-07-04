package com.cavaliers.mylocalbartender.tools.cavs_widgets.availability_calendar;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cavaliers.mylocalbartender.R;


public class AvailabilityButtonHandler implements View.OnClickListener {


    private Context context;

    private LinearLayout ll_availability_root_layout;

    private TextView tv_avail_day;
    private EditText et_startTime;
    private EditText et_startTime_minutes;
    private EditText et_endTime;
    private EditText et_endTime_minutes;

    private ToggleButton tb_startTime;
    private ToggleButton tb_endTime;

    private Button bt_cancel;
    private Button bt_save;

    private TextView tv_msg;
    private int height;



    private AvailabilityDay availabilityDay;
    private AvailabilityCalendarAdapter availabilityCalendarAdapter;

    public AvailabilityButtonHandler(AvailabilityDay availabilityDay, Context context, AvailabilityCalendarAdapter availabilityCalendarAdapter){
        this.availabilityDay = availabilityDay;
        this.context = context;
        this.availabilityCalendarAdapter = availabilityCalendarAdapter;
    }

    @Override
    public void onClick(View v) {
        displayAvailability();
    }


    private void displayAvailability(){

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_availability_calendar);
        setupWidgets(dialog);
        dialog.show();

    }



    public void setupWidgets(final Dialog dialog){

        //INIT COMPONENTS
        this.ll_availability_root_layout = (LinearLayout) dialog.findViewById(R.id.ll_availability_root_layout);

        this.tv_avail_day = (TextView) dialog.findViewById(R.id.tv_availabil_dailog_day);
        this.et_startTime = (EditText) dialog.findViewById(R.id.et_availabil_dailog_start);
        this.et_startTime_minutes = (EditText) dialog.findViewById(R.id.et_availabil_dailog_start_minutes);

        this.et_endTime = (EditText) dialog.findViewById(R.id.et_availabil_dailog_end);
        this.et_endTime_minutes = (EditText) dialog.findViewById(R.id.et_availabil_dailog_end_minutes);

        this.tb_startTime = (ToggleButton) dialog.findViewById(R.id.tb_dialog_startTime);
        this.tb_endTime = (ToggleButton) dialog.findViewById(R.id.tb_dialog_endTime);

        this.bt_save = (Button) dialog.findViewById(R.id.bt_dialog_save);
        this.bt_cancel = (Button) dialog.findViewById(R.id.bt_dialog_cancel);

        this.tv_msg = (TextView) dialog.findViewById(R.id.tv_availabil_dailog_msg);


        height = ll_availability_root_layout.getLayoutParams().height+60;

        String startHour = availabilityDay.getStartTime().split(":")[0];
        String startMinute = availabilityDay.getStartTime().split(":")[1];

        String endHour =  availabilityDay.getEndTime().split(":")[0];
        String endMinute =  availabilityDay.getEndTime().split(":")[1];

                //SET TEXT
        this.tv_avail_day.setText(availabilityDay.getDay());

        this.et_startTime.setText(startHour);
        this.et_startTime_minutes.setText(startMinute);

        this.et_endTime.setText(endHour);
        this.et_endTime_minutes.setText(endMinute);


        //SET CHECKED
        this.tb_startTime.setChecked(setTimeRelation(this.availabilityDay.getStartTimeRelation()));
        this.tb_endTime.setChecked(setTimeRelation(this.availabilityDay.getEndTimeRelation()));


        //SET TEXT WATCHERS
        this.et_startTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("") && Integer.valueOf(s.toString()) > 12){
                    et_startTime.setText("12");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        this.et_startTime_minutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("") && Integer.valueOf(s.toString()) > 59){
                    et_startTime_minutes.setText("59");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





        this.et_endTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("") && Integer.valueOf(s.toString()) > 12){
                    et_endTime.setText("12");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        this.et_endTime_minutes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("") && Integer.valueOf(s.toString()) > 59){
                    et_endTime_minutes.setText("59");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if((!et_startTime.getText().toString().equals("") && Integer.parseInt(et_startTime.getText().toString()) != 0 )
                        && (!et_endTime.getText().toString().equals("") && Integer.parseInt(et_endTime.getText().toString()) != 0 )
                        && (Integer.parseInt(et_startTime.getText().toString()) <= 12 &&Integer.parseInt(et_endTime.getText().toString()) <= 12 )
                        && (Integer.parseInt(et_startTime_minutes.getText().toString()) <= 59 &&Integer.parseInt(et_endTime_minutes.getText().toString()) <= 59 )) {

                    availabilityDay.setStartTime(et_startTime.getText().toString()+":"+et_startTime_minutes.getText().toString());
                    availabilityDay.setStartTimeRelation(tb_startTime.getText().toString());

                    availabilityDay.setEndTime(et_endTime.getText().toString()+":"+et_endTime_minutes.getText().toString());
                    availabilityDay.setEndTimeRelation(tb_endTime.getText().toString());

                    availabilityCalendarAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else {

                    System.out.println("CAN'T BE LEFT EMPTY");

                    ll_availability_root_layout.getLayoutParams().height = height;
                    tv_msg.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    public boolean setTimeRelation(String relation){
       return relation.equals("AM");
    }

}
