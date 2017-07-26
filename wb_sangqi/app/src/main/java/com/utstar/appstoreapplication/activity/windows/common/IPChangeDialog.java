package com.utstar.appstoreapplication.activity.windows.common;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.SharePreUtil;
import com.arialyy.frame.util.StringUtil;
import com.arialyy.frame.util.show.T;
import com.ut.wb.ui.adapter.AbsHolder;
import com.ut.wb.ui.adapter.AbsRVAdapter;
import com.ut.wb.ui.adapter.RvItemClickSupport;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.commons.constants.KeyConstant;
import com.utstar.appstoreapplication.activity.commons.constants.NetConstant;
import com.utstar.appstoreapplication.activity.databinding.DialogIpChangeBinding;
import com.utstar.appstoreapplication.activity.utils.EpgUserUtil;
import com.utstar.appstoreapplication.activity.windows.base.BaseDialog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Aria.Lao on 2016/11/29.
 */
public class IPChangeDialog extends BaseDialog<DialogIpChangeBinding> {
  @Bind(R.id.list) RecyclerView mList;
  private List<ChangeEntity> mData;

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    mData = getData();
    final IpChangeAdapter adapter = new IpChangeAdapter(getContext(), mData);
    mList.setLayoutManager(new LinearLayoutManager(getContext()));
    mList.setAdapter(adapter);
    String currentIp =
        SharePreUtil.getString(KeyConstant.PRE_NAME, getContext(), KeyConstant.PRE.BASE_URL);
    getBinding().setCurrentIp(
        "当前ip：" + (TextUtils.isEmpty(currentIp) ? NetConstant.BASE_URL : currentIp));
    String versionInfo = "渠道："
        + StringUtil.getApplicationMetaData(getContext(), "channel")
        + "，版本："
        + AndroidUtils.getVersionCode(getContext())
        + "，热更新版本："
        + SharePreUtil.getString(KeyConstant.PRE_NAME, getContext(), KeyConstant.PRE.TINKER_ID);

    getBinding().setVersionInfo(versionInfo);
    RvItemClickSupport.addTo(mList).setOnItemClickListener((recyclerView, position, v) -> {
      if (position == mData.size() - 1) {
        changeServer();
      } else {
        saveIp(mData.get(position).ip);
      }
    });
    new Handler().postDelayed(() -> {
      int i = 0;
      if (!TextUtils.isEmpty(currentIp)) {
        for (ChangeEntity entity : mData) {
          if (entity.ip.equalsIgnoreCase(currentIp)) {
            break;
          }
          i++;
        }
      }
      View v = mList.getChildAt(i);
      if (v == null) {
        v = mList.getChildAt(0);
      }
      v.requestFocus();
    }, 200);
  }

  private List<ChangeEntity> getData() {
    List<ChangeEntity> list = new ArrayList<>();
    try {
      Properties properties =
          StringUtil.loadConfig(getContext().getAssets().open("ips.properties"));
      Set<String> keys = properties.stringPropertyNames();
      for (String key : keys) {
        ChangeEntity entity = new ChangeEntity(key, properties.getProperty(key));
        list.add(entity);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    ChangeEntity entity = new ChangeEntity("", "修改当前ip");
    list.add(entity);
    return list;
  }

  private void changeServer() {
    String url =
        SharePreUtil.getString(KeyConstant.PRE_NAME, getContext(), KeyConstant.PRE.BASE_URL);
    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
    mBuilder.setTitle("修改服务器地址");
    final EditText mEditText = new EditText(getContext());
    if (TextUtils.isEmpty(url)) {
      mEditText.setText("http://115.28.65.156/wbManager/");
    } else {
      mEditText.setText(url);
    }
    mEditText.setOnKeyListener((v, keyCode, event) -> {
      InputMethodManager inputMethodManager =
          (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
      if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
        inputMethodManager.showSoftInput(mEditText, 0);
        return true;
      }
      return false;
    });
    mBuilder.setView(mEditText);
    mBuilder.setNegativeButton("取消", null);
    mBuilder.setPositiveButton("确定",
        (dialog, which) -> saveIp(mEditText.getText().toString().trim()));
    mBuilder.create().show();
  }

  private void saveIp(String ip) {
    SharePreUtil.putString(KeyConstant.PRE_NAME, getContext(), KeyConstant.PRE.BASE_URL, ip);
    EpgUserUtil.removeUserEntity();
    T.showLong(getContext(), "已设置IP：" + ip + "为默认ip");
    dismiss();
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_ip_change;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  private class ChangeEntity {
    private ChangeEntity(String hint, String ip) {
      this.hint = hint;
      this.ip = ip;
    }

    String hint, ip;
  }

  class IpChangeAdapter extends AbsRVAdapter<ChangeEntity, IpChangeAdapter.IpChangeHolder> {

    public IpChangeAdapter(Context context, List<ChangeEntity> data) {
      super(context, data);
    }

    @Override protected IpChangeHolder getViewHolder(View convertView, int viewType) {
      return new IpChangeHolder(convertView);
    }

    @Override protected int setLayoutId(int type) {
      return R.layout.item_ip_change;
    }

    @Override protected void bindData(IpChangeHolder holder, int position, ChangeEntity item) {
      if (TextUtils.isEmpty(item.hint)) {
        holder.hint.setVisibility(View.GONE);
      } else {
        holder.hint.setText(item.hint);
      }
      holder.ip.setText(item.ip);
    }

    class IpChangeHolder extends AbsHolder {
      @Bind(R.id.hint) TextView hint;
      @Bind(R.id.ip) TextView ip;
      View itemView;

      IpChangeHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
      }
    }
  }
}
