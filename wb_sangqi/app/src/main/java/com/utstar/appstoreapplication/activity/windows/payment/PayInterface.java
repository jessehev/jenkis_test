package com.utstar.appstoreapplication.activity.windows.payment;

import android.content.Context;
import android.webkit.JavascriptInterface;

/**
 * JS支付回调接口
 */
public class PayInterface {
  private Context mContext;

  private PayCallback mPayCallback;

  public void setPayCallback(PayCallback payCallback) {
    mPayCallback = payCallback;
  }

  public interface PayCallback {
    void onSuccess();

    void onSuccess(String orderCode);

    void onFailure(String orderCode, String errorCode);
  }

  public PayInterface(Context c) {
    mContext = c;
  }

  @JavascriptInterface public void onSuccess() {
    if (mPayCallback != null) {
      mPayCallback.onSuccess();
    }
  }

  @JavascriptInterface public void onSuccess(String orderCode) {
    if (mPayCallback != null) {
      mPayCallback.onSuccess(orderCode);
    }
  }

  @JavascriptInterface public void onFailure(String orderCode, String errorCode) {
    if (mPayCallback != null) {
      mPayCallback.onFailure(orderCode, errorCode);
    }
  }
}