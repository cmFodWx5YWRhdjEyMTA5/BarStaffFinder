package com.cavaliers.mylocalbartender.home.search.handlers;

import android.view.View;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.server.MLBService;

import org.json.JSONObject;

/**
 * Created by Robert on 14/03/2017.
 */

public class WithdrawButtonHandler implements View.OnClickListener, MLBService.MLBResponseListener, MLBService.MLBErrorListener {
    @Override
    public void onClick(View view) {
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {

    }
}
