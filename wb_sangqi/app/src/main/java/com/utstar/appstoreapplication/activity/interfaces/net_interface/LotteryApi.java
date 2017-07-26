package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.AdverEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.AwardListEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.DrawAwardEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.LotteryEntity;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by JesseHev on 2017/1/13.
 * 抽奖活动api
 */

public interface LotteryApi {
  /**
   * 获取配置的界面ui图
   */
  @POST("luckydraw/getAllUrlList.do") Observable<LotteryEntity> getLotteryImgList();
  /**
   * 中奖用户信息
   */
  @POST("luckydraw/getAllUserAwardsList.do")
  Observable<NetListContainerEntity<AdverEntity>> getLotteryUserList();
  /**
   * 奖品列表
   */
  @POST("luckydraw/getAllAwardsList.do")
  Observable<NetListContainerEntity<AwardListEntity>> getLotteryAwardList();
  /**
   * 抽奖
   */
  @POST("luckydraw/getUserZaDanInfo.do") Observable<DrawAwardEntity> drawAward();
}
