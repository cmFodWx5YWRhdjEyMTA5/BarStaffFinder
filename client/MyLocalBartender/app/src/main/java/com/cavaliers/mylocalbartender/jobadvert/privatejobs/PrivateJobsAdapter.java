package com.cavaliers.mylocalbartender.jobadvert.privatejobs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.server.SocketIO;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *  JobAdvert Adapter
 *  Core component to allow cyclerview to be functional
 *  Generates a list of adverts (jobs if SIGNIN as STAFF, profiles if SIGNIN as ORGANISER)
 *  each advert card is then filled with its relative content uploaded from the data-list
 */

public class PrivateJobsAdapter extends RecyclerView.Adapter<PrivateJobsAdapter.PrivateJobsHolder> {

    private ArrayList<Advert> data;
    private Context context;
    private String staff_id;

    /**
     * Initialises local infalter, list and retrives context
     *
     * @param data
     * @param context
     * @param staff_id
     */
    public PrivateJobsAdapter(ArrayList<Advert> data, Context context, String staff_id) {
        this.data = data;
        this.context = context;
        this.staff_id = staff_id;
    }

    /**
     * RECYCLER VIEW
     * Initialises advert card components and sets onClickListener to the card
     */
    class PrivateJobsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_job_title;
        private Button btn_jobad_send;

        public PrivateJobsHolder(View itemView) {
            super(itemView);

            tv_job_title = (TextView) itemView.findViewById(R.id.tv_job_title);
            btn_jobad_send = (Button) itemView.findViewById(R.id.btn_jobad_send);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public PrivateJobsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_jobadvert_private, parent, false);
        PrivateJobsHolder privateJobsHolder = new PrivateJobsHolder(view);
        return privateJobsHolder;
    }

    @Override
    public void onBindViewHolder(final PrivateJobsHolder holder, final int position) {
        final Advert advert = data.get(position);

        holder.tv_job_title.setText(advert.getValue(MLBService.JSONKey.EVENT_TITLE));

        holder.btn_jobad_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                final JSONObject emitJSON = new JSONObject();
                try {
                    jsonObject.put(MLBService.JSONKey.STAFF_ID, staff_id);
                    jsonObject.put(MLBService.JSONKey.JOB_ID, advert.getValue(MLBService.JSONKey.JOB_ID));
                    emitJSON.put(MLBService.JSONKey.JOB_ID, advert.getValue(MLBService.JSONKey.JOB_ID));
                    emitJSON.put(MLBService.JSONKey.ORG_ID, MLBService.getLoggedInUserID());
                    emitJSON.put(MLBService.JSONKey.STAFF_ID, staff_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    MLBService.sendRequest(
                            context,
                            MLBService.ACTION.SEND_PRIVATE_JOB,
                            new MLBService.MLBResponseListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    MLBService.emit(SocketIO.Event.SEND_INVITE, emitJSON);
                                }
                            },
                            new MLBService.MLBErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }, jsonObject, MLBService.getLoggedInUserID()
                    );
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



}