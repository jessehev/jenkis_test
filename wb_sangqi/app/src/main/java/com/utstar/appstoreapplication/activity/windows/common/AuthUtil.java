package com.utstar.appstoreapplication.activity.windows.common;

import com.arialyy.frame.util.show.FL;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetObjectEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.AuthEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.OrderDetailApi;
import com.utstar.appstoreapplication.activity.manager.NetManager;

/**
 * Created by Aria.Lao on 2017/1/13.
 * 鉴权工具
 */
public class AuthUtil {
  public interface AuthCallback {
    void onSuccess();

    void onFailure(String errorCode);

    /**
     * 已下架
     */
    void onShelves(String state);

    /**
     * 网络错误
     */
    void onError();
  }

  public AuthUtil() {
  }

  /**
   * 单款游戏鉴权
   */
  public void auth(int gameId, final AuthCallback callback) {
    final Gson gson =
        new GsonBuilder().registerTypeAdapter(new TypeToken<NetObjectEntity<AuthEntity>>() {
        }.getType(), new BasicDeserializer<NetObjectEntity<AuthEntity>>()).create();
    NetManager.getInstance()
        .request(OrderDetailApi.class, gson)
        .auth(gameId)
        .compose(new HttpCallback<NetObjectEntity<AuthEntity>>() {
          @Override public void onResponse(NetObjectEntity<AuthEntity> response) {
            AuthEntity entity = response.obj;
            String errorCode = entity.errorCode;
            String state = entity.status;
            FL.d("AUTH", entity.toString());
            if (callback != null) {
              if (state.equals("已下架")) {
                callback.onShelves(state);
                return;
              }
              if (errorCode.equals("0")) {
                callback.onSuccess();
              } else {
                callback.onFailure(errorCode);
              }
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
            if (callback != null) {
              callback.onError();
            }
          }
        });
  }
}
