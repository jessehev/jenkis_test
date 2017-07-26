package com.utstar.appstoreapplication.activity.windows.launcher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.orm.DbUtil;
import com.arialyy.frame.core.AbsFrame;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.FileUtil;
import com.arialyy.frame.util.SharePreUtil;
import com.arialyy.frame.util.StringUtil;
import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.L;
import com.arialyy.frame.util.show.T;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.utstar.appstoreapplication.activity.BuildConfig;
import com.utstar.appstoreapplication.activity.Probe.ProbeService;
import com.utstar.appstoreapplication.activity.StartAppActivity;
import com.utstar.appstoreapplication.activity.commons.constants.CommonConstant;
import com.utstar.appstoreapplication.activity.commons.constants.KeyConstant;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetObjectEntity;
import com.utstar.appstoreapplication.activity.entity.db_entity.EpgEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.EPGParamsEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.EPGWbLocationEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.LauncherImgEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.UpdateVersionEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.WbUserEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.LauncherApi;
import com.utstar.appstoreapplication.activity.manager.ActivityManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.service.DownloadServer;
import com.utstar.appstoreapplication.activity.service.LogcatService;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import com.utstar.appstoreapplication.activity.utils.ApkSizeUtil;
import com.utstar.appstoreapplication.activity.utils.ApkUtil;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;
import com.utstar.appstoreapplication.activity.utils.EpgUserUtil;
import com.utstar.appstoreapplication.activity.utils.LauncherUtil;
import com.utstar.appstoreapplication.activity.utils.MacUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import com.utstar.appstoreapplication.activity.windows.main.MainActivity;
import com.utstar.appstoreapplication.activity.windows.payment.OrderInfoActivity;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;
import retrofit2.http.GET;

/**
 * Created by “Aria.Lao” on 2016/10/25. 启动模块
 */
public final class LauncherModule extends BaseModule<ViewDataBinding> {
  private static final String C_sEPG_SesseionID_KEY = "SESSIONID";
  private static final String C_sEPG_UserID_KEY = "USERID";
  private static final String C_sEPG_EPGServer_KEY = "EPGSERVER";
  private static final String C_sEPG_AreaID_KEY = "AREAID";
  private boolean isHaveImg = true;

  public LauncherModule(Context context) {
    super(context);
  }

  /**
   * launcher 启动流程开始
   *
   * @param intent EPG传递的Intent
   * @param img 启动图
   */
  public void start(Intent intent, ImageView img) {
    intServer();
    String areaId = "";
    EpgEntity userEntity = EpgUserUtil.getUserEntity();
    if (intent != null && !TextUtils.isEmpty(intent.getStringExtra(C_sEPG_UserID_KEY))) {
      String epgServer = intent.getStringExtra(C_sEPG_EPGServer_KEY);
      userEntity.setEpgServer(TextUtils.isEmpty(epgServer) ? EpgEntity.DEF_EPG_SERVER : epgServer);
      userEntity.setEpgSessionId(intent.getStringExtra(C_sEPG_SesseionID_KEY));
      userEntity.setEpgUserId(intent.getStringExtra(C_sEPG_UserID_KEY));
      areaId = intent.getStringExtra(C_sEPG_AreaID_KEY);
    }
    userEntity.setEpgAreAId(TextUtils.isEmpty(areaId) ? "999" : areaId);
    EpgUserUtil.saveUserEntity(userEntity);

    if (BuildConfig.DEBUG) {
      customEPGUserId(userEntity, img);
    } else {
      if (TextUtils.isEmpty(userEntity.getEpgUserId())) {
        getEpgUserId(img);
      } else {
        startFlow(img);
      }
    }

    String tinkerId =
        SharePreUtil.getString(KeyConstant.PRE_NAME, getContext(), KeyConstant.PRE.TINKER_ID);
    if (TextUtils.isEmpty(tinkerId)) {
      SharePreUtil.putString(KeyConstant.PRE_NAME, getContext(), KeyConstant.PRE.TINKER_ID,
          BuildConfig.TINKER_ID);
    }
    Aria.get(getContext()).openBroadcast(true);
    installRestartWanbaUtil();

    //getContext().startService(new Intent(getContext(), LogcatService.class));
  }

  /**
   * 安装重启玩吧的工具
   */
  private void installRestartWanbaUtil() {
    if (!AndroidUtils.isInstall(getContext(), CommonConstant.UPGRADE_PACKAGE_NAME)) {
      return;
    }
    File file = new File(DownloadHelpUtil.getWanBaApkPath());
    if (file.exists()) {
      file.delete();
    }
    if (!AndroidUtils.isInstall(getContext(), CommonConstant.RESTART_WANBA_UTIL_PKG)) {
      try {
        String name = "restart_wb.apk";
        String path = DownloadHelpUtil.getDownloadPath(getContext()) + name;
        FileUtil.createFileFormInputStream(getContext().getAssets().open(name), path);
        ApkUtil.installApk(getContext(), path);
      } catch (IOException e) {
        FL.e(TAG, FL.getExceptionString(e));
      }
    }
  }

  /**
   * 如果获取的EPG_USER_ID 为 null，则需要从服务器拿
   */
  private void getEpgUserId(ImageView img) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<EPGParamsEntity>() {
    }.getType(), new BasicDeserializer<EPGParamsEntity>()).create();
    mNetManager.request(LauncherApi.class, gson)
        .getEpgUserParams(LauncherUtil.getLocalEthernetMacAddress().toUpperCase())
        .compose(new HttpCallback<EPGParamsEntity>() {
          @Override public void onResponse(EPGParamsEntity response) {
            EpgEntity userEntity = EpgUserUtil.getUserEntity();
            userEntity.setEpgServer(response.EPG_SERVER);
            userEntity.setEpgSessionId(response.EPG_SESSION_ID);
            userEntity.setEpgUserId(response.EPG_USER_ID);
            String areaId = response.EPG_AREA_ID;
            userEntity.setEpgAreAId(TextUtils.isEmpty(areaId) ? "999" : areaId);
            EpgUserUtil.saveUserEntity(userEntity);
            startFlow(img);
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
            startFlow(img);
          }
        });
  }

  /**
   * 测试自己输入的EPG_USER_ID
   */
  private void customEPGUserId(EpgEntity userEntity, ImageView img) {
    final EditText et = new EditText(getContext());
    et.setText("000030");
    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(600, 100);
    et.setLayoutParams(lp);
    new AlertDialog.Builder(getContext()).setTitle("请输入epg_userid:")
        .setView(et)
        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
          userEntity.setEpgUserId(et.getText().toString());
          EpgUserUtil.saveUserEntity(userEntity);
          startFlow(img);
        })
        .setCancelable(false)
        .show();
  }

  private void startFlow(ImageView img) {
    new Handler().postDelayed(() -> {
      getLauncherImg(img);
      loginFlow();
      //}, 1000);
    }, 0);
  }

  /**
   * 获取启动图
   */
  private void getLauncherImg(ImageView img) {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<LauncherImgEntity>() {
    }.getType(), new BasicDeserializer<LauncherImgEntity>()).create();
    mNetManager.request(LauncherApi.class, gson)
        .getLauncherImg()
        .compose(new HttpCallback<LauncherImgEntity>() {
          @Override public void onResponse(LauncherImgEntity response) {
            //if (TextUtils.isEmpty(response.img)) {
            //  isHaveImg = false;
            //}
            loadLauncherImg(response.img, img);
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
            loadLauncherImg("", img);
          }
        });
  }

  /**
   * 加载启动图
   */
  private void loadLauncherImg(String imgUrl, ImageView img) {
    if (TextUtils.isEmpty(imgUrl)) {
      //Glide.with(getContext()).load(R.mipmap.bg_launcher).into(img);
    } else {
      if (img != null) {
        Glide.with(getContext()).load(imgUrl).into(img);
      }
    }
    //loginFlow();
  }

  /**
   * 开始登录流程
   */
  private void loginFlow() {
    EpgEntity user = EpgUserUtil.getUserEntity();
    if (!TextUtils.isEmpty(user.getWbAccount())) {
      login(user);
    } else {
      reg(user);
    }
  }

  /**
   * 用户登录
   */
  private void login(EpgEntity entity) {
    Map<String, String> params = new WeakHashMap<>();
    params.put("account", entity.getWbAccount());
    params.put("wbversion", AndroidUtils.getVersionCode(getContext()) + "");
    params.put("epg_userid", entity.getEpgUserId());
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<WbUserEntity>() {
    }.getType(), new BasicDeserializer<WbUserEntity>()).create();
    //mNetManager.request(LauncherApi.class, WbUs erEntity.class)
    mNetManager.request(LauncherApi.class, gson)
        .login(params)
        .compose(new HttpCallback<WbUserEntity>() {
          @Override public void onResponse(WbUserEntity response) {
            if (!TextUtils.isEmpty(response.account)) {
              entity.setWbAccount(response.account);
            }
            LauncherUtil.saveAccountAndChannel(getContext(), entity.getWbAccount());
            checkUpdate();
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
            LauncherUtil.saveAccountAndChannel(getContext(), entity.getWbAccount());
            checkUpdate();
          }
        });
  }

  /**
   * 用户注册
   */
  private void reg(EpgEntity entity) {
    Map<String, String> map = new WeakHashMap<>();
    final String channel = StringUtil.getApplicationMetaData(getContext(), "channel");
    final String uuid = LauncherUtil.getLocalMacAddress(getContext());
    entity.setChannel(channel);
    map.put("key", uuid);
    map.put("type", 1 + "");
    map.put("vspcode", channel);
    map.put("wbversion", AndroidUtils.getVersionCode(getContext()) + "");
    map.put("areaid", entity.getEpgAreAId());
    map.put("epg_userid", entity.getEpgUserId());

    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<WbUserEntity>() {
    }.getType(), new BasicDeserializer<WbUserEntity>()).create();

    mNetManager.request(LauncherApi.class, gson).reg(map).compose(new HttpCallback<WbUserEntity>() {
      @Override public void onResponse(WbUserEntity response) {
        if (!TextUtils.isEmpty(response.account)) {
          entity.setWbAccount(response.account);
        }
        LauncherUtil.saveAccountAndChannel(getContext(), entity.getWbAccount());
        checkUpdate();
      }

      @Override public void onFailure(Throwable e) {
        super.onFailure(e);
        LauncherUtil.saveAccountAndChannel(getContext(), entity.getWbAccount());
        checkUpdate();
      }
    });
  }

  private void uploadMemory() {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<String>() {
    }.getType(), new BasicDeserializer<String>()).create();
    mNetManager.request(LauncherApi.class, gson)
        .uploadCap(MacUtil.getCurStCap(getContext()) + "")
        .compose(new HttpCallback<NetObjectEntity<String>>() {
          @Override public void onResponse(NetObjectEntity<String> response) {

          }
        });
  }

  /**
   * 检查升级
   */
  private void checkUpdate() {
    uploadMemory();
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<UpdateVersionEntity>() {
    }.getType(), new BasicDeserializer<UpdateVersionEntity>()).create();
    String hotCode = null;
    try {
      hotCode =
          SharePreUtil.getString(KeyConstant.PRE_NAME, getContext(), KeyConstant.PRE.TINKER_ID);
    } catch (Exception e) {
      FL.e(TAG, "读不到tinkerID");
    }
    mNetManager.request(LauncherApi.class, gson)
        .checkUpdate(AndroidUtils.getVersionCode(getContext()),
            Integer.parseInt(TextUtils.isEmpty(hotCode) ? "0" : hotCode))
        .compose(new HttpCallback<UpdateVersionEntity>() {
          @Override public void onResponse(UpdateVersionEntity response) {
            if (response == null) {
              handleNormalUpdate(null);
              return;
            }
            if (response.isHotUpdate) {
              handleHotUpdate(response);
            } else {
              handleNormalUpdate(response);
            }
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
            checkStart();
          }
        });
  }

  /**
   * 热更新流程
   */
  private void handleHotUpdate(UpdateVersionEntity entity) {
    //new Handler().postDelayed(() -> {
    //  File path = new File(DownloadHelpUtil.getHotUpdateResPath());
    //  String oldTinkerId =
    //      SharePreUtil.getString(KeyConstant.PRE_NAME, getContext(), KeyConstant.PRE.TINKER_ID);
    //  if (TextUtils.isEmpty(oldTinkerId) || Integer.parseInt(oldTinkerId) < Integer.parseInt(
    //      entity.hotUpdateVersionCode)) {
    //    if (path.exists()) {
    //      if (FileUtil.checkMD5(entity.hotUpdateMd5, path)) {
    //        TinkerInstaller.onReceiveUpgradePatch(BaseApp.context,
    //            DownloadHelpUtil.getHotUpdateResPath());
    //      } else {
    //        downloadHotPath(entity);
    //      }
    //    } else {
    //      downloadHotPath(entity);
    //    }
    //  }
    //}, 2000);
    //checkStart();

    HotUpdateDialog dialog = new HotUpdateDialog(false, entity);
    dialog.show(((StartAppActivity) getContext()).getSupportFragmentManager(), "hotUpdateDialog");
  }

  private void downloadHotPath(UpdateVersionEntity entity) {
    SharePreUtil.putString(KeyConstant.PRE_NAME, getContext(), KeyConstant.PRE.TEMP_TINKER_ID,
        entity.hotUpdateVersionCode);
    SharePreUtil.putString(KeyConstant.PRE_NAME, getContext(), KeyConstant.PRE.TEMP_TINKER_MD5,
        entity.hotUpdateMd5);
    Aria.whit(getContext())
        .load(entity.hotUpdateUrl)
        .setDownloadPath(DownloadHelpUtil.getHotUpdateResPath())
        .setDownloadName(DownloadHelpUtil.HOT_UPDATE_FILE_NAME)
        .start();
  }

  /**
   * 正常更新流程
   */
  private void handleNormalUpdate(UpdateVersionEntity entity) {
    if (entity == null
        || TextUtils.isEmpty(entity.versionCode)
        || entity.versionCode.equalsIgnoreCase("null")) {
      checkStart();
    } else if (Integer.parseInt(entity.versionCode) > AndroidUtils.getVersionCode(getContext())) {
      UpdateDialog dialog = new UpdateDialog(entity);
      dialog.show(((StartAppActivity) getContext()).getSupportFragmentManager(), "update_dialog");
    } else {
      checkStart();
    }
  }

  /**
   * 检查启动
   */
  private void checkStart() {
    Intent intent = ((StartAppActivity) getContext()).getIntent();
    String str = intent.getStringExtra("type");
    if (!TextUtils.isEmpty(str)) {
      int type = Integer.parseInt(str);
      switch (type) {
        case CommonConstant.TURN_GAME_DETAIL:
          int gameId = Integer.parseInt(intent.getStringExtra("product_id"));
          TurnManager.getInstance().turnGameDetail(getContext(), gameId);
          AbsFrame.getInstance().finishActivity(StartAppActivity.class);
          break;
        case CommonConstant.TURN_GAME_ACTIVITY:
          TurnManager.getInstance()
              .turnDrawAward(getContext(), ActivityManager.TUEN_TYPE_EPG, "0", true);
          AbsFrame.getInstance().finishActivity(StartAppActivity.class);
          break;
        case CommonConstant.TURN_ORDER_ACTIVITY:
          String productId = intent.getStringExtra(CommonConstant.ORDER_ID_KEY);
          String serviceId = intent.getStringExtra(CommonConstant.CP_SERVICE_ID);
          String contentId = intent.getStringExtra(CommonConstant.CP_CONTENT_ID);
          String spId = intent.getStringExtra(CommonConstant.CP_SP_ID);
          String flowCode = intent.getStringExtra(CommonConstant.CP_FLOW_CODE);
          String cpPackageName = intent.getStringExtra(CommonConstant.CP_PACKAGE_NAME);
          TurnManager.getInstance()
              .cpTurnOrderDetail(getContext(), cpPackageName, productId, serviceId, contentId, spId,
                  flowCode);
          AbsFrame.getInstance().finishActivity(StartAppActivity.class);
          break;
        default:
          startWb();
          break;
      }
      useServiceGameId();
    } else {
      Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<EPGWbLocationEntity>() {
      }.getType(), new BasicDeserializer<EPGWbLocationEntity>()).create();
      mNetManager.request(LauncherApi.class, gson)
          .getEpgWbLocation(EpgUserUtil.getUserEntity().getEpgUserId())
          .compose(new HttpCallback<EPGWbLocationEntity>() {
            @Override public void onResponse(EPGWbLocationEntity response) {
              if (response == null) {
                startWb();
              } else {
                String str = Integer.toString(response.gameId);
                int type = Integer.parseInt(String.valueOf(str.charAt(0)));
                int id = 0;
                if (str.length() >= 2) {
                  id = Integer.parseInt(str.substring(1, str.length()));
                }
                if (type == 0) {
                  startWb();
                  return;
                }
                if (type == CommonConstant.TURN_GAME_DETAIL) {
                  TurnManager.getInstance().turnGameDetail(getContext(), id);
                  AbsFrame.getInstance().finishActivity(StartAppActivity.class);
                } else if (type == CommonConstant.TURN_GAME_ACTIVITY) {
                  TurnManager.getInstance()
                      .turnDrawAward(getContext(), ActivityManager.TUEN_TYPE_EPG, "0", true);
                  AbsFrame.getInstance().finishActivity(StartAppActivity.class);
                } else {
                  startWb();
                }
              }
            }

            @Override public void onFailure(Throwable e) {
              super.onFailure(e);
              startWb();
            }
          });
    }
  }

  /**
   * 消耗服务器的gameId
   */
  private void useServiceGameId() {
    Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<EPGWbLocationEntity>() {
    }.getType(), new BasicDeserializer<EPGWbLocationEntity>()).create();
    mNetManager.request(LauncherApi.class, gson)
        .getEpgWbLocation(EpgUserUtil.getUserEntity().getEpgUserId())
        .compose(new HttpCallback<EPGWbLocationEntity>() {
          @Override public void onResponse(EPGWbLocationEntity response) {
          }
        });
  }

  /**
   * 启动玩吧
   */
  private void startWb() {
    new Handler().postDelayed(() -> {
      Intent intent = new Intent(getContext(), MainActivity.class);
      getContext().startActivity(intent);
      AbsFrame.getInstance().finishActivity(StartAppActivity.class);
    }, BuildConfig.DEBUG ? 1000 : 2000);
    //new Handler().postDelayed(() -> {
    //  Intent intent = new Intent(getContext(), MainActivity.class);
    //  getContext().startActivity(intent);
    //  AbsFrame.getInstance().finishActivity(StartAppActivity.class);
    //}, isHaveImg ? 4000 : 0);
  }

  /**
   * 启动服务
   */
  private void intServer() {
    //LifeService.trigger(getContext());
    Intent intent = new Intent(getContext(), DownloadServer.class);
    getContext().startService(intent);
    Aria.get(BaseApp.application).setMaxDownloadNum(2);
    DbUtil.init(getContext());
  }
}
