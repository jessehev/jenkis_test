package com.utstar.appstoreapplication.activity.entity;

import com.utstar.appstoreapplication.activity.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHev on 2016/12/13.
 */

public class SystemEntity {

  private String name;
  private Integer icon;
  private String number;

  public SystemEntity(String name, Integer icon, String number) {
    this.name = name;
    this.icon = icon;
    this.number = number;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getIcon() {
    return icon;
  }

  public void setIcon(Integer icon) {
    this.icon = icon;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }
}
