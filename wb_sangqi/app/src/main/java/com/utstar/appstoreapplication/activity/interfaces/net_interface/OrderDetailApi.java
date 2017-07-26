package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.common_entity.NetObjectEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.AuthEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.OrderInfoEntity;
import java.util.Map;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by lt on 2017/1/10.
 */
public interface OrderDetailApi {
  /**
   * 订单信息
   */
  @GET("shop/shopOrderList.do") Observable<OrderInfoEntity> getOrderDetail(
      @QueryMap Map<String, String> params);

  /**
   * 获取购买跳转链接（中兴）
   *
   * @param continueType 0 ==> 订购，1==>订购并且自动续订；现在默认为0
   * @param type type == 0 为 产品ID，type ==1 为游戏ID；
   * @param thirdCode 2 ==> 第三方支付
   */
  @GET("shop/reChange.do") Observable<NetObjectEntity<String>> getOrderUrl(@Query("type") int type,
      @Query("userid") String userId, @Query("productId") String productId,
      @Query("continueType") int continueType, @Query("gameId") int gameId,
      @Query("thirdCode") int thirdCode);

  /**
   * 单款游戏鉴权
   *
   * @param gameId 游戏id
   */
  @GET("shop/ID.do") Observable<NetObjectEntity<AuthEntity>> auth(@Query("ID") int gameId);
}
