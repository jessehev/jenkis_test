package com.utstar.appstoreapplication.activity.windows.my_game.my_game_detail;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.arialyy.aria.core.DownloadEntity;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import com.utstar.appstoreapplication.activity.entity.db_entity.DownloadInfo;
import com.utstar.appstoreapplication.activity.entity.db_entity.UpdateInfo;
import com.utstar.appstoreapplication.activity.entity.net_entity.MyGameDetailEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.MyGameDetailApi;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;
import com.utstar.appstoreapplication.activity.utils.GameUpdateUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;

import com.utstar.appstoreapplication.activity.windows.my_game.MyGameUtil;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Created by Aria.Lao on 2016/12/27.
 */
public class MyGameDetailModule extends BaseModule {
  final Gson mGson = new GsonBuilder().registerTypeAdapter(
      new TypeToken<NetListContainerEntity<MyGameDetailEntity>>() {
      }.getType(), new BasicDeserializer<NetListContainerEntity<MyGameDetailEntity>>()).create();

  public MyGameDetailModule(Context context) {
    super(context);
  }

  /**
   * 获取更新的数据
   */
  public void getUpdate(int page) {
    Map<String, String> params = new WeakHashMap<>();
    params.put("selecttypeid", 4 + "");
    params.put("number", 20 + "");
    params.put("page", page + "");
    //1 ==> 已有又游戏，2 ==> 更新，3 ==> 下载
    params.put("id", 2 + "");
    mNetManager.request(MyGameDetailApi.class, mGson)
        .getMyGame(params)
        .compose(new HttpCallback<NetListContainerEntity<MyGameDetailEntity>>() {
          @Override public void onResponse(NetListContainerEntity<MyGameDetailEntity> response) {
            if (response != null) {
              filterInstallGame(response.list);
            }
          }
        });
  }

  /**
   * 获取下载中的数据
   */
  void getDownloadingData() {
    List<DownloadInfo> infos = DataSupport.findAll(DownloadInfo.class);
    List<MyGameDetailEntity> list = new ArrayList<>();
    Map<String, MyGameDetailEntity> map = new WeakHashMap<>();
    Map<String, DownloadEntity> downloadInfo = MyGameUtil.getDownloadInfo(getContext());
    Map<String, UpdateInfo> updateInfos = GameUpdateUtil.getAllUpdateInfo();

    if (infos != null && infos.size() > 0) {
      for (DownloadInfo info : infos) {
        MyGameDetailEntity entity =
            MyGameUtil.convertDownloadInfo(info, downloadInfo.get(info.getDownloadUrl()));
        map.put(entity.getDownloadUrl(), entity);
      }

      Set<String> keys = map.keySet();
      for (String key : keys) {
        MyGameDetailEntity entity = map.get(key);
        //if (AndroidUtils.isInstall(getContext(), entity.getPackageName())) continue;
        if (AndroidUtils.isInstall(getContext(), entity.getPackageName())
            && updateInfos.get(entity.getPackageName()) == null) {
          continue;
        }
        list.add(map.get(key));
      }
    }

    new Handler().postDelayed(() -> callback(MyGameDetailActivity.MY_DATA, list), 100);
  }

  /**
   * 获取我的游戏数据
   *
   * @param page 分页
   */
  void getMyGame(int page) {
    Map<String, String> params = new WeakHashMap<>();
    params.put("selecttypeid", 4 + "");
    params.put("number", 100 + "");
    params.put("page", page + "");
    //1 ==> 已有又游戏，2 ==> 更新，3 ==> 下载
    params.put("id", 1 + "");
    mNetManager.request(MyGameDetailApi.class, mGson)
        .getMyGame(params)
        .compose(new HttpCallback<NetListContainerEntity<MyGameDetailEntity>>() {
          @Override public void onResponse(NetListContainerEntity<MyGameDetailEntity> response) {
            if (response != null) {
              filterMyGame(response.list);
            }
          }
        });
  }

  int countPage(List<MyGameDetailEntity> list) {
    if (list == null || list.size() == 0) {
      return 0;
    }
    int size = list.size();
    if (size <= 10) {
      return 1;
    }
    int page = size / 10;
    if (size % 10 != 0) page += 1;
    return page;
  }

  /**
   * 过滤安装的游戏
   */
  private void filterInstallGame(List<MyGameDetailEntity> list) {
    List<MyGameDetailEntity> filterList = new ArrayList<>();
    Map<String, MyGameDetailEntity> map = new WeakHashMap<>();
    Map<String, DownloadEntity> downloadInfo = MyGameUtil.getDownloadInfo(getContext());
    for (MyGameDetailEntity entity : list) {
      DownloadEntity info = downloadInfo.get(entity.downloadUrl);
      if (AndroidUtils.isInstall(getContext(), entity.packageName)
          && (info == null || info.isDownloadComplete())
          && GameUpdateUtil.isNewApp(entity.packageName, entity.versionCode)) {
        map.put(entity.getDownloadUrl(), entity);
      }
    }
    Set<String> keys = map.keySet();
    for (String key : keys) {
      filterList.add(map.get(key));
    }
    callback(MyGameDetailActivity.MY_DATA, filterList);
  }

  /**
   * 过滤未安装的游戏
   */
  private void filterMyGame(List<MyGameDetailEntity> list) {
    List<MyGameDetailEntity> filterList = new ArrayList<>();
    Map<String, DownloadEntity> downloadInfo = MyGameUtil.getDownloadInfo(getContext());
    Map<String, MyGameDetailEntity> map = new WeakHashMap<>();

    //获取网络下载安装信息
    for (MyGameDetailEntity entity : list) {
      if (entity.isAddPackage) {
        map.put(StringUtil.keyToHashKey(entity.getPackageId()), entity);
        continue;
      }

      if (AndroidUtils.isInstall(getContext(), entity.getPackageName())) {
        map.put(String.valueOf(entity.gameId), entity);
      }

      DownloadEntity entity_1 = downloadInfo.get(entity.getDownloadUrl());
      if (entity_1 == null) continue;
      if (!entity_1.isDownloadComplete()) continue;
      if (AndroidUtils.isInstall(getContext(), entity.getPackageName())) {
        if (TextUtils.isEmpty(entity.getDownloadUrl())) continue;
        map.put(String.valueOf(entity.gameId), entity);
      }
    }
    Set<String> keys = map.keySet();
    for (String key : keys) {
      filterList.add(map.get(key));
    }
    callback(MyGameDetailActivity.MY_DATA, filterList);
  }
}
