package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameVideoEntity;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2017-03-02.
 */

public interface GameVideoApi {
  /**
   * 获取视频列表数据
   */
  //@POST("GameVideo/getAllVideoList.do")
  //Observable<NetListContainerEntity<GameVideoEntity>> getGameVideoList(@Query("number") int number,
  //    @Query("page") int page);
  @POST("GameVideo/getAllVideoList.do")
  Observable<NetListContainerEntity<GameVideoEntity>> getGameVideoList();
}
