package com.utstar.appstoreapplication.activity.Probe;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.arialyy.frame.util.show.L;

/**
 * Created by lyy on 2016/12/3.
 * 探针服务
 */
public class ProbeService extends Service {
  public static final String GAME_PKG_NAME = "GAME_PKG_NAME";

  public static void start(Context context, String gamePackageName) {
    Intent intent = new Intent("com.wb.probe");
    intent.putExtra(GAME_PKG_NAME, gamePackageName);
    intent.setPackage(context.getPackageName());
    context.getApplicationContext().startService(intent);
    L.d("ProbeService start");
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    Notification notification = new Notification();
    notification.flags = Notification.FLAG_ONGOING_EVENT;
    notification.flags |= Notification.FLAG_NO_CLEAR;
    notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
    startForeground(1, notification);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    //ProbeManager.getInstance().startProbeListGame(pkgName);
    if (intent != null) {
      String pkgName = intent.getStringExtra(GAME_PKG_NAME);
      L.d("ganem_package_name ==> " + pkgName);
      ProbeManager.getInstance().startProbeListGame(pkgName);
    }
    return super.onStartCommand(intent, flags, startId);
  }

  @Override public void onDestroy() {
    ProbeManager.getInstance().destroy();
    super.onDestroy();
  }
}
