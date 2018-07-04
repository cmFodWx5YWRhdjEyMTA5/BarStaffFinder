package com.cavaliers.mylocalbartender.forms;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.server.MLBService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by JamesRich on 25/03/2017.
 */

public class ActivityJobCompleteForm extends Activity{

    private CheckBox turnedUpOnTimeBox;
    private CheckBox jobCompletedBox;

    private RadioGroup ratingRadioGroup;
    private RadioButton selectedRadioButton;

    private String text;
    private Button confirmButton;
    private Button cancelButton;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstance){

        super.onCreate(savedInstance);
        setContentView(R.layout.activity_job_complete_form);

        new ArrayList<RadioButton>();
        text = ((EditText) findViewById(R.id.et_jobcomplete_message)).getText().toString();

        initWidgets();
        setListeners();

    }

    private void initWidgets(){

        turnedUpOnTimeBox = (CheckBox) findViewById(R.id.cb_jobcomplete_arrived);

        jobCompletedBox = (CheckBox) findViewById(R.id.cb_jobcompleted);

        ratingRadioGroup = (RadioGroup)findViewById(R.id.et_jobcomplete_radiogroup);

        confirmButton = (Button) findViewById(R.id.bt_jobcomplete_confirm);

        cancelButton = (Button) findViewById(R.id.bt_jobcomplete_cancel);

    }

    private void setListeners(){

        confirmButton.setOnClickListener(new ButtonHandler());
        cancelButton.setOnClickListener(new ButtonHandler());

    }

    private class ButtonHandler implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.bt_jobcomplete_confirm:

                    int selectedId = ratingRadioGroup.getCheckedRadioButtonId();
                    selectedRadioButton = (RadioButton)findViewById(selectedId);
                    int radioButtonValue = Integer.parseInt(selectedRadioButton.getText().toString());

                    JSONObject jsonObject = new JSONObject();

                    try {

                        jsonObject.put("Punctuality", turnedUpOnTimeBox.isChecked());
                        jsonObject.put("Completion", jobCompletedBox.isChecked());
                        jsonObject.put("rating", radioButtonValue);
                        jsonObject.put("Feedback:", text);

                    } catch (JSONException jsonEx){

                        jsonEx.printStackTrace();

                    }

                    try {

                        MLBService.sendRequest(context,
                                MLBService.ACTION.SOCKET_JOB_COMPLETED,
                                new MLBService.MLBResponseListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                    }
                                },
                                new MLBService.MLBErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }
                                ,jsonObject, MLBService.getLoggedInUserID()
                        );

                    } catch (VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }


            default:

                finish();

            }
        }

    }

}
