package com.gamingtournament.msa.payment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.cashfree.API;
import com.gamingtournament.msa.interfaces.APITimeIST;
import com.gamingtournament.msa.model.BankModel;
import com.gamingtournament.msa.model.TimeISTModel;
import com.gamingtournament.msa.model.UserInformation;
import com.gamingtournament.msa.utils.UtilValues;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import papaya.in.sendmail.SendMail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WithdrawMoneyActivity extends AppCompatActivity {

    private EditText et_amount;
    private Button btn_WithdrawMoney;
    private TextView tv_bankName, tv_withBal;
    private int userBal;
    private int withBal;

    private String accNum;
    private String accName;
    private String accIfsc;

    private LinearLayout ll_bankDetails;
    private TextView tv_edit, tv_accNum, tv_ifsc, tv_accName, tv_addBankDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_money);

        et_amount = findViewById(R.id.et_withdrawAmount);
        btn_WithdrawMoney = findViewById(R.id.btn_withdrawMoney);
        //tv_bankName = findViewById(R.id.tv_bankName);
        tv_withBal = findViewById(R.id.tv_withBal);


        ll_bankDetails = findViewById(R.id.ll_bankDetails);
        tv_edit = findViewById(R.id.tv_edit);
        tv_accNum = findViewById(R.id.tv_accNum);
        tv_ifsc = findViewById(R.id.tv_ifsc);
        tv_accName = findViewById(R.id.tv_accName);
        tv_addBankDetails = findViewById(R.id.tv_addBankDetails);


        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WithdrawMoneyActivity.this, BankDetailsActivity.class));
            }
        });

        tv_addBankDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WithdrawMoneyActivity.this, BankDetailsActivity.class));
            }
        });

        // get user Balance
        getUserData();

        // for title
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WithdrawMoneyActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });

        // load bank info
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    BankModel bm = new BankModel();
                    bm.setAccname(snapshot.getValue(BankModel.class).getAccname());
                    bm.setAccnum(snapshot.getValue(BankModel.class).getAccnum());
                    bm.setIfsc(snapshot.getValue(BankModel.class).getIfsc());

                    accNum = bm.getAccnum();
                    accName = bm.getAccname();
                    accIfsc = bm.getIfsc();

                    if (accNum != null) {
                        tv_accNum.setText(accNum);
                        tv_addBankDetails.setVisibility(View.GONE);
                        ll_bankDetails.setVisibility(View.VISIBLE);
                    } else {
                        tv_addBankDetails.setVisibility(View.VISIBLE);
                        ll_bankDetails.setVisibility(View.GONE);
                        tv_addBankDetails.setText("Add Bank Details");
                    }

                    if (accName != null) {
                        tv_accName.setText(accName);
                    }

                    if (accIfsc != null) {
                        tv_ifsc.setText(accIfsc);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WithdrawMoneyActivity.this, "Error1: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        btn_WithdrawMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String withdrawMoney = et_amount.getText().toString();

                if (withdrawMoney.isEmpty()) {
                    et_amount.setError("Mandatory");
                    et_amount.requestFocus();
                } else if (Integer.parseInt(withdrawMoney) < 11) {
                    et_amount.setError("Amount should greater than 11");
                    et_amount.requestFocus();
                }else if (Integer.parseInt(withdrawMoney) > withBal) {
                    et_amount.setError("Insufficient Withdrawable Amount");
                    et_amount.requestFocus();
                } else {
                    if (accNum != null) {

                        int amount = Integer.parseInt(withdrawMoney);
                        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);

                        // for admin transaction history
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("000payment").child(timeStamp);
                        ref.child("status").setValue("pending");
                        ref.child("id").setValue(timeStamp);
                        ref.child("bal").setValue(amount);
                        ref.child("txnid").setValue(timeStamp);
                        ref.child("uid").setValue(uid);
                        ref.child("accnum").setValue(accNum);
                        ref.child("ifsc").setValue(accIfsc);
                        ref.child("accname").setValue(accName);

                        // user transaction history
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("000transaction").child(timeStamp);
                        db.child("status").setValue("pending");
                        db.child("id").setValue(timeStamp);
                        db.child("bal").setValue(amount);

                        // update user balance
                        DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                        rf.child("balance").setValue(userBal - amount);

                        String withDetails = "\nId    \t: \t\t"+ timeStamp+ "\n"+
                                "\nAmount    \t: \t\t"+ amount+ "\n"+
                                "\nUID    \t: \t\t"+ uid+ "\n"+
                                "\nAcc Num    \t: \t\t"+ accNum+ "\n"+
                                "\nIFSC    \t: \t\t"+ accIfsc+ "\n"+
                                "\nAcc Holder Name  \t: \t\t"+ accName+ "\n";

                        String emailSub = "A withdraw request is placed | Id: "+timeStamp;

                        String emailMsg = "Withdraw request is placed with the following details: \n\n" + withDetails;

                        SendMail mail = new SendMail(UtilValues.SENDER_EMAIL, UtilValues.SENDER_PASS, "shahnawazraj4@gmail.com", emailSub , emailMsg);
                        mail.execute();

                        Toast.makeText(WithdrawMoneyActivity.this, "Balance Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(WithdrawMoneyActivity.this, MainActivity.class));
                        finish();

                    } else {
                        Toast.makeText(WithdrawMoneyActivity.this, "Bank Details Not Added", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

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
//                    ui.setTotalmatch(snapshot.getValue(UserInformation.class).getTotalmatch());

                    userBal = ui.getBalance();
                    if (userBal> 11){
                        withBal = userBal - 11;
                    } else {
                        withBal = 0;
                    }

                    tv_withBal.setText("₹ " + withBal);
//                    userTotalMatch = ui.getTotalmatch();

//                    tv_wallet_money.setText("₹ "+userBal);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}