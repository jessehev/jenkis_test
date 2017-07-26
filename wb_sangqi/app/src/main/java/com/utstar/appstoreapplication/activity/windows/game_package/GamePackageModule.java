package com.utstar.appstoreapplication.activity.windows.game_package;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ut.wb.ui.metro.MetroItemEntity;
import com.ut.wb.ui.metro.MetroLayout;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.HalfPackageEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.ActivityApi;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.HomeApi;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/15.
 */
@Deprecated final class GamePackageModule extends BaseModule {
  public GamePackageModule(Context context) {
    super(context);
  }

  private final Gson mMetroItemEntity = new GsonBuilder().registerTypeAdapter(
      new TypeToken<NetListContainerEntity<MetroItemEntity>>() {
      }.getType(), new BasicDeserializer<NetListContainerEntity<MetroItemEntity>>()).create();

  /**
   * 获取包月专区数据
   */
  void getPackageData(int page) {
    mNetManager.request(HomeApi.class, mMetroItemEntity)
        .getPackageArea(page)
        .compose(new HttpCallback<NetListContainerEntity<MetroItemEntity>>() {
          @Override public void onResponse(NetListContainerEntity<MetroItemEntity> response) {
            if (response.list != null && response.list.size() > 0) {
              callback(GamePackageFragment.GET_PACKAGE_DATA, response.list);
            }
          }
        });
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  public List<MetroItemEntity> createGamePackageData() {
    List<MetroItemEntity> list = new ArrayList<>();
    list.add(new MetroItemEntity(0, MetroLayout.SIMPLE_VERTICAL));
    list.add(new MetroItemEntity(0, MetroLayout.SIMPLE_VERTICAL));
    list.add(new MetroItemEntity(0, MetroLayout.SIMPLE_VERTICAL));
    list.add(new MetroItemEntity(0, MetroLayout.SIMPLE_SQUARE));
    list.add(new MetroItemEntity(0, MetroLayout.SIMPLE_SQUARE));
    list.add(new MetroItemEntity(1, MetroLayout.SIMPLE_SQUARE));
    list.add(new MetroItemEntity(1, MetroLayout.SIMPLE_SQUARE));
    return list;
  }

}
