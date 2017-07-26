package com.utstar.appstoreapplication.activity.windows.game_rank;

import android.content.Context;
import android.widget.TextView;
import com.arialyy.frame.util.CalendarUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.common_entity.NetListContainerEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameRankEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameRankMoreEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.GameRankApi;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by JesseHev on 2016/12/9
 * 游戏排行
 */

final class GameRankModule extends BaseModule {
  public GameRankModule(Context context) {
    super(context);
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
   * 获取排行榜单数据
   */
  public void getRankData() {
    Gson gson = new GsonBuilder().registerTypeAdapter(
        new TypeToken<NetListContainerEntity<GameRankEntity>>() {
        }.getType(), new BasicDeserializer<NetListContainerEntity<GameRankEntity>>()).create();
    mNetManager.request(GameRankApi.class, gson)
        .getRankData(8, 1)
        .compose(new HttpCallback<NetListContainerEntity<GameRankEntity>>() {
          @Override public void onResponse(NetListContainerEntity<GameRankEntity> response) {
            callback(GameRankFragment.RANKFRAGMENT_RESULT, response.list);
          }
        });
  }

  /**
   * 获取排行游戏列表（更多）
   */
  public void getRankGameList(GameEntity gameEntity) {
    Gson gson = new GsonBuilder().registerTypeAdapter(
        new TypeToken<NetListContainerEntity<GameRankMoreEntity>>() {
        }.getType(), new BasicDeserializer<NetListContainerEntity<GameRankMoreEntity>>()).create();

    mNetManager.request(GameRankApi.class, gson)
        .getGameListBySelectId(gameEntity.id, gameEntity.selectTypeId, gameEntity.number,
            gameEntity.page)
        .compose(new HttpCallback<NetListContainerEntity<GameRankMoreEntity>>() {
          @Override public void onResponse(NetListContainerEntity<GameRankMoreEntity> response) {
            //  mGameRankMoreEntity = response.list;
            callback(GameRankDetailFragment.GAME_RANK_DETAIL_RESULT, response.list);
          }
        });
  }
}
