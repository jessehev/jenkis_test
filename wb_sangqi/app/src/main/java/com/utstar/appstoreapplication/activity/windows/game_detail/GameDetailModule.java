package com.utstar.appstoreapplication.activity.windows.game_detail;

import android.content.Context;
import android.content.IntentFilter;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.Button;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.DownloadEntity;
import com.arialyy.aria.core.DownloadManager;
import com.arialyy.aria.core.task.Task;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.FileUtil;
import com.arialyy.frame.util.StringUtil;
import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.L;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ut.wb.ui.Utils;
import com.utstar.appstoreapplication.activity.commons.constants.CommonConstant;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetObjectEntity;
import com.utstar.appstoreapplication.activity.entity.db_entity.UpdateInfo;
import com.utstar.appstoreapplication.activity.entity.net_entity.ActionEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameDetailEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameUninstallEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.GameManagerApi;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.LogAPi;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;
import com.utstar.appstoreapplication.activity.utils.MacUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import com.utstar.appstoreapplication.activity.windows.common.AuthUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/23.
 * 游戏详情数据模型
 */
final class GameDetailModule extends BaseModule<ViewDataBinding> {
  private boolean isTest = false;

  public GameDetailModule(Context context) {
    super(context);
  }

  /**
   * 获取卸载信息
   */
  void getUninstallInfo() {
    final Gson gson = new GsonBuilder().registerTypeAdapter(
        new TypeToken<NetListContainerEntity<GameUninstallEntity>>() {
        }.getType(), new BasicDeserializer<NetListContainerEntity<GameUninstallEntity>>()).create();
    mNetManager.request(GameManagerApi.class, gson)
        .getUninstallGame()
        .compose(new HttpCallback<NetListContainerEntity<GameUninstallEntity>>() {
          @Override public void onResponse(NetListContainerEntity<GameUninstallEntity> response) {
            if (response == null || response.list == null) {
              callback(GameDetailActivity.UNINSTALL_INFO, null);
              return;
            }
            callback(GameDetailActivity.UNINSTALL_INFO, filterUninstallGame(response.list));
          }

          @Override public void onFailure(Throwable e) {
            super.onFailure(e);
            callback(GameDetailActivity.UNINSTALL_INFO, null);
          }
        });
  }

  private List<GameUninstallEntity> filterUninstallGame(List<GameUninstallEntity> data) {
    List<GameUninstallEntity> filter = new ArrayList<>();
    for (GameUninstallEntity entity : data) {
      if (AndroidUtils.isInstall(getContext(), entity.packageName)) {
        filter.add(entity);
      }
    }
    return filter;
  }

  /**
   * 检测空间是否足够
   *
   * @return true 内存足够
   */
  boolean checkoutSpace(String apkSize) {
    return TextUtils.isEmpty(apkSize) || MacUtil.getCurStCap(getContext()) > Double.parseDouble(
        apkSize);
  }

  /**
   * 判断当前游戏大于剩余系统容量
   *
   * @return false //需要 true //不需要
   */
  boolean isNeedNoticeUninstallMsg() {
    return MacUtil.getCurStCap(getContext()) < CommonConstant.SPACE_MEMORY;
  }

  /**
   * 获取游戏详情
   */
  void getGameDetailData(int gameId, int type) {
    if (isTest) {
      getTestDate();
    } else {
      Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<GameDetailEntity>() {
      }.getType(), new GameDetailDeserializer()).create();
      mNetManager.request(GameManagerApi.class, gson)
          .getGameDetail(gameId, type)
          .compose(new HttpCallback<GameDetailEntity>() {

            @Override public void onResponse(GameDetailEntity response) {
              try {
                if (response == null) return;
                if (response.hasUpdate) {
                  saveUpdateInfo(response);
                }
                if (response.gameTag == 1) {
                  response.isBuy = true;
                  response.errorCode = "0";
                  callback(GameDetailActivity.GAME_DETAIL_RESULT, response);
                  return;
                }
                final AuthUtil util = new AuthUtil();
                util.auth(response.gameId, new AuthCallback(response));
              } catch (Exception e) {
                callback(GameDetailActivity.GAME_DETAIL_RESULT, response);
              }
            }

            @Override public void onFailure(Throwable e) {
              super.onFailure(e);
              callback(GameDetailActivity.GAME_DETAIL_RESULT, null);
            }
          });
    }
  }

  private void saveUpdateInfo(GameDetailEntity entity) {
    UpdateInfo info = new UpdateInfo();
    info.setAddPackage(entity.isAddPackage);
    info.setDownloadUrl(entity.downloadUrl);
    info.setGameIcon(entity.imgUrl);
    info.setGameName(entity.gameName);
    info.setGameId(entity.gameId);
    info.setMd5Code(entity.apkMd5Code);
    info.setPackageName(entity.packageName);
    info.saveIfNotExist("downloadUrl = ?", entity.downloadUrl);
  }

  /**
   * 上报下载日志
   */
  void uploadLog(int gameId) {
    Gson gson =
        new GsonBuilder().registerTypeAdapter(new TypeToken<NetObjectEntity<ActionEntity>>() {
        }.getType(), new BasicDeserializer<NetObjectEntity<ActionEntity>>()).create();
    mNetManager.request(LogAPi.class, gson)
        .apkAction(gameId, CommonConstant.ACTION_DOWNLOAD)
        .compose(new HttpCallback<NetObjectEntity<ActionEntity>>() {
          @Override public void onResponse(NetObjectEntity<ActionEntity> response) {
            L.d("日志上报成功，gameId = " + gameId);
          }
        });
  }

  /**
   * 获取下载数
   */
  CharSequence getDownloadNumText(int downloadNum) {
    String num = Utils.downloadCountHelp(downloadNum);
    String downloadText = "下载：" + num + "次";
    return StringUtil.highLightStr(downloadText, num + "", Color.parseColor("#edc900"));
  }

  /**
   * 创建过滤器
   */
  IntentFilter createDownloadFilter() {
    IntentFilter filter = new IntentFilter();
    filter.addDataScheme(getContext().getPackageName());
    filter.addAction(Aria.ACTION_PRE);
    filter.addAction(Aria.ACTION_POST_PRE);
    filter.addAction(Aria.ACTION_RESUME);
    filter.addAction(Aria.ACTION_START);
    filter.addAction(Aria.ACTION_RUNNING);
    filter.addAction(Aria.ACTION_STOP);
    filter.addAction(Aria.ACTION_CANCEL);
    filter.addAction(Aria.ACTION_COMPLETE);
    filter.addAction(Aria.ACTION_FAIL);
    return filter;
  }

  /**
   * 获取下载状态
   */
  int getDownloadState(String downloadUrl) {
    DownloadEntity entity = getDownloadEntity(downloadUrl);
    return entity == null ? DownloadEntity.STATE_WAIT : entity.getState();
  }

  /**
   * 获取下载实体
   */
  DownloadEntity getDownloadEntity(String downloadUrl) {
    return Aria.get(getContext()).getDownloadEntity(downloadUrl);
  }

  /**
   * 获取下载按钮文字
   */
  synchronized String setDownloadBtInfo(Button bt, GameDetailEntity detailEntity) {
    String text = "";
    if (detailEntity == null) {
      return "购买";
    }
    bt.setEnabled(true);

    if (detailEntity.isShelves) {
      text = "已下架";
      bt.setText(text);
      bt.setEnabled(false);
      bt.setFocusable(false);
      return text;
    }

    if (!detailEntity.isBuy) {
      text = "购买";
      bt.setText("购买");
      return text;
    }

    DownloadEntity entity = getDownloadEntity(detailEntity.downloadUrl);
    final File apk = new File(
        DownloadHelpUtil.getApkDownloadPath(detailEntity.downloadUrl, detailEntity.packageName));
    if (entity == null) {
      if (AndroidUtils.isInstall(getContext(), detailEntity.packageName)) {
        text = "打开";
      } else if (apk.exists() && FileUtil.checkMD5(detailEntity.apkMd5Code, apk)) {
        text = "安装";
      } else {
        text = "下载";
      }
    } else {
      switch (entity.getState()) {
        case DownloadEntity.STATE_PRE:
        case DownloadEntity.STATE_POST_PRE:
          //text = "暂停";
          text = "下载中";
          break;
        case DownloadEntity.STATE_DOWNLOAD_ING:
          Task task = DownloadManager.getInstance().getTaskQueue().getTask(entity);
          if (task == null || task.isDownloading()) {
            //text = "暂停";
            text = "下载中";
          } else if (task.getDownloadEntity().isDownloadComplete()) {
            text = "安装";
          }
          break;
        case DownloadEntity.STATE_WAIT:
          text = "等待中";
          break;
        case DownloadEntity.STATE_CANCEL:
        case DownloadEntity.STATE_STOP:
        case DownloadEntity.STATE_FAIL:
          text = "恢复下载";
          break;
        case DownloadEntity.STATE_OTHER:
        case DownloadEntity.STATE_COMPLETE:
          if (AndroidUtils.isInstall(getContext(), detailEntity.packageName)) {
            text = "打开";
          } else if (apk.exists() && FileUtil.checkMD5(detailEntity.apkMd5Code, apk)) {
            if (AndroidUtils.isInstall(getContext(), CommonConstant.UPGRADE_PACKAGE_NAME)) {
              text = "安装中";
              bt.setEnabled(false);
            } else {
              text = "安装";
            }
          } else {
            text = "下载";
          }
          break;
        default:
          text = "下载";
          break;
      }
    }
    bt.setText(text);
    return text;
  }

  /**
   * apk是否存在
   */
  boolean apkExists(String downloadUrl, String pkgName) {
    File file = new File(DownloadHelpUtil.getApkDownloadPath(downloadUrl, pkgName));
    return file.exists();
  }

  /**
   * 创建下载实体
   */
  DownloadEntity createDownloadEntity(String downloadUrl, String pkgName) {
    DownloadEntity entity = new DownloadEntity();
    entity.setDownloadPath(DownloadHelpUtil.getApkDownloadPath(downloadUrl, pkgName));
    entity.setFileName(DownloadHelpUtil.getApkDownloadName(downloadUrl, pkgName));
    entity.setDownloadUrl(downloadUrl);
    return entity;
  }

  /**
   * 点赞
   */
  void doPraise(int productId) {
    if (isTest) {
      callback(GameDetailActivity.PRAISE, true);
    } else {
      mNetManager.request(GameManagerApi.class, null)
          .praise(productId, 0)
          .compose(new HttpCallback<Object>() {
            @Override public void onResponse(Object response) {
              callback(GameDetailActivity.PRAISE, true);
            }
          });
    }
  }

  /**
   * 取消点赞
   */
  void unPraise(int productId) {
    if (isTest) {
      callback(GameDetailActivity.UN_PRAISE, true);
    } else {
      mNetManager.request(GameManagerApi.class, null)
          .praise(productId, 1)
          .compose(new HttpCallback<Object>() {
            @Override public void onResponse(Object response) {
              callback(GameDetailActivity.UN_PRAISE, true);
            }
          });
    }
  }

  /**
   * 鉴权回调
   */
  private class AuthCallback implements AuthUtil.AuthCallback {

    GameDetailEntity entity;

    AuthCallback(GameDetailEntity entity) {
      this.entity = entity;
    }

    @Override public void onSuccess() {
      entity.isBuy = true;
      entity.errorCode = "0";
      callback(GameDetailActivity.GAME_DETAIL_RESULT, entity);
    }

    @Override public void onFailure(String errorCode) {
      FL.d(this, "errorCode ==> " + errorCode);
      if (TextUtils.isEmpty(errorCode)) {
        FL.d(this, "errorCode ==> null");
        errorCode = "1001";
      }
      entity.errorCode = errorCode;
      entity.isBuy = false;
      callback(GameDetailActivity.GAME_DETAIL_RESULT, entity);
    }

    @Override public void onShelves(String state) {
      entity.isShelves = true;
      callback(GameDetailActivity.GAME_DETAIL_RESULT, entity);
    }

    @Override public void onError() {
      FL.d(TAG, "auth error");
      entity.errorCode = "1001";
      entity.isBuy = false;
      callback(GameDetailActivity.GAME_DETAIL_RESULT, entity);
    }
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  void getTestDate() {
    String json = " {\n"
        + "            \"desc\": \"由25年丰富早教经验的优质幼儿教育机构——“黄金教育集团”权威出品。可爱卡通风格，有趣游戏动画，简洁操作方式，贴心语音提示及活泼音乐。 游戏目的： 1、综合比较长短、高矮、胖瘦、粗细、宽窄、大小、多少。 2、训练观察能力。 游戏操作简介： 小呆和小动物们在森林里开办了一个森林马戏团，他们正在表演精彩节目。你也来看看吧！听清楚问题，选择正确的小动物吧！\", \n"
        + "            \"productmode\": 3, \n"
        + "            \"tag\": 0, \n"
        + "            \"productstar\": 8, \n"
        + "            \"packagename\": \"air.com.A1248e.Mirror\", \n"
        + "            \"ispraise\": 0, \n"
        + "            \"ishave\": 0, \n"
        + "            \"downnum\": 197955, \n"
        + "            \"type\": 1, \n"
        + "            \"isAddPackage\": \"false\", \n"
        + "            \"price\": 0, \n"
        + "            \"hottag\": 2, \n"
        + "            \"apkuri\": \"http://172.18.104.111/uploadServer/upload/apk/HuLianWangGongXiangZiYuan/ZhaoJingZiHeBei/air.com.A1248e.Mirror-1000001-v1.0.1.false.apk\", \n"
        + "            \"providername\": \"互联网共享资源\", \n"
        + "            \"productid\": 284, \n"
        + "            \"md5\": \"132d7094041cbbb35723d7c9ebb1dc0a\", \n"
        + "            \"imageuri\": \"http://192.168.5.157:8080/uploadServer/upload/pic/HuLianWangGongXiangZiYuan/SenLinMaXiTuan/SLMXT_COVER_490x368.png\", \n"
        + "            \"praise\": 1266, \n"
        + "            \"DOWNNUM\": 197955, \n"
        + "            \"apksize\": \"13.26\", \n"
        + "            \"producttype\": \"亲子互动\", \n"
        + "            \"productname\": \"森林马戏团\", \n"
        + "            \"screenshotlist\": [\n"
        + "                {\n"
        + "                    \"imageuri\": \"http://192.168.5.157:8080/uploadServer/upload/pic/HuLianWangGongXiangZiYuan/SenLinMaXiTuan/SLMXT_SCREEN_1196x674_1.png\"\n"
        + "                }, \n"
        + "                {\n"
        + "                    \"imageuri\": \"http://192.168.5.157:8080/uploadServer/upload/pic/HuLianWangGongXiangZiYuan/SenLinMaXiTuan/SLMXT_SCREEN_1196x674_2.png\"\n"
        + "                }, \n"
        + "                {\n"
        + "                    \"imageuri\": \"http://192.168.5.157:8080/uploadServer/upload/pic/HuLianWangGongXiangZiYuan/SenLinMaXiTuan/SLMXT_SCREEN_1196x674_3.png\"\n"
        + "                }, \n"
        + "                {\n"
        + "                    \"imageuri\": \"http://192.168.5.157:8080/uploadServer/upload/pic/HuLianWangGongXiangZiYuan/SenLinMaXiTuan/SLMXT_SCREEN_1196x674_4.png\"\n"
        + "                }, \n"
        + "                {\n"
        + "                    \"imageuri\": \"http://192.168.5.157:8080/uploadServer/upload/pic/HuLianWangGongXiangZiYuan/SenLinMaXiTuan/SLMXT_SCREEN_1196x674_5.png\"\n"
        + "                }\n"
        + "            ], \n"
        + "            \"relationlist\": [\n"
        + "                {\n"
        + "                    \"isAddPackage\": false, \n"
        + "                    \"imageuri\": \"http://192.168.5.157:8080/uploadServer/upload/pic/ShangHaiShengJian/XinFenNuDeXiaoNiao/XFNDXN_ICON_190x190.png\", \n"
        + "                    \"productid\": 388, \n"
        + "                    \"productname\": \"新愤怒的小鸟\"\n"
        + "                }, \n"
        + "                {\n"
        + "                    \"isAddPackage\": false, \n"
        + "                    \"imageuri\": \"http://192.168.5.157:8080/uploadServer/upload/pic/ShangHaiShengJian/SaiErHaoGuiLai/SEHGL_ICON_190x190.png\", \n"
        + "                    \"productid\": 390, \n"
        + "                    \"productname\": \"赛尔号归来\"\n"
        + "                }, \n"
        + "                {\n"
        + "                    \"isAddPackage\": false, \n"
        + "                    \"imageuri\": \"http://192.168.5.157:8080/uploadServer/upload/pic/ShangHaiShengJian/XinDaTouErZi/XDTEZ_ICON_190x190.png\", \n"
        + "                    \"productid\": 391, \n"
        + "                    \"productname\": \"新大头儿子\"\n"
        + "                }, \n"
        + "                {\n"
        + "                    \"isAddPackage\": false, \n"
        + "                    \"imageuri\": \"http://192.168.5.157:8080/uploadServer/upload/pic/ShangHaiTengMuWangLuo/LuoKeRenZhe/LKRZ_ICON_190x190.png\", \n"
        + "                    \"productid\": 389, \n"
        + "                    \"productname\": \"洛克忍者\"\n"
        + "                }, \n"
        + "                {\n"
        + "                    \"isAddPackage\": true, \n"
        + "                    \"packageId\": \"3\", \n"
        + "                    \"imageuri\": \"http://192.168.5.157:8080/uploadServer/upload/pic/ChengDuJiaYuHuDongKeJiYouXianGongSi/WuZiQi/WZQ_ICON_190x190.png\", \n"
        + "                    \"productid\": 386, \n"
        + "                    \"productname\": \"五子棋\"\n"
        + "                }\n"
        + "            ], \n"
        + "            \"oldproductlist\": [ ]\n"
        + "        }";
    GameDetailEntity entity = new Gson().fromJson(json, GameDetailEntity.class);
    callback(GameDetailActivity.GAME_DETAIL_RESULT, entity);
  }
}
