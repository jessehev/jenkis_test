package com.utstar.appstoreapplication.activity.windows.game_rank;

import android.content.Context;
import android.databinding.ViewDataBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameRankEntity;
import com.utstar.appstoreapplication.activity.windows.base.BaseModule;

/**
 * Created by JesseHev on 2016/12/9.\
 * 排行详情
 */

final class GameRankDetailModule extends BaseModule<ViewDataBinding> {
  GameRankEntity mGameRankEntity;

  public GameRankDetailModule(Context context) {
    super(context);
  }
}
