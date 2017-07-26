package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.NewEventListEntity;
import java.util.Map;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * 最新活动的接口
 * Created by lt on 2016/12/20.
 */
public interface NewEventApi {
  @POST("activity/getAllActivity.do") Observable<NewEventListEntity> getNewEvents(
      @QueryMap Map<String, String> param);
}
