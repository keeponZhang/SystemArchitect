/*
 * Copyright (C) 2016 Square, Inc.
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
package okhttp3.internal.http;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.StreamAllocation;

import static okhttp3.internal.Util.checkDuration;

/**
 * A concrete interceptor chain that carries the entire interceptor chain: all application
 * interceptors, the OkHttp core, all network interceptors, and finally the network caller.
 */
public final class RealInterceptorChain implements Interceptor.Chain {
  private final List<Interceptor> interceptors;
  private final StreamAllocation streamAllocation;
  private final HttpCodec httpCodec;
  private final RealConnection connection;
  private final int index;
  private final Request request;
  private final Call call;
  private final EventListener eventListener;
  private final int connectTimeout;
  private final int readTimeout;
  private final int writeTimeout;
  private int calls;

  public RealInterceptorChain(List<Interceptor> interceptors, StreamAllocation streamAllocation,
      HttpCodec httpCodec, RealConnection connection, int index, Request request, Call call,
      EventListener eventListener, int connectTimeout, int readTimeout, int writeTimeout) {
    this.interceptors = interceptors;
    this.connection = connection;
    this.streamAllocation = streamAllocation;
    this.httpCodec = httpCodec;
    this.index = index;
    this.request = request;
    this.call = call;
    this.eventListener = eventListener;
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
    this.writeTimeout = writeTimeout;
  }

  @Override public Connection connection() {
    return connection;
  }

  @Override public int connectTimeoutMillis() {
    return connectTimeout;
  }

  @Override public Interceptor.Chain withConnectTimeout(int timeout, TimeUnit unit) {
    int millis = checkDuration("timeout", timeout, unit);
    return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index,
        request, call, eventListener, millis, readTimeout, writeTimeout);
  }

  @Override public int readTimeoutMillis() {
    return readTimeout;
  }

  @Override public Interceptor.Chain withReadTimeout(int timeout, TimeUnit unit) {
    int millis = checkDuration("timeout", timeout, unit);
    return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index,
        request, call, eventListener, connectTimeout, millis, writeTimeout);
  }

  @Override public int writeTimeoutMillis() {
    return writeTimeout;
  }

  @Override public Interceptor.Chain withWriteTimeout(int timeout, TimeUnit unit) {
    int millis = checkDuration("timeout", timeout, unit);
    return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index,
        request, call, eventListener, connectTimeout, readTimeout, millis);
  }

  public StreamAllocation streamAllocation() {
    return streamAllocation;
  }

  public HttpCodec httpStream() {
    return httpCodec;
  }

  @Override public Call call() {
    return call;
  }

  public EventListener eventListener() {
    return eventListener;
  }

  @Override public Request request() {
    return request;
  }
  //RealInterceptorChain 的 proceed 方法。
  @Override public Response proceed(Request request) throws IOException {
    return proceed(request, streamAllocation, httpCodec, connection);
  }

  //有几个interceptor，就有n+1（n-1+2 realcall实例化一个，realcall调用proceed实例化一个）调用链实例，第一个调用链的process方法在realcall调用，之后是在interptor的intercept方法调用，最后一个inteceptor不用调用
  //每调用一次chain.proceed方法，就会实例化一个调用链实例
  //或者可以这样理解，realcall的调用链实例是发起调用的，其他的实例作为interceptor中intetcept方法的参数
  public Response proceed(Request request, StreamAllocation streamAllocation, HttpCodec httpCodec,
      RealConnection connection) throws IOException {
    if (index >= interceptors.size()) throw new AssertionError();

    calls++;

    // If we already have a stream, confirm that the incoming request will use it.
    if (this.httpCodec != null && !this.connection.supportsUrl(request.url())) {
      throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
          + " must retain the same host and port");
    }

    // If we already have a stream, confirm that this is the only call to chain.proceed().
    if (this.httpCodec != null && calls > 1) {
      throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
          + " must call proceed() exactly once");
    }
    //关键部分的代码是这几句。
    // Call the next interceptor in the chain.
    RealInterceptorChain next = new RealInterceptorChain(interceptors, streamAllocation, httpCodec,
        connection, index + 1, request, call, eventListener, connectTimeout, readTimeout,
        writeTimeout);
    // 取列表中位于 index 位置的拦截器。
    Interceptor interceptor = interceptors.get(index);
    // 调用它的 intercept 方法，并传入新创建的 RealInterceptorChain。（Real的RealInterceptorChain的proceed方法为第一个interceptor创建intecept的方法参数，第一个interceptor调用chain.proceed为第二个interceptor创建intecept的方法参数，以此类推，然后调用interceptor的intetcept方法，一级一级派发下去）
    Response response = interceptor.intercept(next);

    // Confirm that the next interceptor made its required call to chain.proceed().
    //如果最后一个interceptor调用了chain.process方法，会抛异常
    if (httpCodec != null && index + 1 < interceptors.size() && next.calls != 1) {
      throw new IllegalStateException("network interceptor " + interceptor
          + " must call proceed() exactly once");
    }

    // Confirm that the intercepted response isn't null.
    if (response == null) {
      throw new NullPointerException("interceptor " + interceptor + " returned null");
    }

    if (response.body() == null) {
      throw new IllegalStateException(
          "interceptor " + interceptor + " returned a response with no body");
    }

    return response;
  }
}
