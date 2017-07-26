package com.utstar.appstoreapplication.activity.windows.game_search;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.ut.wb.ui.adapter.AbsHolder;
import com.ut.wb.ui.adapter.AbsRVAdapter;
import com.utstar.appstoreapplication.activity.R;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/19.
 * 搜索键盘适配器
 */
final class SearchKeyAdapter
    extends AbsRVAdapter<SearchKeyEntity, SearchKeyAdapter.SearchKeyHolder> {

  SearchKeyAdapter(Context context, List<SearchKeyEntity> data) {
    super(context, data);
  }

  @Override protected SearchKeyHolder getViewHolder(View convertView, int viewType) {
    return new SearchKeyHolder(convertView);
  }

  @Override protected int setLayoutId(int type) {
    return R.layout.item_search_key;
  }

  @Override protected void bindData(SearchKeyHolder holder, int position, SearchKeyEntity item) {
    if (item.num != -1) {
      holder.icon.setVisibility(View.GONE);
      holder.key.setVisibility(View.VISIBLE);
      holder.num.setText(String.valueOf(item.num));
      if (item.num == 1 || item.num == 0) {
        //holder.key.setVisibility(View.INVISIBLE);
        holder.key.setVisibility(View.GONE);
      }
    }

    if (!TextUtils.isEmpty(item.icon)) {
      holder.icon.setVisibility(View.VISIBLE);
      holder.key.setVisibility(View.INVISIBLE);
      holder.num.setVisibility(View.INVISIBLE);
      holder.icon.setImageResource(Integer.parseInt(item.icon));
    }

    if (item.key != null && item.key.length > 0) {
      holder.icon.setVisibility(View.GONE);
      holder.key.setVisibility(View.VISIBLE);
      holder.num.setVisibility(View.VISIBLE);
      holder.key.setText(new String(item.key));
    }
  }

  class SearchKeyHolder extends AbsHolder {
    @Bind(R.id.num) TextView num;
    @Bind(R.id.key) TextView key;
    @Bind(R.id.icon) ImageView icon;

    SearchKeyHolder(View itemView) {
      super(itemView);
    }
  }
}
