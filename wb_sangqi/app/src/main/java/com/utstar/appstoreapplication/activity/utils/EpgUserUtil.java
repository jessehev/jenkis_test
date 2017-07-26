package com.utstar.appstoreapplication.activity.utils;

import com.arialyy.frame.util.show.L;
import com.utstar.appstoreapplication.activity.entity.db_entity.EpgEntity;
import org.litepal.crud.DataSupport;

/**
 * Created by Lyy on 2016/9/27.
 * 用户实体帮助类
 */
public class EpgUserUtil {
  private static EpgEntity USER_ENTITY = null;

  /**
   * 获取EPG实体
   */
  public static synchronized EpgEntity getUserEntity() {
    if (USER_ENTITY == null) {
      L.w("EPG 参数为null，即将从缓存读取EPG参数");
      USER_ENTITY = EpgEntity.findFirst(EpgEntity.class);
      if (USER_ENTITY == null) {
        L.w("数据库没有用户实体，准备创建用户实体");
        USER_ENTITY = new EpgEntity();
        USER_ENTITY.save();
      }
    }
    return USER_ENTITY;
  }

  /**
   * 删除用户实体
   */
  public static synchronized void removeUserEntity() {
    DataSupport.deleteAll(EpgEntity.class);
  }

  /**
   * 存储EPG参数
   */
  public static synchronized void saveUserEntity(EpgEntity userEntity) {
    if (USER_ENTITY == null) {
      USER_ENTITY = getUserEntity();
    }
    USER_ENTITY.setWbAccount(userEntity.getWbAccount());
    USER_ENTITY.setEpgAreAId(userEntity.getEpgAreAId());
    USER_ENTITY.setEpgUserId(userEntity.getEpgUserId());
    USER_ENTITY.setEpgSessionId(userEntity.getEpgSessionId());
    USER_ENTITY.setEpgServer(userEntity.getEpgServer());
    USER_ENTITY.setChannel(userEntity.getChannel());
    USER_ENTITY.updateAll();
  }
}
