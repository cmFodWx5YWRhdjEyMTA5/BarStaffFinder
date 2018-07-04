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
import com.cavaliers.mylocalbartender.home.review.AnswerFragment;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

import java.util.ArrayList;

/**
 * JobAdvert Adapter
 * Core component to allow cyclerview to be functional
 * Generates a list of adverts (jobs if SIGNIN as STAFF, profiles if SIGNIN as ORGANISER)
 * each advert card is then filled with its relative content uploaded from the data-list
 */

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerHolder> {

    private static ArrayList<Advert> answersList;
    private LayoutInflater inflater;
    private Context contx;

    private boolean isOrganiser = AccessVerificationTools.isOrganiser();
    private ViewGroup parent;
    private RecyclerView.LayoutManager lm;
    private AnswerFragment af;

    /**
     * Initialises local infalter, list and retrives context
     *
     * @param list
     * @param ctx
     */
    public AnswerAdapter(ArrayList<Advert> list, Context ctx, AnswerFragment af) {
        this.inflater = LayoutInflater.from(ctx);
        this.answersList = list;
        this.contx = ctx;
        this.af = af;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        notifyDataSetChanged();

    }

    @Override
    public AnswerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;

        View view = null;

        if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {
            view = inflater.inflate(R.layout.item_answer_profile, parent, false);
        } else if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {
            view = inflater.inflate(R.layout.item_answer_job, parent, false);
        }

        return new AnswerAdapter.AnswerHolder(view);
    }


    @Override
    public void onBindViewHolder(final AnswerHolder holder, final int position) {
        final Advert ad = answersList.get(position);

        int accepted = R.drawable.status_accepted;
        int pending = R.drawable.status_pending;
        int declined = R.drawable.status_declined;

// ______________NOT TOO SURE ABOUT THE STATUS______is it who's status??______________!!!!!!!
        int statusIcon = ad.getValue(MLBService.JSONKey.STATUS).equals("accepted") ? accepted
                : ad.getValue(MLBService.JSONKey.STATUS).equals("declined") ? declined : pending;
        String statusString = ad.getValue(MLBService.JSONKey.STATUS).equals("accepted") ? "ACCEPTED"
                : ad.getValue(MLBService.JSONKey.STATUS).equals("declined") ? "DECLINED" : "PENDING";


        if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {

            holder.tv_answer_profile_title.setText(ad.getValue(MLBService.JSONKey.TITLE));
            holder.tv_answer_profile_username.setText(ad.getValue(MLBService.JSONKey.USERNAME));
            holder.tv_answer_profile_speciality.setText(ad.getValue(MLBService.JSONKey.SPECIALITY));

            holder.ic_answer_profile_status.setImageResource(statusIcon);
            holder.tv_answer_profile_status.setText(statusString);

//            holder.bt_answer_profile_readMore.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//
//                    Intent intent = new Intent(contx, ProfileDescriptionActivity.class);
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
//                    contx.startActivity(intent);
//
//                }
//            });
//            holder.bt_answer_profile_withdraw.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    try {
//
//                        MLBService.sendRequest(contx, MLBService.ACTION.DELETE_APPLICATION, new MLBService.MLBResponseListener() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                try {
//                                    System.out.println("withdraw");
//                                    removeItem(answersList.indexOf(ad));
//                                    Toast.makeText(contx, response.getString("message"), Toast.LENGTH_SHORT).show();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, new MLBService.MLBErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Toast.makeText(contx, error.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }, new JSONObject(), MLBService.getLoggedInUserID(), ad.getId());
//                    } catch (VolleyError volleyError) {
//                        volleyError.printStackTrace();
//                    }
//                    System.out.println("ARRAY SIZE IS===" + answersList.size());
//
//                }
//            });
        }



        else if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {

            holder.tv_answer_job_title.setText(ad.getValue(MLBService.JSONKey.EVENT_TITLE));
            holder.tv_answer_job_payrate.setText(ad.getValue(MLBService.JSONKey.HOUR_RATE));

            holder.ic_answer_job_status.setImageResource(statusIcon);
            holder.tv_answer_job_status.setText(statusString);


//            holder.bt_answer_job_readMore.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//
//                    Intent intent = new Intent(contx, JobDescriptionActivity.class);
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
//                    contx.startActivity(intent);
//
//                }
//            });
//            holder.bt_answer_job_withdraw.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    try {
//
//                        MLBService.sendRequest(contx, MLBService.ACTION.DELETE_APPLICATION, new MLBService.MLBResponseListener() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                try {
//                                    System.out.println("withdrawn");
//                                    removeItem(answersList.indexOf(ad));
//                                    Toast.makeText(contx, response.getString("message"), Toast.LENGTH_SHORT).show();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, new MLBService.MLBErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Toast.makeText(contx, error.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }, new JSONObject(), MLBService.getLoggedInUserID(), ad.getId());
//                    } catch (VolleyError volleyError) {
//                        volleyError.printStackTrace();
//                    }
//
//                }
//            });

        }
    }

    /**
     * Replaces content of list with new given
     *
     * @param adverts
     */
    public void setAdverts(ArrayList<Advert> adverts) {
        this.answersList = adverts;
    }


    @Override
    public int getItemCount() {
        return answersList.size();
    }

    /**
     * Replaces content of list with new given
     *
     * @param adverts
     */
    public void setupList(ArrayList<Advert> adverts) {
        this.answersList = adverts;
        notifyDataSetChanged();
    }


    public void addItem(Advert advert) {
        this.answersList.add(advert);
        notifyItemInserted(answersList.indexOf(advert));
    }


    public void removeItem(int index) {
        this.answersList.remove(index);
        notifyItemRemoved(index);
    }












    /**
     * RECYCLER VIEW
     * Initialises advert card components and sets onClickListener to the card
     */
    class AnswerHolder extends RecyclerView.ViewHolder {

        //        ORGANISER VIEW
        private ImageView ic_answer_profile_thumbnail;

        private TextView tv_answer_profile_title;
        private TextView tv_answer_profile_username;
        private TextView tv_answer_profile_speciality;

        private ImageView ic_answer_profile_status;
        private TextView tv_answer_profile_status;

        private Button bt_answer_profile_readMore;
        private Button bt_answer_profile_withdraw;



        //        BAR STAFF VIEW
        private ImageView ic_answer_job_thumbnail;

        private TextView tv_answer_job_title;
        private TextView tv_answer_job_payrate;

        private ImageView ic_answer_job_status;
        private TextView tv_answer_job_status;

        private Button bt_answer_job_readMore;
        private Button bt_answer_job_withdraw;



        /**
         * initialises all advert card components
         *
         * @param itemView
         */
        public AnswerHolder(View itemView) {
            super(itemView);

//-----------------------------PROFILES
            if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {

                tv_answer_profile_title = (TextView) itemView.findViewById(R.id.tv_answer_profile_title);
                tv_answer_profile_username = (TextView) itemView.findViewById(R.id.tv_answer_profile_username);
                tv_answer_profile_speciality = (TextView) itemView.findViewById(R.id.tv_answer_profile_speciality);

                ic_answer_profile_status = (ImageView) itemView.findViewById(R.id.ic_answer_profile_status);
                tv_answer_profile_status = (TextView) itemView.findViewById(R.id.tv_answer_profile_status);

                bt_answer_profile_readMore = (Button) itemView.findViewById(R.id.bt_answer_profile_read_more);
                bt_answer_profile_withdraw = (Button) itemView.findViewById(R.id.bt_answer_profile_withdraw);

            }
//-----------------------------JOBS
            else if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {

                ic_answer_job_thumbnail = (ImageView) itemView.findViewById(R.id.ic_answer_job_thumbnail);

                tv_answer_job_title = (TextView) itemView.findViewById(R.id.tv_answer_job_title);
                tv_answer_job_payrate = (TextView) itemView.findViewById(R.id.tv_answer_job_payrate);

                ic_answer_job_status = (ImageView) itemView.findViewById(R.id.ic_answer_job_status);
                tv_answer_job_status = (TextView) itemView.findViewById(R.id.tv_answer_job_status);

                bt_answer_job_readMore = (Button) itemView.findViewById(R.id.bt_answer_job_read_more);
                bt_answer_job_withdraw = (Button) itemView.findViewById(R.id.bt_answer_job_withdraw);

            }
        }
    }
}
