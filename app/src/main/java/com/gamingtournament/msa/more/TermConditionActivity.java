package com.gamingtournament.msa.more;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gamingtournament.msa.ClashofClansActivity;
import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.utils.UtilValues;
import com.google.android.material.appbar.MaterialToolbar;

public class TermConditionActivity extends AppCompatActivity {

    WebView webview_term_condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_condition);


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
                startActivity(new Intent(TermConditionActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });


        webview_term_condition = findViewById(R.id.webview_term_condition);
        webview_term_condition.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        webview_term_condition.loadUrl("https://www.rebellionwar.epizy.com/app/term.php");

        WebSettings webSettings = webview_term_condition.getSettings();
        webSettings.setJavaScriptEnabled(true);



    }







    @Override
    public void onBackPressed() {
        if (webview_term_condition.canGoBack()){
            webview_term_condition.goBack();
        } else {
            super.onBackPressed();
        }

    }
}



