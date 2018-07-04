package com.cavaliers.mylocalbartender.home.review;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.fragment.MLBFragment;
import com.cavaliers.mylocalbartender.home.review.AnswerFragment;
import com.cavaliers.mylocalbartender.home.review.RequestFragment;
import com.cavaliers.mylocalbartender.home.search.SearchPageFragment;
import com.cavaliers.mylocalbartender.jobadvert.ViewPagerAdapter;
import com.cavaliers.mylocalbartender.jobadvert.create.JobCreateFragment;

import com.cavaliers.mylocalbartender.jobadvert.modify.JobModifyAdapter;
import com.cavaliers.mylocalbartender.jobadvert.modify.JobModifyFragment;
import com.cavaliers.mylocalbartender.menu.organiser_menu.OrganiserMenuActivity;

public class ReviewPager extends MLBFragment {

    Toolbar toolbar;

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    int counter = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_toolbar, container, false);

        super.onCreate(savedInstanceState);

        toolbar = (Toolbar)view.findViewById(R.id.toolBar);

        tabLayout = (TabLayout)view.findViewById(R.id.tabLayout);
        viewPager = (ViewPager)view.findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());


        viewPagerAdapter.addFragments(new AnswerFragment(), "Answer");
        viewPagerAdapter.addFragments(new RequestFragment(), "Request");


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;

    }

    public static ReviewPager newInstance() {
        ReviewPager fragment = new ReviewPager();

        return fragment;
    }


}
