package com.utstar.appstoreapplication.activity.interfaces.net_interface;

import com.utstar.appstoreapplication.activity.entity.net_entity.UpdateVersionEntity;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Aria.Lao on 2016/12/20.
 * 通用接口
 */
public interface CommonApi {
  /**
   * 检查升级
   */
  @POST("wbupdate/update.do") Observable<UpdateVersionEntity> update(
      @Query("versioncode") int versionCode);
}
