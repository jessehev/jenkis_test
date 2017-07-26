package com.utstar.appstoreapplication.activity.commons.widget.adve_banner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.AdverEntity;
import java.util.List;

/**
 * Created by JesseHev on 2017/1/10.
 */

public class AdVerAdpater {

  private List<List<AdverEntity>> mDatas;

  private Context mContext;

  public AdVerAdpater(List<List<AdverEntity>> mDatas) {
    this.mDatas = mDatas;
  }

  public int getCount() {
    return mDatas == null ? 0 : mDatas.size();
  }

  public List<AdverEntity> getItem(int position) {
    if (mDatas != null && mDatas.size() > 0) {
      return mDatas.get(position);
    }
    return null;
  }

  public View getView(AdverView parent) {
    mContext = parent.getContext();
    return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_banner, null);
  }

  public void setItem(final View view, final List<AdverEntity> data) {
    TextView userName = (TextView) view.findViewById(R.id.username);
    TextView userName1 = (TextView) view.findViewById(R.id.username1);
    TextView award = (TextView) view.findViewById(R.id.award);
    TextView award1 = (TextView) view.findViewById(R.id.award1);
    //String str = mContext.getResources().getString(R.string.lottery_info);
    if (data != null) {
      AdverEntity adverEntity = data.get(0);
      userName.setText("用户ID:" + adverEntity.userid + "  获得 ");
      award.setText(adverEntity.award);
      //String result = String.format(str, adverEntity.name, adverEntity.content);
      //userName.setText(result);

      if (data.size() >= 2) {
        adverEntity = data.get(1);
        userName1.setText("用户ID:" + adverEntity.userid + "  获得 ");
        award1.setText(adverEntity.award);
        //
        //String result2 = String.format(str, adverEntity.name, adverEntity.content);
        //userName1.setText(result2);
      } else {
        userName1.setText("");
        award1.setText("");
      }
    }
  }
}
