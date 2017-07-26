package com.utstar.appstoreapplication.activity.windows.system;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.SystemEntity;
import java.util.List;

public class SystemAdapter extends Adapter<SystemAdapter.SystemHolder> {

  private List<SystemEntity> mSystemEntityList;

  public SystemAdapter(List<SystemEntity> mSystemEntityList) {
    this.mSystemEntityList = mSystemEntityList;
  }

  @Override public SystemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_system, null);
    SystemHolder holder = new SystemHolder(itemView);
    return holder;
  }

  @Override public void onBindViewHolder(final SystemHolder holder, final int position) {
    SystemEntity mSystemEntity = mSystemEntityList.get(position);

    holder.tvName.setText(mSystemEntity.getName());
    holder.ivItem_bg.setBackgroundResource(mSystemEntity.getIcon());
    if (position == 0) {
      holder.tvNum.setVisibility(View.VISIBLE);
      holder.tvNum.setText("(" + mSystemEntity.getNumber() + ")");
    } else {
      holder.tvNum.setVisibility(View.GONE);
    }
    //holder.itemView.setOnFocusChangeListener(new OnFocusChangeListener() {
    //
    //  @Override public void onFocusChange(View v, boolean hasFocus) {
    //    if (hasFocus) {
    //      v.animate().scaleX(1.15f).scaleY(1.15f).start();
    //    } else {
    //      v.animate().scaleX(1.f).scaleY(1.f).start();
    //    }
    //    //   holder.ivShadow.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
    //  }
    //});
  }

  public void setNumber(int position, String number) {
    if (mSystemEntityList != null && mSystemEntityList.size() > position) {
      mSystemEntityList.set(position, new SystemEntity("站内信", R.mipmap.bg_message, number));
      notifyDataSetChanged();
    }
  }

  public void upDateNumer(int position, String mode) {
    if (mSystemEntityList != null && mSystemEntityList.size() > position) {
      int num = Integer.parseInt(mSystemEntityList.get(position).getNumber());
      if (mode.equals("sub")) {  //减
        num -= 1;
        if (num <= 0) {
          num = 0;
        }
      } else if (mode.equals("add")) {//加
        num += 1;
      }
      mSystemEntityList.set(position, new SystemEntity("站内信", R.mipmap.bg_message, num + ""));
      notifyDataSetChanged();
    }
  }

  @Override public int getItemCount() {
    if (mSystemEntityList == null) {
      return 0;
    }
    return mSystemEntityList.size();
  }

  public class SystemHolder extends ViewHolder {
    ImageView ivItem_bg;
    TextView tvNum;
    ImageView ivShadow;
    TextView tvName;

    public SystemHolder(View itemView) {
      super(itemView);
      tvNum = (TextView) itemView.findViewById(R.id.num);
      ivItem_bg = (ImageView) itemView.findViewById(R.id.item_bg);
      ivShadow = (ImageView) itemView.findViewById(R.id.shadow);
      tvName = (TextView) itemView.findViewById(R.id.name);
    }
  }
}
