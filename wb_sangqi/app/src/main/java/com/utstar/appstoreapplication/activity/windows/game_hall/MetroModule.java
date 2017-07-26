package com.utstar.appstoreapplication.activity.windows.game_hall;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ut.wb.ui.metro.MetroItemEntity;
import com.ut.wb.ui.metro.MetroLayout;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.HomeApi;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/15.
 */
final class MetroModule extends BaseModule {
  public MetroModule(Context context) {
    super(context);
  }

  private final Gson mMetroItemEntity = new GsonBuilder().registerTypeAdapter(
      new TypeToken<NetListContainerEntity<MetroItemEntity>>() {
      }.getType(), new BasicDeserializer<NetListContainerEntity<MetroItemEntity>>()).create();

  private boolean isTest = false;

  /**
   * 获取Metro数据
   *
   * @param type {@link MetroFragment#GAME_HALL},{@link MetroFragment#GAME_PACKAGE}
   */
  void getMetroData(int type, int page) {
    if (isTest) {
      getTestGameHallData(type);
      return;
    }
    if (type == MetroFragment.GAME_HALL) {
      getGameHallData(page);
    } else if (type == MetroFragment.GAME_PACKAGE) {
      getPackageData(page);
    }
  }

  /**
   * 获取游戏大厅数据
   */
  private void getGameHallData(int page) {
    List<MetroItemEntity> list = new ArrayList<>();
    if (page == 1) {
      MetroItemEntity ykq = new MetroItemEntity(0, MetroLayout.SIMPLE_SQUARE);
      ykq.simpleItemData = new MetroItemEntity.SimpleItemData();
      ykq.simpleItemData.resId = R.mipmap.bg_game_hall_ykq;
      ykq.simpleItemData.title = "遥控器游戏";
      ykq.turnType = MetroItemEntity.TURN_GAME_HALL_MODE_YKQ;
      list.add(ykq);
      MetroItemEntity sb = new MetroItemEntity(1, MetroLayout.SIMPLE_SQUARE);
      sb.simpleItemData = new MetroItemEntity.SimpleItemData();
      sb.simpleItemData.resId = R.mipmap.bg_game_hall_sb;
      sb.simpleItemData.title = "手柄游戏";
      sb.turnType = MetroItemEntity.TURN_GAME_HALL_MODE_SB;
      list.add(sb);
    }
    mNetManager.request(HomeApi.class, mMetroItemEntity)
        .getGameHall(page)
        .compose(new HttpCallback<NetListContainerEntity<MetroItemEntity>>() {
          @Override public void onResponse(NetListContainerEntity<MetroItemEntity> response) {
            if (response.list != null) {
              list.addAll(response.list);
            }
            callback(MetroFragment.GET_METRO_DATA, list);
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
            callback(MetroFragment.GET_METRO_DATA, list);
          }
        });
  }

  /**
   * 获取包月专区数据
   */
  private void getPackageData(int page) {
    mNetManager.request(HomeApi.class, mMetroItemEntity)
        .getPackageArea(page)
        .compose(new HttpCallback<NetListContainerEntity<MetroItemEntity>>() {
          @Override public void onResponse(NetListContainerEntity<MetroItemEntity> response) {
            callback(MetroFragment.GET_METRO_DATA, response.list);
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
            callback(MetroFragment.GET_METRO_DATA, null);
          }
        });
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  private void getTestGameHallData(int type) {
    if (type == MetroFragment.GAME_HALL) {
      List<MetroItemEntity> list = new ArrayList<>();
      list.add(new MetroItemEntity(0, MetroLayout.SIMPLE_SQUARE));
      list.add(new MetroItemEntity(1, MetroLayout.SIMPLE_SQUARE));
      list.add(new MetroItemEntity(0, MetroLayout.SIMPLE_VERTICAL));
      list.add(new MetroItemEntity(0, MetroLayout.SIMPLE_SQUARE));
      list.add(new MetroItemEntity(0, MetroLayout.SIMPLE_HORIZONTAL));
      list.add(new MetroItemEntity(1, MetroLayout.SIMPLE_SQUARE));
      list.add(new MetroItemEntity(1, MetroLayout.SIMPLE_SQUARE));
      list.add(new MetroItemEntity(1, MetroLayout.SIMPLE_SQUARE));
      callback(MetroFragment.GET_METRO_DATA, list);
    } else if (type == MetroFragment.GAME_PACKAGE) {
      String json = getContext().getResources().getString(R.string.package_area);
      List<MetroItemEntity> list =
          new Gson().fromJson(json, new TypeToken<List<MetroItemEntity>>() {
          }.getType());
      callback(MetroFragment.GET_METRO_DATA, list);
    }
  }
}
