package com.utstar.appstoreapplication.activity.entity.net_entity;

/**
 * Created by lt on 2016/12/19.
 */

public class NewEventsEntity {
  private Integer imgUrl;
  private String number;

  public NewEventsEntity(Integer imgUrl, String number) {
    this.imgUrl = imgUrl;
    this.number = number;
  }

  public NewEventsEntity() {
  }

  public Integer getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(Integer imgUrl) {
    this.imgUrl = imgUrl;
  }

  public NewEventsEntity(Integer imgUrl) {
    this.imgUrl = imgUrl;
  }
}
