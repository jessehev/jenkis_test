package com.ut.wb.ui.metro;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by Aria.Lao on 2016/12/12.
 */
public class MetroItemEntity implements Serializable {
  /**
   * 跳转游戏
   */
  public static final int TURN_GAME             = 1;
  /**
   * 跳转套餐包
   */
  public static final int TURN_PACKAGE          = 2;
  /**
   * 跳转活动
   */
  public static final int TURN_ACTIVITY         = 3;
  /**
   * 跳转视频
   */
  public static final int TURN_VIDEO            = 4;
  /**
   * 其他
   */
  public static final int TURN_OTHER            = 5;
  /**
   * 签到
   */
  public static final int TURN_SIGN             = 6;
  /**
   * 砸蛋
   */
  public static final int TURN_ZA_DAN           = 7;
  /**
   * 跳转CP视频
   */
  public static final int TURN_CP_VIDEO         = 8;
  /**
   *
   * 跳转套餐包关联活动
   */
  public static final int TURN_PACKAGE_ACTIVITY = 9;

  /**
   * 跳转游戏大厅tag二级界面
   */
  public static final int TURN_GAME_HALL_TAG      = 98;
  /**
   * 跳转游戏大厅手柄游戏
   */
  public static final int TURN_GAME_HALL_MODE_SB  = 99;
  /**
   * 跳转游戏大厅遥控器游戏
   */
  public static final int TURN_GAME_HALL_MODE_YKQ = 100;

  /**
   * 视博云跳转
   */
  public static final int TURN_SBY = 1001;

  /**
   * 视博云单款游戏跳转
   */
  public static final int TURN_SBY_GAME = 1002;
  public int type;
  int row;  //所在的行
  public                                   NormalItemData normalItemData;
  @SerializedName("SimpleItemData") public SimpleItemData simpleItemData;
  /**
   * 1：游戏、2：套餐包、3：活动、4：视频、5：暂无产品
   */
  public                                   int            turnType;

  public static class MetroBaseData implements Serializable {
    @SerializedName("selecttypeid") public int     selectTypeId;
    public                                 String  id;
    public                                 String  img;    //背景图
    public                                 String  title; //标题
    public                                 boolean isBuy; //是否购买
    public                                 boolean isAddPage; //是否加入套餐包
    /**
     * 0：免费、1：普通、2：内购、3：收费、4：限时免费、5：、6:、7：砸蛋
     */
    public int tag    = -1;       //标签
    /**
     * 0：热门、1：新增、2：普通
     */
    public int hotTag = -1;    //左上角标签
    public String videoUrl;   //视频链接
  }

  /**
   * 简单的metro 数据结构
   */
  public static class SimpleItemData extends MetroBaseData implements Serializable {
    public int resId = -1;
    @SerializedName("floorimg") public String floorImg; //底部图片
  }

  /**
   * 普通的metro数据结构(推荐页)
   */
  public static class NormalItemData extends MetroBaseData implements Serializable {
    public String  area;  //专区名
    public int     num;// 下载数
    public boolean isShowSb; //显示手柄
    public boolean isShowYkq; //显示遥控器
    public boolean isShowPlay;  //显示播放按钮
  }

  public MetroItemEntity(int row, int type) {
    this.row = row;
    this.type = type;
  }
}
