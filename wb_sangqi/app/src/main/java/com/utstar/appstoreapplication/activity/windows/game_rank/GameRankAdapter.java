package com.utstar.appstoreapplication.activity.windows.game_rank;

import android.content.Context;
import android.content.Intent;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ut.wb.ui.MarqueTextView;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.constants.CommonConstant;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameRankEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import java.util.List;

/**
 * Created by JesseHev on 2016/12/14.
 */

public class GameRankAdapter extends RecyclerView.Adapter<GameRankAdapter.GameRankHolder> {

  private VerticalGridView mVerticalGridView;
  private View mView;
  private Context mContext;
  private List<GameRankEntity.GameRankSubEntity> mData;
  private int mTypeId;
  private String typeName;

  @Override public GameRankHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank, null);
    GameRankHolder holder = new GameRankHolder(view);
    return holder;
  }

  public GameRankAdapter(Context context, View view, VerticalGridView mVerticalGridView,
      List<GameRankEntity.GameRankSubEntity> data) {
    this.mView = view;
    this.mVerticalGridView = mVerticalGridView;
    this.mContext = context;
    this.mData = data;
  }

  @Override public void onBindViewHolder(GameRankHolder holder, int position) {
    holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        holder.rLayout.animate().scaleX(1.15f).scaleY(1.15f).start();
        holder.ivIcon.animate().scaleX(1.15f).scaleY(1.15f).start();
        setScaleAndColor(1.15f, R.color.white);
        holder.tvName.startMarquee();
      } else {
        holder.rLayout.animate().scaleX(1.f).scaleY(1.f).start();
        holder.ivIcon.animate().scaleX(1.f).scaleY(1.f).start();
        setScaleAndColor(1.1f, R.color.pray);
        holder.tvName.stopMarquee();
      }
    });
    GameRankEntity.GameRankSubEntity gameRankSubEntity = null;
    String name = "", coverName = "";
    if (position < 8) {
      gameRankSubEntity = mData.get(position);
      name = gameRankSubEntity.productName;
      coverName = gameRankSubEntity.productName;
      ImageManager.getInstance().loadRoundedImg(holder.ivBg, gameRankSubEntity.imageUri, 20);
    } else {
      name = "更多";
      holder.ivBg.setBackgroundResource(R.mipmap.icon_rank_more);
    }
    setIconOrder(holder.ivIcon, position);
    setOnClickEvent(holder, position, gameRankSubEntity);
    holder.tvName.setText(name);
    holder.tvCoverName.setText(coverName);
  }

  /**
   * 处理左边图标顺序  第一位、第二位、第三位
   */
  public void setIconOrder(ImageView img, int position) {
    int imgRes = -1;
    switch (position) {
      case 0:
        imgRes = R.mipmap.icon_rank_first;
        break;
      case 1:
        imgRes = R.mipmap.icon_rank_second;
        break;
      case 2:
        imgRes = R.mipmap.icon_rank_third;
        break;
      default:
        break;
    }
    if (imgRes != -1) {
      img.setBackgroundResource(imgRes);
    }
  }

  private void setOnClickEvent(GameRankHolder holder, int position,
      GameRankEntity.GameRankSubEntity gameRankSubEntity) {
    holder.itemView.setOnClickListener(v -> {
      if (position == 8) {
        Intent intent = new Intent(mContext, GameRankDetailActivity.class);
        intent.putExtra(CommonConstant.id, mTypeId);
        intent.putExtra(CommonConstant.typeName, typeName);
        intent.putExtra(CommonConstant.selecttypeid, CommonConstant.C_iLineTab_Ranking);
        mContext.startActivity(intent);
      } else {
        if (gameRankSubEntity.isAddPackage && gameRankSubEntity.type.equals("0")) { //区分付费排行
          if (gameRankSubEntity.newPackageId != null) {
            TurnManager.getInstance()
                .turnPackage(mContext, gameRankSubEntity.newPackageId, gameRankSubEntity.productId);
          } else if (gameRankSubEntity.payPackageId != null) {
            TurnManager.getInstance()
                .turnPackage(mContext, gameRankSubEntity.payPackageId, gameRankSubEntity.productId);
          }
        } else {
          TurnManager.getInstance()
              .turnGameDetail(mContext, Integer.parseInt(gameRankSubEntity.productId));
        }
        //TurnManager.getInstance()
        //    .turnGameDetail(mContext, Integer.parseInt(gameRankSubEntity.productId));
      }
    });
  }

  /**
   * 设置标题栏状态、颜色
   */
  private void setScaleAndColor(float scale, int color) {
    if (mView.findViewById(R.id.vgv_pay) == mVerticalGridView) {
      TextView tv = (TextView) mView.findViewById(R.id.tv_pay);
      tv.setTextColor(mContext.getResources().getColor(color));
      tv.animate().scaleX(scale).scaleY(scale).start();
    } else if (mView.findViewById(R.id.vgv_download) == mVerticalGridView) {
      TextView tv = (TextView) mView.findViewById(R.id.tv_download);
      tv.setTextColor(mContext.getResources().getColor(color));
      tv.animate().scaleX(scale).scaleY(scale).start();
    } else if (mView.findViewById(R.id.vgv_newproduct) == mVerticalGridView) {
      TextView tv = (TextView) mView.findViewById(R.id.tv_newproduct);
      tv.setTextColor(mContext.getResources().getColor(color));
      tv.animate().scaleX(scale).scaleY(scale).start();
    }
  }

  public void setFocus(int resourceId) {
    VerticalGridView grid = (VerticalGridView) mView.findViewById(resourceId);
    grid.getLayoutManager().getChildAt(0).requestFocus();
  }

  public void setPosition(int resourceId, int position, int mode) {
    VerticalGridView grid = (VerticalGridView) mView.findViewById(resourceId);
    RecyclerView.LayoutManager manager = grid.getLayoutManager();
    if (KeyEvent.KEYCODE_DPAD_RIGHT == mode) {
      if (position >= 0 && position < 3) {//第一行
        manager.getChildAt(0).requestFocus();
      } else if (position >= 3 && position < 6) {//第二行
        manager.getChildAt(3).requestFocus();
      } else if (position >= 5 && position < 9) { //第三行
        manager.getChildAt(6).requestFocus();
      }
    } else if (KeyEvent.KEYCODE_DPAD_LEFT == mode) {
      if (position >= 0 && position < 3) {//第一行
        manager.getChildAt(2).requestFocus();
      } else if (position >= 3 && position < 6) {//第二行
        manager.getChildAt(5).requestFocus();
      } else if (position >= 5 && position < 9) { //第三行
        manager.getChildAt(8).requestFocus();
      }
    }
  }

  //更新数据
  public void upData(List<GameRankEntity.GameRankSubEntity> mData, int typeId, String typeName) {
    this.mData = mData;
    this.mTypeId = typeId;
    this.typeName = typeName;
    notifyDataSetChanged();
  }

  @Override public int getItemCount() {
    if (mData == null) {
      return 0;
    }
    if (mData.size() >= 8) {
      return 9;
    } else {
      return mData.size();
    }
  }

  public class GameRankHolder extends RecyclerView.ViewHolder {
    public MarqueTextView tvName;
    public MarqueTextView tvCoverName;
    public ImageView ivBg;
    public ImageView ivIcon;

    public RelativeLayout rLayout;

    public GameRankHolder(View itemView) {
      super(itemView);
      tvName = (MarqueTextView) itemView.findViewById(R.id.tv_name);
      ivBg = (ImageView) itemView.findViewById(R.id.iv_bg);
      ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
      tvCoverName = (MarqueTextView) itemView.findViewById(R.id.tv_cover_name);
      rLayout = (RelativeLayout) itemView.findViewById(R.id.rl);
    }
  }
}
