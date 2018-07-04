package com.cavaliers.mylocalbartender.home.review;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.fragment.MLBFragment;
import com.cavaliers.mylocalbartender.home.review.adapter.RequestAdapter;
import com.cavaliers.mylocalbartender.home.review.adapter.RequestJobAdapter;
import com.cavaliers.mylocalbartender.home.review.adapter.RequestProfileAdapter;
import com.cavaliers.mylocalbartender.home.search.model.AdvertData;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents page where either BarStaff can review job invitations that he/she has received
 * or an Organiser can review the job applications that he/she has received
 * to give a response
 */

public class RequestFragment extends MLBFragment {

    private RecyclerView rc_recycler;
    private RequestProfileAdapter requestProfileAdapter;
    private RequestJobAdapter requestJobAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_request, container, false);

        rc_recycler = (RecyclerView) view.findViewById(R.id.recyclerView);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        rc_recycler.setLayoutManager(linearLayoutManager);

        ArrayList<Advert> data = AdvertData.getRequest();

        if (Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {
            requestProfileAdapter = new RequestProfileAdapter(data, getActivity());
            rc_recycler.setAdapter(requestProfileAdapter);
        } else if (Tools.accountType.equals(MLBService.AccountType.BARSTAFF)){
            requestJobAdapter = new RequestJobAdapter(data, getActivity());
            rc_recycler.setAdapter(requestJobAdapter);
        }

        return view;
    }


}
