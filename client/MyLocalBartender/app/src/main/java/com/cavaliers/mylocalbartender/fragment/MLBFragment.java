package com.cavaliers.mylocalbartender.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cavaliers.mylocalbartender.tools.Tools;

public abstract class MLBFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Tools.setCurrentContextName(getClass().getName());
    }
}
