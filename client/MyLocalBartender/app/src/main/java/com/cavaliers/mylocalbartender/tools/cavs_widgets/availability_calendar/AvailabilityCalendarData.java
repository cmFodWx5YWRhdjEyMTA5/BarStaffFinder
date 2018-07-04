package com.cavaliers.mylocalbartender.tools.cavs_widgets.availability_calendar;

import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailabilityCalendarData {

    private static Map<String,ArrayList<AvailabilityDay>> availabilityDaysMap = new HashMap<>();
    private static ArrayList<AvailabilityDay> availabilityDayArrayList = new ArrayList<>();

    private static boolean isBarstaff = false;

    private static String day;
    private static String startTime;
    private static String startTimeRelation;

    private static String endTime;
    private static String endTimeRelation;

    public static void addAdvertAvailability(JSONObject obj){
        isBarstaff = false;

            ArrayList<AvailabilityDay> availabilityDaysList  = new ArrayList<>();
            AvailabilityDay availDay = new AvailabilityDay();

        try {

               String staffID = String.valueOf(obj.getInt("staff_ID"));
                System.out.println("____________I FOUND ID="+staffID);

            day = obj.getString("day");
            System.out.println("____________I FOUND DAY="+day);

            startTime = AccessVerificationTools.getParsedHourMin(obj.getString("start"));
            startTimeRelation = AccessVerificationTools.getParsedAmPm(obj.getString("start"));
            System.out.println("____________I FOUND START TIME="+startTime+" "+startTimeRelation);


            endTime = AccessVerificationTools.getParsedHourMin((obj.getString("end")));
            endTimeRelation = AccessVerificationTools.getParsedAmPm(obj.getString("end"));
            System.out.println("____________I FOUND END TIME="+endTime+" "+endTimeRelation);

            availDay.setDay(day);

            availDay.setStartTime(startTime);
            availDay.setStartTimeRelation(startTimeRelation);

            availDay.setEndTime(endTime);
            availDay.setEndTimeRelation(endTimeRelation);



            if(availabilityDaysMap.get(staffID) != null){
                availabilityDaysMap.get(staffID).add(availDay);
                System.out.println("____________IT DOES EXISTS SO I ADD IT");
            }
            else {
                availabilityDaysList.add(availDay);
                availabilityDaysMap.put(staffID,availabilityDaysList);
                System.out.println("____________IT DOES NOT EXISTS SO I DON'T ADD IT");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void addProfileAvailability(JSONObject obj){
        isBarstaff = true;
        AvailabilityDay availDay = new AvailabilityDay();

        try {

            day = obj.getString("day");
            System.out.println("____________I FOUND DAY="+day);

            startTime = AccessVerificationTools.getParsedHourMin(obj.getString("start"));
            startTimeRelation = AccessVerificationTools.getParsedAmPm(obj.getString("start"));
            System.out.println("____________I FOUND START TIME="+startTime+" "+startTimeRelation);


            endTime = AccessVerificationTools.getParsedHourMin((obj.getString("end")));
            endTimeRelation = AccessVerificationTools.getParsedAmPm(obj.getString("end"));
            System.out.println("____________I FOUND END TIME="+endTime+" "+endTimeRelation);

            availDay.setDay(day);

            availDay.setStartTime(startTime);
            availDay.setStartTimeRelation(startTimeRelation);

            availDay.setEndTime(endTime);
            availDay.setEndTimeRelation(endTimeRelation);


                availabilityDayArrayList.add(availDay);
                System.out.println("____________IT DOES NOT EXISTS SO I DON'T ADD IT");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setUpAvailabilityCalendar(){
        availabilityDayArrayList.clear();
        isBarstaff = true;
        for(int i=0;i<7;++i){
            availabilityDayArrayList.add(new AvailabilityDay(AccessVerificationTools.days[i],"00:00","AM","00:00","PM"));
        }
    }

    public static Map<String,ArrayList<AvailabilityDay>> getAvailabilityData(){
        System.out.println("-------------THE MAP SIZE IS="+availabilityDaysMap.size());
        return availabilityDaysMap;
    }

    public static List<AvailabilityDay> getAvailabilityList(){
        if(isBarstaff) {
            System.out.println("-------------THE LIST SIZE IS="+availabilityDayArrayList.size());
            return availabilityDayArrayList;
        }

        return availabilityDaysMap.get(MLBService.getLoggedInUserID());
    }
    public static void clearAvailabilityList(){
        availabilityDayArrayList.clear();
    }

}
