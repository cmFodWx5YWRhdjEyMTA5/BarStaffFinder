package com.cavaliers.mylocalbartender.home.review.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.home.review.ProfileDescriptionActivity;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.advert.Advert;
import com.cavaliers.mylocalbartender.tools.cavs_widgets.availability_calendar.AvailabilityCalendarData;
import com.cavaliers.mylocalbartender.tools.cavs_widgets.availability_calendar.AvailabilityDay;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AnswerProfileAdapter extends RecyclerView.Adapter<AnswerProfileAdapter.RequestProfileViewHolder>{

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Advert> data;

    public AnswerProfileAdapter(ArrayList<Advert> list, Context context){
        this.context = context;
        this.data = list;

    }

    protected class RequestProfileViewHolder extends RecyclerView.ViewHolder {
        //stuff to display
        public LinearLayout fragment_layout;
        public TextView job_title;
        public TextView username;
        public TextView speciality;
        //buttons
        public Button btnReadMore;
        public Button btnWithdraw;

        public RequestProfileViewHolder(View itemView) {
            super(itemView);
            //Initialise items in the view
            job_title = (TextView) itemView.findViewById(R.id.tv_answer_profile_title);
            username = (TextView) itemView.findViewById(R.id.tv_answer_profile_username);
            speciality = (TextView) itemView.findViewById(R.id.tv_answer_profile_speciality);

            btnReadMore = (Button) itemView.findViewById(R.id.bt_answer_profile_read_more);
            btnWithdraw = (Button) itemView.findViewById(R.id.bt_answer_profile_withdraw);

            fragment_layout = (LinearLayout) itemView.findViewById(R.id.fragment_review_answer_layout);


        }
    }


    @Override
    public RequestProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_answer_profile, parent, false);
        RequestProfileViewHolder viewHolder = new RequestProfileViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AnswerProfileAdapter.RequestProfileViewHolder holder, final int position) {
        final Advert advert = data.get(position);
//        holder.job_title.setText(data.get(position).getValue(MLBService.JSONKey.EVENT_TITLE));
        holder.job_title.setText(data.get(position).getValue(MLBService.JSONKey.EVENT_TITLE));
        holder.username.setText(data.get(position).getValue(MLBService.JSONKey.USERNAME));
        holder.speciality.setText(data.get(position).getValue(MLBService.JSONKey.SPECIALITY));


        holder.btnReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ArrayList<AvailabilityDay> availabilityDays = AvailabilityCalendarData.getAvailabilityData().get(advert.getValue(MLBService.JSONKey.USER_ID));

                Intent intent = new Intent(context, ProfileDescriptionActivity.class);

                intent.putExtra(ProfileDescriptionActivity.USER_ID, data.get(position).getValue(MLBService.JSONKey.ORG_ID));
                intent.putExtra(ProfileDescriptionActivity.IMAGE, data.get(position).getValue(MLBService.JSONKey.IMAGE_PATH));

                intent.putExtra(ProfileDescriptionActivity.USERNAME, data.get(position).getValue(MLBService.JSONKey.USERNAME));

                intent.putExtra(ProfileDescriptionActivity.SPECIALITY1, data.get(position).getValue(MLBService.JSONKey.SPECIALITY));
//                    if (ad.getSpecialities()[1] != null) {
//                        intent.putExtra(JobDescriptionFragment.SPECIALITY2,ad.getSpecialities()[1]);
//                    }
//                    intent.putExtra(JobDescriptionFragment.SPECIALITY3, ad.getSpecialities()[2]);
//                    intent.putExtra(JobDescriptionFragment.SPECIALITY4, ad.getSpecialities()[3]);

                intent.putExtra(ProfileDescriptionActivity.DAYRATE, data.get(position).getValue(MLBService.JSONKey.HOUR_RATE));
                intent.putExtra(ProfileDescriptionActivity.NIGHTRATE, data.get(position).getValue(MLBService.JSONKey.NIGHT_RATE));

                for(int i=0;i<availabilityDays.size();++i){

                    switch (availabilityDays.get(i).getDay()){
                        case "Monday":
                            String mondayFrom = availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation();
                            String mondayTo = availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation();
                            intent.putExtra(ProfileDescriptionActivity.AVAIL_MONDAY, mondayFrom+" - "+mondayTo);
                            ;break;
                        case "Tuesday":
                            String tuesdayFrom = availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation();
                            String tuesdayTo = availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation();
                            intent.putExtra(ProfileDescriptionActivity.AVAIL_TUESDAY, tuesdayFrom+" - "+tuesdayTo);
                            ;break;
                        case "Wednesday":
                            String wednesdayFrom = availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation();
                            String wednesdayTo = availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation();
                            intent.putExtra(ProfileDescriptionActivity.AVAIL_WEDNESDAY, wednesdayFrom+" - "+wednesdayTo);
                            ;break;
                        case "Thursday":
                            String thursdayFrom = availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation();
                            String thursdayTo = availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation();
                            intent.putExtra(ProfileDescriptionActivity.AVAIL_THURSDAY, thursdayFrom+" - "+thursdayTo);
                            ;break;
                        case "Friday":
                            String fridayFrom = availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation();
                            String fridayTo = availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation();
                            intent.putExtra(ProfileDescriptionActivity.AVAIL_FRIDAY, fridayFrom+" - "+fridayTo);
                            ;break;
                        case "Saturday":
                            String saturdayFrom = availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation();
                            String saturdayTo = availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation();
                            intent.putExtra(ProfileDescriptionActivity.AVAIL_SATURDAY, saturdayFrom+" - "+saturdayTo);
                            ;break;
                        case "Sunday":
                            String sundayFrom = availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation();
                            String sundayTo = availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation();
                            intent.putExtra(ProfileDescriptionActivity.AVAIL_SUNDAY, sundayFrom+" - "+sundayTo);
                            ;break;
                    }

                    }

//                    if(availabilityDays.get(i).getDay().equals("Monday")){
//                    String mondayFrom = availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation();
//                    String mondayTo = availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation();
//                    intent.putExtra(ProfileDescriptionActivity.AVAIL_MONDAY, mondayFrom+" - "+mondayTo);
//                    intent.putExtra(ProfileDescriptionActivity.AVAIL_TUESDAY, ad.getAdvert().get...);
//                    intent.putExtra(ProfileDescriptionActivity.AVAIL_WEDNESDAY, ad.getAdvert().get...);
//                    intent.putExtra(ProfileDescriptionActivity.AVAIL_THURSDAY, ad.getAdvert().get...);
//                    intent.putExtra(ProfileDescriptionActivity.AVAIL_FRIDAY, ad.getAdvert().get...);
//                    intent.putExtra(ProfileDescriptionActivity.AVAIL_SATURDAY, ad.getAdvert().get...);
//                    intent.putExtra(ProfileDescriptionActivity.AVAIL_SUNDAY, ad.getAdvert().get...);
//                }



                intent.putExtra(ProfileDescriptionActivity.DESCRIPTION, data.get(position).getValue(MLBService.JSONKey.DESCRIPTION));
                intent.putExtra(ProfileDescriptionActivity.LOCATIION, data.get(position).getValue(MLBService.JSONKey.LOCATION));

                context.startActivity(intent);

            }
        });

        holder.btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(MLBService.JSONKey.STAFF_ID, data.get(position).getValue(MLBService.JSONKey.STAFF_ID));
//                    jsonObject.put(MLBService.JSONKey.STAFF_ID, "4566");
                    jsonObject.put(MLBService.JSONKey.JOB_ID, data.get(position).getValue(MLBService.JSONKey.JOB_ID));
                    System.out.println("ASS ASS ASS ASS ASS ASS ASS ------------------------ STAFF ID: " + data.get(position).getValue(MLBService.JSONKey.STAFF_ID));
                    System.out.println("ASS ASS ASS ASS ASS ASS ASS ------------------------ JOB ID: " + data.get(position).getValue(MLBService.JSONKey.JOB_ID));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    MLBService.sendRequest(
                            context,
                            MLBService.ACTION.REJECT_APPLICANT,
                            new MLBService.MLBResponseListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    System.out.println("withdrawn");
                                    removeItem(data.indexOf(advert));
                                }
                            },
                            new MLBService.MLBErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            }, jsonObject, MLBService.getLoggedInUserID());

                } catch (VolleyError volleyError) {
                    volleyError.printStackTrace();
                }
            }
        });
    }

    public void removeItem(int position) {
        this.data.remove(position);
//        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
