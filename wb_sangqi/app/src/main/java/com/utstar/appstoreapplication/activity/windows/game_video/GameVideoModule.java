package com.utstar.appstoreapplication.activity.windows.game_video;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameVideoEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.GameVideoApi;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;

/**
 * Created by Administrator on 2017-03-02.
 *
 * 游戏视频业务层
 */

final class GameVideoModule extends BaseModule {
  private final Gson mGson = new GsonBuilder().registerTypeAdapter(
      new TypeToken<NetListContainerEntity<GameVideoEntity>>() {
      }.getType(), new BasicDeserializer<NetListContainerEntity<GameVideoEntity>>()).create();

  public GameVideoModule(Context context) {
    super(context);
  }

  /**
   * 获取游戏视频列表
   */
  void getVideoList() {
    mNetManager.request(GameVideoApi.class, mGson)
        .getGameVideoList()
        .compose(new HttpCallback<NetListContainerEntity<GameVideoEntity>>() {
          @Override public void onResponse(NetListContainerEntity<GameVideoEntity> response) {
            callback(GameVideoFragment.GAMEVIDEO_RESULT, response.list);
          }
        });
  }

  public int getPageSize(double dataSize) {
    int page = (int) Math.ceil(dataSize / 8);
    return page;
  }
}
