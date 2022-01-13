package com.gamingtournament.msa.more;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HelpSupportActivity extends AppCompatActivity {

    private EditText et_title, et_query;
    private TextView tv_TitleCount,tv_QueryCount;
    private Button btn_submit;
    private int titleCount=0, queryCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        et_title = findViewById(R.id.et_title);
        et_query = findViewById(R.id.et_query);
        tv_TitleCount = findViewById(R.id.tv_TitleCount);
        tv_QueryCount = findViewById(R.id.tv_QueryCount);
        btn_submit = findViewById(R.id.btn_submit);


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
                startActivity(new Intent(HelpSupportActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });


        et_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                titleCount = et_title.length();
                tv_TitleCount.setText(titleCount+"/40");
                if (titleCount>40){
                    tv_TitleCount.setTextColor(Color.RED);
                } else {
                    tv_TitleCount.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                queryCount = et_query.length();
                tv_QueryCount.setText(queryCount+"/300");
                if (queryCount>300){
                    tv_QueryCount.setTextColor(Color.RED);
                }else {
                    tv_QueryCount.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String st_title = et_title.getText().toString().trim();
                String st_query = et_query.getText().toString().trim();

                if (st_title.isEmpty()){
                    et_title.setError("Enter Title");
                    et_title.requestFocus();
                } else if (st_query.isEmpty()){
                    et_query.setError("Enter your Query");
                    et_query.requestFocus();
                } else {

                    if (titleCount<=40 && queryCount<=300){
                        et_title.setText("");
                        et_query.setText("");

                        long currTime = System.currentTimeMillis();
                        String st_currtime = Long.toString(currTime);

                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("helpsupport").child(uid).child(st_currtime);

                        db.child("title").setValue(st_title);
                        db.child("query").setValue(st_query);
                        db.child("uid").setValue(uid);

                        Toast.makeText(HelpSupportActivity.this, "Your query submitted successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HelpSupportActivity.this, MainActivity.class));
                        finish();

                    }

                    else {
                        Toast.makeText(HelpSupportActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }

                }


            }
        });

    }
}