package com.utstar.appstoreapplication.activity.windows.activity.discount_package;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.HalfPackageEntity;
import com.utstar.appstoreapplication.activity.manager.AnimManager;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHev on 2017/3/31.
 */

public class GamePackageDialogAdapater
    extends RecyclerView.Adapter<GamePackageDialogAdapater.Holder> {

  private List<HalfPackageEntity.GamePackage> mList = new ArrayList<>();
  private int mType = 0;

  @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(setLayout(mType), null);
    Holder holder = new Holder(v);
    return holder;
  }

  public int setLayout(int type) {
    return type == 0 ? R.layout.item_half_package : R.layout.item_ice_break;
  }

  public GamePackageDialogAdapater(List<HalfPackageEntity.GamePackage> list) {
    mList.clear();
    mList.addAll(list);
  }

  public GamePackageDialogAdapater(List<HalfPackageEntity.GamePackage> list, int type) {
    mList.clear();
    mList.addAll(list);
    mType = type;
  }

  @Override public void onBindViewHolder(Holder holder, int position) {

    HalfPackageEntity.GamePackage data = mList.get(position);
    ImageManager.getInstance()
        .setImg(holder.mBg, data.packageImg, R.mipmap.icon_none_2, R.mipmap.icon_none_2, 18);
    holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        AnimManager.getInstance().enlarge(v, 1.15f);
      } else {
        AnimManager.getInstance().narrow(v, 1.0f);
      }
    });
  }

  @Override public int getItemCount() {
    if (mList == null || mList.size() == 0) {
      return 0;
    }
    return mList.size() > 4 ? 4 : mList.size();
  }

  public static class Holder extends RecyclerView.ViewHolder {
    ImageView mBg;

    public Holder(View itemView) {
      super(itemView);
      mBg = (ImageView) itemView.findViewById(R.id.bg);
    }
  }
}
