package com.cavaliers.mylocalbartender.user.barstaff.model;

import java.util.HashMap;

/**
 * Created by Robert on 23/03/2017.
 */

public class ProfileBarstaffData {
    private static HashMap<String, String > profileData = new HashMap<>();
    public static void setProfileData(String key, String value){
        System.out.println("_________I JUST FOUND A PROFILE VALUE="+key+"-"+value);
        profileData.put(key, value);
    }
    public static String get(String key){
        return profileData.get(key);
    }
}
