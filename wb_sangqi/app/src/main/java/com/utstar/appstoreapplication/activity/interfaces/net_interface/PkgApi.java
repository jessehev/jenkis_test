package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.common_entity.NetObjectEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.PackageEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.PkgInfo;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 套餐包接口
 */
public interface PkgApi {
  @GET("shop/shopList.do") Observable<NetObjectEntity<PackageEntity>> getPackage(
      @Query("USERID") String userId, @Query("number") int num, @Query("page") int page);

  @GET("shop/shopDetailedList.do") Observable<PkgInfo> getPkgContent(
      @Query("PRODUCTID") String productId, @Query("number") int num, @Query("page") int page);
}
