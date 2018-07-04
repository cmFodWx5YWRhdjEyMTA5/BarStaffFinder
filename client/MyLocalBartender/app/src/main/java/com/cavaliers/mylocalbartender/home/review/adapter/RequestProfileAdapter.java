package com.cavaliers.mylocalbartender.home.review.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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


public class RequestProfileAdapter extends RecyclerView.Adapter<RequestProfileAdapter.RequestProfileViewHolder>{

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Advert> data;

    public RequestProfileAdapter(ArrayList<Advert> list, Context context){
        this.context = context;
        this.data = list;

    }

    protected class RequestProfileViewHolder extends RecyclerView.ViewHolder {
        //stuff to display
        public LinearLayout fragment_layout;
        public TextView job_title;
        public TextView staff_username;
        public TextView staff_speciality;
        public ImageView staff_image;

        //buttons
        public Button btnReadMore;
        public Button btnAccept;
        public Button btnDecline;

        public RequestProfileViewHolder(View itemView) {
            super(itemView);
            //Initialise items in the view
            job_title = (TextView) itemView.findViewById(R.id.tv_request_profile_title);
            staff_username = (TextView) itemView.findViewById(R.id.tv_request_profile_username);
            staff_speciality = (TextView) itemView.findViewById(R.id.tv_request_profile_speciality);
            staff_image = (ImageView) itemView.findViewById(R.id.ic_request_profile_thumbnail);

            btnReadMore = (Button) itemView.findViewById(R.id.bt_request_profile_more);
            btnAccept = (Button) itemView.findViewById(R.id.bt_request_profile_accept);
            btnDecline = (Button) itemView.findViewById(R.id.bt_request_profile_decline);

            fragment_layout = (LinearLayout) itemView.findViewById(R.id.fragment_review_request_layout);


        }
    }


    @Override
    public RequestProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request_profile, parent, false);
        RequestProfileViewHolder viewHolder = new RequestProfileViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RequestProfileAdapter.RequestProfileViewHolder holder, final int position) {
        final Advert advert = data.get(position);


//        holder.job_title.setText(data.get(position).getValue(MLBService.JSONKey.EVENT_TITLE));
        holder.job_title.setText(advert.getValue(MLBService.JSONKey.EVENT_TITLE));
        holder.staff_username.setText(advert.getValue(MLBService.JSONKey.USERNAME));
        holder.staff_speciality.setText(advert.getValue(MLBService.JSONKey.SPECIALITY));

        holder.btnReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<AvailabilityDay> availabilityDays = AvailabilityCalendarData.getAvailabilityData().get(advert.getValue(MLBService.JSONKey.USER_ID));

                Intent intent = new Intent(context, ProfileDescriptionActivity.class);

                intent.putExtra(ProfileDescriptionActivity.USER_ID, data.get(position).getValue(MLBService.JSONKey.STAFF_ID));
                intent.putExtra(ProfileDescriptionActivity.IMAGE, data.get(position).getValue(MLBService.JSONKey.IMAGE_PATH));

                intent.putExtra(ProfileDescriptionActivity.USERNAME, data.get(position).getValue(MLBService.JSONKey.USERNAME));

                intent.putExtra(ProfileDescriptionActivity.SPECIALITY1, data.get(position).getValue(MLBService.JSONKey.SPECIALITY));
                intent.putExtra(ProfileDescriptionActivity.DAYRATE, data.get(position).getValue(MLBService.JSONKey.HOUR_RATE));
                intent.putExtra(ProfileDescriptionActivity.NIGHTRATE, data.get(position).getValue(MLBService.JSONKey.NIGHT_RATE));

                intent.putExtra(ProfileDescriptionActivity.MESSAGE, data.get(position).getValue(MLBService.JSONKey.MESSAGE));

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


                intent.putExtra(ProfileDescriptionActivity.DESCRIPTION, data.get(position).getValue(MLBService.JSONKey.DESCRIPTION));
                intent.putExtra(ProfileDescriptionActivity.LOCATIION, data.get(position).getValue(MLBService.JSONKey.LOCATION));

                context.startActivity(intent);

            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(MLBService.JSONKey.STAFF_ID, data.get(position).getValue(MLBService.JSONKey.STAFF_ID));
//                    jsonObject.put(MLBService.JSONKey.STAFF_ID, "4566");
                    jsonObject.put(MLBService.JSONKey.JOB_ID, data.get(position).getValue(MLBService.JSONKey.JOB_ID));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    MLBService.sendRequest(
                            context,
                            MLBService.ACTION.ACCEPT_APPLICANT,
                            new MLBService.MLBResponseListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    removeItem(data.indexOf(advert));
                                    System.out.println("ACCEPT_APPLICANT RESPONSE: \n" + response);
                                    System.out.println(".");

                                }
                            },
                            new MLBService.MLBErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            }, jsonObject, MLBService.getLoggedInUserID()
                    );
//                    removeItem(data.indexOf(advert));
                } catch (VolleyError volleyError) {
                    volleyError.printStackTrace();
                }
            }
        });

        holder.btnDecline.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void removeItem(int position) {
        this.data.remove(position);
//        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

}
