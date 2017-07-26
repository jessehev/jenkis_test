package com.utstar.appstoreapplication.activity.windows.common;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.util.StreamUtil;
import com.arialyy.frame.util.show.FL;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.DialogLogBinding;
import com.utstar.appstoreapplication.activity.windows.base.BaseDialog;
import java.io.FileInputStream;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Aria.Lao on 2017/1/12.
 * 日志log
 */
public class LogDialog extends BaseDialog<DialogLogBinding> {
  @Bind(R.id.pb) ProgressBar mPb;
  @Bind(R.id.log) TextView mLog;

  @Override protected int setLayoutId() {
    return R.layout.dialog_log;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    String path = FL.getLogPath();
    Observable.just(path).subscribeOn(Schedulers.newThread()).map(s -> {
      byte[] data = null;
      try {
        FileInputStream fis = new FileInputStream(path);
        data = StreamUtil.readStream(fis);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return data;
    }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<byte[]>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {

      }

      @Override public void onNext(byte[] data) {
        if (data != null && data.length > 0) {
          getBinding().setNetLog(new String(data));
          mLog.requestFocus();
          mPb.setVisibility(View.GONE);
        }
      }
    });
  }
}
