//package com.utstar.appstoreapplication.activity.windows.game_video;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.KeyEvent;
//import android.view.View;
//import android.webkit.WebChromeClient;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import com.utstar.appstoreapplication.activity.BuildConfig;
//import com.utstar.appstoreapplication.activity.R;
//
///**
// * 游戏视频webview
// *
// * created by JesseHev 2017/3/2
// */
//public class GameVideoWebView extends Activity {
//
//  public static final String URL = "GAME_VIDEO_WEB_URL";
//  @Bind(R.id.webView) WebView mWebView;
//
//  private WebSettings mWebSettings;
//
//  private String mUrl;
//
//  @Override protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_game_video_web_view);
//    ButterKnife.bind(this);
//    mUrl = getIntent().getStringExtra(URL);
//    initWebView();
//  }
//
//  public void initWebView() {
//    //解决加载的时候出现白色背景
//    //设置webview背景之前一定要设置setBackgroundColor为透明
//    mWebView.setBackgroundColor(Color.parseColor("#00000000"));
//    mWebView.setBackgroundResource(R.mipmap.bg_base);
//
//    mWebView.setWebViewClient(getWebViewClient());
//    mWebSettings = mWebView.getSettings();
//    mWebSettings.setJavaScriptEnabled(true);
//    mWebSettings.setUseWideViewPort(true);
//    mWebSettings.setLoadWithOverviewMode(true);
//    mWebSettings.setJavaScriptEnabled(true);
//    mWebView.setWebChromeClient(getWebChromeClient());
//    mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//    //支持页面缩放
//    mWebSettings.setBuiltInZoomControls(true);
//    mWebSettings.setSupportZoom(true);
//    //当webview调用requestFocus时为webview设置节点
//    mWebSettings.setNeedInitialFocus(true);
//    if (BuildConfig.DEBUG) {
//      //mUrl = "http://192.168.4.182:8400/es/Default.aspx?userId=1495975";
//      //mUrl = "http://104.128.92.40/test.mp4";
//    }
//    mWebView.loadUrl(mUrl);
//  }
//
//  private WebViewClient getWebViewClient() {
//    return new WebViewClient() {
//      @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
//        super.onPageStarted(view, url, favicon);
//      }
//
//      @Override public void onPageFinished(WebView view, String url) {
//        super.onPageFinished(view, url);
//        mWebView.requestFocus();
//      }
//
//      @Override public void onReceivedError(WebView view, int errorCode, String description,
//          String failingUrl) {
//        super.onReceivedError(view, errorCode, description, failingUrl);
//      }
//
//      @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        //view.loadUrl(url);
//        return false;
//      }
//
//      @Override public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
//        //webview中的事件处理
//        //mWebView.clearView();
//        return super.shouldOverrideKeyEvent(view, event);
//      }
//    };
//  }
//
//  private WebChromeClient getWebChromeClient() {
//    return new WebChromeClient() {
//      @Override public void onProgressChanged(WebView view, int newProgress) {
//        if (newProgress == 100) {
//          mWebView.setVisibility(View.VISIBLE);
//          mWebView.requestFocus();
//        }
//        super.onProgressChanged(view, newProgress);
//      }
//    };
//  }
//
//  @Override public void onBackPressed() {
//    if (mWebView.canGoBack()) {
//      if (mWebView.getUrl().equals(mUrl)) {
//        super.onBackPressed();
//      } else {
//        mWebView.goBack();
//      }
//    } else {
//      super.onBackPressed();
//    }
//  }
//
//  @Override protected void onDestroy() {
//    super.onDestroy();
//    System.exit(0);
//  }
//}
