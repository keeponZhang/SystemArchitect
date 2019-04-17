/*
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.NamedRunnable;
import okhttp3.internal.cache.CacheInterceptor;
import okhttp3.internal.connection.ConnectInterceptor;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.BridgeInterceptor;
import okhttp3.internal.http.CallServerInterceptor;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.http.RetryAndFollowUpInterceptor;
import okhttp3.internal.platform.Platform;

import static okhttp3.internal.platform.Platform.INFO;

final class RealCall implements Call {
  final OkHttpClient client;
  final RetryAndFollowUpInterceptor retryAndFollowUpInterceptor;

  /**
   * There is a cycle between the {@link Call} and {@link EventListener} that makes this awkward.
   * This will be set after we create the call instance then create the event listener instance.
   */
  private EventListener eventListener;

  /** The application's original request unadulterated by redirects or auth headers. */
  final Request originalRequest;
  final boolean forWebSocket;

  // Guarded by this.
  private boolean executed;

  private RealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
    this.client = client;
    this.originalRequest = originalRequest;
    this.forWebSocket = forWebSocket;
    this.retryAndFollowUpInterceptor = new RetryAndFollowUpInterceptor(client, forWebSocket);
  }

  static RealCall newRealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
    // Safely publish the Call instance to the EventListener.
    RealCall call = new RealCall(client, originalRequest, forWebSocket);
    call.eventListener = client.eventListenerFactory().create(call);
    return call;
  }

  @Override public Request request() {
    return originalRequest;
  }
  //同步请求需要自己在子线程中调用
  @Override public Response execute() throws IOException {
    synchronized (this) {
      //如果已经发起过请求，那么直接跑出异常。
      if (executed) throw new IllegalStateException("Already Executed");
      //标记为已经发起过请求。
      executed = true;
    }
    captureCallStackTrace();
    //通知监听者已经开始请求。
    eventListener.callStart(this);
    try {
      //通过 OkHttpClient 的调度器执行请求。(//Dispatcher.executed 仅仅是将 Call 加入到队列当中，而并没有真正执行。)
      client.dispatcher().executed(this);
      //getResponseWithInterceptorChain() 责任链模式。
      Response result = getResponseWithInterceptorChain();
      if (result == null) throw new IOException("Canceled");
      return result;
    } catch (IOException e) {
      //通知监听者发生了异常。
      eventListener.callFailed(this, e);
      throw e;
    } finally {
      //通过调度器结束该任务。
      client.dispatcher().finished(this);
    }
  }

  private void captureCallStackTrace() {
    Object callStackTrace = Platform.get().getStackTraceForCloseable("response.body().close()");
    retryAndFollowUpInterceptor.setCallStackTrace(callStackTrace);
  }

  @Override public void enqueue(Callback responseCallback) {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    //捕获堆栈信息。
    captureCallStackTrace();
    //通知监听者请求开始了。
    eventListener.callStart(this);
    //调用调度器的 enqueue 方法。
    client.dispatcher().enqueue(new AsyncCall(responseCallback));
  }

  @Override public void cancel() {
    retryAndFollowUpInterceptor.cancel();
  }

  @Override public synchronized boolean isExecuted() {
    return executed;
  }

  @Override public boolean isCanceled() {
    return retryAndFollowUpInterceptor.isCanceled();
  }

  @SuppressWarnings("CloneDoesntCallSuperClone") // We are a final type & this saves clearing state.
  @Override public RealCall clone() {
    return RealCall.newRealCall(client, originalRequest, forWebSocket);
  }

  StreamAllocation streamAllocation() {
    return retryAndFollowUpInterceptor.streamAllocation();
  }
  //AsyncCall 为RealCall的一个内部类，持有外部类的引用
  final class AsyncCall extends NamedRunnable {
    //responseCallback 就是调用 call.enqueue 方法时传入的回调。
    private final Callback responseCallback;

    AsyncCall(Callback responseCallback) {
      super("OkHttp %s", redactedUrl());
      this.responseCallback = responseCallback;
    }

    String host() {
      return originalRequest.url().host();
    }

    Request request() {
      return originalRequest;
    }

    RealCall get() {
      return RealCall.this;
    }
    //该函数是在子线程当中执行的。
    @Override protected void execute() {
      boolean signalledCallback = false;
      try {
        //和同步请求的逻辑相同。
        Response response = getResponseWithInterceptorChain();
        if (retryAndFollowUpInterceptor.isCanceled()) {
          signalledCallback = true;
          responseCallback.onFailure(RealCall.this, new IOException("Canceled"));
        } else {
          signalledCallback = true;
          responseCallback.onResponse(RealCall.this, response);
        }
      } catch (IOException e) {
        if (signalledCallback) {
          // Do not signal the callback twice!
          Platform.get().log(INFO, "Callback failure for " + toLoggableString(), e);
        } else {
          eventListener.callFailed(RealCall.this, e);
          responseCallback.onFailure(RealCall.this, e);
        }
      } finally {
        //调用 Dispatcher 的 finished 方法,从而从readyAsyncCalls取出任务执行
        client.dispatcher().finished(this);
      }
    }
  }

  /**
   * Returns a string that describes this call. Doesn't include a full URL as that might contain
   * sensitive information.
   */
  String toLoggableString() {
    return (isCanceled() ? "canceled " : "")
        + (forWebSocket ? "web socket" : "call")
        + " to " + redactedUrl();
  }

  String redactedUrl() {
    return originalRequest.url().redact();
  }

  Response getResponseWithInterceptorChain() throws IOException {
    // Build a full stack of interceptors.
    // 拦截器的一个集合
    List<Interceptor> interceptors = new ArrayList<>();
    // 客户端的所有自定义拦截器
    interceptors.addAll(client.interceptors());// 自己的
    // OKhttp 5 个拦截器 ，责任链设计模式，每一个拦截器只处理与他相关的部分 volley
    interceptors.add(retryAndFollowUpInterceptor);// 重试
    interceptors.add(new BridgeInterceptor(client.cookieJar()));// 基础
    interceptors.add(new CacheInterceptor(client.internalCache()));// 缓存
    interceptors.add(new ConnectInterceptor(client));// 建立连接
    if (!this.forWebSocket) {
      //网络拦截器
      interceptors.addAll(this.client.networkInterceptors());
    }
    //访问服务器的拦截器。
    interceptors.add(new CallServerInterceptor(forWebSocket));// 写数据
    //创建调用链，注意第五个参数目前的值为0(该方法会执行第一个interceptor的intercept方法)。
    Interceptor.Chain chain = new RealInterceptorChain(interceptors, null, null, null, 0,
        originalRequest, this, eventListener, client.connectTimeoutMillis(),
        client.readTimeoutMillis(), client.writeTimeoutMillis());
    //执行调用链的 proceed 方法。
    return chain.proceed(originalRequest);
  }
}
