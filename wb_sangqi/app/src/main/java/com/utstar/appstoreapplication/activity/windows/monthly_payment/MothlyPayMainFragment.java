package com.utstar.appstoreapplication.activity.windows.monthly_payment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.ut.wb.ui.MarqueTextView;
import com.ut.wb.ui.adapter.RvItemClickSupport;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.FragmentMothlyPayMainBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.ModuleBgEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.MothlyPaySecondEntity;
import com.utstar.appstoreapplication.activity.manager.ActionManager;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.manager.TurnManager;
import com.utstar.appstoreapplication.activity.module.CommonModule;
import com.utstar.appstoreapplication.activity.utils.EpgUserUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lt on 2016/12/20.
 * 包月专区碎片
 */

public class MothlyPayMainFragment extends BaseFragment<FragmentMothlyPayMainBinding> {
  @Bind(R.id.vgv_mothly_mian) VerticalGridView mVgvMothlyMain;
  @Bind(R.id.mothly_iv_left) ImageView mIvLeft;
  @Bind(R.id.mothly_iv_right) ImageView mIvRight;
  public MothlyPayAdapter mAdapter;
  List<MothlyPaySecondEntity.MothlyGameEntity> mMothlyGames = new ArrayList<>();
  private static final int MOTHLYPAY_CALL_SUCCESS = 1;
  private String mProductId;
  private String mId;   //游戏ID
  private int mPage;
  private int mNum;
  private MothlyPaySecondEntity mothlyPaySecondEntity;
  private boolean isBuy = false;
  public static int mTag;

  public static MothlyPayMainFragment newInstance(String productId, int page, int num,
      String mProductId) {
    MothlyPayMainFragment mothlyPayMainFragment = new MothlyPayMainFragment();
    Bundle bundle = new Bundle();
    bundle.putString("productId", productId);
    bundle.putInt("page", page);
    bundle.putInt("num", num);
    bundle.putString("mProductId", mProductId);
    mothlyPayMainFragment.setArguments(bundle);
    return mothlyPayMainFragment;
  }

  @Override protected void init(Bundle savedInstanceState) {
    Bundle bundle = getArguments();
    mProductId = bundle.getString("productId");
    mPage = bundle.getInt("page");
    mNum = bundle.getInt("num");
    mId = bundle.getString("mProductId");
    initView();
    onClickAndFocus();
    getModule(MothlyPayModule.class).getData(mProductId, mPage);
  }

  @Override public void onResume() {
    super.onResume();
    if (isVisible()) {
      // 1.配置背景图    精品专区背景  type-1
      getModule(CommonModule.class).getEachPackBackground(mProductId);
    }
  }

  private void onClickAndFocus() {
    RvItemClickSupport.addTo(mVgvMothlyMain)
        .setOnItemClickListener(new RvItemClickSupport.OnItemClickListener() {
          @Override public void onItemClicked(RecyclerView recyclerView, int position, View v) {
            TurnManager.getInstance()
                .turnGameDetail(getActivity(), mMothlyGames.get(position).getID(), 2);
            //2.从套餐包进游戏详情，方便后台做统计功能
            ActionManager.getInstance()
                .statisticsIntoGameDetail(2, mMothlyGames.get(position).getID(),
                    EpgUserUtil.getUserEntity().getChannel());
          }
        });
    RvItemClickSupport.addTo(mVgvMothlyMain)
        .setOnItemFocusChangeListener(new RvItemClickSupport.OnItemFocusChangeListener() {
          @Override public void onFocusChange(View v, int position, boolean hasFocus) {
            MarqueTextView tvTitle = (MarqueTextView) v.findViewById(R.id.item_mothly_tv);
            if (hasFocus) {
              TextView tvMothlyCoount = (TextView) getActivity().findViewById(R.id.tv_mothly_count);
              //焦点变化的页码标签变换
              tvMothlyCoount.setText(" ("
                  + (position + 1 + (mPage - 1) * 10)
                  + "/"
                  + mothlyPaySecondEntity.getSize()
                  + ")");
              v.animate().scaleX(1.10f).scaleY(1.10f).start();
              v.findViewById(R.id.item_mothly_shadow).setVisibility(View.VISIBLE);
              v.findViewById(R.id.item_mothly_tv).setBackgroundResource(R.mipmap.bg_mothly_tv);
              tvTitle.startMarquee();
            } else {
              v.animate().scaleX(1.0f).scaleY(1.0f).start();
              v.findViewById(R.id.item_mothly_shadow).setVisibility(View.INVISIBLE);
              v.findViewById(R.id.item_mothly_tv).setBackgroundResource(R.color.transparent);
              tvTitle.stopMarquee();
            }
          }
        });
  }

  private void initView() {
    mAdapter = new MothlyPayAdapter(mMothlyGames);
    mVgvMothlyMain.setAdapter(mAdapter);
    if (mPage == 1 && mNum != 1) {
      mIvLeft.setVisibility(View.INVISIBLE);
    } else if (mPage == mNum && mNum != 1) {
      mIvRight.setVisibility(View.INVISIBLE);
    } else if (mNum == 1) {
      mIvLeft.setVisibility(View.INVISIBLE);
      mIvRight.setVisibility(View.INVISIBLE);
    }
  }

  @Override protected int setLayoutId() {
    return R.layout.fragment_mothly_pay_main;
  }

  @Override public void updateData() {
    super.updateData();
    getModule(MothlyPayModule.class).getData(mProductId, mPage);
  }

  public void updateBuyIcon(boolean isBuy) {
    mAdapter.update(isBuy);
  }

  @Override protected void dataCallback(int result, Object obj) {
    super.dataCallback(result, obj);
    if (result == MOTHLYPAY_CALL_SUCCESS) {
      mothlyPaySecondEntity = (MothlyPaySecondEntity) obj;
      //mMothlyGames = mothlyPaySecondEntity.getList();
      isBuy = mothlyPaySecondEntity.isIsBuy();
      mTag = mothlyPaySecondEntity.getTag();
      if (mMothlyGames != null) {
        mMothlyGames.clear();
        mMothlyGames.addAll(mothlyPaySecondEntity.getList());
        mAdapter.update(isBuy, mTag);
        if (mMothlyGames.size() == 1) {
          new Handler().postDelayed(new Runnable() {
            @Override public void run() {
              mVgvMothlyMain.scrollToPosition(0);
              View view = mVgvMothlyMain.getLayoutManager().getChildAt(0);
              if (view != null) {
                view.requestFocus();
              }
            }
          }, 60);
        } else {
          for (int i = 0; i < mMothlyGames.size(); i++) {
            if (String.valueOf(mMothlyGames.get(i).getID()).equals(mId)) {
              MothlyPayActivity mothlyPayActivity = (MothlyPayActivity) getActivity();
              mothlyPayActivity.mMothlyVp.setCurrentItem(mPage - 1);
              mVgvMothlyMain.setSelectedPosition(i);
            }
          }
        }
      }
    } else if (result == CommonModule.MODULEBG_SUCCESS) {
      ImageManager.getInstance().loadBackground(getActivity(), (ModuleBgEntity) obj);
    }
  }
}