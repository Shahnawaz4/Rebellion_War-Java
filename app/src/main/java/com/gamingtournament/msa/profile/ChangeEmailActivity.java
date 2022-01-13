package com.gamingtournament.msa.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gamingtournament.msa.LoginActivity;
import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeEmailActivity extends AppCompatActivity {

    private Button btn_ChangeEmail;
    private EditText et_newEmail, et_currPass, et_ConfNewEmail;
    private String st_userEmail;
    private String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);


        btn_ChangeEmail = findViewById(R.id.btn_changeEmail);
        et_currPass = findViewById(R.id.et_currPass);
        et_newEmail = findViewById(R.id.et_newEmail);
        et_ConfNewEmail = findViewById(R.id.et_ConfirmNewEmail);

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
                startActivity(new Intent(ChangeEmailActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });

        btn_ChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String currtPass = et_currPass.getText().toString().trim();
                final String newEmail = et_newEmail.getText().toString().trim();
                final String confNewEmail = et_ConfNewEmail.getText().toString().trim();

                if (currtPass.isEmpty()){
                    et_currPass.setError("Enter Current Pass");
                    et_currPass.requestFocus();
                } else if (newEmail.isEmpty()){
                    et_newEmail.setError("Enter New Email");
                    et_newEmail.requestFocus();
                }else if (!newEmail.matches(emailPattern)){
                    et_newEmail.setError("Enter Valid Email");
                    et_newEmail.requestFocus();
                } else if (confNewEmail.isEmpty()){
                    et_ConfNewEmail.setError("Enter Confirm email");
                    et_ConfNewEmail.requestFocus();
                } else if (!confNewEmail.matches(newEmail)){
                    et_ConfNewEmail.setError("Pass does not match");
                    et_ConfNewEmail.requestFocus();
                } else {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    String userEmail = user.getEmail();
                    assert userEmail != null;

                    AuthCredential credential = EmailAuthProvider.getCredential(userEmail, currtPass);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                                                    .child("users").child(user.getUid());
                                            db.child("email").setValue(newEmail);

                                            sharedPreferences=getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                                            editor=sharedPreferences.edit();
                                            editor.putString("email",newEmail);
                                            editor.putBoolean("savelogin", false);
                                            editor.apply();
                                            Toast.makeText(ChangeEmailActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ChangeEmailActivity.this, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
//                                            finish();
                                        } else {
                                            Toast.makeText(ChangeEmailActivity.this, "Failed :"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(ChangeEmailActivity.this, "Authentication Failed: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }


                //
            }
        });

    }

}