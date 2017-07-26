package com.utstar.appstoreapplication.activity.windows.activity.draw_award;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.AdverEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.AwardListEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.DrawAwardEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.DrawAwardIconEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.LotteryEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SignHintEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.LauncherApi;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.LotteryApi;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.manager.NetManager;
import com.utstar.appstoreapplication.activity.windows.activity.sign.TipsDialog;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import com.utstar.appstoreapplication.activity.windows.common.MsgDialog;
import java.util.Calendar;
import java.util.List;

/**
 * Created by JesseHev on 2017/1/12.
 */

public class DrawAwardModule extends BaseModule {

  private static final int MIN_CLICK_DELAY_TIME = 2000;
  private long lastClickTime = 0;

  public DrawAwardModule(Context context) {

    super(context);
  }

  /**
   * 是否是连续点击
   */
  public boolean isFastDoubleClick() {
    long currentTime = Calendar.getInstance().getTimeInMillis();
    if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
      lastClickTime = currentTime;
      return false;
    }
    return true;
  }

  /**
   * 中奖用户及奖品信息
   */
  public void getLotteryUserList() {
    Gson gson =
        new GsonBuilder().registerTypeAdapter(new TypeToken<NetListContainerEntity<AdverEntity>>() {
        }.getType(), new BasicDeserializer<NetListContainerEntity<AdverEntity>>()).create();
    mNetManager.request(LotteryApi.class, gson)
        .getLotteryUserList()
        .compose(new HttpCallback<NetListContainerEntity<AdverEntity>>() {
          @Override public void onResponse(NetListContainerEntity<AdverEntity> response) {
            callback(DrawAwardActivity.LOTTERY_USER_LIST_RESULT, response.list);
          }
        });
  }

  /**
   * 奖品列表
   */

  public void getLotteryAwardList() {
    Gson gson = new GsonBuilder().registerTypeAdapter(
        new TypeToken<NetListContainerEntity<AwardListEntity>>() {
        }.getType(), new BasicDeserializer<NetListContainerEntity<AwardListEntity>>()).create();

    mNetManager.request(LotteryApi.class, gson)
        .getLotteryAwardList()
        .compose(new HttpCallback<NetListContainerEntity<AwardListEntity>>() {
          @Override public void onResponse(NetListContainerEntity<AwardListEntity> response) {
            callback(DrawAwardActivity.LOTTERY_AWARD_LIST_RESULT, response.list);
          }
        });
  }

  /**
   * 抽奖
   */
  public void drawAward() {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<DrawAwardEntity>() {
    }.getType(), new BasicDeserializer<DrawAwardEntity>()).create();

    mNetManager.request(LotteryApi.class, gson)
        .drawAward()
        .compose(new HttpCallback<DrawAwardEntity>() {
          @Override public void onResponse(DrawAwardEntity response) {
            callback(DrawAwardActivity.LOTTERY_DRAW_AWARD_RESULT, response);
          }
        });
  }

  /**
   * 开启抽奖动画
   */

  public void startAnima(DrawAwardIconEntity iconEntity, Handler handler, ImageView bg,
      ImageView hammer, TextView num) {

    RotateAnimation animation =
        new RotateAnimation(0f, 80f, Animation.RELATIVE_TO_SELF, 0.9f, Animation.RELATIVE_TO_SELF,
            0.9f);
    animation.setDuration(500);
    hammer.startAnimation(animation);

    handler.postDelayed(() -> {
      if (!TextUtils.isEmpty(iconEntity.imageType5)) {
        bg.setImageResource(R.mipmap.icon_egg_10);
        //ImageManager.getInstance().setImg(bg, iconEntity.imageType5);
      }
    }, 600);
    //  handler.postDelayed(() -> hammer.animate().rotation(0f).setDuration(200).start(), 1100);
    handler.postDelayed(() -> {
      ImageManager.getInstance().clear(bg);
      ImageManager.getInstance().setImg(bg, iconEntity.imageType6);
      hammer.setVisibility(View.GONE);
      showToast(num);
    }, 1100);
  }

  /**
   * 更新提示抽奖次数
   */
  public void showToast(TextView lotteryNum) {
    String str = lotteryNum.getText().toString();
    int parseInt = Integer.parseInt(str);
    parseInt--;
    if (parseInt < 0) {
      parseInt = 0;
    }
    lotteryNum.setText(parseInt + "");
  }

  /**
   * 获取当前抽奖次数
   */
  public int getToastNum(TextView lotteryNum) {
    String str = lotteryNum.getText().toString();
    return Integer.parseInt(str);
  }

  public void formartString(Context context, TextView tv, String num) {
    String format = context.getResources().getString(R.string.lottery_num);
    String result = String.format(format, num);
    tv.setText(result);
  }

  /**
   * 抽奖次数不足 显示系统弹框
   */
  public void showDefautDialog(Context context) {
    MsgDialog dialog =
        new MsgDialog(context, "系统提示", getString(context, R.string.lottery_message), false);
    dialog.show();
  }

  /**
   * 获取字符串
   */
  public String getString(Context context, int strId) {
    return context.getResources().getString(strId);
  }

  /**
   * 显示中奖用户登记信息框
   */
  public void showSignHint(Context context) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SignHintEntity>() {
    }.getType(), new BasicDeserializer<SignHintEntity>()).create();
    NetManager.getInstance()
        .request(LauncherApi.class, gson)
        .getSignInfo(2)//type=1 签到活动  type=2抽奖活动
        .compose(new HttpCallback<SignHintEntity>() {
          @Override public void onResponse(SignHintEntity response) {
            if (response != null && response.flag.equals("1")) {  //1 中奖  0未中奖
              TipsDialog shDialog = new TipsDialog(context, response.remarks);
              shDialog.setOnEnterClickLister(() -> {
                shDialog.dismiss();
                String str = shDialog.getEditText();
                if (TextUtils.isEmpty(str)) {
                  str = "";
                }
                commitContent(context, response.id, str);
              });
              shDialog.show();
            }
          }

          @Override public void onFailure(Throwable e) {

          }
        });
  }

  /**
   * 提交领奖信息框输入内容
   */
  private void commitContent(Context context, String id, String content) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SignHintEntity>() {
    }.getType(), new BasicDeserializer<SignHintEntity>()).create();
    NetManager.getInstance()
        .request(LauncherApi.class, gson)
        .commitInfo(id, content, 2)//type=1 签到活动  type=2抽奖活动
        .compose(new HttpCallback<SignHintEntity>() {
          @Override public void onResponse(SignHintEntity response) {
            if (response.success == 1) {
              //MsgDialog msgDialog = new MsgDialog(context, "恭喜您", "感谢您的支持，我们将在5个工作日内联系您领奖！", false);
              //msgDialog.show();
            }
          }

          @Override public void onFailure(Throwable e) {

          }
        });
  }

  /**
   * 处理图片信息
   */
  public DrawAwardIconEntity handleImg(List<LotteryEntity.LotterImageEntity> list) {
    DrawAwardIconEntity aaIcon = new DrawAwardIconEntity();
    for (int i = 0; i < list.size(); i++) {
      LotteryEntity.LotterImageEntity lotterImageEntity = list.get(i);
      switch (lotterImageEntity.type) {
        case "1":
          aaIcon.imageType1 = lotterImageEntity.address;
          break;
        case "2":
          aaIcon.imageType2 = lotterImageEntity.address;
          break;
        case "3":
          aaIcon.imageType3 = lotterImageEntity.address;
          break;
        case "4":
          aaIcon.imageType4 = lotterImageEntity.address;
          break;
        case "5":
          aaIcon.imageType5 = lotterImageEntity.address;
          break;
        case "6":
          aaIcon.imageType6 = lotterImageEntity.address;
          break;
        case "7":
          aaIcon.imageType7 = lotterImageEntity.address;
          break;
        case "8":
          aaIcon.imageType8 = lotterImageEntity.address;
          break;
        case "9":
          aaIcon.imageType9 = lotterImageEntity.address;
          break;
        case "10":
          aaIcon.imageType10 = lotterImageEntity.address;
          break;
        case "11":
          aaIcon.imageType11 = lotterImageEntity.address;
          break;
        case "12":
          aaIcon.imageType12 = lotterImageEntity.address;
          break;
        case "13":
          aaIcon.imageType13 = lotterImageEntity.address;
          break;
        default:
          break;
      }
    }
    return aaIcon;
  }
}