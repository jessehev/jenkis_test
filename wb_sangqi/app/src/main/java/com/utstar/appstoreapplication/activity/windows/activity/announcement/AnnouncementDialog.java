package com.utstar.appstoreapplication.activity.windows.activity.announcement;

import android.content.Context;
import android.text.Html;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import com.arialyy.frame.core.AbsDialog;
import com.utstar.appstoreapplication.activity.R;
import com.utstar.appstoreapplication.activity.entity.net_entity.AnnouncementEntity;

/**
 * Created by JesseHev on 2017/1/9.
 * 公告对话框
 */

public class AnnouncementDialog extends AbsDialog {

  @Bind(R.id.announce_title) TextView mTitle;
  @Bind(R.id.announce_content) TextView mContent;
  @Bind(R.id.team) TextView mTeam;
  @Bind(R.id.time) TextView mTime;
  @Bind(R.id.close_btn) Button mCloseBtn;

  private AnnouncementEntity mEntity;

  public AnnouncementDialog(Context context, AnnouncementEntity entity) {
    super(context, R.style.blur_dialog);
    this.mEntity = entity;
    initWindow();
    showAnnounce();
  }

  private void showAnnounce() {

    mTitle.setText(mEntity.name);
    mContent.setText(Html.fromHtml(convert(mEntity.content)));
    mCloseBtn.setOnClickListener(v -> dismiss());
    mTeam.setText(mEntity.team);
    mTime.setText(mEntity.date);
  }

  private String convert(String span) {
    String color = span.replaceAll("style=\"color:", "color=\"");
    color = color.replace(";", "");
    color = color.replace("span", "font");
    return color;
  }

  @Override protected int setLayoutId() {
    return R.layout.dialog_announcement;
  }

  @Override protected void dataCallback(int result, Object obj) {

  }

  public void initWindow() {
    mCloseBtn.requestFocus();
    if (getWindow() != null) {
      WindowManager.LayoutParams params = getWindow().getAttributes();
      params.width =
          (int) (getWindow().getWindowManager().getDefaultDisplay().getWidth() - getDimen(
              R.dimen.dimen_320dp));
      params.height =
          (int) (getWindow().getWindowManager().getDefaultDisplay().getHeight() - getDimen(
              R.dimen.dimen_160dp));

      getWindow().setBackgroundDrawable(
          getContext().getResources().getDrawable(R.drawable.shape_ad_dialog_bg));
    }
  }

  private float getDimen(int dimenId) {
    return getContext().getResources().getDimension(dimenId);
  }
}
