package com.gamingtournament.msa.utils;

import android.content.Context;
import android.widget.Toast;

import com.gamingtournament.msa.interfaces.APITimeIST;
import com.gamingtournament.msa.model.TimeISTModel;
import com.gamingtournament.msa.payment.WithdrawMoneyActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UtilValues {

    // email related value
    public static String EMAIL_REG_SUB = "Registration Successfull";
    public static String EMAIL_REG_MSG = "Hello sir, \n\nThank you for joining the WR family. \n" +
            "We hope you enjoy our app, WR is a Online gaming tournament app and we have regularly conducted different types of tournament like BGMI, Pubg lite, Free Fire etc" +
            "\n\n " +
            "Have question about Rebellion War? We'd love to help you! just hit reply. \n\n\n\nTeam WR family";


    public static String BASE_URL = "https://rebellionwar.000webhostapp.com";
    public static String WEB_URL = "https://rebellionwar.epizy.com";
    public static String WEB_URL_APP_DIR = "https://rebellionwar.epizy.com/app/";

    public static final String SENDER_EMAIL = "rebellion.war.official@gmail.com";
    public static final String SENDER_PASS = "Shahnawaz4@";



    private void getTime(int amount, String accNum, String ifsc, String accHolderName ){

        Retrofit retrofit = new Retrofit.Builder().baseUrl(UtilValues.WEB_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final APITimeIST service = retrofit.create(APITimeIST.class);

        retrofit2.Call<TimeISTModel> call = service.getTime();
        call.enqueue(new Callback<TimeISTModel>() {
            @Override
            public void onResponse(Call<TimeISTModel> call, Response<TimeISTModel> response) {
                if (response.isSuccessful()) {
                    String dateTime = response.body().getDatetime();
                    int timeStamp = response.body().getUnixtime();
//                    String msg = "Date: "+dateTime+"\n Time Stamp: "+timeStamp;
//                    tv_timezone.setText(dateTime+" and "+timeStamp);

                    if (timeStamp > 0){

                        String timeID = String.valueOf(timeStamp);
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("000payment").child(timeID);
                        ref.child("status").setValue("pending");
                        ref.child("id").setValue(timeID);
                        ref.child("bal").setValue(amount);
                        ref.child("txnid").setValue(timeID);
                        ref.child("uid").setValue(uid);
                        ref.child("accnum").setValue(accNum);
                        ref.child("ifsc").setValue(ifsc);
                        ref.child("accname").setValue(accHolderName);

                    }



                } else {
                    // error found
                }
            }

            @Override
            public void onFailure(Call<TimeISTModel> call, Throwable t) {
                // Error found
            }
        });
    }

}
