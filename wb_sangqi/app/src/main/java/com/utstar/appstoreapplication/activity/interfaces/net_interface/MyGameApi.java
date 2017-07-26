package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.MyGameListEntity;
import java.util.Map;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by lt on 2016/12/26.
 */

public interface MyGameApi {
  @POST("mine/getAllProductList.do") Observable<MyGameListEntity> getMyGameList(
      @QueryMap Map<String, String> params);
}
