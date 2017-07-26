package com.utstar.appstoreapplication.activity.windows.game_hall;

import android.content.Context;
import android.view.View;
import butterknife.Bind;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.show.L;
import com.ut.wb.ui.adapter.AbsHolder;
import com.ut.wb.ui.adapter.AbsRVAdapter;
import com.ut.wb.ui.metro.MetroItemEntity;
import com.ut.wb.ui.metro.MetroLayout;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aria.Lao on 2016/12/22. 游戏大厅适配器
 */
final class MetroAdapter extends AbsRVAdapter<List<MetroItemEntity>, MetroAdapter.GameHallHolder> {
  Map<Integer, Boolean> mUpdateMap = new HashMap<>();

  public MetroAdapter(Context context, List<List<MetroItemEntity>> data) {
    super(context, data);
    for (int i = 0, len = data.size(); i < len; i++) {
      mUpdateMap.put(i, false);
    }
  }

  @Override protected GameHallHolder getViewHolder(View convertView, int viewType) {
    return new GameHallHolder(convertView);
  }

  @Override protected int setLayoutId(int type) {

    return R.layout.item_game_hall;
  }

  @Override
  protected void bindData(GameHallHolder holder, int position, List<MetroItemEntity> item) {
    if (mUpdateMap.get(position) != null && mUpdateMap.get(position)) {
      holder.metro.update(item);
    } else {
      holder.metro.setItem(item);
      holder.metro.setOnMetroClickListener(
          (view, position1, entity) -> TurnManager.getInstance().turnMetro(getContext(), entity));
    }
  }

  public void update(int position) {
    mUpdateMap.put(position, true);
    notifyItemChanged(position);
  }

  class GameHallHolder extends AbsHolder {
    @Bind(R.id.metro) MetroLayout metro;

    GameHallHolder(View itemView) {
      super(itemView);
      int w = (int) (AndroidUtils.getScreenParams(BaseApp.context)[0] - BaseApp.context.getResources()
              .getDimension(R.dimen.dimen_60dp));
      itemView.getLayoutParams().width = w;
    }
  }
}
