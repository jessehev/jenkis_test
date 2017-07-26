package com.utstar.appstoreapplication.activity.windows.activity.draw_award;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.DrawAwardIconEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;

/**
 * Created by JesseHev on 2017/1/10.
 * 抽奖 砸蛋活动适配器
 */

public class HitEggAdapter extends RecyclerView.Adapter<HitEggAdapter.HitEggHolder> {

  private Context mContext;
  private DrawAwardIconEntity mIconEntity;

  @Override public HitEggAdapter.HitEggHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_draw_egg, null);
    HitEggHolder holder = new HitEggHolder(v);
    return holder;
  }

  public HitEggAdapter(Context context, DrawAwardIconEntity iconEntity) {
    mContext = context;
    mIconEntity = iconEntity;
  }

  @Override public void onBindViewHolder(HitEggHolder holder, int position) {
    if (mIconEntity != null) {
      holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
        if (hasFocus) {
          v.animate().scaleX(1.15f).scaleY(1.15f).setDuration(300).start();
          if (!TextUtils.isEmpty(mIconEntity.imageType13)) {
            holder.mHanmmer.setVisibility(View.VISIBLE);
            ImageManager.getInstance().setImg(holder.mHanmmer, mIconEntity.imageType13);
          } else {
            holder.mHanmmer.setVisibility(View.GONE); //显示红包
            ImageManager.getInstance().setImg(holder.mImg, mIconEntity.imageType4);
          }
        } else {
          v.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
          holder.mHanmmer.setVisibility(View.GONE);
          ImageManager.getInstance().setImg(holder.mImg, mIconEntity.imageType3);
        }
      });
      ImageManager.getInstance().setImg(holder.mImg, mIconEntity.imageType3);
    }
  }

  public void updata(DrawAwardIconEntity iconEntity) {
    mIconEntity = iconEntity;
    notifyDataSetChanged();
  }

  @Override public int getItemCount() {
    return 5;
  }

  public class HitEggHolder extends RecyclerView.ViewHolder {

    public ImageView mImg;
    public ImageView mHanmmer;

    public HitEggHolder(View v) {
      super(v);
      mImg = (ImageView) v.findViewById(R.id.iv_egg_bg);
      mHanmmer = (ImageView) v.findViewById(R.id.iv_hammer_bg);
    }
  }
}
