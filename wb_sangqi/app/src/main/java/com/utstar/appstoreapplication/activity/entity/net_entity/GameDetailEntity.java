package com.utstar.appstoreapplication.activity.entity.net_entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/13.
 * 游戏详情实体
 */
public class GameDetailEntity implements Parcelable {
  /**
   * 游戏ID
   */
  @SerializedName("productid") public int gameId;
  @SerializedName("productname") public String gameName;
  @SerializedName("providername") public String CompanyName;  //游戏公司
  @SerializedName("productmode") public int mode;
  @SerializedName("productstar") public int star;
  @SerializedName("praise") public int starNum; // 点赞数
  @SerializedName("ispraise") public String isPraise; // 0:未点赞   1：已点赞
  @SerializedName("imageuri") public String imgUrl;
  public String desc;
  @SerializedName("screenshotlist") public List<ImgUrlEntity> gameShots;
  @SerializedName("relationlist") public List<GuessEntity> guessList;
  @SerializedName("downnum") public int downloadNum;
  @SerializedName("producttype") public String gameType;
  @SerializedName("apkuri") public String downloadUrl; //download
  @SerializedName("packagename") public String packageName;
  @SerializedName("md5") public String apkMd5Code;
  @SerializedName("apksize") public String apkSize;
  //0:非试玩，1:试玩
  @SerializedName("gametag") public int gameTag = 0;
  /**
   * 区分HTML游戏字段："type":"1" (代表APK)    "type":"2"（代表HTML5）
   */
  public int type = 1;
  /**
   * 表示用户是否下载过该游戏
   * 0表示没有 1表示有
   */
  @SerializedName("ishave") public int isHave;

  /**
   * 商品价格
   */
  public String price = "包月￥ 6.0元";
  /**
   * 状态
   */
  public String state = "";
  /**
   * 是否加入套餐包
   */
  public boolean isAddPackage = false;
  /**
   * 付费类型，0：免费，1：包月
   */
  public int payType = 1;

  /**
   * 套餐包Id
   */
  public String packageId;

  /**
   * 中兴鉴权返回码，由自己从鉴权接口中请求获取
   */
  public String errorCode = "1001";

  /**
   * 是否已经购买，由自己从鉴权接口中请求获取
   */
  public boolean isBuy = false;

  /**
   * 是否已下架，由自己从鉴权接口中请求获取
   */
  public boolean isShelves = false;

  /**
   * 是否有更新
   */
  public boolean hasUpdate = false;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.gameId);
    dest.writeString(this.gameName);
    dest.writeString(this.CompanyName);
    dest.writeInt(this.mode);
    dest.writeInt(this.star);
    dest.writeInt(this.starNum);
    dest.writeString(this.isPraise);
    dest.writeString(this.imgUrl);
    dest.writeString(this.desc);
    dest.writeList(this.gameShots);
    dest.writeList(this.guessList);
    dest.writeInt(this.downloadNum);
    dest.writeString(this.gameType);
    dest.writeString(this.downloadUrl);
    dest.writeString(this.packageName);
    dest.writeString(this.apkMd5Code);
    dest.writeString(this.apkSize);
    dest.writeInt(this.gameTag);
    dest.writeInt(this.type);
    dest.writeInt(this.isHave);
    dest.writeString(this.price);
    dest.writeString(this.state);
    dest.writeByte(this.isAddPackage ? (byte) 1 : (byte) 0);
    dest.writeInt(this.payType);
    dest.writeString(this.packageId);
    dest.writeString(this.errorCode);
    dest.writeByte(this.isBuy ? (byte) 1 : (byte) 0);
    dest.writeByte(this.isShelves ? (byte) 1 : (byte) 0);
    dest.writeByte(this.hasUpdate ? (byte) 1 : (byte) 0);
  }

  public GameDetailEntity() {
  }

  protected GameDetailEntity(Parcel in) {
    this.gameId = in.readInt();
    this.gameName = in.readString();
    this.CompanyName = in.readString();
    this.mode = in.readInt();
    this.star = in.readInt();
    this.starNum = in.readInt();
    this.isPraise = in.readString();
    this.imgUrl = in.readString();
    this.desc = in.readString();
    this.gameShots = new ArrayList<ImgUrlEntity>();
    in.readList(this.gameShots, ImgUrlEntity.class.getClassLoader());
    this.guessList = new ArrayList<GuessEntity>();
    in.readList(this.guessList, GuessEntity.class.getClassLoader());
    this.downloadNum = in.readInt();
    this.gameType = in.readString();
    this.downloadUrl = in.readString();
    this.packageName = in.readString();
    this.apkMd5Code = in.readString();
    this.apkSize = in.readString();
    this.gameTag = in.readInt();
    this.type = in.readInt();
    this.isHave = in.readInt();
    this.price = in.readString();
    this.state = in.readString();
    this.isAddPackage = in.readByte() != 0;
    this.payType = in.readInt();
    this.packageId = in.readString();
    this.errorCode = in.readString();
    this.isBuy = in.readByte() != 0;
    this.isShelves = in.readByte() != 0;
    this.hasUpdate = in.readByte() != 0;
  }

  public static final Parcelable.Creator<GameDetailEntity> CREATOR =
      new Parcelable.Creator<GameDetailEntity>() {
        @Override public GameDetailEntity createFromParcel(Parcel source) {
          return new GameDetailEntity(source);
        }

        @Override public GameDetailEntity[] newArray(int size) {
          return new GameDetailEntity[size];
        }
      };
}
