package com.cavaliers.mylocalbartender.home.search.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.home.search.CoverLetterActivity;
import com.cavaliers.mylocalbartender.home.search.SearchPageFragment;
import com.cavaliers.mylocalbartender.home.search.handlers.InviteButtonHandler;
import com.cavaliers.mylocalbartender.home.search.helper.FilterHelper;
import com.cavaliers.mylocalbartender.home.search.model.AdvertData;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;
import com.cavaliers.mylocalbartender.tools.advert.Advert;
import com.cavaliers.mylocalbartender.tools.cavs_widgets.availability_calendar.AvailabilityCalendarData;
import com.cavaliers.mylocalbartender.tools.cavs_widgets.availability_calendar.AvailabilityDay;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * JobAdvert Adapter
 * Core component to allow cyclerview to be functional
 * Generates a list of adverts (jobs if SIGNIN as STAFF, profiles if SIGNIN as ORGANISER)
 * each advert card is then filled with its relative content uploaded from the data-list
 */

public class AdvertAdapter extends RecyclerView.Adapter<AdvertAdapter.AdvertHolder> implements Filterable {

    private static ArrayList<Advert> currentAdvertList;
    private static ArrayList<Advert> filteredList;
    private LayoutInflater inflater;
    private Context contx;
    private SearchPageFragment sp;

    /**
     * Initialises local infalter, list and retrives context
     *
     * @param list
     * @param ctx
     */
    public AdvertAdapter(ArrayList<Advert> list, Context ctx, SearchPageFragment sp) {
        this.inflater = LayoutInflater.from(ctx);
        AdvertAdapter.currentAdvertList = list;
        AdvertAdapter.filteredList = list;
        this.contx = ctx;
        this.sp = sp;
    }

    @Override
    public AdvertHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        //Verifies user profile and displays advertising item
        if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {
            view = inflater.inflate(R.layout.item_advert_profile, parent, false);
        } else if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {
            view = inflater.inflate(R.layout.item_advert_job, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_advert_job, parent, false);
        }

        return new AdvertHolder(view);
    }


    @Override
    public void onBindViewHolder(final AdvertHolder holder, final int position) {
        final Advert ad = currentAdvertList.get(position);


        if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {
            holder.tv_advert_profile_username.setText(ad.getValue(MLBService.JSONKey.USERNAME));
            holder.tv_advert_profile_speciality.setText(ad.getValue(MLBService.JSONKey.SPECIALITY));
            holder.tv_advert_profile_payrate.setText(AccessVerificationTools.getParsedRate(ad.getValue(MLBService.JSONKey.HOUR_RATE)));
            holder.tv_advert_profile_description.setText(ad.getValue(MLBService.JSONKey.DESCRIPTION));
//            holder.tv_ad_location.setText(ad.getLocation());
            holder.bt_advert_profile_invite.setTag(ad.getValue(MLBService.JSONKey.USER_ID));
            holder.bt_advert_profile_invite.setOnClickListener(new InviteButtonHandler(holder.bt_advert_profile_invite, contx));
            // holder.tv_ad_monday.setText();


            ArrayList<AvailabilityDay> availabilityDays = AvailabilityCalendarData.getAvailabilityData().get(ad.getValue(MLBService.JSONKey.USER_ID));

            System.out.println("________THIS IS THE ARRAY="+availabilityDays);

            if(availabilityDays != null)
            for(int i=0;i<availabilityDays.size();++i){
                System.out.println("________looping at="+i);

                if(availabilityDays.get(i).getDay().equals("Monday")){
                    holder.tv_advert_profile_monday_avail_from.setText(availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation());
                    holder.tv_advert_profile_monday_avail_to.setText(availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation());
                    System.out.println("________monday is="+availabilityDays.get(i).getStartTime());
                }
                else if(availabilityDays.get(i).getDay().equals("Tuesday")){
                    holder.tv_advert_profile_tueday_avail_from.setText(availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation());
                    holder.tv_advert_profile_tueday_avail_to.setText(availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation());
                    System.out.println("________tueday is="+availabilityDays.get(i).getStartTime());
                }
                else if(availabilityDays.get(i).getDay().equals("Wednesday")){
                    holder.tv_advert_profile_wednesday_avail_from.setText(availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation());
                    holder.tv_advert_profile_wednesday_avail_to.setText(availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation());
                }
                else if(availabilityDays.get(i).getDay().equals("Thursday")){
                    holder.tv_advert_profile_thursday_avail_from.setText(availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation());
                    holder.tv_advert_profile_thursday_avail_to.setText(availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation());
                }
                else if(availabilityDays.get(i).getDay().equals("Friday")){
                    holder.tv_advert_profile_friday_avail_from.setText(availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation());
                    holder.tv_advert_profile_friday_avail_to.setText(availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation());
                }
                else if(availabilityDays.get(i).getDay().equals("Saturday")){
                    holder.tv_advert_profile_saturday_avail_from.setText(availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation());
                    holder.tv_advert_profile_saturday_avail_to.setText(availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation());
                }
                else if(availabilityDays.get(i).getDay().equals("Sunday")){
                    holder.tv_advert_profile_sunday_avail_from.setText(availabilityDays.get(i).getStartTime()+" "+availabilityDays.get(i).getStartTimeRelation());
                    holder.tv_advert_profile_sunday_avail_to.setText(availabilityDays.get(i).getEndTime()+" "+availabilityDays.get(i).getEndTimeRelation());
                }



            }


        } else if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {
            holder.tv_advert_job_username.setText(ad.getValue(MLBService.JSONKey.USERNAME));

            holder.tv_advert_job_time.setText(AccessVerificationTools.getTimeFromDate(ad.getValue(MLBService.JSONKey.JOB_START)));//format
            holder.tv_advert_job_duration.setText(AccessVerificationTools.getParsedDuration(ad.getValue(MLBService.JSONKey.DURATION)));//format
            holder.tv_advert_job_title.setText(ad.getValue(MLBService.JSONKey.TITLE));
            holder.tv_advert_job_event_type.setText(ad.getValue(MLBService.JSONKey.TYPE));
            holder.tv_advert_job_date.setText(AccessVerificationTools.getParsedDate(ad.getValue(MLBService.JSONKey.JOB_START)));



            holder.tv_advert_job_description.setText(ad.getValue(MLBService.JSONKey.DESCRIPTION));
            holder.tv_advert_job_datePosted.setText(AccessVerificationTools.getParsedDate(ad.getValue(MLBService.JSONKey.JOB_CREATION)));
            holder.tv_advert_job_location.setText(ad.getValue(MLBService.JSONKey.LOCATION));
            holder.tv_advert_job_speciality.setText(ad.getValue(MLBService.JSONKey.SPECIALITY));
            holder.tv_advert_job_payrate.setText(ad.getValue(MLBService.JSONKey.HOUR_RATE));


            holder.bt_advert_job_apply.setTag(ad.getValue(MLBService.JSONKey.JOB_ID));
            holder.bt_advert_job_apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(contx, CoverLetterActivity.class);
                    intent.putExtra(CoverLetterActivity.TITLE, ad.getValue(MLBService.JSONKey.TITLE));
                    intent.putExtra(CoverLetterActivity.ORGANISER, ad.getValue(MLBService.JSONKey.USERNAME));
                    intent.putExtra(CoverLetterActivity.PAYRATE, ad.getValue(MLBService.JSONKey.JOB_RATE));
                    intent.putExtra(CoverLetterActivity.DATE, ad.getValue(MLBService.JSONKey.JOB_START));
                    intent.putExtra(CoverLetterActivity.TIME, ad.getValue(MLBService.JSONKey.START_TIME));
                    intent.putExtra(CoverLetterActivity.DURATION, ad.getValue(MLBService.JSONKey.DURATION));
                    intent.putExtra(CoverLetterActivity.LOCATION, ad.getValue(MLBService.JSONKey.LOCATION));
                    intent.putExtra(CoverLetterActivity.ID, ad.getValue(MLBService.JSONKey.JOB_ID));
                    CoverLetterActivity.getAdvert(ad);
                    contx.startActivity(intent);
                }
            });


//            holder.tv_ad_organiser.setText(ad.getUsername());

        }
//        holder.tv_ad_description.setText(ad.getDescription());


        // holder.tv_ad_location.setText(ad.getLocation());

    }


    /**
     * Replaces content of list with new given
     *
     * @param adverts
     */
    public void setAdverts(ArrayList<Advert> adverts) {
        AdvertAdapter.currentAdvertList = adverts;
    }


    @Override
    public int getItemCount() {
        return AdvertAdapter.currentAdvertList.size();
    }

    @Override
    public Filter getFilter() {
        return FilterHelper.getFilter(filteredList, this);
    }

    public static void setFilteredList(ArrayList<Advert> list) {

        filteredList = list;

    }


    public static void resetAdvertList() {

        filteredList = (ArrayList<Advert>) AdvertData.getAdverts().clone();
        currentAdvertList = filteredList;

    }


    /**
     * RECYCLER VIEW
     * Initialises advert card components and sets onClickListener to the card
     */
    class AdvertHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//__________________ORGANISER VIEW

        private ImageView ic_advert_profile_thumbnail;

        private TextView tv_advert_profile_username;
        private TextView tv_advert_profile_payrate;

        private TextView tv_advert_profile_speciality;
        private TextView tv_advert_profile_description;
        private TextView tv_advert_profile_location;


        private TextView tv_advert_profile_monday_avail_from;
        private TextView tv_advert_profile_monday_avail_to;

        private TextView tv_advert_profile_tueday_avail_from;
        private TextView tv_advert_profile_tueday_avail_to;

        private TextView tv_advert_profile_wednesday_avail_from;
        private TextView tv_advert_profile_wednesday_avail_to;

        private TextView tv_advert_profile_thursday_avail_from;
        private TextView tv_advert_profile_thursday_avail_to;

        private TextView tv_advert_profile_friday_avail_from;
        private TextView tv_advert_profile_friday_avail_to;

        private TextView tv_advert_profile_saturday_avail_from;
        private TextView tv_advert_profile_saturday_avail_to;

        private TextView tv_advert_profile_sunday_avail_from;
        private TextView tv_advert_profile_sunday_avail_to;

        private Button bt_advert_profile_invite;
        private LinearLayout ll_advert_profile_description_container;
        private CardView cv_advert_profile_card;


        //__________________BAR STAFF VIEW
        private ImageView ic_advert_job_thumbnail;

        private TextView tv_advert_job_title;
        private TextView tv_advert_job_username;
        private TextView tv_advert_job_payrate;

        private TextView tv_advert_job_date;
        private TextView tv_advert_job_time;
        private TextView tv_advert_job_duration;

        private TextView tv_advert_job_speciality;
        private TextView tv_advert_job_event_type;

        private TextView tv_advert_job_description;
        private Button bt_advert_job_apply;

        private TextView tv_advert_job_location;
        private TextView tv_advert_job_datePosted;
        private LinearLayout ll_advert_job_description_container;
        private CardView cv_advert_job_card;


        private int containerHeight = 0;
        private boolean isExpanded = false;


        //needed for animation
        ValueAnimator valueAnimator;

        /**
         * initialises all advert card components
         *
         * @param itemView
         */
        public AdvertHolder(View itemView) {
            super(itemView);


            if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {

                tv_advert_profile_username = (TextView) itemView.findViewById(R.id.tv_advert_profile_username);
                tv_advert_profile_speciality = (TextView) itemView.findViewById(R.id.tv_advert_profile_speciality);
                tv_advert_profile_payrate = (TextView) itemView.findViewById(R.id.tv_advert_profile_payrate);
                tv_advert_profile_description = (TextView) itemView.findViewById(R.id.tv_advert_profile_description);
                bt_advert_profile_invite = (Button) itemView.findViewById(R.id.bt_advert_profile_invite);


                tv_advert_profile_monday_avail_from = (TextView) itemView.findViewById(R.id.tv_advert_profile_monday_avail_from);
                tv_advert_profile_monday_avail_to = (TextView) itemView.findViewById(R.id.tv_advert_profile_monday_avail_to);

                tv_advert_profile_tueday_avail_from = (TextView) itemView.findViewById(R.id.tv_advert_profile_tuesday_avail_from);
                tv_advert_profile_tueday_avail_to = (TextView) itemView.findViewById(R.id.tv_advert_profile_tuesday_avail_to);

                tv_advert_profile_wednesday_avail_from = (TextView) itemView.findViewById(R.id.tv_advert_profile_wednesday_avail_from);
                tv_advert_profile_wednesday_avail_to = (TextView) itemView.findViewById(R.id.tv_advert_profile_wednesday_avail_to);

                tv_advert_profile_thursday_avail_from = (TextView) itemView.findViewById(R.id.tv_advert_profile_thursday_avail_from);
                tv_advert_profile_thursday_avail_to = (TextView) itemView.findViewById(R.id.tv_advert_profile_thursday_avail_to);

                tv_advert_profile_friday_avail_from = (TextView) itemView.findViewById(R.id.tv_advert_profile_friday_avail_from);
                tv_advert_profile_friday_avail_to = (TextView) itemView.findViewById(R.id.tv_advert_profile_friday_avail_to);

                tv_advert_profile_saturday_avail_from = (TextView) itemView.findViewById(R.id.tv_advert_profile_saturday_avail_from);
                tv_advert_profile_saturday_avail_to = (TextView) itemView.findViewById(R.id.tv_advert_profile_saturday_avail_to);

                tv_advert_profile_sunday_avail_from = (TextView) itemView.findViewById(R.id.tv_advert_profile_sunday_avail_from);
                tv_advert_profile_sunday_avail_to = (TextView) itemView.findViewById(R.id.tv_advert_profile_sunday_avail_to);

                ll_advert_profile_description_container = (LinearLayout) itemView.findViewById(R.id.ll_advert_profile_description_container);

                cv_advert_profile_card = (CardView) itemView.findViewById(R.id.cv_advert_profile_root);
                cv_advert_profile_card.setOnClickListener(this);
            } else if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {

                ic_advert_job_thumbnail = (ImageView) itemView.findViewById(R.id.ic_advert_job_thumbnail);
                tv_advert_job_title = (TextView) itemView.findViewById(R.id.tv_advert_job_title);
                tv_advert_job_username = (TextView) itemView.findViewById(R.id.tv_advert_job_username);
                tv_advert_job_payrate = (TextView) itemView.findViewById(R.id.tv_advert_job_payrate);
                tv_advert_job_date = (TextView) itemView.findViewById(R.id.tv_advert_job_date);
                tv_advert_job_time = (TextView) itemView.findViewById(R.id.tv_advert_job_time);
                tv_advert_job_duration = (TextView) itemView.findViewById(R.id.tv_advert_job_duration);
                tv_advert_job_event_type = (TextView) itemView.findViewById(R.id.tv_advert_job_event_type);
                tv_advert_job_speciality = (TextView) itemView.findViewById(R.id.tv_advert_job_speciality);
                tv_advert_job_description = (TextView) itemView.findViewById(R.id.tv_advert_job_description);
                tv_advert_job_datePosted = (TextView) itemView.findViewById(R.id.tv_advert_job_datePosted);
                tv_advert_job_location = (TextView) itemView.findViewById(R.id.tv_advert_job_location);
                bt_advert_job_apply = (Button) itemView.findViewById(R.id.bt_advert_job_apply);

                ll_advert_job_description_container = (LinearLayout) itemView.findViewById(R.id.ll_advert_job_description_container);

                cv_advert_job_card = (CardView) itemView.findViewById(R.id.cv_advert_job_root);
                cv_advert_job_card.setOnClickListener(this);

            } else {
                tv_advert_job_location = (TextView) itemView.findViewById(R.id.tv_advert_job_location);
                tv_advert_job_description = (TextView) itemView.findViewById(R.id.tv_advert_job_description);
                tv_advert_job_title = (TextView) itemView.findViewById(R.id.tv_advert_job_title);
                tv_advert_job_username = (TextView) itemView.findViewById(R.id.tv_advert_job_username);
                tv_advert_job_payrate = (TextView) itemView.findViewById(R.id.tv_advert_job_payrate);
                tv_advert_job_date = (TextView) itemView.findViewById(R.id.tv_advert_job_date);
                ic_advert_job_thumbnail = (ImageView) itemView.findViewById(R.id.ic_advert_job_thumbnail);

                ll_advert_job_description_container = (LinearLayout) itemView.findViewById(R.id.ll_advert_job_description_container);

                cv_advert_job_card = (CardView) itemView.findViewById(R.id.cv_advert_job_root);
                cv_advert_job_card.setOnClickListener(this);
            }


        }


        @Override
        public void onClick(final View v) {
            if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {

                expandCard(ll_advert_profile_description_container, cv_advert_profile_card, tv_advert_profile_description, v);
                System.out.println("---LOGGED AS ORGANISER CLICK");


            } else if (Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {
                expandCard(ll_advert_job_description_container, cv_advert_job_card, tv_advert_job_description, v);
            }


        }


        public void expandCard(final LinearLayout layout, final CardView cardView, final TextView description, final View view) {
            sp.getLayoutManager().scrollToPositionWithOffset(getLayoutPosition(), view.getTop());

            //sets description container visible
            layout.setVisibility(View.VISIBLE);

            //Thread was needed to retrive line numbers... It can be removed if needed
            description.post(new Runnable() {
                @Override
                public void run() {
                    int lines = description.getLineCount();
                    Toast.makeText(contx, "LINES ARE = " + description.getLineCount(), Toast.LENGTH_SHORT).show();

                    //checks if layout size has changed
                    if (containerHeight == 0) {
                        containerHeight = cardView.getHeight();
                    }


                    //checks if card has been expanded/opened
                    if (!isExpanded) {
                        isExpanded = true;

                        //sets size for layout expansion
                        valueAnimator = ValueAnimator.ofInt(containerHeight, (int) (containerHeight * 1.15));

                        //sets layout visible
                        layout.setVisibility(View.VISIBLE);

                    } else {
                        isExpanded = false;

                        //turns size back to original
                        valueAnimator = ValueAnimator.ofInt(containerHeight + ((int) (containerHeight * 1.25)), containerHeight - ((int) (containerHeight * 1.5)));


                        //sets layout invisible
                        layout.setVisibility(View.GONE);
                    }
                    //duration set to ZERO as if not present an animation delay tv_time is by default
                    valueAnimator.setDuration(0);
                    valueAnimator.setInterpolator(new LinearInterpolator());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            Integer value = (Integer) animation.getAnimatedValue();
                            view.getLayoutParams().height = value.intValue();
                            view.requestLayout();
                        }
                    });
                    valueAnimator.start();

                }
            });

        }
    }
}
