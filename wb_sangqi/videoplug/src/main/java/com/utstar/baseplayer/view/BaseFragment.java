package com.utstar.baseplayer.view;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

/**
 * Created by Richard
 */
public class BaseFragment extends Fragment {
    private Handler mMainHandler;

    public void setMainHandler(Handler mainHandler) {
        mMainHandler = mainHandler;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }
}
