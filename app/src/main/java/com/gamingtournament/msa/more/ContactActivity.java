package com.gamingtournament.msa.more;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.utils.UtilValues;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

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
                startActivity(new Intent(ContactActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });

    }

    public void gotoemail(View view) {
        try {

            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:")); //only email app can handle this
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{UtilValues.SENDER_EMAIL});
            email.putExtra(Intent.EXTRA_SUBJECT, "Contact");
            startActivity(email);

        }catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(this, "No Email app found", Toast.LENGTH_LONG).show();
        }

//        Intent email= new Intent(Intent.ACTION_SEND);
//        email.setType("plain/text");
//        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"gamingtournament07@gmail.com"});
//        email.putExtra(Intent.EXTRA_SUBJECT, "subject");
//        email.putExtra(Intent.EXTRA_TEXT, "");
//        startActivity(Intent.createChooser(email, ""));

    }

    public void gotocall(View view) {

        try {
            Intent call = new Intent(Intent.ACTION_DIAL);
            call.setData(Uri.parse("tel:+917520244089"));
            startActivity(call);
        }catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(this, "No Calling app found", Toast.LENGTH_LONG).show();
        }

    }

    public void gotowhatsapp(View view) {

        try {
            String msg = "Hello Rebellion War";
            String url = "https://api.whatsapp.com/send?phone=+917520244089&text="+ URLEncoder.encode(msg,"UTF-8");

            PackageManager pm = ContactActivity.this.getPackageManager();
            Intent whatsapp = new Intent(Intent.ACTION_VIEW);
            whatsapp.setPackage("com.whatsapp");
            whatsapp.setData(Uri.parse(url));
            if (whatsapp.resolveActivity(pm) != null){
                startActivity(whatsapp);
            } else {
                Toast.makeText(this, "Whatsapp Not Installed", Toast.LENGTH_LONG).show();
            }

        }catch (Exception e){
            Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void gotomap(View view) {
        Toast.makeText(this, "Map is not Updated", Toast.LENGTH_SHORT).show();
    }
}
