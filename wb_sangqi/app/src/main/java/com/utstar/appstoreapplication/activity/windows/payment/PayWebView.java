package com.utstar.appstoreapplication.activity.windows.payment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.arialyy.frame.util.show.L;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.utils.CommonUtil;
import com.utstar.appstoreapplication.activity.windows.common.MsgDialog;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by lyy on 2016/9/8.
 * 支付WebView，该WebView在另外的线程中执行
 */
public class PayWebView extends Activity implements PayInterface.PayCallback {
  public static final String URL = "URL";
  public static final String TAG = "PayWebView";
  public static final String ACTION_WEB_FINISH = "com.wanba.web.pay.finish";
  public static final String KEY_PAY_FILTER = "WAN_BA_PAY_FILTER_KEY";
  public static final String KEY_ORDER_CODE = "ORDER_CODE_KEY";
  public static final String KEY_ERROR_CODE = "KEY_ERROR_CODE";

  @Bind(R.id.webView) WebView mWebView;
  private WebSettings mWebSetting;
  String mUrl;
  private PayInterface mInterFace;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    //getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#020202")));
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pay_web);
    ButterKnife.bind(this);
    mUrl = getIntent().getStringExtra(URL);
    if (TextUtils.isEmpty(URL)) {
      L.e(TAG, "请输入正确的URL");
      finish();
      return;
    }
    initWidget();
  }

  private void initWidget() {
    mInterFace = new PayInterface(this);
    mInterFace.setPayCallback(this);
    mWebView.setWebViewClient(getWebViewClient());
    mWebSetting = mWebView.getSettings();
    mWebSetting.setJavaScriptEnabled(true);
    mWebSetting.setUseWideViewPort(true);
    mWebSetting.setLoadWithOverviewMode(true);
    mWebView.addJavascriptInterface(mInterFace, "WB_PAY");
    mWebView.setWebChromeClient(getWebChromeClient());
    loadUrl(mUrl);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    Intent intent = new Intent(ACTION_WEB_FINISH);
    intent.putExtra("code", TurnManager.PAY_RESULT_CODE);
    sendBroadcast(intent);
    System.exit(0);
  }

  /**
   * 给子类提供加载网页的接口
   */
  protected void loadUrl(String url) {
    mWebView.loadUrl(url);
  }

  public WebView getmWebView() {
    return mWebView;
  }

  public WebSettings getmWebSetting() {
    return mWebSetting;
  }

  public void setmWebSetting(WebSettings mWebSetting) {
    this.mWebSetting = mWebSetting;
  }

  private WebChromeClient getWebChromeClient() {
    return new WebChromeClient() {
      @Override public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress == 100) {
          mWebView.setVisibility(View.VISIBLE);
          mWebView.requestFocus();
          //mWebView.loadUrl("javascript:doFocus()");
        } else {
          mWebView.setVisibility(View.GONE);
        }
        super.onProgressChanged(view, newProgress);
      }
    };
  }

  @Override public void onSuccess() {
    showDialog("消费提示", "购买成功，快去体验吧！", true, "0", "");
  }

  @Override public void onSuccess(final String orderCode) {
    showDialog("消费提示", "购买成功，快去体验吧！", true, "0", orderCode);
  }

  @Override public void onFailure(String orderCode, String errorCode) {
    showDialog("消费提示", "购买失败，请稍后再次尝试！", false, errorCode, orderCode);
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    sendMessage(false, "-1", "");
    finish();
  }

  private void sendMessage(boolean success, String errorCode, String orderCode) {
    Intent intent = new Intent(KEY_PAY_FILTER);
    intent.putExtra(TurnManager.PAY_SUCCESS_KEY, success);
    intent.putExtra(KEY_ERROR_CODE, errorCode);
    intent.putExtra(KEY_ORDER_CODE, orderCode);
    sendBroadcast(CommonUtil.setData(PayWebView.this, intent));
  }

  private void showDialog(String title, String msg, boolean success, String errorCode,
      String orderCode) {
    Observable.just("")
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<String>() {
          @Override public void onCompleted() {
          }

          @Override public void onError(Throwable e) {
          }

          @Override public void onNext(String s) {
            mWebView.setVisibility(View.GONE);
            MsgDialog dialog = new MsgDialog(PayWebView.this, title, msg, false);
            dialog.setDialogCallback(new MsgDialog.OnMsgDialogCallback() {
              @Override public void onEnter() {
                sendMessage(success, errorCode, orderCode);
                finish();
              }

              @Override public void onCancel() {
              }
            });
            dialog.show();
          }
        });
  }

  private WebViewClient getWebViewClient() {
    return new WebViewClient() {
      @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
        //                showWebLoadingDialog();
        super.onPageStarted(view, url, favicon);
      }

      @Override public void onPageFinished(WebView view, String url) {
        //                dismissWebLoadDialog();
        super.onPageFinished(view, url);
        mWebView.requestFocus();
        mWebView.loadUrl("javascript:doFocus()");
      }

      @Override public void onReceivedError(WebView view, int errorCode, String description,
          String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        //                dismissWebLoadDialog();
      }
    };
  }

  @Override public void finish() {
    super.finish();
    //        dismissWebLoadDialog();
  }

  private void showWebLoadingDialog() {
  }

  private void dismissWebLoadDialog() {
  }
}
