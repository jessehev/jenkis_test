package com.utstar.appstoreapplication.activity.entity.net_entity;

import java.util.List;

/**
 * Created by lt on 2016/12/23.
 */

public class MothlyPaySecondEntity {
  private int size;
  private String IMG;
  private String name;
  private String PRICE;
  private boolean isBuy;
  private int tag;
  private List<MothlyGameEntity> list;

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getIMG() {
    return IMG;
  }

  public void setIMG(String IMG) {
    this.IMG = IMG;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPRICE() {
    return PRICE;
  }

  public void setPRICE(String PRICE) {
    this.PRICE = PRICE;
  }

  public boolean isIsBuy() {
    return isBuy;
  }

  public void setIsBuy(boolean isBuy) {
    this.isBuy = isBuy;
  }

  public int getTag() {
    return tag;
  }

  public void setTag(int tag) {
    this.tag = tag;
  }

  public List<MothlyGameEntity> getList() {
    return list;
  }

  public void setList(List<MothlyGameEntity> list) {
    this.list = list;
  }

  public static class MothlyGameEntity {

    private int ID;
    private String PACKAGENAME;
    private int downcount;    //下载量
    private String APKADDRESS;
    private int productmode;  //遥控器还是手柄
    private String name;
    private String ADDRESS;
    private int gametag;   //限时免费标枪   0 --收费   1 --免费

    public int getDowncount() {
      return downcount;
    }

    public void setDowncount(int downcount) {
      this.downcount = downcount;
    }

    public int getGametag() {
      return gametag;
    }

    public void setGametag(int gametag) {
      this.gametag = gametag;
    }

    public int getID() {
      return ID;
    }

    public void setID(int ID) {
      this.ID = ID;
    }

    public String getPACKAGENAME() {
      return PACKAGENAME;
    }

    public void setPACKAGENAME(String PACKAGENAME) {
      this.PACKAGENAME = PACKAGENAME;
    }

    public String getAPKADDRESS() {
      return APKADDRESS;
    }

    public void setAPKADDRESS(String APKADDRESS) {
      this.APKADDRESS = APKADDRESS;
    }

    public int getProductmode() {
      return productmode;
    }

    public void setProductmode(int productmode) {
      this.productmode = productmode;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getADDRESS() {
      return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
      this.ADDRESS = ADDRESS;
    }
  }
}
