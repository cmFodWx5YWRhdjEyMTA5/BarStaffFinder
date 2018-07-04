package com.cavaliers.mylocalbartender.messaging;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.fragment.MLBFragment;
import com.cavaliers.mylocalbartender.home.search.model.AdvertData;
import com.cavaliers.mylocalbartender.messaging.adapter.ContactsAdapter;

/**
 * Represents page where either BarStaff can review job that he/she has applied for
 * or an Organiser can review the job invitations that he/she has sent
 * to verify if any response has been received
 */


public class ContactsFragment extends MLBFragment {

    private RecyclerView rc_recycler;
    private ContactsAdapter contactsAdapter;
    private LinearLayoutManager linearLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_review_answer,container,false);

        rc_recycler = (RecyclerView)view.findViewById(R.id.recyclerView);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        rc_recycler.setLayoutManager(linearLayoutManager);

        contactsAdapter = new ContactsAdapter(AdvertData.getAnswers(), this.getActivity(), this);

        rc_recycler.setAdapter(contactsAdapter);

        return view;
    }

    public ContactsAdapter getContactsAdapter(){
        return contactsAdapter;
    }


    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        return fragment;
    }


    public static String getClassName(){
        return ""+ContactsFragment.class;
    }




}
