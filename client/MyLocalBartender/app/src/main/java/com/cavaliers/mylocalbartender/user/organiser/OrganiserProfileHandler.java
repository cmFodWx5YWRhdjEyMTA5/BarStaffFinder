package com.cavaliers.mylocalbartender.user.organiser;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.stripe.StripeConfig;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SimoneJRSharpe on 20/03/2017.
 */

public class OrganiserProfileHandler implements View.OnClickListener{

    private OrganiserProfileFragment organiserProfileFragment;
    private Context context;
    private Stripe stripe;

    public OrganiserProfileHandler(OrganiserProfileFragment organiserProfileFragment){
        this.organiserProfileFragment = organiserProfileFragment;
        this.context = this.organiserProfileFragment.getContext();

        try {
            this.stripe  = new Stripe(this.context, StripeConfig.KEY);
        } catch (AuthenticationException e) {
            Toast.makeText(this.context, "Invalid key!!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        // this is why save crashes
        if(this.organiserProfileFragment.stripeValidator()){
            completeRegistration();
        }else{
            completeFields(null); // No stripe token to be passed
        }
    }

    private void updateDataModel(JSONObject response){

    }

    private void completeFields(String token){
        try{
            JSONObject obj = MLBService.RequestBuilder.buildJSONRequest(
                    new Pair<>(MLBService.JSONKey.FIRST_NAME, this.organiserProfileFragment.getFirstName()),
                    new Pair<>(MLBService.JSONKey.SURNAME, this.organiserProfileFragment.getLastName()),
                    new Pair<>(MLBService.JSONKey.DOB, this.organiserProfileFragment.getDOB()),
                    new Pair<>(MLBService.JSONKey.POSTCODE, this.organiserProfileFragment.getPostCode()),
                    new Pair<>(MLBService.JSONKey.GENDER, this.organiserProfileFragment.getGender()),
                    // Event Type
                    new Pair<>(MLBService.JSONKey.PROF_POS, this.organiserProfileFragment.getProfPos())
            );
            if(token != null){
                obj.put(MLBService.JSONKey.STRIPE_TOKEN, token);
            }
            try {

                MLBService.sendRequest(context, MLBService.ACTION.UPDATE_ORGANISER_PROFILE, new MLBService.MLBResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(context, response.getString(MLBService.JSONKey.MESSAGE), Toast.LENGTH_LONG).show();
                            OrganiserProfileHandler.this.organiserProfileFragment.fillProfilePage();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new MLBService.MLBErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, obj, MLBService.getLoggedInUserID());
            } catch (VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private  void completeRegistration(){

        if (this.organiserProfileFragment.stripeValidator()) {
            Toast.makeText(this.context, "Card details must be provided", Toast.LENGTH_LONG).show();
        }else{
            Card cardToSave = new Card(
                    this.organiserProfileFragment.getCardNumber(),
                    Integer.parseInt(this.organiserProfileFragment.getExpMonth()), Integer.parseInt(this.organiserProfileFragment.getExpYear()),
                    this.organiserProfileFragment.getCVC()
            );
            if (!cardToSave.validateCard()) {
                Toast.makeText(this.context, "Invalid Card JobModifyData", Toast.LENGTH_LONG).show();
            }else {
                stripe.createToken(
                        cardToSave,
                        new TokenCallback() {
                            public void onSuccess(Token token) {
                                String token_string = token.getId();
                                Toast.makeText(context, "works: " + token_string, Toast.LENGTH_LONG).show();
                                completeFields(token_string);
                            }

                            public void onError(Exception error) {
                                // Show localized error message
                                Toast.makeText(context,
                                        error.toString(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
            }
        }
    }
}
