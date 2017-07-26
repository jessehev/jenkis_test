package com.utstar.appstoreapplication.activity.windows.payment;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.arialyy.frame.util.CalendarUtils;
import com.arialyy.frame.util.StringUtil;
import com.ut.wb.ui.adapter.AbsHolder;
import com.ut.wb.ui.adapter.AbsRVAdapter;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.OrderInfoEntity;
import com.utstar.appstoreapplication.activity.manager.AnimManager;
import java.util.List;

/**
 * Created by JesseHev on 2017/5/10.
 */

public class OrderInfoAdapater
    extends AbsRVAdapter<OrderInfoEntity.Info, OrderInfoAdapater.OrderInfoHolder> {

  private onBtnChangeListener mOnBtnChangeListener;

  private int mMaxDay;

  private int mDay;

  private Context mContext;

  interface onBtnChangeListener {
    /**
     * 刷新消费提示回调
     */
    void changeUI(int position);

    /**
     * 按钮点击回调
     */
    void buy(int position);
  }

  public void setOnBtnChangeListener(onBtnChangeListener listener) {
    mOnBtnChangeListener = listener;
  }

  public OrderInfoAdapater(Context context, List<OrderInfoEntity.Info> data) {
    super(context, data);
    mContext = context;
    mMaxDay = CalendarUtils.getDayCount();
    mDay = CalendarUtils.getRemainDay();
  }

  @Override protected OrderInfoHolder getViewHolder(View convertView, int viewType) {
    return new OrderInfoHolder(convertView);
  }

  @Override protected int setLayoutId(int type) {
    return R.layout.item_order_info;
  }

  @Override
  protected void bindData(OrderInfoHolder holder, int position, OrderInfoEntity.Info info) {
    holder.name.setText(info.getZfName());
    holder.price.setText(info.getPrice());

    holder.btn.setOnFocusChangeListener((view, hasFocus) -> {
      if (hasFocus && mOnBtnChangeListener != null) {
        mOnBtnChangeListener.changeUI(position);
        AnimManager.getInstance().enlarge(view, 1.15f);
      } else {
        AnimManager.getInstance().narrow(view, 1.0f);
      }
    });
    holder.btn.setOnClickListener(view -> {
      if (mOnBtnChangeListener != null) {
        mOnBtnChangeListener.buy(position);
      }
    });

    switch (info.getIsShow()) {
      case "0":
        holder.yh.setVisibility(View.GONE);
        break;
      case "1":
        //连续包月才有今日优惠
        holder.yh.setVisibility(View.VISIBLE);
        break;
      default:
        holder.yh.setVisibility(View.GONE);
        break;
    }
    //  holder.yh.setText(getPrice(info.getPrice()));
  }

  /**
   * 获取当月剩余天数价格
   */
  public String getPrice(String str) {
    double price = Double.parseDouble(StringUtil.getNumberFromString(str));
    String show = mContext.getResources().getString(R.string.order_yh_price);
    return String.format(show, String.format("%.2f", price / mMaxDay * mDay));
  }

  class OrderInfoHolder extends AbsHolder {
    TextView name;
    TextView price;
    TextView yh;
    Button btn;

    public OrderInfoHolder(View itemView) {
      super(itemView);
      name = (TextView) itemView.findViewById(R.id.name);
      btn = (Button) itemView.findViewById(R.id.btn);
      price = (TextView) itemView.findViewById(R.id.abs);
      yh = (TextView) itemView.findViewById(R.id.yh);
    }
  }
}
