package com.cavaliers.mylocalbartender.tools.cavs_widgets.availability_calendar;

import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;

public class AvailabilityDay {

    private String day;

    private String startTime;
    private String startTimeRelation;

    private String endTime;
    private String endTimeRelation;


    public AvailabilityDay(){}
    public AvailabilityDay(String day, String startTime, String startTimeRelation,
                           String endTime, String endTimeRelation){
        this.day = day;
        this.startTime = startTime;
        this.startTimeRelation = startTimeRelation;
        this.endTime = endTime;
        this.endTimeRelation = endTimeRelation;

    }


    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }



    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTimeRelation() {
        return startTimeRelation;
    }

    public void setStartTimeRelation(String startTimeRelation) {
        this.startTimeRelation = startTimeRelation;
    }




    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public String getEndTimeRelation() {
        return endTimeRelation;
    }

    public void setEndTimeRelation(String endTimeRelation) {
        this.endTimeRelation = endTimeRelation;
    }

    public String getStartTimeForDatabase(){
        return AccessVerificationTools.zeroAdder(startTime)+":00"+" "+startTimeRelation;
    }

    public String getEndTimeForDatabase(){
        return AccessVerificationTools.zeroAdder(endTime)+":00"+" "+endTimeRelation;
    }

}
