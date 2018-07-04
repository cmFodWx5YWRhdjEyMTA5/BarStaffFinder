package com.cavaliers.mylocalbartender.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.tools.Tools;

/**
 * Created by Robert on 19/03/2017.
 */

public abstract class MLBActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Tools.setCurrentContextName(getClass().getName());
    }
}

