package com.gamingtournament.msa.payment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cashfree.pg.CFPaymentService;
import com.gamingtournament.msa.FreeFireActivity;
import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.TeamNameActivity;
import com.gamingtournament.msa.cashfree.API;
import com.gamingtournament.msa.cashfree.ResponseModel;
import com.gamingtournament.msa.model.UserInformation;
import com.gamingtournament.msa.utils.UtilValues;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cashfree.pg.CFPaymentService.PARAM_APP_ID;
import static com.cashfree.pg.CFPaymentService.PARAM_CARD_NUMBER;
import static com.cashfree.pg.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.cashfree.pg.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.cashfree.pg.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_CURRENCY;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_ID;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_NOTE;

public class AddMoneyActivity extends AppCompatActivity {

    private String CF_TEST_APP_ID = "983649edc68f64a558f5e4d1446389";
    private String CF_APP_ID = "146548494c0fd8d4c2a8eb4b29845641";

    private EditText et_amount;
    private Button btn_addMoney;
    private Dialog dialog;
    private TextView tv_50, tv_100, tv_200;
    private int userBal;
    private String userName, userPhno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        et_amount = findViewById(R.id.et_AddAmount);
        btn_addMoney = findViewById(R.id.btn_addMoney);
        tv_50 = findViewById(R.id.tv_50);
        tv_100 = findViewById(R.id.tv_100);
        tv_200 = findViewById(R.id.tv_200);

        dialog = new Dialog(AddMoneyActivity.this, R.style.AlertDialogTheme);
        dialog.setContentView(R.layout.pb_custom_layout);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow()!=null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        // get user Balance
        getUserData();

        // for title
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddMoneyActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });

        tv_50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_amount.setText("50");
            }
        });

        tv_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_amount.setText("100");
            }
        });

        tv_200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_amount.setText("200");
            }
        });

        btn_addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String addMoney = et_amount.getText().toString();
                if (addMoney.isEmpty()){
                    et_amount.setError("Enter amount");
                    et_amount.requestFocus();
                } else if (Integer.parseInt(addMoney) < 5){
                    et_amount.setError("Amount should greater than 5");
                    et_amount.requestFocus();
                } else {
                    dialog.show();
                    int amount = Integer.parseInt(addMoney);
                    initPayment(addMoney);
                }

            }
        });

    }


    public void initPayment(String orderAmount) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UtilValues.BASE_URL)
//                .client(getUnsafeClient().build())
                .addConverterFactory(GsonConverterFactory.create()).build();


        final API service = retrofit.create(API.class);

        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);

        retrofit2.Call<ResponseModel> call = service.getToken(orderAmount, timeStamp);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                if (response.isSuccessful()){
                    if (response.body().getMessage().equals("Token generated")){

                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                        String token = response.body().getCftoken();

                        Map<String, String> params = new HashMap<>();
                        params.put(PARAM_APP_ID, CF_APP_ID);
                        params.put(PARAM_ORDER_ID, timeStamp);
                        params.put(PARAM_ORDER_AMOUNT, orderAmount);
                        params.put(PARAM_ORDER_NOTE, "UID: "+uid);
                        params.put(PARAM_CUSTOMER_NAME, userName);
                        params.put(PARAM_CUSTOMER_PHONE, userPhno);
                        params.put(PARAM_CUSTOMER_EMAIL, userEmail);
                        params.put(PARAM_ORDER_CURRENCY, "INR");
                        if (dialog.isShowing()){
                            dialog.dismiss();
                        }
                        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
                        cfPaymentService.setOrientation(0);
                        cfPaymentService.doPayment(AddMoneyActivity.this,params,token,"PROD",false);

                    } else {
                        if (dialog.isShowing()){
                            dialog.dismiss();
                        }
                        Toast.makeText(AddMoneyActivity.this, "msg3 "+response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Toast.makeText(AddMoneyActivity.this, "Error Response", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
                Toast.makeText(AddMoneyActivity.this, "msg4 "+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CFPaymentService.REQ_CODE && data!=null){
            Bundle bundle = data.getExtras();
            if (bundle != null){

//                String st = transBundleTostring(bundle);

                // all response from cashfree
                String tnxStatus = bundle.getString("txStatus");
                String orderId = bundle.getString("orderId");
                String orderAmm = bundle.getString("orderAmount");
                String paymentMode = bundle.getString("paymentMode");
                String txTime = bundle.getString("txTime");
                String referenceId = bundle.getString("txTime");
                String txMsg = bundle.getString("txMsg");
                String signature = bundle.getString("signature");

//                tv_response.setText(st);

                assert tnxStatus != null;
                if (tnxStatus.equals("SUCCESS")){

                    assert orderAmm != null;
                    int amount = (int) Double.parseDouble(orderAmm);

                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    // user transaction history
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("000transaction").child(orderId);
                    db.child("status").setValue("credited");
                    db.child("id").setValue(orderId);
                    db.child("bal").setValue(amount);

                    // update user balance
                    DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                    rf.child("balance").setValue(userBal + amount);
                    startActivity(new Intent(AddMoneyActivity.this, MainActivity.class));
                    finish();

//                    addTransactionHistory(orderId, amount);

                    Toast.makeText(this, tnxStatus, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, tnxStatus, Toast.LENGTH_LONG).show();
                }

            }

        }

    }

    public String transBundleTostring (Bundle bundle){
        String response = "";
        for (String key:bundle.keySet()){
            response = response.concat(String.format("%s : %s\n",key,bundle.getString(key)));
        }
        return response;
    }

    private void addTransactionHistory(String timeStamp, int amount) {

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("000transaction").child(timeStamp);
        dr.child("bal").setValue(amount);
        dr.child("id").setValue(timeStamp);
        dr.child("status").setValue("credited");
        dr.child("txnid").setValue(timeStamp);


        // update user balance balance
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        db.child("balance").setValue(userBal + amount);
        Toast.makeText(AddMoneyActivity.this, "Balance Updated", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AddMoneyActivity.this, MainActivity.class));
        finish();

    }


    private void getUserData() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    UserInformation ui = new UserInformation();
                    ui.setBalance(snapshot.getValue(UserInformation.class).getBalance());
                    ui.setName(snapshot.getValue(UserInformation.class).getName());
                    ui.setPhno(snapshot.getValue(UserInformation.class).getPhno());
//                    ui.setTotalmatch(snapshot.getValue(UserInformation.class).getTotalmatch());

                    userBal = ui.getBalance();
                    userName = ui.getName();
                    userPhno = ui.getPhno();
//                    userTotalMatch = ui.getTotalmatch();

//                    tv_wallet_money.setText("₹ "+userBal);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void gotocashfree(View view) {

        try {
            Intent updateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cashfree.com"));
            startActivity(updateIntent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(AddMoneyActivity.this, "Error: Browser App Not Found", Toast.LENGTH_LONG).show();
        }


    }
}