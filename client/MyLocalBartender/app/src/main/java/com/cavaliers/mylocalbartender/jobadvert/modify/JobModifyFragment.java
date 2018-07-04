package com.cavaliers.mylocalbartender.jobadvert.modify;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.fragment.MLBFragment;
import com.cavaliers.mylocalbartender.home.search.model.AdvertData;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.server.SocketIO;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by SimoneJRSharpe on 09/03/2017.
 */


public class JobModifyFragment extends MLBFragment {

    private Context context;

    private RecyclerView jobList;
    private JobModifyAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Advert> data;


    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();

        View view = inflater.inflate(R.layout.fragment_modify_jobadvert, container, false);
        jobList = (RecyclerView) view.findViewById(R.id.rc_modify_jobs);
        linearLayoutManager = new LinearLayoutManager(context);
        jobList.setLayoutManager(linearLayoutManager);

        data = AdvertData.getOwnJobs();

        adapter = new JobModifyAdapter(data, this.getActivity(), this);

        jobList.setAdapter(adapter);

        return view;
    }



}