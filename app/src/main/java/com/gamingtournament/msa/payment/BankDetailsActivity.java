package com.gamingtournament.msa.payment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.model.BankModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BankDetailsActivity extends AppCompatActivity {


    private Button btn_addBankDetails;
    private EditText et_accNum, et_confirmAccNum, et_ifsc, et_accHolderName;

    private String accNum;
    private String accName;
    private String accIfsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);



        btn_addBankDetails = findViewById(R.id.btn_addBankDetails);
        et_accNum = findViewById(R.id.et_accNum);
        et_confirmAccNum = findViewById(R.id.et_confirmAccNum);
        et_ifsc = findViewById(R.id.et_ifsc);
        et_accHolderName = findViewById(R.id.et_accHolderName);




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

                    if (accNum!=null){
                        et_accNum.setText(bm.getAccnum());
                        et_confirmAccNum.setText(bm.getAccnum());
                    }

                    if (accName!=null){
                        et_accHolderName.setText(bm.getAccname());
                    }

                    if (accIfsc!=null){
                        et_ifsc.setText(bm.getIfsc());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BankDetailsActivity.this, "Error1:"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        btn_addBankDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String accNumber = et_accNum.getText().toString().trim();
                String ifsc = et_ifsc.getText().toString().trim();
                String confirmAccNum = et_confirmAccNum.getText().toString().trim();
                String accHolderName = et_accHolderName.getText().toString().trim();

                if (accNumber.isEmpty()){
                    et_accNum.setError("Mandatory");
                    et_accNum.requestFocus();
                } else if (confirmAccNum.isEmpty()){
                    et_confirmAccNum.setError("Mandatory");
                    et_confirmAccNum.requestFocus();
                } else  if (!accNumber.matches(confirmAccNum)){
                    et_confirmAccNum.setError("Account number does not match");
                    et_confirmAccNum.requestFocus();
                } else  if (ifsc.isEmpty()){
                    et_ifsc.setError("Mandatory");
                    et_ifsc.requestFocus();
                } else  if (ifsc.length() < 11){
                    et_ifsc.setError("IFSC not valid");
                    et_ifsc.requestFocus();
                } else  if (accHolderName.isEmpty()){
                    et_accHolderName.setError("Mandatory");
                    et_accHolderName.requestFocus();
                }  else {

                    // update user balance
                    DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                    rf.child("accnum").setValue(accNumber);
                    rf.child("ifsc").setValue(ifsc);
                    rf.child("accname").setValue(accHolderName);
                    Toast.makeText(BankDetailsActivity.this, "Bank Details Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BankDetailsActivity.this, WithdrawMoneyActivity.class));
                    finish();

                }
            }
        });

    }

}