package com.utstar.appstoreapplication.activity.windows.monthly_payment;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ut.wb.ui.MarqueTextView;
import com.ut.wb.ui.TagUtil;
import com.ut.wb.ui.Utils;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.MothlyPaySecondEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.List;

/**
 * Created by lt on 2016/12/20.
 */

public final class MothlyPayAdapter
    extends RecyclerView.Adapter<MothlyPayAdapter.MothlyPayViewHolder> {
  private List<MothlyPaySecondEntity.MothlyGameEntity> mothlyGames;
  private static final int CONTANT_MOTHLY_SB = 1;      //手柄
  private static final int CONTANT_MOTHLY_YKQ = 2;    //遥控器
  private static final int CONTANT_MOTHLY_ALL = 3;    //都可以
  private static final int CONTANT_XFMF = 4;             //限时免费

  private boolean isBuy;
  private int tag;

  public MothlyPayAdapter(List<MothlyPaySecondEntity.MothlyGameEntity> mothlyGames) {
    this.mothlyGames = mothlyGames;
  }

  @Override public MothlyPayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = View.inflate(parent.getContext(), R.layout.item_mothly_pay_vgv, null);
    return new MothlyPayViewHolder(itemView);
  }

  @Override public void onBindViewHolder(MothlyPayViewHolder holder, int position) {
    MothlyPaySecondEntity.MothlyGameEntity mothlyGameEntity = mothlyGames.get(position);
    ImageManager.getInstance().loadImg(holder.ivPicture, mothlyGameEntity.getADDRESS());
    setViews(holder, mothlyGameEntity); //view设置数据
    setTagViews(holder, mothlyGameEntity);
  }

  public void update(boolean isBuy) {
    this.isBuy = isBuy;
    notifyDataSetChanged();
  }

  public void update(boolean isBuy, int tag) {
    this.isBuy = isBuy;
    this.tag = tag;
    notifyDataSetChanged();
  }

  private void setTagViews(MothlyPayViewHolder holder,
      MothlyPaySecondEntity.MothlyGameEntity mothlyGameEntity) {
    TagUtil.handleTag(holder.ivTag, tag, isBuy);
    if (MothlyPayMainFragment.mTag == CONTANT_XFMF) {
      //holder.ivTag.setImageResource(R.mipmap.icon_xsmf);
    } else {
      if (mothlyGameEntity.getGametag() == 1) {
        //holder.ivTag.setImageResource(R.mipmap.icon_xsmf);
      }
    }
  }

  private void setViews(MothlyPayViewHolder holder,
      MothlyPaySecondEntity.MothlyGameEntity mothlyGameEntity) {
    holder.tvtitle.setText(mothlyGameEntity.getName());
    if (mothlyGameEntity.getProductmode() == CONTANT_MOTHLY_SB) {
      holder.ivYkq.setVisibility(View.VISIBLE);
      holder.ivYkq.setImageResource(R.mipmap.icon_sb);
    } else if (mothlyGameEntity.getProductmode() == CONTANT_MOTHLY_YKQ) {
      holder.ivYkq.setVisibility(View.VISIBLE);
    } else if (mothlyGameEntity.getProductmode() == CONTANT_MOTHLY_ALL) {
      holder.ivSb.setVisibility(View.VISIBLE);
      holder.ivYkq.setVisibility(View.VISIBLE);
    }
    holder.tvDownloads.setText(Utils.downloadCountHelp(mothlyGameEntity.getDowncount()));
  }

  @Override public int getItemCount() {
    //return mothlyGames.size()==0?0:1;
    return mothlyGames.size();
  }

  public class MothlyPayViewHolder extends RecyclerView.ViewHolder {
    ImageView ivPicture, ivYkq, ivSb, ivTag;
    TextView tvDownloads;
    MarqueTextView tvtitle;
    RelativeLayout rlMothlyShadow;

    public MothlyPayViewHolder(View itemView) {
      super(itemView);
      ivPicture = (ImageView) itemView.findViewById(R.id.item_mothly_iv);
      tvtitle = (MarqueTextView) itemView.findViewById(R.id.item_mothly_tv);
      rlMothlyShadow = (RelativeLayout) itemView.findViewById(R.id.item_mothly_shadow);
      ivYkq = (ImageView) itemView.findViewById(R.id.item_mothly_iv_ykq);
      ivSb = (ImageView) itemView.findViewById(R.id.item_mothly_iv_sb);
      tvDownloads = (TextView) itemView.findViewById(R.id.item_mothly_tv_downloads);
      ivTag = (ImageView) itemView.findViewById(R.id.item_mothly_tag);
    }
  }
}
