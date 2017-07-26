package com.utstar.appstoreapplication.activity.windows.system;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.ActivitySysmsgBinding;
import com.utstar.appstoreapplication.activity.entity.net_entity.SysMessageEntity;
import com.utstar.appstoreapplication.activity.entity.net_entity.SysMessageListEntity;
import com.utstar.appstoreapplication.activity.manager.ImageManager;
import com.utstar.appstoreapplication.activity.utils.TimeUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
import java.util.ArrayList;
import java.util.List;

import static com.utstar.appstoreapplication.activity.windows.system.SystemModule.SYS_DETAIL_CALL_SUCCESS;
import static com.utstar.appstoreapplication.activity.windows.system.SystemModule.SYS_MSGS_CALL_SUCCESS;

/**
 * Created by lt on 2017/1/23.
 * 站内信窗口
 */
public class SysmsgActivity extends BaseActivity<ActivitySysmsgBinding>
    implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
  @Bind(R.id.syslist) ListView mListView;
  @Bind(R.id.listcontent) View listContentView;
  @Bind(R.id.detailcontent) View detailContentView;
  @Bind(R.id.bg) ImageView bgImageView;
  private List<SysMessageEntity> mList;
  private ListAdapter adapter;
  private int page;
  @Bind(R.id.content) TextView contentTextView;
  @Bind(R.id.msgtime) TextView msgtimeTextView;
  @Bind(R.id.imageurl) ImageView imageurlImageView;
  private String msgtime;
  private int msgsize;

  @Override protected int setLayoutId() {
    return R.layout.activity_sysmsg;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    mList = new ArrayList<>();
    mListView.setSelector(R.drawable.item_sysmsg_selector);
    mListView.setDivider(this.getResources().getDrawable(R.drawable.divider));
    mListView.setDividerHeight(1);
    mListView.setOnItemClickListener(this);
    mListView.setOnItemSelectedListener(this);
    adapter = new ListAdapter();
    mListView.setAdapter(adapter);
    page++;
    getModule(SystemModule.class).getSysList(page);
  }

  @Override protected void dataCallback(int result, Object obj) {
    if (result == SYS_MSGS_CALL_SUCCESS) {
      SysMessageListEntity response = (SysMessageListEntity) obj;
      if (response != null && response.getList().size() > 0) {
        msgsize = response.getSize();
        List<SysMessageEntity> list = response.getList();
        mList.addAll(mList.size(), list);
        adapter.notifyDataSetChanged();
      } else {
        Toast.makeText(this, R.string.noMsg, Toast.LENGTH_LONG).show();
      }
    } else if (result == SYS_DETAIL_CALL_SUCCESS) {
      SysMessageEntity response = (SysMessageEntity) obj;
      showDialog(response);
    }
  }

  protected void showDialog(SysMessageEntity result) {
    listContentView.setVisibility(View.GONE);
    if (!TextUtils.isEmpty(result.getContent())) {
      contentTextView.setText(Html.fromHtml(result.getContent()));
    }
    msgtimeTextView.setText(msgtime);
    String imageurl = result.getImageurl();
    if (imageurl != null && !imageurl.equals("")) {
      ImageManager.getInstance().setImg(imageurlImageView, imageurl, 0, 0, 0);
    }
    detailContentView.setVisibility(View.VISIBLE);
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (listContentView.getVisibility() == View.VISIBLE) {
        finish();
      } else {
        detailContentView.setVisibility(View.GONE);
        listContentView.setVisibility(View.VISIBLE);
      }
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public void onItemClick(AdapterView<?> arg0, View convertView, int position, long arg3) {
    SysMessageEntity model = mList.get(position);
    if (model.getIsreaded() != 1) {
      SystemFragment.mAdapter.upDateNumer(0, "sub");
    }
    model.setIsreaded(1);
    adapter.notifyDataSetChanged();
    msgtime = model.getDate();
    getModule(SystemModule.class).showDetail(model);
  }

  @Override public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    if (arg2 == mList.size() - 1 && msgsize > mList.size()) {
      page++;
      getModule(SystemModule.class).getSysList(page);
    }
  }

  @Override public void onNothingSelected(AdapterView<?> parent) {

  }

  /**
   * listView 信息列表的适配器
   */
  private class ListAdapter extends BaseAdapter {
    @Override public int getCount() {
      return mList.size();
    }

    @Override public Object getItem(int position) {
      return mList.get(position);
    }

    @Override public long getItemId(int position) {
      return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
      ListAdapter.ViewHolder holder = null;
      if (convertView == null) {
        convertView =
            LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_syslist, null);
        holder = new ListAdapter.ViewHolder();
        holder.icon = (ImageView) convertView.findViewById(R.id.icon);
        holder.time = (TextView) convertView.findViewById(R.id.time);
        holder.title = (TextView) convertView.findViewById(R.id.title);
        convertView.setTag(holder);
      } else {
        holder = (ListAdapter.ViewHolder) convertView.getTag();
      }
      SysMessageEntity model = mList.get(position);
      if (model.getIsreaded() == 1) {
        holder.time.setTextColor(Color.parseColor("#999999"));
        holder.title.setTextColor(Color.parseColor("#999999"));
      } else {
        holder.time.setTextColor(Color.parseColor("#cccccc"));
        holder.title.setTextColor(Color.parseColor("#ffcc00"));
      }
      if (!TextUtils.isEmpty(model.getDate())) {
        holder.time.setText(TimeUtil.getTimeStamp(model.getDate()));
      }
      holder.title.setText(model.getTitle());
      return convertView;
    }

    class ViewHolder {
      ImageView icon;
      TextView title;
      TextView time;
    }
  }
}
