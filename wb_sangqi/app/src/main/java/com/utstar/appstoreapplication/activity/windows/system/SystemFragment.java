package com.utstar.appstoreapplication.activity.windows.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import butterknife.Bind;
import com.ut.wb.ui.adapter.RvItemClickSupport;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.FragmentSystemBinding;
import com.utstar.appstoreapplication.activity.entity.SystemEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SysMessageListEntity;
import com.utstar.appstoreapplication.activity.manager.AnimManager;
import com.utstar.appstoreapplication.activity.utils.ActionUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import com.utstar.appstoreapplication.activity.windows.main.MainActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统
 */
public class SystemFragment extends BaseFragment<FragmentSystemBinding> {

  @Bind(R.id.hgv_list) HorizontalGridView mList;
  public static SystemAdapter mAdapter;
  private List<SystemEntity> mSystemEntityList = new ArrayList<>();
  private static final int SYS_MSG_CALL_SUCCESS = 1101;
  private int mCurrentPosition = 0;

  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(ActionUtil.ACTION_SYS_STATUS)) {
        updateSysMsg();
      }
    }
  };

  public static SystemFragment newInstance() {
    SystemFragment systemFragment = new SystemFragment();
    Bundle arg = new Bundle();
    systemFragment.setArguments(arg);
    return systemFragment;
  }

  public void requestFocus() {
    View view = mList.getChildAt(0);
    if (view != null) {
      view.requestFocus();
    }
  }

  /**
   * 更新数据
   */
  public void updateSysMsg() {
    getModule(SystemModule.class).getSysMsgTotal();
  }

  @Override protected void init(Bundle savedInstanceState) {
    register();
    initGrid();
    updateSysMsg();
    setListener();
    setBgParam(6, 1);
  }

  public void initGrid() {
    mSystemEntityList.add(new SystemEntity("站内信", R.mipmap.bg_message, "0"));
    mSystemEntityList.add(new SystemEntity("手柄购买", R.mipmap.bg_shoubing, "0"));
    mAdapter = new SystemAdapter(mSystemEntityList);
    mList.setAdapter(mAdapter);
  }

  @Override public void onResume() {
    super.onResume();
    if (getUserVisibleHint()) {
      updateSysMsg();
    }
  }

  private void setListener() {
    RvItemClickSupport.addTo(mList).setOnItemClickListener((recyclerView, position, v) -> {
      switch (position) {
        case 0:
          getContext().startActivity(new Intent(getContext(), SysmsgActivity.class));
          break;
        case 1:
          Intent intent = new Intent(getContext(), HandBuyActivity.class);
          getContext().startActivity(intent);
          break;
        default:
          break;
      }
    });

    RvItemClickSupport.addTo(mList).setOnItemFocusChangeListener((v, position, hasFocus) -> {
      if (hasFocus) {
        AnimManager.getInstance().enlarge(v, 1.15f);
      } else {
        AnimManager.getInstance().enlarge(v, 1.f);
      }
      mCurrentPosition = position;
    });
  }

  /**
   * 注册广播信息
   */
  private void register() {
    IntentFilter intentFilter = new IntentFilter(ActionUtil.ACTION_SYS_STATUS);
    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
  }

  public int getCurrentPosition() {
    return mCurrentPosition;
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_system;
  }

  @Override protected void dataCallback(int result, Object obj) {
    super.dataCallback(result, obj);
    if (result == SYS_MSG_CALL_SUCCESS) {
      SysMessageListEntity sysMsgList = (SysMessageListEntity) obj;
      mAdapter.setNumber(0, sysMsgList.getUnreadcount() + "");
      MainActivity mainActivity = (MainActivity) getActivity();
      if (sysMsgList.getUnreadcount() > 0) {
        mainActivity.mTab.showRedSpot(4, true);
      } else {
        mainActivity.mTab.showRedSpot(4, false);
      }
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
  }
}
