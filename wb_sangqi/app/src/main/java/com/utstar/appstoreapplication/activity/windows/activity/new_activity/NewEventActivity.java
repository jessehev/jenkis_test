package com.utstar.appstoreapplication.activity.windows.activity.new_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.core.AbsFrame;
import com.ut.wb.ui.viewpager.SimpleViewPagerAdapter;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.ActivityNewEventBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.NewEventListEntity;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import com.utstar.appstoreapplication.activity.windows.main.MainActivity;

/**
 * lt
 * 最新活动窗口
 */
public class NewEventActivity extends BaseActivity<ActivityNewEventBinding> {
  private final static int NEWEVENTGET_SUCCESS = 1001;
  @Bind(R.id.new_event_vp) ViewPager mNewEventVp;
  @Bind(R.id.tv_count) TextView mTvCount;
  @Bind(R.id.ll_new_event) LinearLayout mLlNewEvent;
  @Bind(R.id.iv_await) ImageView mIvAwait;
  private int mNum; //总页数
  private int mCurrentPosition = 0;

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    getModule(NewEventModule.class).getData();
    mNewEventVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        NewEventMainFragment fragment =
            (NewEventMainFragment) ((SimpleViewPagerAdapter) mNewEventVp.getAdapter()).getItem(
                mNewEventVp.getCurrentItem());
        fragment.requestFirstFocused();
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });
  }

  @Override protected int setLayoutId() {
    return R.layout.activity_new_event;
  }

  @Override protected void dataCallback(int result, Object data) {
    super.dataCallback(result, data);
    if (result == NEWEVENTGET_SUCCESS) {
      NewEventListEntity listEntity = (NewEventListEntity) data;
      mNum = (int) Math.ceil((double) listEntity.getSize() / 4);
      SimpleViewPagerAdapter adapter = new SimpleViewPagerAdapter(getSupportFragmentManager());
      mLlNewEvent.setVisibility(mNum == 1 || mNum == 0 ? View.INVISIBLE : View.VISIBLE);
      setAwaitBg();
      for (int i = 0; i < mNum; i++) {
        NewEventMainFragment eventFragment = NewEventMainFragment.newInstance(i + 1, mNum);
        adapter.addFrag(eventFragment, "main");
        mNewEventVp.setAdapter(adapter);
      }
    }
  }

  /**
   * 如果没有配置最新活动设置敬请期待
   */
  private void setAwaitBg() {
    if (mNum == 0) {
      mIvAwait.setVisibility(View.VISIBLE);
      mIvAwait.setBackgroundResource(R.mipmap.icon_await);
    }
  }

  //处理上下页翻页
  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (mNum == 1) {
      return super.onKeyDown(keyCode, event);
    }
    if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_2) { // 下一页
      if (mCurrentPosition < mNum) {
        mNewEventVp.setCurrentItem(++mCurrentPosition);
      }
      return true;
    } else if (keyCode == KeyEvent.KEYCODE_PAGE_UP || keyCode == KeyEvent.KEYCODE_1) { //上一页
      if (mCurrentPosition > 0) {
        mNewEventVp.setCurrentItem(--mCurrentPosition);
      }
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override public void finish() {
    //TODO 判断是否购买成功
    //if (getCurrentFragment().getIsBuy()) {
    //  Intent intent = new Intent();
    //  intent.putExtra(TurnManager.UPDATE_KEY, true);
    //  intent.putExtra(TurnManager.POSITION_KEY, 1);//只有写死为1了。
    //  setResult(TurnManager.PAY_RESULT_CODE, intent);
    //}
    super.finish();
  }

  @Override public void onBackPressed() {
    if (AbsFrame.getInstance().activityExists(MainActivity.class)) {
      super.onBackPressed();
    } else {
      AbsFrame.getInstance().finishActivity(NewEventActivity.class);
      startActivity(new Intent(this, MainActivity.class));
    }
    super.onBackPressed();
  }

  public NewEventMainFragment getCurrentFragment() {
    return (NewEventMainFragment) ((SimpleViewPagerAdapter) mNewEventVp.getAdapter()).getItem(
        mNewEventVp.getCurrentItem());
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (data == null) return;
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == TurnManager.DRAW_REQUEST_CODE && resultCode == TurnManager.PAY_RESULT_CODE) {
      if (data.getBooleanExtra(TurnManager.UPDATE_KEY, false) || data.getBooleanExtra(
          TurnManager.PAY_SUCCESS_KEY, false)) {
        getCurrentFragment().setIsBuy(true);
      } else {
        getCurrentFragment().setIsBuy(false);
      }
    }
  }
}







