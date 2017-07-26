package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JesseHev on 2017/1/16.
 * 抽奖
 */

public class DrawAwardEntity {
  public int id;
  public int num; //抽奖剩余次数
  @SerializedName("typename") public String typeName;//中奖等级
  public String award;//奖品名字
  // code = 1：砸中有奖    0：砸中没奖    -1 ：次数为0   没有机会了
  public String code;
  @SerializedName("winninginfo") public String winningInfo;
  public String img;
}
