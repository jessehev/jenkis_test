package com.utstar.appstoreapplication.activity.commons.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ut.wb.ui.MarqueTextView;
import com.ut.wb.ui.TagUtil;
import com.ut.wb.ui.Utils;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameClassifyEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.windows.game_hall.game_classify.GameClassifyActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHev on 2016/12/20.
 */

public class HorizontalView extends LinearLayout {
  private static final String TAG = "HorizontalView";

  private static final int MODE_SB = 1;      //手柄
  private static final int MODE_YKQ = 2;    //遥控器
  private static final int MODE_ALL = 3;    //都可以

  private static final Object LOCK = new Object();
  private List<View> mChilds = new ArrayList<>();
  private int mCurrentPosition = 0;
  private WBNavView mWbNavView;
  //WBNavView 当前所在的位置
  private int mWbPosition;

  private GameClassifyActivity mActivity;

  private Context mContext;
  //当前view在列表中的位置
  private int mPage = 0;
  //列表中的总数
  private int mCount;

  private ArrayList<GameClassifyEntity.GameClassifySubEntity> mGameClassifySubEntity;

  private int mRadius;

  public HorizontalView(Context context) {
    super(context);
    init(context);
  }

  public HorizontalView(Context context, AttributeSet attrs) {
    super(context, attrs);

    init(context);
  }

  public HorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  public void init(Context context) {
    LayoutInflater.from(context).inflate(R.layout.layout_classify, this);
    mContext = context;
    mRadius = (int) context.getResources().getDimension(R.dimen.dimen_10dp);
    mChilds.add(getView(R.id.layout_1));
    mChilds.add(getView(R.id.layout_2));
    mChilds.add(getView(R.id.layout_3));
    mChilds.add(getView(R.id.layout_4));
    mChilds.add(getView(R.id.layout_5));
    mChilds.add(getView(R.id.layout_6));
    mChilds.add(getView(R.id.layout_7));
    mChilds.add(getView(R.id.layout_8));

    for (int i = 0; i < mChilds.size(); i++) {
      childsFoucsListener(mChilds.get(i), i);
    }
  }

  private <T extends View> T getView(int id) {
    return (T) findViewById(id);
  }

  private void childsFoucsListener(final View v, final int position) {
    v.setOnFocusChangeListener((v1, hasFocus) -> {
      MarqueTextView cover = (MarqueTextView) v.findViewById(R.id.tv_cover_name);
      MarqueTextView tvName = (MarqueTextView) v.findViewById(R.id.tv_name);
      LinearLayout label = (LinearLayout) v.findViewById(R.id.rl_label);
      if (hasFocus) {

        v.animate().scaleX(1.15f).scaleY(1.15f).setDuration(300).start();

        mCurrentPosition = position;

        tvName.setVisibility(GONE);
        cover.setVisibility(VISIBLE);
        label.setVisibility(VISIBLE);
        cover.startMarquee();
        if (mActivity != null) {
          TextView tvNum = (TextView) mActivity.findViewById(R.id.num);
          if (mActivity instanceof GameClassifyActivity) {
            int num = position + mActivity.getCurrentPosition() * 8 + 1;
            synchronized (LOCK) {
              tvNum.setText("(" + num + "/" + mActivity.getItemGameCount() + ")");
            }
            //tvNum.setText("(" + num + "/" + mActivity.getItemGameCount() + ")");
          }
        }
      } else {
        v.animate().scaleX(1.f).scaleY(1.f).setDuration(300).start();
        cover.stopMarquee();
        tvName.setVisibility(VISIBLE);
        cover.setVisibility(GONE);
        label.setVisibility(GONE);
      }
    });
    v.setOnKeyListener((v13, keyCode, event) -> {
      if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
          && event.getAction() == KeyEvent.ACTION_DOWN
          && mActivity.getCurrentPosition() == 0) {
        //当前位置在第一行、第二行的第一个才选中左边导航
        if (mCurrentPosition % 4 == 0) {
          mWbNavView.setItemSelected(mWbPosition);
        }
      } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
        if (mCurrentPosition >= 0 && mCurrentPosition < 4) return true;
      } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
          && event.getAction() == KeyEvent.ACTION_DOWN) {
        if (mCurrentPosition >= 4 && mCurrentPosition < 7) return true;
      }
      return false;
    });
    v.setOnClickListener(v12 -> {
      if (mGameClassifySubEntity != null) {
        GameClassifyEntity.GameClassifySubEntity data = mGameClassifySubEntity.get(position);
        //if (mGameClassifySubEntity.get(position).isAddPackage) {
        //  TurnManager.getInstance().turnPackage(mContext, data.packageId + "", data.productId);
        //} else {
        //  //TurnManager.getInstance().turnGameDetail(mContext, data.productId);
        //  TurnManager.getInstance().turnGameDetail(mContext, Integer.parseInt(data.productId));
        //}
        TurnManager.getInstance().turnGameDetail(mContext, Integer.parseInt(data.productId));
      }
    });
  }

  /**
   * 设置wb对象 及wb当前位置
   */
  public void setWBNavView(WBNavView wbNavView, int wbPosition) {
    this.mWbNavView = wbNavView;
    this.mWbPosition = wbPosition;
  }

  public int getCurrentPosition() {
    return mCurrentPosition;
  }

  public View getChildView(int position) {
    if (position > mChilds.size() || position < 0) {
      Log.d(TAG, "child下标错误！");
      return null;
    }
    return mChilds.get(position);
  }

  public void setActivity(GameClassifyActivity activity) {
    this.mActivity = activity;
  }

  /**
   * tag ==>       //标签
   * 0：免费、1：普通、2：内购、3：收费、4：限时免费
   *
   * hotTag ==>    //左上角标签
   * 0：热门、1：新增、2：普通
   */

  /**
   * 设置View数据
   */
  public void setViewValue(
      ArrayList<GameClassifyEntity.GameClassifySubEntity> gameClassifySubEntity) {
    this.mGameClassifySubEntity = gameClassifySubEntity;
    for (int i = 0; i < mChilds.size(); i++) {
      RelativeLayout view = (RelativeLayout) mChilds.get(i);
      Holder holder = new Holder(view);
      if (gameClassifySubEntity != null && gameClassifySubEntity.size() > i) {
        if (gameClassifySubEntity.size() >= 8) {
          mCount = 8;
        } else {
          mCount = gameClassifySubEntity.size();
        }
        view.setVisibility(VISIBLE);
        GameClassifyEntity.GameClassifySubEntity data = gameClassifySubEntity.get(i);
        holder.mTvName.setText(data.productName);
        holder.mTvCover.setText(data.productName);

        ImageManager.getInstance().loadRoundedImg(holder.mIvBg, data.imageUri, mRadius);

        handleTag(holder.mIvRight, data.tag, data.isBuy);
        handleHotTag(holder.mIvLeft, data.hotTag);

        holder.mTvNum.setText(Utils.downloadCountHelp(data.downCount));

        handleMode(data.mode, holder);
      } else {
        view.setVisibility(INVISIBLE);
      }
    }
  }

  /**
   * 游戏类型
   */
  public void handleMode(int mode, Holder holder) {
    switch (mode) {
      case MODE_SB:
        holder.mIvShoubing.setVisibility(VISIBLE);
        holder.mIvYaokong.setVisibility(GONE);
        break;
      case MODE_YKQ:
        holder.mIvShoubing.setVisibility(GONE);
        holder.mIvYaokong.setVisibility(VISIBLE);
        break;
      case MODE_ALL:
        holder.mIvShoubing.setVisibility(VISIBLE);
        holder.mIvYaokong.setVisibility(VISIBLE);
        break;
      default:
        break;
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
   * 0：免费、1：普通、2：内购、3：收费、4：限时免费、5：周三限费
   */
  protected void handleTag(ImageView img, int tag, boolean isBuy) {
    TagUtil.handleTag(img, tag, isBuy);
  }

  class Holder {
    MarqueTextView mTvCover;
    ImageView mIvYaokong;
    ImageView mIvShoubing;
    TextView mTvNum;
    ImageView mIvLeft;
    ImageView mIvRight;
    ImageView mIvBg;
    MarqueTextView mTvName;
    LinearLayout mLabel;

    public Holder(View itemView) {
      mTvCover = (MarqueTextView) itemView.findViewById(R.id.tv_cover_name);
      mIvYaokong = (ImageView) itemView.findViewById(R.id.ykq);
      mIvShoubing = (ImageView) itemView.findViewById(R.id.sb);
      mTvNum = (TextView) itemView.findViewById(R.id.num);
      mIvLeft = (ImageView) itemView.findViewById(R.id.iv_left_bg);
      mIvRight = (ImageView) itemView.findViewById(R.id.iv_right_bg);
      mTvName = (MarqueTextView) itemView.findViewById(R.id.tv_name);
      mIvBg = (ImageView) itemView.findViewById(R.id.iv_bg);
      mLabel = (LinearLayout) itemView.findViewById(R.id.rl_label);
    }
  }

  @Override public void setOnKeyListener(OnKeyListener l) {

    super.setOnKeyListener(l);
  }
}
