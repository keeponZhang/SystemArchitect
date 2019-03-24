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
package com.darren.architect_day16.retrofit2;

import com.darren.architect_day16.retrofit2.http.DELETE;
import com.darren.architect_day16.retrofit2.http.FormUrlEncoded;
import com.darren.architect_day16.retrofit2.http.GET;
import com.darren.architect_day16.retrofit2.http.HEAD;
import com.darren.architect_day16.retrofit2.http.HTTP;
import com.darren.architect_day16.retrofit2.http.Multipart;
import com.darren.architect_day16.retrofit2.http.OPTIONS;
import com.darren.architect_day16.retrofit2.http.PATCH;
import com.darren.architect_day16.retrofit2.http.POST;
import com.darren.architect_day16.retrofit2.http.PUT;
import com.darren.architect_day16.retrofit2.http.Url;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Adapts an invocation of an interface method into an HTTP call.
 */
final class ServiceMethod<T> {
    // Upper and lower characters, digits, underscores, and hyphens, starting
    // with a character.
    static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
    static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + PARAM
            + ")\\}");
    static final Pattern PARAM_NAME_REGEX = Pattern.compile(PARAM);

    final okhttp3.Call.Factory callFactory;
    final CallAdapter<?> callAdapter;

    private final HttpUrl baseUrl;
    private final Converter<ResponseBody, T> responseConverter;
    private final String httpMethod;
    private final String relativeUrl;
    private final Headers headers;
    private final MediaType contentType;
    private final boolean hasBody;
    private final boolean isFormEncoded;
    private final boolean isMultipart;
    private final ParameterHandler<?>[] parameterHandlers;

    ServiceMethod(Builder<T> builder) {
        // 负责创建请求
        this.callFactory = builder.retrofit.callFactory();
        // 负责将请求适配成API中的Call或者RxJava的Observable
        this.callAdapter = builder.callAdapter;
        // 请求的baseUrl
        this.baseUrl = builder.retrofit.baseUrl();
        // 响应体的解析器，事例中设置了Gson来解析成
        this.responseConverter = builder.responseConverter;
        // 请求的方式，POST，GET。。。
        this.httpMethod = builder.httpMethod;
        // 请求的相对路径，API中定义的路径
        this.relativeUrl = builder.relativeUrl;
        // 请求报文头
        this.headers = builder.headers;
        // 请求报文类型
        this.contentType = builder.contentType;
        // 是否带有请求体
        this.hasBody = builder.hasBody;
        // 是否表单请求
        this.isFormEncoded = builder.isFormEncoded;
        // 是否是Multipart请求，一般用于传输文件
        this.isMultipart = builder.isMultipart;
        // 数组，记录请求中方法的参数，@Path，@Query 等等
        this.parameterHandlers = builder.parameterHandlers;
    }

    /**
     * Builds an HTTP request from method arguments.
     */
    Request toRequest(Object... args) throws IOException {
        // 创建一个 RequestBuilder
        RequestBuilder requestBuilder = new RequestBuilder(httpMethod, baseUrl,
                relativeUrl, headers, contentType, hasBody, isFormEncoded,
                isMultipart);

        @SuppressWarnings("unchecked")
        // It is an error to invoke a method with the wrong arg types.
                ParameterHandler<Object>[] handlers = (ParameterHandler<Object>[]) parameterHandlers;

        int argumentCount = args != null ? args.length : 0;
        if (argumentCount != handlers.length) {
            throw new IllegalArgumentException("Argument count ("
                    + argumentCount + ") doesn't match expected count ("
                    + handlers.length + ")");
        }

        for (int p = 0; p < argumentCount; p++) {
            handlers[p].apply(requestBuilder, args[p]);
        }

        return requestBuilder.build();
    }

    /**
     * Builds a method return value from an HTTP response body.
     */
    T toResponse(ResponseBody body) throws IOException {
        // 交给了解析Factory去解析
        return responseConverter.convert(body);
    }

    /**
     * Inspects the annotations on an interface method to construct a reusable
     * service method. This requires potentially-expensive reflection so it is
     * best to build each service method only once and reuse it. Builders cannot
     * be reused.
     */
    static final class Builder<T> {
        final Retrofit retrofit;
        final Method method;
        final Annotation[] methodAnnotations;
        final Annotation[][] parameterAnnotationsArray;
        final Type[] parameterTypes;

        Type responseType;
        boolean gotField;
        boolean gotPart;
        boolean gotBody;
        boolean gotPath;
        boolean gotQuery;
        boolean gotUrl;
        String httpMethod;
        boolean hasBody;
        boolean isFormEncoded;
        boolean isMultipart;
        String relativeUrl;
        Headers headers;
        MediaType contentType;
        Set<String> relativeUrlParamNames;
        ParameterHandler<?>[] parameterHandlers;
        Converter<ResponseBody, T> responseConverter;
        CallAdapter<?> callAdapter;

        public Builder(Retrofit retrofit, Method method) {
            this.retrofit = retrofit;
            this.method = method;
            // 方法注解列表(相当于我们的LoginService中的: @POST和@FormUrlEncoded......)
            this.methodAnnotations = method.getAnnotations();
            //获取方法的所有的参数的原始类型。
            this.parameterTypes = method.getGenericParameterTypes();
            // 方法参数注解列表(相当于我们的LoginService中的: @Field......)
            this.parameterAnnotationsArray = method.getParameterAnnotations();
        }

        public ServiceMethod build() {
            callAdapter = createCallAdapter();
            responseType = callAdapter.responseType();
            if (responseType == Response.class
                    || responseType == okhttp3.Response.class) {
                throw methodError("'"
                        + Utils.getRawType(responseType).getName()
                        + "' is not a valid response body type. Did you mean ResponseBody?");
            }
            responseConverter = createResponseConverter();

            // 循环遍历方法注解列表
            for (Annotation annotation : methodAnnotations) {
                // 解析方法属性
                parseMethodAnnotation(annotation);
            }

            if (httpMethod == null) {
                throw methodError("HTTP method annotation is required (e.g., @GET, @POST, etc.).");
            }

            if (!hasBody) {
                if (isMultipart) {
                    throw methodError("Multipart can only be specified on HTTP methods with request body (e.g., @POST).");
                }
                if (isFormEncoded) {
                    throw methodError("FormUrlEncoded can only be specified on HTTP methods with "
                            + "request body (e.g., @POST).");
                }
            }
            // 获取方法参数 - 长度
            int parameterCount = parameterAnnotationsArray.length;
            // 创建一个 ParameterHandler 数组
            parameterHandlers = new ParameterHandler<?>[parameterCount];
            for (int p = 0; p < parameterCount; p++) {
                // 获取 Type
                Type parameterType = parameterTypes[p];

                if (Utils.hasUnresolvableType(parameterType)) {
                    throw parameterError(
                            p,
                            "Parameter type must not include a type variable or wildcard: %s",
                            parameterType);
                }
                // 获取某个参数上面的 Annotation[] @Query @Path 等等
                Annotation[] parameterAnnotations = parameterAnnotationsArray[p];

                if (parameterAnnotations == null) {
                    throw parameterError(p, "No Retrofit annotation found.");
                }

                parameterHandlers[p] = parseParameter(p, parameterType,
                        parameterAnnotations);
            }

            if (relativeUrl == null && !gotUrl) {
                throw methodError("Missing either @%s URL or @Url parameter.",
                        httpMethod);
            }
            if (!isFormEncoded && !isMultipart && !hasBody && gotBody) {
                throw methodError("Non-body HTTP method cannot contain @Body.");
            }
            if (isFormEncoded && !gotField) {
                throw methodError("Form-encoded method must contain at least one @Field.");
            }
            if (isMultipart && !gotPart) {
                throw methodError("Multipart method must contain at least one @Part.");
            }

            return new ServiceMethod<>(this);
        }

        /**
         * 创建 CallAdapter
         *
         * @return
         */
        private CallAdapter<?> createCallAdapter() {
            // 先获取返回的 Type
            Type returnType = method.getGenericReturnType();

            if (Utils.hasUnresolvableType(returnType)) {
                throw methodError(
                        "Method return type must not include a type variable or wildcard: %s",
                        returnType);
            }
            // 不能返回一个 void
            if (returnType == void.class) {
                throw methodError("Service methods cannot return void.");
            }
            // 获取方法上面的所有属性
            Annotation[] annotations = method.getAnnotations();
            try {
                // callAdapter
                return retrofit.callAdapter(returnType, annotations);
            } catch (RuntimeException e) { // Wide exception range because
                // factories are user code.
                throw methodError(e, "Unable to create call adapter for %s",
                        returnType);
            }
        }

        private void parseMethodAnnotation(Annotation annotation) {
            //annotation.value(): 参数的key
            if (annotation instanceof DELETE) {
                // 解析 请求的方法注解
                parseHttpMethodAndPath("DELETE", ((DELETE) annotation).value(),
                        false);
            } else if (annotation instanceof GET) {
                parseHttpMethodAndPath("GET", ((GET) annotation).value(), false);
            } else if (annotation instanceof HEAD) {
                parseHttpMethodAndPath("HEAD", ((HEAD) annotation).value(),
                        false);
                if (!Void.class.equals(responseType)) {
                    throw methodError("HEAD method must use Void as response type.");
                }
            } else if (annotation instanceof PATCH) {
                parseHttpMethodAndPath("PATCH", ((PATCH) annotation).value(),
                        true);
            } else if (annotation instanceof POST) {
                parseHttpMethodAndPath("POST", ((POST) annotation).value(),
                        true);
            } else if (annotation instanceof PUT) {
                parseHttpMethodAndPath("PUT", ((PUT) annotation).value(), true);
            } else if (annotation instanceof OPTIONS) {
                parseHttpMethodAndPath("OPTIONS",
                        ((OPTIONS) annotation).value(), false);
            } else if (annotation instanceof HTTP) {
                HTTP http = (HTTP) annotation;
                parseHttpMethodAndPath(http.method(), http.path(),
                        http.hasBody());
            } else if (annotation instanceof com.darren.architect_day16.retrofit2.http.Headers) {
                String[] headersToParse = ((com.darren.architect_day16.retrofit2.http.Headers) annotation)
                        .value();
                if (headersToParse.length == 0) {
                    throw methodError("@Headers annotation is empty.");
                }
                headers = parseHeaders(headersToParse);
            } else if (annotation instanceof Multipart) {
                if (isFormEncoded) {
                    throw methodError("Only one encoding annotation is allowed.");
                }
                isMultipart = true;
            } else if (annotation instanceof FormUrlEncoded) {
                if (isMultipart) {
                    throw methodError("Only one encoding annotation is allowed.");
                }
                isFormEncoded = true;
            }
        }

        /**
         * 说白了：就是对我们的URL进行拼接
         *
         * @param httpMethod
         * @param value
         * @param hasBody
         */
        private void parseHttpMethodAndPath(String httpMethod, String value,
                                            boolean hasBody) {
            if (this.httpMethod != null) {
                // 只允许一个请求方法注解
                throw methodError(
                        "Only one HTTP method is allowed. Found: %s and %s.",
                        this.httpMethod, httpMethod);
            }
            this.httpMethod = httpMethod;
            this.hasBody = hasBody;

            if (value.isEmpty()) {
                return;
            }

            // 获取相关的URL路径和现有的查询字符串
            // 获取 ？ 的位置
            int question = value.indexOf('?');

            if (question != -1 && question < value.length() - 1) {
                // 确保查询字符串没有任何命名参数。
                // 截取问号以后的字符串
                String queryParams = value.substring(question + 1);
                // 正则表达式匹配判断
                Matcher queryParamMatcher = PARAM_URL_REGEX
                        .matcher(queryParams);
                if (queryParamMatcher.find()) {
                    throw methodError(
                            "URL query string \"%s\" must not have replace block. "
                                    + "For dynamic query parameters use @Query.",
                            queryParams);
                }
            }
            // 相对的 url 路径
            this.relativeUrl = value;
            // 解析 Path Parameters参数
            this.relativeUrlParamNames = parsePathParameters(value);
        }

        private Headers parseHeaders(String[] headers) {
            Headers.Builder builder = new Headers.Builder();
            for (String header : headers) {
                int colon = header.indexOf(':');
                if (colon == -1 || colon == 0 || colon == header.length() - 1) {
                    throw methodError(
                            "@Headers value must be in the form \"Name: Value\". Found: \"%s\"",
                            header);
                }
                String headerName = header.substring(0, colon);
                String headerValue = header.substring(colon + 1).trim();
                if ("Content-Type".equalsIgnoreCase(headerName)) {
                    MediaType type = MediaType.parse(headerValue);
                    if (type == null) {
                        throw methodError("Malformed content type: %s",
                                headerValue);
                    }
                    contentType = type;
                } else {
                    builder.add(headerName, headerValue);
                }
            }
            return builder.build();
        }

        /**
         * 解析方法参数
         *
         * @param p             角标
         * @param parameterType 参数的原始类型
         * @param annotations   该方法参数上面的所有注解
         * @return
         */
        private ParameterHandler<?> parseParameter(int p, Type parameterType,
                                                   Annotation[] annotations) {
            ParameterHandler<?> result = null;

            for (Annotation annotation : annotations) {
                ParameterHandler<?> annotationAction = parseParameterAnnotation(
                        p, parameterType, annotations, annotation);

                if (annotationAction == null) {
                    // 跳出当前循环
                    continue;
                }

                if (result != null) {
                    throw parameterError(p,
                            "Multiple Retrofit annotations found, only one allowed.");
                }

                result = annotationAction;
            }

            // 也就是说每个方法参数上面都必须要有 注解
            if (result == null) {
                throw parameterError(p, "No Retrofit annotation found.");
            }

            return result;
        }

        /**
         * 解析 Parameter 参数上面的注解
         *
         * @param p
         * @param type
         * @param annotations
         * @param annotation
         * @return
         */
        private ParameterHandler<?> parseParameterAnnotation(int p, Type type,
                                                             Annotation[] annotations, Annotation annotation) {
            if (annotation instanceof Url) {
                if (gotUrl) {
                    throw parameterError(p,
                            "Multiple @Url method annotations found.");
                }
                if (gotPath) {
                    throw parameterError(p,
                            "@Path parameters may not be used with @Url.");
                }
                if (gotQuery) {
                    throw parameterError(p,
                            "A @Url parameter must not come after a @Query");
                }
                if (relativeUrl != null) {
                    throw parameterError(p, "@Url cannot be used with @%s URL",
                            httpMethod);
                }

                gotUrl = true;

                if (type == HttpUrl.class
                        || type == String.class
                        || type == URI.class
                        || (type instanceof Class && "android.net.Uri"
                        .equals(((Class<?>) type).getName()))) {
                    return new ParameterHandler.RelativeUrl();
                } else {
                    throw parameterError(p,
                            "@Url must be okhttp3.HttpUrl, String, java.net.URI, or android.net.Uri type.");
                }

            } else if (annotation instanceof com.darren.architect_day16.retrofit2.http.Path) {
                if (gotQuery) {
                    throw parameterError(p,
                            "A @Path parameter must not come after a @Query.");
                }
                if (gotUrl) {
                    throw parameterError(p,
                            "@Path parameters may not be used with @Url.");
                }
                if (relativeUrl == null) {
                    throw parameterError(p,
                            "@Path can only be used with relative url on @%s",
                            httpMethod);
                }
                gotPath = true;

                com.darren.architect_day16.retrofit2.http.Path path = (com.darren.architect_day16.retrofit2.http.Path) annotation;
                // 获取 path 的值
                String name = path.value();
                // 检验值
                validatePathName(p, name);

                // 工厂设计模式，数据格式转换
                Converter<?, String> converter = retrofit.stringConverter(type,
                        annotations);
                return new ParameterHandler.Path<>(name, converter,
                        path.encoded());

            } else if (annotation instanceof com.darren.architect_day16.retrofit2.http.Query) {
                com.darren.architect_day16.retrofit2.http.Query query = (com.darren.architect_day16.retrofit2.http.Query) annotation;
                // 获取值
                String name = query.value();
                boolean encoded = query.encoded();
                // 获取参数的类型 ， int String ...
                Class<?> rawParameterType = Utils.getRawType(type);

                gotQuery = true;
                if (Iterable.class.isAssignableFrom(rawParameterType)) {
                    if (!(type instanceof ParameterizedType)) {
                        throw parameterError(p,
                                rawParameterType.getSimpleName()
                                        + " must include generic type (e.g., "
                                        + rawParameterType.getSimpleName()
                                        + "<String>)");
                    }
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Type iterableType = Utils.getParameterUpperBound(0,
                            parameterizedType);
                    Converter<?, String> converter = retrofit.stringConverter(
                            iterableType, annotations);
                    return new ParameterHandler.Query<>(name, converter,
                            encoded).iterable();
                } else if (rawParameterType.isArray()) {
                    Class<?> arrayComponentType = boxIfPrimitive(rawParameterType
                            .getComponentType());
                    Converter<?, String> converter = retrofit.stringConverter(
                            arrayComponentType, annotations);
                    return new ParameterHandler.Query<>(name, converter,
                            encoded).array();
                } else {
                    Converter<?, String> converter = retrofit.stringConverter(
                            type, annotations);
                    return new ParameterHandler.Query<>(name, converter,
                            encoded);
                }

            } else if (annotation instanceof com.darren.architect_day16.retrofit2.http.QueryMap) {
                Class<?> rawParameterType = Utils.getRawType(type);

                if (!Map.class.isAssignableFrom(rawParameterType)) {
                    throw parameterError(p,
                            "@QueryMap parameter type must be Map.");
                }
                Type mapType = Utils.getSupertype(type, rawParameterType,
                        Map.class);
                if (!(mapType instanceof ParameterizedType)) {
                    throw parameterError(p,
                            "Map must include generic types (e.g., Map<String, String>)");
                }
                ParameterizedType parameterizedType = (ParameterizedType) mapType;
                Type keyType = Utils.getParameterUpperBound(0,
                        parameterizedType);
                if (String.class != keyType) {
                    throw parameterError(p,
                            "@QueryMap keys must be of type String: " + keyType);
                }
                Type valueType = Utils.getParameterUpperBound(1,
                        parameterizedType);
                Converter<?, String> valueConverter = retrofit.stringConverter(
                        valueType, annotations);

                return new ParameterHandler.QueryMap<>(valueConverter,
                        ((com.darren.architect_day16.retrofit2.http.QueryMap) annotation).encoded());

            } else if (annotation instanceof com.darren.architect_day16.retrofit2.http.Header) {
                com.darren.architect_day16.retrofit2.http.Header header = (com.darren.architect_day16.retrofit2.http.Header) annotation;
                String name = header.value();

                Class<?> rawParameterType = Utils.getRawType(type);
                if (Iterable.class.isAssignableFrom(rawParameterType)) {
                    if (!(type instanceof ParameterizedType)) {
                        throw parameterError(p,
                                rawParameterType.getSimpleName()
                                        + " must include generic type (e.g., "
                                        + rawParameterType.getSimpleName()
                                        + "<String>)");
                    }
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Type iterableType = Utils.getParameterUpperBound(0,
                            parameterizedType);
                    Converter<?, String> converter = retrofit.stringConverter(
                            iterableType, annotations);
                    return new ParameterHandler.Header<>(name, converter)
                            .iterable();
                } else if (rawParameterType.isArray()) {
                    Class<?> arrayComponentType = boxIfPrimitive(rawParameterType
                            .getComponentType());
                    Converter<?, String> converter = retrofit.stringConverter(
                            arrayComponentType, annotations);
                    return new ParameterHandler.Header<>(name, converter)
                            .array();
                } else {
                    Converter<?, String> converter = retrofit.stringConverter(
                            type, annotations);
                    return new ParameterHandler.Header<>(name, converter);
                }

            } else if (annotation instanceof com.darren.architect_day16.retrofit2.http.HeaderMap) {
                Class<?> rawParameterType = Utils.getRawType(type);
                if (!Map.class.isAssignableFrom(rawParameterType)) {
                    throw parameterError(p,
                            "@HeaderMap parameter type must be Map.");
                }
                Type mapType = Utils.getSupertype(type, rawParameterType,
                        Map.class);
                if (!(mapType instanceof ParameterizedType)) {
                    throw parameterError(p,
                            "Map must include generic types (e.g., Map<String, String>)");
                }
                ParameterizedType parameterizedType = (ParameterizedType) mapType;
                Type keyType = Utils.getParameterUpperBound(0,
                        parameterizedType);
                if (String.class != keyType) {
                    throw parameterError(p,
                            "@HeaderMap keys must be of type String: "
                                    + keyType);
                }
                Type valueType = Utils.getParameterUpperBound(1,
                        parameterizedType);
                Converter<?, String> valueConverter = retrofit.stringConverter(
                        valueType, annotations);

                return new ParameterHandler.HeaderMap<>(valueConverter);

            } else if (annotation instanceof com.darren.architect_day16.retrofit2.http.Field) {
                if (!isFormEncoded) {
                    throw parameterError(p,
                            "@Field parameters can only be used with form encoding.");
                }
                com.darren.architect_day16.retrofit2.http.Field field = (com.darren.architect_day16.retrofit2.http.Field) annotation;
                String name = field.value();
                boolean encoded = field.encoded();

                gotField = true;

                Class<?> rawParameterType = Utils.getRawType(type);
                if (Iterable.class.isAssignableFrom(rawParameterType)) {
                    if (!(type instanceof ParameterizedType)) {
                        throw parameterError(p,
                                rawParameterType.getSimpleName()
                                        + " must include generic type (e.g., "
                                        + rawParameterType.getSimpleName()
                                        + "<String>)");
                    }
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Type iterableType = Utils.getParameterUpperBound(0,
                            parameterizedType);
                    Converter<?, String> converter = retrofit.stringConverter(
                            iterableType, annotations);
                    return new ParameterHandler.Field<>(name, converter,
                            encoded).iterable();
                } else if (rawParameterType.isArray()) {
                    Class<?> arrayComponentType = boxIfPrimitive(rawParameterType
                            .getComponentType());
                    Converter<?, String> converter = retrofit.stringConverter(
                            arrayComponentType, annotations);
                    return new ParameterHandler.Field<>(name, converter,
                            encoded).array();
                } else {
                    Converter<?, String> converter = retrofit.stringConverter(
                            type, annotations);
                    return new ParameterHandler.Field<>(name, converter,
                            encoded);
                }

            } else if (annotation instanceof com.darren.architect_day16.retrofit2.http.FieldMap) {
                if (!isFormEncoded) {
                    throw parameterError(p,
                            "@FieldMap parameters can only be used with form encoding.");
                }
                Class<?> rawParameterType = Utils.getRawType(type);
                if (!Map.class.isAssignableFrom(rawParameterType)) {
                    throw parameterError(p,
                            "@FieldMap parameter type must be Map.");
                }
                Type mapType = Utils.getSupertype(type, rawParameterType,
                        Map.class);
                if (!(mapType instanceof ParameterizedType)) {
                    throw parameterError(p,
                            "Map must include generic types (e.g., Map<String, String>)");
                }
                ParameterizedType parameterizedType = (ParameterizedType) mapType;
                Type keyType = Utils.getParameterUpperBound(0,
                        parameterizedType);
                if (String.class != keyType) {
                    throw parameterError(p,
                            "@FieldMap keys must be of type String: " + keyType);
                }
                Type valueType = Utils.getParameterUpperBound(1,
                        parameterizedType);
                Converter<?, String> valueConverter = retrofit.stringConverter(
                        valueType, annotations);

                gotField = true;
                return new ParameterHandler.FieldMap<>(valueConverter,
                        ((com.darren.architect_day16.retrofit2.http.FieldMap) annotation).encoded());

            } else if (annotation instanceof com.darren.architect_day16.retrofit2.http.Part) {
                if (!isMultipart) {
                    throw parameterError(p,
                            "@Part parameters can only be used with multipart encoding.");
                }
                com.darren.architect_day16.retrofit2.http.Part part = (com.darren.architect_day16.retrofit2.http.Part) annotation;
                gotPart = true;

                String partName = part.value();
                Class<?> rawParameterType = Utils.getRawType(type);
                if (partName.isEmpty()) {
                    if (Iterable.class.isAssignableFrom(rawParameterType)) {
                        if (!(type instanceof ParameterizedType)) {
                            throw parameterError(
                                    p,
                                    rawParameterType.getSimpleName()
                                            + " must include generic type (e.g., "
                                            + rawParameterType.getSimpleName()
                                            + "<String>)");
                        }
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        Type iterableType = Utils.getParameterUpperBound(0,
                                parameterizedType);
                        if (!MultipartBody.Part.class.isAssignableFrom(Utils
                                .getRawType(iterableType))) {
                            throw parameterError(p,
                                    "@Part annotation must supply a name or use MultipartBody.Part parameter type.");
                        }
                        return ParameterHandler.RawPart.INSTANCE.iterable();
                    } else if (rawParameterType.isArray()) {
                        Class<?> arrayComponentType = rawParameterType
                                .getComponentType();
                        if (!MultipartBody.Part.class
                                .isAssignableFrom(arrayComponentType)) {
                            throw parameterError(p,
                                    "@Part annotation must supply a name or use MultipartBody.Part parameter type.");
                        }
                        return ParameterHandler.RawPart.INSTANCE.array();
                    } else if (MultipartBody.Part.class
                            .isAssignableFrom(rawParameterType)) {
                        return ParameterHandler.RawPart.INSTANCE;
                    } else {
                        throw parameterError(p,
                                "@Part annotation must supply a name or use MultipartBody.Part parameter type.");
                    }
                } else {
                    Headers headers = Headers.of("Content-Disposition",
                            "form-data; name=\"" + partName + "\"",
                            "Content-Transfer-Encoding", part.encoding());

                    if (Iterable.class.isAssignableFrom(rawParameterType)) {
                        if (!(type instanceof ParameterizedType)) {
                            throw parameterError(
                                    p,
                                    rawParameterType.getSimpleName()
                                            + " must include generic type (e.g., "
                                            + rawParameterType.getSimpleName()
                                            + "<String>)");
                        }
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        Type iterableType = Utils.getParameterUpperBound(0,
                                parameterizedType);
                        if (MultipartBody.Part.class.isAssignableFrom(Utils
                                .getRawType(iterableType))) {
                            throw parameterError(
                                    p,
                                    "@Part parameters using the MultipartBody.Part must not "
                                            + "include a part name in the annotation.");
                        }
                        Converter<?, RequestBody> converter = retrofit
                                .requestBodyConverter(iterableType,
                                        annotations, methodAnnotations);
                        return new ParameterHandler.Part<>(headers, converter)
                                .iterable();
                    } else if (rawParameterType.isArray()) {
                        Class<?> arrayComponentType = boxIfPrimitive(rawParameterType
                                .getComponentType());
                        if (MultipartBody.Part.class
                                .isAssignableFrom(arrayComponentType)) {
                            throw parameterError(
                                    p,
                                    "@Part parameters using the MultipartBody.Part must not "
                                            + "include a part name in the annotation.");
                        }
                        Converter<?, RequestBody> converter = retrofit
                                .requestBodyConverter(arrayComponentType,
                                        annotations, methodAnnotations);
                        return new ParameterHandler.Part<>(headers, converter)
                                .array();
                    } else if (MultipartBody.Part.class
                            .isAssignableFrom(rawParameterType)) {
                        throw parameterError(
                                p,
                                "@Part parameters using the MultipartBody.Part must not "
                                        + "include a part name in the annotation.");
                    } else {
                        Converter<?, RequestBody> converter = retrofit
                                .requestBodyConverter(type, annotations,
                                        methodAnnotations);
                        return new ParameterHandler.Part<>(headers, converter);
                    }
                }

            } else if (annotation instanceof com.darren.architect_day16.retrofit2.http.PartMap) {
                if (!isMultipart) {
                    throw parameterError(p,
                            "@PartMap parameters can only be used with multipart encoding.");
                }
                gotPart = true;
                Class<?> rawParameterType = Utils.getRawType(type);
                if (!Map.class.isAssignableFrom(rawParameterType)) {
                    throw parameterError(p,
                            "@PartMap parameter type must be Map.");
                }
                Type mapType = Utils.getSupertype(type, rawParameterType,
                        Map.class);
                if (!(mapType instanceof ParameterizedType)) {
                    throw parameterError(p,
                            "Map must include generic types (e.g., Map<String, String>)");
                }
                ParameterizedType parameterizedType = (ParameterizedType) mapType;

                Type keyType = Utils.getParameterUpperBound(0,
                        parameterizedType);
                if (String.class != keyType) {
                    throw parameterError(p,
                            "@PartMap keys must be of type String: " + keyType);
                }

                Type valueType = Utils.getParameterUpperBound(1,
                        parameterizedType);
                if (MultipartBody.Part.class.isAssignableFrom(Utils
                        .getRawType(valueType))) {
                    throw parameterError(
                            p,
                            "@PartMap values cannot be MultipartBody.Part. "
                                    + "Use @Part List<Part> or a different value type instead.");
                }

                Converter<?, RequestBody> valueConverter = retrofit
                        .requestBodyConverter(valueType, annotations,
                                methodAnnotations);

                com.darren.architect_day16.retrofit2.http.PartMap partMap = (com.darren.architect_day16.retrofit2.http.PartMap) annotation;
                return new ParameterHandler.PartMap<>(valueConverter,
                        partMap.encoding());

            } else if (annotation instanceof com.darren.architect_day16.retrofit2.http.Body) {
                if (isFormEncoded || isMultipart) {
                    throw parameterError(p,
                            "@Body parameters cannot be used with form or multi-part encoding.");
                }
                if (gotBody) {
                    throw parameterError(p,
                            "Multiple @Body method annotations found.");
                }

                Converter<?, RequestBody> converter;
                try {
                    converter = retrofit.requestBodyConverter(type,
                            annotations, methodAnnotations);
                } catch (RuntimeException e) {
                    // Wide exception range because factories are user code.
                    throw parameterError(e, p,
                            "Unable to create @Body converter for %s", type);
                }
                gotBody = true;
                return new ParameterHandler.Body<>(converter);
            }

            return null; // Not a Retrofit annotation.
        }

        private void validatePathName(int p, String name) {
            if (!PARAM_NAME_REGEX.matcher(name).matches()) {
                throw parameterError(p,
                        "@Path parameter name must match %s. Found: %s",
                        PARAM_URL_REGEX.pattern(), name);
            }
            // Verify URL replacement name is actually present in the URL path.
            if (!relativeUrlParamNames.contains(name)) {
                throw parameterError(p,
                        "URL \"%s\" does not contain \"{%s}\".", relativeUrl,
                        name);
            }
        }

        private Converter<ResponseBody, T> createResponseConverter() {
            Annotation[] annotations = method.getAnnotations();
            try {
                return retrofit
                        .responseBodyConverter(responseType, annotations);
            } catch (RuntimeException e) { // Wide exception range because
                // factories are user code.
                throw methodError(e, "Unable to create converter for %s",
                        responseType);
            }
        }

        private RuntimeException methodError(String message, Object... args) {
            return methodError(null, message, args);
        }

        private RuntimeException methodError(Throwable cause, String message,
                                             Object... args) {
            message = String.format(message, args);
            return new IllegalArgumentException(message + "\n    for method "
                    + method.getDeclaringClass().getSimpleName() + "."
                    + method.getName(), cause);
        }

        private RuntimeException parameterError(Throwable cause, int p,
                                                String message, Object... args) {
            return methodError(cause,
                    message + " (parameter #" + (p + 1) + ")", args);
        }

        private RuntimeException parameterError(int p, String message,
                                                Object... args) {
            return methodError(message + " (parameter #" + (p + 1) + ")", args);
        }
    }

    /**
     * Gets the set of unique path parameters used in the given URI. If a
     * parameter is used twice in the URI, it will only show up once in the set.
     */
    static Set<String> parsePathParameters(String path) {
        // 解析那些需要替换的字符串存入集合返回，如 @GET("/users/{user}/repos") 就会解析到 [user]
        Matcher m = PARAM_URL_REGEX.matcher(path);
        Set<String> patterns = new LinkedHashSet<>();
        while (m.find()) {
            patterns.add(m.group(1));
        }
        return patterns;
    }

    static Class<?> boxIfPrimitive(Class<?> type) {
        if (boolean.class == type)
            return Boolean.class;
        if (byte.class == type)
            return Byte.class;
        if (char.class == type)
            return Character.class;
        if (double.class == type)
            return Double.class;
        if (float.class == type)
            return Float.class;
        if (int.class == type)
            return Integer.class;
        if (long.class == type)
            return Long.class;
        if (short.class == type)
            return Short.class;
        return type;
    }
}
