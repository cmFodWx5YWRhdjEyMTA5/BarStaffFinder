package com.cavaliers.mylocalbartender.jobadvert.privatejobs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.activity.MLBActivity;
import com.cavaliers.mylocalbartender.home.search.model.AdvertData;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

import java.util.ArrayList;


public class PrivateJobsActivity extends MLBActivity {


    private RecyclerView rc_recycler;
    private PrivateJobsAdapter privateJobsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Advert> data;
    private String staff_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_jobadvert);



        rc_recycler = (RecyclerView) findViewById(R.id.rc_privatejobs);

        linearLayoutManager = new LinearLayoutManager(this);
        rc_recycler.setLayoutManager(linearLayoutManager);

        data = AdvertData.getOwnJobs();
        staff_id = getIntent().getExtras().getString("STAFF_ID");

        privateJobsAdapter = new PrivateJobsAdapter(AdvertData.getOwnJobs(), this, staff_id);

        rc_recycler.setAdapter(privateJobsAdapter);


    }


//    public PrivateJobsAdapter getPrivateJobsAdapter(){
//        return  privateJobsAdapter;
//    }

}
