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

public class ChangePassActivity extends AppCompatActivity {

    private Button btn_ChangePass;
    private EditText et_NewPass, et_CurrPass, et_ConfNewPass;
    private String st_userEmail;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        btn_ChangePass = findViewById(R.id.btn_ChangePass);
        et_CurrPass = findViewById(R.id.et_CurrPass);
        et_NewPass = findViewById(R.id.et_NewPass);
        et_ConfNewPass = findViewById(R.id.et_ConfNewPass);

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
                startActivity(new Intent(ChangePassActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });

        btn_ChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String currentPass = et_CurrPass.getText().toString().trim();
                final String newPass = et_NewPass.getText().toString().trim();
                final String confirmNewPass = et_ConfNewPass.getText().toString().trim();


                if (currentPass.isEmpty()){
                    et_CurrPass.setError("Enter Current Pass");
                    et_CurrPass.requestFocus();
                } else if (newPass.isEmpty()){
                    et_NewPass.setError("Enter New Pass");
                    et_NewPass.requestFocus();
                } else if (confirmNewPass.isEmpty()){
                    et_ConfNewPass.setError("Enter Confirm Pass");
                    et_ConfNewPass.requestFocus();
                } else if (!confirmNewPass.matches(newPass)){
                    et_ConfNewPass.setError("Pass does not match");
                    et_ConfNewPass.requestFocus();
                } else {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    String userEmail = user.getEmail();
                    assert userEmail != null;
                    AuthCredential credential = EmailAuthProvider.getCredential(userEmail, currentPass);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                                                    .child("users").child(user.getUid());
                                            db.child("password").setValue(newPass);

                                            sharedPreferences=getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                                            editor=sharedPreferences.edit();
                                            editor.putString("password",newPass);
                                            editor.putBoolean("savelogin", false);
                                            editor.apply();
                                            Toast.makeText(ChangePassActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ChangePassActivity.this, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(ChangePassActivity.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(ChangePassActivity.this, "Authentication Failed: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                // end
            }
        });

    }
}
