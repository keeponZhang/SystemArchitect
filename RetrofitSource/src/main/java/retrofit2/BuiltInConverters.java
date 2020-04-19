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
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Streaming;

//这个类有好几个方法呢，responseBodyConverter需要特别注意
//所以默认的只能解析Call<ResponseBody>
final class BuiltInConverters extends Converter.Factory {
  @Override
  //responseBodyConverter方法所传入的type，其实是CallAdapter#responseType的类型，这也是它们相关联的地方。
  public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
      Retrofit retrofit) {
    //如果Type是ResponseBody，那么当注解中包含了Streaming.class时，返回StreamingXXX，否则返回BufferingXXX
    //这两个其实都是Converter<ResponseBody, ResponseBody>


    //Call<ResponseBody> listReposResponseBody 返回的是BufferingResponseBodyConverter
    if (type == ResponseBody.class) {
      if (Utils.isAnnotationPresent(annotations, Streaming.class)) {
        return StreamingResponseBodyConverter.INSTANCE;
      }
      return BufferingResponseBodyConverter.INSTANCE;
    }
    //如果Type为空，那么返回null。
    if (type == Void.class) {
      return VoidResponseBodyConverter.INSTANCE;
    }
    //如果返回类型不是Call<ResponseBody>或者Call<Void>,这里会返回null
    return null;
  }

  @Override
  public Converter<?, RequestBody> requestBodyConverter(Type type,
      Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
    //如果是RequestBody，或者是它的父类或者接口，那么返回Convert<RequestBody, RequestBody>，否则返回null。
    if (RequestBody.class.isAssignableFrom(Utils.getRawType(type))) {
      return RequestBodyConverter.INSTANCE;
    }
    return null;
  }

  @Override public Converter<?, String> stringConverter(Type type, Annotation[] annotations,
      Retrofit retrofit) {
    if (type == String.class) {
      return StringConverter.INSTANCE;
    }
    return null;
  }

  static final class StringConverter implements Converter<String, String> {
    static final StringConverter INSTANCE = new StringConverter();

    @Override public String convert(String value) throws IOException {
      return value;
    }
  }

  static final class VoidResponseBodyConverter implements Converter<ResponseBody, Void> {
    static final VoidResponseBodyConverter INSTANCE = new VoidResponseBodyConverter();

    @Override public Void convert(ResponseBody value) throws IOException {
      value.close();
      return null;
    }
  }

  static final class RequestBodyConverter implements Converter<RequestBody, RequestBody> {
    static final RequestBodyConverter INSTANCE = new RequestBodyConverter();

    @Override public RequestBody convert(RequestBody value) throws IOException {
      return value;
    }
  }

  static final class StreamingResponseBodyConverter
      implements Converter<ResponseBody, ResponseBody> {
    //其实就是一个单例
    static final StreamingResponseBodyConverter INSTANCE = new StreamingResponseBodyConverter();

    @Override public ResponseBody convert(ResponseBody value) throws IOException {
      return value;
    }
  }

  static final class BufferingResponseBodyConverter
      implements Converter<ResponseBody, ResponseBody> {
    static final BufferingResponseBodyConverter INSTANCE = new BufferingResponseBodyConverter();

    @Override public ResponseBody convert(ResponseBody value) throws IOException {
      try {
        // Buffer the entire body to avoid future I/O.
        return Utils.buffer(value);
      } finally {
        value.close();
      }
    }
  }

  static final class ToStringConverter implements Converter<Object, String> {
    static final ToStringConverter INSTANCE = new ToStringConverter();

    @Override public String convert(Object value) {
      return value.toString();
    }
  }
}
