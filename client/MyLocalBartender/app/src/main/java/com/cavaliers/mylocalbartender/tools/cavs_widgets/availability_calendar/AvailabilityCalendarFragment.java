package com.cavaliers.mylocalbartender.tools.cavs_widgets.availability_calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cavaliers.mylocalbartender.R;


public class AvailabilityCalendarFragment extends Fragment{


    private RecyclerView rc_availability_recycler;
    private AvailabilityCalendarAdapter availabilityCalendarAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.availability_row,container,false);


        rc_availability_recycler = (RecyclerView)view.findViewById(R.id.rc_availability_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rc_availability_recycler.setLayoutManager(linearLayoutManager);
        System.out.println("_____ABOUT TO RUN THE CALENDAR ADAPT");

        availabilityCalendarAdapter = new AvailabilityCalendarAdapter(AvailabilityCalendarData.getAvailabilityList(), this.getActivity());

        System.out.println("_____I JUST RUN THE CALENDA ADAPT");

        rc_availability_recycler.setAdapter(availabilityCalendarAdapter);



        return view;
    }

}
