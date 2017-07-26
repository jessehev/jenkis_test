package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JesseHev on 2017/1/16.
 * 奖品列表
 */

public class AwardListEntity {

  @SerializedName("tubiao") public String imgUrl;
  @SerializedName("typeid") public String rank;
  @SerializedName("award") public String name;
}
