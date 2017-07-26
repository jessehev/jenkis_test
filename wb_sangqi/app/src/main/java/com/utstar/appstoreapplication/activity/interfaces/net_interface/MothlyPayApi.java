package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.MothlyPaySecondEntity;
import java.util.Map;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by lt on 2016/12/23.
 */

public interface MothlyPayApi {
  @POST("shop/shopDetailedList.do") Observable<MothlyPaySecondEntity> getMothlyPaySecList(
      @QueryMap Map<String, String> params);
}
