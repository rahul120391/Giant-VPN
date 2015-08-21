package com.giantvpn;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

public class web_view extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openweb);

        ImageView close = (ImageView) findViewById(R.id.webView_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        WebView mywebview = (WebView) findViewById(R.id.webView);
        mywebview.loadUrl("http://developer.android.com/index.html");
    }
}