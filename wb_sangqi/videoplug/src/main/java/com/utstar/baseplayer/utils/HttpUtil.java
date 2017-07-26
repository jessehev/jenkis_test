package com.utstar.baseplayer.utils;

import android.support.annotation.NonNull;
import com.utstar.baseplayer.utils.inf.IResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by “AriaLyy@outlook.com” on 2015/11/5.
 * 网络连接工具
 */
public class HttpUtil {
  private static final String TAG = "HttpUtil";
  private static final int TIME_OUT = 5000;

  /**
   * 基本get方法
   */
  public void get(final @NonNull String url, final Map<String, String> params,
      final Map<String, String> header, @NonNull final IResponse absResponse) {
    L.v(TAG, "请求链接 >>>> " + url);
    String requestUrl = url;
    if (params != null && params.size() > 0) {
      Set set = params.entrySet();
      int i = 0;
      requestUrl += "?";
      for (Object aSet : set) {
        i++;
        Map.Entry entry = (Map.Entry) aSet;
        requestUrl += entry.getKey() + "=" + entry.getValue() + (i < params.size() ? "&" : "");
      }
      L.v(TAG, "请求参数为 >>>> ");
      L.m(params);
    }

    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
        .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
        .readTimeout(10000, TimeUnit.MILLISECONDS)
        .build();

    Headers.Builder hb = new Headers.Builder();

    if (header != null && !header.isEmpty()) {
      Set<String> keys = header.keySet();
      for (String key : keys) {
        hb.add(key, header.get(key));
      }
    }

    final Request request = new Request.Builder().url(requestUrl).headers(hb.build()).build();
    Call call = client.newCall(request);

    //请求加入调度
    call.enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {
        L.e(TAG, "请求链接【" + url + "】失败");
        absResponse.onError(e);
      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        String data = response.body().string();
        L.d(TAG, "数据获取成功，获取到的数据为 >>>> ");
        L.j(data);
        absResponse.onResponse(data);
      }
    });
  }

  /**
   * 返回String类型的响应
   */
  public static class AbsResponse implements IResponse {

    @Override public void onResponse(String data) {

    }

    @Override public void onError(IOException e) {

    }
  }
}
