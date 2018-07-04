package com.cavaliers.mylocalbartender.home.review;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.fragment.MLBFragment;
import com.cavaliers.mylocalbartender.home.review.adapter.AnswerAdapter;
import com.cavaliers.mylocalbartender.home.review.adapter.AnswerJobAdapter;
import com.cavaliers.mylocalbartender.home.review.adapter.AnswerProfileAdapter;
import com.cavaliers.mylocalbartender.home.review.adapter.RequestJobAdapter;
import com.cavaliers.mylocalbartender.home.review.adapter.RequestProfileAdapter;
import com.cavaliers.mylocalbartender.home.search.SearchPageFragment;
import com.cavaliers.mylocalbartender.home.search.adapter.AdvertAdapter;
import com.cavaliers.mylocalbartender.home.search.model.AdvertData;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

import java.util.ArrayList;

/**
 * Represents page where either BarStaff can review job that he/she has applied for
 * or an Organiser can review the job invitations that he/she has sent
 * to verify if any response has been received
 */


public class AnswerFragment extends MLBFragment {

    private RecyclerView rc_recycler;
    private SearchPageFragment searchPageFragment;
    private AnswerJobAdapter answerJobAdapter;
    private AnswerProfileAdapter answerProfileAdapter;

    private AdvertAdapter advertAdapter;
    private LinearLayoutManager linearLayoutManager;
    private static boolean isFirstTime = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_review_answer,container,false);

        rc_recycler = (RecyclerView)view.findViewById(R.id.recyclerView);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        rc_recycler.setLayoutManager(linearLayoutManager);

        ArrayList<Advert> data = AdvertData.getAnswers();

        if (Tools.accountType.equals(MLBService.AccountType.ORGANISER)) {
            answerProfileAdapter = new AnswerProfileAdapter(data, getActivity());
            rc_recycler.setAdapter(answerProfileAdapter);
        } else if (Tools.accountType.equals(MLBService.AccountType.BARSTAFF)){
            answerJobAdapter = new AnswerJobAdapter(data, getActivity());
            rc_recycler.setAdapter(answerJobAdapter);
        }

        return view;
    }



    public static AnswerFragment newInstance() {
        AnswerFragment fragment = new AnswerFragment();
        return fragment;
    }




}
