package it.trade.android.exampleapp;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static it.trade.android.exampleapp.OauthLinkBrokerActivity.APP_DEEP_LINK;
import static it.trade.android.exampleapp.OauthLinkBrokerActivity.OAUTH_URL_PARAMETER;

public class WebViewActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String urlParam = intent.getStringExtra(OAUTH_URL_PARAMETER);

        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(urlParam);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if (url.contains(APP_DEEP_LINK)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}
