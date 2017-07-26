package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017-03-02.
 */

public class GameVideoEntity {
  @SerializedName("img") public String imgUrl; //图片地址
  public String name;//专区名字
  public String address; //视频地址
}
