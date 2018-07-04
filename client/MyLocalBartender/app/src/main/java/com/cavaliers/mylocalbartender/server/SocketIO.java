package com.cavaliers.mylocalbartender.server;

import com.android.volley.Response;

import org.json.JSONObject;

public class SocketIO {

    public interface EventListener{

        void onResponse(JSONObject object);

        String getName();
    }

    public static enum Event{

        APPLY_TO_JOB,
        ACCEPT_JOB_INVITE,
        SEND_INVITE,
        SETUP_CONNECTION,
        NEW_JOB,
        BAR_STAFF_JOINED,
        CARD_UPDATE,
        ORGANISER_ACCEPTED_APPLICANT,
        INITIATE_CHAT
    }

    //Organiser events

    public abstract static class BarStaffJoinedListener implements EventListener {

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "BarStaffJoinedListener"; }
    }

    public abstract static class BarStaffAppliedListener implements EventListener {

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName(){ return "BarStaffAppliedListener"; }
    }

    public abstract static class PrivateJobAcceptedListener implements EventListener {

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "PrivateJobAcceptedListener"; }
    }

    public abstract static class PrivateJobRejectedListener implements EventListener {

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "PrivateJobRejectedListener"; }
    }

    public abstract static class BarStaffRejectedApplication implements EventListener{

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "BarStaffRejectedApplication"; }
    }

    //Organiser - personal notifications

    public abstract static class JobCreatedListener implements EventListener{

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "JobCreatedListener"; }
    }

    public abstract static class JobDeletedListener implements EventListener{

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "JobDeletedListener"; }
    }

    //Bar Staff events

    public abstract static class JobPostedListener implements EventListener{

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "JobPostedListener"; }
    }

    public abstract static class JobAcceptedListener implements EventListener{

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "JobAcceptedListener"; }
    }

    public abstract static class JobRejectedListener implements EventListener {

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "JobRejectedListener"; }
    }

    public abstract static class PrivateJobReceivedListener implements EventListener {

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "PrivateJobReceivedListener"; }
    }

    //Chat events

    public abstract static class ChatMessageReceivedListener implements EventListener{

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "ChatMessageReceivedListener"; }
    }

    public abstract static class JobPayRateOfferReceivedListener implements EventListener{

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "JobPayRateOfferReceivedListener"; }
    }

    public abstract static class JobPayRateOfferRejectedListener implements EventListener{

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "JobPayRateOfferRejectedListener"; }
    }

    public abstract static class ContractMadeListener implements EventListener{

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "ContractMadeListener"; }
    }

    public abstract static class JobCompletedListener implements EventListener{

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "JobCompletedListener"; }
    }

    //General events

    public abstract static class ProfileChangeListener implements EventListener{

        @Override
        public abstract void  onResponse(JSONObject object);

        @Override
        public String getName() { return "ProfileChangeListener"; }
    }

    // Payment

    public abstract static class  CardUpdatedListener implements EventListener {

        @Override
        public abstract void onResponse(JSONObject object);

        @Override
        public String getName() {return "CardUpdatedListener"; }
    }

    public abstract static class  CompletedProfileListener implements EventListener {

        @Override
        public abstract void onResponse(JSONObject object);

        @Override
        public String getName() {return "CompletedProfileListener"; }

    }

}
