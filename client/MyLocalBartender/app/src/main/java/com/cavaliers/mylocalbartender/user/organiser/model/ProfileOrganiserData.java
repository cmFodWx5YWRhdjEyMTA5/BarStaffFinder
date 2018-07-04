package com.cavaliers.mylocalbartender.user.organiser.model;

import java.util.HashMap;

/**
 * Created by Robert on 23/03/2017.
 */

public class ProfileOrganiserData {
    private static HashMap<String, String > profileData = new HashMap<>();
    public static void setProfileData(String key, String value){
        profileData.put(key, value);
    }
    public static String get(String key){
        return profileData.get(key);
    }
}
