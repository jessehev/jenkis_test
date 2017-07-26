package com.utstar.baseplayer.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.utstar.baseplayer.R;
import com.utstar.baseplayer.utils.APPLog;
import com.utstar.baseplayer.utils.ToastUtil;

/**
 * Created by Richard
 */
public class PlayerCtrlView {

    private String mEndText = "即将播放结束";
    private View mCtrlView;
    private TextView mSpeed;

    public enum Status {
        /**
         * 初始化状态
         */
        INIT_STATUS,
        /**
         * 播放状态
         */
        PLAY_STATUS,
        /**
         * 暂停状态
         */
        PAUSE_STATUS,
        /**
         * 快进状态
         */
        FAST_FORWARD_STATUS,
        /**
         * 快退状态
         */
        REWIND_STATUS,
        /**
         * 释放状态
         */
        RELEASE_STATUS
    }

    public static final int ANIM_TIME = 300;
    /**
     * 状态条显示时间
     */
    public static final int STATUS_BAR_DISPLAY_TIME = 15 * 1000;
    /**
     * 音量条显示时间
     */
    public static final int VOLUME_DISPLAY_TIME = 5 * 1000;
    /**
     * 快进/快退速度
     */
    public int DELTA = 10 * 1000;
    public static final int MAX_OFFSET = 5 * 1000;

    private Context context;
    /**
     * 影片当前时长
     */
    private TextView currentTimeView;
    /**
     * 影片总时长
     */
    private TextView totalTimeView;
    /**
     * 状态条状态视图
     */
    private ImageView playStatusIcon;
    /**
     * 屏幕中间状态视图
     */
    private ImageView playStatusImg;
    /**
     * 播放进度条
     */
    private SeekBar playProgressView;
    /**
     * 音量大小条
     */
    private SeekBar volumeProgressView;
    /**
     * 静音视图
     */
    private ImageView muteImg;
    /**
     * 播放时屏幕中间显示的图片
     */
    private int playImgResId;
    /**
     * 播放时状态条显示的图片
     */
    private int playIconResId;
    /**
     * 快进时屏幕中间显示的图片
     */
    private int fastForwardImgResId;
    /**
     * 快进时状态条显示的图片
     */
    private int fastForwardIconResId;
    /**
     * 快退时屏幕中间显示的图片
     */
    private int rewindImgResId;
    /**
     * 快退时状态条图片
     */
    private int rewindIconResId;
    /**
     * 暂停时屏幕中间显示的图片
     */
    private int pauseImgResId;
    /**
     * 暂停时状态条图片
     */
    private int pauseIconResId;
    /**
     * 播放状态
     *
     * @see PlayerCtrlView.Status
     */
    private Status status = Status.INIT_STATUS;
    /**
     * 最大音量大小
     */
    private int maxVolume;
    /**
     * 当前音量大小
     */
    private int currentVolume;
    /**
     * 影片长度，毫秒为单位
     */
    private int maxProgress = 1;
    /**
     * 当前播放位置，毫秒为单位
     */
    private int currentProgress;
    //    private long lastForwardTime;
    /**
     * 加载中进度圈
     */
    private View loadingView;
    /**
     * 状态条区域，包含有状态图标，进度条
     */
    private View playerStatusBar;
    /**
     * 音量条区域，包含有音量大小条，音量+、-标示
     */
    private View playerVolumeView;
    private boolean isLoading;
    private MediaPlayer player;
    private ValueAnimator hideAnim;
    private ValueAnimator showAnim;
    private int statusBarTop;
    private int statusBarHeight;
    private boolean isFinish;
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public PlayerCtrlView(Context context) {
        this.context = context;
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        init();
    }

    public void init() {
        APPLog.printSimpleInfo("init status:" + status);
        status = Status.INIT_STATUS;
        pauseProgress();
        initView();
//        lastForwardTime = System.currentTimeMillis();
    }

    public void release() {
        status = Status.RELEASE_STATUS;
        if (ctrlHandler.hasMessages(HIDE_VOLUME_MSG)) {
            ctrlHandler.removeMessages(HIDE_VOLUME_MSG);
        }
        if (ctrlHandler.hasMessages(HIDE_PROGRESS_MSG)) {
            ctrlHandler.removeMessages(HIDE_PROGRESS_MSG);
        }
        player = null;
        initView();
        pauseProgress();
    }

    private void initView() {
        setProgress(0);
        setMaxProgress(1);
        if (playerStatusBar != null && playerStatusBar.getVisibility() == View.VISIBLE) {
            hideAnim();
        }
        if (playStatusImg != null && playStatusImg.getVisibility() == View.VISIBLE) {
            playStatusImg.setVisibility(View.INVISIBLE);
        }
        if (loadingView != null && loadingView.getVisibility() == View.VISIBLE) {
            loadingView.setVisibility(View.INVISIBLE);
        }
        if (totalTimeView != null) {
            totalTimeView.setText("--:--:--");
        }
        if (currentTimeView != null) {
            currentTimeView.setText("--:--:--");
        }
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = (maxProgress / 1000) * 1000;
        if (playProgressView != null) {
            playProgressView.setMax(maxProgress);
        }

        if (totalTimeView != null) {
            String maxText = formatTime(maxProgress);
            totalTimeView.setText(maxText);
        }
        DELTA = Math.max(1, maxProgress / 100);
    }

    public void setEndText(String text) {
        mEndText = text;
    }

    public void setProgress(int progress) {
        this.currentProgress = progress;
        if (playProgressView != null) {
            playProgressView.setProgress(progress);
        }

        if (currentTimeView != null && currentTimeView.getVisibility() == View.VISIBLE) {
            String progressText = formatTime(progress);
            currentTimeView.setText(progressText);
            displayCurrentTime();
        }
        if (progress <= 0 || player == null) {
            return;
        }
        if (progress > maxProgress - 5000) {
            if (!isFinish) {
                ToastUtil.getInstace().showToastByString(context, mEndText, 30);
            }
            isFinish = true;
        } else {
            isFinish = false;
        }
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setMaxVolume(int value) {
        this.maxVolume = value;
        if (volumeProgressView != null) {
            volumeProgressView.setMax(maxVolume);
        }
    }

    public void setCurrentVolume(int currentVolume) {
        this.currentVolume = currentVolume;
        if (volumeProgressView != null) {
            volumeProgressView.setProgress(currentVolume);
            displayVolumeView();
        }
    }

    public View getPlayerVolumeView() {
        return volumeProgressView;
    }

    public void setContentView(@android.support.annotation.LayoutRes int layout, @NonNull ViewGroup parentView, int styleId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mCtrlView = inflater.inflate(layout, parentView, false);
        playerStatusBar = mCtrlView.findViewById(R.id.player_status_bar);
        playerVolumeView = mCtrlView.findViewById(R.id.player_volume_view);
        currentTimeView = (TextView) mCtrlView.findViewById(R.id.player_current_time);
        totalTimeView = (TextView) mCtrlView.findViewById(R.id.player_total_time);
        playStatusIcon = (ImageView) mCtrlView.findViewById(R.id.player_status_icon);
        playStatusImg = (ImageView) mCtrlView.findViewById(R.id.player_status_img);
        playProgressView = (SeekBar) mCtrlView.findViewById(R.id.player_progress);
        volumeProgressView = (SeekBar) mCtrlView.findViewById(R.id.player_volume_bar);
        muteImg = (ImageView) mCtrlView.findViewById(R.id.player_mute_icon);
        loadingView = mCtrlView.findViewById(R.id.player_loading);
        mSpeed = (TextView) mCtrlView.findViewById(R.id.speed);

        if (volumeProgressView != null) {
            setMaxVolume(maxVolume);
            setCurrentVolume(currentVolume);
        }

        if (playProgressView != null) {
            playProgressView.setMax(maxProgress);
            playProgressView.setProgress(currentProgress);
        }

        if (totalTimeView != null) {
            totalTimeView.setText("--:--:--");
        }

        if (currentTimeView != null) {
            currentTimeView.setText("--:--:--");
        }

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) playerStatusBar.getLayoutParams();
        statusBarTop = params.topMargin;
        statusBarHeight = params.height;

        TypedArray typedArray = context.obtainStyledAttributes(styleId, R.styleable.PlayerAttr);
        playImgResId = typedArray.getResourceId(R.styleable.PlayerAttr_play_img, -1);
        playIconResId = typedArray.getResourceId(R.styleable.PlayerAttr_play_icon, -1);
        fastForwardImgResId = typedArray.getResourceId(R.styleable.PlayerAttr_fast_forward_img, -1);
        fastForwardIconResId = typedArray.getResourceId(R.styleable.PlayerAttr_fast_forward_icon, -1);
        rewindImgResId = typedArray.getResourceId(R.styleable.PlayerAttr_rewind_img, -1);
        rewindIconResId = typedArray.getResourceId(R.styleable.PlayerAttr_rewind_icon, -1);
        pauseImgResId = typedArray.getResourceId(R.styleable.PlayerAttr_pause_img, -1);
        pauseIconResId = typedArray.getResourceId(R.styleable.PlayerAttr_pause_icon, -1);
        typedArray.recycle();
        parentView.addView(mCtrlView);
    }

    public void setVisibility(int visibility) {
        APPLog.printSimpleInfo("visibility:" + visibility);
        if (playerStatusBar != null && visibility != View.VISIBLE) {
            playerStatusBar.setVisibility(visibility);
        }
        if (playerVolumeView != null) {
            playerVolumeView.setVisibility(visibility);
        }
        if (muteImg != null) {
            muteImg.setVisibility(visibility);
        }
        if (playStatusImg != null) {
            playStatusImg.setVisibility(visibility);
        }
        if (loadingView != null) {
            if (!isLoading) {
                loadingView.setVisibility(View.INVISIBLE);
            } else {
                loadingView.setVisibility(View.VISIBLE);
            }
        }
        if (visibility != View.VISIBLE) {
            return;
        }
        if (status == Status.PLAY_STATUS) {
            displayStatusBar(false);
        }
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    public void onFastForward() {
        APPLog.printSimpleInfo("onFastForward status:" + status);
        if (status == Status.INIT_STATUS || status == Status.RELEASE_STATUS) {
            return;
        }
        if (currentProgress >= maxProgress - MAX_OFFSET) {
            return;
        }
        changeProgress(DELTA);
        if (status != Status.FAST_FORWARD_STATUS) {
            displayStatus(fastForwardImgResId, fastForwardIconResId);
            status = Status.FAST_FORWARD_STATUS;
            pauseProgress();
            displayStatusBar(true);
            if (loadingView != null && loadingView.getVisibility() == View.VISIBLE) {
                loadingView.setVisibility(View.GONE);
            }
        }
    }

    public void onRewind() {
        APPLog.printSimpleInfo("onRewind status:" + status);
        if (status == Status.INIT_STATUS || status == Status.RELEASE_STATUS) {
            return;
        }
        changeProgress(-DELTA);
        if (status != Status.REWIND_STATUS) {
            displayStatus(rewindImgResId, rewindIconResId);
            status = Status.REWIND_STATUS;
            pauseProgress();
            displayStatusBar(true);
            if (loadingView != null && loadingView.getVisibility() == View.VISIBLE) {
                loadingView.setVisibility(View.GONE);
            }
        }
    }

    public void onPause() {
        APPLog.printSimpleInfo("onPause status:" + status);
        if (status == Status.INIT_STATUS || status == Status.RELEASE_STATUS) {
            return;
        }
        if (status != Status.PAUSE_STATUS) {
            pauseProgress();
            displayStatus(pauseImgResId, pauseIconResId);
            status = Status.PAUSE_STATUS;
            displayStatusBar(true);
            if (loadingView != null && loadingView.getVisibility() == View.VISIBLE) {
                loadingView.setVisibility(View.GONE);
            }
        }
    }

    public void onPlay() {
        APPLog.printSimpleInfo("onPlay status:" + status);
        if (status != Status.PLAY_STATUS) {
            playProgress();
            displayStatus(playImgResId, playIconResId);
            status = Status.PLAY_STATUS;
            displayStatusBar(false);
        }
        if (isLoading && loadingView != null && loadingView.getVisibility() != View.VISIBLE) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    public void onMute() {
        if (muteImg != null && muteImg.getVisibility() != View.VISIBLE) {
            muteImg.setVisibility(View.VISIBLE);
        }
    }

    public void onVolumeValueChange(int value) {
        setCurrentVolume(value);
//        displayVolumeView();
    }

    public void onLoading(int bufferedSize) {
        isLoading = true;
        if (loadingView != null && (status == Status.PLAY_STATUS || status == Status.INIT_STATUS)) {
            loadingView.setVisibility(View.VISIBLE);
            ctrlHandler.removeMessages(COMPUTE_NETWORK_SPEED);
            ctrlHandler.sendEmptyMessage(COMPUTE_NETWORK_SPEED);
        }
        pauseProgress();
    }

    public void onLoaded() {
        if (loadingView != null && loadingView.getVisibility() == View.VISIBLE) {
            loadingView.setVisibility(View.GONE);
        }
//        APPLog.printSimpleInfo(">>>>>>>status:" + status);
        if (status == Status.PLAY_STATUS || status == Status.INIT_STATUS) {
            playProgress();
        }
        isLoading = false;
    }

    private void pauseProgress() {
        if (ctrlHandler.hasMessages(UPDATE_PROGRESS_MSG)) {
            ctrlHandler.removeMessages(UPDATE_PROGRESS_MSG);
        }
    }

    private void displayStatusBar(boolean isHold) {
        if (playerStatusBar == null) {
            return;
        }
        if (playerStatusBar.getVisibility() != View.VISIBLE) {
            showAnim();
        }
        if (ctrlHandler.hasMessages(HIDE_PROGRESS_MSG)) {
            ctrlHandler.removeMessages(HIDE_PROGRESS_MSG);
        }
        if (!isHold) {
            ctrlHandler.sendEmptyMessageDelayed(HIDE_PROGRESS_MSG, STATUS_BAR_DISPLAY_TIME);
        }
    }

    private void playProgress() {
        if (ctrlHandler.hasMessages(UPDATE_PROGRESS_MSG)) {
            ctrlHandler.removeMessages(UPDATE_PROGRESS_MSG);
        }
        ctrlHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS_MSG, 1000);
    }

    private void displayStatus(int imgResId, int iconResId) {
        APPLog.printSimpleInfo("status:" + status + "/imgResId:" + imgResId + "/iconResId:" + iconResId);
        if (playStatusImg != null) {
            if (imgResId != -1) {
                playStatusImg.setImageResource(imgResId);
                playStatusImg.setVisibility(View.VISIBLE);
            } else {
                playStatusImg.setVisibility(View.GONE);
            }
        }

        if (playStatusIcon != null && iconResId != -1) {
            playStatusIcon.setImageResource(iconResId);
        }
    }

    private void changeProgress(int value) {
//        long time = System.currentTimeMillis();
//        long offsetTime = time - lastForwardTime;
//        if (offsetTime < 950) {
//            return;
//        }
//        int delta = maxProgress / 100;
//        lastForwardTime = System.currentTimeMillis();
        APPLog.printSimpleInfo("changeProgress:" + value);
        currentProgress += value;
        if (currentProgress < 0) {
            currentProgress = 0;
        } else if (currentProgress > maxProgress) {
            currentProgress = maxProgress;
        }
        setProgress(currentProgress);
    }

    private void updateProgress() {
        if (status != Status.PLAY_STATUS) {
            return;
        }
        if (player != null) {
            setProgress(player.getCurrentPosition());
        } else {
            changeProgress(1000);
        }
    }

    private void displayVolumeView() {
        if (muteImg != null && currentVolume > 0) {
            muteImg.setVisibility(View.GONE);
        }

        APPLog.printSimpleInfo("displayVolumeView--------->" + (volumeProgressView));
        if (playerVolumeView != null && playerVolumeView.getVisibility() != View.VISIBLE) {
            playerVolumeView.setVisibility(View.VISIBLE);
        }
        if (ctrlHandler.hasMessages(HIDE_VOLUME_MSG)) {
            ctrlHandler.removeMessages(HIDE_VOLUME_MSG);
        }
        ctrlHandler.sendEmptyMessageDelayed(HIDE_VOLUME_MSG, VOLUME_DISPLAY_TIME);
    }

    private void displayCurrentTime() {
        int seekBarWidth = playProgressView.getWidth();
        int seekBarLeft = playProgressView.getLeft();
//        int seekBarOffset = playProgressView.getThumbOffset();
        int progress = playProgressView.getProgress();
        int max = playProgressView.getMax();
        int currentTimeViewWidth = currentTimeView.getWidth();

        int left = 0;
        int progressLeft = (int) ((float) progress / max * seekBarWidth);
        if (max != 0) {
            left = seekBarLeft + progressLeft - currentTimeViewWidth / 2;
        }

        if (left + currentTimeViewWidth > seekBarLeft + seekBarWidth + currentTimeViewWidth / 2) {
            left = seekBarLeft + seekBarWidth - currentTimeViewWidth;
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) currentTimeView.getLayoutParams();
        params.leftMargin = left;
        currentTimeView.setLayoutParams(params);
    }

    private String formatTime(long value) {
        int hourValue = (int) (value / (1000 * 60 * 60));
        int minValue = (int) ((value / (1000 * 60)) % 60);
        int secondValue = (int) ((value / 1000) % 60);
        StringBuilder sb = new StringBuilder();

        if (hourValue < 10) {
            sb.append("0");
        }
        sb.append(hourValue).append(":");

        if (minValue < 10) {
            sb.append("0");
        }
        sb.append(minValue).append(":");

        if (secondValue < 10) {
            sb.append("0");
        }
        sb.append(secondValue);
        return sb.toString();
    }

    private void hideAnim() {
        if (hideAnim != null && hideAnim.isRunning()) {
            return;
        }
        if (showAnim != null && showAnim.isRunning()) {
            showAnim.end();
        }
        hideAnim = ValueAnimator.ofFloat(0f, 1f);
        hideAnim.setDuration(ANIM_TIME);
        hideAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animValue = (float) animation.getAnimatedValue();
                int statusTop = (int) (statusBarHeight * animValue + statusBarTop);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) playerStatusBar.getLayoutParams();
                params.topMargin = statusTop;
                playerStatusBar.setLayoutParams(params);
            }
        });
        hideAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) playerStatusBar.getLayoutParams();
                params.topMargin = statusBarHeight + statusBarTop;
                playerStatusBar.setLayoutParams(params);
                playerStatusBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        hideAnim.start();
    }

    private void showAnim() {
        if (showAnim != null && showAnim.isRunning()) {
            return;
        }
        if (hideAnim != null && hideAnim.isRunning()) {
            hideAnim.end();
        }
        showAnim = ValueAnimator.ofFloat(1f, 0f);
        showAnim.setDuration(ANIM_TIME);
        showAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animValue = (float) animation.getAnimatedValue();
                int statusTop = (int) (statusBarHeight * animValue + statusBarTop);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) playerStatusBar.getLayoutParams();
                params.topMargin = statusTop;
                playerStatusBar.setLayoutParams(params);
            }
        });
        showAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mCtrlView.getVisibility() != View.VISIBLE) {
                    mCtrlView.setVisibility(View.VISIBLE);
                }
                playerStatusBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) playerStatusBar.getLayoutParams();
                params.topMargin = statusBarTop;
                playerStatusBar.setLayoutParams(params);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        showAnim.start();
    }

    private long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    private void computeNetworkSpeed() {
        if (mSpeed == null) {
            return;
        }
        long nowTotalRxBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long time = Math.max((nowTimeStamp - lastTimeStamp), 1000);
        long speed = (nowTotalRxBytes - lastTotalRxBytes) * 1000 / time;//毫秒转换
        String value = String.valueOf(speed) + " KB/s";
        lastTotalRxBytes = nowTotalRxBytes;
        lastTimeStamp = nowTimeStamp;
        mSpeed.setText(value);
    }

    //-------------------------
    private static final int UPDATE_PROGRESS_MSG = 0;
    private static final int HIDE_PROGRESS_MSG = 1;
    private static final int HIDE_VOLUME_MSG = 2;
    /**
     * 测试网速
     */
    public static final int COMPUTE_NETWORK_SPEED = 3;
    private Handler ctrlHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
//            APPLog.printSimpleInfo("what:" + what);
            if (what == UPDATE_PROGRESS_MSG) {
                updateProgress();
                sendEmptyMessageDelayed(UPDATE_PROGRESS_MSG, 1000);
            } else if (what == HIDE_PROGRESS_MSG) {
                hideAnim();
            } else if (what == HIDE_VOLUME_MSG) {
                if (playerVolumeView != null && playerVolumeView.getVisibility() == View.VISIBLE) {
                    playerVolumeView.setVisibility(View.INVISIBLE);
                }
            } else if (what == COMPUTE_NETWORK_SPEED) {
                computeNetworkSpeed();
                sendEmptyMessageDelayed(COMPUTE_NETWORK_SPEED, 1000);
            }
        }
    };
}
