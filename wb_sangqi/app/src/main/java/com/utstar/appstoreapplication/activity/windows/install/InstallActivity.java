//package com.utstar.appstoreapplication.activity.windows.install;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.widget.TextView;
//import butterknife.Bind;
//import com.arialyy.aria.core.Aria;
//import com.arialyy.aria.core.task.Task;
//import com.arialyy.frame.util.AndroidUtils;
//import com.utstar.appstoreapplication.activity.R;
//import com.utstar.appstoreapplication.activity.databinding.ActivityInstallBinding;
//import com.utstar.appstoreapplication.activity.manager.ImageManager;
//import com.utstar.appstoreapplication.activity.utils.ApkUtil;
//import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
//import java.lang.ref.WeakReference;
//
///**
// * 即点即用加载界面
// */
//public class InstallActivity extends BaseActivity<ActivityInstallBinding> {
//  private final String TAG = "InstallActivity";
//  /**
//   * 安装完成
//   */
//  private static final int COMPLETE = 0xab1;
//  /**
//   * 等待中
//   */
//  private static final int WATING = 0xab2;
//
//  /**
//   * 安装玩吧
//   */
//  public static final int CODE_WANBA = 0xaa1;
//  /**
//   * 安装游戏
//   */
//  public static final int CODE_GAME = 0xaa2;
//
//  /**
//   * 需要安装的apk路径key
//   */
//  public static final String KEY_APK_PATH = "KEY_APK_PATH";
//  /**
//   * 类型key
//   * {@link #CODE_WANBA}、{@link #CODE_GAME}
//   */
//  public static final String KEY_TYPE = "TYPE";
//
//  private String mPath;
//  private String mPackageName;
//  private int mCode = -1;
//  @Bind(R.id.hint) TextView mHint;
//
//  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//    @Override public void onReceive(Context context, Intent intent) {
//      String action = intent.getAction();
//      if (action.equals(Intent.ACTION_PACKAGE_REPLACED) || action.equals(
//          Intent.ACTION_PACKAGE_ADDED)) {
//        String pkgName = intent.getDataString();
//        if (mCode != CODE_WANBA && pkgName.equals(mPackageName)) {
//          mHandler.obtainMessage(COMPLETE, mPackageName).sendToTarget();
//        }
//      }
//    }
//  };
//
//  private Handler mHandler = new Handler() {
//    @Override public void handleMessage(Message msg) {
//      super.handleMessage(msg);
//      switch (msg.what) {
//        case COMPLETE:
//          mHint.setText("平台正在更新，请耐心等待...  " + 100 + "%");
//          AndroidUtils.startOtherApp(InstallActivity.this, String.valueOf(msg.obj));
//          InstallActivity.this.finish();
//          break;
//        case WATING:
//          break;
//      }
//    }
//  };
//
//  @Override protected void init(Bundle savedInstanceState) {
//    super.init(savedInstanceState);
//    loadParam();
//  }
//
//  private void loadParam() {
//    mPath = getIntent().getStringExtra(KEY_APK_PATH);
//    mCode = getIntent().getIntExtra(KEY_TYPE, -1);
//    if (TextUtils.isEmpty(mPath)) {
//      Log.e(TAG, "包名不能为null");
//      finish();
//    } else if (mCode == -1) {
//      Log.e(TAG, "code 不能为null");
//    }
//    mPackageName = AndroidUtils.getApkPackageName(this, mPath);
//    ImageManager.getInstance().loadBackground(getRootView(), R.mipmap.bg_launcher);
//  }
//
//  private void install() {
//    if (mCode == CODE_WANBA) {
//      ApkUtil.installWanba(this, mPath);
//    } else if (mCode == CODE_GAME) {
//      Util.installGame(this, mPath);
//      Aria.whit(this).load()
//    }
//  }
//
//  @Override protected void onResume() {
//    super.onResume();
//    IntentFilter filter = new IntentFilter();
//    filter.addAction(Intent.ACTION_PACKAGE_ADDED);
//    filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
//    filter.addDataScheme("package");
//    registerReceiver(mReceiver, filter);
//    Aria.whit(this).addSchedulerListener(new MyDownloadListener(mHandler));
//  }
//
//  @Override protected void onPostResume() {
//    super.onPostResume();
//    install();
//  }
//
//  @Override protected void onDestroy() {
//    super.onDestroy();
//    unregisterReceiver(mReceiver);
//  }
//
//  @Override protected int setLayoutId() {
//    return R.layout.activity_install;
//  }
//
//  private static class MyDownloadListener extends Aria.SimpleSchedulerListener {
//    WeakReference<Handler> handler;
//
//    MyDownloadListener(Handler handler) {
//      this.handler = new WeakReference<>(handler);
//    }
//
//    @Override public void onTaskRunning(Task task) {
//      super.onTaskRunning(task);
//      handler.get().obtainMessage(WATING, task).sendToTarget();
//    }
//  }
//}
