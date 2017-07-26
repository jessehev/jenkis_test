package com.utstar.appstoreapplication.activity.entity.net_entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by JesseHev on 2017/1/19.
 * 游戏大厅游戏size
 */

public class GameSizeEntity {
  public String size;

  @SerializedName("list") public List<GameClassifyEntity.GameClassifySubEntity>
      gameClassifySubEntities;

  public static class GameClassifySubEntity {

    public boolean isAddPackage;
    public String desc;
    @SerializedName("hottag") public int hotTag;
    @SerializedName("providername") public String providerName;
    @SerializedName("productid") public int productId;
    public String packageId;
    @SerializedName("productstar") public int productStar;
    public int tag;
    @SerializedName("packagename") public String packageName;
    @SerializedName("imageuri") public String imageUri;
    public int praise;
    @SerializedName("productname") public String productName;
    @SerializedName("downcount") public int downCount;
    @SerializedName("productmode") public int mode;
    public boolean isBuy;
  }
}
