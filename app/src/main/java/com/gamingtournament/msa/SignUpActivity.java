package com.gamingtournament.msa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gamingtournament.msa.interfaces.APITimeIST;
import com.gamingtournament.msa.model.TimeISTModel;
import com.gamingtournament.msa.model.UserInformation;
import com.gamingtournament.msa.payment.WithdrawMoneyActivity;
import com.gamingtournament.msa.utils.UtilValues;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import papaya.in.sendmail.SendMail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {

    private EditText fname, email, password, confirmpassword, et_phno;
    private Button btn_register;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressBar pb_register;

    FirebaseAuth mAuth;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fname = findViewById(R.id.et_fullName);
        email = findViewById(R.id.et_email);
        et_phno = findViewById(R.id.et_phno);
        password = findViewById(R.id.et_password);
        confirmpassword = findViewById(R.id.et_confirmPassword);
        btn_register = findViewById(R.id.btn_register);
        pb_register = findViewById(R.id.pb_register);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mAuth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pb_register.setVisibility(View.VISIBLE);

                final String name = fname.getText().toString().trim();
                final String register_email = email.getText().toString().trim();
                final String phoneNo = et_phno.getText().toString().trim();
                final String register_password = password.getText().toString().trim();
                final String register_confirm_password = confirmpassword.getText().toString().trim();


                if (name.isEmpty()) {
                    pb_register.setVisibility(View.GONE);
                    fname.setError("Enter your name");
                    fname.requestFocus();
                } else if (register_email.isEmpty()) {
                    pb_register.setVisibility(View.GONE);
                    email.setError("Enter your Email");
                    email.requestFocus();
                } else if (!register_email.matches(emailPattern)) {
                    pb_register.setVisibility(View.GONE);
                    email.setError("Email not valid");
                    email.requestFocus();
                } else if (phoneNo.isEmpty()) {
                    pb_register.setVisibility(View.GONE);
                    et_phno.setError("Enter Phone No");
                    et_phno.requestFocus();
                } else if (phoneNo.length() < 10 || phoneNo.length() > 13) {
                    pb_register.setVisibility(View.GONE);
                    et_phno.setError("Enter Valid Phone No");
                    et_phno.requestFocus();
                } else if (register_password.isEmpty()) {
                    pb_register.setVisibility(View.GONE);
                    password.setError("Email your password");
                    password.requestFocus();
                } else if (register_password.length() < 6) {
                    pb_register.setVisibility(View.GONE);
                    password.setError("password must be atleast 6 characters");
                    password.requestFocus();
                } else if (register_confirm_password.isEmpty()) {
                    pb_register.setVisibility(View.GONE);
                    confirmpassword.setError("Email your confirm password");
                    confirmpassword.requestFocus();
                } else if (!register_confirm_password.matches(register_password)) {
                    pb_register.setVisibility(View.GONE);
                    confirmpassword.setError("Password does not match");
                    confirmpassword.requestFocus();
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(register_email, register_password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {


                                        SendMail mail = new SendMail(UtilValues.SENDER_EMAIL, UtilValues.SENDER_PASS,
                                                register_email, UtilValues.EMAIL_REG_SUB, UtilValues.EMAIL_REG_MSG);

                                        mail.execute();

                                        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                        UserInformation information = new UserInformation(name, register_email, register_password, phoneNo,
                                                "", id, 0, 0, 11, 51);

                                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("users").child(id);
                                        dr.setValue(information);

                                        editor.putString("email", register_email);
                                        editor.putString("password", register_password);
                                        editor.putBoolean("savelogin", false);
                                        editor.apply();

                                        // Rs 11 transaction History
                                        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users")
                                                .child(id).child("000transaction").child(timeStamp);
                                        ref.child("status").setValue("credited");
                                        ref.child("id").setValue(timeStamp);
                                        ref.child("bal").setValue(11);

                                        Toast.makeText(SignUpActivity.this, "Registered Success", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        pb_register.setVisibility(View.GONE);
                                        Toast.makeText(SignUpActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }


                                }
                            });
                }


            }
        });

    }

    public void gotologin(View view) {
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }

}
