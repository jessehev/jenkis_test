package com.utstar.appstoreapplication.activity.temp.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.SharePreUtil;
import com.arialyy.frame.util.show.L;
import com.arialyy.frame.util.show.T;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.constants.KeyConstant;
import com.utstar.appstoreapplication.activity.databinding.ActivityTempBinding;
import com.utstar.appstoreapplication.activity.entity.db_entity.EpgEntity;
import com.utstar.appstoreapplication.activity.utils.EpgUserUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by utstarcom on 2016/12/8.
 */
public class TempActivity extends BaseActivity<ActivityTempBinding> {
  private Call mCurrentCall;
  private static final int TIME_OUT = 5000;
  private String mSession = "";

  /**
   * 数据响应接口
   */
  public interface IResponse {
    /**
     * 响应的数据回调
     */
    public void onResponse(String data);

    /**
     * 错误返回回掉
     */
    public void onError(IOException e);
  }

  @Override protected int setLayoutId() {
    return R.layout.activity_temp;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    //String oldTinkerId =
    //    SharePreUtil.getString(KeyConstant.PRE_NAME, this, KeyConstant.PRE.TINKER_ID);
    ////getBinding().setVersion("热更新版本号:" + oldTinkerId);
    //getBinding().setVersion("TEMP界面___1\n"
    //    + "apk版本号: "
    //    + AndroidUtils.getVersionCode(this)
    //    + ", 热更新版本号:"
    //    + oldTinkerId);

    getBinding().setUserId("userId：" + EpgUserUtil.getUserEntity().getEpgUserId());
  }

  private static final String URL =
      "http://192.168.0.12:33200/EPG/jsp/gdgaoqing/en/vaitf/getVodPlayUrl.jsp?code=63d4eb06fa974b169df5a761990c76e9";

  public void getUrl(View view) {
    mSession = "JSESSIONID=" + EpgUserUtil.getUserEntity().getEpgSessionId();
    getBinding().setSession("session：" + mSession);
    switch (view.getId()) {
      case R.id.bt_1:
        String url = getPlayUrl(URL, mSession);
        if (TextUtils.isEmpty(url)) {
          url = "链接为空";
        }
        getBinding().setUrl(url);
        //Map<String, String> params_1 = new WeakHashMap<>();
        //params_1.put("userid", EpgUserUtil.getUserEntity().getEpgUserId());
        //Map<String, String> head_1 = new WeakHashMap<>();
        //head_1.put("Cookie", mSession);
        //get(URL, params_1, head_1, new IResponse() {
        //  @Override public void onResponse(String data) {
        //    runOnUiThread(() -> getBinding().setUrl(data));
        //  }
        //
        //  @Override public void onError(IOException e) {
        //    runOnUiThread(() -> getBinding().setError(e.getMessage()));
        //  }
        //});
        break;
      case R.id.bt_2:
        Map<String, String> params = new WeakHashMap<>();
        params.put("userid", EpgUserUtil.getUserEntity().getEpgUserId());
        Map<String, String> head = new WeakHashMap<>();
        head.put("Cookie", mSession);
        get(URL, null, head, new IResponse() {
          @Override public void onResponse(String data) {
            runOnUiThread(() -> {
              try {
                JSONObject jsonObject = new JSONObject(data);
                String url1 = jsonObject.optString("playurl");
                getBinding().setUrl(TextUtils.isEmpty(url1) ? "url 为null" : url1);
              } catch (JSONException e) {
                e.printStackTrace();
              }
            });

            runOnUiThread(() -> getBinding().setUrl(data));
          }

          @Override public void onError(IOException e) {
            runOnUiThread(() -> getBinding().setError(e.getMessage()));
          }
        });
        break;
    }
  }


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

  public String getPlayUrl(String reqUrl, String cookie) {
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

  private String parseResult(Response response) {
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
