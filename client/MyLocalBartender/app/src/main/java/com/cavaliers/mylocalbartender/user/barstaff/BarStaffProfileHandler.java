package com.cavaliers.mylocalbartender.user.barstaff;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.stripe.StripeConfig;
import com.cavaliers.mylocalbartender.user.organiser.OrganiserProfileFragment;
import com.cavaliers.mylocalbartender.user.organiser.OrganiserProfileHandler;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Robert on 23/03/2017.
 */

/**
 * Called when the Save button is clicked.
 *
 * Checks if any fields have been left empty. If yes, will not allow information to be saved, if no will allow information to be saved.
 *
 */

public class BarStaffProfileHandler implements View.OnClickListener {

    private BarStaffProfileFragment barStaffProfileFragment;
    private Context context;
    private Stripe stripe;

    public  BarStaffProfileHandler(BarStaffProfileFragment organiserProfileFragment){
        this.barStaffProfileFragment = organiserProfileFragment;
        this.context = this.barStaffProfileFragment.getContext();
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
        if(this.barStaffProfileFragment.stripeValidator()){
            completeRegistration();
        }else{
            completeFields(null); // No stripe token to be passed
        }
    }

    private void updateDataModel(JSONObject response){

    }

    private void completeFields(String token){
        try{
            Bitmap bitmap = BarStaffProfileHandler.this.barStaffProfileFragment.getImage();
            try {
                JSONObject obj = MLBService.RequestBuilder.buildJSONRequest(
                        new Pair<>(MLBService.JSONKey.USER_ID, MLBService.getLoggedInUserID()),
                        new Pair<>(MLBService.JSONKey.FIRST_NAME, BarStaffProfileHandler.this.barStaffProfileFragment.getFirstName()),
                        new Pair<>(MLBService.JSONKey.USERNAME,BarStaffProfileHandler.this.barStaffProfileFragment.getLastName() ),
                        new Pair<>(MLBService.JSONKey.GENDER, BarStaffProfileHandler.this.barStaffProfileFragment.getGender()),
                        new Pair<>(MLBService.JSONKey.POSTCODE, BarStaffProfileHandler.this.barStaffProfileFragment.getPostCode()),
                        new Pair<>(MLBService.JSONKey.PROXIMITY, BarStaffProfileHandler.this.barStaffProfileFragment.getProximity()),
                        new Pair<>(MLBService.JSONKey.NIGHT_RATE, BarStaffProfileHandler.this.barStaffProfileFragment.getNightlyRate()),
                        new Pair<>(MLBService.JSONKey.HOUR_RATE, BarStaffProfileHandler.this.barStaffProfileFragment.getHourlyRate()),
                        new Pair<>(MLBService.JSONKey.IMAGE_PATH, MLBService.ImageUploader.encode(bitmap)),
                        new Pair<>(MLBService.JSONKey.EXPERIENCE, BarStaffProfileHandler.this.barStaffProfileFragment.getExperience()),
                        new Pair<>(MLBService.JSONKey.SPECIALITY, BarStaffProfileHandler.this.barStaffProfileFragment.getSpeciality()),
                        new Pair<>(MLBService.JSONKey.DESCRIPTION, BarStaffProfileHandler.this.barStaffProfileFragment.getShortSummary())
                );
            if(token != null){
                obj.put(MLBService.JSONKey.STRIPE_TOKEN, token);
            }
                MLBService.sendRequest(context, MLBService.ACTION.UPDATE_BARTENDER_PROFILE, new MLBService.MLBResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(context, response.getString(MLBService.JSONKey.MESSAGE), Toast.LENGTH_LONG).show();
                            BarStaffProfileHandler.this.barStaffProfileFragment.fillProfilePage();
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

        if (this.barStaffProfileFragment.stripeValidator()) {
            Toast.makeText(this.context, "Card details must be provided", Toast.LENGTH_LONG).show();
        }else{
            Card cardToSave = new Card(
                    this.barStaffProfileFragment.getCardNumber(),
                    Integer.parseInt(this.barStaffProfileFragment.getExpMonth()), Integer.parseInt(this.barStaffProfileFragment.getExpYear()),
                    this.barStaffProfileFragment.getCVC()
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
