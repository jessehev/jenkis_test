package com.utstar.appstoreapplication.activity.module;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.net_entity.ModuleBgEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.EachModuleBgApi;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;

/**
 * 灵活配置各个专区的背景图
 * Created by lt on 2017/3/1.
 */
public class CommonModule extends BaseModule<ViewDataBinding> {
  public static final int MODULEBG_SUCCESS = 0X33;
  public static final int MODULEBG_FAILURE = 0X44;
  private final Gson mGson = new GsonBuilder().registerTypeAdapter(new TypeToken<ModuleBgEntity>() {
  }.getType(), new BasicDeserializer<ModuleBgEntity>()).create();

  public CommonModule(Context context) {
    super(context);
  }

  /**
   * 1.各个tab模块的背景图
   *
   * @param type 热门推荐-1，精品专区-2，游戏大厅-3，视频专区-4，搜索-5,系统-6）
   */
  public void getEachModuleBackground(int type, int pageNo) {
    mNetManager.request(EachModuleBgApi.class, mGson)
        .getModuleBg(type, pageNo)
        .compose(new HttpCallback<ModuleBgEntity>() {
          @Override public void onResponse(ModuleBgEntity response) {
            if (response != null) {
              callback(MODULEBG_SUCCESS, response);
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
            callback(MODULEBG_FAILURE, "获取背景图失败，或者没有配置背景图");
          }
        });
  }

  /**
   * 2.精品专区的背景图
   */

  public void getEachPackBackground(String packageId) {
    mNetManager.request(EachModuleBgApi.class, mGson)
        .getPackageBg(packageId)
        .compose(new HttpCallback<ModuleBgEntity>() {
          @Override public void onResponse(ModuleBgEntity response) {
            if (response != null) {
              callback(MODULEBG_SUCCESS, response);
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
          }
        });
  }

  /**
   * 3.游戏大厅各个模块的背景图
   */
  public void getGameHallBg(int tagId) {
    mNetManager.request(EachModuleBgApi.class, mGson)
        .getGameHall(tagId)
        .compose(new HttpCallback<ModuleBgEntity>() {
          @Override public void onResponse(ModuleBgEntity response) {
            if (response != null) {
              callback(MODULEBG_SUCCESS, response);
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
          }
        });
  }

  /**
   * 4.游戏大厅左侧导航模块的背景图
   *
   * @param type type=1 手柄游戏
   * type=2 遥控器游戏
   */
  public void getGameHallLeftBg(int type) {
    mNetManager.request(EachModuleBgApi.class, mGson)
        .getGameHallLeftBg(type, 1)
        .compose(new HttpCallback<ModuleBgEntity>() {
          @Override public void onResponse(ModuleBgEntity response) {
            if (response != null) callback(MODULEBG_SUCCESS, response);
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
          }
        });
  }
}
