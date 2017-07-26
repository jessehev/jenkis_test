package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by JesseHev on 2017/1/9.
 * 签到奖品
 */

public class SignEntity {

  public List<AwardEntity> list;
  @SerializedName("signday") public int signDay;
  @SerializedName("totaldays") public int totalDays;
  @SerializedName("issigned") public int isSigned;
  @SerializedName("signdate") public String signDate;

  public List<AwardEntity> getList() {
    return list;
  }

  public void setList(List<AwardEntity> list) {
    this.list = list;
  }

  public String getSignDate() {
    return signDate;
  }

  public void setSignDate(String signDate) {
    this.signDate = signDate;
  }

  public int getIsSigned() {
    return isSigned;
  }

  public void setIsSigned(int isSigned) {
    this.isSigned = isSigned;
  }

  public int getTotalDays() {
    return totalDays;
  }

  public void setTotalDays(int totalDays) {
    this.totalDays = totalDays;
  }

  public int getSignDay() {
    return signDay;
  }

  public void setSignDay(int signDay) {
    this.signDay = signDay;
  }

  @Override public String toString() {
    return "SignEntity{"
        + "list="
        + list
        + ", signDay="
        + signDay
        + ", totalDays="
        + totalDays
        + ", isSigned="
        + isSigned
        + ", signDate='"
        + signDate
        + '\''
        + '}';
  }
}
