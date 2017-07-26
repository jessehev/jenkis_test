package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.ProcessEntity;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Aria.Lao on 2017/1/20.
 */

public interface ProbeApi {

  /**
   * 获取事件id
   */
  @GET("log/login.do") Observable<ProcessEntity> getEventId(@Query("packageName") String pkgName,
      @Query("userid") String userId, @Query("startTime") String startTime);

  /**
   * 获取用户id
   */
  @GET("mine/login.do") Observable<ProcessEntity> getUserId(@Query("account") String account);

  /**
   * 结束统计
   */
  @GET("log/logout.do") Observable<ProcessEntity> stop(@Query("id") String id,
      @Query("endTime") String endTime);

  /**
   * 获取试玩情况
   */

  @GET("") Observable<ProcessEntity> getState(@Query("packageName") String packageName);
}
