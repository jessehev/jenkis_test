package com.utstar.appstoreapplication.activity.entity.net_entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class UpdateVersionEntity implements Parcelable {
  public String desc;
  @SerializedName("versionname") public String versionName;
  @SerializedName("apkurl") public String apkUrl;
  @SerializedName("packagename") public String packageName;
  public String md5;
  @SerializedName("versioncode") public String versionCode;
  @SerializedName("updatetype") public int updateType;
  public int size;
  public boolean isHotUpdate;
  public String hotUpdateVersionCode;
  public String hotUpdateUrl;
  public String hotUpdateMd5;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.desc);
    dest.writeString(this.versionName);
    dest.writeString(this.apkUrl);
    dest.writeString(this.packageName);
    dest.writeString(this.md5);
    dest.writeString(this.versionCode);
    dest.writeInt(this.updateType);
    dest.writeInt(this.size);
    dest.writeByte(this.isHotUpdate ? (byte) 1 : (byte) 0);
    dest.writeString(this.hotUpdateVersionCode);
    dest.writeString(this.hotUpdateUrl);
    dest.writeString(this.hotUpdateMd5);
  }

  public UpdateVersionEntity() {
  }

  protected UpdateVersionEntity(Parcel in) {
    this.desc = in.readString();
    this.versionName = in.readString();
    this.apkUrl = in.readString();
    this.packageName = in.readString();
    this.md5 = in.readString();
    this.versionCode = in.readString();
    this.updateType = in.readInt();
    this.size = in.readInt();
    this.isHotUpdate = in.readByte() != 0;
    this.hotUpdateVersionCode = in.readString();
    this.hotUpdateUrl = in.readString();
    this.hotUpdateMd5 = in.readString();
  }

  public static final Parcelable.Creator<UpdateVersionEntity> CREATOR =
      new Parcelable.Creator<UpdateVersionEntity>() {
        @Override public UpdateVersionEntity createFromParcel(Parcel source) {
          return new UpdateVersionEntity(source);
        }

        @Override public UpdateVersionEntity[] newArray(int size) {
          return new UpdateVersionEntity[size];
        }
      };
}
