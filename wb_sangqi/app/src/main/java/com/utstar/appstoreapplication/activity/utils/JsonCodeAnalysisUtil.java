package com.utstar.appstoreapplication.activity.utils;

import android.text.TextUtils;
import com.arialyy.frame.util.AndroidUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.db_entity.EpgEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.WbUserEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.LauncherApi;
import com.utstar.appstoreapplication.activity.manager.NetManager;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by Aria.Lao on 2017/2/24.
 * 返回码解析器
 */
public class JsonCodeAnalysisUtil {

  public interface LoginCallback {
    public void onSuccess();

    public void onFail();
  }

  public static boolean isSuccess(JsonObject obj) {
    int code = obj.get("rltcode").getAsInt();
    if (code == 0 || code == -2) return true;
    if (code == -5) {
      reLogin();
    }
    return false;
  }

  /**
   * 重新登录
   */
  private static void reLogin() {
    reLogin(null);
  }

  public static void reLogin(LoginCallback callback) {
    Map<String, String> params = new WeakHashMap<>();
    EpgEntity entity = EpgUserUtil.getUserEntity();
    params.put("account", entity.getWbAccount());
    params.put("wbversion", AndroidUtils.getVersionCode(BaseApp.context) + "");
    params.put("epg_userid", entity.getEpgUserId());
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<WbUserEntity>() {
    }.getType(), new BasicDeserializer<WbUserEntity>()).create();
    //mNetManager.request(LauncherApi.class, WbUs erEntity.class)
    NetManager.getInstance()
        .request(LauncherApi.class, gson)
        .login(params)
        .compose(new HttpCallback<WbUserEntity>() {
          @Override public void onResponse(WbUserEntity response) {
            if (!TextUtils.isEmpty(response.account)) {
              entity.setWbAccount(response.account);
            }
            LauncherUtil.saveAccountAndChannel(BaseApp.context, entity.getWbAccount());
            if (callback != null) {
              callback.onSuccess();
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
            LauncherUtil.saveAccountAndChannel(BaseApp.context, entity.getWbAccount());
            if (callback != null) {
              callback.onFail();
            }
          }
        });
  }
}
