package com.ut_sdk.pay;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aria.Lao on 2017/3/31.
 */

public class PayEntity implements Parcelable {
  /**
   * 产品ID
   */
  public String productId;
  /**
   * 内容Id
   */
  public String contentId;
  /**
   * 0:订购，1:订购并且自动续订
   */
  public int continueType;

  /**
   * 订单号
   */
  public String orderCode;

  /**
   * 错误码
   */
  public String errorCode;

  /**
   * 服务编码
   */
  public String serviceId;

  /**
   * cp渠道
   */
  public String spId;

  /**
   * cp提供的流水号
   */
  public String flowCode;

  /**
   * epg 用户id
   */
  public String epgUserId;

  /**
   * 支付类型
   */
  public int payType;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.productId);
    dest.writeString(this.contentId);
    dest.writeInt(this.continueType);
    dest.writeString(this.orderCode);
    dest.writeString(this.errorCode);
    dest.writeString(this.serviceId);
    dest.writeString(this.spId);
    dest.writeString(this.flowCode);
    dest.writeString(this.epgUserId);
    dest.writeInt(this.payType);
  }

  public PayEntity() {
  }

  @Override public String toString() {
    return "PayEntity{"
        + "productId='"
        + productId
        + '\''
        + ", contentId='"
        + contentId
        + '\''
        + ", continueType="
        + continueType
        + ", orderCode='"
        + orderCode
        + '\''
        + ", errorCode='"
        + errorCode
        + '\''
        + ", serviceId='"
        + serviceId
        + '\''
        + ", spId='"
        + spId
        + '\''
        + ", flowCode='"
        + flowCode
        + '\''
        + ", epgUserId='"
        + epgUserId
        + '\''
        + ", payType="
        + payType
        + '}';
  }

  protected PayEntity(Parcel in) {
    this.productId = in.readString();
    this.contentId = in.readString();
    this.continueType = in.readInt();
    this.orderCode = in.readString();
    this.errorCode = in.readString();
    this.serviceId = in.readString();
    this.spId = in.readString();
    this.flowCode = in.readString();
    this.epgUserId = in.readString();
    this.payType = in.readInt();
  }

  public void readFromParcel(Parcel dest) {
    this.productId = dest.readString();
    this.contentId = dest.readString();
    this.continueType = dest.readInt();
    this.orderCode = dest.readString();
    this.errorCode = dest.readString();
    this.serviceId = dest.readString();
    this.spId = dest.readString();
    this.flowCode = dest.readString();
    this.epgUserId = dest.readString();
    this.payType = dest.readInt();
  }

  public static final Creator<PayEntity> CREATOR = new Creator<PayEntity>() {
    @Override public PayEntity createFromParcel(Parcel source) {
      return new PayEntity(source);
    }

    @Override public PayEntity[] newArray(int size) {
      return new PayEntity[size];
    }
  };
}
