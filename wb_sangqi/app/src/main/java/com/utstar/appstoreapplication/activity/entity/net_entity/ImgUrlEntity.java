package com.utstar.appstoreapplication.activity.entity.net_entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by JesseHev on 2016/12/13.
 */

public class ImgUrlEntity implements Parcelable {
  @SerializedName("imageuri") public String imgUrl;
  public String videoUrl;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.imgUrl);
    dest.writeString(this.videoUrl);
  }

  public ImgUrlEntity() {
  }

  protected ImgUrlEntity(Parcel in) {
    this.imgUrl = in.readString();
    this.videoUrl = in.readString();
  }

  public static final Parcelable.Creator<ImgUrlEntity> CREATOR =
      new Parcelable.Creator<ImgUrlEntity>() {
        @Override public ImgUrlEntity createFromParcel(Parcel source) {
          return new ImgUrlEntity(source);
        }

        @Override public ImgUrlEntity[] newArray(int size) {
          return new ImgUrlEntity[size];
        }
      };
}
