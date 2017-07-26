package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lyy on 2016/9/9.
 * 鉴权实体
 */
public class AuthEntity {
  @SerializedName("error_code") public String errorCode;
  public String status;

  @Override public String toString() {
    return "AuthEntity{" + "errorCode='" + errorCode + '\'' + ", status='" + status + '\'' + '}';
  }
}
