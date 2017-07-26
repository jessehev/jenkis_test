package com.utstar.appstoreapplication.activity.windows.game_search;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.net_entity.SearchEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.SearchApi;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/19.
 */
final class SearchModule extends BaseModule {
  final char[][] keys = new char[][] {
      { 'A', 'B', 'C' }, { 'D', 'E', 'F' }, { 'G', 'H', 'I' }, { 'J', 'K', 'L' }, { 'M', 'N', 'O' },
      { 'P', 'Q', 'R', 'S' }, { 'T', 'U', 'V' }, { 'X', 'W', 'Y', 'Z' },
  };

  private final Gson mSearchGson =
      new GsonBuilder().registerTypeAdapter(new TypeToken<SearchEntity>() {
      }.getType(), new BasicDeserializer<SearchEntity>()).create();

  private List<SearchEntity.SearchResultEntity> mBack = new ArrayList<>();

  public SearchModule(Context context) {
    super(context);
  }

  /**
   * 猜你喜欢
   */
  void getGameRecommendGame() {
    mNetManager.request(SearchApi.class, mSearchGson)
        .getRecommendGame(1, 6)
        .compose(new HttpCallback<SearchEntity>() {
          @Override public void onResponse(SearchEntity response) {
            if (response.list != null && response.list.size() > 0) {
              mBack.clear();
              mBack.addAll(response.list);
            }
            callback(SearchFragment.RECOMMEND_RESULT, response.list);
            //L.d(response.size + "");
          }
        });
  }

  /**
   * 搜索
   */
  void search(String key, int page) {
    mNetManager.request(SearchApi.class, mSearchGson)
        .search(key, page, 20)
        .compose(new HttpCallback<SearchEntity>() {
          @Override public void onResponse(SearchEntity response) {
            callback(SearchFragment.SEARCH_RESULT, response.list);
          }
        });
  }

  /**
   * 获取备份数据
   */
  void getBack() {
    callback(SearchFragment.RECOMMEND_RESULT, mBack);
  }

  /**
   * 创建搜索key
   */
  List<SearchKeyEntity> createKeyData() {
    List<SearchKeyEntity> list = new ArrayList<>();
    list.add(new SearchKeyEntity(1));
    int i = 2;
    for (char[] key : keys) {
      list.add(new SearchKeyEntity(i, key));
      i++;
    }
    list.add(new SearchKeyEntity(10, String.valueOf(R.mipmap.icon_search_del)));
    list.add(new SearchKeyEntity(0));
    list.add(new SearchKeyEntity(11, String.valueOf(R.mipmap.icon_search_clean)));
    return list;
  }
}
