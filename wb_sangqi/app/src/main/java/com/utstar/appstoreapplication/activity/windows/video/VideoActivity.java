package com.utstar.appstoreapplication.activity.windows.video;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.MediaController;
import android.widget.VideoView;
import butterknife.Bind;
import com.arialyy.frame.util.NetUtils;
import com.arialyy.frame.util.show.L;
import com.arialyy.frame.util.show.T;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.databinding.ActivityVideoBinding;
import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;

/**
 * Created by Aria.Lao on 2017/2/13.
 */
public class VideoActivity extends BaseActivity<ActivityVideoBinding> {
  public static final String VIDEO_URL_KEY = "VIDEO_URL_KEY";

  @Bind(R.id.video_view) VideoView mVideoView;
  private String mVideoUrl = "";
  MediaController mController;

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
    mVideoUrl = getIntent().getStringExtra(VIDEO_URL_KEY);
    if (TextUtils.isEmpty(mVideoUrl)) {
      L.e(TAG, "视频地址不能为null");
      finish();
      return;
    }
    showLoadingDialog();
    if (!NetUtils.isConnected(this)) {
      T.showShort(VideoActivity.this, "没有网络，不能播放视频");
      finish();
      return;
    }

    mController = new MediaController(this);
    mVideoView.setMediaController(mController);
    mVideoView.setVideoURI(Uri.parse(mVideoUrl));
    mVideoView.setOnErrorListener((mp, what, extra) -> {
      T.showShort(VideoActivity.this, "视频错误");
      return false;
    });
    mVideoView.setOnCompletionListener(mp -> finish());
    mVideoView.setOnPreparedListener(mp -> dismissLoadingDialog());
    mVideoView.start();
  }

  @Override protected int setLayoutId() {
    return R.layout.activity_video;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    dismissLoadingDialog();
    if (mVideoView != null && mVideoView.isPlaying()) {
      mVideoView.stopPlayback();
    }
  }

  @Override protected void dataCallback(int result, Object data) {

  }
}
