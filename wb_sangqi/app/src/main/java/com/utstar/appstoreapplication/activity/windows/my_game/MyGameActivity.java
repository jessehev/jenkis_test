package com.utstar.appstoreapplication.activity.windows.my_game;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v17.leanback.widget.VerticalGridView;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.DownloadEntity;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.show.L;
import com.ut.wb.ui.adapter.RvItemClickSupport;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.ActivityMyGameBinding;
import com.utstar.appstoreapplication.activity.entity.db_entity.DownloadInfo;
import com.utstar.appstoreapplication.activity.entity.net_entity.MyGameDetailEntity;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.receiver.InstallReceiver;
import com.utstar.appstoreapplication.activity.utils.ApkUtil;
import com.utstar.appstoreapplication.activity.utils.DownloadHelpUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;

import butterknife.Bind;
import com.utstar.appstoreapplication.activity.windows.my_game.my_game_detail.MyGameDetailActivity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.litepal.crud.DataSupport;

/**
 * 我的游戏mian
 */
public class MyGameActivity extends BaseActivity<ActivityMyGameBinding> {
  static final int RESOPNSE_CALL_SUCCESS = 111;
  static final int MY_DATA = 0xaa2;
  static final int UPDATE_DATE = 0x125;

  //@Bind(R.id.my_game_vp) ViewPager mMyGameVp;
  //MyGameMainFragment mMainFragment;
  @Bind(R.id.my_game_vgv_have) VerticalGridView mMyGameVgvHave;
  @Bind(R.id.my_game_vgv_down) VerticalGridView mMyGameVgvDown;
  @Bind(R.id.my_game_vgv_update) VerticalGridView mMyGameVgvUpdate;
  @Bind(R.id.ll_my_game_have_outer) LinearLayout mllMyGamehave;
  @Bind(R.id.ll_my_game_down_outer) LinearLayout mllMyGameDown;
  @Bind(R.id.ll_my_game_update_outer) LinearLayout mllMyGameUpdate;
  @Bind(R.id.tv_have) TextView mTvHave;
  @Bind(R.id.tv_download) TextView mTvDownload;
  @Bind(R.id.tv_updated) TextView mTvUpdated;
  @Bind(R.id.ll_my_game_all) LinearLayout mllRoot;

  public MyGameOneAdapter mHaveGameAdapter, mDownloadAdapter, mUpdateAdapter;

  private List<MyGameDetailEntity> mDownGames = new ArrayList<>();
  private List<MyGameDetailEntity> mUpdateGameDate = new ArrayList<>();
  private List<MyGameDetailEntity> mMyHaveGames = new ArrayList<>();
  private MyGameMainModule mModule;

  private BroadcastReceiver mSysReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      String packageName = intent.getStringExtra(InstallReceiver.DATA_PKG_NAME);
      switch (action) {
        case InstallReceiver.ACTION_APK_UNINSTALL: //卸载
          updateHaveData(packageName);
          break;
        case InstallReceiver.ACTION_APK_INSTALL:   //安装
          updateHaveAndDownData(packageName);
          break;
      }
    }
  };
  private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      //可以通过intent获取到下载实体，下载实体中包含了各种下载状态
      DownloadEntity entity = intent.getParcelableExtra(Aria.ENTITY);
      switch (action) {
        case Aria.ACTION_COMPLETE: //下载完成
          updateDownloadCompleteData(entity);
          break;
        case Aria.ACTION_FAIL:     // 下载失败
          L.w("下载地址【" + entity.getDownloadUrl() + "】的apk文件下载失败");
          break;
      }
    }
  };

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    //initVp();
    mModule = getModule(MyGameMainModule.class);
    initView();
    updateAll();
  }

  private void initView() {
    mllMyGamehave.setSelected(true);
    mllMyGamehave.animate().scaleX(1.10f).scaleY(1.10f).start();
    initHaveGrid();
    initDownGrid();
    initUpdateGrid();
  }

  //private void initVp() {
  //  SimpleViewPagerAdapter adapter = new SimpleViewPagerAdapter(getSupportFragmentManager());
  //  mMainFragment = MyGameMainFragment.newInstance();
  //  adapter.addFrag(mMainFragment, "main");
  //  mMyGameVp.setAdapter(adapter);
  //}

  /**
   * 第三个 待更新 Grid
   */
  private void initUpdateGrid() {
    mUpdateAdapter = new MyGameOneAdapter(this, mUpdateGameDate);
    mMyGameVgvUpdate.setAdapter(mUpdateAdapter);
    RvItemClickSupport.addTo(mMyGameVgvUpdate)
        .setOnItemClickListener((recyclerView, position, v) -> {
          if (mUpdateGameDate == null || mUpdateGameDate.size() == 0) return;
          if (position < 0 || position >= mUpdateGameDate.size()) return;
          MyGameDetailEntity entity = mUpdateGameDate.get(position);
          if (entity.isTemp) return;
          if (entity.isLastItem) {
            TurnManager.getInstance().turnMyGameDetail(this, MyGameDetailActivity.TYPE_UPDATE);
          } else {
            MyGameDetailEntity myGameEntity = mUpdateGameDate.get(position);
            DownloadEntity entitye =
                DownloadHelpUtil.createDownloadEntity(myGameEntity.getDownloadUrl(),
                    myGameEntity.getPackageName());
            DownloadHelpUtil.createDownloadInfo(entitye.getDownloadPath(), myGameEntity);
            Aria.whit(this).load(entitye).start();
            updateNewGameAndDownload(entity.getPackageName());
            //updateAll();
            //T.showShort(getActivity(), "亲，请去管理中更新游戏哟");
          }
        });
    //TODO 处理焦点
    RvItemClickSupport.addTo(mMyGameVgvUpdate)
        .setOnItemKeyListenr((v, keyCode, position, event) -> {
          if (position == 3
              && keyCode == KeyEvent.KEYCODE_DPAD_LEFT
              && event.getAction() == KeyEvent.ACTION_DOWN) {
            requestFocus(mMyGameVgvDown, 5);
            setSelected(mllMyGameUpdate, mllMyGameDown);
            return true;
          } else if (position == 0
              && keyCode == KeyEvent.KEYCODE_DPAD_LEFT
              && event.getAction() == KeyEvent.ACTION_DOWN) {
            requestFocus(mMyGameVgvDown, 2);
            setSelected(mllMyGameUpdate, mllMyGameDown);
            return true;
          }
          return false;
        });
  }

  /**
   * 第二个 下载中 Grid
   */
  private void initDownGrid() {
    mDownloadAdapter = new MyGameOneAdapter(this, mDownGames);
    mMyGameVgvDown.setAdapter(mDownloadAdapter);
    RvItemClickSupport.addTo(mMyGameVgvDown).setOnItemClickListener((recyclerView, position, v) -> {
      if (mDownGames == null || mDownGames.size() == 0) return;
      if (position < 0 || position >= mDownGames.size()) return;
      MyGameDetailEntity entity = mDownGames.get(position);
      if (entity.isTemp) return;
      if (entity.isLastItem) {
        TurnManager.getInstance().turnMyGameDetail(this, MyGameDetailActivity.TYPE_DOWNLOADING);
      } else {
        TurnManager.getInstance().turnGameDetail(this, mDownGames.get(position).getGameId());
      }
    });
    RvItemClickSupport.addTo(mMyGameVgvDown).setOnItemKeyListenr((v, keyCode, position, event) -> {
      if (position == 5
          && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
          && event.getAction() == KeyEvent.ACTION_DOWN) {
        requestFocus(mMyGameVgvUpdate, 3);
        setSelected(mllMyGameDown, mllMyGameUpdate);
        return true;
      } else if (position == 2
          && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
          && event.getAction() == KeyEvent.ACTION_DOWN) {
        requestFocus(mMyGameVgvUpdate, 0);
        setSelected(mllMyGameDown, mllMyGameUpdate);
        return true;
      } else if (position == 3
          && keyCode == KeyEvent.KEYCODE_DPAD_LEFT
          && event.getAction() == KeyEvent.ACTION_DOWN) {
        requestFocus(mMyGameVgvHave, 5);
        setSelected(mllMyGameDown, mllMyGamehave);
        return true;
      } else if (position == 0
          && keyCode == KeyEvent.KEYCODE_DPAD_LEFT
          && event.getAction() == KeyEvent.ACTION_DOWN) {
        requestFocus(mMyGameVgvHave, 2);
        setSelected(mllMyGameDown, mllMyGamehave);
        return true;
      }
      return false;
    });
  }

  /**
   * 打一个 已有游戏 Grid
   */
  private void initHaveGrid() {
    mHaveGameAdapter = new MyGameOneAdapter(this, mMyHaveGames);
    mMyGameVgvHave.setAdapter(mHaveGameAdapter);
    RvItemClickSupport.addTo(mMyGameVgvHave).setOnItemClickListener((recyclerView, position, v) -> {
      if (mMyHaveGames == null || mMyHaveGames.size() == 0) return;
      if (position < 0 || position >= mMyHaveGames.size()) return;
      MyGameDetailEntity entity = mMyHaveGames.get(position);
      if (entity.isTemp) return;
      if (entity.isLastItem) {
        TurnManager.getInstance().turnMyGameDetail(this, MyGameDetailActivity.TYPE_MY_GAME);
      } else {
        if (mMyHaveGames.get(position).isAddPackage()) {
          TurnManager.getInstance().turnPackage(this, mMyHaveGames.get(position).getPackageId());
        } else {
          if (AndroidUtils.checkApkExists(this, mMyHaveGames.get(position).getPackageName())
              && !TextUtils.isEmpty(mMyHaveGames.get(position).getPackageName())) {
            //AndroidUtils.startOtherApp(this, mMyHaveGames.get(position).getPackageName());
            //ProbeService.start(this, mMyHaveGames.get(position).getPackageName());
            ApkUtil.startGame(this, mMyHaveGames.get(position).getPackageName());
          }
        }
      }
    });
    RvItemClickSupport.addTo(mMyGameVgvHave).setOnItemKeyListenr((v, keyCode, position, event) -> {
      if (position == 2
          && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
          && event.getAction() == KeyEvent.ACTION_DOWN) {
        requestFocus(mMyGameVgvDown, 0);
        setSelected(mllMyGamehave, mllMyGameDown);
        return true;
      } else if (position == 5
          && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
          && event.getAction() == KeyEvent.ACTION_DOWN) {
        requestFocus(mMyGameVgvDown, 3);
        setSelected(mllMyGamehave, mllMyGameDown);
        return true;
      }
      return false;
    });
  }

  /**
   * 设置选择
   */
  public void setSelected(LinearLayout formGrid, LinearLayout toGrid) {
    toGrid.setSelected(true);
    toGrid.animate().scaleX(1.10f).scaleY(1.10f).start();
    formGrid.setSelected(false);
    formGrid.animate().scaleX(1.0f).scaleY(1.0f).start();
    TextView tvCount = (TextView) findViewById(R.id.tv_my_game_count);
    if (toGrid.getId() == R.id.ll_my_game_down_outer) {
      tvCount.setText(" (2/3)");
    } else if (toGrid.getId() == R.id.ll_my_game_update_outer) {
      tvCount.setText(" (3/3)");
    } else if (toGrid.getId() == R.id.ll_my_game_have_outer) {
      tvCount.setText(" (1/3)");
    }
  }

  /**
   * 获取焦点
   */
  private void requestFocus(VerticalGridView grid, int index) {
    if (grid.getChildCount() > 0) {
      grid.getLayoutManager().getChildAt(index).requestFocus();
    }
  }

  /**
   * 更新下载完成的数据
   */
  private void updateDownloadCompleteData(DownloadEntity entity) {
    List<DownloadInfo> infos = DataSupport.findAll(DownloadInfo.class);
    DownloadInfo info = null;
    for (DownloadInfo temp : infos) {
      if (temp.getDownloadUrl().equals(entity.getDownloadUrl())) {
        info = temp;
        break;
      }
    }
    if (info != null) {
      MyGameDetailEntity detailEntity = MyGameUtil.convertDownloadInfo(info, entity);
      MyGameDetailEntity temp = mModule.findDetailEntity(detailEntity.getPackageName(), mDownGames);
      if (temp == null) {
        mDownGames.add(detailEntity);
        mDownloadAdapter.notifyDataSetChanged();
      }
    }
  }

  /**
   * 更新已有游戏中的数据列表
   */
  public void updateHaveData(String packageName) {
    MyGameDetailEntity temp = mModule.findDetailEntity(packageName, mMyHaveGames);
    if (temp != null) {
      mMyHaveGames.remove(temp);
      mMyHaveGames.add(mModule.createTempEntity());
      mHaveGameAdapter.notifyDataSetChanged();
    }
  }

  /**
   * 安装完成，更新下载中的游戏和已有游戏的数据
   */
  private void updateHaveAndDownData(String packageName) {
    MyGameDetailEntity temp = mModule.findDetailEntity(packageName, mDownGames);
    if (temp != null) {
      mMyHaveGames.add(temp);
      List<MyGameDetailEntity> reSort = mModule.sortList(mMyHaveGames);
      mMyHaveGames.clear();
      mMyHaveGames.addAll(reSort);
      mDownGames.remove(temp);
      mModule.mDownloadCount--;
      if (mModule.countContentSize(mDownGames) == 0) {
        List<MyGameDetailEntity> tempList = mModule.createTempList();
        mDownGames.clear();
        mDownGames.addAll(tempList);
        mDownloadAdapter.notifyDataSetChanged();
      } else if (mModule.mDownloadCount > 5) {
        mDownGames.add(mModule.createTempEntity());
        mModule.getDownloadingData();
      } else {
        mDownGames.add(mModule.createTempEntity());
        mDownloadAdapter.notifyDataSetChanged();
      }
      mHaveGameAdapter.notifyDataSetChanged();
      mTvHave.setText("已有游戏 (" + ++mModule.mMyGameCount + ")");
      mTvDownload.setText(
          "下载中 (" + (mModule.mDownloadCount < 0 ? 0 : mModule.mDownloadCount) + ")");
    }
  }

  /**
   * 点击待更新， 更新待更新和下载中的数据
   */
  private void updateNewGameAndDownload(String packageName) {
    MyGameDetailEntity temp = mModule.findDetailEntity(packageName, mUpdateGameDate);
    if (temp != null) {
      mDownGames.add(temp);
      List<MyGameDetailEntity> reSort = mModule.sortList(mDownGames);
      mDownGames.clear();
      mDownGames.addAll(reSort);
      mUpdateGameDate.remove(temp);
      mModule.mUpdateCount--;
      if (mModule.countContentSize(mUpdateGameDate) == 0) {
        List<MyGameDetailEntity> tempList = mModule.createTempList();
        mUpdateGameDate.clear();
        mUpdateGameDate.addAll(tempList);
        mUpdateAdapter.notifyDataSetChanged();
      } else if (mModule.mUpdateCount > 5) {
        mModule.getUpdate(1);
      } else {
        mUpdateGameDate.add(mModule.createTempEntity());
        mUpdateAdapter.notifyDataSetChanged();
      }
      mDownloadAdapter.notifyDataSetChanged();
      mTvDownload.setText("下载中 (" + mModule.mDownloadCount + ")");
      mTvUpdated.setText("待更新 (" + (mModule.mUpdateCount < 0 ? 0 : mModule.mUpdateCount) + ")");
    }
  }

  /**
   * 更新已有游戏，下载中，更新中数据列表
   */
  public void updateAll() {
    mModule.getMyGame(1);
    mModule.getDownloadingData();
    mModule.getUpdate(1);
  }

  @Override protected void onResume() {
    super.onResume();
    downLoadTaskReceiver();
    systemOperationReceiver();
  }

  /**
   * 系统操作的广播处理
   */
  private void systemOperationReceiver() {
    IntentFilter filter = new IntentFilter();
    filter.addDataScheme(getPackageName());
    filter.addAction(InstallReceiver.ACTION_APK_INSTALL);
    filter.addAction(InstallReceiver.ACTION_APK_UNINSTALL);
    if (mSysReceiver != null) {
      registerReceiver(mSysReceiver, filter);
    }
  }

  /**
   * 下载任务的广播处理
   */
  private void downLoadTaskReceiver() {
    IntentFilter filter = new IntentFilter();
    filter.addDataScheme(getPackageName());
    filter.addAction(Aria.ACTION_PRE);
    filter.addAction(Aria.ACTION_POST_PRE);
    filter.addAction(Aria.ACTION_RESUME);
    filter.addAction(Aria.ACTION_START);
    filter.addAction(Aria.ACTION_RUNNING);
    filter.addAction(Aria.ACTION_STOP);
    filter.addAction(Aria.ACTION_CANCEL);
    filter.addAction(Aria.ACTION_COMPLETE);
    filter.addAction(Aria.ACTION_FAIL);
    if (mDownloadReceiver != null) {
      registerReceiver(mDownloadReceiver, filter);
    }
  }

  @Override protected void dataCallback(int result, Object obj) {
    super.dataCallback(result, obj);
    if (result == RESOPNSE_CALL_SUCCESS) { //已有游戏
      mMyHaveGames.clear();
      mMyHaveGames.addAll((Collection<? extends MyGameDetailEntity>) obj);
      if (mMyHaveGames != null) {
        mTvHave.setText("已有游戏 (" + mModule.mMyGameCount + ")");
      }
      mHaveGameAdapter.notifyDataSetChanged();
    } else if (result == MY_DATA) { //下载中
      mDownGames.clear();
      mDownGames.addAll((Collection<? extends MyGameDetailEntity>) obj);
      int num = 0;
      if (mDownGames != null) {
        num = mModule.mDownloadCount;
      }
      mTvDownload.setText("下载中 (" + num + ")");
      mDownloadAdapter.notifyDataSetChanged();
    } else if (result == UPDATE_DATE) {//更新
      mUpdateGameDate.clear();
      mUpdateGameDate.addAll((Collection<? extends MyGameDetailEntity>) obj);
      if (mUpdateGameDate != null) {
        mTvUpdated.setText("待更新 (" + mModule.mUpdateCount + ")");
      }
      mUpdateAdapter.notifyDataSetChanged();
    }
  }

  @Override protected void onRestart() {
    super.onRestart();
    L.d(TAG, "restart");
  }

  @Override protected void onStop() {
    L.d(TAG, "onStop");
    super.onStop();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mDownloadReceiver);
    unregisterReceiver(mSysReceiver);
  }

  @Override protected int setLayoutId() {
    return R.layout.activity_my_game;
  }

  /**
   * 窗口反向传值
   */
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == TurnManager.MY_GAME_RESULT_CODE && resultCode == Activity.RESULT_OK) {
      updateAll();
    }
  }
}