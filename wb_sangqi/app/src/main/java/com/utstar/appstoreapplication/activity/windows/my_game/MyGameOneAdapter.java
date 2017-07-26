package com.utstar.appstoreapplication.activity.windows.my_game;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.ut.wb.ui.MarqueTextView;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.MyGameDetailEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.List;

/**
 * Created by lt on 2016/12/22. 我的游戏adapter
 */
final class MyGameOneAdapter extends RecyclerView.Adapter<MyGameOneAdapter.MyGameOneViewHolder> {
  private Context mContext;
  private List<MyGameDetailEntity> mData;
  private static final int LIST_SIZE = 6;

  public MyGameOneAdapter(Context context, List<MyGameDetailEntity> data) {
    this.mContext = context;
    this.mData = data;
  }

  @Override public MyGameOneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = View.inflate(mContext, R.layout.item_my_game, null);
    return new MyGameOneViewHolder(itemView);
  }

  @Override public void onBindViewHolder(MyGameOneViewHolder holder, int position) {
    setViews(holder, position);
    holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        ViewGroup view = (ViewGroup) v;
        view.getChildAt(0).animate().scaleX(1.10f).scaleY(1.10f).start();
        holder.title.startMarquee();
      } else {
        ViewGroup view = (ViewGroup) v;
        view.getChildAt(0).animate().scaleX(1.0f).scaleY(1.0f).start();
        holder.title.stopMarquee();
      }
    });
  }

  private void setViews(MyGameOneViewHolder holder, int position) {
    if (mData != null && mData.size() > 0) {
      final MyGameDetailEntity entity = mData.get(position);
      if (entity == null) return;
      if (entity.isTemp) {
        ImageManager.getInstance().setImg(holder.img, R.mipmap.icon_none, 0, 0, 10);
        holder.title.setText("");
      } else if (!entity.isLastItem) {
        ImageManager.getInstance().loadImg(holder.img, mData.get(position).getGameIcon());
        holder.title.setText(mData.get(position).getGameName());
      } else {
        holder.img.setImageResource(R.mipmap.icon_my_game_more);
        holder.title.setText("管理");
      }
    }
  }

  @Override public int getItemCount() {
    return LIST_SIZE;
  }

  public class MyGameOneViewHolder extends RecyclerView.ViewHolder {
    ImageView img;
    MarqueTextView title;
    RelativeLayout rootView;

    public MyGameOneViewHolder(View itemView) {
      super(itemView);
      img = (ImageView) itemView.findViewById(R.id.item_my_game_iv);
      title = (MarqueTextView) itemView.findViewById(R.id.item_my_game_tv);  //下面title
      rootView = (RelativeLayout) itemView.findViewById(R.id.item_my_game);
    }
  }
}
