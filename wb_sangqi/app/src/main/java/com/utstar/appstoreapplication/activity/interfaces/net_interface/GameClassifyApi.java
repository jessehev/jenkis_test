package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.GameClassifyEntity;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameSizeEntity;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by JesseHev on 2016/12/22.
 */

public interface GameClassifyApi {

  /**
   * 根据mode获取分类游戏
   * type = 1; // 获取手柄游戏
   * type = 2;  // 获取遥控器游戏
   */
  @POST("search/getProductListByMode.do")
  Observable<NetListContainerEntity<GameClassifyEntity>> getGameListByMode(@Query("page") int page,
      @Query("number") int number, @Query("mode") int mode);

  /**
   * /* * 根据typeId获取分类游戏
   */
  @POST("search/getProductListByTagType.do")
  Observable<NetListContainerEntity<GameClassifyEntity>> getGameListByTag(@Query("page") int page,
      @Query("number") int number, @Query("typeid") int typeId);

  /**
   * 根据mode、typeId获取分类游戏
   */
  @POST("search/getProductListByModeAndType.do")
  Observable<NetListContainerEntity<GameClassifyEntity.GameClassifySubEntity>> getGameListByModeAndType(
      @Query("type") int type, @Query("page") int page, @Query("number") int number,
      @Query("typeid") int typeId, @Query("mode") int mode);

  @POST("search/getTotalSizeByMode.do") Observable<GameSizeEntity> getGameSizeByMode(
      @Query("mode") int mode, @Query("typeid") int typeId);

  @POST("search/getTotalSizeByType.do") Observable<GameSizeEntity> getGameSizeByType(
      @Query("typeid") int typeId, @Query("mode") int mode);
}
