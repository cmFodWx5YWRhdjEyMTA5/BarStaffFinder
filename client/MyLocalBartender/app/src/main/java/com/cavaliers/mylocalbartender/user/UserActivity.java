package com.cavaliers.mylocalbartender.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cavaliers.mylocalbartender.activity.MLBActivity;
import com.cavaliers.mylocalbartender.server.MLBService;
import com.cavaliers.mylocalbartender.server.SocketIO;
import com.cavaliers.mylocalbartender.stripe.StripeConfig;
import com.cavaliers.mylocalbartender.tools.Tools;
import com.cavaliers.mylocalbartender.tools.access.AccessVerificationTools;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import static com.cavaliers.mylocalbartender.server.SocketIO.Event.CARD_UPDATE;

/**
 * Created by Robert on 23/03/2017.
 */

public abstract class UserActivity extends MLBActivity implements  View.OnClickListener, MLBService.MLBResponseListener,MLBService.MLBErrorListener {

    public static final String PUBLISHABLE_KEY = StripeConfig.KEY;

    public Stripe stripe = null;
    private String token_string = "";

    protected EditText init_et_first_name;
    protected EditText init_et_last_name;
    protected EditText init_et_DOB;
    protected EditText init_et_postcode;
    protected Spinner init_spn_gender;

    //Payment
    protected EditText et_card_number;
    protected EditText et_expire_month;
    protected EditText et_expire_year;
    protected EditText et_cvc;

    protected Button init_btn_next;

    public EditText[] init_editTextArray;
    protected CheckBox[] init_checkBoxes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initWidget();

        setEditTextListener();
        setNextButtonListener();

        try {

            this.stripe  = new Stripe(this, PUBLISHABLE_KEY);
        } catch (AuthenticationException e) {

            Toast.makeText(this, "Invalid key!!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    protected abstract void completeFields();
    protected abstract void initWidget();
    protected abstract void startIntent();

    protected String getDOB(){
        return init_et_DOB.getText().toString();
    }

    protected void setNextButtonListener(){
        init_btn_next.setOnClickListener(this);
    }

    protected String getGender(){
        //((String) init_spn_gender.getSelectedItem()).charAt(0))
        if(init_spn_gender.getSelectedItem().toString().equals("Female")) return "F";
        else if(init_spn_gender.getSelectedItem().toString().equals("Male")) return "M";
        else return "Gender";
    }

    protected String optionalGetValue(EditText editText){
        String value = editText.getText().toString();
        return ((value == "") ? null : value);
    }

    protected String getValue(EditText editText){
        return editText.getText().toString();
    }

    protected void fillEditText(String DOB){
        init_et_DOB.setText(DOB);
        //init_et_DOB.setEnabled(false);
    }

    protected void setEditTextListener(){
        for(EditText entry : init_editTextArray){ //loops through all entries of the array
                AccessVerificationTools.setEditTextListener(entry); //calls method in AccessVerificationTools
        }
    }

    // Method to initialize checkboxes array
    protected CheckBox[] checkBoxesArray(CheckBox ... checkBoxes){
        return checkBoxes;
    }

    protected EditText[] editTextsValidation(EditText ... editTexts){
        return  editTexts;
    }

   /* protected void setETConstraints(){
        // Limiting text length in the EditTexts
        et_card_number.setFilters(new InputFilter[] { new InputFilter.LengthFilter(16) });
        et_expire_month.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
        et_expire_year.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
        et_cvc.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
    }*/

    public void completeRegistration(){
        Card cardToSave = new Card(
                et_card_number.getText().toString(),
                Integer.parseInt(et_expire_month.getText().toString()), Integer.parseInt(et_expire_year.getText().toString()),
                et_cvc.getText().toString()
        );
        if (!cardToSave.validateCard()) {
            Toast.makeText(this, "Invalid Card JobModifyData", Toast.LENGTH_LONG).show();
        }else {
            stripe.createToken(
                    cardToSave,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            token_string = token.getId();
                            if(token_string != null && token_string != ""){
                                MLBService.emit(CARD_UPDATE, UserActivity.this.sendToken());
                                System.out.println("EMIT CARD_UPDATE EVENT");
                            }else{
                                Toast.makeText(UserActivity.this, "Something went wrong with your card details", Toast.LENGTH_SHORT).show();
                            }
                        }
                        public void onError(Exception error) {
                            // Show localized error message
                            Toast.makeText(UserActivity.this,
                                    error.toString(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );
        }
    }


    private JSONObject sendToken(){
        try {
            JSONObject data = new JSONObject();
            data.put(MLBService.JSONKey.USER_ID, MLBService.getLoggedInUserID());
            data.put("type", Tools.accountType.name());
            data.put(MLBService.JSONKey.STRIPE_TOKEN, token_string);
            System.out.println("Adding LeBron");
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View view) {

        if (AccessVerificationTools.isEmpty(init_editTextArray, init_spn_gender) != 0) {
            Toast.makeText(this, "All fields must be filled in order to proceed", Toast.LENGTH_LONG).show();
            return;
        }
        completeFields();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
           /* AdvertData.updateAdverts(response);
            AnswerData.updateAnswers(response);
            RequestData.updateRequests(response);
            PrivateJobsData.updateAdverts(response);
            JobModifyData.updateAdverts(response);*/
        try{
            Toast.makeText(this, response.getString(MLBService.JSONKey.MESSAGE), Toast.LENGTH_SHORT).show();
            System.out.println("complete registration --------=====================------------");
            completeRegistration();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        MLBService.signOut();
        super.onBackPressed();

    }
}
