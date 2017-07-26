package com.utstar.appstoreapplication.activity.manager;

import com.arialyy.frame.util.show.L;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.StatisiticsApi;

/**
 * 方便后台做统计功能
 * Created by lt on 2017/3/23.
 */

public class ActionManager extends CommonManager {
  NetManager mNetManager;
  private static volatile ActionManager INSTANCE = null;
  private static final Object LOCK = new Object();
  private final Gson mGson = new GsonBuilder().registerTypeAdapter(new TypeToken<String>() {
  }.getType(), new BasicDeserializer<String>()).create();

  public static ActionManager getInstance() {
    if (INSTANCE == null) {
      synchronized (LOCK) {
        INSTANCE = new ActionManager();
      }
    }
    return INSTANCE;
  }

  private ActionManager() {

  }

  @Override void init() {
    mNetManager = NetManager.getInstance();
  }

  @Override String initName() {
    return "ActionManager";
  }

  @Override void onDestroy() {

  }

  /**
   * 1.从热门推荐或者套餐包进游戏详情
   *
   * @param type 热门推荐 type=1 热门推荐 type=2 从套餐包
   * @param gameId 游戏Id
   * @param vspId 渠道编号
   */
  public void statisticsIntoGameDetail(int type, int gameId, String vspId) {
    mNetManager.request(StatisiticsApi.class, mGson)
        .statisiticIntoGameDetail(type, gameId, vspId)
        .compose(new HttpCallback<String>() {
          @Override public void onResponse(String response) {
            L.d("response" + response);
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
          }
        });
  }

  /**
   * 1.从热门推荐或者套餐包进游戏详情并点击购买
   */
  public void statistcsBuy(int type, String packageId, int gameId) {
    mNetManager.request(StatisiticsApi.class, mGson)
        .statisiticBuy(type, packageId, gameId)
        .compose(new HttpCallback<String>() {
          @Override public void onResponse(String response) {
            L.d("response" + response);
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
          }
        });
  }
}
