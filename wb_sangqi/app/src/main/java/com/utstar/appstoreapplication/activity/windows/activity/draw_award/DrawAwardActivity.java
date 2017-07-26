package com.utstar.appstoreapplication.activity.windows.activity.draw_award;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.VerticalGridView;
import android.text.Html;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import com.arialyy.frame.util.show.FL;
import com.ut.wb.ui.adapter.RvItemClickSupport;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.widget.adve_banner.AdVerAdpater;
import com.utstar.appstoreapplication.activity.commons.widget.adve_banner.AdverView;
import com.utstar.appstoreapplication.activity.databinding.ActivityDrawAwardBinding;
import com.utstar.appstoreapplication.activity.entity.common_entity.UpdateIconEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.AdverEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.AwardListEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.DrawAwardEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.DrawAwardIconEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.LotteryEntity;
import com.utstar.appstoreapplication.activity.manager.ActivityManager;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.utils.ActionUtil;
import com.utstar.appstoreapplication.activity.windows.activity.new_activity.NewEventActivity;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * 砸蛋、抽奖活动
 */
public class DrawAwardActivity extends BaseActivity<ActivityDrawAwardBinding> {

  @Bind(R.id.title) TextView mRuleTitle;
  @Bind(R.id.iv_rule) ImageView mIvRule;
  @Bind(R.id.iv_main_bg) ImageView mMainbg;
  @Bind(R.id.iv_banner) ImageView mIvBanner;
  @Bind(R.id.hit_egg_grid) VerticalGridView mHitEggGrid;
  @Bind(R.id.award_grid) VerticalGridView mAwardGrid;
  @Bind(R.id.adverview_user_info) AdverView mAdverView;
  @Bind(R.id.rl_award) FrameLayout mRlAward;
  @Bind(R.id.award_grid_bg) ImageView mAwardGridBg;
  @Bind(R.id.tv_rule) TextView mRule;
  @Bind(R.id.tv_lottery_num) TextView mLotteryNum;
  @Bind(R.id.adverview_user_info_bg) ImageView mUserInfoBg;

  private List<List<AdverEntity>> mBannerDatas = new ArrayList<>();
  private List<AdverEntity> mBannerSubDatas = new ArrayList<>();

  /**
   * 奖品列表
   */
  public static final int LOTTERY_AWARD_LIST_RESULT = 0X34;
  /**
   * 中奖用户
   */
  public static final int LOTTERY_USER_LIST_RESULT = 0X35;
  /**
   * 抽奖
   */
  public static final int LOTTERY_DRAW_AWARD_RESULT = 0X36;

  /**
   * 跳转key
   */
  public static final String LOTTERY_IMG_LIST_KEY = "LotteryEntity";

  /**
   * 跳转类型
   */
  private int mTurnType;

  /**
   * 套餐包id
   */
  private String mPackageId;
  /**
   * 蛋所在的当前位置
   */
  private int mEggCurrentPosition = 0;
  /**
   * 关联的套餐包是否购买
   */
  private boolean isBuy;

  private DrawAwardIconEntity mAaIcon;

  private HitAwardAdapter mAwardAdapter;
  private HitEggAdapter mEggAdapter;

  private List<AwardListEntity> mAwardListEntity = new ArrayList<>();

  private LotteryEntity mLotteryEntity;

  private AdVerAdpater mAdVerAdpater;

  private boolean isLoadFinish = true;

  @Override protected int setLayoutId() {
    return R.layout.activity_draw_award;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    initGrid();
    initAnima();
    getIntentData();
    sendQuest();
    setListener();
  }

  public void initGrid() {
    mEggAdapter = new HitEggAdapter(this, mAaIcon);
    mAwardAdapter = new HitAwardAdapter(mAwardListEntity);
    mHitEggGrid.setAdapter(mEggAdapter);
    mAwardGrid.setAdapter(mAwardAdapter);
    mHitEggGrid.setSelectedPosition(2);
  }

  public void sendQuest() {
    getModule(DrawAwardModule.class).getLotteryUserList();
    getModule(DrawAwardModule.class).getLotteryAwardList();
  }

  public void getIntentData() {
    mLotteryEntity = (LotteryEntity) getIntent().getExtras().getSerializable(LOTTERY_IMG_LIST_KEY);
    mTurnType = getIntent().getExtras().getInt(ActivityManager.TYPE_KEY, -1);
    mPackageId = getIntent().getExtras().getString(ActivityManager.PACKAGE_KEY, "");
    isBuy = getIntent().getExtras().getBoolean(ActivityManager.PACKAGE_ISBUY_KEY, false);
    mAaIcon = getModule(DrawAwardModule.class).handleImg(mLotteryEntity.list);
    mEggAdapter.updata(mAaIcon);
    setImg(mAaIcon);
    setUIData();
  }

  /**
   * 生成用户滚动信息
   */
  public void createBanner() {
    mAdVerAdpater = new AdVerAdpater(mBannerDatas);
    mAdverView.setAdapter(mAdVerAdpater);
    //开启线程
    mAdverView.stop();
    mAdverView.start();
  }

  public void initAnima() {
    mRlAward.animate().scaleX(1.08f).scaleY(1.08f).start();
  }

  public void setListener() {
    RvItemClickSupport.addTo(mHitEggGrid).setOnItemKeyListenr((v, keyCode, position, event) -> {
      if (event.getAction() == KeyEvent.ACTION_DOWN) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
          mEggCurrentPosition = position;
          mRlAward.requestFocus();
          return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
          if (position == 0) {
            return true;
          }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
          if (position == mHitEggGrid.getLayoutManager().getChildCount() - 1) {
            return true;
          }
        }
      }
      return false;
    });

    mRlAward.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        v.animate().scaleX(1.15f).scaleY(1.15f).setDuration(300).start();
        ImageManager.getInstance().setImg(mAwardGridBg, mAaIcon.imageType8);
      } else {
        v.animate().scaleX(1.08f).scaleY(1.08f).setDuration(300).start();
        ImageManager.getInstance().setImg(mAwardGridBg, mAaIcon.imageType7);
      }
    });
    RvItemClickSupport.addTo(mAwardGrid).setOnItemKeyListenr((v, keyCode, position, event) -> {
      if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
        mHitEggGrid.requestFocus();
        mHitEggGrid.setSelectedPosition(mEggCurrentPosition);
        return true;
      }
      return false;
    });

    RvItemClickSupport.addTo(mAwardGrid).setOnItemFocusChangeListener((v, position, hasFocus) -> {
      if (hasFocus) {
        mAwardGrid.animate().scaleX(1.15f).scaleY(1.15f).setDuration(300).start();
      } else {
        mAwardGrid.animate().scaleX(1.05f).scaleY(1.05f).setDuration(300).start();
      }
    });

    mRlAward.setOnClickListener(v -> {
      if (mAwardListEntity != null && mAwardListEntity.size() > 0) {
        AwardListDialog awDialog =
            new AwardListDialog(DrawAwardActivity.this, mAwardListEntity, mAaIcon);
        awDialog.show();
      }
    });

    RvItemClickSupport.addTo(mHitEggGrid).setOnItemClickListener((recyclerView, position, v) -> {
      if (!getModule(DrawAwardModule.class).isFastDoubleClick()) {
        if (getModule(DrawAwardModule.class).getToastNum(mLotteryNum) > 0) {
          //TODO 圣剑抽奖活动改版，需要判断是否购买，若没购买，点抽奖进入订购页面；
          if (isBuy) {
            getModule(DrawAwardModule.class).drawAward();
            Handler handler = new Handler();
            ImageView bg = (ImageView) v.findViewById(R.id.iv_egg_bg);
            ImageView hammer = (ImageView) v.findViewById(R.id.iv_hammer_bg);
            getModule(DrawAwardModule.class).startAnima(mAaIcon, handler, bg, hammer, mLotteryNum);
          } else {
            //TODO 跳转订购
            TurnManager.getInstance().turnOrderDetail(DrawAwardActivity.this, 0, mPackageId);
          }
        } else {
          getModule(DrawAwardModule.class).showDefautDialog(DrawAwardActivity.this);
        }
      }
    });
  }

  /**
   * 显示中奖弹框
   */
  public void showAwardInfoDialog(DrawAwardEntity drawAwardEntity, DrawAwardIconEntity iconEntity) {
    AwardInfoDialog aiDiaolog =
        new AwardInfoDialog(DrawAwardActivity.this, drawAwardEntity, iconEntity);
    aiDiaolog.setOnDismissListener(dialog -> {
      mEggAdapter.notifyDataSetChanged();
      //显示领奖提示框
      getModule(DrawAwardModule.class).showSignHint(DrawAwardActivity.this);
    });

    if (!isDestroyed() && !isFinishing()) {
      aiDiaolog.show();
    }
  }

  @Override protected void dataCallback(int result, Object data) {
    super.dataCallback(result, data);
    if (result == LOTTERY_AWARD_LIST_RESULT) {//奖品列表
      mAwardListEntity = (List<AwardListEntity>) data;
      mAwardAdapter.update(mAwardListEntity);
    } else if (result == LOTTERY_USER_LIST_RESULT) {//中奖用户列表
      mBannerSubDatas = (List<AdverEntity>) data;
      if (mBannerSubDatas != null && mBannerSubDatas.size() > 0) {
        //生成用户轮播
        setData();
        createBanner();
      }
    } else if (result == LOTTERY_DRAW_AWARD_RESULT) { //抽奖
      // DrawAwardEntity drawAwardEntity = (DrawAwardEntity) data;
      if (data != null) {
        isLoadFinish = true;
        new Handler().postDelayed(() -> {
          getModule(DrawAwardModule.class).getLotteryUserList();
          showAwardInfoDialog((DrawAwardEntity) data, mAaIcon);
        }, 1500);
      }
    }
  }

  /**
   * 设置界面所有图片
   */
  public void setImg(DrawAwardIconEntity aaIcon) {
    ImageManager imageManeger = ImageManager.getInstance();
    imageManeger.setImg(mMainbg, aaIcon.imageType1);
    imageManeger.setImg(mIvRule, aaIcon.imageType10);
    imageManeger.setImg(mUserInfoBg, aaIcon.imageType12);
    imageManeger.setImg(mAwardGridBg, aaIcon.imageType7);
    imageManeger.setImg(mIvBanner, aaIcon.imageType2);
  }

  public void setUIData() {
    mRule.setText(Html.fromHtml(mLotteryEntity.rule));
    mLotteryNum.setText(mLotteryEntity.everyDayRecords);
  }

  public void testData() {
    for (int i = 0; i < 9; i++) {
      mBannerSubDatas.add(new AdverEntity("用户ID:12345678" + i + "  获得 ", "iphone 6s"));
    }
  }

  public void setData() {
    for (int i = 0; i < mBannerSubDatas.size(); i += 2) {
      if (mBannerSubDatas.size() >= i + 2) {
        mBannerDatas.add(mBannerSubDatas.subList(i, i + 2));
      } else {
        List<AdverEntity> list = new ArrayList();
        list.add(mBannerSubDatas.get(i));
        mBannerDatas.add(list);
      }
    }
  }

  @Override public void finish() {
    setResult(TurnManager.DRAW_RESULT_CODE);
    //如果是精品专区的跳转则发送相应广播
    if (mTurnType == ActivityManager.TURN_TYPE_SIGN) {
      Intent intent = new Intent(ActivityManager.ACTION_DRAW_FINISH);
      intent.putExtra(ActivityManager.PACKAGE_KEY, mPackageId);
      sendBroadcast(intent);
    } else if (mTurnType == ActivityManager.TURN_TYPE_PACKAGE
        || mTurnType == ActivityManager.TURN_TYPE_ACTIVITY) {
      TurnManager.getInstance().turnPackage(this, mPackageId, isBuy);
      refreshIcon();
    } else if (mTurnType == ActivityManager.TUEN_TYPE_EPG) {
      startActivity(new Intent(this, NewEventActivity.class));
    }
    //中奖用户发送系统消息
    ActionUtil.updateSysMsgStatus(DrawAwardActivity.this);
    super.finish();
  }

  /**
   * 刷新订购状态图标
   */
  public void refreshIcon() {
    try {
      UpdateIconEntity entity = new UpdateIconEntity(isBuy, isBuy);
      Intent intent = new Intent();
      intent.putExtra(TurnManager.UPDATE_ICON_KEY, entity);
      intent.putExtra(TurnManager.PAY_SUCCESS_KEY, isBuy);
      setResult(TurnManager.PAY_RESULT_CODE, intent);
      super.finish();
    } catch (Exception e) {
      FL.d(TAG, "GameDetail finish ggg");
      FL.d(TAG, FL.getExceptionString(e));
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == TurnManager.PAY_REQUEST_CODE
        && resultCode == TurnManager.PAY_RESULT_CODE
        && data != null
        && data.getBooleanExtra(TurnManager.PAY_SUCCESS_KEY, false)) {
      DrawAwardActivity.this.isBuy = true;
      //TODO 弹出推半价优惠荐页
      ActivityManager.getInstance().getPackageData(this, ActivityManager.SWICH_OFF);
    }
  }
}
