package com.utstar.appstoreapplication.activity.windows.my_game;

import android.content.Context;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.DownloadEntity;
import com.utstar.appstoreapplication.activity.entity.db_entity.DownloadInfo;
import com.utstar.appstoreapplication.activity.entity.net_entity.MyGameDetailEntity;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by AriaL on 2017/3/13.
 */
public class MyGameUtil {

  public static Map<String, DownloadEntity> getDownloadInfo(Context context) {
    Map<String, DownloadEntity> downloadInfo = new WeakHashMap<>();
    List<DownloadEntity> list = Aria.get(context).getDownloadList();
    if (list == null) return downloadInfo;
    for (DownloadEntity entity : list) {
      downloadInfo.put(entity.getDownloadUrl(), entity);
    }
    return downloadInfo;
  }

  /**
   * 转换DownloadInfo 为 MyGameDetailEntity
   */
  public static MyGameDetailEntity convertDownloadInfo(DownloadInfo info,
      DownloadEntity downloadEntity) {
    MyGameDetailEntity entity = new MyGameDetailEntity();
    entity.downloadState =
        downloadEntity != null ? downloadEntity.getState() : DownloadEntity.STATE_WAIT;
    entity.gameId = info.getGameId();
    entity.gameIcon = info.getGameIcon();
    entity.gameName = info.getGameName();
    entity.downloadUrl = info.getDownloadUrl();
    entity.packageName = info.getPackageName();
    entity.md5 = info.getMd5Code();
    return entity;
  }
}
