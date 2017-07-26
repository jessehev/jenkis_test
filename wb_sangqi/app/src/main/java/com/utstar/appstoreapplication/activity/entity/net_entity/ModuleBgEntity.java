package com.utstar.appstoreapplication.activity.entity.net_entity;

/**
 * Created by lt on 2017/3/6.
 */

public class ModuleBgEntity {
  public String url;
  public String name;

  public ModuleBgEntity(String url, String name) {
    this.url = url;
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
