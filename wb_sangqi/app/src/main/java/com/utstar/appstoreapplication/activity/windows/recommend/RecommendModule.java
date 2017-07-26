package com.utstar.appstoreapplication.activity.windows.recommend;

import android.content.Context;
import android.view.View;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ut.wb.ui.metro.MetroItemEntity;
import com.ut.wb.ui.metro.MetroLayout;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.HomeApi;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/12.
 */
final class RecommendModule extends BaseModule {
  public RecommendModule(Context context) {
    super(context);
  }

  private final Gson mMetroItemEntity = new GsonBuilder().registerTypeAdapter(
      new TypeToken<NetListContainerEntity<MetroItemEntity>>() {
      }.getType(), new BasicDeserializer<NetListContainerEntity<MetroItemEntity>>()).create();

  /**
   * 获取首页推荐数据
   */
  public void getRecommendData(int page) {
    mNetManager.request(HomeApi.class, mMetroItemEntity)
        .getRecommend(page)
        .compose(new HttpCallback<NetListContainerEntity<MetroItemEntity>>() {
          @Override public void onResponse(NetListContainerEntity<MetroItemEntity> response) {
            callback(RecommendFragment.GET_RECOMMEND_DATA, response.list);
          }
        });
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////

  List<MetroItemEntity> createMainData() {
    List<MetroItemEntity> list = new ArrayList<>();
    list.add(new MetroItemEntity(0, MetroLayout.NORMAL_MAX_VERTICAL));
    list.add(new MetroItemEntity(0, MetroLayout.NORMAL_HORIZONTAL));
    list.add(new MetroItemEntity(0, MetroLayout.NORMAL_SQUARE));
    list.add(new MetroItemEntity(1, MetroLayout.NORMAL_SQUARE));
    list.add(new MetroItemEntity(1, MetroLayout.NORMAL_SQUARE));
    list.add(new MetroItemEntity(1, MetroLayout.NORMAL_SQUARE));
    return list;
  }

  List<MetroItemEntity> createSecondData() {
    List<MetroItemEntity> list = new ArrayList<>();
    list.add(new MetroItemEntity(0, MetroLayout.NORMAL_SQUARE));
    list.add(new MetroItemEntity(0, MetroLayout.NORMAL_SQUARE));
    list.add(new MetroItemEntity(0, MetroLayout.NORMAL_HORIZONTAL));
    list.add(new MetroItemEntity(0, MetroLayout.NORMAL_SQUARE));
    list.add(new MetroItemEntity(1, MetroLayout.NORMAL_SQUARE));
    list.add(new MetroItemEntity(1, MetroLayout.NORMAL_SQUARE));
    list.add(new MetroItemEntity(1, MetroLayout.NORMAL_SQUARE));
    list.add(new MetroItemEntity(1, MetroLayout.NORMAL_SQUARE));
    list.add(new MetroItemEntity(1, MetroLayout.NORMAL_SQUARE));
    return list;
  }

  /**
   * 防止放大动画得时候被遮住   备注：jesse
   */
  void preventCover(View v) {
    View parent = (View) v.getParent();
    v.bringToFront();
    parent.requestLayout();
    parent.invalidate();
  }
}
