package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameDetailEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameUninstallEntity;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by JesseHev on 2016/12/13.
 */
public interface GameManagerApi {

  /**
   * 获取游戏详情
   */
  @GET("common/getProductDetailInfo.do") Observable<GameDetailEntity> getGameDetail(
      @Query("productId") int productId, @Query("type") int type);

  /**
   * 游戏点赞
   */
  @GET("common/praise.do") Observable<Object> praise(@Query("productid") int productId,
      @Query("praise") int praiseType);

  /**
   * 卸载游戏
   */
  @GET("cancelGame/getCancelGameList.do")
  Observable<NetListContainerEntity<GameUninstallEntity>> getUninstallGame();
}
