package com.utstar.baseplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;

import com.utstar.baseplayer.view.MediaplayerFragment;

/**
 * Created by Richard
 */
public class TestPlayerActivity extends FragmentActivity {

    private MediaplayerFragment mMediaplayerFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        mMediaplayerFragment = new MediaplayerFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.player_window, mMediaplayerFragment).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_0) {
            setDisplay(null);
        } else if (keyCode == KeyEvent.KEYCODE_1) {
            setDataSource(null);
        } else if (keyCode == KeyEvent.KEYCODE_2) {
            setDataSourceList(null);
        } else if (keyCode == KeyEvent.KEYCODE_3) {
            start(null);
        } else if (keyCode == KeyEvent.KEYCODE_4) {
            play(null);
        } else if (keyCode == KeyEvent.KEYCODE_5) {
            pause(null);
        } else if (keyCode == KeyEvent.KEYCODE_6) {
            seekTo(null);
        } else if (keyCode == KeyEvent.KEYCODE_7) {
            release(null);
        } else if (keyCode == KeyEvent.KEYCODE_8) {
            fullScreen(null);
        } else if (keyCode == KeyEvent.KEYCODE_9) {
            exitFullScreen(null);
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            openDefaultPlayer(null);
        }
        return mMediaplayerFragment.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mMediaplayerFragment.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event);
    }

    public void openDefaultPlayer(View view) {
        mMediaplayerFragment.openDefaultMediaplayer("http://data.video.ptqy.gitv.tv/videos/other/20160725/fe/a1/3f7dbf84c23fa3424399a040cd7688cf.f4v?v=845642301,http://data.video.ptqy.gitv.tv/videos/other/20160725/fe/a1/3f7dbf84c23fa3424399a040cd7688cf.f4v?v=845642301,http://data.video.ptqy.gitv.tv/videos/other/20160725/fe/a1/3f7dbf84c23fa3424399a040cd7688cf.f4v?v=845642301,http://data.video.ptqy.gitv.tv/videos/other/20160725/fe/a1/3f7dbf84c23fa3424399a040cd7688cf.f4v?v=845642301");
    }

    public void setDisplay(View view) {
        mMediaplayerFragment.setDisplay(100, 150, 500, 500);
    }

    public void setDataSourceList(View view) {
        mMediaplayerFragment.setDataSourceList("http://data.video.ptqy.gitv.tv/videos/other/20160725/fe/a1/3f7dbf84c23fa3424399a040cd7688cf.f4v?v=845642301,http://data.video.ptqy.gitv.tv/videos/other/20160725/fe/a1/3f7dbf84c23fa3424399a040cd7688cf.f4v?v=845642301,http://data.video.ptqy.gitv.tv/videos/other/20160725/fe/a1/3f7dbf84c23fa3424399a040cd7688cf.f4v?v=845642301,http://data.video.ptqy.gitv.tv/videos/other/20160725/fe/a1/3f7dbf84c23fa3424399a040cd7688cf.f4v?v=845642301");
    }

    public void setDataSource(View view) {
        mMediaplayerFragment.setDataSource("http://111.161.5.36/h5/Public/Home/video/video_20160212.mp4");
    }

    public void start(View view) {
        mMediaplayerFragment.start();
    }

    public void play(View view) {
        mMediaplayerFragment.play();
    }

    public void pause(View view) {
        mMediaplayerFragment.pause();
    }

    public void getDuration(View view) {
        mMediaplayerFragment.getDuration();
    }

    public void getCurrentPosition(View view) {
        mMediaplayerFragment.getCurrentPosition();
    }

    public void seekTo(View view) {
        mMediaplayerFragment.seekTo(2 * 1000);
    }

    public void release(View view) {
        mMediaplayerFragment.release();
    }

    public void fullScreen(View view) {
        mMediaplayerFragment.fullScreen();
    }

    public void exitFullScreen(View view) {
        mMediaplayerFragment.exitFullScreen();
    }
}
