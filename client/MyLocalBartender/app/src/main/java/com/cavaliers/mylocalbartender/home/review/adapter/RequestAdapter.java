package com.cavaliers.mylocalbartender.home.review.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

import java.util.ArrayList;

/**
 *  JobAdvert Adapter
 *  Core component to allow cyclerview to be functional
 *  Generates a list of adverts (jobs if SIGNIN as STAFF, profiles if SIGNIN as ORGANISER)
 *  each advert card is then filled with its relative content uploaded from the data-list
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ReviewHolder> {

    private ArrayList<Advert> requestsList;
    private LayoutInflater inflater;
    private Context context;private RecyclerView.LayoutManager lm;


    public RequestAdapter(ArrayList<Advert> list, Context context){
        this.inflater = LayoutInflater.from(context);
        this.requestsList = list;
        this.context = context;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        if(Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.ORGANISER)){
            view = inflater.inflate(R.layout.item_request_profile, parent, false);
        }
        else if(Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.BARSTAFF)){
            view = inflater.inflate(R.layout.item_request_job, parent, false);
        }

        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        Advert ad = requestsList.get(position);

        if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {

//            if(ad.getAdvert() == null){
//                requestsList.remove(position);
//                return;
//            }

            holder.tv_request_profile_title.setText(ad.getValue(MLBService.JSONKey.EVENT_TITLE));
            holder.tv_request_profile_username.setText(ad.getValue(MLBService.JSONKey.USERNAME));
            holder.tv_request_profile_speciality.setText(ad.getValue(MLBService.JSONKey.SPECIALITY));





//            holder.bt_request_profile_more.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//
//                    Intent intent = new Intent(context, ProfileDescriptionActivity.class);
//
//                    intent.putExtra(ProfileDescriptionActivity.USER_ID, ad.getAdvert().getId());
//                    intent.putExtra(ProfileDescriptionActivity.IMAGE, ad.getThumbnail());
//
//                    intent.putExtra(ProfileDescriptionActivity.USERNAME, ad.getUsername().toString());
//
//                    intent.putExtra(ProfileDescriptionActivity.SPECIALITY1, ad.getAdvert().getSpeciality());
////                    if (ad.getSpecialities()[1] != null) {
////                        intent.putExtra(JobDescriptionFragment.SPECIALITY2,ad.getSpecialities()[1]);
////                    }
////                    intent.putExtra(JobDescriptionFragment.SPECIALITY3, ad.getSpecialities()[2]);
////                    intent.putExtra(JobDescriptionFragment.SPECIALITY4, ad.getSpecialities()[3]);
//
//                    intent.putExtra(ProfileDescriptionActivity.DAYRATE, ad.getAdvert().getHourRate());
//                    intent.putExtra(ProfileDescriptionActivity.NIGHTRATE, ad.getAdvert().getNightRate());
//
//
////                    intent.putExtra(ProfileDescriptionActivity.AVAIL_MONDAY, ad.getAdvert().get...);
////                    intent.putExtra(ProfileDescriptionActivity.AVAIL_TUESDAY, ad.getAdvert().get...);
////                    intent.putExtra(ProfileDescriptionActivity.AVAIL_WEDNESDAY, ad.getAdvert().get...);
////                    intent.putExtra(ProfileDescriptionActivity.AVAIL_THURSDAY, ad.getAdvert().get...);
////                    intent.putExtra(ProfileDescriptionActivity.AVAIL_FRIDAY, ad.getAdvert().get...);
////                    intent.putExtra(ProfileDescriptionActivity.AVAIL_SATURDAY, ad.getAdvert().get...);
////                    intent.putExtra(ProfileDescriptionActivity.AVAIL_SUNDAY, ad.getAdvert().get...);
//
//                    intent.putExtra(ProfileDescriptionActivity.DESCRIPTION, ad.getAdvert().getDescription());
//                    intent.putExtra(ProfileDescriptionActivity.LOCATIION, ad.getAdvert().getLocation());
//
//                    context.startActivity(intent);
//
//                }
//            });
//            holder.bt_request_profile_decline.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    try {
//
//                        MLBService.sendRequest(context, MLBService.ACTION.DELETE_APPLICATION, new MLBService.MLBResponseListener() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                try {
//                                    System.out.println("withdraw");
//                                    removeItem(requestsList.indexOf(ad));
//                                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, new MLBService.MLBErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }, new JSONObject(), MLBService.getLoggedInUserID(), ad.getId());
//                    } catch (VolleyError volleyError) {
//                        volleyError.printStackTrace();
//                    }
//                    System.out.println("ARRAY SIZE IS===" + requestsList.size());
//
//                }
//            });
//
//            //Pass the staff_id
//            //job_id
//            final JSONObject acceptJson = new JSONObject();
//            try {
//                acceptJson.put(MLBService.JSONKey.STAFF_ID, ad.getAdvert().getId());
//                acceptJson.put(MLBService.JSONKey.JOB_ID, ad.getId());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            holder.bt_request_profile_accept.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        MLBService.sendRequest(
//                                context,
//                                MLBService.ACTION.ACCEPT_APPLICANT,
//                                new MLBService.MLBResponseListener() {
//                                    @Override
//                                    public void onResponse(JSONObject response) {
//
//                                    }
//                                },
//                                new MLBService.MLBErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//
//                                    }
//                                }, acceptJson, MLBService.getLoggedInUserID()
//                        );
//                    } catch (VolleyError volleyError) {
//                        volleyError.printStackTrace();
//                    }
//                }
//            });

        }

        else if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {


//            holder.ic_request_job_thumbnail.setImageResource("");

            holder.tv_request_job_title.setText(ad.getValue(MLBService.JSONKey.EVENT_TITLE));
            holder.tv_request_job_payrate.setText(ad.getValue(MLBService.JSONKey.HOUR_RATE));




//            holder.bt_request_job_more.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//
//                    Intent intent = new Intent(context, JobDescriptionActivity.class);
//                    intent.putExtra(JobDescriptionActivity.IMAGE, ad.getThumbnail());
//                    intent.putExtra(JobDescriptionActivity.TITLE, ((JobAdvert) ad).getEventTitle());
//                    intent.putExtra(JobDescriptionActivity.USERNAME, ad.getUsername().toString());
//                    intent.putExtra(JobDescriptionActivity.DAYRATE, ad.getHourRate());
//                    intent.putExtra(JobDescriptionActivity.NIGHTRATE, ad.getNightRate());
//                    intent.putExtra(JobDescriptionActivity.DATE, ((JobAdvert) ad).getEventDate());
//                    intent.putExtra(JobDescriptionActivity.TIME, ((JobAdvert) ad).getEventStartTime());
//                    intent.putExtra(JobDescriptionActivity.DURATION, ((JobAdvert) ad).getEventDuration());
//                    intent.putExtra(JobDescriptionActivity.SPECIALITY1, ad.getSpeciality());
////                    if (ad.getSpecialities()[1] != null) {
////                        intent.putExtra(JobDescriptionFragment.SPECIALITY2,ad.getSpecialities()[1]);
////                    }
////                    intent.putExtra(JobDescriptionFragment.SPECIALITY3, ad.getSpecialities()[2]);
////                    intent.putExtra(JobDescriptionFragment.SPECIALITY4, ad.getSpecialities()[3]);
//                    intent.putExtra(JobDescriptionActivity.TYPE, ((JobAdvert) ad).getEventType());
//                    intent.putExtra(JobDescriptionActivity.DESCRIPTION, ad.getDescription());
//                    intent.putExtra(JobDescriptionActivity.LOCATIION, ad.getLocation());
//
//                    context.startActivity(intent);
//
//                }
//            });
//            holder.bt_request_job_decline.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    try {
//
//                        MLBService.sendRequest(context, MLBService.ACTION.DELETE_APPLICATION, new MLBService.MLBResponseListener() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                try {
//                                    System.out.println("declined");
//                                    removeItem(requestsList.indexOf(ad));
//                                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, new MLBService.MLBErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }, new JSONObject(), MLBService.getLoggedInUserID(), ad.getId());
//                    } catch (VolleyError volleyError) {
//                        volleyError.printStackTrace();
//                    }
//
//                }
//            });

//            holder.bt_request_job_accept.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    try {
//
//                        MLBService.sendRequest(context, MLBService.ACTION.ACCEPT_APPLICANT, new MLBService.MLBResponseListener() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                try {
//                                    System.out.println("___JUST GOT ACCEPTED");
//                                    removeItem(requestsList.indexOf(ad));
//                                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, new MLBService.MLBErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }, new JSONObject(), MLBService.getLoggedInUserID(), ad.getId());
//                    } catch (VolleyError volleyError) {
//                        volleyError.printStackTrace();
//                    }
//
//
//                }
//            });

        }
    }

    /**
     * Replaces content of list with new given
     * @param adverts
     */
    public void setupList(ArrayList<Advert> adverts){
        this.requestsList = adverts;
        notifyDataSetChanged();
    }



    public void addItem(Advert advert){
        this.requestsList.add(advert);
        notifyItemInserted(requestsList.indexOf(advert));
    }



    public void removeItem(int index){
        this.requestsList.remove(index);
        notifyItemRemoved(index);
    }


    @Override
    public int getItemCount() {

        if(requestsList == null ) return 0;
        return requestsList.size();
    }














    /**
     * RECYCLER VIEW
     * Initialises advert card components and sets onClickListener to the card
     */
    class ReviewHolder extends RecyclerView.ViewHolder {

        //      ORGANISER VIEW
        private ImageView ic_request_profile_thumbnail;

        private TextView tv_request_profile_username;
        private TextView tv_request_profile_title;
        private TextView tv_request_profile_speciality;

        private Button bt_request_profile_more;
        private Button bt_request_profile_accept;
        private Button bt_request_profile_decline;


        //      STAFF VIEW
        private ImageView ic_request_job_thumbnail;

        private TextView tv_request_job_title;
        private TextView tv_request_job_payrate;

        private Button bt_request_job_more;
        private Button bt_request_job_accept;
        private Button bt_request_job_decline;



        public ReviewHolder(View itemView) {
            super(itemView);


//--------------------------------PROFILES
            if(Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.ORGANISER)){

                ic_request_profile_thumbnail = (ImageView) itemView.findViewById(R.id.ic_request_profile_thumbnail);

                tv_request_profile_title = (TextView) itemView.findViewById(R.id.tv_request_profile_title);
                tv_request_profile_username = (TextView) itemView.findViewById(R.id.tv_request_profile_username);
                tv_request_profile_speciality = (TextView) itemView.findViewById(R.id.tv_request_profile_speciality);

                bt_request_profile_more = (Button) itemView.findViewById(R.id.bt_request_profile_more);
                bt_request_profile_accept = (Button) itemView.findViewById(R.id.bt_request_profile_accept);
                bt_request_profile_decline = (Button) itemView.findViewById(R.id.bt_request_profile_decline);

            }

//--------------------------------JOBS
            else if(Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.BARSTAFF)){

                ic_request_job_thumbnail = (ImageView) itemView.findViewById(R.id.ic_request_job_thumbnail);

                tv_request_job_title = (TextView) itemView.findViewById(R.id.tv_request_job_title);
                tv_request_job_payrate = (TextView) itemView.findViewById(R.id.tv_request_job_payrate);

                bt_request_job_more = (Button) itemView.findViewById(R.id.bt_request_job_more);
                bt_request_job_accept = (Button) itemView.findViewById(R.id.bt_request_job_accept);
                bt_request_job_decline = (Button) itemView.findViewById(R.id.bt_request_job_decline);

            }
        }
    }
}
