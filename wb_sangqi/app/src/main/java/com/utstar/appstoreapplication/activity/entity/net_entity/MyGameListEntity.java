package com.utstar.appstoreapplication.activity.entity.net_entity;

import java.util.List;

/**
 * Created by lt on 2016/12/19.
 */

public class MyGameListEntity {

  private int size;
  private List<GameEntiy> list;

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public List<GameEntiy> getList() {
    return list;
  }

  public void setList(List<GameEntiy> list) {
    this.list = list;
  }

  public static class GameEntiy {

    private String typeid;
    private int size;
    private String typename;
    private List<MyGameEntity> sublist;

    public String getTypeid() {
      return typeid;
    }

    public void setTypeid(String typeid) {
      this.typeid = typeid;
    }

    public int getSize() {
      return size;
    }

    public void setSize(int size) {
      this.size = size;
    }

    public String getTypename() {
      return typename;
    }

    public void setTypename(String typename) {
      this.typename = typename;
    }

    public List<MyGameEntity> getSublist() {
      return sublist;
    }

    public void setSublist(List<MyGameEntity> sublist) {
      this.sublist = sublist;
    }

    public static class MyGameEntity {

      private boolean isAddPackage;
      private int hottag;
      private String productid;
      private String typename;
      private String typeid;
      private String packagename;
      private String imageuri;
      private int producttype;
      private String productname;
      private String id;
      private String md5;
      private String downloadUrl;
      private int tag;

      public int getTag() {
        return tag;
      }

      public void setTag(int tag) {
        this.tag = tag;
      }

      public String getDownloadUrl() {
        return downloadUrl;
      }

      public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
      }

      public String getMd5() {
        return md5;
      }

      public void setMd5(String md5) {
        this.md5 = md5;
      }

      public String getId() {
        return id;
      }

      public void setId(String id) {
        this.id = id;
      }

      public boolean isIsAddPackage() {
        return isAddPackage;
      }

      public void setIsAddPackage(boolean isAddPackage) {
        this.isAddPackage = isAddPackage;
      }

      public int getHottag() {
        return hottag;
      }

      public void setHottag(int hottag) {
        this.hottag = hottag;
      }

      public String getProductid() {
        return productid;
      }

      public void setProductid(String productid) {
        this.productid = productid;
      }

      public String getTypename() {
        return typename;
      }

      public void setTypename(String typename) {
        this.typename = typename;
      }

      public String getTypeid() {
        return typeid;
      }

      public void setTypeid(String typeid) {
        this.typeid = typeid;
      }

      public String getPackagename() {
        return packagename;
      }

      public void setPackagename(String packagename) {
        this.packagename = packagename;
      }

      public String getImageuri() {
        return imageuri;
      }

      public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
      }

      public int getProducttype() {
        return producttype;
      }

      public void setProducttype(int producttype) {
        this.producttype = producttype;
      }

      public String getProductname() {
        return productname;
      }

      public void setProductname(String productname) {
        this.productname = productname;
      }
    }
  }
}
