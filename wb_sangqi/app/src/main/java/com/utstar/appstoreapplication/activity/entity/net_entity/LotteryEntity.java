package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by JesseHev on 2017/1/13.
 */

public class LotteryEntity implements Serializable {

  public String rule;
  public ArrayList<LotterImageEntity> list;
  //每天的抽奖次数
  @SerializedName("everydayrecords") public String everyDayRecords;

  public static class LotterImageEntity implements Serializable {

    public String type;
    public String address;
  }
}
