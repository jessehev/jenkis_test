package com.utstar.appstoreapplication.activity.windows.monthly_payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.core.AbsFrame;
import com.ut.wb.ui.viewpager.SimpleViewPagerAdapter;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.ActivityMothlyPayBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.MothlyPaySecondEntity;
import com.utstar.appstoreapplication.activity.manager.ActivityManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.utils.CommonUtil;
import com.utstar.appstoreapplication.activity.utils.IsUpdateIconUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import com.utstar.appstoreapplication.activity.windows.main.MainActivity;
import com.utstar.appstoreapplication.activity.windows.payment.PayWebView;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 套餐包详情页面
 */
public class MothlyPayActivity extends BaseActivity<ActivityMothlyPayBinding> {
  // TODO: 2016/12/23  套餐包跳转
  public static final String KEY_PACKAGE_ID = "KEY_PACKAGE_ID";
  public static final String KEY_GAME_ID = "KEY_GAME_ID";
  public static final String KEY_IS_BUY = "KEY_IS_BUY";
  public static final String KEY_BACK_TYPE = "KEY_BACK_TYPE";
  private static final int MOTHLY_PAY_CALL_SUCCESS = 1;
  @Bind(R.id.mothly_vp) ViewPager mMothlyVp;
  @Bind(R.id.ll_mothly_pay) LinearLayout mMothlyPay;
  @Bind(R.id.tv_mothly_name) TextView mTvMothlyName;
  @Bind(R.id.rl_rootview) public View mMothRoot;
  private String mPackageId;
  private String mProductId;
  private int num;  //总页数
  private int mCurrentPosition = 0;
  private MothlyPaySecondEntity mEntity;
  private SimpleViewPagerAdapter mAdapter;
  private List<MothlyPayMainFragment> mFragments = new ArrayList<>();
  private boolean isUpdate = false;
  //返回类型
  private int mBackType;
  //private boolean isPackageBuy; //套餐包是否购买成功

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    mPackageId = getIntent().getStringExtra(KEY_PACKAGE_ID);
    mProductId = getIntent().getStringExtra(KEY_GAME_ID);
    isUpdate = getIntent().getBooleanExtra(KEY_IS_BUY, false);
    //从套餐包返回后的跳转类型 0是跳转到每日任务
    mBackType = getIntent().getIntExtra(KEY_BACK_TYPE, -1);
    try {
      TextUtils.isEmpty(mPackageId);
    } catch (Exception e) {
      throw new IllegalArgumentException("套餐吧ID不能为null");
    }
    getModule(MothlyPayModule.class).getData(mPackageId, 1);
  }

  @Override protected int setLayoutId() {
    return R.layout.activity_mothly_pay;
  }

  @Override protected void dataCallback(int result, Object data) {
    super.dataCallback(result, data);
    if (result == MOTHLY_PAY_CALL_SUCCESS) {
      mEntity = (MothlyPaySecondEntity) data;
      mTvMothlyName.setText(mEntity.getName());
      mAdapter = new SimpleViewPagerAdapter(getSupportFragmentManager());
      num = (int) Math.ceil((double) mEntity.getSize() / 10);
      mMothlyPay.setVisibility(num == 1 ? View.INVISIBLE : View.VISIBLE);
      for (int i = 0; i < num; i++) {
        MothlyPayMainFragment fragment =
            MothlyPayMainFragment.newInstance(mPackageId, i + 1, num, mProductId);
        mFragments.add(fragment);
        mAdapter.addFrag(fragment, "main" + i);
        mMothlyVp.setAdapter(mAdapter);
      }
    }
  }

  @Override public void onBackPressed() {
    if (AbsFrame.getInstance().activityExists(MainActivity.class)) {
      super.onBackPressed();
    } else {
      AbsFrame.getInstance().finishActivity(MothlyPayActivity.class);
      startActivity(new Intent(this, MainActivity.class));
    }
    if (mBackType == 0) sendDayTaskMsg();
  }

  //发送广播 显示每日任务
  private void sendDayTaskMsg() {
    sendBroadcast(new Intent(ActivityManager.KEY_TASK_FILTER));
  }

  @Override public void finish() {
    if (isUpdate) {
      Intent intent = new Intent();
      intent.putExtra(TurnManager.UPDATE_KEY, true);
      setResult(TurnManager.PAY_RESULT_CODE, intent);
    }
    super.finish();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (data == null) return;
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == TurnManager.PAY_REQUEST_CODE
        && resultCode == TurnManager.PAY_RESULT_CODE
        && (isUpdate = IsUpdateIconUtil.isUpdateIcon(data))
        && mEntity != null
        && mFragments != null) {
      mEntity.setIsBuy(IsUpdateIconUtil.isBuySuccess(data));
      for (MothlyPayMainFragment fragment : mFragments) {
        fragment.updateBuyIcon(mEntity.isIsBuy());
        fragment.mAdapter.notifyDataSetChanged();
      }
    }
  }

  //处理上下页翻页
  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (num == 1) {
      return super.onKeyDown(keyCode, event);
    }
    if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {//下一页
      if (mCurrentPosition < num) {
        mMothlyVp.setCurrentItem(++mCurrentPosition);
        return true;
      }
    } else if (keyCode == KeyEvent.KEYCODE_PAGE_UP || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {//上一页
      if (mCurrentPosition > 0) {
        mMothlyVp.setCurrentItem(--mCurrentPosition);
        return true;
      }
    }
    return super.onKeyDown(keyCode, event);
  }
}














