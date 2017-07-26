package com.utstar.appstoreapplication.activity.entity.net_entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lyy on 2016/8/24.
 * 套餐专区实体
 */
public class PackageEntity implements Parcelable {
  @SerializedName("IMG") String imgUrl;
  String name;
  @SerializedName("PRODUCTID") String id;
  boolean isBuy = false;
  @SerializedName("PRICE") int price;
  int tag;

  public int getTag() {
    return tag;
  }

  public void setTag(int tag) {
    this.tag = tag;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public PackageEntity(String imgUrl, String name) {
    this.imgUrl = imgUrl;
    this.name = name;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public boolean isBuy() {
    return isBuy;
  }

  public void setBuy(boolean buy) {
    isBuy = buy;
  }

  public String getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.imgUrl);
    dest.writeString(this.name);
    dest.writeString(this.id);
    dest.writeByte(this.isBuy ? (byte) 1 : (byte) 0);
    dest.writeInt(this.price);
  }

  protected PackageEntity(Parcel in) {
    this.imgUrl = in.readString();
    this.name = in.readString();
    this.id = in.readString();
    this.isBuy = in.readByte() != 0;
    this.price = in.readInt();
  }

  public static final Creator<PackageEntity> CREATOR = new Creator<PackageEntity>() {
    @Override public PackageEntity createFromParcel(Parcel source) {
      return new PackageEntity(source);
    }

    @Override public PackageEntity[] newArray(int size) {
      return new PackageEntity[size];
    }
  };
}
