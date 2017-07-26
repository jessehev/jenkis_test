package com.utstar.appstoreapplication.activity.windows.activity.new_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.net_entity.AutoSignEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.LotteryEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.NewEventListEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SignEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SignHintEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.LauncherApi;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.LotteryApi;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.NewEventApi;
import com.utstar.appstoreapplication.activity.manager.ActivityManager;
import com.utstar.appstoreapplication.activity.manager.NetManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.windows.activity.draw_award.DrawAwardActivity;
import com.utstar.appstoreapplication.activity.windows.activity.sign.SignDeserializer;
import com.utstar.appstoreapplication.activity.windows.activity.sign.SignDialog;
import com.utstar.appstoreapplication.activity.windows.activity.sign.TipsDialog;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import com.utstar.appstoreapplication.activity.windows.common.MsgDialog;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lt on 2016/12/19.
 */
class NewEventModule extends BaseModule {

  private final static int NEWEVENTGET_SUCCESS = 1001;
  private int page = 1;
  Gson mGson = new GsonBuilder().registerTypeAdapter(new TypeToken<NewEventListEntity>() {
  }.getType(), new BasicDeserializer<NewEventListEntity>()).create();
  Map<String, String> mParam = new HashMap<>();

  public NewEventModule(Context context) {
    super(context);
  }

  public void getData() {

    mParam.put("number", "4");
    mParam.put("page", page + "");
    mNetManager.request(NewEventApi.class, mGson)
        .getNewEvents(mParam)
        .compose(new HttpCallback<NewEventListEntity>() {
          @Override public void onResponse(NewEventListEntity response) {
            if (response != null) {
              callback(NEWEVENTGET_SUCCESS, response);
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
          }
        });
  }

  public void getPageData(int page) {
    mParam.put("number", "4");
    mParam.put("page", page + "");
    mNetManager.request(NewEventApi.class, mGson)
        .getNewEvents(mParam)
        .compose(new HttpCallback<NewEventListEntity>() {
          @Override public void onResponse(NewEventListEntity response) {
            if (response != null) {
              callback(NEWEVENTGET_SUCCESS, response);
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
          }
        });
  }

  /**
   * 显示抽奖活动
   */
  public void showDrawAward(Context context, boolean isBuy, String packageId) {
    getLotteryImgList(context, isBuy, packageId);
  }

  /**
   * 获取抽奖活动界面所有图片
   */
  private void getLotteryImgList(Context context, boolean isBuy, String packageId) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<LotteryEntity>() {
    }.getType(), new BasicDeserializer<LotteryEntity>()).create();
    mNetManager.request(LotteryApi.class, gson)
        .getLotteryImgList()
        .compose(new HttpCallback<LotteryEntity>() {
          @Override public void onResponse(LotteryEntity response) {
            if (response.list != null && response.list.size() > 0) {
              startDrawAwardActivity(context, response, isBuy, packageId);
            }
          }
        });
  }

  /**
   * 启动抽奖界面
   */
  private void startDrawAwardActivity(Context context, LotteryEntity response, boolean isBuy,
      String packageId) {
    Intent intent = new Intent(getContext(), DrawAwardActivity.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable(DrawAwardActivity.LOTTERY_IMG_LIST_KEY, response);
    bundle.putInt(ActivityManager.TYPE_KEY, ActivityManager.TURN_TYPE_ACTIVITY);
    bundle.putBoolean(ActivityManager.PACKAGE_ISBUY_KEY, isBuy);
    bundle.putString(ActivityManager.PACKAGE_KEY, packageId);
    intent.putExtras(bundle);
    if (context instanceof Activity) {
      ((Activity) context).startActivityForResult(intent, TurnManager.DRAW_REQUEST_CODE);
    } else {
      context.startActivity(intent);
    }
  }

  /**
   * 签到
   */
  public void autoSign(Context context) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<AutoSignEntity>() {
    }.getType(), new SignDeserializer<AutoSignEntity>()).create();
    NetManager.getInstance()
        .request(LauncherApi.class, gson)
        .sign()
        .compose(new HttpCallback<AutoSignEntity>() {
          @Override public void onResponse(AutoSignEntity response) {
            if (response.succeed == 1) {
              getAwardList(context, response.succeed); //更新签到状态
            } else {
              getAwardList(context, 0); //不更新签到状态，直显示UI
            }
          }
        });
  }

  /**
   * 显示签到页面
   *
   * @param context
   * @param packageId
   * @param isSigned
   * @param type
   */
  private int sign;

  private void getAwardList(Context context, int isSigned) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SignEntity>() {
    }.getType(), new BasicDeserializer<SignEntity>()).create();
    NetManager.getInstance()
        .request(LauncherApi.class, gson)
        .getAwardList("1")
        .compose(new HttpCallback<SignEntity>() {
          @Override public void onResponse(SignEntity response) {
            if (response != null && response.list != null && response.list.size() > 0) {
              sign = isSigned;
              SignDialog signDialog = new SignDialog(context, response, isSigned);
              signDialog.setOnSureClickListener(() -> {
                if (sign == 1) {
                  //显示领奖信息框
                  sign = 0;
                  showSignHint(context, signDialog);
                } else {
                  signDialog.dismiss();
                }
              });
              signDialog.show();
              //TODO 弹出签到页面后6s，如果中奖则显示领奖信息
              new Handler().postDelayed(() -> {
                if (sign == 1) {
                  sign = 0;
                  showSignHint(context, signDialog);
                } else {
                  signDialog.dismiss();
                }
              }, 6000);
            }
          }
        });
  }

  /**
   * 显示领奖信息框
   */
  private void showSignHint(Context context, SignDialog signDialog) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SignHintEntity>() {
    }.getType(), new BasicDeserializer<SignHintEntity>()).create();
    NetManager.getInstance()
        .request(LauncherApi.class, gson)
        .getSignInfo(1)//type=1 签到活动  type=2抽奖活动
        .compose(new HttpCallback<SignHintEntity>() {
          @Override public void onResponse(SignHintEntity response) {
            if (response != null && response.flag.equals("1")) {  //1 中奖  0未中奖
              TipsDialog shDialog = new TipsDialog(context, response.remarks);
              shDialog.setOnEnterClickLister(
                  () -> handleCommit(context, signDialog, shDialog, response));
              shDialog.show();
            }
          }
        });
  }

  /**
   * 处理提交业务
   */
  private void handleCommit(Context context, SignDialog signDialog, TipsDialog shDialog,
      SignHintEntity response) {
    String content = shDialog.getEditText();
    if (TextUtils.isEmpty(content)) {
      content = "";
    }
    shDialog.dismiss();
    signDialog.dismiss();
    commitContent(context, response.id, content);
  }

  /**
   * 提交领奖信息框输入内容
   */
  private void commitContent(Context context, String id, String content) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SignHintEntity>() {
    }.getType(), new BasicDeserializer<SignHintEntity>()).create();
    NetManager.getInstance()
        .request(LauncherApi.class, gson)
        .commitInfo(id, content, 1)//type=1 签到活动  type=2抽奖活动
        .compose(new HttpCallback<SignHintEntity>() {
          @Override public void onResponse(SignHintEntity response) {
            //if (response.success == 1) {
            //  MsgDialog msgDialog = new MsgDialog(context, "恭喜您", "感谢您的支持，我们将在5个工作日内联系您领奖！", false);
            //  msgDialog.show();
            //}
          }
        });
  }
}
