package com.utstar.appstoreapplication.activity.entity.net_entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by JesseHev on 2017/4/13.
 * 每日任务实体
 */

public class DayTaskEntity implements Parcelable {

  public boolean isFinished; //任务是否完成

  public String taskId;
  public String dayTask;
  public String taskPty;
  public String sttime;
  public String taskType;
  public String gameId;
  public String remarks;
  public int totalTask;
  public String endTime;
  public String gameName;
  public String awardId;
  public String gameUrl;
  public String taskValue;
  public String dailyTaskName;
  public String taskDescribe;
  public boolean taskPositionStatus;//单个任务状态
  public boolean hasPrizes;//是否有奖
  public String promptMsg;//提示信息
  @SerializedName("taskPosition") public int currentTask; //当前任务进度
  @SerializedName("prizesData") public List<TaskSubEntity> awardList;

  public static class TaskSubEntity implements Parcelable {
    public String day;
    public boolean isCompleted;//当前任务是否已经完成
    public int progressStatus;

    public int taskId;
    public String awardUrl;
    public int awardId;
    public String status; //status -2 - 活动还未开始， -1 - 待领奖, 1 - 领取成功,0 - 奖品数量不足
    public String isLimit;
    public int taskPosition; //领取奖励需要达到的条件
    public String awardName;
    public String awardDesc;
    @SerializedName("unmsg") public String unMsg; //未中奖提示
    public String msg;//中奖提示

    @Override public String toString() {
      return "TaskSubEntity{"
          + "day='"
          + day
          + '\''
          + ", isCompleted="
          + isCompleted
          + ", taskId="
          + taskId
          + ", awardUrl='"
          + awardUrl
          + '\''
          + ", awardId="
          + awardId
          + ", status='"
          + status
          + '\''
          + ", isLimit='"
          + isLimit
          + '\''
          + ", taskPosition="
          + taskPosition
          + ", awardName='"
          + awardName
          + '\''
          + ", awardDesc='"
          + awardDesc
          + '\''
          + ", unMsg='"
          + unMsg
          + '\''
          + ", msg='"
          + msg
          + '\''
          + '}';
    }

    @Override public int describeContents() {
      return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.day);
      dest.writeByte(this.isCompleted ? (byte) 1 : (byte) 0);
      dest.writeInt(this.taskId);
      dest.writeString(this.awardUrl);
      dest.writeInt(this.awardId);
      dest.writeString(this.status);
      dest.writeString(this.isLimit);
      dest.writeInt(this.taskPosition);
      dest.writeString(this.awardName);
      dest.writeString(this.awardDesc);
      dest.writeString(this.unMsg);
      dest.writeString(this.msg);
    }

    public TaskSubEntity() {
    }

    protected TaskSubEntity(Parcel in) {
      this.day = in.readString();
      this.isCompleted = in.readByte() != 0;
      this.taskId = in.readInt();
      this.awardUrl = in.readString();
      this.awardId = in.readInt();
      this.status = in.readString();
      this.isLimit = in.readString();
      this.taskPosition = in.readInt();
      this.awardName = in.readString();
      this.awardDesc = in.readString();
      this.unMsg = in.readString();
      this.msg = in.readString();
    }

    public static final Creator<TaskSubEntity> CREATOR = new Creator<TaskSubEntity>() {
      @Override public TaskSubEntity createFromParcel(Parcel source) {
        return new TaskSubEntity(source);
      }

      @Override public TaskSubEntity[] newArray(int size) {
        return new TaskSubEntity[size];
      }
    };
  }

  @Override public String toString() {
    return "DayTaskEntity{"
        + "isFinished="
        + isFinished
        + ", taskId='"
        + taskId
        + '\''
        + ", dayTask='"
        + dayTask
        + '\''
        + ", taskPty='"
        + taskPty
        + '\''
        + ", sttime='"
        + sttime
        + '\''
        + ", taskType='"
        + taskType
        + '\''
        + ", gameId='"
        + gameId
        + '\''
        + ", remarks='"
        + remarks
        + '\''
        + ", totalTask="
        + totalTask
        + ", endTime='"
        + endTime
        + '\''
        + ", gameName='"
        + gameName
        + '\''
        + ", awardId='"
        + awardId
        + '\''
        + ", gameUrl='"
        + gameUrl
        + '\''
        + ", taskValue='"
        + taskValue
        + '\''
        + ", dailyTaskName='"
        + dailyTaskName
        + '\''
        + ", taskDescribe='"
        + taskDescribe
        + '\''
        + ", taskPositionStatus="
        + taskPositionStatus
        + ", hasPrizes="
        + hasPrizes
        + ", promptMsg='"
        + promptMsg
        + '\''
        + ", currentTask="
        + currentTask
        + ", awardList="
        + awardList
        + '}';
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeByte(this.isFinished ? (byte) 1 : (byte) 0);
    dest.writeString(this.taskId);
    dest.writeString(this.dayTask);
    dest.writeString(this.taskPty);
    dest.writeString(this.sttime);
    dest.writeString(this.taskType);
    dest.writeString(this.gameId);
    dest.writeString(this.remarks);
    dest.writeInt(this.totalTask);
    dest.writeString(this.endTime);
    dest.writeString(this.gameName);
    dest.writeString(this.awardId);
    dest.writeString(this.gameUrl);
    dest.writeString(this.taskValue);
    dest.writeString(this.dailyTaskName);
    dest.writeString(this.taskDescribe);
    dest.writeByte(this.taskPositionStatus ? (byte) 1 : (byte) 0);
    dest.writeByte(this.hasPrizes ? (byte) 1 : (byte) 0);
    dest.writeString(this.promptMsg);
    dest.writeInt(this.currentTask);
    dest.writeList(this.awardList);
  }

  public DayTaskEntity() {
  }

  protected DayTaskEntity(Parcel in) {
    this.isFinished = in.readByte() != 0;
    this.taskId = in.readString();
    this.dayTask = in.readString();
    this.taskPty = in.readString();
    this.sttime = in.readString();
    this.taskType = in.readString();
    this.gameId = in.readString();
    this.remarks = in.readString();
    this.totalTask = in.readInt();
    this.endTime = in.readString();
    this.gameName = in.readString();
    this.awardId = in.readString();
    this.gameUrl = in.readString();
    this.taskValue = in.readString();
    this.dailyTaskName = in.readString();
    this.taskDescribe = in.readString();
    this.taskPositionStatus = in.readByte() != 0;
    this.hasPrizes = in.readByte() != 0;
    this.promptMsg = in.readString();
    this.currentTask = in.readInt();
    this.awardList = new ArrayList<TaskSubEntity>();
    in.readList(this.awardList, TaskSubEntity.class.getClassLoader());
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
