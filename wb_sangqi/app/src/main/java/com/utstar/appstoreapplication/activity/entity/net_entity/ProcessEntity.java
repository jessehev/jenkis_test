package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lyy on 2017/1/22.
 */
public class ProcessEntity {
  private String packageName;
  @SerializedName("id") private String eventId;
  @SerializedName("userid") private String userId;
  private long playTime;
  private int playNum;
  private int type;

  public long getPlayTime() {
    return playTime;
  }

  public void setPlayTime(long playTime) {
    this.playTime = playTime;
  }

  public int getPlayNum() {
    return playNum;
  }

  public void setPlayNum(int playNum) {
    this.playNum = playNum;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
