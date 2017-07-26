package com.utstar.appstoreapplication.activity.entity.net_entity;

import java.util.List;

public class SysMessageListEntity {

  private int size;

  private int unreadcount;

  private List<SysMessageEntity> list;

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getUnreadcount() {
    return unreadcount;
  }

  public void setUnreadcount(int unreadcount) {
    this.unreadcount = unreadcount;
  }

  public List<SysMessageEntity> getList() {
    return list;
  }

  public void setList(List<SysMessageEntity> list) {
    this.list = list;
  }
}
