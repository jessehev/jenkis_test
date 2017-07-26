package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.ModuleBgEntity;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by lt on 2017/3/6.
 */

public interface EachModuleBgApi {
  //1.配置背景图   热门推荐type-1   分页编号pageNo-1
  @POST("bgmap/getareabgmap.do") Observable<ModuleBgEntity> getModuleBg(@Query("type") int type,
      @Query("pageNo") int pageNO);

  //2.配置背景图 精品专区packageId=XXX
  @POST("bgmap/getpackagebgmap.do") Observable<ModuleBgEntity> getPackageBg(
      @Query("packageId") String packageId);

  //3.配置背景图 游戏大厅tagId=XX
  @POST("bgmap/gettagbgmap.do") Observable<ModuleBgEntity> getGameHall(@Query("tagId") int tagId);

  //4.配置背景图 游戏大厅左侧导航栏
  @POST("bgmap/getleftnavbgmap.do") Observable<ModuleBgEntity> getGameHallLeftBg(
      @Query("type") int type, @Query("pageNo") int pageNo);
}
