//package com.utstar.appstoreapplication.activity.test;
//
//import android.content.Intent;
//import android.graphics.SurfaceTexture;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Surface;
//import android.view.TextureView;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import butterknife.Bind;
//import butterknife.OnClick;
//import butterknife.OnFocusChange;
//import com.arialyy.frame.util.show.L;
//import com.arialyy.frame.util.show.T;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.devbrackets.android.exomedia.listener.OnPreparedListener;
//import com.devbrackets.android.exomedia.ui.widget.VideoView;
//import com.utstar.appstoreapplication.activity.R;
//import com.utstar.appstoreapplication.activity.databinding.TestActivityVideoDetailBinding;
//import com.utstar.appstoreapplication.activity.entity.net_entity.VideoGameEntity;
//import com.utstar.appstoreapplication.activity.manager.AnimManager;
//import com.utstar.appstoreapplication.activity.manager.TurnManager;
//import com.utstar.appstoreapplication.activity.windows.base.BaseActivity;
//import java.io.IOException;
//import java.lang.ref.WeakReference;
//import java.util.List;
//import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
//
///**
// * Created by Aria.Lao on 2017/6/19.
// */
//
//public class TestVideoDetailActivity extends BaseActivity<TestActivityVideoDetailBinding> {
//
//  @Bind(R.id.video_img_1) ImageView mVideoImg1;
//  @Bind(R.id.video_img_2) ImageView mVideoImg2;
//
//  @Bind(R.id.game_img_1) ImageView mGameImg1;
//  @Bind(R.id.game_img_2) ImageView mGameImg2;
//  @Bind(R.id.game_img_3) ImageView mGameImg3;
//  @Bind(R.id.game_img_4) ImageView mGameImg4;
//
//  @Bind(R.id.video_view) VideoView mVideo;
//  static int currentPosition = 0;
//  VideoGameEntity mEntity;
//  private String mUrl;
//  private final String mPackageId = "16021215165731000002";
//
//  @Override protected void init(Bundle savedInstanceState) {
//    super.init(savedInstanceState);
//    getModule(VideoDetailModule.class).getData();
//    mVideo.setOnPreparedListener(new OnPreparedListener() {
//      @Override public void onPrepared() {
//        mVideo.start();
//      }
//    });
//    //mVideo.setVideoURI(Uri.parse("https://archive.org/download/Popeye_forPresident/Popeye_forPresident_512kb.mp4"));
//  }
//
//  private void loadVideoImg() {
//    List<VideoGameEntity.ChildEntity> videoImgs = mEntity.videos;
//    loadImg(mVideoImg1, videoImgs.get(1).imgUrl);
//    loadImg(mVideoImg2, videoImgs.get(2).imgUrl);
//  }
//
//  private void loadGameImg() {
//    List<VideoGameEntity.ChildEntity> gameImgs = mEntity.games;
//    loadImg(mGameImg1, gameImgs.get(0).imgUrl);
//    loadImg(mGameImg2, gameImgs.get(1).imgUrl);
//    loadImg(mGameImg3, gameImgs.get(2).imgUrl);
//    loadImg(mGameImg4, gameImgs.get(3).imgUrl);
//  }
//
//  private void loadImg(ImageView img, String url) {
//    Glide.with(this)
//        .load(url)
//        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//        .error(R.mipmap.place_def)
//        .placeholder(R.mipmap.place_def)
//        .bitmapTransform(new RoundedCornersTransformation(this, 30, 0,
//            RoundedCornersTransformation.CornerType.ALL))
//        .into(img);
//  }
//
//  private void initUI() {
//    loadGameImg();
//    loadVideoImg();
//    getModule(VideoDetailModule.class).getUrl(mEntity.videos.get(0).code);
//  }
//
//  @OnFocusChange({
//      R.id.game_img_1, R.id.game_img_2, R.id.game_img_3, R.id.game_img_4, R.id.video_img_1,
//      R.id.video_img_2
//  }) void hasFocus(View v, boolean hasFocus) {
//    if (hasFocus) {
//      AnimManager.getInstance().enlarge(v, 1.1f);
//    } else {
//      AnimManager.getInstance().narrow(v, 1.1f);
//    }
//  }
//
//  @OnClick({ R.id.game_img_1, R.id.game_img_2, R.id.game_img_3, R.id.game_img_4 }) void gameClick(
//      View view) {
//    switch (view.getId()) {
//      case R.id.game_img_1:
//        TurnManager.getInstance().turnGameDetail(this, 304);
//        break;
//      case R.id.game_img_2:
//        TurnManager.getInstance().turnGameDetail(this, 291);
//        break;
//      case R.id.game_img_3:
//        TurnManager.getInstance().turnGameDetail(this, 296);
//        break;
//      case R.id.game_img_4:
//        TurnManager.getInstance().turnGameDetail(this, 301);
//        break;
//    }
//  }
//
//  @OnClick({ R.id.video_img_1, R.id.video_img_2 }) void videoClick(View view) {
//    if (mEntity.isBuy) {
//      T.showShort(this, "已经购买");
//    } else {
//      TurnManager.getInstance().turnOrderDetail(this, 0, mPackageId);
//    }
//  }
//
//  @Override protected int setLayoutId() {
//    return R.layout.test_activity_video_detail;
//  }
//
//  private void play(String url) {
//    T.showShort(this, "url ==> " + url);
//    mUrl = url;
//    if (TextUtils.isEmpty(url)) return;
//    mVideo.setVideoURI(Uri.parse(url));
//  }
//
//  @Override protected void dataCallback(int result, Object data) {
//    super.dataCallback(result, data);
//    if (result == VideoDetailModule.GET_PLAY_URL) {
//      runOnUiThread(() -> {
//        if (data != null) {
//          play(String.valueOf(data));
//        }
//      });
//    } else if (result == VideoDetailModule.GET_DETAIL) {
//      runOnUiThread(() -> {
//        if (data == null) {
//          T.showShort(TestVideoDetailActivity.this, "数据为null");
//          return;
//        }
//        mEntity = (VideoGameEntity) data;
//        initUI();
//      });
//    }
//  }
//
//  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    if (requestCode == TurnManager.PAY_REQUEST_CODE
//        && resultCode == TurnManager.PAY_RESULT_CODE
//        && data != null
//        && data.getBooleanExtra(TurnManager.PAY_SUCCESS_KEY, false)) {
//      mEntity.isBuy = true;
//    }
//  }
//}
