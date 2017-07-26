package com.utstar.appstoreapplication.activity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;
import com.ut_sdk.day_task.DayTaskEntity;
import com.utstar.appstoreapplication.activity.manager.ActivityManager;
import com.utstar.appstoreapplication.activity.service.DayTaskRemoteService;

/**
 * Created by JesseHev on 2017/4/17.
 */

public class DayTaskReceiver extends BroadcastReceiver {
  @Override public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(DayTaskRemoteService.ACTION_DAY_TASK)) {
      DayTaskEntity entity = intent.getParcelableExtra(DayTaskRemoteService.DATA_DAY_TASK_DATA);
      ActivityManager.getInstance()
          .handleDayTask(context, entity.spId, entity.packageName, entity.taskType,
              entity.taskCondition);

      //DayTaskDialog dialog = new DayTaskDialog(context, "11");
      //dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
      //dialog.show();
    }
  }
}
