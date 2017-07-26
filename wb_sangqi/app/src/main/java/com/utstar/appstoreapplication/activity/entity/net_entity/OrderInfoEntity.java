package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyy on 2016/9/2.
 * 游戏购买确认信息实体
 */
public class OrderInfoEntity {
  int id;
  /**
   * 游戏名（标题）
   */
  @SerializedName("gameName") String title;

  List<Info> infos = new ArrayList<>();

  public List<Info> getInfos() {
    return infos;
  }

  public void setInfos(List<Info> infos) {
    this.infos = infos;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * 产品介绍信息
   */
  public static class Info {
    /**
     * 按钮文字
     */
    @SerializedName("title") String btName;
    /**
     * 资费id
     */
    @SerializedName("serviceId") String packageId;
    /**
     * 商品价格
     */
    String price;
    /**
     * 显示剩余价格图标
     */
    String isShow;

    /**
     * 产品名
     */
    @SerializedName("mProductName") String productName;
    /**
     * 完整的订购说明，使用html高亮颜色，防止运营商要求修改
     */
    String orderInfo;
    /**
     * 完整产品资费介绍，使用html高亮颜色，防止运营商要求修改
     */
    String tariffInfo;

    public String getZfName() {
      return zfName;
    }

    public void setZfName(String zfName) {
      this.zfName = zfName;
    }

    /**
     * 资费名称
     *
     * @return
     */
    @SerializedName("zfname") String zfName;

    public String getBtName() {
      return btName;
    }

    public String getPackageId() {
      return packageId;
    }

    public String getPrice() {
      return price;
    }

    public void setPrice(String price) {
      this.price = price;
    }

    public String getProductName() {
      return productName;
    }

    public void setProductName(String mProductName) {
      this.productName = mProductName;
    }

    public String getOrderInfo() {
      return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
      this.orderInfo = orderInfo;
    }

    public String getTariffInfo() {
      return tariffInfo;
    }

    public void setTariffInfo(String tariffInfo) {
      this.tariffInfo = tariffInfo;
    }

    public String getIsShow() {
      return isShow;
    }
  }
}
