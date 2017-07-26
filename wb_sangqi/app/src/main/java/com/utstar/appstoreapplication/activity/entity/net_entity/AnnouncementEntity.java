package com.utstar.appstoreapplication.activity.entity.net_entity;

/**
 * Created by JesseHev on 2017/1/9.
 * 公告
 */

public class AnnouncementEntity {
  public String name;
  public String content;
  public String count;
  public String team;
  public String date;

  @Override public String toString() {
    return "AnnouncementEntity{"
        + "name='"
        + name
        + '\''
        + ", content='"
        + content
        + '\''
        + ", count='"
        + count
        + '\''
        + ", team='"
        + team
        + '\''
        + ", date='"
        + date
        + '\''
        + '}';
  }
}
