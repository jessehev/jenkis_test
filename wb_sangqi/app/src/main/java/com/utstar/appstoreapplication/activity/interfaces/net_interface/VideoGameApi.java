package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.SysMessageEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.VideoGameEntity;
import java.util.Map;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Aria.Lao on 2017/6/20.
 */

public interface VideoGameApi {

  /**
   * 查看详细制定信息
   */
  @POST("shop/gameVideoList.do") Observable<VideoGameEntity> getVideoGameDetail(
      @QueryMap Map<String, String> params);
}
