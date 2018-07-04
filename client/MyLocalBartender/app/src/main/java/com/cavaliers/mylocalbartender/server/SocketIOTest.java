package com.cavaliers.mylocalbartender.server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.ServerTest;
import com.cavaliers.mylocalbartender.activity.MLBActivity;
import com.cavaliers.mylocalbartender.tools.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SocketIOTest extends MLBActivity{

    public static MLBService.AccountType currentType = MLBService.AccountType.ORGANISER;

    public static int mID = -1;

    private static int jobs = -1;

    private Button make_job;
    private LinearLayout top_panel, job_panel, private_panel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socketio_test_login);

        top_panel = (LinearLayout) findViewById(R.id.button_panel);
        job_panel = (LinearLayout) findViewById(R.id.t_job_panel);
        private_panel = (LinearLayout) findViewById(R.id.user_panel);
        make_job = (Button) findViewById(R.id.t_make_job);

        if(currentType.equals(MLBService.AccountType.BARSTAFF)) setBarStaffMode();
        else setOrganiserMode();

       /* MLBService.setTestEventListener(new SocketIO.EventAdapter() {

            @Override
            public void onJobApplied(JSONObject object) {

                Toast.makeText(SocketIOTest.this, object.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNewJobAvailable(JSONObject object) {

                try {

                    Toast.makeText(SocketIOTest.this, object.toString(), Toast.LENGTH_SHORT).show();

                    Button button = new Button(SocketIOTest.this);
                    button.setText("{ org_ID: " + object.getInt("org_ID") + ", job_id: "+object.getInt("job_ID") + " } apply");

                    addJobToView(object.getInt("org_ID"), object.getInt("job_ID"), button);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

           @Override
            public void onBarStaffJoined(final JSONObject object) {

                try {
                    Log.i(Tools.MLB_SERVICE_TEST_TAG, object.toString());
                    Toast.makeText(SocketIOTest.this, object.toString(), Toast.LENGTH_SHORT).show();

                    Button button = new Button(SocketIOTest.this);
                    button.setText("{ user_ID: " + object.getInt("user_ID") + "}");

                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            try {

                                if(jobs > -1 ) {

                                    JSONObject data = new JSONObject();

                                    data.put("user_ID", object.getInt("user_ID"));
                                    data.put("org_ID", mID);
                                    data.put("job_ID", Integer.parseInt((""+(mID)) + (""+(jobs))));

                                    MLBService.emit(SocketIO.Event.SEND_INVITE, data);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    addToPrivateView(button);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBarStaffInvite(final JSONObject object) {

                try {

                    Log.i(Tools.MLB_SERVICE_TEST_TAG, object.toString());
                    Toast.makeText(SocketIOTest.this, object.toString(), Toast.LENGTH_SHORT).show();

                    final Button button = new Button(SocketIOTest.this);

                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            button.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    try {

                                        JSONObject data = new JSONObject();

                                        data.put("user_ID", mID);
                                        data.put("org_ID", object.getInt("org_ID"));
                                        data.put("job_ID", object.getInt("job_ID"));

                                        MLBService.emit(SocketIO.Event.ACCEPT_JOB_INVITE, data);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });

                    button.setText("{ org_ID: " + object.getInt("org_ID") + ", job_ID: "+object.getInt("job_ID") + " } apply");
                    addToPrivateView(button);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBarStaffAcceptedInvite(JSONObject object) {

                Log.i(Tools.MLB_SERVICE_TEST_TAG, object.toString());
                Toast.makeText(SocketIOTest.this, object.toString(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void setBarStaffMode(){

        make_job.setVisibility(View.GONE);
        top_panel.invalidate();
    }

    private void setOrganiserMode(){

        make_job.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                addJob();
            }
        });
    }

    /**
     * The action that happens if the back button was pressed
     */
    @Override
    public void onBackPressed() {

        MLBService.signOut(); //sign out the user
        MLBService.disconnectSocket();
        Intent menu = new Intent(this, ServerTest.class);
        this.startActivity(menu);
    }

    private void addJob(){

        String event_id = (""+(mID)) + (""+(++jobs));

        Button button = new Button(this);
        button.setText("{ org_ID: " + mID + ", job_id: "+ event_id + " }");

        addJobToView(mID, Integer.parseInt(event_id), button);

        JSONObject object = new JSONObject();
        try {

            object.put(MLBService.JSONKey.ORG_ID, mID);
            object.put("job_ID", event_id);
            Log.i(Tools.MLB_SERVICE_TEST_TAG,"job message: "+ object.toString());
            MLBService.emit(SocketIO.Event.NEW_JOB, object);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addJobToView(final int org_id, final int event_id, Button button){

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(currentType.equals(MLBService.AccountType.BARSTAFF)){

                    Toast.makeText(view.getContext(), "applying for job: " + event_id, Toast.LENGTH_SHORT).show();

                    JSONObject object = new JSONObject();
                    try {

                        object.put("user_ID", mID);
                        object.put("org_ID", org_id);
                        object.put("job_ID", event_id);

                        MLBService.emit(SocketIO.Event.APPLY_TO_JOB, object);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Tools.addToViewGroupLayout(job_panel, button, getLayoutParam());
    }

    private void addToPrivateView(Button button){

        Tools.addToViewGroupLayout(private_panel, button, getLayoutParam());
    }

    private LinearLayout.LayoutParams getLayoutParam(){

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) Tools.convertPixelsToDp(20,this),0,0); //20dp margin at top per message added
        return params;
    }
}
