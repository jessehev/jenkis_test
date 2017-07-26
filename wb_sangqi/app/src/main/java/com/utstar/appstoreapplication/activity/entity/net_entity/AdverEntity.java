package com.utstar.appstoreapplication.activity.entity.net_entity;

/**
 * Created by JesseHev on 2017/1/10.
 */

public class AdverEntity {

  public String userid;
  public String award;

  @Override public String toString() {
    return "AdverEntity{" + "userid='" + userid + '\'' + ", award='" + award + '\'' + '}';
  }

  public AdverEntity(String userid, String award) {
    this.userid = userid;
    this.award = award;
  }
}
