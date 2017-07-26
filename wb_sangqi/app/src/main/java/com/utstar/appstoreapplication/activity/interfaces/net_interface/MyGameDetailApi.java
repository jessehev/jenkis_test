package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.MyGameDetailEntity;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import java.util.Map;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Aria.Lao on 2016/12/27.
 * 游戏详情APi
 */
public interface MyGameDetailApi {
  /**
   * 我的游戏
   */
  @POST("common/getProductListByTypeAndId.do")
  Observable<NetListContainerEntity<MyGameDetailEntity>> getMyGame(
      @QueryMap Map<String, String> params);
}
