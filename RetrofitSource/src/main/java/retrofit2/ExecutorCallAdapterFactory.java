/*
 * Copyright (C) 2015 Square, Inc.
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
package retrofit2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import okhttp3.Request;

final class ExecutorCallAdapterFactory extends CallAdapter.Factory {
  final Executor callbackExecutor;
  //这里传入的Executor，就是前面我们构造的MainThreadExecutor，即把任务放到主线程中执行.
  ExecutorCallAdapterFactory(Executor callbackExecutor) {
    this.callbackExecutor = callbackExecutor;
  }
  //只重写了get方法，没有重写另外两个方法.
  @Override
  public CallAdapter<Call<?>> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
    //1.先判断返回值是不是Call<T>
    if (getRawType(returnType) != Call.class) {
      return null;
    }
    //获得Call<?>中类型，对于例子来说，就是ResponseBody.
    final Type responseType = Utils.getCallResponseType(returnType);
    //get方法返回的是CallAdapter对象
    return new CallAdapter<Call<?>>() {
  //就是Call<T>中T的类型.
      @Override public Type responseType() {
        return responseType;
      }
      //call是OkHttpCall，进行了包装，并没有改变它的类型.（静态代理）
      //所以输入跟返回类型一样
      @Override public <R> Call<R> adapt(Call<R> call) {

        return new ExecutorCallbackCall<>(callbackExecutor, call);
      }
    };
  }

  static final class ExecutorCallbackCall<T> implements Call<T> {
    final Executor callbackExecutor;
    final Call<T> delegate;

    ExecutorCallbackCall(Executor callbackExecutor, Call<T> delegate) {
      this.callbackExecutor = callbackExecutor;
      this.delegate = delegate;
    }

    //T的类型经callback传进来
    @Override public void enqueue(final Callback<T> callback) {
      if (callback == null) throw new NullPointerException("callback == null");
      //在实际执行任务的Call完成之后，调用MainThreadExecutor，使得使用者收到的回调是运行在主线程当中的.
      //retrofit的OkHttpCall<T>的泛型类型与Response<T>的泛型类型一样，所以这里的onResponse返回的final Response<T> response，类型已经解析出来
      delegate.enqueue(new Callback<T>() {
        @Override public void onResponse(Call<T> call, final Response<T> response) {
          //把回调切换回主线程
          callbackExecutor.execute(new Runnable() {
            @Override public void run() {
              //如果取消了，那么仍然算作失败.
              if (delegate.isCanceled()) {
                // Emulate OkHttp's behavior of throwing/delivering an IOException on cancellation.
                callback.onFailure(ExecutorCallbackCall.this, new IOException("Canceled"));
              } else {
                callback.onResponse(ExecutorCallbackCall.this, response);
              }
            }
          });
        }

        @Override public void onFailure(Call<T> call, final Throwable t) {
          callbackExecutor.execute(new Runnable() {
            @Override public void run() {
              callback.onFailure(ExecutorCallbackCall.this, t);
            }
          });
        }
      });
    }

    @Override public boolean isExecuted() {
      return delegate.isExecuted();
    }

    @Override public Response<T> execute() throws IOException {
      return delegate.execute();
    }

    @Override public void cancel() {
      delegate.cancel();
    }

    @Override public boolean isCanceled() {
      return delegate.isCanceled();
    }

    @SuppressWarnings("CloneDoesntCallSuperClone") // Performing deep clone.
    @Override public Call<T> clone() {
      return new ExecutorCallbackCall<>(callbackExecutor, delegate.clone());
    }

    @Override public Request request() {
      return delegate.request();
    }
  }
}
