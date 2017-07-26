package com.utstar.appstoreapplication.activity.windows.my_game.my_game_detail;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import com.arialyy.aria.core.task.Task;
import com.ut.wb.ui.adapter.AbsHolder;
import com.ut.wb.ui.adapter.AbsRVAdapter;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.widget.my_game_detail.MyGameDetailLayout;
import com.utstar.appstoreapplication.activity.entity.net_entity.MyGameDetailEntity;
import java.util.List;

/**
 * Created by Aria.Lao on 2017/1/4.
 * 我的游戏list适配器
 */
final class MyGameDetailAdapter
    extends AbsRVAdapter<List<MyGameDetailEntity>, MyGameDetailAdapter.MyGameDetailHolder> {
  int mType;

  MyGameDetailAdapter(Context context, List<List<MyGameDetailEntity>> data, int type) {
    super(context, data);
    mType = type;
  }

  @Override protected MyGameDetailHolder getViewHolder(View convertView, int viewType) {
    return new MyGameDetailHolder(convertView);
  }

  @Override protected int setLayoutId(int type) {
    return R.layout.item_my_game_list;
  }

  @Override
  protected void bindData(MyGameDetailHolder holder, int position, List<MyGameDetailEntity> item) {
    holder.gameDetailLayout.bindData(mType, item);
    //holder.gameDetailLayout.requestItemFocus(0);
    //ImageManager.getInstance().loadImg(holder.img, item.get(0).gameIcon);
  }

  public void update(int position, Task task) {

  }

  class MyGameDetailHolder extends AbsHolder {
    //@Bind(R.id.img) ImageView img;
    @Bind(R.id.my_game_detail) MyGameDetailLayout gameDetailLayout;

    MyGameDetailHolder(View itemView) {
      super(itemView);
    }
  }
}
