package com.utstar.appstoreapplication.activity.windows.activity.draw_award;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.AwardListEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.List;

/**
 * Created by JesseHev on 2017/1/10.
 * 砸蛋奖品列表适配器
 */

public class HitAwardAdapter extends RecyclerView.Adapter<HitAwardAdapter.HitAwardHolder> {

  List<AwardListEntity> mData;

  @Override
  public HitAwardAdapter.HitAwardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_egg_award, null);
    HitAwardHolder holder = new HitAwardHolder(v);
    return holder;
  }

  @Override public void onBindViewHolder(HitAwardHolder holder, int position) {
    AwardListEntity entity = mData.get(position);

    holder.name.setText(entity.name);
    holder.rank.setText(entity.rank);

    ImageManager.getInstance().loadImg(holder.bg, entity.imgUrl);
    holder.name.setText(entity.name);
  }

  public HitAwardAdapter(List<AwardListEntity> list) {
    this.mData = list;
  }

  @Override public int getItemCount() {
    if (mData == null || mData.size() <= 0) {
      return 0;
    }
    if (mData.size() > 3) {
      return 3;
    } else {
      return mData.size();
    }
  }

  public void update(List<AwardListEntity> list) {
    this.mData = list;
    notifyDataSetChanged();
  }

  public class HitAwardHolder extends RecyclerView.ViewHolder {
    TextView name;
    TextView rank;
    ImageView bg;

    public HitAwardHolder(View v) {
      super(v);
      name = (TextView) v.findViewById(R.id.tv_award_name);
      rank = (TextView) v.findViewById(R.id.tv_award_rank);
      bg = (ImageView) v.findViewById(R.id.iv_award_bg);
    }
  }
}
