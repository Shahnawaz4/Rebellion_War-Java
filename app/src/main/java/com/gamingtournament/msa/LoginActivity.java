package com.gamingtournament.msa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login;
    private EditText et_email, et_password;
    private CheckBox checkbox_login;
    private ProgressBar pb_login;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login=findViewById(R.id.btn_login);
        et_email=findViewById(R.id.et_login_email);
        et_password=findViewById(R.id.et_login_password);
        checkbox_login=findViewById(R.id.checkbox_login);
        pb_login=findViewById(R.id.pb_login);


        sharedPreferences=getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        editor=sharedPreferences.edit();

        String mail=sharedPreferences.getString("email","");
        String password=sharedPreferences.getString("password","");
        Boolean savelogin = sharedPreferences.getBoolean("savelogin", false);
        et_email.setText(mail);
        et_password.setText(password);

        if (savelogin){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb_login.setVisibility(View.VISIBLE);


                String register_email=et_email.getText().toString();
                String register_password=et_password.getText().toString();


                if (register_email.isEmpty()){
                    pb_login.setVisibility(View.GONE);
                    et_email.setError("Enter your email");
                    et_email.requestFocus();
                } else if (register_password.isEmpty()){
                    pb_login.setVisibility(View.GONE);
                    et_password.setError("Enter your password");
                    et_password.requestFocus();
                } else {

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(register_email,register_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){
                                        pb_login.setVisibility(View.GONE);

                                        //for store the datd in share preferance if checked
                                        if (checkbox_login.isChecked()){
                                            editor.putString("email",et_email.getText().toString());
                                            editor.putString("password",et_password.getText().toString());
                                            editor.putBoolean("savelogin", true);
                                            editor.apply();
                                        } else {
                                            editor.putString("email","");
                                            editor.putString("password","");
                                            editor.putBoolean("savelogin", false);
                                            editor.commit();
                                        }

                                        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        pb_login.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this,"Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }



            }
        });
    }

    public void gotoRegisterActivity(View view) {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    public void gotoForgotPass(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotpassActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
