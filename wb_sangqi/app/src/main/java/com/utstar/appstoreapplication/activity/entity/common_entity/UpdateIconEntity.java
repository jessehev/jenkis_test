package com.utstar.appstoreapplication.activity.entity.common_entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aria.Lao on 2017/2/14.
 * 控制刷新图标的实体
 */
public class UpdateIconEntity implements Parcelable {
  public boolean isBuySuccess = false;
  public boolean isAuthSuccess = false;

  public UpdateIconEntity(boolean isBuySuccess, boolean isAuthSuccess) {
    this.isBuySuccess = isBuySuccess;
    this.isAuthSuccess = isAuthSuccess;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeByte(this.isBuySuccess ? (byte) 1 : (byte) 0);
    dest.writeByte(this.isAuthSuccess ? (byte) 1 : (byte) 0);
  }

  public UpdateIconEntity() {
  }

  protected UpdateIconEntity(Parcel in) {
    this.isBuySuccess = in.readByte() != 0;
    this.isAuthSuccess = in.readByte() != 0;
  }

  public static final Parcelable.Creator<UpdateIconEntity> CREATOR =
      new Parcelable.Creator<UpdateIconEntity>() {
        @Override public UpdateIconEntity createFromParcel(Parcel source) {
          return new UpdateIconEntity(source);
        }

        @Override public UpdateIconEntity[] newArray(int size) {
          return new UpdateIconEntity[size];
        }
      };
}
