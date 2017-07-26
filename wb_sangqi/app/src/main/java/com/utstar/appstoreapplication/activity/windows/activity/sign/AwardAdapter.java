package com.utstar.appstoreapplication.activity.windows.activity.sign;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.AwardEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHev on 2017/1/9.
 */

public class AwardAdapter extends BaseAdapter {

  List<AwardEntity> mData;
  List<AwardEntity> mProgressList = new ArrayList<>(); //符合领取奖励条件的数据

  int[] nums_bg = {
      R.mipmap.icon_lable_01, R.mipmap.icon_lable_02, R.mipmap.icon_lable_03, R.mipmap.icon_lable_04
  };
  //int[] icons = { R.drawable.img01, R.drawable.img02, R.drawable.img03, R.drawable.img04 };

  public AwardAdapter(List<AwardEntity> data) {
    this.mData = data;
    for (int i = 0; i < data.size(); i++) {
      if (data.get(i).flag.equals("1")) {
        mProgressList.add(data.get(i));
      }
    }
  }

  @Override public int getCount() {
    return mData.size();
  }

  @Override public Object getItem(int position) {
    return mData.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder = null;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_award, null);
      holder.award_content = (TextView) convertView.findViewById(R.id.award_content);
      holder.award_icon = (ImageView) convertView.findViewById(R.id.award_icon);
      holder.award_nums = (TextView) convertView.findViewById(R.id.award_nums);
      holder.item_icon = (ImageView) convertView.findViewById(R.id.item_icon);
      holder.line = (ImageView) convertView.findViewById(R.id.line);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    AwardEntity entity = mData.get(position);
    String days = holder.award_nums.getText().toString();
    days = String.format(days, entity.targetDay + "");
    holder.award_nums.setText(days);
    holder.award_nums.setBackgroundResource(nums_bg[position % nums_bg.length]);
    holder.award_content.setText(entity.award);

    if ("1".equals(entity.flag)) { //flag		0: 未达到领取条件  1 ：已达到领取条件
      if (mProgressList != null && mProgressList.size() > 0 && (0 <= position
          && position < mProgressList.size())) {
        judge(position, mProgressList);
        if (mProgressList.get(position).select) {
          holder.line.setBackgroundResource(R.drawable.icon_sign_point_line1);
        } else {
          holder.line.setBackgroundResource(R.drawable.icon_sign_point_line2);
        }
      }
      if ("0".equals(entity.okStatus)) { //okstatus	-1 奖品不可领取 0-奖品数已领完 1-奖品可领
        holder.award_icon.setImageResource(R.mipmap.icon_img_bg_s2);
      } else if ("1".equals(entity.okStatus)) {
        holder.award_icon.setImageResource(R.mipmap.icon_img_bg_s1);
      }
    } else {//未达到领取条件
      ImageManager.getInstance().loadImg(holder.award_icon, entity.awardUrl);
      holder.line.setBackgroundResource(R.drawable.icon_sign_point_line);
    }

    return convertView;
  }

  private void judge(int position, List<AwardEntity> list) {
    if (0 <= position && position < list.size() - 1) {
      list.get(position).setSelect(true);
    } else if (position == list.size() - 1) {
      list.get(position).setSelect(false);
    } else {
      list.get(position).setSelect(true);
    }
  }

  private class ViewHolder {
    TextView award_nums;
    ImageView award_icon;
    TextView award_content;
    ImageView item_icon;
    ImageView line;
  }
}
