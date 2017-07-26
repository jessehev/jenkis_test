package com.utstar.appstoreapplication.activity.windows.activity.day_task;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.arialyy.frame.util.show.T;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.DayTaskEntity;
import com.utstar.appstoreapplication.activity.manager.AnimManager;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.List;

/**
 * Created by JesseHev on 2017/4/11.
 * 奖品列表
 */

public class DayTaskAwardAdapter
    extends RecyclerView.Adapter<DayTaskAwardAdapter.DayTaskAwardHolder> {

  private List<DayTaskEntity.TaskSubEntity> mData;
  private Context mContext;

  public DayTaskAwardAdapter(Context context, List<DayTaskEntity.TaskSubEntity> list) {
    mContext = context;
    mData = list;
  }

  @Override public DayTaskAwardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_award_chane, null);
    return new DayTaskAwardHolder(view);
  }

  @Override public void onBindViewHolder(DayTaskAwardHolder holder, int position) {

    DayTaskEntity.TaskSubEntity entity = mData.get(position);
    ImageManager.getInstance().loadImg(holder.icon, entity.awardUrl);
    ImageManager.getInstance().loadImg(holder.icon_foc, entity.awardUrl);
    holder.num.setText(entity.awardDesc);
    holder.name.setText(entity.awardName);
    holder.num_foc.setText(entity.awardDesc);
    holder.name_foc.setText(entity.awardName);

    // status -2 - 活动还未开始， -1 - 待领奖, 1 - 领取成功,    0 - 奖品数量不足
    setStatusTxt(entity.status, entity, holder);

    if (entity.status.equals("-1")) {
      holder.itemView.setEnabled(true);
      holder.itemView.setFocusable(true);
    } else {
      holder.itemView.setEnabled(false);
      holder.itemView.setFocusable(false);
    }

    holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        AnimManager.getInstance().enlarge(v, 1.35f);
      } else {
        AnimManager.getInstance().enlarge(v, 1.0f);
      }
      showView(holder, hasFocus);
    });
  }

  public void showView(DayTaskAwardHolder holder, boolean hasFocus) {
    holder.fl_foc.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
    holder.fl.setVisibility(hasFocus ? View.INVISIBLE : View.VISIBLE);
  }

  public void setStatusTxt(String status, DayTaskEntity.TaskSubEntity entity,
      DayTaskAwardHolder holder) {
    String text = "";
    String textFoc = "";
    boolean isShow = false;
    switch (status) {
      case "-2":
        text = "完成任务" + entity.taskPosition + "领取";
        textFoc = "完成任务" + entity.taskPosition + "领取";
        break;
      case "-1":
        text = "";
        textFoc = "";
        isShow = true;
        break;
      case "0":
        text = "已领完";
        textFoc = "已领完";
        break;
      case "1":
        text = "已领取";
        textFoc = "已领取";
        break;
      default:
        break;
    }
    holder.status_txt.setText(text);
    holder.status_txt_foc.setText(textFoc);
    holder.status_txt2.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    holder.status_txt2_foc.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
  }

  public void updata(List<DayTaskEntity.TaskSubEntity> list) {
    mData = list;
    notifyDataSetChanged();
  }

  @Override public int getItemCount() {
    if (mData == null) {
      return 0;
    }
    return mData.size() > 3 ? 3 : mData.size();
  }

  class DayTaskAwardHolder extends RecyclerView.ViewHolder {

    public ImageView icon, icon_foc;
    public ImageView bg, bg_foc;
    public TextView num, name;
    public TextView num_foc, name_foc;
    public FrameLayout fl, fl_foc;
    public TextView status_txt, status_txt_foc;
    public TextView status_txt2, status_txt2_foc;

    public DayTaskAwardHolder(View v) {
      super(v);
      icon = getView(v, R.id.icon_award);
      num = getView(v, R.id.award_num);
      name = getView(v, R.id.award_name);
      bg = getView(v, R.id.bg);
      fl = getView(v, R.id.fl);
      status_txt = getView(v, R.id.status_txt);
      status_txt2 = getView(v, R.id.status_txt2);

      icon_foc = getView(v, R.id.icon_award_foc);
      num_foc = getView(v, R.id.award_num_foc);
      name_foc = getView(v, R.id.award_name_foc);
      bg_foc = getView(v, R.id.bg_foc);
      fl_foc = getView(v, R.id.fl_foc);
      status_txt_foc = getView(v, R.id.status_txt_foc);
      status_txt2_foc = getView(v, R.id.status_txt2_foc);
    }
  }

  public <T extends View> T getView(View v, @IdRes int id) {
    return (T) v.findViewById(id);
  }
}
