package com.utstar.appstoreapplication.activity.commons.constants;

import com.utstar.appstoreapplication.activity.BuildConfig;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import com.utstar.appstoreapplication.activity.utils.FilePathUtils;

/**
 * Created by “Aria.Lao” on 2016/10/25.
 * 公用常量
 */
public interface CommonConstant {

  /**
   * 跳转游戏详情
   */
  int TURN_GAME_DETAIL = 5;

  /**
   * 跳转活动
   */
  int TURN_GAME_ACTIVITY = 4;

  /**
   * 跳转订单界面
   */
  int TURN_ORDER_ACTIVITY = 3;


  /**
   * 空余内存判断条件
   */
  //int SPACE_MEMORY = BuildConfig.DEBUG ? Integer.MAX_VALUE : 500;
  int SPACE_MEMORY = 200;
  //int SPACE_MEMORY = 500;
  //int SPACE_MEMORY = Integer.MAX_VALUE;
  // int SPACE_MEMORY = 1;  //暂时屏蔽

  //int SPACE_MEMORY = 1;  //暂时屏蔽
  /**
   * 测试按钮
   */
  boolean IS_TEST = false;

  /**
   * 订购类型
   */
  String ORDER_TYPE_KEY = "ORDER_TYPE_KEY";

  /**
   * 订购的产品ID
   */
  String ORDER_ID_KEY = "ORDER_ID_KEY";

  /**
   * cp服务ID
   */
  String CP_SERVICE_ID = "CP_SERVICE_ID";

  /**
   * cpContent ID
   */
  String CP_CONTENT_ID = "CP_CONTENT_ID";

  /**
   * cpSPDI
   */
  String CP_SP_ID = "CP_SP_ID";

  /**
   * cp 包名
   */
  String CP_PACKAGE_NAME = "CP_PACKAGE_NAME";

  /**
   * cp提供的流水号
   */
  String CP_FLOW_CODE = "CP_FLOW_CODE";

  /**
   * 第三方支付服务Action
   */
  String THIRD_PAY_SERVICE_ACTION = "com.utstar.appstoreapplication.activity.thirdPay";

  /**
   * 日志ACTION
   */
  int ACTION_DOWNLOAD = 2;
  int ACTION_INSTALL = 3;
  int ACTION_UNINSTALL = 4;
  int ACTION_UPDATE = 7;

  /**
   * 最新活动typeId关联type
   * -1-图片   1-游戏 2-套餐包 3-活动 4-签到 5-每日任务
   */
  int TYPE_PIC = -1;
  int TYPE_GAME = 1;
  int TYPE_PACKAGE = 2;
  int TYPE_ACTIVITY = 3;
  int TYPE_SIGN = 4;
  int TYPE_TASK = 5;

  String UPGRADE_PACKAGE_NAME = "com.utstar.upgrade";

  /**
   * key
   */
  String selecttypeid = "selecttypeid";
  String id = "id";
  String typeName = "typeName";
  /**
   * selecttypeid 对应的各个模块的value
   */
  int C_iLineTab_Default = 0;
  int C_iLineTab_Home = 1;
  int C_iLineTab_Category = 2;

  int C_iLineTab_Ranking = 3;
  int C_iLineTab_MyGame = 4;
  int C_iLineTab_Search = 5;
  int C_iLineTab_Account = 6;
  int C_iLineTab_Buy = 7;
  int C_iLineTab_HuoDong = 8;
  int C_iLineTab_Sysmsg = 9;

  /**
   * 重启玩吧工具的包名
   */
  String RESTART_WANBA_UTIL_PKG = "com.ut_star.restartwanba";
  /**
   * 重启玩吧工具启动action
   */
  String RESTART_WANBA_UTIL_ACTION = "com.utstar.appstoreapplication.activity.restart_wanba";

  /**
   * 中兴支付
   */
  int WB_PAY_TYPE_ZTE = 0xaa190;

  /**
   * 玩吧支付
   */
  int WB_PAY_TYPE_WB = 0xaa191;
}
