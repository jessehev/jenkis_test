package com.utstar.appstoreapplication.activity.windows.activity.day_task;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.DayTaskEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHev on 2017/4/11.
 * 当前任务进度适配器
 */

public class DayTaskProgressAdapter
    extends RecyclerView.Adapter<DayTaskProgressAdapter.DayTaskProgressHolder> {

  List<DayTaskEntity.TaskSubEntity> mList = new ArrayList<>();
  int mTotalTask; //任务总数
  int mCurrentTask;//当前任务进度

  static final int TASK_FINISHED = 0; //已完成
  static final int TASK_NORMAL = 1; //活动未开始
  static final int TASK_CURRENT = 2;//当前任务进度
  private boolean mIsFinished;

  public DayTaskProgressAdapter(Context context, int totalTask, int currentTask,
      boolean isFinished) {
    mTotalTask = totalTask;
    mCurrentTask = currentTask;
    mIsFinished = isFinished;
    defaultData();
  }

  /**
   * 设置默认数据
   */
  private void defaultData() {
    mList.clear();
    for (int i = 1; i <= mTotalTask; i++) {
      DayTaskEntity.TaskSubEntity task = new DayTaskEntity.TaskSubEntity();
      task.day = i + "";
      //if (i < mCurrentTask) task.isCompleted = true;
      if (i < mCurrentTask) task.progressStatus = TASK_FINISHED;
      if (i == mCurrentTask) task.progressStatus = TASK_CURRENT;
      if (i == mTotalTask & mIsFinished) task.progressStatus = TASK_FINISHED;//最后一个任务是否完成
      if (i >= mCurrentTask + 1) task.progressStatus = TASK_NORMAL;
      mList.add(task);
    }
  }

  public void update(DayTaskEntity task) {
    mTotalTask = task.totalTask;
    mCurrentTask = task.currentTask;
    mIsFinished = task.isFinished;
    defaultData();
    notifyDataSetChanged();
  }

  @Override public DayTaskProgressHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_progress, null);
    DayTaskProgressHolder holder = new DayTaskProgressHolder(view);
    return holder;
  }

  @Override public void onBindViewHolder(DayTaskProgressHolder holder, int position) {
    DayTaskEntity.TaskSubEntity entity = mList.get(position);
    holder.num.setText(entity.day);
    // holder.num.setBackgroundResource(entity.isCompleted ? R.mipmap.bg_task_1 : R.mipmap.bg_task_2);
    int resId = -1;
    switch (entity.progressStatus) {
      case TASK_FINISHED:
        resId = R.mipmap.bg_task_3;
        break;
      case TASK_CURRENT:
        resId = R.mipmap.bg_task_1;
        break;
      case TASK_NORMAL:
        resId = R.mipmap.bg_task_2;
        break;
    }
    if (resId != -1) {
      holder.num.setBackgroundResource(resId);
    }
  }

  @Override public int getItemCount() {
    return mList.size();
  }

  class DayTaskProgressHolder extends RecyclerView.ViewHolder {
    TextView num;

    public DayTaskProgressHolder(View itemView) {
      super(itemView);
      num = (TextView) itemView.findViewById(R.id.num);
    }
  }
}
