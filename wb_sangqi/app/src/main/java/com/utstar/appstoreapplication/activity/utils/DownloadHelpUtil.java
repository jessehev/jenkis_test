package com.utstar.appstoreapplication.activity.utils;

import android.content.Context;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.DownloadEntity;
import com.arialyy.aria.core.DownloadManager;
import com.arialyy.aria.util.Configuration;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.FileUtil;
import com.arialyy.frame.util.StringUtil;
import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.T;
import com.utstar.appstoreapplication.activity.commons.constants.CommonConstant;
import com.utstar.appstoreapplication.activity.entity.db_entity.DownloadInfo;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameDetailEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.MyGameDetailEntity;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import java.io.File;

/**
 * Created by Aria.Lao on 2016/12/23. 下载帮助工具
 */
public class DownloadHelpUtil {
  public static final String HOT_UPDATE_FILE_NAME = "wanba.zip";
  public static final String SBY_FILE_NAME = "sby.apk";
  public static final String WANBA_APK_NAME = "wanba.apk";

  /**
   * 是否存在该下载信息
   */
  public static boolean hasDownloadInfo(String packageName) {
    return getDownloadInfo(packageName) != null;
  }

  /**
   * 获取下载信息
   */
  public static DownloadInfo getDownloadInfo(String packageName) {
    return DownloadInfo.where("packageName = ?", packageName).findFirst(DownloadInfo.class);
  }

  /**
   * 处理下载完成
   */
  public static void handleDownloadComplete(String downloadUrl) {
    DownloadInfo info =
        DownloadInfo.where("downloadUrl = ?", downloadUrl).findFirst(DownloadInfo.class);
    if (info != null) {
      ActionUtil.updateAction(info, CommonConstant.ACTION_DOWNLOAD);
      info.setComplete(true);
      info.save();
      File apk = new File(info.getDownloadPath());
      if (FileUtil.checkMD5(info.getMd5Code(), apk)) {
        ApkUtil.installApk(BaseApp.context, apk.getPath());
      } else {
        FL.d("GameDownloadUtil", "下载地址【" + info.getDownloadUrl() + "】的apk文件md5码错误，即将重新下载");
        //Aria.whit(getApplicationContext()).load(entity).start();
        T.showShort(BaseApp.context, "文件错误");
      }

      //if (GameUpdateUtil.isUpdate(info.getPackageName())){
      //  GameUpdateUtil.delGameUpdateInfo(info.getPackageName());
      //}
    }
  }

  public static DownloadEntity createDownloadEntity(String downloadUrl, String packageName) {
    DownloadEntity entity = new DownloadEntity();
    entity.setDownloadPath(DownloadHelpUtil.getApkDownloadPath(downloadUrl, packageName));
    entity.setFileName(DownloadHelpUtil.getApkDownloadName(downloadUrl, packageName));
    entity.setDownloadUrl(downloadUrl);
    return entity;
  }

  /**
   * 下载
   */
  public static void download(Context context, DownloadEntity entity) {
    if (isMaxDownload()) {
      Aria.whit(context).load(entity).add();
    } else {
      Aria.whit(context).load(entity).start();
    }
  }

  /**
   * 是否是最大下载
   */
  public static boolean isMaxDownload() {
    int size = DownloadManager.getInstance().getTaskQueue().size();
    return size >= Configuration.getInstance().getDownloadNum();
  }

  ///**
  // * 待更新的
  // */
  //public static DownloadInfo createDownloadInfo(DownloadEntity downloadEntity, MyGameDetailEntity entity) {
  //  DownloadInfo info = new DownloadInfo();
  //  //info.setDownloadUrl(entity.downloadUrl);
  //  //info.setMd5Code(entity.apkMd5Code);
  //  info.setPackageName(entity.getPackageName());
  //  info.setGameIcon(entity.getGameIcon());
  //  info.setGameId(entity.getGameId());
  //  info.setComplete(false);
  //  info.setGameName(entity.getGameName());
  //  info.setAddPackage(entity.isAddPackage);
  //  info.setDownloadPath(downloadEntity.getDownloadPath());
  //  info.save();
  //  return info;
  //}

  /**
   * 创建并下载信息实体
   */
  public static DownloadInfo createDownloadInfo(String downloadPath, GameDetailEntity entity) {
    DownloadInfo info = new DownloadInfo();
    info.setDownloadUrl(entity.downloadUrl);
    info.setMd5Code(entity.apkMd5Code);
    info.setPackageName(entity.packageName);
    info.setGameIcon(entity.imgUrl);
    info.setGameId(entity.gameId);
    info.setComplete(false);
    info.setGameName(entity.gameName);
    info.setAddPackage(entity.isAddPackage);
    info.setDownloadPath(downloadPath);
    info.saveIfNotExist("packageName = ? AND downloadUrl = ?", entity.packageName,
        entity.downloadUrl);
    return info;
  }

  /**
   * 创建下载信息实体
   */
  public static DownloadInfo createDownloadInfo(String downloadPath, MyGameDetailEntity entity) {
    DownloadInfo info = new DownloadInfo();
    info.setDownloadUrl(entity.downloadUrl);
    info.setMd5Code(entity.md5);
    info.setPackageName(entity.packageName);
    info.setGameIcon(entity.gameIcon);
    info.setGameId(entity.gameId);
    info.setComplete(false);
    info.setGameName(entity.gameName);
    info.setAddPackage(entity.isAddPackage);
    info.setDownloadPath(downloadPath);
    info.saveIfNotExist("downloadUrl = ?", entity.getDownloadUrl());
    return info;
  }

  /**
   * 设置apk下载名
   *
   * @param downloadUrl 下载链接
   * @param pkg 包名
   */
  public static String getApkDownloadName(String downloadUrl, String pkg) {
    return StringUtil.keyToHashKey(downloadUrl) + "_" + pkg + ".apk";
  }

  /**
   * 获取下载路径
   */
  public static String getDownloadPath(Context context) {
    return FilePathUtils.getDownloadFolder(context).getAbsolutePath() + "/";
  }

  /**
   * 获取apk完整路径
   */
  public static String getApkDownloadPath(String downloadUrl, String pkg) {
    return getDownloadPath(BaseApp.context) + getApkDownloadName(downloadUrl, pkg);
  }

  /**
   * 热更新资源路径
   */
  public static String getHotUpdateResPath() {
    return FilePathUtils.getDownloadFolder(BaseApp.context).getAbsolutePath()
        + "/hotRes/"
        + HOT_UPDATE_FILE_NAME;
  }

  /**
   * 获取玩吧apk路径
   */
  public static String getWanBaApkPath() {
    return getDownloadPath(BaseApp.context) + WANBA_APK_NAME;
  }

  /**
   * 视博云路径
   */
  public static String getSBYApkPath() {
    return FilePathUtils.getDownloadFolder(BaseApp.context).getAbsolutePath()
        + "/sby/"
        + SBY_FILE_NAME;
  }
}
