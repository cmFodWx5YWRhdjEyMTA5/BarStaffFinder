package com.cavaliers.mylocalbartender.jobadvert;

/**
 * Created by SimoneJRSharpe on 13/03/2017.
 */

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.fragment.MLBFragment;
import com.cavaliers.mylocalbartender.jobadvert.create.JobCreateFragment;

import com.cavaliers.mylocalbartender.jobadvert.modify.JobModifyAdapter;
import com.cavaliers.mylocalbartender.jobadvert.modify.JobModifyFragment;
import com.cavaliers.mylocalbartender.menu.organiser_menu.OrganiserMenuActivity;

public class JobAdvertFragment extends MLBFragment {

    Toolbar toolbar;

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    JobModifyAdapter jobAdAdapter;
    int counter = 0;
    OrganiserMenuActivity orgMenu;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_toolbar, container, false);

        super.onCreate(savedInstanceState);

        toolbar = (Toolbar)view.findViewById(R.id.toolBar);

        tabLayout = (TabLayout)view.findViewById(R.id.tabLayout);
        viewPager = (ViewPager)view.findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());


        viewPagerAdapter.addFragments(new JobModifyFragment(), "Modify");
        viewPagerAdapter.addFragments(new JobCreateFragment(), "Create");


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;

    }


}
