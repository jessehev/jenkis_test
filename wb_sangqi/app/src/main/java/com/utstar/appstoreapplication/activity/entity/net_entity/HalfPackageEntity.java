package com.utstar.appstoreapplication.activity.entity.net_entity;

import java.util.List;

/**
 * Created by JesseHev on 2017/3/31.
 */

public class HalfPackageEntity {

  public boolean isVaild;//是否购买连续包月
  public boolean isShow;//当天是否显示过推荐页
  public List<GamePackage> list;
  public static class GamePackage {
    public String packgeName;
    public String packageId;
    public String packageImg;
  }
}
