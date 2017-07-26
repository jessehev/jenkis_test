package com.utstar.appstoreapplication.activity.windows.activity.day_task;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.widget.VerticalGridView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.arialyy.frame.core.AbsActivity;
import com.arialyy.frame.core.AbsFrame;
import com.arialyy.frame.util.SharePreUtil;
import com.arialyy.frame.util.StringUtil;
import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.T;
import com.ut.wb.ui.adapter.RvItemClickSupport;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.ActivityTaskBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.DayTaskEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.utils.ApkUtil;
import com.utstar.appstoreapplication.activity.windows.activity.sign.TipsDialog;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import java.util.Stack;

/**
 * Created by JesseHev on 2017/4/13.
 *
 * 每日任务
 */
public class DayTaskActivity extends BaseActivity<ActivityTaskBinding> {
  @Bind(R.id.game_icon) ImageView mGameIcon;
  @Bind(R.id.task_content) TextView mTaskContent;//任务内容
  @Bind(R.id.play_game) Button mPlayGame;
  @Bind(R.id.current_task) TextView mCurrentTask;//当前任务进度
  @Bind(R.id.grid_progress) VerticalGridView mGridProgress;//任务进度
  @Bind(R.id.gird_award) VerticalGridView mGirdAward;//奖品列表

  public DayTaskAwardAdapter mAwardAdapter;
  private DayTaskProgressAdapter mProgressAdapter;
  private DayTaskEntity mEntity;
  //数据传递key
  public static final String DAYTASK_ENTITY_KEY = "ENTITY_KEY";

  public static final String TAG = "DayTaskActivity";
  //请求 成功回调码
  public static final int AWARD_RESULT = 0x020;
  //奖品待领取状态
  public static final String RECEIVE = "-1";
  //奖品id
  private int mAwardId;
  //任务编号
  private int mNum;
  //游戏包名
  private String mPackageName;
  //游戏包名key
  public static String PCK_KEY = "GAME_PACKAGE";
  //共享文件文件名
  public static String FILE_NAME_TASK = "DAY_TASK_ENTITY";
  //共享文件实体key
  public static String SHARE_DAY_TASK_KEY = "SHARE_DAT_TASK_KEY";

  @Override protected int setLayoutId() {
    return R.layout.activity_task;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    initData();
    initGrid();
    setUIData();
    setLister();
  }

  @Override protected void onResume() {
    super.onResume();
  }

  public void initData() {
    mEntity = getIntent().getParcelableExtra(DAYTASK_ENTITY_KEY);
    mPackageName = getIntent().getExtras().getString(PCK_KEY);
    getModule(DayTaskModule.class).saveObject(this, mEntity, mPackageName);
    if (mEntity == null) {
      FL.e(TAG, "每日任务实体为空！");
      throw new IllegalArgumentException("任务实体不能为null！");
    }
  }

  public void changeData(DayTaskEntity entity) {
    mEntity = entity;
  }

  @Override protected void dataCallback(int result, Object data) {
    super.dataCallback(result, data);
    if (result == AWARD_RESULT) {
      // 领取奖励
      DayTaskEntity entity = (DayTaskEntity) data;
      if (entity.hasPrizes) {
        // 领取成功
        TipsDialog tdDialog = new TipsDialog(this, entity.promptMsg);
        tdDialog.setOnEnterClickLister(() -> {
          getModule(DayTaskModule.class).saveWinInfo(DayTaskActivity.this, mAwardId,
              tdDialog.getEditText(), mNum, mPackageName);
          tdDialog.dismiss();
        });
        tdDialog.show();
      } else {
        if (entity != null && entity.awardList != null && entity.awardList.size() > 0) {
          for (DayTaskEntity.TaskSubEntity task : entity.awardList) {
            if (task.awardId == mAwardId) {
              DayTaskMsgDialog tmDialog = new DayTaskMsgDialog(this, task.unMsg);
              tmDialog.show();
              mAwardAdapter.updata(entity.awardList);
            }
          }
          getModule(DayTaskModule.class).saveObject(this, (DayTaskEntity) data, mPackageName);
        }
      }
    }
  }

  private void initGrid() {
    mAwardAdapter = new DayTaskAwardAdapter(this, mEntity.awardList);
    mProgressAdapter = new DayTaskProgressAdapter(this, mEntity.totalTask, mEntity.currentTask,
        mEntity.isFinished);
    mGirdAward.setAdapter(mAwardAdapter);
    //mAwardAdapter.setOnSureLister(this);
    mGridProgress.setAdapter(mProgressAdapter);
    mGridProgress.setFocusable(false);
  }

  private void setUIData() {
    ImageManager.getInstance().loadRoundedImg(mGameIcon, mEntity.gameUrl, 18);
    String str = "当前进度 : 任务" + mEntity.currentTask;
    mCurrentTask.setText(
        StringUtil.highLightStr(str, "任务" + mEntity.currentTask, Color.parseColor("#FEF640")));
    mTaskContent.setText(mEntity.taskDescribe);

    boolean isFocus = getModule(DayTaskModule.class).checkFoucus(mEntity);
    if (isFocus) {
      requestAward(mEntity);
    } else if (!mEntity.isFinished) {
      requestBtn();
    }
    if (mEntity.isFinished) {
      mPlayGame.setClickable(false);
      mPlayGame.setFocusable(false);
      mPlayGame.setBackgroundResource(R.mipmap.bg_task_complete);
    } else {
      mPlayGame.setClickable(true);
      mPlayGame.setFocusable(true);
      mPlayGame.setBackgroundResource(R.drawable.selector_task_play_bt);
      //  requestBtn();
    }
  }

  @Override public void finish() {
    try {
      Stack<AbsActivity> stack = AbsFrame.getInstance().getActivityStack();
      for (int i = 0; i < stack.size(); i++) {
        if (stack.get(i).getClass().getName().equals(DayTaskActivity.class.getName())) {
          if (!TextUtils.isEmpty(mPackageName)) {
            ApkUtil.startGame(this, mPackageName);
            //AndroidUtils.startOtherApp(this, mPackageName);
          }
          super.finish();
          return;
        }
      }
    } catch (Exception e) {
      FL.d(TAG, FL.getExceptionString(e));
      super.finish();
    }
    super.finish();
  }

  @OnClick(R.id.play_game) public void onClick() {
    TurnManager.getInstance().turnGameDetail(this, Integer.parseInt(mEntity.gameId));
  }

  private void setLister() {
    RvItemClickSupport.addTo(mGirdAward).setOnItemKeyListenr((v, keyCode, position, event) -> {
      if (event.getAction() == KeyEvent.ACTION_DOWN) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
          if (!mEntity.isFinished) {
            requestBtn();
          }
          return false;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
          if (position == 0) {
            return true;
          }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
          if (position == mEntity.awardList.size() - 1) {
            return true;
          }
        }
        return false;
      }
      return false;
    });
    mPlayGame.setOnKeyListener((v, keyCode, event) -> {
      if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
        requestAward(mEntity);
        return true;
      }
      return false;
    });
    RvItemClickSupport.addTo(mGirdAward).setOnItemClickListener((recyclerView, position, v) -> {
      DayTaskEntity.TaskSubEntity entity = mEntity.awardList.get(position);
      mAwardId = entity.awardId;
      mNum = entity.taskPosition;
      getModule(DayTaskModule.class).getDayTaskAwards(DayTaskActivity.this, entity.awardId,
          entity.taskPosition);
    });
  }

  public void requestAward(DayTaskEntity entity) {
    if (entity != null && entity.awardList != null && entity.awardList.size() > 0) {
      for (int i = 0; i < entity.awardList.size(); i++) {
        if (entity.awardList.get(i).status.equals(RECEIVE)) { //待领取
          mPlayGame.clearFocus();
          mGirdAward.requestFocus();
          mGirdAward.setSelectedPosition(i);
          return;
        }
      }
    }
  }

  public void requestBtn() {
    mGirdAward.clearFocus();
    mPlayGame.requestFocus();
    mPlayGame.setFocusable(true);
  }
  //
  ///**
  // * 领取奖励
  // */
  //@Override public void get(int position) {
  //  //DayTaskEntity.TaskSubEntity entity = mEntity.awardList.get(position);
  //  //mAwardId = entity.awardId;
  //  //mNum = entity.taskPosition;
  //  //getModule(DayTaskModule.class).getDayTaskAwards(this, entity.awardId, entity.taskPosition);
  //}

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == TurnManager.PAY_REQUEST_CODE && resultCode == TurnManager.PAY_RESULT_CODE) {
      DayTaskEntity d =
          SharePreUtil.getObject(FILE_NAME_TASK, this, SHARE_DAY_TASK_KEY, DayTaskEntity.class);
      if (d != null) {
        mEntity = d;
        mAwardAdapter.updata(mEntity.awardList);
        mProgressAdapter.update(mEntity);
        setUIData();
      }
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (TextUtils.isEmpty(mPackageName)) {
      SharePreUtil.removeKey(FILE_NAME_TASK, this, SHARE_DAY_TASK_KEY);
    }
  }
}
