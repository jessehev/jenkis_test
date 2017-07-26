package com.utstar.appstoreapplication.activity.windows.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.arialyy.frame.util.CalendarUtils;
import com.arialyy.frame.util.StringUtil;
import com.arialyy.frame.util.show.T;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ut.wb.ui.TabLayout.TabEntity;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.net_entity.AdEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.AnnouncementEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.AutoSignEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SignEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SignHintEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.ActivityApi;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.LauncherApi;
import com.utstar.appstoreapplication.activity.manager.ActivityManager;
import com.utstar.appstoreapplication.activity.manager.NetManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.windows.activity.advertise.AdDialog;
import com.utstar.appstoreapplication.activity.windows.activity.announcement.AnnouncementDialog;
import com.utstar.appstoreapplication.activity.windows.activity.sign.SignDeserializer;
import com.utstar.appstoreapplication.activity.windows.activity.sign.SignDialog;
import com.utstar.appstoreapplication.activity.windows.activity.sign.TipsDialog;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import com.utstar.appstoreapplication.activity.windows.common.MsgDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Aria.Lao on 2016/12/9.
 */
final class MainModule extends BaseModule {
  TurnManager turnManager = TurnManager.getInstance();

  public MainModule(Context context) {
    super(context);
  }

  /**
   * 定时获取wifi信号强弱
   */
  void setWifiState(ImageView img) {
    Observable.interval(0, 1, TimeUnit.MINUTES)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(aLong -> {
          if (!wifiIsConnect()) {
            img.setVisibility(View.GONE);
            return;
          } else {
            img.setVisibility(View.VISIBLE);
          }
          @SuppressLint("WifiManagerLeak") WifiManager wifiManager =
              (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
          WifiInfo wifiInfo = wifiManager.getConnectionInfo();
          int level = wifiInfo.getRssi();
          //根据获得的信号强度发送信息
          if (level <= 0 && level >= -20) {
            img.setImageResource(R.mipmap.icon_wifi_1);
          } else if (level < -20 && level >= -30) {
            img.setImageResource(R.mipmap.icon_wifi_2);
          } else if (level < -30 && level >= -50) {
            img.setImageResource(R.mipmap.icon_wifi_3);
          } else if (level < -50 && level >= -100) {
            img.setImageResource(R.mipmap.icon_wifi_4);
          } else {
            img.setImageResource(R.mipmap.icon_wifi);
          }
        });
  }

  /**
   * 获取wifi连接状态
   */
  boolean wifiIsConnect() {
    ConnectivityManager connManager =
        (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    return wifi.getType() != ConnectivityManager.TYPE_ETHERNET && wifi.isConnected();
  }

  /**
   * 定时设置时间
   */
  void setTime(final TextView text) {
    Observable.interval(0, 1, TimeUnit.MINUTES)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(aLong -> {
          text.setText(CalendarUtils.getShortTime());
        });
  }

  /**
   * 创建tab
   */
  List<TabEntity> createTab() {
    List<TabEntity> tabs = new ArrayList<>();
    String[] strs = StringUtil.getStringArrayFromXML(getContext(), R.array.tabs);
    int i = 0;
    for (String str : strs) {
      TabEntity entity = new TabEntity();
      entity.tabName = str;
      entity.tabId = i;
      tabs.add(entity);
    }
    return tabs;
  }

  /**
   * 显示广告
   */
  private void showAd() {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<AdEntity>() {
    }.getType(), new BasicDeserializer<AdEntity>()).create();

    mNetManager.request(LauncherApi.class, gson).getAdList().compose(new HttpCallback<AdEntity>() {
      @Override public void onResponse(AdEntity response) {
        if (response != null && response.img != null && response.img.size() > 0) {
          //显示广告
          AdDialog adDialog = new AdDialog(getContext(), response);
          adDialog.setOnDismissListener(dialog -> ActivityManager.getInstance()
              .getPackageData(getContext(), ActivityManager.SWICH_ON));
          adDialog.show();
        } else {
          //TODO 弹出半价优惠
          ActivityManager.getInstance().getPackageData(getContext(), ActivityManager.SWICH_ON);
        }
      }

      @Override public void onFailure(Throwable e) {
        ActivityManager.getInstance().getPackageData(getContext(), ActivityManager.SWICH_ON);
      }
    });
  }

  /**
   * 显示公告
   */
  void showAnnouncement() {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<AnnouncementEntity>() {
    }.getType(), new BasicDeserializer<AnnouncementEntity>()).create();
    mNetManager.request(LauncherApi.class, gson)
        .getAnnouncement()
        .compose(new HttpCallback<AnnouncementEntity>() {
          @Override public void onResponse(AnnouncementEntity response) {
            if (response == null || TextUtils.isEmpty(response.content)) {
              //显示广告
              showAd();
              return;
            }
            if (response.count.equals("0")) {   //count =0  说明公告未显示
              // 显示显示公告
              AnnouncementDialog anDialog = new AnnouncementDialog(getContext(), response);
              anDialog.setOnDismissListener(dialog -> showAd());
              anDialog.show();
            } else {  //已经显示过了，直接显示广告
              //显示广告
              showAd();
            }
            //L.d("公告==>response=" + response.toString());
          }

          @Override public void onFailure(Throwable e) {
            showAd();
          }
        });
  }

  /**
   * 区分活动是平台还是套餐包
   */
  public void diffActivity() {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<String>() {
    }.getType(), new BasicDeserializer<String>()).create();
    NetManager.getInstance()
        .request(ActivityApi.class, gson)
        .diffActivity()
        .compose(new HttpCallback<String>() {
          @Override public void onResponse(String response) {
            //     ruletype = 1 套餐包， ruletype = 2 平台    ruletype = 0 什么都没配
            if (response.equals("2")) {
              //  显示签到
              autoSign();
            }
          }
        });
  }

  /**
   * 自动签到  先签到，再获取签到数据并显示
   */
  private void autoSign() {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<AutoSignEntity>() {
    }.getType(), new SignDeserializer<AutoSignEntity>()).create();
    mNetManager.request(LauncherApi.class, gson).sign().compose(new HttpCallback<AutoSignEntity>() {
      @Override public void onResponse(AutoSignEntity response) {
        if (response.succeed == 1) {
          getAwardList(response.succeed);
        } else {
          //显示抽奖活动
          turnManager.turnDrawAward(getContext());
        }
      }
    });
  }

  /**
   * 获取签到奖品列表
   */
  private void getAwardList(int isSigned) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SignEntity>() {
    }.getType(), new BasicDeserializer<SignEntity>()).create();
    mNetManager.request(LauncherApi.class, gson)
        .getAwardList("1")
        .compose(new HttpCallback<SignEntity>() {
          @Override public void onResponse(SignEntity response) {
            if (response != null && response.list != null && response.list.size() > 0) {
              SignDialog signDialog = new SignDialog(getContext(), response, isSigned);
              signDialog.setOnDismissListener(dialog -> {
                // 显示砸蛋
                turnManager.turnDrawAward(getContext());
              });
              signDialog.setOnSureClickListener(() -> {
                if (isSigned == 1) {
                  showSignHint(signDialog);
                } else {
                  signDialog.dismiss();
                }
              });
              signDialog.show();
              // 弹出签到页面后3s，如果中奖则显示领奖信息， 领奖框消失后3s取消签到页面
              if (isSigned == 1) {
                new Handler().postDelayed(() -> showSignHint(signDialog), 3000);
              }
            } else {
              // 显示砸蛋
              turnManager.turnDrawAward(getContext());
            }
          }

          @Override public void onFailure(Throwable e) {
            //TODO 显示砸蛋
            turnManager.turnDrawAward(getContext());
          }
        });
  }

  /**
   * 显示领奖信息框
   */
  private void showSignHint(SignDialog signDialog) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SignHintEntity>() {
    }.getType(), new BasicDeserializer<SignHintEntity>()).create();
    mNetManager.request(LauncherApi.class, gson)
        .getSignInfo(1)
        .compose(new HttpCallback<SignHintEntity>() {
          @Override public void onResponse(SignHintEntity response) {
            if (response != null && response.flag.equals("1")) {  //1 中奖  0未中奖
              TipsDialog shDialog = new TipsDialog(getContext(), response.remarks);
              shDialog.setOnEnterClickLister(() -> handleCommit(signDialog, shDialog, response));
              shDialog.show();
            } else {
              signDialog.dismiss();
            }
          }

          @Override public void onFailure(Throwable e) {
            signDialog.dismiss();
          }
        });
  }

  /**
   * 处理提交业务
   */
  private void handleCommit(SignDialog signDialog, TipsDialog shDialog, SignHintEntity response) {
    String content = shDialog.getEditText();
    if (TextUtils.isEmpty(content)) {
      content = "";
    }
    shDialog.dismiss(); //可以防止用户重复点击
    commitContent(signDialog, response.id, content);
    //delayCancleDialog(signDialog, 3000);
  }

  /**
   * 提交领奖信息框输入内容
   */
  private void commitContent(SignDialog signDialog, String id, String content) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<SignHintEntity>() {
    }.getType(), new BasicDeserializer<SignHintEntity>()).create();
    mNetManager.request(LauncherApi.class, gson)
        .commitInfo(id, content, 1)
        .compose(new HttpCallback<SignHintEntity>() {
          @Override public void onResponse(SignHintEntity response) {
            //if (response.success == 1) {
            //  //   showToastRes(R.string.hint_success);
            //  MsgDialog msgDialog =
            //      new MsgDialog(getContext(), "恭喜您", "感谢您的支持，我们将在5个工作日内联系您领奖！", false);
            //  msgDialog.setDialogCallback(new MsgDialog.OnMsgDialogCallback() {
            //    @Override public void onEnter() {
            //      delayCancleDialog(signDialog, 3000);
            //    }
            //
            //    @Override public void onCancel() {
            //
            //    }
            //  });
            //  msgDialog.show();
            //} else {
            //  delayCancleDialog(signDialog, 3000);
            //}
            delayCancleDialog(signDialog, 3000);
          }

          @Override public void onFailure(Throwable e) {
            delayCancleDialog(signDialog, 3000);
          }
        });
  }



  /**
   * 延迟取消dialog
   */
  private void delayCancleDialog(Dialog dialog, long time) {
    new Handler().postDelayed(() -> {
      if (dialog.isShowing()) {
        dialog.dismiss();
      }
    }, time);
  }
}
