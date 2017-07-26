package com.utstar.appstoreapplication.activity.entity.net_entity;

public class SysMessageEntity {

  private int messageid;
  private String title;
  private String content;
  private int isreaded;
  private String date;
  private String imageurl;
  /**
   * 0:站内信，1:系统消息
   */
  private int flag;

  public int getFlag() {
    return flag;
  }

  public String getImageurl() {
    return imageurl;
  }

  public void setImageurl(String imageurl) {
    this.imageurl = imageurl;
  }

  public int getMessageid() {
    return messageid;
  }

  public void setMessageid(int messageid) {
    this.messageid = messageid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getIsreaded() {
    return isreaded;
  }

  public void setIsreaded(int isreaded) {
    this.isreaded = isreaded;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }
}
