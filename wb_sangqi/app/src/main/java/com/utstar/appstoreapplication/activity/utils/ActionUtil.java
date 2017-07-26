package com.utstar.appstoreapplication.activity.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.arialyy.aria.util.Configuration;
import com.arialyy.frame.util.show.L;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetObjectEntity;
import com.utstar.appstoreapplication.activity.entity.db_entity.DownloadInfo;
import com.utstar.appstoreapplication.activity.entity.net_entity.ActionEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.LogAPi;
import com.utstar.appstoreapplication.activity.manager.ActivityManager;
import com.utstar.appstoreapplication.activity.manager.NetManager;

/**
 * Created by AriaL on 2017/2/22.
 */

public class ActionUtil {
  public static String ACTION_SYS_STATUS = "action_sys_status";

  public static void updateAction(DownloadInfo info, int type) {
    Gson gson =
        new GsonBuilder().registerTypeAdapter(new TypeToken<NetObjectEntity<ActionEntity>>() {
        }.getType(), new BasicDeserializer<NetObjectEntity<ActionEntity>>()).create();
    NetManager.getInstance()
        .request(LogAPi.class, gson)
        .apkAction(info.getGameId(), type)
        .compose(new HttpCallback<NetObjectEntity<ActionEntity>>() {
          @Override public void onResponse(NetObjectEntity<ActionEntity> response) {
            L.d("日志上报成功，gameId = " + info.getGameId() + "，type = " + type);
          }
        });
  }

  /**
   * 更新系统小红点
   */
  public static void updateSysMsgStatus(Context context) {
    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ACTION_SYS_STATUS));
  }
}
