package com.app.gogreen.activities.buyer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.app.gogreen.activities.helpers.PaypalConfigClass;
import com.app.gogreen.R;
import com.app.gogreen.activities.helpers.BaseActivity;
import com.app.gogreen.databinding.ActivityPaypalPaymentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;

public class PaypalPaymentActivity extends BaseActivity {

    String userId="";

    ActivityPaypalPaymentBinding binding;
    private int PAYPAL_REQ_CODE = 12;
    private static PayPalConfiguration payPalConfiguration = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId(PaypalConfigClass.PAYPAL_CLIENT_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_paypal_payment);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        binding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePayment();
            }
        });

    }

    private void makePayment() {
        int q = Integer.parseInt(PlantDetailsActivity.quantity);
        float p = Float.parseFloat(PlantDetailsActivity.model.getPrice());

        double total = q*p;

        PayPalPayment payment = new PayPalPayment(new BigDecimal(total), "GBP", "Plants payment",PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent,PAYPAL_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQ_CODE){
            if(resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null){
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString();
                        startActivity(new Intent(getApplicationContext(), PaymentStatusActivity.class).putExtra("details",paymentDetails));
                        finish();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
            }else if(resultCode ==RESULT_CANCELED){
                Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
            }else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
                Toast.makeText(this, "Invalid Details!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(getApplicationContext(), PayPalService.class));
        super.onDestroy();
    }

}