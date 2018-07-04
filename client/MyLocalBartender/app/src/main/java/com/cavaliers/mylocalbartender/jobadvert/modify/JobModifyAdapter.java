package com.cavaliers.mylocalbartender.jobadvert.modify;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.jobadvert.update.JobUpdateActivity;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

import org.json.JSONObject;

import java.util.ArrayList;


public class JobModifyAdapter extends RecyclerView.Adapter<JobModifyAdapter.JobModifyHolder> {

    private ArrayList<Advert> jobadvertList;
    private LayoutInflater inflater;
    private Context contx;
    private ViewGroup parent;
    private Advert ad;
    private JobModifyFragment jobModifyFragment;

    /**
     *
     * @param list
     * @param ctx
     * @param jobModifyFragment
     */
    public JobModifyAdapter(ArrayList<Advert> list, Context ctx, JobModifyFragment jobModifyFragment){
        this.inflater = LayoutInflater.from(ctx);
        this.jobadvertList = list;
        System.out.println("______________ADVERT MODIFY SIZE IS="+jobadvertList.size());
        this.contx = ctx;
        this.jobModifyFragment = jobModifyFragment;
    }

    /**
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public JobModifyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;

        View view = inflater.inflate(R.layout.item_jobadvert_modify, parent, false);

        return new JobModifyHolder(view);
    }

    /**
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final JobModifyHolder holder, final int position) {
        final Advert ad = jobadvertList.get(position);

        System.out.println("______________ADVER TTITLE IS="+(ad.getValue(MLBService.JSONKey.EVENT_TITLE)));

        holder.tv_jobTitle.setText(ad.getValue(MLBService.JSONKey.EVENT_TITLE));
        holder.tv_jobDate.setText(ad.getValue(MLBService.JSONKey.JOB_START));
        holder.tv_jobType.setText(ad.getValue(MLBService.JSONKey.EVENT_TYPE));


        holder.btn_jobad_delete.setOnClickListener(new View.OnClickListener(){

            // use this for the remove button
            @Override
            public void onClick(View v){
                try {
                    MLBService.sendRequest(
                            contx,
                            MLBService.ACTION.DELETE_ORGANISER_PUBLIC_JOB,
                            new MLBService.MLBResponseListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    removeItem(jobadvertList.indexOf(ad));
                                    System.out.println("good"+ad.getValue(MLBService.JSONKey.JOB_ID));
                                }
                            },
                            new MLBService.MLBErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(JobModifyAdapter.this.contx, error.getMessage(), Toast.LENGTH_LONG).show();
                                    error.printStackTrace();
                                    System.out.println("error"+ad.getValue(MLBService.JSONKey.JOB_ID));
                                }
                            }, new JSONObject(), MLBService.getLoggedInUserID(), ad.getValue(MLBService.JSONKey.JOB_ID));
                } catch (VolleyError volleyError) {
                    volleyError.printStackTrace();
                }

            }
        });


        // Opens the update page
        holder.btn_jobad_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent intent = new Intent(contx,JobUpdateActivity.class);
                JobUpdateActivity.setAdvert(ad);
               contx.startActivity(intent);
            }
        });
    }

    /**
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return jobadvertList.size();
    }


    /**
     * Replaces content of list with new given
     * @param adverts
     */
    public void setupList(ArrayList<Advert> adverts){
        this.jobadvertList = adverts;
        notifyDataSetChanged();
    }


    public void addItem(Advert advert){
        this.jobadvertList.add(advert);
        notifyItemInserted(jobadvertList.indexOf(advert));
    }


    /**
     *
     * @param index
     */
    public void removeItem(int index){
        jobadvertList.remove(index);
        notifyItemRemoved(index);
    }



    /**
     * RECYCLER VIEW
     * Initialises advert card components and sets onClickListener to the card
     */

    class JobModifyHolder extends RecyclerView.ViewHolder {


        TextView tv_jobTitle;
        TextView tv_jobDate;
        TextView tv_jobType;
        Button btn_jobad_delete;
        Button btn_jobad_update;


        /**
         * initialises all advert card components
         * @param itemView
         */
        public JobModifyHolder(View itemView) {
            super(itemView);

            tv_jobTitle = (TextView) itemView.findViewById(R.id.tv_job_title);
            tv_jobDate = (TextView) itemView.findViewById(R.id.tv_full_job_date);
            tv_jobType = (TextView) itemView.findViewById(R.id.tv_job_type);
            btn_jobad_delete = (Button) itemView.findViewById(R.id.btn_jobad_delete);
            btn_jobad_update = (Button) itemView.findViewById(R.id.btn_jobad_update);


        }
    }
}
