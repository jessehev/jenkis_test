//package com.utstar.baseplayer.activity;
//
//import android.content.ComponentName;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.PixelFormat;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.View;
//import android.webkit.JavascriptInterface;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.FrameLayout;
//import com.utstar.baseplayer.R;
//import com.utstar.baseplayer.utils.APPLog;
//import com.utstar.baseplayer.utils.PlayUrlProxy;
//import com.utstar.baseplayer.utils.ToastUtil;
//import com.utstar.baseplayer.view.LoadingView;
//import com.utstar.baseplayer.view.MediaplayerFragment;
//import com.utstar.baseplayer.view.ObservableWebView;
//import java.net.Inet4Address;
//import java.net.InetAddress;
//import java.net.NetworkInterface;
//import java.net.SocketException;
//import java.util.Enumeration;
//
//public class WebViewsActivity_back_1 extends FragmentActivity {
//  private ObservableWebView webView;
//  private LoadingView loaddingView;
//  private MediaplayerFragment mMediaplayerFragment; //视频窗口的fragment
//  private long lastBackDown;
//  private boolean mIsDefualtPlayer;
//  private boolean mIsFullScreen;
//  private AndroidMediaplayer mAndroidMediaplayer;
//  private FrameLayout mBrowserWindows;
//
//  @Override
//  protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_web_views);
//    mBrowserWindows = (FrameLayout) findViewById(R.id.rl_all);
//    setViews();
//    setSettings();
//    setWebViewClient();
//    setJsInterface();
//    setUrl();
//    getWindow().setFormat(PixelFormat.TRANSLUCENT);
//    setScrollListener();
//  }
//
//  /**
//   * 监听webview的滑动
//   */
//  private void setScrollListener() {
//    webView.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {
//      public void onScroll(int dx, int dy) {
//        if (dy == 0) {//显示出框
//          getSupportFragmentManager().beginTransaction().show(mMediaplayerFragment).commit();
//        } else {
//          getSupportFragmentManager().beginTransaction().hide(mMediaplayerFragment).commit();
//        }
//      }
//    });
//  }
//
//
//  @Override
//  public boolean dispatchKeyEvent(KeyEvent event) {
//    int keyCode = event.getKeyCode();
//    int action = event.getAction();
//    if (mIsDefualtPlayer || mIsFullScreen) {
//      if (action == KeyEvent.ACTION_DOWN) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//          if (isExit()) {
//            webView.loadUrl("javascript:onExit()");
//            APPLog.printInfo("webView.canGoBack():" + webView.canGoBack());
//            return webView.canGoBack() || super.dispatchKeyEvent(event);
//          }
//          return true;
//        }
//        return mMediaplayerFragment.onKeyDown(keyCode, event) || super.dispatchKeyEvent(event);
//      } else if (action == KeyEvent.ACTION_UP) {
//        return mMediaplayerFragment.onKeyUp(keyCode, event) || super.dispatchKeyEvent(event);
//      }
//    }
//    return super.dispatchKeyEvent(event);
//  }
//  //
//  //    @Override
//  //    public boolean onKeyDown(int keyCode, KeyEvent event) {
//  //        if (keyCode == KeyEvent.KEYCODE_BACK) {
//  //            return isExit();
//  //        }
//  //        return mMediaplayerFragment.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
//  //    }
//  //
//  //    @Override
//  //    public boolean onKeyUp(int keyCode, KeyEvent event) {
//  //        return mMediaplayerFragment.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event);
//  //    }
//
//  /**
//   * 设置此时的网络地址
//   */
//  private void setUrl() {
//    String url = getIntent().getStringExtra("url");
//    if (TextUtils.isEmpty(url)) {
//      loaddingView.notifyDataChanged(LoadingView.State.error);
//      return;
//    }
//    webView.loadUrl(url);
//    //        webView.loadUrl("http://192.168.4.182:8400/esUT/Default.aspx");
//    //        webView.loadUrl("file:///android_asset/player.html");
//    //webView.loadUrl("http://chances.vicp.net:58090/epg/hdvod/biz_31142953.epg?user_id=${USERID}&stb_type=${STBTYPE}&sign=${USERTOKEN}&return=${BACKURL}&domain=${DOMAIN}");
//    //webView.loadUrl("http://chances.vicp.net:58090/epg/hdvod/biz_31142953.epg?user_id=${USERID}&stb_type=${STBTYPE}&sign=${USERTOKEN}&return=${BACKURL}&domain=${DOMAIN}");
//  }
//
//  /**
//   * 注册JS调用Nativie的接口
//   */
//  private void setJsInterface() {
//    AndroidContext androidContext = new AndroidContext();
//    AndroidBrowser androidBrowser = new AndroidBrowser();
//    mAndroidMediaplayer = new AndroidMediaplayer();
//    webView.addJavascriptInterface(androidContext, "androidContext");
//    webView.addJavascriptInterface(androidBrowser, "androidBrowser");
//    webView.addJavascriptInterface(mAndroidMediaplayer, "androidMediaplayer");
//  }
//
//
//  private void setViews() {
//    webView = (ObservableWebView) findViewById(R.id.web_view);
//    webView.setVisibility(View.INVISIBLE);
//    loaddingView = (LoadingView) findViewById(R.id.loading_view);
//    loaddingView.notifyDataChanged(LoadingView.State.ing);
//
//    //视频窗口的初始化
//    MediaPlayerEvent mediaPlayerEvent = new MediaPlayerEvent();
//    mMediaplayerFragment = new MediaplayerFragment();
//    mMediaplayerFragment.setOnInfoListener(mediaPlayerEvent);
//    mMediaplayerFragment.setOnCompletionListener(mediaPlayerEvent);
//    mMediaplayerFragment.setOnErrorListener(mediaPlayerEvent);
//    mIsFullScreen = true;
//    mIsDefualtPlayer = true;
//  }
//
//
//  /**
//   * 开启webView的JS调用
//   */
//  private void setSettings() {
//    //WebSettings对象封装了webView
//    //的基本设置
//    WebSettings s = webView.getSettings();
//    //设置webView启用Javascript
//    s.setJavaScriptEnabled(true);
//    s.setJavaScriptCanOpenWindowsAutomatically(true);
//    s.setUseWideViewPort(true);
//    s.setLoadWithOverviewMode(true);
//    s.setGeolocationEnabled(true);
//    s.setDomStorageEnabled(true);
//    s.setLoadsImagesAutomatically(true);
//    s.setDefaultTextEncodingName("utf-8");
//  }
//
//  /**
//   * 使其不跳转其它浏览器
//   */
//  private void setWebViewClient() {
//    webView.setWebViewClient(new WebViewClient() {
//      @Override
//      public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        view.loadUrl(url);
//        return true;
//      }
//      //重写第二个方法，也是很常用的，当页加加载完成后，再调用页面的方法，若在其它地方调用，很有可能造成线程不同步。
//
//      @Override
//      public void onPageFinished(WebView view, String url) {
//        // TODO Auto-generated method stub
//        super.onPageFinished(view, url);
//        //在这里写具体调用JS的哪个方法
//        loaddingView.notifyDataChanged(LoadingView.State.done);
//        webView.setVisibility(View.VISIBLE);
//      }
//    });
//  }
//
//
//  @Override
//  protected void onStart() {
//    super.onStart();
//  }
//
//  @Override
//  protected void onStop() {
//    if (mMediaplayerFragment != null) {
//      mMediaplayerFragment.release();
//    }
//    super.onStop();
//  }
//
//  @Override
//  protected void onDestroy() {
//    super.onDestroy();
//    try {
//      mBrowserWindows.removeView(webView);
//      webView.destroy();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }
//
//  @Override
//  protected void onPause() {
//    super.onPause();
//  }
//
//  @Override
//  protected void onResume() {
//    super.onResume();
//  }
//
//
//  private boolean isExit() {
//    long currentTime = System.currentTimeMillis();
//    if (currentTime - lastBackDown < 2000) {
//      if (mIsDefualtPlayer || mIsFullScreen) {
//        ToastUtil.getInstace().hideToast();
//        mAndroidMediaplayer.exitFullScreen();
//        return true;
//      }
//    } else {
//      lastBackDown = currentTime;
//      APPLog.printInfo("lastBackDown:" + lastBackDown);
//      if (mIsFullScreen) {
//        ToastUtil.getInstace().showToastByString(this, "再次按返回退出全屏播放", 20);
//        return false;
//      } else if (mIsDefualtPlayer) {
//        ToastUtil.getInstace().showToastByString(this, "再次按返回退出播放", 20);
//        return false;
//      }
//    }
//    return false;
//  }
//
//  /**
//   * 申明JS调用的Native的接口
//   * androidContext
//   */
//  class AndroidContext {
//    @JavascriptInterface
//    public void startActivity(String pkgName, String activityName, String actions, String categorys, String datas, String params) {  //打开第三方APK
//      APPLog.printInfo("____________________");
//      APPLog.printInfo("startActivity pkgName:" + pkgName);
//      APPLog.printInfo("startActivity activityName:" + activityName);
//      APPLog.printInfo("startActivity actions:" + actions);
//      APPLog.printInfo("startActivity categorys:" + categorys);
//      APPLog.printInfo("startActivity datas:" + datas);
//      APPLog.printInfo("startActivity params:" + params);
//      APPLog.printInfo("____________________");
//      Intent intent = new Intent();
//      ComponentName componentName = new ComponentName(pkgName, activityName);
//      intent.setComponent(componentName);
//      if (TextUtils.isEmpty(actions)) {
//        intent.setAction("android.intent.action.MAIN");
//      } else {
//        if (!actions.contains(",")) {
//          intent.setAction(actions);
//        } else {
//          String[] split = actions.split(",");
//          for (String aSplit : split) {
//            intent.setAction(aSplit);
//          }
//        }
//      }
//      if (!TextUtils.isEmpty(params)) intent.putExtra("params", params);
//      if (!TextUtils.isEmpty(categorys)) {
//        if (!categorys.contains(",")) {
//          intent.addCategory(categorys);
//        } else {
//          String[] split = categorys.split(",");
//          for (String aSplit : split) {
//            intent.addCategory(aSplit);
//          }
//        }
//      }
//      if (!TextUtils.isEmpty(datas)) {
//        if (!datas.contains(",")) {
//          intent.setData(Uri.parse(datas));
//        } else {
//          String[] split = datas.split(",");
//          for (String aSplit : split) {
//            intent.setData(Uri.parse(aSplit));
//          }
//        }
//      }
//      WebViewsActivity_back_1.this.startActivity(intent);
//    }
//
//    @JavascriptInterface
//    public String getValue(String key) { //获得共享信息
//      APPLog.printInfo("getValue key:" + key);
//      if ("STBMAC".equals(key)) {
//        String[] ipAndMac = getIpAndMac("eth0");
//        return ipAndMac[1];
//      } else if ("STBIP".equals(key)) {
//        String[] ipAndMac = getIpAndMac("eth0");
//        return ipAndMac[0];
//      } else if ("STBSN".equals(key)) {
//        return Build.SERIAL;
//      }
//      Uri uri = Uri.parse("content://com.utstar.appstoreapplication.common.db.UserIDProvider");
//      Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//      if (cursor != null && cursor.moveToFirst()) {
//        int index = cursor.getColumnIndex(key);
//        if (index != -1) {
//          String value = cursor.getString(index);
//          cursor.close();
//          return value;
//        } else {
//          cursor.close();
//          return null;
//        }
//      } else {
//        return null;
//      }
//    }
//
//    /**
//     * 返回IP与MAC
//     *
//     * @param networkName 网卡名
//     * @return 返回WIFI的IP与MAC
//     * @throws SocketException
//     */
//    public String[] getIpAndMac(String networkName) {
//      try {
//        NetworkInterface networkInterface = NetworkInterface.getByName(networkName);
//        if (networkInterface != null) {
//          return getIpAndMac(networkInterface);
//        }
//      } catch (SocketException e) {
//        e.printStackTrace();
//      }
//      return null;
//    }
//
//    /**
//     * 获取指定网卡的IP与MAC
//     *
//     * @param networkInterface
//     * @return
//     * @throws SocketException
//     */
//    public String[] getIpAndMac(NetworkInterface networkInterface) throws SocketException {
//      String ip = getIp(networkInterface);
//      String mac = getMac(networkInterface);
//      String[] result = new String[2];
//      result[0] = ip;
//      result[1] = mac;
//      return result;
//    }
//
//    private String getIp(NetworkInterface networkInterface) {
//      Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
//      while (addresses.hasMoreElements()) {
//        InetAddress address = addresses.nextElement();
//        if (address instanceof Inet4Address) {
//          return address.getHostAddress();
//        }
//      }
//      return null;
//    }
//
//    private String getMac(NetworkInterface networkInterface) throws SocketException {
//      byte[] macByte = networkInterface.getHardwareAddress();
//      if (macByte == null) {
//        return null;
//      }
//      StringBuilder sb = new StringBuilder();
//      int len = macByte.length;
//      for (int i = 0; i < len; i++) {
//        int value = macByte[i] & 0xff;
//        String hexValue = Integer.toHexString(value);
//        if (value < 16) {
//          sb.append("0");
//        }
//        sb.append(hexValue);
//        if (i < (len - 1)) {
//          sb.append(":");
//        }
//      }
//      return sb.toString();
//    }
//  }
//
//
//  /**
//   * 申明JS调用的Native的接口
//   * AndroidBrowser
//   */
//  class AndroidBrowser {
//
//    @JavascriptInterface
//    public void goBack() { //回到webview上一页，若无，退出
//      APPLog.printInfo("goBack");
//      runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          if (mMediaplayerFragment != null) {
//            mMediaplayerFragment.release();
//          }
//          if (webView.canGoBack()) {
//            webView.goBack();
//          } else {
//            WebViewsActivity_back_1.this.finish();
//          }
//        }
//      });
//
//    }
//
//    @JavascriptInterface
//    public void finish() { //退出页面
//      APPLog.printInfo("finish");
//      if (mMediaplayerFragment != null) {
//        mMediaplayerFragment.release();
//      }
//      WebViewsActivity_back_1.this.finish();
//    }
//  }
//
//  /**
//   * 申明JS调用的Native的接口
//   * AndroidMediaplayer
//   */
//  class AndroidMediaplayer {
//    private int result = 0;
//    private boolean mIsNeedWait;
//
//    public synchronized void waitResult() {
//      APPLog.printInfo("waitResult");
//      try {
//        if (mIsNeedWait) {
//          wait();
//        }
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//    }
//
//    public synchronized void notifyResult() {
//      APPLog.printInfo("notifyResult");
//      try {
//        notifyAll();
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
//
//    private void init() {
//      if (!mMediaplayerFragment.isAdded()) {
//        APPLog.printInfo("Added");
//        getSupportFragmentManager().beginTransaction().add(R.id.player_window, mMediaplayerFragment).commit();
//      }
//    }
//
//    @JavascriptInterface
//    public int openDefaultPlayer(final String playList) {
//      init();
//      APPLog.printInfo("setDataSourceList\n" +
//          "----------\n" +
//          "playList->" + playList + "\n");
//      mIsDefualtPlayer = true;
//      mIsNeedWait = true;
//      runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          result = mMediaplayerFragment.openDefaultMediaplayer(playList);
//          mIsNeedWait = false;
//          notifyResult();
//        }
//      });
//      waitResult();
//      APPLog.printInfo("result:" + result);
//      return result;
//    }
//
//    @JavascriptInterface
//    public int setBreakpoint(int position) {
//      init();
//      APPLog.printInfo("setBreakpoint");
//      mMediaplayerFragment.setBreakpoint(position);
//      return 0;
//    }
//
//    @JavascriptInterface
//    public int setDisplay(final int left, final int top, final int width, final int height) { //设置小窗口长宽
//      APPLog.printInfo("setDisplay:left->" + left + "/top->" + top + "/width->" + width + "/height->" + height);
//      mIsDefualtPlayer = false;
//      mIsFullScreen = width == FrameLayout.LayoutParams.MATCH_PARENT && height == FrameLayout.LayoutParams.MATCH_PARENT;
//      APPLog.printInfo("setDisplay");
//      mIsNeedWait = true;
//      init();
//      runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          result = mMediaplayerFragment.setDisplay(left, top, width, height);
//          mIsNeedWait = false;
//          notifyResult();
//        }
//      });
//      waitResult();
//      APPLog.printInfo("result:" + result);
//      return result;
//    }
//
//    @JavascriptInterface
//    public int setDataSource(String url) { //设置播放地址
//      APPLog.printInfo("setDataSource url:" + url);
//      if (url.contains("/EPG/jsp/gdgaoqing/en/vaitf/getVodPlayUrl.jsp")) {
//        String session = getSession();
//        if (TextUtils.isEmpty(session)) {
//          return -1;
//        }
//        if (!session.startsWith("JSESSIONID=")) {
//          session = "JSESSIONID=" + session;
//        }
//        try {
//          //ToastUtil.getInstace().showToastByString(WebViewsActivity.this, "start", 20);
//          url = PlayUrlProxy.getPlayUrl(url, session);
//          toast("start");
//        } catch (Exception e) {
//          //ToastUtil.getInstace().showToastByString(WebViewsActivity.this, "exception:" + e.getMessage(), 20);
//          toast("exception:" + e.getMessage());
//          return -1;
//        }
//        //ToastUtil.getInstace().showToastByString(WebViewsActivity.this, "url:" + url, 20);
//        toast("url:" + url);
//        if (TextUtils.isEmpty(url)) {
//          return -1;
//        }
//      }
//      final String playUrl = url;
//      mIsNeedWait = true;
//      init();
//      runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          result = mMediaplayerFragment.setDataSource(playUrl);
//          mIsNeedWait = false;
//          notifyResult();
//          APPLog.printInfo("notifyResult:" + result);
//        }
//      });
//      waitResult();
//      APPLog.printInfo("result:" + result);
//      return result;
//    }
//
//    private void toast(final String msg){
//      runOnUiThread(new Runnable() {
//        @Override public void run() {
//          ToastUtil.getInstace().showToastByString(WebViewsActivity_back_1.this, msg, 20);
//        }
//      });
//    }
//
//    private String getSession() {
//      Uri uri = Uri.parse("content://com.utstar.appstoreapplication.common.db.UserIDProvider");
//      Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//      if (cursor != null && cursor.moveToFirst()) {
//        int index = cursor.getColumnIndex("epgToken");
//        if (index != -1) {
//          String value = cursor.getString(index);
//          cursor.close();
//          return value;
//        } else {
//          cursor.close();
//          return null;
//        }
//      } else {
//        return null;
//      }
//    }
//
//    @JavascriptInterface
//    public int setDataSourceList(final String urlList) { //设置播放地址集合
//      APPLog.printInfo("setDataSourceList\n" +
//          "----------\n" +
//          "playList->" + urlList + "\n");
//      mIsNeedWait = true;
//      init();
//      runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          result = mMediaplayerFragment.setDataSourceList(urlList);
//          mIsNeedWait = false;
//          notifyResult();
//        }
//      });
//      waitResult();
//      APPLog.printInfo("result:" + result);
//      return result;
//    }
//
//    @JavascriptInterface
//    public int start() { //开始播放
//      init();
//      APPLog.printInfo("start");
//      mIsNeedWait = true;
//      runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          result = mMediaplayerFragment.start();
//          mIsNeedWait = false;
//          notifyResult();
//        }
//      });
//      waitResult();
//      APPLog.printInfo("result:" + result);
//      return result;
//    }
//
//    @JavascriptInterface
//    public int play() { //开始播放，这里指暂停后开始播放
//      mIsNeedWait = true;
//      init();
//      runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          result = mMediaplayerFragment.play();
//          mIsNeedWait = false;
//          notifyResult();
//        }
//      });
//      waitResult();
//      APPLog.printInfo("result:" + result);
//      return result;
//    }
//
//    @JavascriptInterface
//    public int pause() { //暂停播放
//      mIsNeedWait = true;
//      init();
//      runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          result = mMediaplayerFragment.pause();
//          mIsNeedWait = false;
//          notifyResult();
//        }
//      });
//      waitResult();
//      APPLog.printInfo("result:" + result);
//      return result;
//    }
//
//    @JavascriptInterface
//    public int getDuration() { //获得总时长
//      init();
//      return mMediaplayerFragment.getDuration();
//    }
//
//    @JavascriptInterface
//    public int getCurrentPosition() { //获得当前时长
//      init();
//      return mMediaplayerFragment.getCurrentPosition();
//    }
//
//    @JavascriptInterface
//    public int seekTo(final int position) { //快进/退
//      mIsNeedWait = true;
//      init();
//      runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          result = mMediaplayerFragment.seekTo(position);
//          mIsNeedWait = false;
//          notifyResult();
//        }
//      });
//      waitResult();
//      APPLog.printInfo("result:" + result);
//      return result;
//    }
//
//    @JavascriptInterface
//    public int release() { //终止播放
//      init();
//      APPLog.printInfo("release");
//      mIsNeedWait = true;
//      runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          result = mMediaplayerFragment.release();
//          mIsNeedWait = false;
//          notifyResult();
//        }
//      });
//      waitResult();
//      APPLog.printInfo("result:" + result);
//      return result;
//    }
//
//    @JavascriptInterface
//    public int fullScreen() { //全屏播放
//      APPLog.printInfo("fullScreen");
//      mIsFullScreen = true;
//      mIsNeedWait = true;
//      init();
//      runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          result = mMediaplayerFragment.fullScreen();
//          mIsNeedWait = false;
//          notifyResult();
//        }
//      });
//      waitResult();
//      APPLog.printInfo("result:" + result);
//      return result;
//    }
//
//    @JavascriptInterface
//    public int exitFullScreen() { //退出全屏播放
//      APPLog.printInfo("exitFullScreen");
//      init();
//      if (mIsDefualtPlayer) {
//        try {
//          mMediaplayerFragment.release();
//          getSupportFragmentManager().beginTransaction().remove(mMediaplayerFragment).commit();
//          mIsDefualtPlayer = false;
//          return 0;
//        } catch (Exception e) {
//          APPLog.printError(e);
//          return -1;
//        }
//      }
//      mIsFullScreen = false;
//      mIsNeedWait = true;
//      runOnUiThread(new Runnable() {
//        @Override
//        public void run() {
//          result = mMediaplayerFragment.exitFullScreen();
//          mIsNeedWait = false;
//          notifyResult();
//        }
//      });
//      waitResult();
//      APPLog.printInfo("result:" + result);
//      return result;
//    }
//  }
//
//  class MediaPlayerEvent implements MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//      APPLog.printInfo("onCompletion");
//      if (webView != null) {
//        webView.loadUrl("javascript:onCompleted()");
//      }
//    }
//
//    @Override
//    public boolean onError(MediaPlayer mp, int what, int extra) {
//      APPLog.printInfo("onError");
//      if (webView != null) {
//        webView.loadUrl("javascript:onError(" + what + ",ignore)");
//      }
//      return false;
//    }
//
//    //---------------
//    @Override
//    public boolean onInfo(MediaPlayer mp, int what, int extra) {
//      switch (what) {
//        case MediaPlayer.MEDIA_INFO_UNKNOWN:
//          break;
//        case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
//          break;
//        case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
//          break;
//        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//          break;
//        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//          break;
//        default:
//          break;
//      }
//      return false;
//    }
//
//    //----------------
//    @Override
//    public void onPrepared(MediaPlayer mp) {
//    }
//  }
//}
//
//
