package com.utstar.appstoreapplication.activity.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.arialyy.frame.util.show.L;
import com.ut_sdk.day_task.DayTaskEntity;
import com.ut_sdk.day_task.IDayTaskInterface;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;

/**
 * Created by Aria.Lao on 2017/4/13.
 */
public class DayTaskRemoteService extends Service {
  private static final String TAG = "DayTaskRemoteService";
  public static final String DATA_DAY_TASK_DATA = "DATA_DAY_TASK_DATA";
  public static final String ACTION_DAY_TASK = "ACTION_DAY_TASK";

  @Override public IBinder onBind(Intent intent) {
    // Return the interface
    return mBinder;
  }

  private final IDayTaskInterface.Stub mBinder = new IDayTaskInterface.Stub() {

    @Override public void loadDayTaskEntity(DayTaskEntity dayTaskEntity) throws RemoteException {
      Intent intent = new Intent(ACTION_DAY_TASK);
      intent.putExtra(DATA_DAY_TASK_DATA, dayTaskEntity);
      BaseApp.context.sendBroadcast(intent);
      L.d(TAG, dayTaskEntity.toString());
    }

    public int getPid() {
      return android.os.Process.myPid();
    }
  };
}
