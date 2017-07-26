package com.utstar.appstoreapplication.activity.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;
import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.L;
import com.arialyy.frame.util.show.T;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.net_entity.EPGParamsEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameDetailEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.VideoGameEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.VideoGameApi;
import com.utstar.appstoreapplication.activity.temp.activity.TempActivity;
import com.utstar.appstoreapplication.activity.utils.EpgUserUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import com.utstar.appstoreapplication.activity.windows.common.AuthUtil;
import com.utstar.appstoreapplication.activity.windows.game_detail.GameDetailActivity;
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
 * Created by Aria.Lao on 2017/6/19.
 */

final class VideoDetailModule extends BaseModule {
  static final int GET_PLAY_URL = 0x123;
  static final int GET_DETAIL = 0x124;
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

  public VideoDetailModule(Context context) {
    super(context);
  }

  public void getData() {
    Map<String, String> params = new WeakHashMap<>();
    params.put("productId", "1");
    params.put("number", "10");
    params.put("page", "1");
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<VideoGameEntity>() {
    }.getType(), new BasicDeserializer<VideoGameEntity>()).create();
    mNetManager.request(VideoGameApi.class, gson)
        .getVideoGameDetail(params)
        .compose(new HttpCallback<VideoGameEntity>() {
          @Override public void onResponse(VideoGameEntity response) {
            //callback(GET_DETAIL, response);
            AuthUtil util = new AuthUtil();
            util.auth(response.videos.get(0).id, new AuthCallback(response));
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
            //T.showShort(getContext(), FL.getExceptionString(e));
            callback(GET_DETAIL, null);
          }
        });
  }

  public void getUrl(String code) {
    callback(GET_PLAY_URL,
        //"http://172.18.104.14:8088/uploadServer/upload/mp4/AndroidCommercial.mp4");
        "http://data.video.ptqy.gitv.tv/videos/other/20160725/fe/a1/3f7dbf84c23fa3424399a040cd7688cf.f4v?v=845642301");
        //"https://archive.org/download/Popeye_forPresident/Popeye_forPresident_512kb.mp4");
    //url =
    //    "http://192.168.0.12:33200/EPG/jsp/gdgaoqing/en/vaitf/getVodPlayUrl.jsp?code=91a9758952f6400d88fb24bed311a511";
    //T.showShort(getContext(), "code ==> " + code);
    //String url =
    //    "http://192.168.0.12:33200/EPG/jsp/gdgaoqing/en/vaitf/getVodPlayUrl.jsp?code=" + code;
    //mSession = "JSESSIONID=" + EpgUserUtil.getUserEntity().getEpgSessionId();
    //Map<String, String> params = new WeakHashMap<>();
    //params.put("userid", EpgUserUtil.getUserEntity().getEpgUserId());
    //Map<String, String> head = new WeakHashMap<>();
    //head.put("Cookie", mSession);
    //get(url, null, head, new IResponse() {
    //  @Override public void onResponse(String data) {
    //    String callback = null;
    //    try {
    //      JSONObject jsonObject = new JSONObject(data);
    //      callback = jsonObject.optString("playurl");
    //    } catch (JSONException e) {
    //      e.printStackTrace();
    //    } finally {
    //      callback(GET_PLAY_URL, TextUtils.isEmpty(callback) ? "url 为null" : callback);
    //    }
    //  }
    //
    //  @Override public void onError(IOException e) {
    //    FL.e(TAG, FL.getExceptionString(e));
    //    callback(GET_PLAY_URL, null);
    //  }
    //});
  }

  /**
   * 鉴权回调
   */
  private class AuthCallback implements AuthUtil.AuthCallback {

    VideoGameEntity entity;

    AuthCallback(VideoGameEntity entity) {
      this.entity = entity;
    }

    @Override public void onSuccess() {
      entity.isBuy = true;
      callback(GET_DETAIL, entity);
    }

    @Override public void onFailure(String errorCode) {
      entity.isBuy = false;
      callback(GET_DETAIL, entity);
    }

    @Override public void onShelves(String state) {
      callback(GET_DETAIL, entity);
    }

    @Override public void onError() {
      entity.isBuy = false;
      callback(GET_DETAIL, entity);
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
}
