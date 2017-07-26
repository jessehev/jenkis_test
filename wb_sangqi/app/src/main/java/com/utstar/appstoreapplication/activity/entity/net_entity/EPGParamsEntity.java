package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;

/**
 * 从服务器读取的EPG参数
 */
public class EPGParamsEntity {
  public String EPG_SESSION_ID = "";
  @SerializedName("epg_userid") public String EPG_USER_ID = "";
  @SerializedName("areaid") public String EPG_AREA_ID = "";
  public String EPG_SERVER = "http://192.168.0.12:33200/EPG/jsp/gdgaoqing/en/Category.jsp";
}