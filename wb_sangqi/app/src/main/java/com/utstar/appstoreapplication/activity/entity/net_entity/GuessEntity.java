package com.utstar.appstoreapplication.activity.entity.net_entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by JesseHev on 2016/12/13.
 * 游戏详情，猜你喜欢数据
 */
public class GuessEntity implements Parcelable {

  @SerializedName("productid") public int gameId;
  @SerializedName("imageuri") public String imgUrl;
  public boolean isChecked;
  @SerializedName("productname") public String name;
  /**
   * 是否加入套餐包
   */
  public boolean isAddPackage = false;
  /**
   * 套餐包Id
   */
  public String packageId;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.gameId);
    dest.writeString(this.imgUrl);
    dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    dest.writeString(this.name);
    dest.writeByte(this.isAddPackage ? (byte) 1 : (byte) 0);
    dest.writeString(this.packageId);
  }

  public GuessEntity() {
  }

  protected GuessEntity(Parcel in) {
    this.gameId = in.readInt();
    this.imgUrl = in.readString();
    this.isChecked = in.readByte() != 0;
    this.name = in.readString();
    this.isAddPackage = in.readByte() != 0;
    this.packageId = in.readString();
  }

  public static final Parcelable.Creator<GuessEntity> CREATOR =
      new Parcelable.Creator<GuessEntity>() {
        @Override public GuessEntity createFromParcel(Parcel source) {
          return new GuessEntity(source);
        }

        @Override public GuessEntity[] newArray(int size) {
          return new GuessEntity[size];
        }
      };
}
