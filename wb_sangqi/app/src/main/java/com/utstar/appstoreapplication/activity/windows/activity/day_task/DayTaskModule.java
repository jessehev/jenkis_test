package com.utstar.appstoreapplication.activity.windows.activity.day_task;

import android.content.Context;
import android.text.TextUtils;
import com.arialyy.frame.util.SharePreUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.net_entity.DayTaskEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.ActivityApi;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import com.utstar.appstoreapplication.activity.windows.common.MsgDialog;

/**
 * Created by JesseHev on 2017/4/13.
 */

final class DayTaskModule extends BaseModule {
  public DayTaskModule(Context context) {
    super(context);
  }

  Gson mGson = new GsonBuilder().registerTypeAdapter(new TypeToken<DayTaskEntity>() {
  }.getType(), new BasicDeserializer<DayTaskEntity>()).create();

  /**
   * 领取奖励
   *
   * @param context context
   * @param awardId 奖品id
   * @param num 任务编号
   */
  void getDayTaskAwards(Context context, int awardId, int num) {
    mNetManager.request(ActivityApi.class, mGson)
        .getDayTaskAwards(awardId, num)
        .compose(new HttpCallback<DayTaskEntity>() {
          @Override public void onResponse(DayTaskEntity response) {
            callback(DayTaskActivity.AWARD_RESULT, response);
          }
        });
  }

  /**
   * 保存中奖用户信息
   *
   * @param awardId 奖品id
   * @param phone 手机号
   * @param num 任务编号
   * @param packageName 游戏包名
   */
  void saveWinInfo(Context context, int awardId, String phone, int num, String packageName) {
    mNetManager.request(ActivityApi.class, mGson)
        .saveWinInfo(awardId, phone, num)
        .compose(new HttpCallback<DayTaskEntity>() {
          @Override public void onResponse(DayTaskEntity response) {
            //MsgDialog msgDialog = new MsgDialog(context, "恭喜您", "感谢您的支持，我们将在5个工作日内联系您领奖！", false);
            //msgDialog.show();
            if (context instanceof DayTaskActivity) {
              DayTaskActivity activity = (DayTaskActivity) context;
              activity.changeData(response);
              activity.mAwardAdapter.updata(response.awardList);//正常提交手机后才会改变奖品状态
              boolean isFocus = checkFoucus(response);
              if (isFocus) {
                activity.requestAward(response);
              } else if (!response.isFinished) {
                activity.requestBtn();
              }
            }
            saveObject(context, response, packageName);
          }
        });
  }

  /**
   * 检查是否有奖品可领取
   */
  public boolean checkFoucus(DayTaskEntity entity) {
    for (DayTaskEntity.TaskSubEntity task : entity.awardList) {
      if (task.status.equals(DayTaskActivity.RECEIVE)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 保存每日任务实体
   */
  public void saveObject(Context context, DayTaskEntity mEntity, String packageName) {
    if (!TextUtils.isEmpty(packageName)) {
      SharePreUtil.putObject(DayTaskActivity.FILE_NAME_TASK, context,
          DayTaskActivity.SHARE_DAY_TASK_KEY, DayTaskEntity.class, mEntity);
    }
  }
}
