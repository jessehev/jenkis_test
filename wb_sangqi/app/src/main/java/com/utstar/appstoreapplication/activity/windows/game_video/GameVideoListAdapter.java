package com.utstar.appstoreapplication.activity.windows.game_video;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.ut.wb.ui.MarqueTextView;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameVideoEntity;
import com.utstar.appstoreapplication.activity.manager.AnimManager;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-03-01.
 * 游戏视频列表适配器
 */

public class GameVideoListAdapter
    extends RecyclerView.Adapter<GameVideoListAdapter.GameVideoHolder> {

  private List<GameVideoEntity> mData = new ArrayList<>();

  @Override public GameVideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_game_video, null);
    View v =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_game_video_2, null);
    GameVideoHolder holder = new GameVideoHolder(v);
    return holder;
  }

  public GameVideoListAdapter(List<GameVideoEntity> list) {
    mData.clear();
    mData.addAll(list);
  }

  @Override public void onBindViewHolder(GameVideoHolder holder, int position) {

    holder.itemView.setOnFocusChangeListener((view, hasFocus) -> {
      if (hasFocus) {
        AnimManager.getInstance().enlarge(holder.rl, 1.25f);
        holder.mTvCover.startMarquee();
        holder.mTvName.setVisibility(View.GONE);
        holder.mTvCover.setVisibility(View.VISIBLE);
      } else {
        AnimManager.getInstance().narrow(holder.rl, 1.0f);
        holder.mTvCover.stopMarquee();
        holder.mTvName.setVisibility(View.VISIBLE);
        holder.mTvCover.setVisibility(View.GONE);
      }
    });

    GameVideoEntity videoEntity = mData.get(position);
    holder.mTvName.setText(videoEntity.name);
    holder.mTvCover.setText(videoEntity.name);
    ImageManager.getInstance().loadRoundedImg(holder.bg, videoEntity.imgUrl, 18);
  }

  @Override public int getItemCount() {
    if (mData == null || mData.size() == 0) {
      return 0;
    }
    return mData.size() > 8 ? 8 : mData.size();
  }

  public static class GameVideoHolder extends RecyclerView.ViewHolder {

    ImageView bg;
    RelativeLayout rl;
    MarqueTextView mTvCover;
    MarqueTextView mTvName;
    LinearLayout mLabel;

    public GameVideoHolder(View v) {
      super(v);
      bg = (ImageView) v.findViewById(R.id.iv_bg);
      rl = (RelativeLayout) v.findViewById(R.id.rl);
      mLabel = (LinearLayout) v.findViewById(R.id.rl_label);
      mTvCover = (MarqueTextView) v.findViewById(R.id.tv_cover_name);
      mTvName = (MarqueTextView) v.findViewById(R.id.tv_name);
    }
  }
}
