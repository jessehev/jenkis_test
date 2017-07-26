package com.utstar.appstoreapplication.activity.windows.my_game;

import android.content.Context;
import android.databinding.ViewDataBinding;
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
import com.utstar.appstoreapplication.activity.utils.GameUpdateUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Created by Aria.Lao on 2016/12/19.
 */
class MyGameMainModule extends BaseModule<ViewDataBinding> {
  int mMyGameCount = 0, mDownloadCount = 0, mUpdateCount = 0;

  //我的游戏
  public MyGameMainModule(Context context) {
    super(context);
  }

  /**
   * havGame   已有游戏
   */
  void getMyGame(int page) {
    Map<String, String> params = new WeakHashMap<>();
    params.put("selecttypeid", 4 + "");
    params.put("number", 100 + "");
    params.put("page", page + "");
    //1 ==> 已有又游戏，2 ==> 更新，3 ==> 下载
    params.put("id", 1 + "");
    final Gson gson = new GsonBuilder().registerTypeAdapter(
        new TypeToken<NetListContainerEntity<MyGameDetailEntity>>() {
        }.getType(), new BasicDeserializer<NetListContainerEntity<MyGameDetailEntity>>()).create();
    mNetManager.request(MyGameDetailApi.class, gson)
        .getMyGame(params)
        .compose(new HttpCallback<NetListContainerEntity<MyGameDetailEntity>>() {
          @Override public void onResponse(NetListContainerEntity<MyGameDetailEntity> response) {
            if (response != null) {
              callback(MyGameActivity.RESOPNSE_CALL_SUCCESS, filterMyGame(response.list));
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
    Map<String, DownloadEntity> downloadInfo = MyGameUtil.getDownloadInfo(getContext());
    Map<String, MyGameDetailEntity> map = new WeakHashMap<>();
    Map<String, UpdateInfo> updateInfos = GameUpdateUtil.getAllUpdateInfo();

    int i = 0;
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
        if (i < 5) {
          list.add(map.get(key));
        }
        i++;
      }
      mDownloadCount = i;
    } else {
      mDownloadCount = 0;
    }

    callback(MyGameActivity.MY_DATA, addTempData(list));
  }

  /**
   * 过滤未安装的游戏
   */
  private List<MyGameDetailEntity> filterMyGame(List<MyGameDetailEntity> list) {
    List<MyGameDetailEntity> filterList = new ArrayList<>();
    Map<String, MyGameDetailEntity> map = new WeakHashMap<>();
    Map<String, DownloadEntity> downloadInfo = MyGameUtil.getDownloadInfo(getContext());
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

    int i = 0;
    Set<String> keys = map.keySet();
    for (String key : keys) {
      if (i < 5) {
        filterList.add(map.get(key));
      }
      i++;
    }
    mMyGameCount = i;
    return addTempData(filterList);
  }

  private List<MyGameDetailEntity> addTempData(List<MyGameDetailEntity> filterList) {
    if (filterList != null && filterList.size() != 0) {
      MyGameDetailEntity lastEntity = new MyGameDetailEntity();
      lastEntity.isLastItem = true;
      filterList.add(lastEntity);
    } else {
      filterList = new ArrayList<>();
    }

    if (filterList.size() < 6) {
      for (int j = 0, len = 6 - filterList.size(); j < len; j++) {
        filterList.add(createTempEntity());
      }
    }
    return filterList;
  }

  /**
   * 创建填充实体
   */
  MyGameDetailEntity createTempEntity() {
    MyGameDetailEntity temp = new MyGameDetailEntity();
    temp.isTemp = true;
    return temp;
  }

  int countNum(List<MyGameDetailEntity> list) {
    int count = 0;
    for (MyGameDetailEntity entity : list) {
      if (!entity.isLastItem && !entity.isTemp) {
        count++;
      }
    }
    return count;
  }

  /**
   * 获取更新的数据
   */
  void getUpdate(int page) {
    Map<String, String> params = new WeakHashMap<>();
    params.put("selecttypeid", 4 + "");
    params.put("number", 20 + "");
    params.put("page", page + "");
    //1 ==> 已有又游戏，2 ==> 更新，3 ==> 下载
    params.put("id", 2 + "");
    final Gson gson = new GsonBuilder().registerTypeAdapter(
        new TypeToken<NetListContainerEntity<MyGameDetailEntity>>() {
        }.getType(), new BasicDeserializer<NetListContainerEntity<MyGameDetailEntity>>()).create();
    mNetManager.request(MyGameDetailApi.class, gson)
        .getMyGame(params)
        .compose(new HttpCallback<NetListContainerEntity<MyGameDetailEntity>>() {
          @Override public void onResponse(NetListContainerEntity<MyGameDetailEntity> response) {
            if (response != null) {
              filterUpdateGame(response.list);
              GameUpdateUtil.saveUpdateInfo(response.list);
            }
          }
        });
  }

  /**
   * 过滤安装的游戏
   */
  private void filterUpdateGame(List<MyGameDetailEntity> list) {
    List<MyGameDetailEntity> filterList = new ArrayList<>();
    Map<String, DownloadEntity> downloadInfo = MyGameUtil.getDownloadInfo(getContext());
    if (list != null) {
      Map<String, MyGameDetailEntity> map = new WeakHashMap<>();
      for (MyGameDetailEntity entity : list) {
        DownloadEntity info = downloadInfo.get(entity.downloadUrl);
        if (AndroidUtils.isInstall(getContext(), entity.packageName) && (info == null
            || info.isDownloadComplete()) && GameUpdateUtil.isNewApp(entity.packageName,
            entity.versionCode)) {
          map.put(entity.getDownloadUrl(), entity);
        }
      }
      int i = 0;
      Set<String> keys = map.keySet();
      for (String key : keys) {
        if (i < 5) {
          filterList.add(map.get(key));
        }
        i++;
      }
      mUpdateCount = map.size();
    }
    callback(MyGameActivity.UPDATE_DATE, addTempData(filterList));
  }

  /**
   * 通过包名查找 list中的对应的实体
   *
   * @param packageName 包名
   * @return 没有返回null
   */
  MyGameDetailEntity findDetailEntity(String packageName, List<MyGameDetailEntity> list) {
    MyGameDetailEntity temp = null;
    for (MyGameDetailEntity entity : list) {
      if (entity == null || TextUtils.isEmpty(entity.packageName)) continue;
      if (entity.packageName.equals(packageName)) {
        temp = entity;
        break;
      }
    }
    return temp;
  }

  /**
   * 创建一组temp对象
   */
  List<MyGameDetailEntity> createTempList() {
    List<MyGameDetailEntity> tempList = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      tempList.add(createTempEntity());
    }
    return tempList;
  }

  /**
   * 计算有效内容
   */
  int countContentSize(List<MyGameDetailEntity> list) {
    int count = 0;
    for (MyGameDetailEntity entity : list) {
      if (!entity.isLastItem && !entity.isTemp) {
        count++;
      }
    }
    return count;
  }

  /**
   * 重新排序列表数据
   */
  List<MyGameDetailEntity> sortList(List<MyGameDetailEntity> list) {
    MyGameDetailEntity manager = null;
    List<MyGameDetailEntity> games = new ArrayList<>();
    List<MyGameDetailEntity> sortList = new ArrayList<>();
    Map<String, MyGameDetailEntity> map = new WeakHashMap<>();
    for (MyGameDetailEntity entity : list) {
      if (entity.isLastItem) {
        manager = entity;
      } else if (!entity.isTemp) {
        games.add(entity);
      }
    }

    for (MyGameDetailEntity entity : games) {
      map.put(entity.packageName, entity);
    }

    if (manager == null) {
      manager = new MyGameDetailEntity();
      manager.isLastItem = true;
    }
    int gameSize = map.size();
    if (gameSize == 0) {
      for (int i = 0; i < 6; i++) {
        sortList.add(createTempEntity());
      }
      return sortList;
    }

    Set<String> keys = map.keySet();
    int k = 0, count = gameSize <= 5 ? gameSize : 5;
    for (String key : keys) {
      if (k < count) {
        sortList.add(map.get(key));
      }
      k++;
    }
    sortList.add(manager);

    if (sortList.size() < 6) {
      for (int i = 0, len = 6 - sortList.size(); i < len; i++) {
        sortList.add(createTempEntity());
      }
    }

    return sortList;
  }
}
