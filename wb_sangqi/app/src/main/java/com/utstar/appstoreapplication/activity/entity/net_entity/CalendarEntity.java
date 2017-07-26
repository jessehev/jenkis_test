package com.utstar.appstoreapplication.activity.entity.net_entity;

/**
 * Created by JesseHev on 2017/1/9.
 *
 * 签到日历
 */

public class CalendarEntity {

  public boolean isSign;

  public String date;

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public boolean isSign() {
    return isSign;
  }

  public void setSign(boolean sign) {
    isSign = sign;
  }
}
