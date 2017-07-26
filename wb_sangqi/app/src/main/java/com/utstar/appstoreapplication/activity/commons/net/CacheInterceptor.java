package com.utstar.appstoreapplication.activity.commons.net;

import com.arialyy.frame.util.show.L;
import com.utstar.appstoreapplication.activity.entity.CacheEntity;
import com.utstar.appstoreapplication.activity.manager.MemoryManager;
import java.io.IOException;
import java.nio.charset.Charset;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by Aria.Lao on 2017/2/13.
 * 缓存拦截器
 */
public class CacheInterceptor implements Interceptor {
  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Response response = chain.proceed(request);
    ResponseBody responseBody = response.body();
    BufferedSource source = responseBody.source();
    Buffer buffer = source.buffer();
    Charset UTF8 = Charset.forName("UTF-8");
    Charset charset = UTF8;
    //buffer.writeString(MemoryManager.getInstance().getCache())
    //buffer.clone().readString(charset);
    CacheEntity entity = MemoryManager.getInstance().getCache(request.url().toString());
    if (entity != null) {
      buffer.writeString(entity.getData(), charset);
    }
    MemoryManager.getInstance().saveCache(request.url().toString(), buffer.readString(charset));

    //entity = MemoryManager.getInstance().getCache(request.url().toString());
    //
    //L.e("gg", "===========================");
    //
    //L.j(entity.getData());
    //
    //L.e("gg", "===========================");

    return response;
  }
}
