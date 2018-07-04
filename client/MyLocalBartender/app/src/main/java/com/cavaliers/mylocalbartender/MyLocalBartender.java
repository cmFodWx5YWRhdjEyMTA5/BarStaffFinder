package com.cavaliers.mylocalbartender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cavaliers.mylocalbartender.activity.MLBActivity;
import com.cavaliers.mylocalbartender.home.TestActivity;
import com.cavaliers.mylocalbartender.menu.bartender_menu.BartenderMenuActivity;
import com.cavaliers.mylocalbartender.menu.organiser_menu.OrganiserMenuActivity;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.signin.SignInDialog;
import com.cavaliers.mylocalbartender.signup.Confirmation;
import com.cavaliers.mylocalbartender.signup.SignUp;
import com.cavaliers.mylocalbartender.signup.SignupHandler;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.cavs_widgets.date_picker.CavsDatePicker;
import com.cavaliers.mylocalbartender.user.barstaff.BarStaffProfileActivity;
import com.cavaliers.mylocalbartender.user.organiser.OrganiserProfileActivity;

public class MyLocalBartender extends MLBActivity {

    public static int state;

    /**
     * when you have not logged in and in TEST_MODE
     */
    public final static int DEFAULT_PROFILE = 0x00;

    /**
     * when you have logged in
     */
    public final static int SIGNED_IN_PROFILE = 0x01;

    private int backButtonCount = 0; //counts number of times back button is pressed


    //HOME BUTTONS
    private Button sign_up_button; //button to trigger signup pop-up

    private View signupView; //pop-up view inflater
    private View signinView;
    private View logoutView;
    private SignUp ctrl_signup; //SignUp view controller
    private Confirmation ctrl_confirmation;

    private AlertDialog signInDialog;
    private  AlertDialog signUpDialog;
    private  AlertDialog confirmationUpDialog;//
    private AlertDialog logoutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        state = DEFAULT_PROFILE;

        MLBService.startService(this);
        setContentView(R.layout.activity_main);

        setSignedInBtnHandler(); // Run SignInDialog dialog
        displaySignup(); //run SignUp dialog
        ctrl_signup.onSignupListener(); //signup click listener


//        new CavsDateTimePicker(this, null);
    }
//START---------------------------------SIGNIN COMPONENTS-----

    public void displaySignin(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyLocalBartender.this);

        //add layout of sign_in instead of dialog_signup
        View signInView = getLayoutInflater().inflate(R.layout.dialog_signin, null);
        SignInDialog si = new SignInDialog(MyLocalBartender.this, signInView);

        si.setListener();

        alertBuilder.setView(signInView);
        MyLocalBartender.this.signInDialog = alertBuilder.create();
        MyLocalBartender.this.signInDialog.show();
    }
    /**
     * Sets up SignInDialog Dialog and launches it
     */
    private void setSignedInBtnHandler(){
        Button sign_in_button = (Button) findViewById(R.id.btn_sign_in);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             displaySignin();
            }
        });
    }
    //=================================================================
    // HOME TESTING CODE

    //START---------------------------------HOME PAGE-----

    public void startHomePage(){
        Intent home = new Intent(this, TestActivity.class);
        this.startActivity(home);
    }

    // ---------------------------------HOME PAGE-----END+




    //START---------------------------------SIGNUP COMPONENTS-----

    /**
     * Sets up SignUp Dialog and launches it
     */
    private void displaySignup(){
        signupView = getLayoutInflater().inflate(R.layout.dialog_signup, null);
        ctrl_signup = new SignUp(signupView, this);
        sign_up_button = (Button) findViewById(R.id.btn_sign_up);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyLocalBartender.this);
        alertBuilder.setView(signupView);
        this.signUpDialog = alertBuilder.create();
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyLocalBartender.this.signUpDialog.dismiss();
                MyLocalBartender.this.signUpDialog.show();
            }
        });
    }

    //---------------------------------SIGNUP COMPONENTS-----END+

    //START---------------------------------SIGNUP COMPONENTS-----

    /**
     * Sets up SignUp Dialog and launches it
     */
    private void displayConfirm(){
        signupView = getLayoutInflater().inflate(R.layout.activity_confirmation, null);
        ctrl_signup = new SignUp(signupView, this);
        sign_up_button = (Button) findViewById(R.id.btn_sign_up);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyLocalBartender.this);
        alertBuilder.setView(signupView);
        this.signUpDialog = alertBuilder.create();
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyLocalBartender.this.signUpDialog.dismiss();
                MyLocalBartender.this.signUpDialog.show();
            }
        });
    }

    /**
     * creates confirmation object.
     */
    public void dismissSignup(){

        MyLocalBartender.this.signUpDialog.dismiss();
        signupView = getLayoutInflater().inflate(R.layout.activity_confirmation, null);
        signinView = getLayoutInflater().inflate(R.layout.dialog_signin, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyLocalBartender.this);

        alertBuilder.setView(signupView);
        this.confirmationUpDialog = alertBuilder.create();

        alertBuilder.setView(signinView);
        this.signInDialog = alertBuilder.create();

        showConfirmationDialog();
    }

    private void showConfirmationDialog(){
        final SignupHandler suph = ctrl_signup.getSignupHandler();
        ctrl_confirmation = new Confirmation(signupView, this, confirmationUpDialog, signInDialog, suph);
        MyLocalBartender.this.confirmationUpDialog.show();
    }

//    public void dismissLogout(){
//
//
//        MyLocalBartender.this.logoutDialog.dismiss();
////        logoutView = getLayoutInflater().inflate(R.layout.t);
//
//    }


    //---------------------------------SIGNUP COMPONENTS-----END+



    @Override
    public void onBackPressed() {

        if(backButtonCount >= 1) {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            backButtonCount = 0;
        }
        else {

            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    /**
     * Generates new Intent holding a given Activity
     *
     * @param activityClass
     */
    private void startActivity(Class activityClass){
        Intent menu = new Intent(this, activityClass);
        this.startActivity(menu);
    }


    /**
     * Displays calendar
     *
     * @param v
     */
    public void showDatePickerDialog(View v) {
        new CavsDatePicker(this, (EditText) v);
//        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog dialogo = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                ctrl_signup.setDateData(dayOfMonth,month,year);
//            }
//        }, year, month, day);
//
//        dialogo.show();
//        long now = System.currentTimeMillis();
//        dialogo.getDatePicker().setMaxDate(now);
    }

    /**
     * Start the BarStaffProfileFragment page
     *
     * @param isProfileComplete Whether the account has been filled with the mandatory data or not
     *                          0 for false 1 for true
     */
    public void startBarStaffActivity(boolean isProfileComplete){

        //TODO
        // Initiate BarStaffProfileFragment activity page here
        if(isProfileComplete){
            Intent menu = new Intent(this, BartenderMenuActivity.class);
            startActivity(menu);

        }else{ //Else if the user has not completed their profile..

            Intent initProfile = new Intent(this, BarStaffProfileActivity.class);
            startActivity(initProfile); //start the profile, rather than the menu - to force them to complete their profile


        }

        state = SIGNED_IN_PROFILE;
    }

    /**
     * Start the Organiser page
     *
     * @param isProfileComplete Whether the account has been filled with the mandatory data or not
     *                          0 for false 1 for true
     */
    public void startOrganiserActiviy(boolean isProfileComplete){

        //TODO
        // Initiate Organiser activity page here
        if(isProfileComplete){
            Intent menu = new Intent(this, OrganiserMenuActivity.class);
            this.startActivity(menu);
        }else{

            Intent initProfile = new Intent(this, OrganiserProfileActivity.class);
            startActivity(initProfile); //start the profile, rather than the menu - to force them to complete their profile

        }
        state = SIGNED_IN_PROFILE;
    }

    /**
     * dismiss the dialogs window before the activity is destroyed
     * to avoid WindowLeak exception
     *
     */

    private void dismissDialogs(){
        if(this.signUpDialog != null && this.signUpDialog.isShowing())
            this.signUpDialog.dismiss();

        if(this.signInDialog != null && this.signInDialog.isShowing())
            this.signInDialog.dismiss();

//        // TO REMOVE WHEN THE ORGANISER TEST IN REMOVED
//        if(this.toBeRemoved != null && this.toBeRemoved.isShowing())
//            this.toBeRemoved.dismiss();
//        // -----------------------------------------------------
    }

    @Override
    protected void onPause(){
        super.onPause();
        // dismiss the dialog window before the activity gets paused
        // to avoid WindowLeak exception
        this.dismissDialogs();

    }

    @Override
    protected void onDestroy(){

        // dismiss the dialog window before the activity is destroyed
        // to avoid WindowLeak exception
        this.dismissDialogs();
        Tools.DB_CONNECTION.close();

        try {

            MLBService.stopService();

        } catch (InterruptedException e) {

            Log.i("MLBService", "Service was unable to close");
            e.printStackTrace();
        }

        Log.i("MLBService", "clossing program");

        super.onDestroy();
    }
}