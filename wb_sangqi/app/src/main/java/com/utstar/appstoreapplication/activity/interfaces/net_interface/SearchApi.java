package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.SearchEntity;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Aria.Lao on 2016/12/20.
 * 搜索
 */
public interface SearchApi {
  /**
   * 猜你喜欢
   */
  @POST("search/getRecommendProductList.do") Observable<SearchEntity> getRecommendGame(
      @Query("page") int page, @Query("number") int num);

  /**
   * 搜索
   */
  @POST("search/getProductListByKey.do") Observable<SearchEntity> search(@Query("key") String key,
      @Query("page") int page, @Query("number") int num);
}
