package com.utstar.baseplayer.utils;

import android.text.TextUtils;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Richard
 */
public class PlayUrlProxy {
  private static Call mCurrentCall;

  public static String getPlayUrl(String reqUrl, String cookie) {
    if (mCurrentCall != null) {
      mCurrentCall.cancel();
    }
    Request.Builder builder = new Request.Builder();
    if (!TextUtils.isEmpty(cookie)) {
      builder.addHeader("Cookie", cookie);
    }
    Request req = builder.url(reqUrl).get().build();
    OkHttpClient.Builder builder1 = new OkHttpClient.Builder();
    builder1.connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS);
    OkHttpClient client = builder1.build();
    mCurrentCall = client.newCall(req);
    try {
      return parseResult(mCurrentCall.execute());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      mCurrentCall = null;
    }
    return null;
  }

  private static String parseResult(Response response) {
    try {
      String result = response.body().string();
      JSONObject jsonObject = new JSONObject(result);
      return jsonObject.optString("playurl");
    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
    return null;
  }
}
