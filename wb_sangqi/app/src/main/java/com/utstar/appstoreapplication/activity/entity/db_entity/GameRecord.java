package com.utstar.appstoreapplication.activity.entity.db_entity;

import com.utstar.appstoreapplication.activity.db.sdcard.RecordDbEntity;

/**
 * Created by Aria.Lao on 2017/5/31.
 * 游戏启动记录
 */
public class GameRecord extends RecordDbEntity {

  /**
   * 上一次启动时间
   */
  public long lastStartTime;
  /**
   * 安装时间
   */
  public long installTime;
  public String packageName;
  public String gameName;
  public String gameIcon;
  public String versionName;
  public int versionCode;
  /**
   * 启动次数
   */
  public int startNum;

  @Override public String toString() {
    return "GameRecord{"
        + "lastStartTime="
        + lastStartTime
        + ", installTime="
        + installTime
        + ", packageName='"
        + packageName
        + '\''
        + ", gameName='"
        + gameName
        + '\''
        + ", gameIcon='"
        + gameIcon
        + '\''
        + ", versionName='"
        + versionName
        + '\''
        + ", versionCode="
        + versionCode
        + ", startNum="
        + startNum
        + '}';
  }
}