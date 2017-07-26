package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by lt on 2016/12/20.
 */

public class NewEventListEntity {

  private int size;
  public List<NewEventEntity> list;

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public List<NewEventEntity> getList() {
    return list;
  }

  public void setList(List<NewEventEntity> list) {
    this.list = list;
  }

  public static class NewEventEntity {

    @SerializedName("typeid") private int typeId;
    private boolean isAddPackage;
    private String content;
    private String desc;
    private String productid;
    private int activityid;
    private String activityname;
    private int number;
    private String imageurl;
    private int type;
    private String packageId;

    public boolean isBuy;//套餐包是否购买
    public String typeActivity;// "0":  没有活动   "1": 抽奖    "2": 签到

    public int getTypeId() {
      return typeId;
    }

    public void setTypeId(int typeId) {
      this.typeId = typeId;
    }

    public String getPackageId() {
      return packageId;
    }

    public void setPackageId(String packageId) {
      this.packageId = packageId;
    }

    public boolean isIsAddPackage() {
      return isAddPackage;
    }

    public void setIsAddPackage(boolean isAddPackage) {
      this.isAddPackage = isAddPackage;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public String getDesc() {
      return desc;
    }

    public void setDesc(String desc) {
      this.desc = desc;
    }

    public String getProductid() {
      return productid;
    }

    public void setProductid(String productid) {
      this.productid = productid;
    }

    public int getActivityid() {
      return activityid;
    }

    public void setActivityid(int activityid) {
      this.activityid = activityid;
    }

    public String getActivityname() {
      return activityname;
    }

    public void setActivityname(String activityname) {
      this.activityname = activityname;
    }

    public int getNumber() {
      return number;
    }

    public void setNumber(int number) {
      this.number = number;
    }

    public String getImageurl() {
      return imageurl;
    }

    public void setImageurl(String imageurl) {
      this.imageurl = imageurl;
    }

    public int getType() {
      return type;
    }

    public void setType(int type) {
      this.type = type;
    }
  }
}
