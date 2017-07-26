package com.utstar.appstoreapplication.activity.windows.game_hall.game_classify;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.widget.HorizontalView;
import com.utstar.appstoreapplication.activity.commons.widget.WBNavView;
import com.utstar.appstoreapplication.activity.entity.net_entity.GameClassifyEntity;
import java.util.ArrayList;

/**
 * Created by JesseHev on 2016/12/14.
 */

public class GameClassifyAdapter
    extends RecyclerView.Adapter<GameClassifyAdapter.GameClassifyHolder> {

  private ArrayList<ArrayList<GameClassifyEntity.GameClassifySubEntity>> mGameClassifySubEntity =
      new ArrayList<>();

  private int mCount = 1;
  //WBNavView 当前所在的位置
  private int mWbNavPosition;

  private WBNavView mWBNavView;

  private GameClassifyActivity mActivity;
  //是否是上一页
  private boolean mIsUp;

  @Override public GameClassifyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classify, null);
    GameClassifyHolder holder = new GameClassifyHolder(v);
    return holder;
  }

  public GameClassifyAdapter(Context context, WBNavView mWBNavView, int wbNavPosition, int count) {
    this.mCount = count;
    this.mWbNavPosition = wbNavPosition;
    this.mWBNavView = mWBNavView;

    mActivity = (GameClassifyActivity) context;
    mGameClassifySubEntity = new ArrayList<>();
  }

  public GameClassifyAdapter(Context context, WBNavView mWBNavView, int wbNavPosition, int count,
      ArrayList<GameClassifyEntity.GameClassifySubEntity> gameClassifySubEntity) {
    this.mCount = count;
    this.mWbNavPosition = wbNavPosition;
    this.mWBNavView = mWBNavView;

    mActivity = (GameClassifyActivity) context;
    mGameClassifySubEntity.add(gameClassifySubEntity);
  }

  @Override public void onBindViewHolder(GameClassifyHolder holder, int position) {

    holder.itemList.setViewValue(mGameClassifySubEntity.get(position));

    holder.itemList.setActivity(mActivity);
    //当前位置在自定义view的第一行、第二行的第一个选中左边导航
    holder.itemList.setWBNavView(mWBNavView, mWbNavPosition);
    holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {

      View view = holder.itemList.getChildView(0);
      if (mIsUp) {
        view = holder.itemList.getChildView(3);
      }
      if (hasFocus) {
        //mHoriView = holder.itemList;
        view.requestFocus();
      } else {
      }
    });
  }

  public void upDate(int count,
      ArrayList<GameClassifyEntity.GameClassifySubEntity> gameClassifySubEntity) {
    this.mCount = count;
    this.mGameClassifySubEntity.add(gameClassifySubEntity);
    notifyItemInserted(mGameClassifySubEntity.size() - 1);
  }

  public void setUp(boolean isUp) {
    this.mIsUp = isUp;
  }

  @Override public int getItemCount() {
    if (mGameClassifySubEntity == null) return 0;
    return mGameClassifySubEntity.size();
  }

  public class GameClassifyHolder extends RecyclerView.ViewHolder {

    public HorizontalView itemList;

    public GameClassifyHolder(View itemView) {
      super(itemView);
      itemList = (HorizontalView) itemView.findViewById(R.id.item_list);
    }
  }
}
