package com.cavaliers.mylocalbartender.tools;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cavaliers.mylocalbartender.server.DatabaseHandler;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.user.barstaff.model.ProfileBarstaffData;
import com.cavaliers.mylocalbartender.user.organiser.model.ProfileOrganiserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class Tools {

    private static String currentContextName;

    public final static String MLB_SERVICE_TEST_TAG = "MLBServiceTest";

    public static DatabaseHandler DB_CONNECTION = null;

    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd:HH:mm", Locale.ENGLISH);

    public static MLBService.AccountType accountType;

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(int px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = Math.round(px / (metrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static void setCurrentContextName(@NonNull String name){

        if(name != null){
            currentContextName = name;
        }
    }

    public static String GetCurrentActivityName(){

        return currentContextName;
    }
    /**
     * This adds the view to the layout
     *
     * @param container the container
     * @param toAdd add to the container
     */
    public static void addToViewGroupLayout(ViewGroup  container, View toAdd, ViewGroup.LayoutParams params){

        toAdd.setLayoutParams(params);
        container.addView(toAdd);
        container.invalidate(); //updates UI
    }


    public static void loadUserData(JSONObject object) throws JSONException {
        if(accountType == MLBService.AccountType.ORGANISER){
            if(!(object.has(MLBService.JSONKey.SIGNIN_JSON) && object.getJSONObject(MLBService.JSONKey.SIGNIN_JSON).has(MLBService.JSONKey.PROFILE))) return;
            JSONObject profile = object.getJSONObject(MLBService.JSONKey.SIGNIN_JSON).getJSONObject(MLBService.JSONKey.PROFILE);
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.FIRST_NAME,  profile.getString(MLBService.JSONKey.FIRST_NAME));
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.SURNAME, profile.getString(MLBService.JSONKey.SURNAME));
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.POSTCODE, profile.getString(MLBService.JSONKey.POSTCODE));
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.DOB, profile.getString(MLBService.JSONKey.DOB));
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.GENDER, profile.getString(MLBService.JSONKey.GENDER));
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.PROF_POS, profile.getString(MLBService.JSONKey.PROF_POS));
            ProfileOrganiserData.setProfileData(MLBService.JSONKey.EVENT_TYPE, profile.getString(MLBService.JSONKey.EVENT_TYPE));

        }else if(accountType == MLBService.AccountType.BARSTAFF) {

            if (!(object.has(MLBService.JSONKey.SIGNIN_JSON) && object.getJSONObject(MLBService.JSONKey.SIGNIN_JSON).has(MLBService.JSONKey.PROFILE)))
                return;
            JSONObject profile = object.getJSONObject(MLBService.JSONKey.SIGNIN_JSON).getJSONObject(MLBService.JSONKey.PROFILE);
            ProfileBarstaffData.setProfileData(MLBService.JSONKey.FIRST_NAME, profile.getString(MLBService.JSONKey.FIRST_NAME));
            ProfileBarstaffData.setProfileData(MLBService.JSONKey.SURNAME, profile.getString(MLBService.JSONKey.SURNAME));
            ProfileBarstaffData.setProfileData(MLBService.JSONKey.POSTCODE, profile.getString(MLBService.JSONKey.POSTCODE));
            ProfileBarstaffData.setProfileData(MLBService.JSONKey.DOB, profile.getString(MLBService.JSONKey.DOB));
            ProfileBarstaffData.setProfileData(MLBService.JSONKey.GENDER, profile.getString(MLBService.JSONKey.GENDER));
            String imagePath = profile.getString(MLBService.JSONKey.IMAGE_PATH);
        }
    }
}
