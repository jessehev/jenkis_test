package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JesseHev on 2017/1/9.
 * 签到奖品
 */

public class AwardEntity {

  @SerializedName("okstatus") public String okStatus;
  @SerializedName("targetday") public int targetDay;
  public String flag;
  public String award;
  public boolean select;
  @SerializedName("awardurl") public String awardUrl;
  public boolean recive;

  public int getTargetDay() {
    return targetDay;
  }

  public void setTargetDay(int targetDay) {
    this.targetDay = targetDay;
  }

  public String getFlag() {
    return flag;
  }

  public void setFlag(String flag) {
    this.flag = flag;
  }

  public String getOkStatus() {
    return okStatus;
  }

  public void setOkStatus(String okStatus) {
    this.okStatus = okStatus;
  }

  public String getAward() {
    return award;
  }

  public void setAward(String award) {
    this.award = award;
  }

  public String getAwardUrl() {
    return awardUrl;
  }

  public void setAwardUrl(String awardUrl) {
    this.awardUrl = awardUrl;
  }

  public boolean isRecive() {
    return recive;
  }

  public void setRecive(boolean recive) {
    this.recive = recive;
  }

  public boolean isSelect() {
    return select;
  }

  public void setSelect(boolean select) {
    this.select = select;
  }
}
