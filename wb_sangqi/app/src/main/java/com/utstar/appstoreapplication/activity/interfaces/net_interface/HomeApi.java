package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.ut.wb.ui.metro.MetroItemEntity;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Aria.Lao on 2016/12/21.
 * 首页API
 */
public interface HomeApi {
  /**
   * 首页推荐
   */
  @POST("recommend/getHomeNewPpts.do")
  Observable<NetListContainerEntity<MetroItemEntity>> getRecommend(@Query("page") int page);

  /**
   * 游戏大厅
   */
  @POST("lobby/getLobbyAllProductList.do")
  Observable<NetListContainerEntity<MetroItemEntity>> getGameHall(@Query("page") int page);

  /**
   * 包月专区
   */
  @POST("shop/shopHomeList.do") Observable<NetListContainerEntity<MetroItemEntity>> getPackageArea(
      @Query("page") int page);
}
