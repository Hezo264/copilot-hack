package com.afrobuilders.gradecalculator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.WindowInsets;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private static final long SPLASH_DURATION_MS = 1800;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private WebView webView;
    private final Runnable showCalculator = this::showCalculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showSplashScreen();
        mainHandler.postDelayed(showCalculator, SPLASH_DURATION_MS);
    }

    private void showSplashScreen() {
        TextView brandName = new TextView(this);
        brandName.setText("KABONA");
        brandName.setTextColor(Color.RED);
        brandName.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        brandName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
        brandName.setGravity(Gravity.CENTER);
        brandName.setContentDescription("KABONA");

        FrameLayout splashScreen = new FrameLayout(this);
        splashScreen.setBackgroundColor(Color.WHITE);
        splashScreen.addView(brandName, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        setContentView(splashScreen);
    }

    private void showCalculator() {
        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            webView.setOnApplyWindowInsetsListener((view, insets) -> {
                android.graphics.Insets systemBars =
                        insets.getInsets(WindowInsets.Type.systemBars());
                view.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                );
                return insets;
            });
        }

        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mainHandler.removeCallbacks(showCalculator);
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
