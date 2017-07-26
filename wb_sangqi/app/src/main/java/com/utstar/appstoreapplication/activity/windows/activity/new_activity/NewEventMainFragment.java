package com.utstar.appstoreapplication.activity.windows.activity.new_activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.constants.CommonConstant;
import com.utstar.appstoreapplication.activity.databinding.FragmentNewEventMainBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.NewEventListEntity;
import com.utstar.appstoreapplication.activity.manager.ActivityManager;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import java.util.ArrayList;

/**
 * Created by lt on 2016/12/19.
 * 最新活动
 */
public class NewEventMainFragment extends BaseFragment<FragmentNewEventMainBinding>
    implements View.OnFocusChangeListener, View.OnClickListener {
  private int mPage;
  private int mNum;
  private final static int NEWEVENTGET_SUCCESS = 1001;
  @Bind(R.id.iv_item_new_01) ImageView mIvEventOne;
  @Bind(R.id.iv_item_new_02) ImageView mIvEventTwo;
  @Bind(R.id.iv_item_new_03) ImageView mIvEventThree;
  @Bind(R.id.iv_item_new_04) ImageView mIvEventFour;
  @Bind(R.id.rl_event_rootview) RelativeLayout mRlRootView;
  private ImageManager imageManager = ImageManager.getInstance();
  private ArrayList<ImageView> views;
  private NewEventListEntity eventListEntity;
  TextView tvCount;
  @Bind(R.id.iv_new_event_left) ImageView mIvNewEventLeft;
  @Bind(R.id.iv_new_event_right) ImageView mIvNewEventRight;
  private static float SCALE_X = 1.5f;
  private static float SCALE_Y = 1.8f;

  private boolean mIsBuy; //活动关联得套餐包是否购买

  public static NewEventMainFragment newInstance(int page, int num) {
    NewEventMainFragment newEventMainFragment = new NewEventMainFragment();
    Bundle bundle = new Bundle();
    bundle.putInt("page", page);
    bundle.putInt("num", num);
    newEventMainFragment.setArguments(bundle);
    return newEventMainFragment;
  }

  @Override protected void init(Bundle savedInstanceState) {
  }

  @Override protected void onDelayLoad() {
    super.onDelayLoad();
    Bundle bundle = getArguments();
    mPage = bundle.getInt("page", 0);
    mNum = bundle.getInt("num", 0);
    initView();
    setFocus();
    //getModule(NewEventModule.class).getPageData(mPage);
  }

  public void requestFirstFocused() {
    View view = views.get(0);
    if (view != null) {
      view.requestFocus();
    }
  }

  private void initView() {
    mIvEventOne.requestFocus();
    if (mPage == 1 && mNum != 1) {
      mIvNewEventLeft.setVisibility(View.INVISIBLE);
    } else if (mPage == mNum && mNum != 1) {
      mIvNewEventRight.setVisibility(View.INVISIBLE);
    } else if (mNum == 1) {
      mIvNewEventLeft.setVisibility(View.INVISIBLE);
      mIvNewEventRight.setVisibility(View.INVISIBLE);
    }
  }

  private void setFocus() {
    views = new ArrayList<>();
    mIvEventOne.setOnFocusChangeListener(this);
    mIvEventOne.setOnClickListener(this);
    views.add(mIvEventOne);
    mIvEventTwo.setOnFocusChangeListener(this);
    mIvEventTwo.setOnClickListener(this);
    views.add(mIvEventTwo);
    mIvEventThree.setOnFocusChangeListener(this);
    mIvEventThree.setOnClickListener(this);
    views.add(mIvEventThree);
    mIvEventFour.setOnFocusChangeListener(this);
    mIvEventFour.setOnClickListener(this);
    views.add(mIvEventFour);

    mRlRootView.post(() -> {
      int w = (int) (mRlRootView.getMeasuredWidth()
          - mRlRootView.getPaddingLeft()
          - mRlRootView.getPaddingRight()
          - getResources().getDimension(R.dimen.dimen_30dp));
      int h = (int) (mRlRootView.getMeasuredHeight()
          - mRlRootView.getPaddingTop()
          - mRlRootView.getPaddingBottom()
          - getResources().getDimension(R.dimen.dimen_30dp));

      for (ImageView img : views) {
        ViewGroup.LayoutParams lp = img.getLayoutParams();
        lp.width = w / 2;
        lp.height = h / 2;
        img.setLayoutParams(lp);
      }
      getModule(NewEventModule.class).getPageData(mPage);
    });
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_new_event_main;
  }

  @Override protected void dataCallback(int result, Object obj) {
    super.dataCallback(result, obj);
    if (result == NEWEVENTGET_SUCCESS) {
      eventListEntity = (NewEventListEntity) obj;
      tvCount = (TextView) getActivity().findViewById(R.id.tv_count);
      tvCount.setText(" (1/" + eventListEntity.getSize() + ")");
      if (eventListEntity.getSize() > 0 && eventListEntity.getList().size() == 1) {
        mIvEventOne.setVisibility(View.VISIBLE);
        imageManager.loadRoundedImg(mIvEventOne, eventListEntity.getList().get(0).getImageurl(),
            10);
        mIvEventTwo.setVisibility(View.INVISIBLE);
        mIvEventThree.setVisibility(View.INVISIBLE);
        mIvEventFour.setVisibility(View.INVISIBLE);
        if (mPage == 1) {
          mIvEventOne.setVisibility(View.VISIBLE);
          mIvEventTwo.setVisibility(View.GONE);
          mIvEventThree.setVisibility(View.GONE);
          mIvEventFour.setVisibility(View.GONE);
        }
      } else if (eventListEntity.getSize() > 0 && eventListEntity.getList().size() == 2) {
        mIvEventOne.setVisibility(View.VISIBLE);
        mIvEventTwo.setVisibility(View.VISIBLE);
        imageManager.loadRoundedImg(mIvEventOne, eventListEntity.getList().get(0).getImageurl(),
            10);
        imageManager.loadRoundedImg(mIvEventTwo, eventListEntity.getList().get(1).getImageurl(),
            10);
        mIvEventThree.setVisibility(View.INVISIBLE);
        mIvEventFour.setVisibility(View.INVISIBLE);
        if (mPage == 1) {
          mIvEventOne.setVisibility(View.VISIBLE);
          mIvEventTwo.setVisibility(View.VISIBLE);
        }
      } else if (eventListEntity.getSize() > 0 && eventListEntity.getList().size() == 3) {
        mIvEventOne.setVisibility(View.VISIBLE);
        mIvEventTwo.setVisibility(View.VISIBLE);
        mIvEventThree.setVisibility(View.VISIBLE);
        mIvEventFour.setVisibility(View.INVISIBLE);
        imageManager.loadRoundedImg(mIvEventOne, eventListEntity.getList().get(0).getImageurl(),
            10);
        imageManager.loadRoundedImg(mIvEventTwo, eventListEntity.getList().get(1).getImageurl(),
            10);
        imageManager.loadRoundedImg(mIvEventThree, eventListEntity.getList().get(2).getImageurl(),
            10);
      } else if (eventListEntity.getSize() > 0 && eventListEntity.getList().size() == 4) {
        mIvEventOne.setVisibility(View.VISIBLE);
        mIvEventTwo.setVisibility(View.VISIBLE);
        mIvEventThree.setVisibility(View.VISIBLE);
        mIvEventFour.setVisibility(View.VISIBLE);
        imageManager.loadRoundedImg(mIvEventOne, eventListEntity.getList().get(0).getImageurl(),
            10);
        imageManager.loadRoundedImg(mIvEventTwo, eventListEntity.getList().get(1).getImageurl(),
            10);
        imageManager.loadRoundedImg(mIvEventThree, eventListEntity.getList().get(2).getImageurl(),
            10);
        imageManager.loadRoundedImg(mIvEventFour, eventListEntity.getList().get(3).getImageurl(),
            10);
      }
    }
  }

  @Override public void onFocusChange(View v, boolean hasFocus) {
    setView(v);
    if (hasFocus) {
      setAnimations(v);
      setCountTitle(v);
    } else {
      ScaleAnimation animation =
          new ScaleAnimation(SCALE_X, 1.0f, SCALE_Y, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
              Animation.RELATIVE_TO_SELF, 0.0f);
      animation.setDuration(200);
      animation.setFillAfter(true);
      v.startAnimation(animation);
    }
  }

  private void setAnimations(View view) {
    if (view == mIvEventOne) {
      //1：中心点为左上角(放大)
      ScaleAnimation animation =
          new ScaleAnimation(1.0f, SCALE_X, 1.0f, SCALE_Y, Animation.RELATIVE_TO_SELF, 0.1f,
              Animation.RELATIVE_TO_SELF, 0.1f);
      animation.setDuration(450);
      animation.setFillAfter(true);
      view.startAnimation(animation);
    } else if (view == mIvEventTwo) {
      //2: 中心点为右上角（放大）
      ScaleAnimation animation =
          new ScaleAnimation(1.0f, SCALE_X, 1.0f, SCALE_Y, Animation.RELATIVE_TO_SELF, 0.9f,
              Animation.RELATIVE_TO_SELF, 0.1f);
      animation.setDuration(300);
      animation.setFillAfter(true);
      view.startAnimation(animation);
    } else if (view == mIvEventThree) {
      //3:中心点为左下角（放大）
      ScaleAnimation animation =
          new ScaleAnimation(1.0f, SCALE_X, 1.0f, SCALE_Y, Animation.RELATIVE_TO_SELF, 0.1f,
              Animation.RELATIVE_TO_SELF, 0.9f);
      animation.setDuration(300);
      animation.setFillAfter(true);
      view.startAnimation(animation);
    } else if (view == mIvEventFour) {
      //4:中心点为右下角（放大）
      ScaleAnimation animation =
          new ScaleAnimation(1.0f, SCALE_X, 1.0f, SCALE_Y, Animation.RELATIVE_TO_SELF, 0.9f,
              Animation.RELATIVE_TO_SELF, 0.9f);
      animation.setDuration(300);
      animation.setFillAfter(true);
      view.startAnimation(animation);
    }
  }

  private void setCountTitle(View v) {
    for (int i = 0; i < views.size(); i++) {
      if (v == views.get(i)) {
        int num = i + 1 + (mPage - 1) * 4;
        tvCount.setText("(" + num + "/" + eventListEntity.getSize() + ")");
      }
    }
  }

  private void setView(View v) {
    View parent = (View) v.getParent();
    v.bringToFront();
    parent.requestLayout();
    parent.invalidate();
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.iv_item_new_01:
        turnDetail(eventListEntity.list.get(0), 0);
        break;
      case R.id.iv_item_new_02:
        turnDetail(eventListEntity.list.get(1), 1);
        break;
      case R.id.iv_item_new_03:
        turnDetail(eventListEntity.list.get(2), 2);
        break;
      case R.id.iv_item_new_04:
        turnDetail(eventListEntity.list.get(3), 3);
        break;
    }
  }

  private void turnDetail(NewEventListEntity.NewEventEntity entity, int position) {
    switch (entity.getTypeId()) {
      case CommonConstant.TYPE_PIC: //图片
        break;
      case CommonConstant.TYPE_ACTIVITY://活动
        //showDrawAwardActivity(true); //和套餐包无关，所以直接传true
        break;
      case CommonConstant.TYPE_PACKAGE://套餐包
        switch (entity.typeActivity) {
          case "0":
            turnPackage(position); // 没有活动 直接进套餐包
            break;
          case "1": //关联抽奖
            mIsBuy = entity.isBuy;
            showDrawAwardActivity(getContext(), entity.isBuy, entity.getPackageId());
            break;
          case "2": //关联签到
            showSign();
            break;
        }
        break;
      case CommonConstant.TYPE_GAME://游戏详情
        turnGameDetail(position);
        break;
      case CommonConstant.TYPE_SIGN://签到
        showSign();
        break;
      case CommonConstant.TYPE_TASK:
        //每日任务
        ActivityManager.getInstance().getDayTaskInfo(getContext(), ActivityManager.DAY_TASK_TYPE_ACTIVITY);
        break;
      default:
        break;
    }
  }

  private void turnGameDetail(int num) {
    String productId = eventListEntity.getList().get(num).getProductid();
    if (!TextUtils.isEmpty(productId)) {
      TurnManager.getInstance().turnGameDetail(getContext(), Integer.parseInt(productId));
    }
  }

  private void turnPackage(int num) {
    String productid = eventListEntity.getList().get(num).getProductid();
    String packageId = eventListEntity.getList().get(num).getPackageId();
    if (!TextUtils.isEmpty(productid) && !TextUtils.isEmpty(packageId)) {
      // 如果套餐包关联了活动，则先进活动，（此处逻辑和套餐包关联活动一致，接口也一样，只会显示一次，如果要一直显示则后台逻辑需要变动）
      TurnManager.getInstance().turnPackage(getActivity(), packageId, productid);
    }
  }

  /**
   * 显示抽奖活动
   */
  public void showDrawAwardActivity(Context context, boolean isBuy, String packageId) {
    getModule(NewEventModule.class).showDrawAward(context, isBuy, packageId);
  }

  /**
   * 显示签到
   */
  public void showSign() {
    getModule(NewEventModule.class).autoSign(getContext());
  }

  public boolean getIsBuy() {
    return mIsBuy;
  }

  public void setIsBuy(boolean isBuy) {
    mIsBuy = isBuy;
  }
}
