package com.utstar.appstoreapplication.activity.commons.net;

import com.arialyy.frame.util.show.FL;
import com.arialyy.frame.util.show.L;
import com.utstar.appstoreapplication.activity.interfaces.common_interface.INetResponse;
import com.utstar.appstoreapplication.activity.manager.MemoryManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by “Aria.Lao” on 2016/10/26.
 * HTTP数据回调
 */
public abstract class HttpCallback<T> implements INetResponse<T>, Observable.Transformer<T, T> {

  @Override public void onFailure(Throwable e) {
    L.e("HttpCallback", FL.getExceptionString(e));
  }

  @Override public Observable<T> call(Observable<T> observable) {
    Observable<T> tObservable = observable.subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(t -> {

          onResponse(t);
          return t;
        })
        .onErrorReturn(throwable -> {
          onFailure(throwable);
          return null;
        });
    tObservable.subscribe();
    return tObservable;
  }
}
