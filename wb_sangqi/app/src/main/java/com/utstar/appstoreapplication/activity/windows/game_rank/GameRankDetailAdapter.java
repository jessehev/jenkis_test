package com.utstar.appstoreapplication.activity.windows.game_rank;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ut.wb.ui.MarqueTextView;
import com.ut.wb.ui.TagUtil;
import com.ut.wb.ui.Utils;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameRankMoreEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHev on 2016/12/15.
 */

public class GameRankDetailAdapter
    extends RecyclerView.Adapter<GameRankDetailAdapter.GameRankDetailHolder> {
  private View mRootView;
  private GameRankDetailActivity mActivity;

  private List<GameRankMoreEntity> mData = new ArrayList<>();

  //页数
  private int mPage = 0;
  //一页总量
  private static final int pageCount = 10;
  //全部
  private static final int allPageCount = 20;
  private static final int RANK_MODE_SB = 1;      //手柄
  private static final int RANK_MODE_YKQ = 2;    //遥控器
  private static final int RANK_MODE_ALL = 3;    //都可以

  private int mCurrentPosition;

  @Override public GameRankDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank_detail, null);
    GameRankDetailHolder holder = new GameRankDetailHolder(view);
    return holder;
  }

  public GameRankDetailAdapter(Activity activity, View view, int page) {
    this.mActivity = (GameRankDetailActivity) activity;
    this.mRootView = view;
    //this.mPage = page;
  }

  public GameRankDetailAdapter(Context context, View view, List<GameRankMoreEntity> list,
      int page) {
    this.mActivity = (GameRankDetailActivity) context;
    this.mRootView = view;
    this.mPage = page;
    mData.clear();
    mData.addAll(list);
  }

  @Override public void onBindViewHolder(GameRankDetailHolder holder, int position) {
    if (mData != null && mData.size() > position) {
      GameRankMoreEntity gameRankMoreEntity = mData.get(position);
      TextView tvNum = (TextView) mActivity.findViewById(R.id.num);
      holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
        int num = position + 1 + mPage * pageCount;
        tvNum.setText(num + "/" + mActivity.getData().size());
        if (hasFocus) {
          v.animate().scaleX(1.15f).scaleY(1.15f).start();
          holder.mTvName.setVisibility(View.GONE);
          holder.mTvCover.setVisibility(View.VISIBLE);
          holder.mLabel.setVisibility(View.VISIBLE);
          holder.mTvCover.startMarquee();
          mCurrentPosition = position;
        } else {
          v.animate().scaleX(1.f).scaleY(1.f).start();
          holder.mTvName.setVisibility(View.VISIBLE);
          holder.mTvCover.setVisibility(View.GONE);
          holder.mLabel.setVisibility(View.GONE);
          holder.mTvCover.stopMarquee();
        }
      });
      holder.mTvName.setText(gameRankMoreEntity.productName);
      holder.mTvCover.setText(gameRankMoreEntity.productName);
      //if (mPage == 1) {
      //  setIconOrder(holder.mIvRankOrder, position);
      //}
      handleHotTag(holder.mIvLeft, gameRankMoreEntity.hotTag);
      handleTag(holder.mIvRight, gameRankMoreEntity.tag, gameRankMoreEntity.isBuy);
      setGameModeIcon(holder.mIvYaokong, holder.mIvShoubing, gameRankMoreEntity.productMode);

      holder.mTvNum.setText(Utils.downloadCountHelp(gameRankMoreEntity.downCount));
      ImageManager.getInstance().loadRoundedImg(holder.mIvBg, gameRankMoreEntity.imageUri, 20);
    }
  }

  /**
   * 处理左上角标签
   * 0：热门、1：新增、2：普通
   */
  protected void handleHotTag(ImageView img, int hotTag) {
    TagUtil.handleHotTag(img, hotTag);
  }

  /**
   * 处理标签
   * 0：免费、1：普通、2：内购、3：收费、4：限时免费
   */
  protected void handleTag(ImageView img, int tag, boolean isBuy) {
    TagUtil.handleTag(img, tag, isBuy);
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

  /**
   * 设置游戏类型icon
   */
  public void setGameModeIcon(ImageView ykqImg, ImageView sbImg, int mode) {
    switch (mode) {
      case RANK_MODE_SB:
        ykqImg.setVisibility(View.GONE);
        sbImg.setVisibility(View.VISIBLE);
        break;
      case RANK_MODE_YKQ:
        ykqImg.setVisibility(View.VISIBLE);
        sbImg.setVisibility(View.GONE);
        break;
      case RANK_MODE_ALL:
        sbImg.setVisibility(View.VISIBLE);
        ykqImg.setVisibility(View.VISIBLE);
        break;
    }
  }

  @Override public int getItemCount() {
    //if (mData == null) {
    //  return 0;
    //}
    //return 10;
    return mData == null ? 0 : mData.size();
  }

  //public void update(List<GameRankMoreEntity> gameRankMoreEntity, int page) {
  //  this.mData = gameRankMoreEntity;
  //  this.mPage = page;
  //  notifyDataSetChanged();
  //}

  public class GameRankDetailHolder extends RecyclerView.ViewHolder {
    MarqueTextView mTvCover;
    ImageView mIvYaokong;
    ImageView mIvShoubing;
    TextView mTvNum;
    ImageView mIvLeft;
    ImageView mIvRight;
    ImageView mIvBg;
    ImageView mIvRankOrder;
    MarqueTextView mTvName;
    LinearLayout mLabel;

    public GameRankDetailHolder(View view) {
      super(view);
      mTvCover = (MarqueTextView) itemView.findViewById(R.id.tv_cover_name);
      mIvYaokong = (ImageView) itemView.findViewById(R.id.ykq);
      mIvShoubing = (ImageView) itemView.findViewById(R.id.sb);
      mTvNum = (TextView) itemView.findViewById(R.id.num);
      mIvLeft = (ImageView) itemView.findViewById(R.id.iv_left_bg);
      mIvRight = (ImageView) itemView.findViewById(R.id.iv_right_bg);
      mTvName = (MarqueTextView) itemView.findViewById(R.id.tv_name);
      mIvBg = (ImageView) itemView.findViewById(R.id.iv_bg);
      mLabel = (LinearLayout) itemView.findViewById(R.id.rl_label);
      mIvRankOrder = (ImageView) itemView.findViewById(R.id.iv_rank_order_bg);
    }
  }
}
