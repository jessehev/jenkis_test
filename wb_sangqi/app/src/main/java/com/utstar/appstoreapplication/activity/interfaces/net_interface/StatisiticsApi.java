package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;

/**
 * 方便与后台做统计功能
 * Created by lt on 2017/3/23.
 */

public interface StatisiticsApi {
  /**
   * 1.统计热门推荐和套餐包进游戏详情
   *
   * @param type 1 热门推荐进游戏详情 2 套餐包
   * @param vspId 渠道编号
   */
  @POST("intercept/transferparameter.do") Observable<String> statisiticIntoGameDetail(
      @Query("type") int type, @Query("gameId") int gameId, @Query("vspId") String vspId);

  /**
   * 2.统计热门推荐和套餐包点击购买
   *
   * @param type 1 热门推荐进游戏详情 2 套餐包
   * @param packageId 套餐包ID
   */
  @POST("intercept/transferparameter.do") Observable<String> statisiticBuy(@Query("type") int type,
      @Query("packageId") String packageId, @Query("gameId") int gameId);
}
