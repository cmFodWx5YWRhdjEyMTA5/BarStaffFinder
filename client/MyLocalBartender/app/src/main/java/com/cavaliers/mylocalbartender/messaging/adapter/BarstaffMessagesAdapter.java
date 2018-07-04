package com.cavaliers.mylocalbartender.messaging.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.health.SystemHealthManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.home.review.JobDescriptionActivity;
import com.cavaliers.mylocalbartender.server.ChatServer;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BarstaffMessagesAdapter extends RecyclerView.Adapter<BarstaffMessagesAdapter.ViewHolder>{

    private  Context context;
    private ArrayList<Advert> data;

    public BarstaffMessagesAdapter(Context context, ArrayList<Advert> data) {
        this.context = context;
        this.data = data;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView chatTitle;
        public TextView chatContactName;
        private Button readMoreButton;
        private Button chatButton;
        private Button rejectButton;
        public LinearLayout fragmentLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            chatTitle = (TextView) itemView.findViewById(R.id.tv_contact_title);
            chatContactName = (TextView) itemView.findViewById(R.id.tv_contact_name);
            chatButton = (Button) itemView.findViewById(R.id.bt_contact_chat);
            readMoreButton = (Button) itemView.findViewById(R.id.bt_contact_more);
            rejectButton = (Button) itemView.findViewById(R.id.bt_contact_reject);
            fragmentLayout = (LinearLayout) itemView.findViewById(R.id.fragment_chat_messages_layout);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Advert advert = data.get(position);


        holder.chatTitle.setText(advert.getValue(MLBService.JSONKey.EVENT_TITLE));
        holder.chatContactName.setText(advert.getValue(MLBService.JSONKey.USERNAME));

        holder.chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String org_id = data.get(position).getValue(MLBService.JSONKey.ORG_ID);
                String job_id = data.get(position).getValue(MLBService.JSONKey.JOB_ID);
                Intent intent = new Intent(context, ChatServer.class);
                intent.putExtra(MLBService.JSONKey.JOB_ID, job_id);
                intent.putExtra(MLBService.JSONKey.USER_ID, org_id);
                context.startActivity(intent);
            }
        });

        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(MLBService.JSONKey.ORG_ID, data.get(position).getValue(MLBService.JSONKey.ORG_ID));
                    jsonObject.put(MLBService.JSONKey.JOB_ID, data.get(position).getValue(MLBService.JSONKey.JOB_ID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    MLBService.sendRequest(
                            context,
                            MLBService.ACTION.REJECT_OFFER,
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
                            }, new JSONObject(), MLBService.getLoggedInUserID(), data.get(position).getValue(MLBService.JSONKey.JOB_ID));

                } catch (VolleyError volleyError) {

                }
            }
        });

        holder.readMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, JobDescriptionActivity.class);
                intent.putExtra(JobDescriptionActivity.IMAGE, advert.getValue(MLBService.JSONKey.IMAGE_PATH));
                intent.putExtra(JobDescriptionActivity.TITLE, advert.getValue(MLBService.JSONKey.EVENT_TITLE));
                intent.putExtra(JobDescriptionActivity.USERNAME, advert.getValue(MLBService.JSONKey.USERNAME));
                intent.putExtra(JobDescriptionActivity.DAYRATE, advert.getValue(MLBService.JSONKey.HOUR_RATE));
                intent.putExtra(JobDescriptionActivity.NIGHTRATE, advert.getValue(MLBService.JSONKey.NIGHT_RATE));
                intent.putExtra(JobDescriptionActivity.DATE, advert.getValue(MLBService.JSONKey.JOB_START));
                intent.putExtra(JobDescriptionActivity.TIME, advert.getValue(MLBService.JSONKey.START_TIME));
                intent.putExtra(JobDescriptionActivity.DURATION, advert.getValue(MLBService.JSONKey.DURATION));
                intent.putExtra(JobDescriptionActivity.SPECIALITY1, advert.getValue(MLBService.JSONKey.SPECIALITY));
//                    if (advert.getSpecialities()[1] != null) {
//                        intent.putExtra(JobDescriptionFragment.SPECIALITY2,advert.getSpecialities()[1]);
//                    }
//                    intent.putExtra(JobDescriptionFragment.SPECIALITY3, advert.getSpecialities()[2]);
//                    intent.putExtra(JobDescriptionFragment.SPECIALITY4, advert.getSpecialities()[3]);
                intent.putExtra(JobDescriptionActivity.TYPE, advert.getValue(MLBService.JSONKey.EVENT_TYPE));
                intent.putExtra(JobDescriptionActivity.DESCRIPTION, advert.getValue(MLBService.JSONKey.DESCRIPTION));
                intent.putExtra(JobDescriptionActivity.LOCATIION, advert.getValue(MLBService.JSONKey.LOCATION));

                context.startActivity(intent);

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
