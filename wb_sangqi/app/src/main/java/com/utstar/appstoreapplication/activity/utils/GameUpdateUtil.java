package com.utstar.appstoreapplication.activity.utils;

import android.content.pm.PackageInfo;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.show.FL;
import com.utstar.appstoreapplication.activity.entity.db_entity.UpdateInfo;
import com.utstar.appstoreapplication.activity.entity.net_entity.MyGameDetailEntity;
import com.utstar.appstoreapplication.activity.temp.activity.TempActivity;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.litepal.crud.DataSupport;

/**
 * Created by AriaL on 2017/3/12.
 */
public class GameUpdateUtil {

  /**
   * 获取数据库中所有的更新数据
   */
  public static Map<String, UpdateInfo> getAllUpdateInfo() {
    Map<String, UpdateInfo> map = new WeakHashMap<>();
    try {
      List<UpdateInfo> infos = DataSupport.findAll(UpdateInfo.class);
      if (infos != null && infos.size() > 0) {
        for (UpdateInfo info : infos) {
          map.put(info.getPackageName(), info);
        }
      }
      return map;
    } catch (Exception e) {
      FL.d("GameUpdateUtil", FL.getExceptionString(e));
      return map;
    }
  }

  /**
   * 是否是更新
   */
  public static boolean isUpdate(String pkgName) {
    return DataSupport.isExist(UpdateInfo.class, "packageName = ?", pkgName);
  }

  /**
   * 保存更新信息
   */
  public static void saveUpdateInfo(List<MyGameDetailEntity> list) {
    new Thread(() -> {
      for (MyGameDetailEntity entity : list) {
        if (AndroidUtils.isInstall(BaseApp.context, entity.packageName)) {
          PackageInfo pkgInfo = AndroidUtils.getAppInfo(BaseApp.context, entity.packageName);
          if (pkgInfo != null && pkgInfo.versionCode < entity.versionCode) {
            saveInfo(entity);
          }
        }
      }
    }).start();
  }

  /**
   * 和本地对比是否有新版本
   */
  public static boolean isNewApp(String packageName, int versionCode) {
    PackageInfo pkgInfo = AndroidUtils.getAppInfo(BaseApp.context, packageName);
    return pkgInfo != null && pkgInfo.versionCode < versionCode;
  }

  /**
   * 删除游戏更新信息
   */
  public static void delGameUpdateInfo(String pkgName) {
    DataSupport.deleteAll(UpdateInfo.class, "packageName = ?", pkgName);
  }

  /**
   * 保存更新信息
   */
  public static void saveInfo(MyGameDetailEntity entity) {
    UpdateInfo info = new UpdateInfo();
    info.setAddPackage(entity.isAddPackage());
    info.setDownloadUrl(entity.getDownloadUrl());
    info.setGameIcon(entity.getGameIcon());
    info.setGameName(entity.getGameName());
    info.setGameId(entity.getGameId());
    info.setMd5Code(entity.getMd5());
    info.setPackageName(entity.getPackageName());
    info.saveIfNotExist("packageName = ?", entity.packageName);
  }
}
