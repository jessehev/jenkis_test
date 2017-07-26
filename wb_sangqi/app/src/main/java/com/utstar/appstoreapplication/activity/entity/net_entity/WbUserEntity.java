package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aria.Lao on 2016/12/9.
 * 玩吧用户实体
 */
public class WbUserEntity {
  @SerializedName("userid") public int userId;
  public String account;
  public String phone;
  public String weixin;
  @SerializedName("epg_userid") public String epgUserId;
}
