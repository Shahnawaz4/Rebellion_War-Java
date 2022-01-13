package com.gamingtournament.msa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class ForgotpassActivity extends AppCompatActivity {

    private EditText et_forgot_email;
    private Button btn_reset;
    String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);

        et_forgot_email=findViewById(R.id.et_forgot_email);
        btn_reset=findViewById(R.id.btn_reset);

         mAuth = FirebaseAuth.getInstance();

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String useremail=et_forgot_email.getText().toString().trim();

                if (useremail.equals("")){
                    Toast.makeText(ForgotpassActivity.this, "Enter Register Email", Toast.LENGTH_SHORT).show();
                }else if (!useremail.matches(emailPattern)){
                    Toast.makeText(ForgotpassActivity.this,"Email Not Valid", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.fetchSignInMethodsForEmail(useremail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()) {
                                boolean check = !task.getResult().getSignInMethods().isEmpty();
                                if (check) {
                                    sendEmail(useremail);
                                } else {
                                    Toast.makeText(ForgotpassActivity.this, "Email not Registered", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                }

            }
        });
    }
    public void gotologin(View view) {
        startActivity(new Intent(ForgotpassActivity.this,LoginActivity.class));
        finish();
    }

    private void sendEmail(String email){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotpassActivity.this, "Reset Password email sent", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotpassActivity.this,LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(ForgotpassActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
