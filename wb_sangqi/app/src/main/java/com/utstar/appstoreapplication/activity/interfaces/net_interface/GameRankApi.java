package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.GameRankEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameRankMoreEntity;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by JesseHev on 2016/12/14.
 */

public interface GameRankApi {

  /**
   * 获取游戏排行榜单
   */
  @POST("rank/getAllProductList.do") Observable<NetListContainerEntity<GameRankEntity>> getRankData(
      @Query("number") int number, @Query("page") int page);

  /**
   * 根据selectId获取游戏排行列表（更多）
   */

  @POST("common/getProductListByTypeAndId.do")
  Observable<NetListContainerEntity<GameRankMoreEntity>> getGameListBySelectId(@Query("id") int id,
      @Query("selecttypeid") int selecttypeid, @Query("number") int number,
      @Query("page") int page);
}
