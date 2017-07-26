package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.ActivityEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.DayTaskEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.HalfPackageEntity;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2017-03-06.
 *
 * 精品专区活动配置 接口
 */

public interface ActivityApi {

  /**
   * 套餐包关联活动
   */
  @POST("activityPackage/getActivityPackageList.do") Observable<ActivityEntity> popActivity(
      @Query("productId") String packageId);

  /**
   * 区分活动是属于平台还是套餐包
   */
  @POST("mine/getActivityByUserId.do") Observable<String> diffActivity();

  /**
   * 套餐包半价优惠活动
   */
  @POST("pkg/timelimit/getpreferencelist.do") Observable<HalfPackageEntity> getPackageData();

  /**
   * 每日任务 领取奖励
   *
   * @param awardId 奖品id
   * @param taskPosition 任务编号
   */
  @POST("daily/tasks/receivePrizes.do") Observable<DayTaskEntity> getDayTaskAwards(
      @Query("fkAwardId") int awardId, @Query("taskPosition") int taskPosition);

  /**
   * 获取每日任务信息
   *
   * @param taskPosition 任务编号
   */
  @POST("daily/tasks/getDailyTasks.do") Observable<DayTaskEntity> getDayTaskInfo(
      @Query("taskPosition") int taskPosition);

  /**
   * 处理每日任务
   *
   * @param spId 渠道id
   * @param packageName 游戏包名
   * @param taskType 任务类型
   * @param taskValue 触发条件
   */
  @POST("daily/tasks/taskHandle.do") Observable<DayTaskEntity> handlerDayTask(
      @Query("spId") String spId, @Query("gamePkgName") String packageName,
      @Query("taskType") String taskType, @Query("taskValue") String taskValue);

  /**
   * 保存中奖用户信息
   *
   * @param awardId 奖品id
   * @param phone 手机号
   * @param taskPosition 任务编号
   */
  @POST("daily/tasks/saveWinInfo.do") Observable<DayTaskEntity> saveWinInfo(
      @Query("fkAwardId") int awardId, @Query("mobilePhone") String phone,
      @Query("taskPosition") int taskPosition);

  /**
   * 破冰活动
   */
  @POST("icebreaking/getPackageList.do") Observable<HalfPackageEntity> getIceBreakData();
}
