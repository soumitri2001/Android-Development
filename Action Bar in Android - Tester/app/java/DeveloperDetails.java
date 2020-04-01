package com.example.actionbartester;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DeveloperDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_details);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Developer Details");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // back button

        WebView webView=findViewById(R.id.github);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient()); // finds the default browser on the users phone and sets it up
        webView.canGoBackOrForward(1);
        /*
         *  To load up an existing webpage i.e. passing an URL
         * webView.loadUrl("https://www.github.com/soumitri2001");
        */


        /*
          * to put in a self-made HTML page to load up
         */
          webView.loadData("<html><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"><body><h1>Developer Details:</h1><h2>Soumitri Chattopadhyay<br>Jadavpur University<br>BE IT UG_1</h2></body></html>", "text/html", "UTF-8");

    }
}
