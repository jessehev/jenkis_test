package com.utstar.baseplayer.view;

import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.FrameLayout;

import android.widget.Toast;
import com.utstar.baseplayer.R;
import com.utstar.baseplayer.exception.PlayerProcessException;
import com.utstar.baseplayer.utils.APPLog;
import com.utstar.baseplayer.utils.FL;
import com.utstar.baseplayer.utils.HttpUtil;
import com.utstar.baseplayer.utils.ToastUtil;

import com.utstar.baseplayer.utils.inf.IResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Richard
 */
public class MediaplayerFragment extends Fragment {
  private static final String TAG = "MediaplayerFragment";

  private interface MessageType {
    int SEEK = 0x01;
  }

  //    public static final int ANIM_TIME = 300;
  private static final int POOL_SIZE = 10;
  /**
   * 整个播放窗口
   */
  private ViewGroup mMediaplayerWindow;
  /**
   * 播放区域
   */
  private SurfaceView mPlayerScreen;
  /**
   * 播放holder
   */
  private SurfaceHolder mScreenHolder;
  /**
   * 播放控制条
   */
  private PlayerCtrlView mCtrlView;
  /**
   * 当前是否为全屏在播放
   */
  private boolean mIsFullScreen;
  /**
   * 播放区域左边界
   */
  private int mScreenLeft = 0;
  /**
   * 播放区域上边界
   */
  private int mScreenTop = 0;
  /**
   * 播放区域宽
   */
  private int mScreenWidth = FrameLayout.LayoutParams.MATCH_PARENT;
  /**
   * 播放区域高
   */
  private int mScreenHeight = FrameLayout.LayoutParams.MATCH_PARENT;
  /**
   * 播放地址列表
   */
  private String[] urls;
  /**
   * 当前正在播放的播放实例
   */
  private MediaPlayer mPlayer;
  /**
   * 当前播放到第几个地址
   */
  private int mCurrentUrlPosition;
  /**
   * 断点
   */
  private int mCurrentBreakpoint;
  /**
   * 准备播放的线程池
   */
  private ScheduledThreadPoolExecutor mScheduledThreadPoolExecutor;
  /**
   * 是否在准备中
   */
  private boolean mIsPrepared;
  /**
   * 是否调用了Start
   */
  private boolean mIsStarted;

  private Handler mFragmentHandler;

  private MediaPlayer.OnCompletionListener mOnCompletionListener;

  private MediaPlayer.OnErrorListener mOnErrorListener;

  private MediaPlayer.OnInfoListener mOnInfoListener;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    mMediaplayerWindow =
        (ViewGroup) inflater.inflate(R.layout.mediaplayer_fragment_layout, container, false);
    initView();
    return mMediaplayerWindow;
  }

  private void initView() {
    mPlayerScreen = (SurfaceView) mMediaplayerWindow.findViewById(R.id.player_screen);
    mPlayerScreen.getHolder().setKeepScreenOn(true);
    mPlayerScreen.getHolder().addCallback(new ScreenEvent());

    mCtrlView = new PlayerCtrlView(getActivity());
    mCtrlView.setContentView(R.layout.mediaplayer_ctrl_layout, mMediaplayerWindow,
        R.style.PlayerCtrlStyle);
    mCtrlView.setVisibility(View.INVISIBLE);

    mFragmentHandler = new Handler(new FragmentCallback());
    mIsFullScreen = true;
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (!mIsFullScreen) {
      return false;
    }
    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
        || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
        || keyCode == KeyEvent.KEYCODE_L) {
      mCtrlView.onRewind();
      return true;
    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
        || keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
        || keyCode == KeyEvent.KEYCODE_R) {
      mCtrlView.onFastForward();
      return true;
    } else if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
      if (PlayerCtrlView.Status.PLAY_STATUS == mCtrlView.getStatus()) {
        pause();
      } else if (PlayerCtrlView.Status.PAUSE_STATUS == mCtrlView.getStatus()) {
        play();
      }
      return true;
    }
    return false;
  }

  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (!mIsFullScreen) {
      return false;
    }

    if (PlayerCtrlView.Status.REWIND_STATUS == mCtrlView.getStatus()
        || PlayerCtrlView.Status.FAST_FORWARD_STATUS == mCtrlView.getStatus()) {
      mFragmentHandler.removeMessages(MessageType.SEEK);
      Message message =
          mFragmentHandler.obtainMessage(MessageType.SEEK, mCtrlView.getCurrentProgress(), 0);
      mFragmentHandler.sendMessageDelayed(message, 300);
      return true;
    }
    return false;
  }

  public void setOnInfoListener(MediaPlayer.OnInfoListener onInfoListener) {
    mOnInfoListener = onInfoListener;
  }

  public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
    mOnCompletionListener = onCompletionListener;
  }

  public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener) {
    mOnErrorListener = onErrorListener;
  }

  public int fullScreen() {
    APPLog.printSimpleInfo("fullScreen mIsFullScreen:" + mIsFullScreen);
    if (mIsFullScreen) {
      return -1;
    } else {
      mIsFullScreen = true;
      FrameLayout.LayoutParams params =
          new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
              FrameLayout.LayoutParams.MATCH_PARENT);
      mPlayerScreen.setLayoutParams(params);
      params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
          FrameLayout.LayoutParams.MATCH_PARENT);
      mMediaplayerWindow.setLayoutParams(params);
      mCtrlView.setVisibility(View.VISIBLE);
      return 0;
    }
  }

  public int exitFullScreen() {
    APPLog.printSimpleInfo("exitFullScreen mIsFullScreen:" + mIsFullScreen);
    if (!mIsFullScreen) {
      return -1;
    } else {
      mIsFullScreen = false;
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mScreenWidth, mScreenHeight);
      mPlayerScreen.setLayoutParams(params);

      params = new FrameLayout.LayoutParams(mScreenWidth, mScreenHeight);
      params.leftMargin = mScreenLeft;
      params.topMargin = mScreenTop;
      mMediaplayerWindow.setLayoutParams(params);
      mCtrlView.setVisibility(View.INVISIBLE);
      return 0;
    }
  }

  public int openDefaultMediaplayer(String playList) {
    APPLog.printSimpleInfo(
        "openDefaultMediaplayer\n" + "----------\n" + "playList->" + playList + "\n");
    fullScreen();
    setDataSourceList(playList);
    return start();
  }

  public int setDisplay(int left, int top, int width, int height) {
    APPLog.printSimpleInfo("setDisplay\n"
        + "----------\n"
        + "left->"
        + left
        + "\n"
        + "top->"
        + top
        + "\n"
        + "width->"
        + width
        + "\n"
        + "height->"
        + height
        + "\n");
    mScreenLeft = left;
    mScreenTop = top;
    mScreenWidth = width;
    mScreenHeight = height;
    if (width != FrameLayout.LayoutParams.MATCH_PARENT
        || height != FrameLayout.LayoutParams.MATCH_PARENT) {
      exitFullScreen();
    }
    return 0;
  }

  public int setDataSourceList(String playList) {
    APPLog.printSimpleInfo("setDataSourceList\n" + "----------\n" + "playList->" + playList + "\n");
    try {
      urls = playList.split(",");
      mCurrentUrlPosition = 0;
      if (urls.length > 1) {
        mCtrlView.setEndText("即将播放下一条内容");
      }
      release();
    } catch (Exception e) {
      APPLog.printError(e);
      return -1;
    }
    return 0;
  }

  public int setDataSource(String url) {
    APPLog.printSimpleInfo("setDataSource\n" + "----------\n" + "url->" + url + "\n");
    try {
      urls = new String[] { url };
      mCurrentUrlPosition = 0;
      mCurrentBreakpoint = 0;
      release();
    } catch (Exception e) {
      APPLog.printError(e);
      return -1;
    }
    return 0;
  }

  public int start() {
    APPLog.printSimpleInfo("start");
    if (urls == null || urls.length == 0) {
      return -1;
    }
    mIsStarted = true;
    if (mScreenHolder == null) {
      return 0;
    }
    try {
      mPlayer = createMediaplayer(urls[mCurrentUrlPosition]);
    } catch (PlayerProcessException e) {
      APPLog.printError(e);
      return -1;
    }
    return 0;
  }

  public int play() {
    APPLog.printSimpleInfo("play");
    if (mPlayer == null || !mIsPrepared) {
      return -1;
    }
    if (mIsFullScreen) {
      mCtrlView.onPlay();
    } else {
      mCtrlView.setStatus(PlayerCtrlView.Status.PLAY_STATUS);
    }
    mPlayer.start();
    return 0;
  }

  public int pause() {
    APPLog.printSimpleInfo("pause");
    if (mPlayer == null || !mIsPrepared) {
      return -1;
    }
    if (mIsFullScreen) {
      mCtrlView.onPause();
    } else {
      mCtrlView.setStatus(PlayerCtrlView.Status.PAUSE_STATUS);
    }
    mPlayer.pause();
    return 0;
  }

  public void setBreakpoint(int breakpoint) {
    APPLog.printSimpleInfo("setBreakpoint\n" + "----------\n" + "breakpoint->" + breakpoint + "\n");
    mCurrentBreakpoint = breakpoint;
  }

  public int seekTo(int position) {
    APPLog.printSimpleInfo("seekTo\n" + "----------\n" + "position->" + position + "\n");
    if (mPlayer == null || !mIsPrepared) {
      if (mIsStarted) {
        mCurrentBreakpoint = position;
        return 0;
      }
      return -1;
    }
    if (mIsFullScreen) {
      mCtrlView.onPlay();
    } else {
      mCtrlView.setStatus(PlayerCtrlView.Status.PLAY_STATUS);
    }
    mPlayer.seekTo(position);
    mPlayer.start();
    return 0;
  }

  public int getDuration() {
    APPLog.printSimpleInfo("getDuration");
    if (mPlayer == null || !mIsPrepared) {
      return 0;
    }
    return mPlayer.getDuration();
  }

  public int getCurrentPosition() {
    APPLog.printSimpleInfo("getCurrentPosition");
    if (mPlayer == null || !mIsPrepared) {
      return 0;
    }
    return mPlayer.getCurrentPosition();
  }

  public int release() {
    APPLog.printSimpleInfo("release");
    if (mCtrlView != null) {
      mCtrlView.release();
    }
    if (mPlayer != null) {
      mPlayer.release();
    }
    mPlayer = null;
    releaseMessge();
    return 0;
  }

  String mVideoUrl;

  private MediaPlayer createMediaplayer(String url) throws PlayerProcessException {
    if (TextUtils.isEmpty(url) || !URLUtil.isValidUrl(url)) {
      throw new PlayerProcessException("(" + url + ")是非法播放地址!");
    }
    if (mScreenHolder == null) {
      throw new PlayerProcessException("播放窗口尚未准备就绪");
    }
    if (url.contains("/EPG/jsp/gdgaoqing/en/vaitf/getVodPlayUrl.jsp")) {

      Map<String, String> head = new WeakHashMap<>();
      String session = getSession();
      APPLog.printSimpleInfo("session ==> " + session);
      head.put("Cookie", "JSESSIONID=" + getSession());
      new HttpUtil().get(url, null, head, new IResponse() {
        @Override public void onResponse(String data) {
          try {
            JSONObject jsonObject = new JSONObject(data);
            mVideoUrl = jsonObject.optString("playurl");
            APPLog.printSimpleInfo("data ==> " +  data);
            APPLog.printSimpleInfo("videoUrl ==> " +  mVideoUrl);
          } catch (JSONException e) {
            e.printStackTrace();
          }

          synchronized (MediaplayerFragment.this) {
            MediaplayerFragment.this.notifyAll();
          }
        }

        @Override public void onError(IOException e) {

        }
      });
      synchronized (this) {
        try {
          wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    release();
    mCtrlView.init();
    if (mIsFullScreen) {
      mCtrlView.setVisibility(View.VISIBLE);
    }
    MediaPlayerEvent playerEvent = new MediaPlayerEvent();
    MediaPlayer player = new MediaPlayer();
    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    player.setOnCompletionListener(playerEvent);
    player.setOnPreparedListener(playerEvent);
    player.setOnErrorListener(playerEvent);
    player.setOnInfoListener(playerEvent);
    player.setDisplay(mScreenHolder);
    try {
      //player.setDataSource(url);
      player.setDataSource(mVideoUrl);
    } catch (IOException e) {
      APPLog.printError(e);
      throw new PlayerProcessException("无法播放（" + url + "）");
    }
    prepareAsync(player);
    return player;
  }

  private String parseResult(Response response) {
    try {
      String result = response.body().string();
      JSONObject jsonObject = new JSONObject(result);
      return jsonObject.optString("playurl");
    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String getSession() {
    Uri uri = Uri.parse("content://com.utstar.appstoreapplication.common.db.UserIDProvider");
    Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
    if (cursor != null && cursor.moveToFirst()) {
      int index = cursor.getColumnIndex("epgToken");
      if (index != -1) {
        String value = cursor.getString(index);
        cursor.close();
        return value;
      } else {
        cursor.close();
        return null;
      }
    } else {
      return null;
    }
  }

  private void prepareAsync(final MediaPlayer player) {
    if (mScheduledThreadPoolExecutor == null) {
      mScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(POOL_SIZE);
    }
    mCtrlView.onLoading(0);
    mScheduledThreadPoolExecutor.execute(new Runnable() {
      @Override public void run() {
        mIsPrepared = false;
        player.prepareAsync();
      }
    });
  }

  private void releaseMessge() {
    if (mFragmentHandler != null) {
      mFragmentHandler.removeMessages(MessageType.SEEK);
    }
  }

  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (hidden) {
      release();
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    release();
  }

  class MediaPlayerEvent implements MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener,
      MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    @Override public void onCompletion(MediaPlayer mp) {
      mCurrentUrlPosition++;
      APPLog.printSimpleInfo(
          "onCompletion mCurrentUrlPosition:" + mCurrentUrlPosition + "/length:" + urls.length);
      if (mCurrentUrlPosition < urls.length) {
        start();
        if (mCurrentUrlPosition >= urls.length - 1) {
          mCtrlView.setEndText("即将播放完成");
        }
      } else {
        mCurrentUrlPosition = 0;
        release();
        if (mOnCompletionListener != null) {
          mOnCompletionListener.onCompletion(mp);
        }
      }
    }

    @Override public boolean onError(MediaPlayer mp, int what, int extra) {
      if (mOnErrorListener != null) {
        mOnErrorListener.onError(mp, what, extra);
      }
      return false;
    }

    //---------------
    @Override public boolean onInfo(MediaPlayer mp, int what, int extra) {
      switch (what) {
        case MediaPlayer.MEDIA_INFO_UNKNOWN:
          ToastUtil.getInstace().showToastByString(getActivity(), "无法播放当前视频", 30);
          break;
        case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
          mCtrlView.onLoaded();
          break;
        case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
          break;
        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
          mCtrlView.onLoading(0);
          break;
        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
          mCtrlView.onLoaded();
          break;
        default:
          break;
      }
      if (mOnInfoListener != null) {
        mOnInfoListener.onInfo(mp, what, extra);
      }
      return false;
    }

    //----------------
    @Override public void onPrepared(MediaPlayer mp) {
      mIsPrepared = true;
      APPLog.printSimpleInfo("mCurrentBreakpoint:" + mCurrentBreakpoint);
      if (mCurrentBreakpoint >= 2000) {
        mp.seekTo(mCurrentBreakpoint);
      }
      mp.start();
      play();
      mCtrlView.setPlayer(mp);
      mCtrlView.setMaxProgress(mp.getDuration());
      mCtrlView.setProgress(mp.getCurrentPosition());
    }
  }

  class ScreenEvent implements SurfaceHolder.Callback {
    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      APPLog.printSimpleInfo("surfaceChanged");
      mScreenHolder = holder;
    }

    @Override public void surfaceCreated(SurfaceHolder holder) {
      APPLog.printSimpleInfo("surfaceCreated");
      mScreenHolder = holder;
      if (mIsStarted) {
        start();
      }
    }

    @Override public void surfaceDestroyed(SurfaceHolder holder) {
      APPLog.printSimpleInfo("surfaceDestroyed");
      mScreenHolder = null;
      if (mPlayer != null) {
        mCurrentBreakpoint = mPlayer.getCurrentPosition();
      }
    }
  }

  class FragmentCallback implements Handler.Callback {
    @Override public boolean handleMessage(Message msg) {
      if (MessageType.SEEK == msg.what) {
        seekTo(msg.arg1);
      }
      return true;
    }
  }
}
