package com.cavaliers.mylocalbartender.messaging;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.fragment.MLBFragment;
import com.cavaliers.mylocalbartender.home.search.model.AdvertData;
import com.cavaliers.mylocalbartender.messaging.adapter.BarstaffMessagesAdapter;
import com.cavaliers.mylocalbartender.messaging.adapter.OrganiserMessagesAdapter;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessagesFragment extends MLBFragment{
    //set up all the things needed
    private Context context;

    private RecyclerView chatMessageList;
    private RecyclerView.Adapter chatMessageListAdapter;
    private RecyclerView.LayoutManager fragmentLayoutManager;

    private ArrayList<Advert> data;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Initialise items
        context = getActivity();
        //Setup view
        View view = inflater.inflate(R.layout.fragment_chat_messages, container, false);

        chatMessageList = (RecyclerView) view.findViewById(R.id.rc_messages_list);
        fragmentLayoutManager = new LinearLayoutManager(context);

        chatMessageList.setLayoutManager(fragmentLayoutManager);

        //Real one will need to be Advert
        data = AdvertData.getChatJobs();



        if(Tools.accountType.equals(MLBService.AccountType.ORGANISER)){
            chatMessageListAdapter = new OrganiserMessagesAdapter(context, data);
        } else if (Tools.accountType.equals(MLBService.AccountType.BARSTAFF)) {
            chatMessageListAdapter = new BarstaffMessagesAdapter(context, data);
        }

        chatMessageList.setAdapter(chatMessageListAdapter);

        return view;
    }
}
