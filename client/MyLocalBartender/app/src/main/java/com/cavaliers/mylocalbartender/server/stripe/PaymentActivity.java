package com.cavaliers.mylocalbartender.server.stripe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.activity.MLBActivity;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

public class PaymentActivity extends MLBActivity {

    private static final String TAG = "MLBPayment";

    // You will need to use your live API key even while testing
    public static final String PUBLISHABLE_KEY = "pk_test_b0njLC4p2hI2sHnUUH7IRyr9";

    // Unique identifiers for asynchronous requests:
    private static final int LOAD_MASKED_WALLET_REQUEST_CODE = 1000;
    private static final int LOAD_FULL_WALLET_REQUEST_CODE = 1001;

    public Stripe stripe = null;
    private String token_string = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            this.stripe  = new Stripe(this, PUBLISHABLE_KEY);
            showAndroidPay();

        } catch (AuthenticationException e) {

            Toast.makeText(this, "Invalid key!!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void showAndroidPay() {
        setContentView(R.layout.stripe_payment_fragment);

        final EditText et_card_input = (EditText) findViewById(R.id.card_input);
        final EditText et_month_input = (EditText) findViewById(R.id.month_input);
        final EditText et_cvc_input = (EditText) findViewById(R.id.cvc_input);

        final Button set_card = (Button) findViewById(R.id.set_card);

        set_card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String[] date = et_month_input.getText().toString().split("/");
                Card cardToSave = new Card(
                        et_card_input.getText().toString(),
                        Integer.parseInt(date[0]), Integer.parseInt(date[1]),
                        et_cvc_input.getText().toString()
                );

                if (!cardToSave.validateCard()) {

                    Toast.makeText(view.getContext(), "Invalid Card JobModifyData", Toast.LENGTH_LONG).show();

                }else{

                    stripe.createToken(
                            cardToSave,
                            new TokenCallback() {
                                public void onSuccess(Token token) {

                                    token_string = token.getId();
                                    Toast.makeText(PaymentActivity.this, "works: "+ token_string, Toast.LENGTH_LONG).show();
                                }
                                public void onError(Exception error) {
                                    // Show localized error message
                                    Toast.makeText(PaymentActivity.this,
                                            error.toString(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                    );


                }
            }
        });
    }
}
