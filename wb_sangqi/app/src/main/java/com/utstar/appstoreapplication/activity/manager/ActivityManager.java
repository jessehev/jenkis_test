package com.utstar.appstoreapplication.activity.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.net_entity.ActivityEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.AutoSignEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.DayTaskEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.HalfPackageEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SignEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SignHintEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.ActivityApi;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.LauncherApi;
import com.utstar.appstoreapplication.activity.windows.activity.day_task.DayTaskActivity;
import com.utstar.appstoreapplication.activity.windows.activity.discount_package.GamePackageDialog;
import com.utstar.appstoreapplication.activity.windows.activity.sign.SignDeserializer;
import com.utstar.appstoreapplication.activity.windows.activity.sign.SignDialog;
import com.utstar.appstoreapplication.activity.windows.activity.sign.TipsDialog;

import java.util.List;

/**
 * Created by Administrator on 2017-03-06.
 *
 * 活动配置管理
 */

public class ActivityManager extends CommonManager {

  private static volatile ActivityManager INSTANCE = null;
  private static final Object LOCK = new Object();
  private TurnManager mTurnManager;
  /**
   * 砸蛋、抽奖活动结束
   */
  public static final String ACTION_DRAW_FINISH = "draw_finish";

  /**
   * 跳转类型key
   */
  public static final String TYPE_KEY = "turn_type";

  /**
   * 套餐包id的key
   */
  public static final String PACKAGE_KEY = "package_id";
  /**
   * 进入签到
   */
  public static final int TURN_TYPE_SIGN = 2;
  /**
   * 进入套餐包
   */
  public static final int TURN_TYPE_PACKAGE = 3;
  /**
   * 从最新活动进入抽奖页面
   */
  public static final int TURN_TYPE_ACTIVITY = 4;
  /**
   * epg启动抽奖得时候，从抽奖页返回进入最新活动
   */
  public static final int TUEN_TYPE_EPG = 5;
  /**
   * 关联得套餐包是否购买key
   */
  public static final String PACKAGE_ISBUY_KEY = "isBuy";

  /**
   * 开启 开启半价套餐包对话框在mainModule中dismiss后的业务
   */
  public static final boolean SWICH_ON = true;
  /**
   * 关闭 开启半价套餐包对话框在mainModule中dismiss后的业务
   */
  public static final boolean SWICH_OFF = false;
  /**
   * 调用每日任务信息接口的模块类型 1-大厅  2-最新活动
   */
  public static final int DAY_TASK_TYPE_NOMAL = 1;
  public static final int DAY_TASK_TYPE_ACTIVITY = 2;

  /**
   * 启动每日任务的action
   */
  public static final String KEY_TASK_FILTER = "action_show_task";

  Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<ActivityEntity>() {
  }.getType(), new BasicDeserializer<ActivityEntity>()).create();

  public static ActivityManager getInstance() {
    if (INSTANCE == null) {
      synchronized (LOCK) {
        INSTANCE = new ActivityManager();
      }
    }
    return INSTANCE;
  }

  @Override void init() {
    mTurnManager = TurnManager.getInstance();
  }

  @Override String initName() {
    return "活动配置管理";
  }

  @Override void onDestroy() {
  }

  /**
   * 区分活动是平台还是套餐包
   */
  public void diffActivity(Context context, String packageId) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<String>() {
    }.getType(), new BasicDeserializer<String>()).create();
    NetManager.getInstance()
        .request(ActivityApi.class, gson)
        .diffActivity()
        .compose(new HttpCallback<String>() {
          @Override public void onResponse(String response) {
            //     ruletype = 1 套餐包， ruletype = 2 平台    ruletype = 0 什么都没配
            //if (response.equals("1")) {
            //  popActivity(context, packageId);
            //}
          }
        });
  }

  /**
   * 弹出配置活动
   */
  public void popActivity(Context context, String packageId, boolean isBuy) {
    NetManager.getInstance()
        .request(ActivityApi.class, gson)
        .popActivity(packageId)
        .compose(new HttpCallback<ActivityEntity>() {
          @Override public void onResponse(ActivityEntity response) {
            if (response == null || response.activitys == null || response.activitys.size() == 0) {
              //没有关联活动或关联的活动已经弹出过
              mTurnManager.turnPackage(context, packageId);
              return;
            }
            //popType :0 未弹出,1 关联活动已经弹出过了
            if (response.popType == 0) {
              turnActivity(context, response.activitys, packageId, isBuy);
            } else if (response.popType == 1) {
              mTurnManager.turnPackage(context, packageId);
            }
          }

          @Override public void onFailure(Throwable e) {
            mTurnManager.turnPackage(context, packageId);
          }
        });
  }

  /**
   * 跳转业务
   *
   * @param activitys 已配置活动列表
   */
  public void turnActivity(Context context, List<ActivityEntity.ActivitySubEntity> activitys,
      String packageId, boolean isBuy) {

    switch (activitys.size()) {
      case 1:  //只配置了一个活动
        //type活动类型:1 是抽奖活动,2是签到活动
        int type = activitys.get(0).type;
        if (type == 1) {
          mTurnManager.turnDrawAward(context, TURN_TYPE_PACKAGE, packageId, isBuy);
        } else if (type == 2) {
          autoSign(context, packageId, 1);
        }
        break;
      case 2: //配置得多个活动
        int type2 = activitys.get(0).type;
        if (type2 == 1) {
          //      mTurnManager.turnDrawAward(context, TURN_TYPE_SIGN, packageId);
          mTurnManager.turnDrawAward(context, TURN_TYPE_SIGN, packageId, isBuy);//先抽奖后签到
        } else if (type2 == 2) {
          autoSign(context, packageId, 2);
        }
        break;
    }
  }

  /**
   * 获取签到奖品列表
   */
  private int sign;

  private void getAwardList(Context context, String packageId, int isSigned, int type) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SignEntity>() {
    }.getType(), new BasicDeserializer<SignEntity>()).create();

    sign = isSigned;
    NetManager.getInstance()
        .request(LauncherApi.class, gson)
        .getAwardList("1")
        .compose(new HttpCallback<SignEntity>() {
          @Override public void onResponse(SignEntity response) {
            if (response != null && response.list != null && response.list.size() > 0) {
              SignDialog signDialog = new SignDialog(context, response, isSigned);
              signDialog.setOnSureClickListener(() -> {
                //  if (sign == 1) {
                //显示领奖信息框
                sign = 0;
                showSignHint(context, type, packageId, signDialog);
                //}
              });
              signDialog.show();
              //弹出签到页面后6s，如果中奖则显示领奖信息
              new Handler().postDelayed(() -> {
                if (sign == 1) {
                  sign = 0;
                  showSignHint(context, type, packageId, signDialog);
                }
              }, 6000);
            }
          }

          @Override public void onFailure(Throwable e) {
            if (type == 1) {
              mTurnManager.turnPackage(context, packageId);
            } else {
              mTurnManager.turnDrawAward(context, TURN_TYPE_PACKAGE, packageId);
            }
          }
        });
  }

  /**
   * 自动签到  先签到，再获取签到数据并显示
   *
   * type 1 只显示签到， 2先显示签到，再显示抽奖
   */
  public void autoSign(Context context, String packageId, int type) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<AutoSignEntity>() {
    }.getType(), new SignDeserializer<AutoSignEntity>()).create();
    NetManager.getInstance()
        .request(LauncherApi.class, gson)
        .sign()
        .compose(new HttpCallback<AutoSignEntity>() {
          @Override public void onResponse(AutoSignEntity response) {
            if (response.succeed == 1) {
              getAwardList(context, packageId, response.succeed, type);
            } else {
              if (type == 1) {
                mTurnManager.turnPackage(context, packageId);
              } else {
                mTurnManager.turnDrawAward(context, TURN_TYPE_PACKAGE, packageId);
              }
            }
          }

          @Override public void onFailure(Throwable e) {
            if (type == 1) {
              mTurnManager.turnPackage(context, packageId);
            } else {
              mTurnManager.turnDrawAward(context, TURN_TYPE_PACKAGE, packageId);
            }
          }
        });
  }

  /**
   * 显示领奖信息框
   */
  private void showSignHint(Context context, int type, String packageId, SignDialog signDialog) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SignHintEntity>() {
    }.getType(), new BasicDeserializer<SignHintEntity>()).create();
    NetManager.getInstance()
        .request(LauncherApi.class, gson)
        .getSignInfo(1)//type=1 签到活动  type=2抽奖活动
        .compose(new HttpCallback<SignHintEntity>() {
          @Override public void onResponse(SignHintEntity response) {
            if (response != null && response.flag.equals("1")) {  //1 中奖  0未中奖
              //TipsDialog shDialog = TipsDialog.getInstance(context, response.remarks);
              TipsDialog shDialog = new TipsDialog(context, response.remarks);
              shDialog.setOnEnterClickLister(
                  () -> handleCommit(context, signDialog, shDialog, response, type, packageId));
              shDialog.show();
            } else {
              if (type == 1) {
                mTurnManager.turnPackage(context, packageId);
              } else {
                mTurnManager.turnDrawAward(context, TURN_TYPE_PACKAGE, packageId);
              }
              signDialog.dismiss();
            }
          }

          @Override public void onFailure(Throwable e) {
            if (type == 1) {
              mTurnManager.turnPackage(context, packageId);
            } else {
              mTurnManager.turnDrawAward(context, TURN_TYPE_PACKAGE, packageId);
            }
          }
        });
  }

  /**
   * 处理提交业务
   */
  private void handleCommit(Context context, SignDialog signDialog, TipsDialog shDialog,
      SignHintEntity response, int type, String packageId) {
    String content = shDialog.getEditText();
    if (TextUtils.isEmpty(content)) {
      content = "";
    }
    shDialog.dismiss();
    signDialog.dismiss();
    commitContent(context, response.id, content, type, packageId);
  }

  /**
   * 提交领奖信息框输入内容
   */
  private void commitContent(Context context, String id, String content, int type,
      String packageId) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SignHintEntity>() {
    }.getType(), new BasicDeserializer<SignHintEntity>()).create();
    NetManager.getInstance()
        .request(LauncherApi.class, gson)
        .commitInfo(id, content, 1)//type=1 签到活动  type=2抽奖活动
        .compose(new HttpCallback<SignHintEntity>() {
          @Override public void onResponse(SignHintEntity response) {
            if (response.success == 1) {
              //MsgDialog msgDialog = new MsgDialog(context, "恭喜您", "感谢您的支持，我们将在5个工作日内联系您领奖！", false);
              //msgDialog.setDialogCallback(new MsgDialog.OnMsgDialogCallback() {
              //  @Override public void onEnter() {
              //    if (type == 1) {
              //      mTurnManager.turnPackage(context, packageId);
              //    } else {
              //      mTurnManager.turnDrawAward(context, TURN_TYPE_PACKAGE, packageId);
              //    }
              //  }
              //
              //  @Override public void onCancel() {
              //
              //  }
              //});
              //msgDialog.show();
              if (type == 1) {
                mTurnManager.turnPackage(context, packageId);
              } else {
                mTurnManager.turnDrawAward(context, TURN_TYPE_PACKAGE, packageId);
              }
            }
          }

          @Override public void onFailure(Throwable e) {
            if (type == 1) {
              mTurnManager.turnPackage(context, packageId);
            } else {
              mTurnManager.turnDrawAward(context, TURN_TYPE_PACKAGE, packageId);
            }
          }
        });
  }

  /**
   * 套餐包半价优惠
   *
   * @param swich {@link ActivityManager#SWICH_ON}、{@link ActivityManager#SWICH_OFF}
   */
  public void getPackageData(Context context, boolean swich) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<HalfPackageEntity>() {
    }.getType(), new BasicDeserializer<HalfPackageEntity>()).create();
    NetManager.getInstance()
        .request(ActivityApi.class, gson)
        .getPackageData()
        .compose(new HttpCallback<HalfPackageEntity>() {
          @Override public void onResponse(HalfPackageEntity response) {
            if (response != null
                && response.list != null
                && response.isVaild
                && !response.isShow
                && response.list.size() > 0) {
              GamePackageDialog pdDialog = new GamePackageDialog(context, response.list);
              pdDialog.setOnPackDialogLister(() -> {
                if (swich) {
                  //  getDayTaskInfo(context, DAY_TASK_TYPE_NOMAL); //每日任务
                  getIceBreakData(context);
                }
              });
              pdDialog.show(((FragmentActivity) context).getSupportFragmentManager(),
                  "half_package");
            } else {
              if (swich) {
                //getDayTaskInfo(context, DAY_TASK_TYPE_NOMAL); //每日任务
                getIceBreakData(context);
              }
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
            if (swich) {
              //getDayTaskInfo(context, DAY_TASK_TYPE_NOMAL);
              getIceBreakData(context);
            }
          }
        });
  }
  /**********************每日任务***********************************************/

  /**
   * 处理每日任务
   *
   * @param spId 渠道id
   * @param packageName 游戏包名
   * @param taskType 任务类型
   * @param taskValue 任务触发条件（通过关卡数、积分数等条件值）
   */
  public void handleDayTask(Context context, String spId, String packageName, String taskType,
      String taskValue) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<DayTaskEntity>() {
    }.getType(), new BasicDeserializer<DayTaskEntity>()).create();
    NetManager.getInstance()
        .request(ActivityApi.class, gson)
        .handlerDayTask(spId, packageName, taskType, taskValue)
        .compose(new HttpCallback<DayTaskEntity>() {
          @Override public void onResponse(DayTaskEntity response) {
            turnDayTask(context, response, packageName);
          }
        });
  }

  /**
   * 获取每日任务信息
   *
   * @param type {@link ActivityManager#DAY_TASK_TYPE_NOMAL}、{@link ActivityManager#DAY_TASK_TYPE_ACTIVITY}
   */
  public void getDayTaskInfo(Context context, int type) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<DayTaskEntity>() {
    }.getType(), new BasicDeserializer<DayTaskEntity>()).create();
    NetManager.getInstance()
        .request(ActivityApi.class, gson)
        .getDayTaskInfo(-1)
        .compose(new HttpCallback<DayTaskEntity>() {
          @Override public void onResponse(DayTaskEntity response) {
            switch (type) {
              case DAY_TASK_TYPE_NOMAL:
                if (!response.isFinished) {
                  turnDayTask(context, response, null);
                }
                break;
              case DAY_TASK_TYPE_ACTIVITY:
                turnDayTask(context, response, null);
                break;
            }
          }
        });
  }

  /**
   * 跳转每日任务
   *
   * @param context context
   * @param entity 实体
   */
  public void turnDayTask(Context context, DayTaskEntity entity, String packageName) {
    Intent data = new Intent(context, DayTaskActivity.class);
    //Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag.
    data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    data.putExtra(DayTaskActivity.DAYTASK_ENTITY_KEY, entity);
    data.putExtra(DayTaskActivity.PCK_KEY, packageName);
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(data, TurnManager.PAY_REQUEST_CODE);
    } else {
      context.startActivity(data);
    }
  }

  /**
   * 破冰活动
   */
  public void getIceBreakData(Context context) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<HalfPackageEntity>() {
    }.getType(), new BasicDeserializer<HalfPackageEntity>()).create();
    NetManager.getInstance()
        .request(ActivityApi.class, gson)
        .getIceBreakData()
        .compose(new HttpCallback<HalfPackageEntity>() {
          @Override public void onResponse(HalfPackageEntity response) {
            if (response != null
                && response.list != null
                && response.isVaild
                && response.isShow
                && response.list.size() > 0) {
              GamePackageDialog gpDialog = new GamePackageDialog(context, response.list, 1);
              gpDialog.setOnPackDialogLister(() -> {
                //如果没有体验套餐包按返回键则显示每日任务
                getDayTaskInfo(context, DAY_TASK_TYPE_NOMAL); //每日任务
              });
              gpDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "ice_break");
            } else {
              getDayTaskInfo(context, DAY_TASK_TYPE_NOMAL); //每日任务
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
            getDayTaskInfo(context, DAY_TASK_TYPE_NOMAL); //每日任务
          }
        });
  }
}
