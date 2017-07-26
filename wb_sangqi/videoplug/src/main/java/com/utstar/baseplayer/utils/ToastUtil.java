package com.utstar.baseplayer.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Richard
 */

public class ToastUtil {
    private static ToastUtil toastUtil = null;
    private Toast toast;

    private ToastUtil() {
    }

    public static ToastUtil getInstace() {
        if (toastUtil == null) {
            toastUtil = new ToastUtil();
        }
        return toastUtil;
    }

    public void showToastByString(Context context, String message, int left, int top, int width, int heigth, int padding, float textSize) {
        hideToast();
        FrameLayout.LayoutParams messageViewParams = new FrameLayout.LayoutParams(width, heigth);
        messageViewParams.leftMargin = left;
        messageViewParams.topMargin = top;
        TextView messageView = new TextView(context);
        messageView.setText(message);
        messageView.setTextColor(Color.WHITE);
        messageView.setBackgroundColor(Color.argb(0xb4, 0x00, 0x00, 0x00));
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        messageView.setLayoutParams(messageViewParams);
        messageView.setGravity(Gravity.CENTER);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(params);
        frameLayout.setBackgroundColor(Color.TRANSPARENT);
        frameLayout.addView(messageView);

        toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(frameLayout);
        toast.show();
    }

    public void showToastByString(Context context, String message, int textSize) {
        hideToast();
        RelativeLayout.LayoutParams messageViewParams = new RelativeLayout.LayoutParams(300, 100);
        TextView messageView = new TextView(context);
        messageView.setText(message);
        messageView.setTextColor(Color.WHITE);
        messageView.setBackgroundColor(Color.argb(0xb4, 0x00, 0x00, 0x00));
        messageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        messageView.setLayoutParams(messageViewParams);
        messageView.setGravity(Gravity.CENTER);

        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.bottomMargin = 100;
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setBackgroundColor(Color.TRANSPARENT);
        relativeLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        relativeLayout.addView(messageView);
        relativeLayout.setLayoutParams(params);

        toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 30);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(relativeLayout);
        toast.show();
    }

    public void hideToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

}
