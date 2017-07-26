package com.utstar.appstoreapplication.activity.manager;

import com.arialyy.frame.util.SharePreUtil;
import com.utstar.appstoreapplication.activity.commons.constants.KeyConstant;
import com.utstar.appstoreapplication.activity.entity.db_entity.EpgEntity;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;

/**
 * Created by “Aria.Lao” on 2016/10/26.
 * 用户管理器，处理和用户相关的东西
 */
public class UserManager extends CommonManager implements KeyConstant {
  private static final Object LOCK = new Object();
  private static volatile UserManager INSTANCE = null;

  public static UserManager getInstance() {
    if (INSTANCE == null) {
      synchronized (LOCK) {
        INSTANCE = new UserManager();
      }
    }
    return INSTANCE;
  }

  @Override void init() {

  }

  /**
   * 存储用户实体
   */
  public void saveUser(EpgEntity userEntity) {
    SharePreUtil.putObject(PRE_NAME, BaseApp.context, KEY_USER_ENTITY, EpgEntity.class, userEntity);
  }

  /**
   * 获取用户实体
   */
  public EpgEntity getUser() {
    return SharePreUtil.getObject(PRE_NAME, BaseApp.context, KEY_USER_ENTITY, EpgEntity.class);
  }

  @Override String initName() {
    return "用户管理器";
  }

  @Override void onDestroy() {

  }
}
