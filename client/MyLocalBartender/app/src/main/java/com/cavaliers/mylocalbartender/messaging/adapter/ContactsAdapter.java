package com.cavaliers.mylocalbartender.messaging.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.home.review.JobDescriptionActivity;
import com.cavaliers.mylocalbartender.messaging.ContactsFragment;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

import java.util.ArrayList;

/**
 *  JobAdvert Adapter
 *  Core component to allow cyclerview to be functional
 *  Generates a list of adverts (jobs if SIGNIN as STAFF, profiles if SIGNIN as ORGANISER)
 *  each advert card is then filled with its relative content uploaded from the data-list
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactHolder> {

    private static ArrayList<Advert> contactsList;
    private LayoutInflater inflater;
    private Context contx;

    private boolean isOrganiser = AccessVerificationTools.isOrganiser();
    private ViewGroup parent;
    private RecyclerView.LayoutManager lm;
    private ContactsFragment af;
    /**
     * Initialises local infalter, list and retrives context
     * @param list
     * @param ctx
     */
    public ContactsAdapter(ArrayList<Advert> list, Context ctx, ContactsFragment af){
        this.inflater = LayoutInflater.from(ctx);
        this.contactsList = list;
        this.contx = ctx;
        this.af = af;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        notifyDataSetChanged();

    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;

        View view = inflater.inflate(R.layout.item_contact, parent, false);

        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactHolder holder, final int position) {
       final Advert ad = contactsList.get(position);

        if(Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.ORGANISER)){
            holder.tv_contact_title.setText(ad.getValue(MLBService.JSONKey.SPECIALITY));
            holder.tv_contact_name.setText(ad.getValue(MLBService.JSONKey.USERNAME));
        }else if(Tools.accountType != null && Tools.accountType.equals(MLBService.AccountType.BARSTAFF)){
            holder.tv_contact_title.setText(ad.getValue(MLBService.JSONKey.EVENT_TITLE));
            holder.tv_contact_name.setText(ad.getValue(MLBService.JSONKey.USERNAME));
        }
        holder.bt_readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(contx, JobDescriptionActivity.class);
                intent.putExtra(JobDescriptionActivity.IMAGE, ad.getValue(MLBService.JSONKey.IMAGE_PATH));
                intent.putExtra(JobDescriptionActivity.TITLE, ad.getValue(MLBService.JSONKey.EVENT_TITLE));
                intent.putExtra(JobDescriptionActivity.USERNAME, ad.getValue(MLBService.JSONKey.USERNAME));
                intent.putExtra(JobDescriptionActivity.DAYRATE, ad.getValue(MLBService.JSONKey.HOUR_RATE));
                intent.putExtra(JobDescriptionActivity.NIGHTRATE, ad.getValue(MLBService.JSONKey.NIGHT_RATE));
                intent.putExtra(JobDescriptionActivity.DATE, ad.getValue(MLBService.JSONKey.JOB_START));
                intent.putExtra(JobDescriptionActivity.TIME, ad.getValue(MLBService.JSONKey.START_TIME));
                intent.putExtra(JobDescriptionActivity.DURATION, ad.getValue(MLBService.JSONKey.DURATION));
                intent.putExtra(JobDescriptionActivity.SPECIALITY1, ad.getValue(MLBService.JSONKey.SPECIALITY));
//                    if (ad.getSpecialities()[1] != null) {
//                        intent.putExtra(JobDescriptionFragment.SPECIALITY2,ad.getSpecialities()[1]);
//                    }
//                    intent.putExtra(JobDescriptionFragment.SPECIALITY3, ad.getSpecialities()[2]);
//                    intent.putExtra(JobDescriptionFragment.SPECIALITY4, ad.getSpecialities()[3]);
                intent.putExtra(JobDescriptionActivity.TYPE, ad.getValue(MLBService.JSONKey.EVENT_TYPE));
                intent.putExtra(JobDescriptionActivity.DESCRIPTION, ad.getValue(MLBService.JSONKey.DESCRIPTION));
                intent.putExtra(JobDescriptionActivity.LOCATIION, ad.getValue(MLBService.JSONKey.LOCATION));

                contx.startActivity(intent);

            }
        });
    }
    /**
     * Replaces content of list with new given
     * @param adverts
     */
    public void setAdverts(ArrayList<Advert> adverts){
        this.contactsList = adverts;
    }


    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    /**
     * Replaces content of list with new given
     * @param adverts
     */
    public void setupList(ArrayList<Advert> adverts){
        this.contactsList = adverts;
        notifyDataSetChanged();
    }



    public void addItem(Advert advert){
        this.contactsList.add(advert);
        notifyItemInserted(contactsList.indexOf(advert));
    }



    public void removeItem(int index){
        this.contactsList.remove(index);
        notifyItemRemoved(index);
    }


    /**
     * RECYCLER VIEW
     * Initialises advert card components and sets onClickListener to the card
     */
    class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ic_contact_thumbnail;
        private TextView tv_contact_title;
        private TextView tv_contact_name;

        private Button bt_readMore;
        private Button bt_chat;
        private Button bt_hire;
        private Button bt_reject;

        /**
         * initialises all advert card components
         * @param itemView
         */
        public ContactHolder(View itemView) {
            super(itemView);

            ic_contact_thumbnail = (ImageView) itemView.findViewById(R.id.ic_contact_thumbnail);
            tv_contact_title = (TextView) itemView.findViewById(R.id.tv_contact_title);
            tv_contact_name = (TextView) itemView.findViewById(R.id.tv_contact_name);

            bt_readMore = (Button) itemView.findViewById(R.id.bt_contact_more);
            bt_chat = (Button) itemView.findViewById(R.id.bt_contact_chat);
//            bt_hire = (Button) itemView.findViewById(R.id.bt_contact_hire);
            bt_reject = (Button) itemView.findViewById(R.id.bt_contact_reject);

        }


        @Override
        public void onClick(View v) {

            Button b = (Button)v;

            Toast.makeText(contx, b.getText().toString()+" PRESSED", Toast.LENGTH_SHORT).show();

            System.out.print(b.getText().toString()+" PRESSED");

            switch (b.getText().toString()){
                case "READ MORE.." : System.out.print("READ MORE");
                    break;
                case "WITHDRAW" : System.out.print("WITHDRAW");
                    break;
            }


        }
    }
}
