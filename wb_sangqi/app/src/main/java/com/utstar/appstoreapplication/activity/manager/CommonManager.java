package com.utstar.appstoreapplication.activity.manager;

import com.arialyy.frame.util.show.FL;

/**
 * Created by “Aria.Lao” on 2016/10/25.
 * 基类管理器
 */
abstract class CommonManager {
  private String name;

  CommonManager() {
    name = initName();
    FL.isDebug = true;
    init();
  }

  /**
   * 初始化
   */
  abstract void init();

  /**
   * 初始化管理器名字
   */
  abstract String initName();

  /**
   * 注销管理器
   */
  abstract void onDestroy();

  public String getName() {
    return name;
  }
}
