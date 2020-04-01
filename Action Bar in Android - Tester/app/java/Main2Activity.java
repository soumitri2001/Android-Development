package com.example.actionbartester;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Browser");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // back button

        WebView webView=findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient()); // finds the default browser on the users phone and sets it up
        webView.canGoBackOrForward(1);
        /*
        *  To load up an existing webpage i.e. passing an URL
        */
        webView.loadUrl("https://www.codeforces.com");


        /*
        * to put in a self-made HTML page to load up
        * webView.loadData("<html><body><h1>Hello this is my website</h1></body></html>", "text/html", "UTF-8");
        */
    }
}
