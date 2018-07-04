package com.cavaliers.mylocalbartender;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cavaliers.mylocalbartender.activity.MLBActivity;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.server.DatabaseHandler;
import com.cavaliers.mylocalbartender.server.SocketIO;
import com.cavaliers.mylocalbartender.server.SocketIOTest;
import com.cavaliers.mylocalbartender.server.stripe.PaymentActivity;
import com.cavaliers.mylocalbartender.tools.Tools;

import java.io.UnsupportedEncodingException;

public class ServerTest extends MLBActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_server_test);

        final EditText em = (EditText) findViewById(R.id.email);
        final EditText ha = (EditText) findViewById(R.id.hash);

        final Button signUp = (Button) findViewById(R.id.sign_up_test);
        final TextView signUpText = (TextView) findViewById(R.id.sign_up_test_text);

        /**
         * set action listener for button
         */
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = em.getText().toString();
                String password = ha.getText().toString();
                String dob = "1991-02-12";
                MLBService.AccountType type = MLBService.AccountType.ORGANISER;

                try {
                    MLBService.signUp(view.getContext(), email, password, dob, type, new MLBService.MLBResponseListener() {

                        /**
                         * The action to take place after receiving an response, this action
                         * is posted to main thread therefore UI element may be updated directly in the method
                         *
                         * @param response the response relieved by the server
                         */
                        @Override
                        public void onResponse(JSONObject response) {
                            signUpText.setText(signUpText.getText() + response.toString());
                        }

                    }, new MLBService.MLBErrorListener() {

                        /**
                         * The action to take place after receiving an error from the
                         * server/api
                         *
                         * @param error the error relieved by the server/api
                         */
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showError(error);
                        }
                    });

                } catch (VolleyError e){

                    showError(e);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        Button signIn = (Button) findViewById(R.id.sign_in_test);
        final TextView signInText = (TextView) findViewById(R.id.sign_in_test_text);

        /**
         * set the action listener to the sign in button, it sends data to the server and prints out the response
         */
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = em.getText().toString();
                String password = ha.getText().toString();
                /**
                 * The action to take place after receiving an response, this action
                 * is posted to main thread therefore UI element may be updated directly in the method
                 *
                 * @param response the response relieved by the server
                 */
                try {
                    MLBService.signIn(view.getContext(), email, password, new MLBService.MLBResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            signInText.setText(signInText.getText() + response.toString());
                        }


                    },new MLBService.MLBErrorListener() {

                        /**
                         * The action to take place after receiving an error from the
                         * server/api
                         *
                         * @param error the error relieved by the server/api
                         */
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showError(error);
                        }
                    });

                } catch (VolleyError e){

                    showError(e);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });


        final Button listUsers = (Button) findViewById(R.id.list_users);
        final TextView usersText = (TextView) findViewById(R.id.list_users_text);

        listUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    MLBService.sendRequest(view.getContext(), MLBService.ACTION.GET_BARTENDERS_PROFILE, new MLBService.MLBResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            usersText.setText(usersText.getText() + response.toString());
                        }
                    }, new MLBService.MLBErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            showError(error);
                        }
                    }, null);

                } catch (VolleyError e){

                    showError(e);

                }
            }
        });

        final Button chat = (Button) findViewById(R.id.chat_button);

        chat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent chat_activity = new Intent(view.getContext(), com.cavaliers.mylocalbartender.server.ChatServer.class);
                startActivity(chat_activity);
            }
        });

        final Button db = (Button) findViewById(R.id.db_connect);
        final TextView db_test = (TextView) findViewById(R.id.db_test);

        db.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Tools.DB_CONNECTION.getTestData(new DatabaseHandler.DBRequest() {

                    @Override
                    public void onResponse(final Cursor cursor) {

                        String result =  "";

                        if(cursor.getCount() > 0){

                            result = "{ id: '" + cursor.getInt(0) + "', event_id: '" + cursor.getInt(1) +
                                    "', sender: '" + cursor.getString(2) + "', receiver: '" + cursor.getString(3) +
                                    "', message: '" + cursor.getString(4) + "', date_time: '" + cursor.getString(5) + "' }";

                        }

                        db_test.setText(result);
                    }
                });
            }
        });

        final Button clear_db = (Button) findViewById(R.id.clear_db);

        clear_db.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Tools.DB_CONNECTION.clearMessageTable();
            }
        });

        final Button send_image = (Button) findViewById(R.id.send_image);

        send_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                
                System.out.println("sending");
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.menu_pencil);
                try {
                    JSONObject j = MLBService.RequestBuilder.buildJSONRequest(
                            new Pair<>("user_ID", "6"),
                            new Pair<>("first_name", "LeBron"),
                            new Pair<>("last_name", "James"),
                            new Pair<>("username", "LebronJ"),
                            new Pair<>("gender", "M"),
                            new Pair<>("mobile_number", "12345678901"),
                            new Pair<>("location", "Cle"),
                            new Pair<>("postcode", "1234567890"),
                            new Pair<>("proximity", "12"),
                            new Pair<>("type", "Bartender"),
                            new Pair<>("night_rate", 22.23),
                            new Pair<>("hour_rate", 23.22),
                            new Pair<>("image_path", MLBService.ImageUploader.encode(bitmap)),
                            new Pair<>("bank_acc_number", "1"),
                            new Pair<>("bank_sortcode", "123456"),
                            new Pair<>("speciality", "The Block"),
                            new Pair<>("description", "GOAT")
                    );
                    System.out.println(j);

                    MLBService.sendRequest(view.getContext(), MLBService.ACTION.COMPLETE_BARTENDER_PROFILE, new MLBService.MLBResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                System.out.println(response.getString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new MLBService.MLBErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error.getMessage());
                        }
                    }, j, j.get("user_ID").toString());

                } catch (JSONException | VolleyError e) {
                    e.printStackTrace();
                }
            }

                /*try {



                    try {



                        MLBService.sendImage(view.getContext(), j,new MLBService.MLBResponseListener() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Log.i("MLBService", response.toString());
                            }

                        }, new MLBService.MLBErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                showError(error);
                            }
                        });

                    } catch (VolleyError volleyError) {

                        Log.e("MLBService","No Internet Connection");
                    }


                } catch (JSONException  e) {
                    e.printStackTrace();
                }
            }*/
        });

        final Button test_payment = (Button) findViewById(R.id.test_payment);

        test_payment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent payment = new Intent(view.getContext(), PaymentActivity.class);
                startActivity(payment);
            }
        });

        final EditText email = (EditText) findViewById(R.id.et_email_test);
        final EditText password = (EditText) findViewById(R.id.et_password_test);
        final Button button = (Button) findViewById(R.id.t_sign_in);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {

                try {
                    MLBService.signIn(view.getContext(), email.getText().toString(), password.getText().toString(),
                            new MLBService.MLBResponseListener() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    System.out.println(response);

                                    try {

                                        JSONObject user = response.getJSONObject("user");
                                        if(user.getString("account_type").equals(MLBService.AccountType.ORGANISER.name())){

                                            SocketIOTest.currentType = MLBService.AccountType.ORGANISER;
                                            MLBService.connectSocket();

                                        }else{

                                            SocketIOTest.currentType = MLBService.AccountType.BARSTAFF;
                                            MLBService.connectSocket();
                                        }

                                        SocketIOTest.mID = user.getInt("user_ID");

                                        JSONObject object = new JSONObject();

                                        object.put("user_ID",SocketIOTest.mID);
                                        object.put("account_type", user.getString("account_type"));
                                        MLBService.emit(SocketIO.Event.SETUP_CONNECTION, object);

                                        Log.i(Tools.MLB_SERVICE_TEST_TAG, "connected to server");

                                        Intent t_home_page = new Intent(view.getContext(),SocketIOTest.class);
                                        startActivity(t_home_page);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new MLBService.MLBErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    System.out.println(error.getMessage());
                                }
                            });

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (VolleyError volleyError) {
                    Log.e("MLBTest","error occured");
                    volleyError.printStackTrace();
                }
            }
        });

        // Image download button test
        Button btnDownloadImage = (Button) findViewById(R.id.btn_download_image);
        final ImageView imageView = (ImageView) findViewById(R.id.imgView);

        btnDownloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MLBService.loadImage("https://91.121.78.111:1133/api/upload/name.png", new MLBService.MLBImageResponseListener() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                }, new MLBService.MLBErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("IMAGE_ERROR", error.getMessage());
                    }
                });
            }
        });
    }


    /**
     *
     * @param error The error to be displayed to the user
     */
    public void showError(VolleyError error){
        Toast.makeText(ServerTest.this, error.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * The action that happens if the back button was pressed
     */
    @Override
    public void onBackPressed() {

        MLBService.signOut(); //sign out the user
        Intent menu = new Intent(this, com.cavaliers.mylocalbartender.MyLocalBartender.class);
        this.startActivity(menu);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}