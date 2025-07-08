package com.app.hammocklife.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.app.hammocklife.R;

public class AuthenticationDialog extends Dialog {
    private final String request_url;
    private final String redirect_url;
    private AuthenticationListener listener;

    public AuthenticationDialog(@NonNull Context context, AuthenticationListener listener) {
        super(context);
        this.listener = listener;
        this.redirect_url = context.getResources().getString(R.string.redirect_url);
        this.request_url = "https://www.instagram.com/oauth/authorize?client_id=537367623582326&redirect_uri=https://socialsizzle.herokuapp.com/auth/&scope=user_profile,user_media&response_type=code";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.auth_dialog);
        initializeWebView();
    }

    private void initializeWebView() {
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(request_url);
        webView.setWebViewClient(webViewClient);
    }

    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(redirect_url)) {
                AuthenticationDialog.this.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e("url",url);
            if (url.contains("https://socialsizzle.herokuapp.com/auth/")&&url.contains("#")) {
                String token = url.replace("https://socialsizzle.herokuapp.com/auth/?code=","").replace("#","");
                token = token.substring(0, token.length() - 1);
                Log.e("access_token", token);
                listener.onTokenReceived(token);
                dismiss();
            } else if (url.contains("?error")) {
                Log.e("access_token", "getting error fetching access token");
                dismiss();
            }
        }
    };
}
