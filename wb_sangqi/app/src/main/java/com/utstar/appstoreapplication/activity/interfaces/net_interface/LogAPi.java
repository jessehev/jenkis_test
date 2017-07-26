package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.commons.constants.CommonConstant;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetObjectEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.ActionEntity;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Aria.Lao on 2016/12/26.
 * 日志接口
 */
public interface LogAPi {

  /**
   * 上报日志接口
   *
   * @param type {@link CommonConstant#ACTION_DOWNLOAD}、{@link CommonConstant#ACTION_INSTALL}、{@link
   * CommonConstant#ACTION_UNINSTALL}、{@link CommonConstant#ACTION_UPDATE}
   */
  @POST("mine/action.do") Observable<NetObjectEntity<ActionEntity>> apkAction(
      @Query("productid") int gameId, @Query("operatetype") int type);
}
