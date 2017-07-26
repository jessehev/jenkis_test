package com.utstar.appstoreapplication.activity.Probe;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.L;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.utstar.appstoreapplication.activity.commons.net.BasicDeserializer;
import com.utstar.appstoreapplication.activity.commons.net.HttpCallback;
import com.utstar.appstoreapplication.activity.entity.net_entity.ProcessEntity;
import com.utstar.appstoreapplication.activity.interfaces.net_interface.ProbeApi;
import com.utstar.appstoreapplication.activity.manager.NetManager;
import com.utstar.appstoreapplication.activity.tinker.BaseApp;
import com.utstar.appstoreapplication.activity.utils.EpgUserUtil;
import com.utstar.appstoreapplication.activity.windows.game_detail.GameDetailActivity;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 探针
 * Created by Aria.Lao on 2017/1/22.
 */
final class ProbeManager {
  private static final String TAG = "ProbeManager";
  private static final String UPLOAD_STOP_TIME_URI = "log/logout.do";
  private static final String GET_USER_ID_URI = "mine/login.do";
  private static final String GET_EVENT_ID_URI = "log/login.do";

  private Map<String, ProcessEntity> processMap = null;
  private Timer timer = null;
  private TimerTask taskWork = null;
  private volatile static ProbeManager instance = null;
  private String mCurrentPackageName;

  private List<String> systemProcessList = null;
  final Gson mGson = new GsonBuilder().registerTypeAdapter(new TypeToken<ProcessEntity>() {
  }.getType(), new BasicDeserializer<ProcessEntity>()).create();

  private Handler mStopUploadHandler = new Handler() {
    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (msg.what == 0) {
        uploadGameStopTime(msg.obj.toString());
      } else if (msg.what == 1) {
        updateGameTime(msg.obj.toString());
      }
    }
  };

  public synchronized static ProbeManager getInstance() {
    if (instance == null) {
      instance = new ProbeManager();
      L.d("probe manager inited...");
    }
    return instance;
  }

  private ProbeManager() {
      processMap = new ConcurrentHashMap<>();
      initTimer();
  }

  private void initTimer() {
    timer = new Timer();
    taskWork = new TimerTask() {
      @Override public void run() {
        try {
          process();
        } catch (Exception e) {
          L.e(FL.getExceptionString(e));
        }
      }
    };

    timer.schedule(taskWork, 1000, 3000);
  }

  void destroy() {
    taskWork.cancel();
    timer.cancel();
    this.processMap.clear();
    L.d("probe manager destroy...");
  }

  private void process() {
    systemProcessList = getSystemProcessList();
    Set<String> packageNameList = processMap.keySet();
    boolean isOk = false;
    for (String packageName : packageNameList) {
      isOk = false;
      for (String str : systemProcessList) {
        if (str.equals(packageName)) {
          isOk = true;
          break;
        }
      }

      if (!isOk) {
        L.d("game process :" + packageName + " dead...");
        mStopUploadHandler.obtainMessage(0, packageName).sendToTarget();
      } else {
        mStopUploadHandler.obtainMessage(1, packageName).sendToTarget();
        L.d(TAG, "game process :" + packageName + " update game time...");
      }
    }
    L.d(TAG, "probe loop process flished... ");
  }

  private void updateGameTime(String packageName) {
    ProcessEntity process = this.processMap.get(packageName);
    if (process == null) return;
    this.upLoadGameTime(process);
    L.d(TAG, "upload game time...update ! gameName:"
        + process.getPackageName()
        + " userId:"
        + process.getUserId());
  }

  private void uploadGameStopTime(String packageName) {
    ProcessEntity deadProcess = this.processMap.get(packageName);
    if (deadProcess == null) return;
    this.upLoadStopTime(deadProcess);
    L.d("upload game over time... gameName:"
        + deadProcess.getPackageName()
        + " userId:"
        + deadProcess.getUserId());
  }

  void startProbeListGame(String packName) {
    ProcessEntity en = new ProcessEntity();
    en.setPackageName(packName);
    firstGetUserId(en);
    L.d("game:" + packName + " add prebe listen service flished...");
  }

  private void onGetUserIdSuccess(ProcessEntity en) {
    L.d("get userid flished... gameName:" + en.getPackageName() + " userId;" + en.getUserId());
    upLoadStartTime(en);
    //timeDemo(en);
    //NetManager.getInstance()
    //    .request(ProbeApi.class, mGson)
    //    .getState(en.getPackageName())
    //    .compose(new HttpCallback<ProcessEntity>() {
    //      @Override public void onResponse(ProcessEntity response) {
    //        switch (response.getType()) {
    //          case 0:
    //            upLoadStartTime(en);
    //            break;
    //          case 1: //时长试玩
    //            timeDemo(response);
    //            upLoadStartTime(en);
    //            break;
    //          case 2: //次数试玩
    //            numDemo(response);
    //            upLoadStartTime(en);
    //            break;
    //          case 3: //不能玩
    //            break;
    //        }
    //      }
    //    });
  }

  private void numDemo(ProcessEntity entity) {
    // TODO: 2017/1/22 不做处理
  }

  private void timeDemo(ProcessEntity entity) {
    final Timer timer = new Timer();
    TimerTask taskWork = new TimerTask() {
      @Override public void run() {
        List<String> process = getSystemProcessList();
        if (process.contains(entity.getPackageName())) {
          ActivityManager am =
              (ActivityManager) BaseApp.context.getSystemService(Context.ACTIVITY_SERVICE);
          //1
          //am.killBackgroundProcesses(entity.getPackageName());

          //2
          //try {
          //  Method method = am.getClass().getMethod("forceStopPackage", String.class);
          //  method.invoke(am, entity.getPackageName());
          //} catch (Exception e) {
          //  e.printStackTrace();
          //}

          //3
          //List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
          //for (ActivityManager.RunningAppProcessInfo info : infos) {
          //  if (info.processName.contains(entity.getPackageName())) {
          //    android.os.Process.sendSignal(info.pid, android.os.Process.SIGNAL_KILL);
          //    android.os.Process.killProcess(info.pid);
          //    L.e(TAG, "结束进程【" + entity.getPackageName() + "】");
          //    break;
          //  }
          //}
          Intent intent = new Intent(BaseApp.context, GameDetailActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          BaseApp.context.startActivity(intent);
        }
      }
    };

    timer.schedule(taskWork, 1000 * 10);
  }

  private void onGetEventIdSuccess(ProcessEntity en) {
    this.processMap.put(en.getPackageName(), en);
    L.d("add prcessMap successed... gameName:"
        + en.getPackageName()
        + " eventId:"
        + en.getEventId()
        + " userId:"
        + en.getUserId());
    L.d("get game eventid flished... gameName:"
        + en.getPackageName()
        + " eventId:"
        + en.getEventId());
  }

  private void onUploadGameOverTimeSuccess(ProcessEntity en) {
    this.processMap.remove(en.getPackageName());
    L.d("upload game over timed... gameName:" + en.getPackageName() + " userId:" + en.getUserId());
  }

  private void onUploadGameTimeSuccess(ProcessEntity en) {
    L.d("upload game time update success... gameName:"
        + en.getPackageName()
        + " userId:"
        + en.getUserId());
  }

  private void firstGetUserId(final ProcessEntity en) {
    NetManager.getInstance()
        .request(ProbeApi.class, mGson)
        .getUserId(EpgUserUtil.getUserEntity().getWbAccount())
        .compose(new HttpCallback<ProcessEntity>() {
          @Override public void onResponse(ProcessEntity response) {
            if (response != null) {
              en.setUserId(response.getUserId());
              onGetUserIdSuccess(en);
            }
          }
        });
  }

  private void upLoadStartTime(ProcessEntity en) {
    String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    NetManager.getInstance()
        .request(ProbeApi.class, mGson)
        .getEventId(en.getPackageName(), en.getUserId(), startTime)
        .compose(new HttpCallback<ProcessEntity>() {
          @Override public void onResponse(ProcessEntity response) {
            en.setEventId(response == null ? "-1" : response.getEventId());
            onGetEventIdSuccess(en);
          }
        });
  }

  private void upLoadStopTime(ProcessEntity en) {
    String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    NetManager.getInstance()
        .request(ProbeApi.class, mGson)
        .stop(en.getEventId(), endTime)
        .compose(new HttpCallback<ProcessEntity>() {
          @Override public void onResponse(ProcessEntity response) {
            onUploadGameOverTimeSuccess(en);
          }
        });

    //String URL = BASE_URL + UPLOAD_STOP_TIME_URI;
    //RequestParams params = new RequestParams();
    //params.put("id", en.getEventid());
    //
    //params.put("endTime", endTime);
    //
    //final AsyncHttpClient client = new AsyncHttpClient();
    //client.post(URL, params, new AsyncHttpResponseHandler() {
    //  @Override public void onSuccess(int i, Header[] headers, byte[] bytes) {
    //    if (bytes != null) {
    //      onUploadGameOverTimeSuccess(en);
    //    }
    //  }
    //
    //  @Override public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
    //    if (bytes == null) {
    //      return;
    //    }
    //    L.e(new String(bytes));
    //  }
    //});
  }

  private void upLoadGameTime(ProcessEntity en) {
    String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    NetManager.getInstance()
        .request(ProbeApi.class, mGson)
        .stop(en.getEventId(), endTime)
        .compose(new HttpCallback<ProcessEntity>() {
          @Override public void onResponse(ProcessEntity response) {
            onUploadGameTimeSuccess(en);
          }
        });

    //String URL = BASE_URL + UPLOAD_STOP_TIME_URI;
    //RequestParams params = new RequestParams();
    //params.put("id", en.getEventid());
    //String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    //params.put("endTime", endTime);
    //
    //final AsyncHttpClient client = new AsyncHttpClient();
    //client.post(URL, params, new AsyncHttpResponseHandler() {
    //  @Override public void onSuccess(int i, Header[] headers, byte[] bytes) {
    //    if (bytes != null) {
    //      onUploadGameTimeSuccess(en);
    //    }
    //  }
    //
    //  @Override public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
    //    if (bytes == null) {
    //      return;
    //    }
    //    L.e(new String(bytes));
    //  }
    //});
  }

  private List<String> getSystemProcessList() {
    ActivityManager mActivityManager =
        (ActivityManager) BaseApp.context.getSystemService(Context.ACTIVITY_SERVICE);
    List<String> listCurrentProcess = new ArrayList<>();

    ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
    String packageName = topActivity.getPackageName();
    listCurrentProcess.add(packageName);

    //ActivityManager mActivityManager = (ActivityManager) GameStopApplication.getInstance()
    //    .getSystemService(Context.ACTIVITY_SERVICE);
    ////在Android 5.0系统以上，
    //// 调用getRunningAppProcesses() 方法来获取所有运行的进程是获取不到的，因为谷歌出于安全的考虑，所以在5.0系统以上把这个方法移除了。
    //List<ActivityManager.RunningAppProcessInfo> processList =
    //    mActivityManager.getRunningAppProcesses();
    //for (ActivityManager.RunningAppProcessInfo appProcess : processList) {
    //  String[] pkgNameList = appProcess.pkgList;
    //  Collections.addAll(listCurrentProcess, pkgNameList);
    //}
    return listCurrentProcess;
  }

  @SuppressWarnings("deprecation")
  private boolean isApplicationBroughtToBackground(final Context context) {
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
    if (!tasks.isEmpty()) {
      ComponentName topActivity = tasks.get(0).topActivity;
      if (topActivity.getPackageName().equals(context.getPackageName())) {
        return true;
      }
    }
    return false;
  }

  ////只适合用于5.0系统以上，以下同样也是获取不到的,6.0以上没有试过
  //private List<String> getSystemProcessListFive() {
  //  AppUtils proutils = new AppUtils(GameStopApplication.getInstance());
  //  List<AndroidAppProcess> listInfo = ProcessManager.getRunningAppProcesses();
  //  if (listInfo.isEmpty() || listInfo.size() == 0) {
  //    return null;
  //  }
  //  for (AndroidAppProcess info : listInfo) {
  //    ApplicationInfo app = proutils.getApplicationInfo(info.name);
  //    // 过滤系统的应用
  //    if ((app.flags & app.FLAG_SYSTEM) > 0) {
  //      continue;
  //    }
  //    systemProcessList.add(app.packageName);
  //  }
  //  return systemProcessList;
  //}
}









