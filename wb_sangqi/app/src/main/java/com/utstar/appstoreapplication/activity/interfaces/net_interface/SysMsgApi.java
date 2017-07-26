package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.SysMessageEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SysMessageListEntity;
import java.util.Map;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * 站内信接口
 * Created by lt on 2016/12/15.
 */

public interface SysMsgApi {
  /**
   * 获取站内信All
   */
  @POST("activity/sysMsg.do") Observable<SysMessageListEntity> getSysMsgList(
      @QueryMap Map<String, String> params);

  /**
   * 查看详细制定信息
   */
  @POST("activity/readMsg.do") Observable<SysMessageEntity> getSysMsgDetail(
      @QueryMap Map<String, String> params);
}











