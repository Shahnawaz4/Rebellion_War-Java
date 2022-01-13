package com.gamingtournament.msa.more;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.gamingtournament.msa.MainActivity;
import com.gamingtournament.msa.R;
import com.gamingtournament.msa.utils.UtilValues;
import com.google.android.material.appbar.MaterialToolbar;

public class PrivacyPolicyActivity extends AppCompatActivity {

    WebView webview_privacy;
    private int data;
    private TextView tv_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        webview_privacy = findViewById(R.id.webview_privacy);
        tv_loading = findViewById(R.id.tv_loading);

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
                startActivity(new Intent(PrivacyPolicyActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });

        webview_privacy.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

        });
        WebSettings webSettings = webview_privacy.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview_privacy.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100){
                    tv_loading.setVisibility(View.GONE);
                    webview_privacy.setVisibility(View.VISIBLE);
                }
            }

        });


        Intent i = getIntent();
        data = i.getIntExtra("data", 1);

        if (data == 1){
            webview_privacy.loadUrl(UtilValues.WEB_URL_APP_DIR+"privacy.php");
        } else if (data == 2){
            toolbar.setTitle("Refund Policy");
            webview_privacy.loadUrl(UtilValues.WEB_URL_APP_DIR+"refund.php");
        } else if (data == 3){
            toolbar.setTitle("About us");
            webview_privacy.loadUrl(UtilValues.WEB_URL_APP_DIR+"aboutus.php");
        }else if (data == 4){
            toolbar.setTitle("Contact us");
            webview_privacy.loadUrl(UtilValues.WEB_URL_APP_DIR+"contactus.php");
        }else if (data == 5){
            toolbar.setTitle("Game Rules");
            webview_privacy.loadUrl(UtilValues.WEB_URL_APP_DIR+"rules.php");
        }else if (data == 6){
            toolbar.setTitle("Term Condition");
            webview_privacy.loadUrl(UtilValues.WEB_URL_APP_DIR+"term.php");
        }else if (data == 7){
            toolbar.setTitle("FAQ's");
            webview_privacy.loadUrl(UtilValues.WEB_URL_APP_DIR+"faqs.php");
        }else if (data == 8){
            toolbar.setTitle("How To Play");
            webview_privacy.loadUrl(UtilValues.WEB_URL_APP_DIR+"howtoplay.php");
        }else if (data == 9){
            toolbar.setTitle("Become Admin");
            webview_privacy.loadUrl(UtilValues.WEB_URL_APP_DIR+"becomeadmin.php");
        } else {
            webview_privacy.loadUrl(UtilValues.WEB_URL_APP_DIR+"privacy.php");
        }

    }

    @Override
    public void onBackPressed() {
        if (webview_privacy.canGoBack()){
            webview_privacy.goBack();
        } else {
            super.onBackPressed();
        }

    }

}
