package com.utstar.appstoreapplication.activity.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.arialyy.aria.core.Aria;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.FileUtil;
import com.arialyy.frame.util.SharePreUtil;
import com.arialyy.frame.util.show.L;
import com.arialyy.frame.util.show.T;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ut.wb.ui.metro.MetroItemEntity;
import com.ut.wb.ui.metro.MetroLayout;
import com.utstar.appstoreapplication.activity.commons.constants.KeyConstant;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.net_entity.LotteryEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SbyEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SignEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.LauncherApi;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.LotteryApi;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.SbyPackageApi;
import com.utstar.appstoreapplication.activity.utils.ApkUtil;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;
import com.utstar.appstoreapplication.activity.utils.EpgUserUtil;
import com.utstar.appstoreapplication.activity.windows.activity.draw_award.DrawAwardActivity;
import com.utstar.appstoreapplication.activity.windows.activity.new_activity.NewEventActivity;
import com.utstar.appstoreapplication.activity.windows.activity.sign.SignDialog;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import com.utstar.appstoreapplication.activity.windows.game_detail.GameDetailActivity;
import com.utstar.appstoreapplication.activity.windows.game_hall.game_classify.GameClassifyActivity;
import com.utstar.appstoreapplication.activity.windows.main.MainActivity;
import com.utstar.appstoreapplication.activity.windows.monthly_payment.MothlyPayActivity;
import com.utstar.appstoreapplication.activity.windows.my_game.my_game_detail.MyGameDetailActivity;
import com.utstar.appstoreapplication.activity.windows.payment.OrderInfoActivity;
import com.utstar.appstoreapplication.activity.windows.payment.PayWebView;
import com.utstar.appstoreapplication.activity.windows.video.VideoActivity;
import com.utstar.baseplayer.activity.WebViewsActivity;
import java.io.File;

import static android.content.ContentValues.TAG;

/**
 * Created by Aria.Lao on 2016/12/23.
 * 通用跳转管理
 */
public class TurnManager extends CommonManager {

  private static volatile TurnManager INSTANCE = null;
  private static final Object LOCK = new Object();
  public static final int PAY_REQUEST_CODE = 110;
  public static final int PAY_RESULT_CODE = 111;
  public static final int DRAW_RESULT_CODE = 222;
  public static final int DRAW_REQUEST_CODE = 220;
  public static final int MY_GAME_RESULT_CODE = 66;
  /**
   * 购买成功
   */
  public static final String PAY_SUCCESS_KEY = "PAY_SUCCESS_KEY";

  /**
   * 控制图标刷新实体返回key
   */
  public static final String UPDATE_ICON_KEY = "UPDATE_ICON_KEY";

  /**
   * 是否刷新标识key
   */
  public static final String UPDATE_KEY = "UPDATE_KEY";
  /**
   * 需要刷新标识的fragment所在位置下表key
   */
  public static final String POSITION_KEY = "POSITION_KEY";

  public static TurnManager getInstance() {
    if (INSTANCE == null) {
      synchronized (LOCK) {
        INSTANCE = new TurnManager();
      }
    }
    return INSTANCE;
  }

  @Override void init() {

  }

  @Override String initName() {
    return "TurnManager";
  }

  @Override void onDestroy() {

  }

  /**
   * 跳转视频播放
   */
  public void turnVideoPlay(Context context, String videoUrl) {
    if (TextUtils.isEmpty(videoUrl)) {
      L.e(TAG, "视频链接不能为null");
      return;
    }
    Intent intent = new Intent(context, VideoActivity.class);
    intent.putExtra(VideoActivity.VIDEO_URL_KEY, videoUrl);
    context.startActivity(intent);
  }

  /**
   * 跳转视频专区网页
   */
  public void turnWebGameVideo(Context context, String webUrl) {
    if (TextUtils.isEmpty(webUrl)) {
      L.e(TAG, "网页链接不能为null");
      return;
    }
    Intent intent = new Intent(context, WebViewsActivity.class);
    intent.putExtra("url", webUrl);
    context.startActivity(intent);
  }

  /**
   * 重载（为了后台马全做数据统计） 要加一个参数GameId
   * 跳转订购界面
   *
   * @param type type == 0 id为产品ID，type ==1 id为游戏ID；
   * @param gameId 上报apk用户日志，方便后台做统计功能
   */
  public void turnOrderDetail(Context context, int type, String id, int gameId) {
    if (TextUtils.isEmpty(id)) {
      L.e(TAG, "id不能为null");
      return;
    }
    Intent intent = new Intent(context, OrderInfoActivity.class);
    intent.putExtra(OrderInfoActivity.ID, id);
    intent.putExtra(OrderInfoActivity.TYPE, type);
    intent.putExtra(OrderInfoActivity.GAMAID, gameId);
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, TurnManager.PAY_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * 跳转订购界面
   *
   * @param type type == 0 id为产品ID，type ==1 id为游戏ID；
   */
  public void turnOrderDetail(Context context, int type, String id) {
    if (TextUtils.isEmpty(id)) {
      L.e(TAG, "id不能为null");
      return;
    }
    Intent intent = new Intent(context, OrderInfoActivity.class);
    intent.putExtra(OrderInfoActivity.ID, id);
    intent.putExtra(OrderInfoActivity.TYPE, type);
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, TurnManager.PAY_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * CP跳转订购界面
   *
   * @param cpPackageName cp的应用包名
   * @param productId 产品编码
   * @param serviceId 服务编码
   * @param contentId content编码
   * @param flowCode cp提供的流水号
   */
  public void cpTurnOrderDetail(Context context, String cpPackageName, String productId,
      String serviceId, String contentId, String spId, String flowCode) {
    if (TextUtils.isEmpty(productId)) {
      L.e(TAG, "id不能为null");
      return;
    }
    Intent intent = new Intent(context, OrderInfoActivity.class);
    intent.putExtra(OrderInfoActivity.ID, productId);
    intent.putExtra(OrderInfoActivity.TYPE, 0);
    intent.putExtra(OrderInfoActivity.CP_SERVICE_ID, serviceId);
    intent.putExtra(OrderInfoActivity.CP_CONTENT_ID, contentId);
    intent.putExtra(OrderInfoActivity.CP_SP_ID, spId);
    intent.putExtra(OrderInfoActivity.CP_PACKAGE_NAME, cpPackageName);
    intent.putExtra(OrderInfoActivity.CP_FLOW_CODE, flowCode);
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, TurnManager.PAY_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * 跳转支付网页，为了防止重定向出现的关闭不了activity的问题，需要从新的进程启动webView
   */
  public void turnPayWeb(Context context, String url) {
    if (TextUtils.isEmpty(url)) {
      L.e(TAG, "URL不能为null");
      return;
    }
    Intent intent = new Intent("com.wanba.web.pay");
    //Intent intent = new Intent(context, PayWebView.class);
    intent.putExtra(PayWebView.URL, url);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, PAY_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * 跳转我的游戏
   *
   * @param type {@link MyGameDetailActivity#TYPE_MY_GAME}、{@link MyGameDetailActivity#TYPE_DOWNLOADING}、{@link
   * MyGameDetailActivity#TYPE_UPDATE}、
   */
  public void turnMyGameDetail(Activity context, int type) {
    Intent intent = new Intent(context, MyGameDetailActivity.class);
    intent.putExtra(MyGameDetailActivity.TYPE_KEY, type);
    context.startActivityForResult(intent, MY_GAME_RESULT_CODE);
  }

  /**
   * 跳转游戏详情
   *
   * @param gameId 游戏Id
   */
  public void turnGameDetail(Context context, int gameId) {
    Intent intent = new Intent(context, GameDetailActivity.class);
    intent.putExtra(GameDetailActivity.KEY_GAME_ID, gameId);
    if (context instanceof BaseActivity) {
      ((BaseActivity) context).startActivityForResult(intent, PAY_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * OverLoad
   * 跳转游戏详情    各个模块跳转游戏详情页面为了后台统计
   *
   * @param type 跳转类型 1-热门推荐  2-套餐包跳转
   * @param gameId 游戏Id
   */
  public void turnGameDetail(Context context, int gameId, int type) {
    Intent intent = new Intent(context, GameDetailActivity.class);
    intent.putExtra(GameDetailActivity.KEY_GAME_ID, gameId);
    intent.putExtra(GameDetailActivity.TYPE_ID, type);
    if (context instanceof BaseActivity) {
      ((BaseActivity) context).startActivityForResult(intent, PAY_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * 套餐包跳转
   *
   * @param packageId 套餐包Id
   */
  public void turnPackage(Context context, String packageId) {
    Intent intent = new Intent(context, MothlyPayActivity.class);
    intent.putExtra(MothlyPayActivity.KEY_PACKAGE_ID, packageId);
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, TurnManager.PAY_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * 套餐包跳转
   *
   * @param packageId 套餐包Id
   * @param isBuy 是否购买
   */
  public void turnPackage(Context context, String packageId, boolean isBuy) {
    Intent intent = new Intent(context, MothlyPayActivity.class);
    intent.putExtra(MothlyPayActivity.KEY_PACKAGE_ID, packageId);
    if (isBuy) {
      intent.putExtra(MothlyPayActivity.KEY_IS_BUY, isBuy);
    }
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, TurnManager.PAY_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * 套餐包跳转
   *
   * @param packageId 套餐包Id
   * @param turnType 返回跳转类型
   */
  public void turnPackage(Context context, String packageId, int turnType) {
    Intent intent = new Intent(context, MothlyPayActivity.class);
    intent.putExtra(MothlyPayActivity.KEY_PACKAGE_ID, packageId);
    intent.putExtra(MothlyPayActivity.KEY_BACK_TYPE, turnType);
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, TurnManager.PAY_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * 套餐包游戏跳转套餐包
   *
   * @param packageId 套餐包Id
   * gameId     游戏Id
   */
  public void turnPackage(Context context, String packageId, String gameId) {
    Intent intent = new Intent(context, MothlyPayActivity.class);
    intent.putExtra(MothlyPayActivity.KEY_PACKAGE_ID, packageId);
    intent.putExtra(MothlyPayActivity.KEY_GAME_ID, gameId);
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, TurnManager.PAY_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * 跳转游戏大厅 遥控器、手柄二级分类
   *
   * @param mode {@link GameClassifyActivity#TYPE_MODE_SB}、{@link GameClassifyActivity#TYPE_MODE_YKQ}
   */
  public void turnClassifyMode(Context context, int mode, String typeName) {
    Intent intent = new Intent(context, GameClassifyActivity.class);
    intent.putExtra(GameClassifyActivity.TYPE_MODE_KEY, mode);
    intent.putExtra(GameClassifyActivity.TYPR_NAME, typeName);
    context.startActivity(intent);
  }

  /**
   * 跳转游戏大厅 其他分类
   */
  public void turnClassifyTag(Context context, int typeId, String typeName) {
    Intent intent = new Intent(context, GameClassifyActivity.class);
    intent.putExtra(GameClassifyActivity.TYPE_TAG_KEY, typeId);
    intent.putExtra(GameClassifyActivity.TYPR_NAME, typeName);
    context.startActivity(intent);
  }

  /**
   * 跳转签到
   */
  public void turnSign(Context context) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SignEntity>() {
    }.getType(), new BasicDeserializer<SignEntity>()).create();
    NetManager.getInstance()
        .request(LauncherApi.class, gson)
        .getAwardList("1")
        .compose(new HttpCallback<SignEntity>() {
          @Override public void onResponse(SignEntity response) {
            if (response != null && response.list != null && response.list.size() > 0) {
              SignDialog signDialog = new SignDialog(context, response, 0);
              signDialog.setOnSureClickListener(() -> signDialog.dismiss());
              signDialog.show();
            }
          }
        });
  }

  /**
   * 跳转签到  重载
   */
  public void turnSign(Context context, int turnType, String packageId) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SignEntity>() {
    }.getType(), new BasicDeserializer<SignEntity>()).create();
    NetManager.getInstance()
        .request(LauncherApi.class, gson)
        .getAwardList("1")
        .compose(new HttpCallback<SignEntity>() {
          @Override public void onResponse(SignEntity response) {
            if (response != null && response.list != null && response.list.size() > 0) {
              SignDialog signDialog = new SignDialog(context, response, 0);
              //signDialog.setOnSureClickListener(() -> signDialog.dismiss());
              signDialog.setOnSureClickListener(() -> {
                if (turnType == ActivityManager.TURN_TYPE_PACKAGE) {
                  turnPackage(context, packageId);
                }
                signDialog.dismiss();
              });
              signDialog.show();
            }
          }

          @Override public void onFailure(Throwable e) {
            if (turnType == ActivityManager.TURN_TYPE_PACKAGE) {
              turnPackage(context, packageId);
            }
          }
        });
  }

  /**
   * 跳转活动：砸蛋，抽奖
   */
  public void turnDrawAward(Context context, int turnType, String packageId) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<LotteryEntity>() {
    }.getType(), new BasicDeserializer<LotteryEntity>()).create();
    NetManager.getInstance()
        .request(LotteryApi.class, gson)
        .getLotteryImgList()
        .compose(new HttpCallback<LotteryEntity>() {
          @Override public void onResponse(LotteryEntity response) {
            if (response.list != null && response.list.size() > 0) {
              startDrawAwardActivity(context, response, turnType, packageId);
            }
          }
        });
  }

  /**
   * 跳转活动：砸蛋，抽奖
   */
  public void turnDrawAward(Context context, int turnType, String packageId, boolean isBuy) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<LotteryEntity>() {
    }.getType(), new BasicDeserializer<LotteryEntity>()).create();
    NetManager.getInstance()
        .request(LotteryApi.class, gson)
        .getLotteryImgList()
        .compose(new HttpCallback<LotteryEntity>() {
          @Override public void onResponse(LotteryEntity response) {
            if (response.list != null && response.list.size() > 0) {
              startDrawAwardActivity(context, response, turnType, packageId, isBuy);
            } else if ((response.list == null || response.list.size() == 0)
                && turnType == ActivityManager.TUEN_TYPE_EPG) {
              if (context instanceof Activity) {
                context.startActivity(new Intent(context, MainActivity.class));
              }
            }
          }
        });
  }

  /**
   * 跳转活动：砸蛋，抽奖
   */
  public void turnDrawAward(Context context) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<LotteryEntity>() {
    }.getType(), new BasicDeserializer<LotteryEntity>()).create();
    NetManager.getInstance()
        .request(LotteryApi.class, gson)
        .getLotteryImgList()
        .compose(new HttpCallback<LotteryEntity>() {
          @Override public void onResponse(LotteryEntity response) {
            if (response.list != null && response.list.size() > 0) {
              startDrawAwardActivity(context, response);
            }
          }
        });
  }

  /**
   * 启动抽奖界面
   */
  private void startDrawAwardActivity(Context context, LotteryEntity response, int turnType,
      String packageId) {
    Intent intent = new Intent(context, DrawAwardActivity.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable(DrawAwardActivity.LOTTERY_IMG_LIST_KEY, response);
    bundle.putInt(ActivityManager.TYPE_KEY, turnType);
    bundle.putString(ActivityManager.PACKAGE_KEY, packageId);
    intent.putExtras(bundle);
    //context.startActivity(intent);
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, TurnManager.DRAW_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * 启动抽奖界面
   *
   * @param isBuy 是否购买套餐包
   */
  private void startDrawAwardActivity(Context context, LotteryEntity response, int turnType,
      String packageId, boolean isBuy) {
    Intent intent = new Intent(context, DrawAwardActivity.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable(DrawAwardActivity.LOTTERY_IMG_LIST_KEY, response);
    bundle.putInt(ActivityManager.TYPE_KEY, turnType);
    bundle.putBoolean(ActivityManager.PACKAGE_ISBUY_KEY, isBuy);
    bundle.putString(ActivityManager.PACKAGE_KEY, packageId);
    intent.putExtras(bundle);
    //context.startActivity(intent);
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, TurnManager.DRAW_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * 启动抽奖界面
   */
  private void startDrawAwardActivity(Context context, LotteryEntity response) {
    Intent intent = new Intent(context, DrawAwardActivity.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable(DrawAwardActivity.LOTTERY_IMG_LIST_KEY, response);
    bundle.putBoolean(ActivityManager.PACKAGE_ISBUY_KEY, true);
    intent.putExtras(bundle);
    //context.startActivity(intent);
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, TurnManager.DRAW_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * metro跳转
   */
  public void turnMetro(Context context, MetroItemEntity entity) {
    MetroItemEntity.MetroBaseData baseData = null;
    switch (entity.type) {
      case MetroLayout.NORMAL_VERTICAL:
      case MetroLayout.NORMAL_HORIZONTAL:
      case MetroLayout.NORMAL_MAX_VERTICAL:
      case MetroLayout.NORMAL_SQUARE:
        baseData = entity.normalItemData;
        break;
      case MetroLayout.SIMPLE_HORIZONTAL:
      case MetroLayout.SIMPLE_VERTICAL:
      case MetroLayout.SIMPLE_SQUARE:
        baseData = entity.simpleItemData;
        break;
    }
    if (baseData != null) {
      switch (entity.turnType) {
        case MetroItemEntity.TURN_GAME:
          turnGameDetail(context, Integer.parseInt(baseData.id), 1);
          //上报apk用户日志，方便后台统计
          ActionManager.getInstance()
              .statisticsIntoGameDetail(1, Integer.parseInt(baseData.id),
                  EpgUserUtil.getUserEntity().getChannel());
          break;
        case MetroItemEntity.TURN_PACKAGE:
          turnPackage(context, baseData.id);
          break;
        case MetroItemEntity.TURN_SBY:
          turnSbyPackage(context, baseData.id);
          break;
        case MetroItemEntity.TURN_PACKAGE_ACTIVITY:
          ActivityManager.getInstance().popActivity(context, baseData.id, baseData.isBuy);
          break;
        case MetroItemEntity.TURN_ACTIVITY:
          context.startActivity(new Intent(context, NewEventActivity.class));
          break;
        case MetroItemEntity.TURN_SIGN:
          turnSign(context);
          break;
        case MetroItemEntity.TURN_ZA_DAN:
          turnDrawAward(context);
          break;
        case MetroItemEntity.TURN_VIDEO:

          //turnWebGameVideo(context);
          break;
        case MetroItemEntity.TURN_OTHER:
          break;
        case MetroItemEntity.TURN_GAME_HALL_TAG:
          //Intent intent = new Intent(context, GameClassifyActivity.class);
          //context.startActivity(intent);
          turnClassifyTag(context, Integer.parseInt(baseData.id), baseData.title);
          break;
        case MetroItemEntity.TURN_GAME_HALL_MODE_SB:
          turnClassifyMode(context, GameClassifyActivity.TYPE_MODE_SB, "手柄游戏");
          break;
        case MetroItemEntity.TURN_GAME_HALL_MODE_YKQ:
          turnClassifyMode(context, GameClassifyActivity.TYPE_MODE_YKQ, "遥控器游戏");
          break;
        case MetroItemEntity.TURN_CP_VIDEO:
          //TODO baseData.videoUrl 为CP视频地址
          break;
      }
    }
  }

  /**
   * 视博云安装跳转
   */
  public void turnSbyPackage(Context context, String id) {

    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SbyEntity>() {
    }.getType(), new BasicDeserializer<SbyEntity>()).create();
    NetManager.getInstance()
        .request(SbyPackageApi.class, gson)
        .getSbyInfo(id)
        .compose(new HttpCallback<SbyEntity>() {
          @Override public void onResponse(SbyEntity response) {
            if (AndroidUtils.isInstall(context, response.packName)) {
              //AndroidUtils.startOtherApp(context, response.packName);
              ApkUtil.startGame(context, response.packName);
            } else {
              SharePreUtil.putObject(KeyConstant.PRE_NAME, context, KeyConstant.KEY_SBY_ENTITY,
                  SbyEntity.class, response);
              File apk = new File(DownloadHelpUtil.getSBYApkPath());
              SbyEntity sbyEntity =
                  SharePreUtil.getObject(KeyConstant.PRE_NAME, context, KeyConstant.KEY_SBY_ENTITY,
                      SbyEntity.class);
              if (sbyEntity != null && apk.exists() && FileUtil.checkMD5(sbyEntity.md5, apk)) {
                //T.showShort(context, "您需要安装应用");
                //AndroidUtils.install(context, apk);
                ApkUtil.installApk(context, apk.getPath());
                SharePreUtil.removeKey(KeyConstant.PRE_NAME, context, KeyConstant.KEY_SBY_ENTITY);
                return;
              }
              T.showShort(context, "正在下载安装应用,请耐心等待...");
              Aria.whit(context)
                  .load(response.downloadUrl)
                  .setDownloadPath(DownloadHelpUtil.getSBYApkPath())
                  .setDownloadName(DownloadHelpUtil.SBY_FILE_NAME)
                  .start();
            }
          }
        });
  }
}
