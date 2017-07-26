package com.utstar.appstoreapplication.activity.windows.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.L;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.ut.wb.ui.TabLayout.TabLayout;
import com.ut.wb.ui.metro.MetroLayout;
import com.ut.wb.ui.viewpager.SimpleViewPagerAdapter;
import com.utstar.appstoreapplication.activity.BuildConfig;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.ActivityMainBinding;
import com.utstar.appstoreapplication.activity.manager.ActivityManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.utils.IsUpdateIconUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import com.utstar.appstoreapplication.activity.windows.game_hall.MetroFragment;
import com.utstar.appstoreapplication.activity.windows.game_search.SearchFragment;
import com.utstar.appstoreapplication.activity.windows.game_video.GameVideoFragment;
import com.utstar.appstoreapplication.activity.windows.my_game.my_game_detail.MyGameDetailActivity;
import com.utstar.appstoreapplication.activity.windows.recommend.RecommendFragment;
import com.utstar.appstoreapplication.activity.windows.system.SystemFragment;

import static com.utstar.appstoreapplication.activity.manager.ActivityManager.ACTION_DRAW_FINISH;
import static com.utstar.appstoreapplication.activity.manager.ActivityManager.KEY_TASK_FILTER;

/**
 * Created by Aria.Lao on 2016/12/9.
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {
  @Bind(R.id.tab) public TabLayout mTab;
  @Bind(R.id.vp) ViewPager mVp;
  @Bind(R.id.time) TextView mTime;
  @Bind(R.id.wifi) ImageView mWifi;
  private SimpleViewPagerAdapter mAdapter;
  private BroadcastReceiver mMainReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (intent != null) {
        String action = intent.getAction();
        switch (action) {
          case ACTION_DRAW_FINISH:
            handleSign(context, intent);
            break;
          case Intent.ACTION_CONFIGURATION_CHANGED:
            handleWifi(context);
            break;
          case KEY_TASK_FILTER:
            handDayTask(context);
            break;
        }
      }
    }

    /**
     * 抽奖活动结束-->弹出签到
     */
    private void handleSign(Context context, Intent intent) {
      String id = intent.getStringExtra(ActivityManager.PACKAGE_KEY);
      //TurnManager.getInstance().turnSign(context,ActivityManager.TURN_TYPE_PACKAGE, id);
      ActivityManager.getInstance().autoSign(context, id, 1);
    }

    /**
     * 破冰活动结束从套餐包返回显示每日任务
     * @param context
     *
     */
    private void handDayTask(Context context) {
      ActivityManager.getInstance()
          .getDayTaskInfo(context, ActivityManager.DAY_TASK_TYPE_NOMAL); //每日任务
    }

    /**
     * 处理wifi
     * @param context
     */
    private void handleWifi(Context context) {
      ConnectivityManager conMan =
          (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfo = conMan.getActiveNetworkInfo();
      if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
        mWifi.setVisibility(View.VISIBLE);
      } else {
        mWifi.setVisibility(View.GONE);
      }
    }
  };

  @Override protected int setLayoutId() {
    return R.layout.activity_main;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    mTab.setTab(getModule(MainModule.class).createTab());
    mTab.setSelected(0);
    getModule(MainModule.class).setTime(mTime);
    initVp();
    getModule(MainModule.class).showAnnouncement();
    mWifi.setVisibility(getModule(MainModule.class).wifiIsConnect() ? View.VISIBLE : View.GONE);
    getModule(MainModule.class).setWifiState(mWifi);
  }

  /**
   * 获取tab
   */
  public TabLayout getTabLayout() {
    return mTab;
  }

  @Override protected void dataCallback(int result, Object data) {
    super.dataCallback(result, data);
  }

  private void initVp() {
    //final SimpleViewPagerAdapter adapter = new SimpleViewPagerAdapter(getSupportFragmentManager());
    mAdapter = new SimpleViewPagerAdapter(getSupportFragmentManager());
    mAdapter.addFrag(RecommendFragment.newInstance(), "热门推荐");
    mAdapter.addFrag(MetroFragment.newInstance(MetroFragment.GAME_PACKAGE), "精品专区");
    mAdapter.addFrag(MetroFragment.newInstance(MetroFragment.GAME_HALL), "游戏大厅");
    //mAdapter.addFrag(GameVideoFragment.newInstance(), "游戏视频");
    mAdapter.addFrag(SearchFragment.newInstance(), "搜索");
    mAdapter.addFrag(SystemFragment.newInstance(), "系统");
    mVp.setAdapter(mAdapter);
    mVp.setOffscreenPageLimit(5);
    mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      }

      @Override public void onPageSelected(int position) {
        mTab.setSelected(position);
        //配置背景图
        BaseFragment fragment = getCurrentFragment();
        if (fragment != null) {
          fragment.changeBackground();
        }
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });
    mTab.setOnTabLayoutSelectedListener(new TabLayout.OnTabLayoutSelectedListener() {
      @Override public void onItemSelected(int position, View view) {
        mVp.setCurrentItem(position, true);
      }

      @Override public void onDown(int position, View view) {
        mTab.clearFocus();
        BaseFragment fragment = (BaseFragment) mAdapter.getItem(position);
        if (fragment instanceof SearchFragment) { //搜索
          ((SearchFragment) fragment).requestFocus();
        } else if (fragment instanceof RecommendFragment) { //推荐页
          ((RecommendFragment) fragment).requestFocus();
        } else if (fragment instanceof SystemFragment) {  //系统
          ((SystemFragment) fragment).requestFocus();
        } else if (fragment instanceof GameVideoFragment) { //游戏视频
          ViewPager pager = ((GameVideoFragment) fragment).getViewpager();
          if (pager != null && pager.getAdapter().getCount() > 0) {
            ((GameVideoFragment) fragment).requestFocus();
          } else {
            mTab.requestFocus();
          }
        } else if (fragment instanceof MetroFragment) { //游戏大厅、包月专区
          MetroLayout metro = ((MetroFragment) fragment).getMetro();
          if (metro != null && metro.getChildCount() > 0) {
            View v = metro.getFirstView();
            if (v != null) {
              v.requestFocus();
            } else {
              metro.requestChildFocus(0);
            }
          } else {
            L.w("metro 为 null，不做处理");
            mTab.requestFocus();
          }
        }
      }
    });
  }

  @Override protected void onResume() {
    super.onResume();
    IntentFilter filter = new IntentFilter();
    filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
    filter.addAction(ACTION_DRAW_FINISH);
    filter.addAction(ActivityManager.KEY_TASK_FILTER);
    if (mMainReceiver != null) {
      registerReceiver(mMainReceiver, filter);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (mMainReceiver != null) {
      unregisterReceiver(mMainReceiver);
    }
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN
        || keyCode == KeyEvent.KEYCODE_1
        || keyCode == KeyEvent.KEYCODE_2
        || keyCode == KeyEvent.KEYCODE_PAGE_UP
        || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
        || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
      if (event.getAction() == KeyEvent.ACTION_DOWN) {
        BaseFragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof MetroFragment) {
          return ((MetroFragment) currentFragment).onKeyDown(keyCode, event);
        } else if (currentFragment instanceof RecommendFragment
            && keyCode == KeyEvent.KEYCODE_DPAD_LEFT
            && ((RecommendFragment) currentFragment).isLeftView()) {
          return ((RecommendFragment) currentFragment).handleLeftViewKey(keyCode, event);
        } else if (currentFragment instanceof SystemFragment
            && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
            && ((SystemFragment) currentFragment).getCurrentPosition() == 1) {
          setTabSelected(0, true);
          return true;
        } else if (currentFragment instanceof GameVideoFragment) {
          return ((GameVideoFragment) currentFragment).onKeyDown(keyCode, event);
        }
      }
    }

    //热更新测试
    //if (keyCode == KeyEvent.KEYCODE_1 || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
    //  //startActivity(new Intent(this, TempActivity.class));
    //  startActivity(new Intent(this, TestVideoDetailActivity.class));
    //  //GameInstallDialog dialog = new GameInstallDialog(292);
    //  //dialog.show(getSupportFragmentManager(), "install_dialog");
    //} else if (keyCode == KeyEvent.KEYCODE_2) {
    //  startActivity(new Intent(this, TempActivity.class));
    //} else if (keyCode == KeyEvent.KEYCODE_3) {
    //  Intent intent = new Intent("com.android.action.START_IPTV");
    //  intent.putExtra("jumpURL", "http://192.168.5.3:8080/Wanba/EPG/v1.0.0/portal.jsp");
    //  sendBroadcast(intent);
    //  Toast.makeText(this, "sendBroadcast", Toast.LENGTH_SHORT).show();
    //}
    //
    //else
    // if (keyCode == KeyEvent.KEYCODE_4) {
    //  startActivity(new Intent(this, TempActivity_3.class));
    //}

    //else
    //if (keyCode == KeyEvent.KEYCODE_5) {
    //  startActivity(new Intent(this, TempActivity_4.class));
    //}
    //
    // else
    //if (keyCode == KeyEvent.KEYCODE_7) {
    //  //T.showLong(this, "这是最后的zip了");
    //  //AndroidUtils.reStartApp(BaseApp.application);
    //}

    if (BuildConfig.DEBUG) {
      if (keyCode == KeyEvent.KEYCODE_3) {
        //startActivity(new Intent(this, TempActivity.class));
      } else if (keyCode == KeyEvent.KEYCODE_4) {
        TurnManager.getInstance().turnMyGameDetail(this, MyGameDetailActivity.TYPE_MY_GAME);
      } else if (keyCode == KeyEvent.KEYCODE_5) {
        TurnManager.getInstance().turnMyGameDetail(this, MyGameDetailActivity.TYPE_DOWNLOADING);
      } else if (keyCode == KeyEvent.KEYCODE_6) {
        TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(),
            Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Download/patch_signed_7zip.apk");
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  private ServiceConnection mConnection;

  public BaseFragment getCurrentFragment() {
    try {
      return (BaseFragment) ((SimpleViewPagerAdapter) mVp.getAdapter()).getItem(
          mVp.getCurrentItem());
    } catch (Exception e) {
      FL.e(TAG, FL.getExceptionString(e));
      return null;
    }
  }

  public void setTabSelected(int position) {
    mTab.setSelected(position);
    mVp.setCurrentItem(position, true);
  }

  public void setTabSelected(int position, boolean cleanFocus) {
    if (cleanFocus) mTab.clearFocus();
    mTab.setSelected(position);
    mVp.setCurrentItem(position, true);
  }

  public void showRedSpot(int position, boolean show) {
    mTab.showRedSpot(position, show);
  }

  @Override public void onBackPressed() {
    if (onDoubleClickExit()) {
      exitApp();
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (data == null) return;
    super.onActivityResult(requestCode, resultCode, data);
    if ((requestCode == TurnManager.DRAW_REQUEST_CODE
        || requestCode == TurnManager.PAY_REQUEST_CODE)
        && resultCode == TurnManager.PAY_RESULT_CODE
        && IsUpdateIconUtil.isUpdateIcon(data)) {
      BaseFragment fragment = getCurrentFragment();
      BaseFragment currentFragment = null;
      if (fragment != null) {
        if (fragment instanceof RecommendFragment) {
          currentFragment = ((RecommendFragment) fragment).getCurrentFragment();
        } else if (fragment instanceof MetroFragment) {
          currentFragment = fragment;
        }
        //if (data.getIntExtra(TurnManager.POSITION_KEY, -1) == 1) {
        //  currentFragment = (BaseFragment) mAdapter.getItem(1);
        //}
        if (currentFragment != null) {
          currentFragment.updateData();
        }
      }
    }
  }
}
