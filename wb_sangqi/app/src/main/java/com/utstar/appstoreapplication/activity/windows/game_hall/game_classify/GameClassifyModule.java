package com.utstar.appstoreapplication.activity.windows.game_hall.game_classify;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameClassifyEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameSizeEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.GameClassifyApi;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;

/**
 * Created by Aria.Lao on 2016/12/15.
 */
public class GameClassifyModule extends BaseModule {
  public GameClassifyModule(Context context) {
    super(context);
  }

  /**
   * 根据mode获取分类游戏
   * type = 1; // 获取手柄游戏
   * type = 2;  // 获取遥控器游戏
   */
  public void getGameListByMode(int page, int mode) {
    Gson gson = new GsonBuilder().registerTypeAdapter(
        new TypeToken<NetListContainerEntity<GameClassifyEntity>>() {
        }.getType(), new BasicDeserializer<NetListContainerEntity<GameClassifyEntity>>()).create();

    mNetManager.request(GameClassifyApi.class, gson)
        .getGameListByMode(1, 9, mode)
        .compose(new HttpCallback<NetListContainerEntity<GameClassifyEntity>>() {
          @Override public void onResponse(NetListContainerEntity<GameClassifyEntity> response) {
            callback(GameClassifyActivity.MODE_RESULT, response.list);
          }

          @Override public void onFailure(Throwable e) {
            callback(GameClassifyActivity.REQUEST_ERROR_RESULT, e);
          }
        });
  }

  /**
   * 根据typeId获取分类游戏
   */
  public void getGameListByTag(int page, int typeId) {
    Gson gson = new GsonBuilder().registerTypeAdapter(
        new TypeToken<NetListContainerEntity<GameClassifyEntity>>() {
        }.getType(), new BasicDeserializer<NetListContainerEntity<GameClassifyEntity>>()).create();

    mNetManager.request(GameClassifyApi.class, gson)
        .getGameListByTag(1, 9, typeId)
        .compose(new HttpCallback<NetListContainerEntity<GameClassifyEntity>>() {
          @Override public void onResponse(NetListContainerEntity<GameClassifyEntity> response) {
            callback(GameClassifyActivity.TAG_RESULT, response.list);
          }

          @Override public void onFailure(Throwable e) {
            callback(GameClassifyActivity.REQUEST_ERROR_RESULT, e);
          }
        });
  }

  /**
   * 根据mode、typeId获取单个分类游戏
   *
   * @param type 1:分类下的 "全部" 标签 数据; 0:遥控器、手柄下的"全部" 标签数据
   */
  public void getGameListByModeAndType(int type, int page, int typeId, int mode) {
    Gson gson = new GsonBuilder().registerTypeAdapter(
        new TypeToken<NetListContainerEntity<GameClassifyEntity.GameClassifySubEntity>>() {
        }.getType(),
        new BasicDeserializer<NetListContainerEntity<GameClassifyEntity.GameClassifySubEntity>>())
        .create();
    mNetManager.request(GameClassifyApi.class, gson)
        .getGameListByModeAndType(type, page, 8, typeId, mode)
        .compose(
            new HttpCallback<NetListContainerEntity<GameClassifyEntity.GameClassifySubEntity>>() {
              @Override public void onResponse(
                  NetListContainerEntity<GameClassifyEntity.GameClassifySubEntity> response) {
                callback(GameClassifyActivity.TAG_MODE_RESULT, response.list);
              }

              //@Override public void onFailure(Throwable e) {
              //  callback(GameClassifyActivity.REQUEST_ERROR_RESULT,e);
              //}
            }

        );
  }

  /**
   * 获取mode分类游戏大小
   */
  public void getGameSizeByMode(int mode, int typeId) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<GameSizeEntity>() {
    }.getType(), new BasicDeserializer<GameSizeEntity>()).create();

    mNetManager.request(GameClassifyApi.class, gson)
        .getGameSizeByMode(mode, typeId)
        .compose(new HttpCallback<GameSizeEntity>() {
          @Override public void onResponse(GameSizeEntity response) {
            callback(GameClassifyActivity.TAG_MODE_SIZE_RESULT, response);
          }
        });
  }

  /**
   * 获取type分类游戏大小
   */
  public void getGameSizeByType(int typeId, int mode) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<GameSizeEntity>() {
    }.getType(), new BasicDeserializer<GameSizeEntity>()).create();

    mNetManager.request(GameClassifyApi.class, gson)
        .getGameSizeByType(typeId, mode)
        .compose(new HttpCallback<GameSizeEntity>() {
          @Override public void onResponse(GameSizeEntity response) {
            callback(GameClassifyActivity.TAG_TYPE_SIZE_RESULT, response);
          }
        });
  }

  /**
   * 计算viewpage的页数
   */
  public int getPageSize(double dataSize) {
    int num = (int) Math.ceil(dataSize / 8);
    return num;
  }
}
