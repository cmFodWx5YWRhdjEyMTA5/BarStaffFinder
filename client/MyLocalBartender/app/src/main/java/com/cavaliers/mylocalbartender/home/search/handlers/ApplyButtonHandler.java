package com.cavaliers.mylocalbartender.home.search.handlers;

/**
 * Created by JamesRich on 04/03/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.home.review.adapter.AnswerAdapter;
import com.cavaliers.mylocalbartender.home.search.model.AdvertData;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.server.SocketIO;
import com.cavaliers.mylocalbartender.tools.advert.Advert;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class ApplyButtonHandler implements View.OnClickListener {

    //private Advert advert;
    private Context context;
    private Advert advert;

    private EditText message;

    public ApplyButtonHandler(Advert advert, Context context, EditText message) {
        this.advert = advert;
        this.context = context;
        this.message = message;
    }

    public Context getContext() {
        return this.context;
    }

    public void onClick(View view) {
        try {
            JobApplyHandler jobApplyHandler = new JobApplyHandler();

            MLBService.sendRequest(context, MLBService.ACTION.APPLY_JOB, jobApplyHandler, jobApplyHandler,
                    MLBService.RequestBuilder.buildJSONRequest(
                            new Pair<>(MLBService.JSONKey.MESSAGE, (this.message.getText().toString()==null) ? "" :  this.message.getText().toString())
                    ),
                    MLBService.getLoggedInUserID(), advert.getValue(MLBService.JSONKey.JOB_ID));

        } catch (VolleyError volleyError) {
            volleyError.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class JobApplyHandler implements MLBService.MLBResponseListener, MLBService.MLBErrorListener {

        private void successJobApply(JSONObject response) {

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(MLBService.JSONKey.JOB_ID, advert.getValue(MLBService.JSONKey.JOB_ID));
                jsonObject.put(MLBService.JSONKey.ORG_ID, MLBService.JSONKey.ORG_ID);
                jsonObject.put(MLBService.JSONKey.STAFF_ID, MLBService.getLoggedInUserID());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MLBService.emit(SocketIO.Event.APPLY_TO_JOB, jsonObject);

            try {
                ApplicationHandler applicationHandler = new ApplicationHandler();

                Toast.makeText(context, response.getString(MLBService.JSONKey.MESSAGE), Toast.LENGTH_SHORT).show();
                try {

                    MLBService.sendRequest(context, MLBService.ACTION.GET_APPLICATIONS, applicationHandler, applicationHandler, null, MLBService.getLoggedInUserID());
                } catch (VolleyError volleyError) {
                    volleyError.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void failJobaApply(VolleyError error) {
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            failJobaApply(error);
        }

        @Override
        public void onResponse(JSONObject response) {
            successJobApply(response);
        }


        private class ApplicationHandler implements MLBService.MLBResponseListener, MLBService.MLBErrorListener {

            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(JSONObject response) {
                AdvertData.updateAdvert(response, AdvertData.TYPE.ANSWEARS);
            }
        }
    }
}
