package com.utstar.appstoreapplication.activity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aria.Lao on 2017/4/13.
 * 每日任务实体
 */

public class DayTaskEntity implements Parcelable {
  /**
   * 任务类型
   */
  public String taskType;
  /**
   * 任务触发条件
   */
  public String taskCondition;
  /**
   * 游戏ID
   */
  public String gameId;
  /**
   * 渠道ID
   */
  public String spId;
  /**
   * 完成时间
   */
  public long completeTime;

  @Override public String toString() {
    return "DayTaskEntity{"
        + "taskType='"
        + taskType
        + '\''
        + ", taskCondition='"
        + taskCondition
        + '\''
        + ", gameId='"
        + gameId
        + '\''
        + ", spId='"
        + spId
        + '\''
        + ", completeTime="
        + completeTime
        + '}';
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.taskType);
    dest.writeString(this.taskCondition);
    dest.writeString(this.gameId);
    dest.writeString(this.spId);
    dest.writeLong(this.completeTime);
  }

  public void readFromParcel(Parcel dest) {
    taskType = dest.readString();
    taskCondition = dest.readString();
    gameId = dest.readString();
    spId = dest.readString();
    completeTime = dest.readLong();
  }

  public DayTaskEntity() {
  }

  protected DayTaskEntity(Parcel in) {
    this.taskType = in.readString();
    this.taskCondition = in.readString();
    this.gameId = in.readString();
    this.spId = in.readString();
    this.completeTime = in.readLong();
  }

  public static final Parcelable.Creator<DayTaskEntity> CREATOR =
      new Parcelable.Creator<DayTaskEntity>() {
        @Override public DayTaskEntity createFromParcel(Parcel source) {
          return new DayTaskEntity(source);
        }

        @Override public DayTaskEntity[] newArray(int size) {
          return new DayTaskEntity[size];
        }
      };
}
