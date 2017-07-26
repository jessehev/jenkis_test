package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.SbyEntity;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by messi.mo on 2017/4/13.
 */
public interface SbyPackageApi {
  @POST("shop/getareaaccess.do") Observable<SbyEntity> getSbyInfo(@Query("id") String id);
}
