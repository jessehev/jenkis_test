package com.utstar.appstoreapplication.activity.windows.activity.sign;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.CalendarEntity;
import java.util.List;

/**
 * Created by JesseHev on 2017/1/9.
 */

public class CalendarAdapter extends BaseAdapter {

  private List<CalendarEntity> mList;
  private int selectColor;
  private int unselectColor;

  public CalendarAdapter(List<CalendarEntity> mList) {
    this.mList = mList;
    selectColor = Color.parseColor("#DC4E4C");
    unselectColor = Color.parseColor("#8674A5");
  }

  @Override public int getCount() {
    return 35;
  }

  @Override public Object getItem(int position) {
    return position >= mList.size() ? null : mList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    TextView textView = null;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar, null);
      textView = (TextView) convertView.findViewById(R.id.date);
      convertView.setTag(textView);
    } else {
      textView = (TextView) convertView.getTag();
    }
    CalendarEntity item = (CalendarEntity) getItem(position);
    if (item != null && item.isSign) {
      convertView.setBackgroundResource(R.drawable.bg_calendar_item_s);
      textView.setText(item.date);
      textView.setTextColor(selectColor);
    } else if (item != null && !item.isSign) {
      convertView.setBackgroundResource(R.drawable.bg_calendar_item_n);
      textView.setText(item.date);
      textView.setTextColor(unselectColor);
    } else {
      convertView.setBackgroundResource(R.drawable.bg_calendar_item_n);
    }
    return convertView;
  }
}
