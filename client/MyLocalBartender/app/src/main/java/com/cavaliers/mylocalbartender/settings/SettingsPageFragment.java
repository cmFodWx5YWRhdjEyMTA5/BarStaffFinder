package com.cavaliers.mylocalbartender.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.fragment.MLBFragment;
import com.cavaliers.mylocalbartender.startedservices.MessageService;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;

import static com.cavaliers.mylocalbartender.R.id.resendButton;
import static com.cavaliers.mylocalbartender.R.id.signinButton;

/**
 * Created by JamesRich on 07/03/2017.
 */

public class SettingsPageFragment extends MLBFragment{

    private static boolean vibrationIsChecked;
    private static boolean soundIsChecked;
    private static boolean isPlaying;
    private static boolean notifyIsChecked;
    private static boolean isReloading;

    private static SoundThread soundThread;

    private Switch notificationSwitch;
    private Switch soundSwitch;

    private Switch vibrationSwitch;
    private Button deleteButton;
    private Vibrator vibrator;
    private AudioTrack message_notification;
    private Uri uri;

    private View view;
    private Activity activity;
    private Context context;
    private Bundle savedInstanceState;
    private LayoutInflater inflater;

    private AlertDialog alertDialog;
    private static int milliseconds = 500;
    private Handler handler;
    private Intent intent;


    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        this.view = inflater.inflate(R.layout.fragment_settings_page,container,false);
        //setRetainInstance(true);

        this.activity = getActivity();
        intent = new Intent(this.activity, MessageService.class);
        context = activity.getApplicationContext();
        this.handler = new Handler();

        setPreferences();
        //setNotification();
        setVibrate();
        setSound();
        setListeners();


        if(isReloading){

            loadState();
            isReloading = false;
        }

        return view;
    }

    private void setPreferences(){

        sharedPreferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        isReloading = sharedPreferences.getBoolean("reloadState",false);

    }


    private void setVibrate(){

        this.vibrator = (Vibrator)activity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void setListeners(){

        notificationSwitch = (Switch)view.findViewById(R.id.notifications_switch);
        notificationSwitch.setOnCheckedChangeListener(new SwitchHandler());

        soundSwitch = (Switch)view.findViewById(R.id.sound_switch);
        soundSwitch.setOnCheckedChangeListener(new SwitchHandler());

        vibrationSwitch = (Switch)view.findViewById(R.id.vibration_switch);
        vibrationSwitch.setOnCheckedChangeListener(new SwitchHandler());

        deleteButton = (Button)view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new ButtonHandler());

    }

    private void setSound(){

        int minBufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        message_notification = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);

        //uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + getPackageName() + "/raw/mlb_doorbell");

    }

    private class SwitchHandler implements CompoundButton.OnCheckedChangeListener {


        // check switch
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            final Toast toastToUse;
            Switch s = (Switch)buttonView;


            switch(buttonView.getId()) {

                case R.id.notifications_switch:
                    if (isChecked) {

                        toastToUse = Toast.makeText(context, "notifications : On", Toast.LENGTH_SHORT);
                        //for testing.
                        notifyIsChecked = true;

                        s.setText("On");

                    } else {

                        toastToUse = Toast.makeText(context, "notifications : Off", Toast.LENGTH_SHORT);
                        notifyIsChecked = false;
                        s.setText("Off");

                    }

                    toastToUse.show();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            toastToUse.cancel();

                        }
                    }, milliseconds);

                    break;

                case R.id.sound_switch:
                    if (isChecked) {
                        //need to change to managing inside service intent.
                        //notificationBuilder.setSound(uri);
                        toastToUse = Toast.makeText(context, "sounds : On", Toast.LENGTH_SHORT);
                        s.setText("On");
                        soundIsChecked = true;

                    } else {

                        //notificationBuilder.setSound(null);
                        toastToUse = Toast.makeText(context, "sounds : Off", Toast.LENGTH_SHORT);

                        s.setText("Off");
                        soundIsChecked = false;
                    }

                    toastToUse.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            toastToUse.cancel();

                        }
                    }, milliseconds);

                    break;

                case R.id.vibration_switch:
                    if (isChecked) {

                        toastToUse = Toast.makeText(context, "vibrations : On", Toast.LENGTH_SHORT);
                        vibrationIsChecked = true;
                        s.setText("On");

                    } else {

                        toastToUse = Toast.makeText(context, "vibrations: Off", Toast.LENGTH_SHORT);
                        vibrationIsChecked = false;
                        s.setText("Off");
                    }

                    toastToUse.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            toastToUse.cancel();

                        }
                    }, milliseconds);

                    break;
            }

            /**
             * setting the vibrate function makes my android crash.
             */



            if(!isReloading){

                if(vibrationIsChecked){

                    vibrator.vibrate(1000);

                }

                if(soundIsChecked && !isPlaying){

                    isPlaying = true;

                    soundThread = new SoundThread();
                    soundThread.start();

                    isPlaying = false;
                }
                // throw away method...
                if(buttonView.getId() == R.id.notifications_switch && notifyIsChecked) {
                    // for testing
                    //addNotification();

                }
            }

        }

    }

    /**
     * An inner class to be used for the switches exclusively on the settings page.
     * Decided not to make this anonymous, so, if necessary, new additional buttons can easily use this
     * listener later on, this may be the case with a settings page.
     */
    private class ButtonHandler implements View.OnClickListener{

        /**
         * @param buttonView an alertDialog is used to warn the user of
         *                   what they are doing and whether they really meant to
         *                   touch the delete button. The names are a little confusing
         *                   as in essence it was easier to re-use an already existing
<<<<<<< HEAD
         *                   layout. Resend corresponds with cancel, and
=======
         *                   layout. Resend corresponds with cancel, delete button, is the original
         *                   button intended for deletion, bringing up a notification and the signin
         *                   button is the button that deletes on the alert dialog.
>>>>>>> frontEndRefactor
         */

        public void onClick(View buttonView){

            Button cancelButton = null;
            Button confirmButton = null;

            switch (buttonView.getId()){

                case R.id.delete_button:

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
                    View dialogView = getLayoutInflater(savedInstanceState).inflate(R.layout.activity_confirmation,null);
                    alertBuilder.setView(dialogView);

                    TextView textHeader = (TextView) dialogView.findViewById(R.id.tv_confirmation);
                    textHeader.setText("Delete Account");
                    TextView text = (TextView) dialogView.findViewById(R.id.confirmation_message);
                    text.setText("Are you sure you want to delete your account?");

                    cancelButton = (Button) dialogView.findViewById(resendButton);
                    cancelButton.setText("Cancel");
                    cancelButton.setOnClickListener(this);

                    confirmButton = (Button) dialogView.findViewById(R.id.signinButton);
                    confirmButton.setText("Delete");
                    confirmButton.setOnClickListener(this);

                    alertDialog = alertBuilder.create();

                    alertDialog.show();

                    break;

                case resendButton:

                    if(alertDialog != null && alertDialog.isShowing()){

                        alertDialog.dismiss();
                        Toast.makeText(context, "back", Toast.LENGTH_LONG).show();

                    }

                    break;

                case signinButton:

                    if(alertDialog != null && alertDialog.isShowing()) {

                        Toast.makeText(context, "Profile Deleted!", Toast.LENGTH_LONG).show();
                        intent.putExtra(MessageService.EXTRA_MESSAGE,
                                getResources().getString(R.string.apply_button));
                        //WARNING, KEEP THIS LINE OF CODE.
                        MessageService.setNotificationMessage("Profile Deleted", "Your profile has been deleted");
                        activity.startService(intent);
                        //
                    }

                    break;
            }


        }

    }

    /**
     * only one sound can be played, due to concurrency issues.
     * prevent more than one thread accessing this at once and the
     * event queue will be of size 1 even if two notifications
     * in quick succession. This generally should not be a problem
     * as the audiotrack should stop before another one is being played
     * ,however the synchronized method is left in place in case
     * other changes are to be made, but by all means only one
     * sound should be played at a given moment.
     */
    private class SoundThread extends Thread{

        @Override
        public void run(){

            message_notification.stop();
            playSound();


        }

    }

    /**
     * only one sound can be played, due to concurrency issues.
     * prevent more than one thread accessing this at once and the
     * event queue will be of size 1 even if two notifications
     * in quick succession.
     */
    public synchronized void playSound(){

        message_notification.play();
        int i = 0;
        int bufferSize = 2048;
        byte [] buffer = new byte[bufferSize];
        InputStream inputStream = getResources().openRawResource(R.raw.mlb_doorbell);
        try {
            while((i = inputStream.read(buffer)) != -1)
                message_notification.write(buffer, 0, i);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * The saveState subroutine is for items to save to be called at stages of the fragment lifecycle
     * where the app might be hidden or closed and the state should be maintained on restart caused by
     * reorientation or temporarily navigating away from the app's GUI.
     * The chosen way to do this is shared preference, this is because it is
     * efficient for relatively small collections of key value pairs,
     * and this class is set inside a fragment so the saved instance is always equal to null, which means items
     * cannot be added to the fragment's instance state.
     */
    private void saveState(){

        sharedPreferences.edit().putString("nSwitchText", notificationSwitch.getText().toString()).commit();
        sharedPreferences.edit().putBoolean("nSwitchCheck", notificationSwitch.isChecked()).commit();

        sharedPreferences.edit().putString("sSwitchText", soundSwitch.getText().toString()).commit();
        sharedPreferences.edit().putBoolean("sSwitchCheck", soundSwitch.isChecked()).commit();

        sharedPreferences.edit().putString("vSwitchText", vibrationSwitch.getText().toString()).commit();
        sharedPreferences.edit().putBoolean("vSwitchCheck", vibrationSwitch.isChecked()).commit();

        sharedPreferences.edit().putBoolean("nCheck",notifyIsChecked).commit();
        sharedPreferences.edit().putBoolean("sCheck",soundIsChecked).commit();
        sharedPreferences.edit().putBoolean("play",isPlaying).commit();
        sharedPreferences.edit().putBoolean("vCheck",vibrationIsChecked).commit();
        sharedPreferences.edit().putBoolean("reloadState",true).commit();

    }

    /**
     * subroutine for items to load.
     * Similar to above, is called by the onCreateView method, to load saved items.
     */
    private void loadState(){

        notificationSwitch.setText(sharedPreferences.getString("nSwitchText","Off"));
        notificationSwitch.setChecked(sharedPreferences.getBoolean("nSwitchCheck",false));

        soundSwitch.setText(sharedPreferences.getString("sSwitchText","Off"));
        soundSwitch.setChecked(sharedPreferences.getBoolean("sSwitchCheck",false));

        vibrationSwitch.setText(sharedPreferences.getString("vSwitchText","Off"));
        vibrationSwitch.setChecked(sharedPreferences.getBoolean("vSwitchCheck",false));

        notifyIsChecked = sharedPreferences.getBoolean("nCheck",false);
        soundIsChecked = sharedPreferences.getBoolean("sCheck",false);
        vibrationIsChecked = sharedPreferences.getBoolean("vCheck",false);
        isPlaying = sharedPreferences.getBoolean("play",false);
    }

    /**
     * already at the top.
     @Override
     public void onCreateView(LayoutInflater layout, Bundle savedInstanceState...){


     }
     */
    /*
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    */

    /*
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }*/

    /*
    public void onStart(){
        super.onStart();

    }*/
    /*
    can reactivate widgets to match the desired state.
    @Override
    public void onResume(){
        super.onResume();
        loadState();

    }
    */
    /*
    @Override
    public void onPause(){
        super.onPause();

    }
    */

    @Override
    public void onStop(){
        super.onStop();

        saveState();
    }


    /*
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
    */

    /**
     * getter method
     * @return allows the started service or any other class required to see
     * if notifications are or ought to be run
     */
    public static boolean getNotifyState(){

        return notifyIsChecked;

    }

    /**
     * getter method
     * @return allows the started service or any other class required to see
     * if sounds are or ought to be run.
     */
    public static boolean getSoundState(){

        return soundIsChecked;

    }

    /*
    public Uri getSound(){

        return uri;

    }
    */
    /**
     * getter method
     * @return allows the started service, or any other class required, to see
     * if vibrations should run.
     */
    public static boolean getVibrateState(){

        return vibrationIsChecked;

    }
    /*
    public long[] getVibration(){

        return notifyVibration;
    }
    */

}
