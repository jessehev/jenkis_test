package com.utstar.appstoreapplication.activity.windows.game_search;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import com.arialyy.frame.util.StringUtil;
import com.ut.wb.ui.MarqueTextView;
import com.ut.wb.ui.adapter.AbsHolder;
import com.ut.wb.ui.adapter.AbsRVAdapter;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.SearchEntity;
import com.utstar.appstoreapplication.activity.manager.AnimManager;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import java.util.List;

/**
 * Created by Aria.Lao on 2016/12/20.
 * 搜索内容、猜你喜欢
 */
final class SearchContentAdapter extends
    AbsRVAdapter<SearchEntity.SearchResultEntity, SearchContentAdapter.SearchContentHolder> {

  private int mRadius;
  private String mStr;

  SearchContentAdapter(Context context, List<SearchEntity.SearchResultEntity> data) {
    super(context, data);
    mRadius = (int) context.getResources().getDimension(R.dimen.dimen_15dp);
  }

  @Override protected SearchContentHolder getViewHolder(View convertView, int viewType) {
    return new SearchContentHolder(convertView);
  }

  @Override protected int setLayoutId(int type) {
    return R.layout.item_search_content;
  }

  @Override protected void bindData(SearchContentHolder holder, int position,
      SearchEntity.SearchResultEntity item) {
    ImageManager.getInstance().loadRoundedImg(holder.img, item.img, mRadius);
    cleanSpan(holder.name);
    if (!TextUtils.isEmpty(mStr)) {
      convert(item.productName, holder.name);
    } else {
      holder.name.setText(item.productName);
    }
    holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        AnimManager.getInstance().enlarge(v, 1.2f);
        holder.name.startMarquee();
      } else {
        AnimManager.getInstance().narrow(v, 1.2f);
        holder.name.stopMarquee();
      }
    });
  }

  /**
   * @param str 首字母串
   */
  public void update(String str) {
    mStr = str;
    notifyDataSetChanged();
  }

  private void cleanSpan(MarqueTextView textView) {
    CharSequence text = textView.getText();
    if (text instanceof SpannableString) {
      SpannableString ss = (SpannableString) text;
      ForegroundColorSpan[] spans = ss.getSpans(0, text.length(), ForegroundColorSpan.class);
      for (ForegroundColorSpan span : spans) {
        ss.removeSpan(span);
      }
    }
  }

  private void convert(String name, MarqueTextView textView) {
    cleanSpan(textView);
    textView.setText("");
    name = name.replaceAll(" ", "");
    String allFirstLetter = StringUtil.getAllFirstLetter(name).toUpperCase();
    for (int i = 0; i < allFirstLetter.length(); i++) {
      SpannableString spannableString = new SpannableString(name.substring(i, i + 1));
      if (mStr.contains(allFirstLetter.substring(i, i + 1))) {
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.RED);
        spannableString.setSpan(foregroundColorSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      } else {
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.WHITE);
        spannableString.setSpan(foregroundColorSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      }
      textView.append(spannableString);
    }
  }

  class SearchContentHolder extends AbsHolder {
    @Bind(R.id.img) ImageView img;
    @Bind(R.id.name) MarqueTextView name;

    SearchContentHolder(View itemView) {
      super(itemView);
    }
  }
}
