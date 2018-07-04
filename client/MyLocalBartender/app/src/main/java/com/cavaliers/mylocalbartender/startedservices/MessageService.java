package com.cavaliers.mylocalbartender.startedservices;

/**
 * Created by JamesRich on 19/03/2017.
 */

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;


import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.server.ChatServer;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.settings.SettingsPageFragment;

public class MessageService extends IntentService {

    public static final int NOTIFICATION_ID = 5453;

    public static final String EXTRA_MESSAGE = "message";
    //private Handler handler;

    private Notification notification;

    private static final SettingsPageFragment settings = new SettingsPageFragment();
    private static String title;
    private static String text;
    private Uri uri;
    private Intent intent;

    private TaskStackBuilder stackBuilder;
    private PendingIntent pendingIntent;
    private Notification.Builder notificationBuilder;
    private NotificationManager notificationManager;


    public MessageService() {
        super("MessageService: message sent");
    }

    /**
     * The method is run on the main thread.
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
        //handler = new Handler();
    }

    /**
     * Thread that was used for testing when app was closed.
     * The method is run when startActivity is called.
     * @param intent an intent is passed making this method execute.
     */

    @Override
    protected void onHandleIntent(Intent intent) {

        synchronized (this) {
            try {
                wait(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String text = intent.getStringExtra(EXTRA_MESSAGE);
        showText(text);

    }

    /**
     * Currently unused due to persistent bugs...
     * Meant to take the user to the relevant page within the app that
     * triggered the notification.
     */

    private void setStackBuild() {

        //Check this section more.
        intent = new Intent(this, ChatServer.class);

        stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SettingsPageFragment.class);
        stackBuilder.addNextIntent(intent);

        pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    /**
     * class for building notifications in general with minimum requirements
     * Two checks for vibrations and sound are made, to see whether the notification to send,
     * made by the builder should be set.
     */

    public void setNotificationBuild() {

        notificationBuilder = new Notification.Builder(getApplicationContext());
        notificationBuilder.setSmallIcon(R.drawable.notification);

        Bitmap resizedIcon = changeIconSize(124,getResources().getDrawable(R.drawable.bkgr));
        notificationBuilder.setLargeIcon(resizedIcon);
        //notificationBuilder.setContentTitle("You have mail, Click Me!");
        notificationBuilder.setContentTitle(title);
        //notificationBuilder.setContentText("clique me");
        notificationBuilder.setContentText(text);
        //notificationBuilder.setVibrate(settingsPage.getVibration());
        if(settings.getVibrateState()) {

            notificationBuilder.setVibrate(new long[]{500, 500, 500});

        }
    }

    /**
     * independent subroutine, if the sound settings are switched on
     * then the notification built will have that sound set to it
     * by the notification builder. The sound file can be changed from here, previous attempts
     * from the settings class did not work.
     */

    private void setNotificationSound() {

        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + getPackageName() + "/raw/mlb_doorbell");
        //uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //uri = settings.getSound();
        notificationBuilder.setSound(uri);

    }

    /**
     * The main body for building notifications, if switched on inside settings.
     */
    private void sendNotification() {

        notification = notificationBuilder.getNotification();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    /**
     * show text is the main method for showing cand controlling the output of the notification builder.
     * @param text unused but can be used for showing message logs if necessary.
     */
    public void showText(final String text) {

        if(!ChatServer.isActive){

            setStackBuild();
            setNotificationBuild();

            if (settings.getSoundState()) {

                setNotificationSound();

            }

            if (settings.getNotifyState()) {

                sendNotification();

            }

        }
    }

    /**
     *
     * @param length the icon will be set to a different size, assuming it is a square.
     * @param dr 'drawable' the icon will be converted to a bitmap, resized.
     * @return new bitmap will be sent to the notification builder class
     */
    public static Bitmap changeIconSize(int length, Drawable dr){


        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();

        bitmap = Bitmap.createScaledBitmap(bitmap, length, length, true);
        return bitmap;

    }

    /**
     * intended for use, but did not work in practise
     * @param sound unused but potential to make work in future if feature becomes necessary
     *              as it is a likely enhancement for future versions.
     */
    public void setNotificationSound(Uri sound) {

        if (sound != null) {

            this.uri = sound;
            notificationBuilder.setSound(uri);

        } else {

            setNotificationBuild();

        }
    }

    public static void setNotificationMessage(String ti, String message){

        title = ti;
        text = message;

    }
}