package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.common_entity.NetObjectEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.AdEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.AnnouncementEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.AutoSignEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.EPGParamsEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.EPGWbLocationEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.LauncherImgEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SignEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SignHintEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.UpdateVersionEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.WbUserEntity;
import java.util.Map;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Aria.Lao on 2016/12/8.
 * 启动图接口
 */
public interface LauncherApi {
  /**
   * 启动图
   */
  @GET("mine/loginUrl.do") Observable<LauncherImgEntity> getLauncherImg();

  /**
   * 登录
   */
  @POST("mine/login.do") Observable<WbUserEntity> login(@QueryMap Map<String, String> params);

  /**
   * 注册
   */
  @POST("mine/addAccount.do") Observable<WbUserEntity> reg(@QueryMap Map<String, String> params);

  /**
   * 检查升级
   */
  @POST("wbupdate/update.do") Observable<UpdateVersionEntity> checkUpdate(
      @Query("apkvcode") int versionCode, @Query("zipvcode") int hotVersionCode);

  /**
   * 获取公告内容
   */
  @POST("announcement/announcementList.do") Observable<AnnouncementEntity> getAnnouncement();

  /**
   * 获取广告列表
   */

  @POST("advertise/advertiseList.do") Observable<AdEntity> getAdList();

  /**
   * 签到
   */
  @POST("mine/userSign.do") Observable<AutoSignEntity> sign();

  /**
   * 获取签到奖品列表
   */
  @POST("activity/getAwardList.do") Observable<SignEntity> getAwardList(@Query("type") String type);

  /**
   * 获取签到奖励领奖信息
   */
  @POST("luckydraw/getUserSignList.do") Observable<SignHintEntity> getSignInfo(
      @Query("type") int type);

  /**
   * 提交中奖用户信息
   */
  @POST("luckydraw/getSignAwardByApp.do") Observable<SignHintEntity> commitInfo(
      @Query("id") String name, @Query("number") String number, @Query("type") int type);

  /**
   * 获取Epg_user_id
   */
  @POST("mine/getEpgUserId.do") Observable<EPGParamsEntity> getEpgUserParams(
      @Query("mac") String mac);

  /**
   * 获取EPG 玩吧推荐位
   */
  @POST("mine/getProductIdByEpgUserId.do") Observable<EPGWbLocationEntity> getEpgWbLocation(
      @Query("epg_userid") String epgUserId);

  /**
   * 上传当前用户剩余内存
   */
  @POST("sysManager/getMemorySize.do") Observable<NetObjectEntity<String>> uploadCap(
      @Query("memorySize") String currentCap);
}
