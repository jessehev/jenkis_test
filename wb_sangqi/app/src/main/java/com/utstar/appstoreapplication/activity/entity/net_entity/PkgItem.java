package com.utstar.appstoreapplication.activity.entity.net_entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class PkgItem implements Parcelable {
  @SerializedName("ID") public int id;
  public String name;
  @SerializedName("ADDRESS") public String imgUrl;
  @SerializedName("APKADDRESS") public String apkUrl;
  @SerializedName("PACKAGENAME") public String packageName;
  public boolean isShelves = false;

  public PkgItem() {
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.name);
    dest.writeString(this.imgUrl);
    dest.writeString(this.apkUrl);
    dest.writeString(this.packageName);
    dest.writeByte(this.isShelves ? (byte) 1 : (byte) 0);
  }

  protected PkgItem(Parcel in) {
    this.id = in.readInt();
    this.name = in.readString();
    this.imgUrl = in.readString();
    this.apkUrl = in.readString();
    this.packageName = in.readString();
    this.isShelves = in.readByte() != 0;
  }

  public static final Creator<PkgItem> CREATOR = new Creator<PkgItem>() {
    @Override public PkgItem createFromParcel(Parcel source) {
      return new PkgItem(source);
    }

    @Override public PkgItem[] newArray(int size) {
      return new PkgItem[size];
    }
  };
}
