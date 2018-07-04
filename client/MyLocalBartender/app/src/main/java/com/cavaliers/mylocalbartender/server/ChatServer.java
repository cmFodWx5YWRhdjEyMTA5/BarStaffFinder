package com.cavaliers.mylocalbartender.server;

import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.drm.DrmManagerClient;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.activity.MLBActivity;
import com.cavaliers.mylocalbartender.server.chat.ChatAdapter;
import com.cavaliers.mylocalbartender.server.chat.ChatData;
import com.cavaliers.mylocalbartender.startedservices.MessageService;
import com.cavaliers.mylocalbartender.tools.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatServer extends MLBActivity {

    private final String TAG = "ChatServer";
    private String job_ID, user_ID, staff_id, org_id;

    private LinearLayout button_pane;
    private Socket socket;

    private boolean offerReceived;
    private ChatAdapter chatAdapter;

    private EditText messageInput;
    private Button btnMessageSend;

    private ListView chatListView;

    //NOGOTIATION STUFF
    private Button btnOffer;
    private Button btnAccept;
    private RadioButton hourly;
    private RadioButton sum;
    private EditText etOffer;

    /**
     * setup socket for later use
     *
     */
    { socket = MLBService.socket; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offerReceived = false;
        user_ID = getIntent().getExtras().getString(MLBService.JSONKey.USER_ID);
        job_ID = getIntent().getStringExtra(MLBService.JSONKey.JOB_ID);
        if(Tools.accountType.equals(MLBService.AccountType.ORGANISER)){
            org_id = MLBService.getLoggedInUserID();
        } else if (Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {
            org_id = user_ID;
            staff_id = user_ID;
            staff_id = MLBService.getLoggedInUserID();
        }


        Tools.DB_CONNECTION.addDataToChat(5, "4", MLBService.getLoggedInUserID(), "Test message", new Date());
        Tools.DB_CONNECTION.addDataToChat(5, "4", MLBService.getLoggedInUserID(), "Test message 2", new Date());


        MLBService.setSocketEvent(new SocketIO.ContractMadeListener() {

            @Override
            public void onResponse(JSONObject object) {

                btnAccept.setEnabled(false);
                btnOffer.setEnabled(false);
                etOffer.setEnabled(false);
                hourly.setEnabled(false);
                sum.setEnabled(false);
                if(Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {
                    btnAccept.setText("COMPLETE JOB");
                    btnAccept.setEnabled(true);
                }
            }
        });

        MLBService.setSocketEvent(new SocketIO.JobPayRateOfferReceivedListener() {
            @Override
            public void onResponse(JSONObject object) {
                System.out.println("obj: "+object);
                try {
                    String responseMessage = (String) ((JSONObject) object.get("chat_message")).get("chat_message");
                    String[] splitMessage = responseMessage.split(" ");
                    System.out.println("||||||||||||||||||||| " + responseMessage.split(" made offer of ")[1]);
                    boolean isHourly = splitMessage[splitMessage.length - 1].equals("true");
                    String offer = splitMessage[splitMessage.length - 3];
                    if(!responseMessage.split(" made offer of ")[0].equals(MLBService.getLoggedInUserID())){
                        etOffer.setText(offer);
                        offerReceived = true;
                        btnAccept.setEnabled(offerReceived);
                        hourly.setChecked(isHourly);
                        sum.setChecked(!isHourly);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });




        System.out.println(".\n.");
        System.out.println("-------------------------------->JOB ID: " + job_ID);
        System.out.println("-------------------------------->USER ID: " + user_ID);
        System.out.println("-------------------------------->MY ID: " + MLBService.getLoggedInUserID());
        System.out.println(".\n.");

        setContentView(R.layout.activity_chat);
        chatAdapter = new ChatAdapter(this, R.layout.message_layout);
        startChat(user_ID);





        //SET EVENT LISTENER

        Log.i(TAG,"CONNECTED WITH SOCKET TO: "+ user_ID);
    }

    /**
     * This opens chat page between this user and the user selected
     *
     * @param user the person user is talking to
     *
     * @see ChatServer#onCreate(Bundle) onCreate(Bundle)
     */
    private void startChat(final String user){
//        chatAdapter.add(new ChatData(true, text));   <------ HOW MESSAGES WOUOLD BE DISPLAYED ON THE message_layout

        setContentView(R.layout.activity_chat);

        btnOffer = (Button) findViewById(R.id.bt_chat_offer);
        btnAccept = (Button) findViewById(R.id.bt_chat_accept);
        hourly = (RadioButton) findViewById(R.id.hourly);
        sum = (RadioButton) findViewById(R.id.sum);
        etOffer = (EditText) findViewById(R.id.et_chat_offer);



        this.messageInput = (EditText) findViewById(R.id.et_chat_message);
        this.btnMessageSend= (Button) findViewById(R.id.message_send);


        MLBService.setSocketEvent(new SocketIO.ChatMessageReceivedListener() {
            @Override
            public void onResponse(JSONObject object) {
                try {
                    System.out.println(object);
                    chatAdapter.add(new ChatData(false, object.getString(MLBService.JSONKey.CHAT_MESSAGE_KEY)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



        //Setup the listview to contain all the messages in the chat
        this.chatListView = (ListView) findViewById(R.id.chat_listview);
        this.chatListView.setAdapter(chatAdapter);
        this.chatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        this.chatAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //Set current position at the bottom of the listview
                chatListView.setSelection(chatAdapter.getCount()-1);
            }
        });


        this.btnMessageSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String text = messageInput.getText().toString();
                if(!text.equals("")) {

                    Tools.DB_CONNECTION.addDataToChat(hashCode(), MLBService.getLoggedInUserID(), user_ID, text, new Date());

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(MLBService.JSONKey.CHAT_MESSAGE_KEY, text);
                        jsonObject.put(MLBService.JSONKey.JOB_ID, job_ID);
                        if(Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {
                            jsonObject.put(MLBService.JSONKey.USER_ID, MLBService.getLoggedInUserID());
                            jsonObject.put(MLBService.JSONKey.ORG_ID, MLBService.getLoggedInUserID());
                            jsonObject.put(MLBService.JSONKey.STAFF_ID, user_ID);
                        } else if (Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {
                            jsonObject.put(MLBService.JSONKey.USER_ID, MLBService.getLoggedInUserID());
                            jsonObject.put(MLBService.JSONKey.STAFF_ID, MLBService.getLoggedInUserID());
                            jsonObject.put(MLBService.JSONKey.ORG_ID, user_ID);
                        }
                        MLBService.sendRequest(messageInput.getContext(),
                                MLBService.ACTION.CHAT_SEND_MESSAGE,
                                new MLBService.MLBResponseListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d(TAG, "test responce chat");
                                        System.out.println("RESPONSE: " + response);
                                        chatAdapter.add(new ChatData(true, text));
                                    }
                                },
                                new MLBService.MLBErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Log.d(TAG, "test error chat");
                                    }
                                },
                                jsonObject,
                                job_ID, user_ID
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (VolleyError e) {
                        e.printStackTrace();
                    }
                    messageInput.setText("");
                }
            }
        });


        this.btnOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("selected: "+  hourly.isChecked());
                boolean isHourly = hourly.isChecked();
                String money = etOffer.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(MLBService.JSONKey.JOB_ID, job_ID);
                    jsonObject.put(MLBService.JSONKey.USER_ID, MLBService.getLoggedInUserID());
                    if(Tools.accountType.equals(MLBService.AccountType.ORGANISER)){
                        jsonObject.put(MLBService.JSONKey.ORG_ID, MLBService.getLoggedInUserID());
                        jsonObject.put(MLBService.JSONKey.STAFF_ID, user_ID);
                    } else if (Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {
                        jsonObject.put(MLBService.JSONKey.STAFF_ID, MLBService.getLoggedInUserID());
                        jsonObject.put(MLBService.JSONKey.ORG_ID, user_ID);
                    }
                    jsonObject.put("offer", money);
                    jsonObject.put("isHourly", isHourly);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("------------------------------------------\n");
                System.out.println("JSON - : " + jsonObject);
                System.out.println("JOB ID - : " + job_ID);
                System.out.println("USER ID - : " + user_ID);
                System.out.println("------------------------------------------\n");
                try {
                    MLBService.sendRequest(
                            btnOffer.getContext(),
                            MLBService.ACTION.CHAT_SEND_JOB_OFFER,
                            new MLBService.MLBResponseListener() {
                                @Override
                                public void onResponse(JSONObject response) {

                                }
                            },
                            new MLBService.MLBErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }, jsonObject,job_ID,  user_ID
                    );
                    offerReceived = false;
                    btnAccept.setEnabled(offerReceived);
                } catch (VolleyError volleyError) {
                    volleyError.printStackTrace();
                }
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {




            @Override
            public void onClick(View v) {

                final JSONObject jsonObject = new JSONObject();

                try{

                    jsonObject.put(MLBService.JSONKey.JOB_ID, job_ID);
                    jsonObject.put(MLBService.JSONKey.USER_ID, MLBService.getLoggedInUserID());
                    if (Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {
                        jsonObject.put(MLBService.JSONKey.ORG_ID, MLBService.getLoggedInUserID());
                        jsonObject.put(MLBService.JSONKey.STAFF_ID, user_ID);
                    } else if (Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {
                        jsonObject.put(MLBService.JSONKey.STAFF_ID, MLBService.getLoggedInUserID());
                        jsonObject.put(MLBService.JSONKey.ORG_ID, user_ID);
                    }
                    jsonObject.put("offer", etOffer.getText());

                }catch(JSONException e){
                    e.printStackTrace();
                }

                if(btnAccept.getText().equals("ACCEPT")) {


                    try {
                        MLBService.sendRequest(
                                btnAccept.getContext(),
                                MLBService.ACTION.CHAT_ACCEPT_JOB_OFFER,
                                new MLBService.MLBResponseListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                    }
                                },
                                new MLBService.MLBErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }, jsonObject, job_ID, user_ID
                        );
                    } catch (VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                } else {
                    try {
                        MLBService.sendRequest(
                                btnAccept.getContext(),
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
                                }, jsonObject, job_ID
                        );
                    } catch (VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                }

                btnAccept.setEnabled(false);
                btnOffer.setEnabled(false);
                etOffer.setEnabled(false);
                hourly.setEnabled(false);
                sum.setEnabled(false);
                if(Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {
                    btnAccept.setText("COMPLETE JOB");
                    btnAccept.setEnabled(true);
                }
            }

        });

        loadCache();
    }


    /**
     * re-adds all the old message to the chat  between this user and the person he/she
     * s talking to
     */
    private void loadCache() {

        Log.i(TAG, "nickname: " + MLBService.getLoggedInUserID() + " to: " + user_ID);
        Tools.DB_CONNECTION.query(

                "SELECT sender,receiver,message,date_time FROM CHAT WHERE " +
                        "(sender = '" + MLBService.getLoggedInUserID() + "' AND receiver = '" + user_ID + "') OR " +
                        "(receiver = '" + MLBService.getLoggedInUserID() + "' AND sender = '" + user_ID + "')"
                , new DatabaseHandler.DBRequest() {

                    @Override
                    public void onResponse(Cursor cursor) {

                        Log.i(TAG, "cursor: " + cursor);
                        if (cursor.moveToFirst()) {
                            do {


                                if (cursor.getString(0).equals(MLBService.getLoggedInUserID())) {
                                    chatAdapter.add(new ChatData(true, cursor.getString(2)));
                                } else {
                                    chatAdapter.add(new ChatData(false, cursor.getString(2)));
                                }


                            } while (cursor.moveToNext());
                        }
                    }
                }
        );
    }

    /**
     * set the chat event for when user is in the chat page taking to the online
     * user
     */

    @Override
    public void onDestroy(){
        super.onDestroy();
        closeConnection();
    }

    private void closeConnection(){

        if(socket.connected()){

            socket.disconnect();
            socket.close();
        }
    }

    private Intent serviceIntent;

    /**
     * Multiple listeners will call from this method as a subroutine
     * within this chat service class; making notifications pop up.
     */

    public static boolean isActive;

    private void startNotification(){

        serviceIntent = new Intent(this, MessageService.class);
        startActivity(serviceIntent);

    }

    @Override
    public void onResume(){
        super.onResume();
        isActive = true;

    }

    @Override
    public void onPause(){
        super.onPause();
        isActive = false;

    }

}

