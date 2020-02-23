/*
 * Copyright (C) 2011 Google Inc.
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

package com.google.gson.internal.bind;

import android.util.Log;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.reflect.ReflectionAccessor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Type adapter that reflects over the fields and methods of a class.
 */
public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
  private final ConstructorConstructor constructorConstructor;
  private final FieldNamingStrategy fieldNamingPolicy;
  private final Excluder excluder;
  private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
  private final ReflectionAccessor accessor = ReflectionAccessor.getInstance();

  public ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor,
      FieldNamingStrategy fieldNamingPolicy, Excluder excluder,
      JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory) {
    this.constructorConstructor = constructorConstructor;
    this.fieldNamingPolicy = fieldNamingPolicy;
    this.excluder = excluder;
    this.jsonAdapterFactory = jsonAdapterFactory;
  }

  public boolean excludeField(Field f, boolean serialize) {
    return excludeField(f, serialize, excluder);
  }

  static boolean excludeField(Field f, boolean serialize, Excluder excluder) {
    return !excluder.excludeClass(f.getType(), serialize) && !excluder.excludeField(f, serialize);
  }

  /** first element holds the default name */
  private List<String> getFieldNames(Field f) {
    SerializedName annotation = f.getAnnotation(SerializedName.class);
    if (annotation == null) {
      String name = fieldNamingPolicy.translateName(f);
      return Collections.singletonList(name);
    }

    String serializedName = annotation.value();
    String[] alternates = annotation.alternate();
    if (alternates.length == 0) {
      return Collections.singletonList(serializedName);
    }

    List<String> fieldNames = new ArrayList<String>(alternates.length + 1);
    fieldNames.add(serializedName);
    for (String alternate : alternates) {
      fieldNames.add(alternate);
    }
    return fieldNames;
  }

  @Override public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
    Class<? super T> raw = type.getRawType();

    if (!Object.class.isAssignableFrom(raw)) {
      return null; // it's a primitive!
    }

    Log.e("TAG", "ReflectiveTypeAdapterFactory create typeOfT candidate 创建啦 用于自定义model的构造器:"+type);
    ObjectConstructor<T> constructor = constructorConstructor.get(type);
    Log.w("TAG", "ReflectiveTypeAdapterFactory create 准备创建ReflectiveTypeAdapterFactory的adapter" +
            "(首先通过getBoundFields创建adapter的构造函数参数):" );
    Adapter<T> tAdapter = new Adapter<>(constructor, getBoundFields(gson, type, raw));
    Log.e("TAG", "ReflectiveTypeAdapterFactory create tAdapter 创建成功返回:"+tAdapter );
    return tAdapter;
  }
  boolean isInit = false;
  private BoundField createBoundField(
      final Gson context, final Field field, final String name,
      final TypeToken<?> fieldType, boolean serialize, boolean deserialize) {
    final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
    // special casing primitives here saves ~5% on Android...
    JsonAdapter annotation = field.getAnnotation(JsonAdapter.class);
    TypeAdapter<?> mapped = null;
    if (annotation != null) {
      mapped = jsonAdapterFactory.getTypeAdapter(
          constructorConstructor, context, fieldType, annotation);
    }
    //自定义model里面会匹配类如StringAdapter作为BoundField的变量
    final boolean jsonAdapterPresent = mapped != null;
    String s = !isInit ? " ！！！！！！！！！！！！！！！！！！" : "????????????????????";
    Log.e("TAG赋值",
            s +"ReflectiveTypeAdapterFactory " +
                    "(比如User.name也会走到这里哦，用StringAdapter) " +
                    "createBoundField 开始根据fieldType(这里的fieldType其实就是最外层的)adapter获取啦 " +
                    fieldType);
    isInit = true;
    if (mapped == null) mapped = context.getAdapter(fieldType);
    Log.w("TAG赋值",
            "ReflectiveTypeAdapterFactory candidate createBoundField 根据fieldType创建返回了adapter" +
                    "(这里的adapter在BoundField里面的read方法调用) " +
                    "TypeToken " +
                    "fieldType:"+fieldType+
            "  " +
            "创建了mapped " +
            "="+mapped.getClass().getGenericSuperclass());
    final TypeAdapter<?> typeAdapter = mapped;
    return new BoundField(name, serialize, deserialize) {
      @SuppressWarnings({"unchecked", "rawtypes"}) // the type adapter and field type always agree
      @Override void write(JsonWriter writer, Object value)
          throws IOException, IllegalAccessException {
        Object fieldValue = field.get(value);
        TypeAdapter t = jsonAdapterPresent ? typeAdapter
            : new TypeAdapterRuntimeTypeWrapper(context, typeAdapter, fieldType.getType());
        t.write(writer, fieldValue);
      }
      @Override void read(JsonReader reader, Object value)
          throws IOException, IllegalAccessException {
        Log.e("TAG赋值",
                "ReflectiveTypeAdapterFactory BoundField  开始 typeAdapterread:"+typeAdapter+" " +
                        "value" );
        Object fieldValue = typeAdapter.read(reader);
        Log.w("TAG赋值",
                "ReflectiveTypeAdapterFactory BoundField read 结束 typeAdapterread typeAdapter:"+typeAdapter +
                        " " +
                        "field" +
                        "="+field.getName()+" fieldValue="+fieldValue);
        if (fieldValue != null || !isPrimitive) {
          field.set(value, fieldValue);
        }
        Log.e("TAG", "ReflectiveTypeAdapterFactory BoundField read 赋值成功 field:"+field );
      }
      @Override public boolean writeField(Object value) throws IOException, IllegalAccessException {
        if (!serialized) return false;
        Object fieldValue = field.get(value);
        return fieldValue != value; // avoid recursion for example for Throwable.cause
      }
    };
  }

  private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw) {
    Log.w("TAG", "ReflectiveTypeAdapterFactory 调用<<getBoundFields>>方法:" );
    Map<String, BoundField> result = new LinkedHashMap<String, BoundField>();
    if (raw.isInterface()) {
      return result;
    }

    Type declaredType = type.getType();
    while (raw != Object.class) {
      Field[] fields = raw.getDeclaredFields();
      for (Field  field : fields) {
        Log.d("TAG", "ReflectiveTypeAdapterFactory getBoundFields field:"+field );
      }
      for (Field field : fields) {
        boolean serialize = excludeField(field, true);
        boolean deserialize = excludeField(field, false);
        if (!serialize && !deserialize) {
          continue;
        }
        accessor.makeAccessible(field);
        //candidate resolve前 属性field:data  type.getType()=com.darren.architect_day01.data.entity
        // .Result<java.util.List<T>>  com.darren.architect_day01.data.entity.Result     field
        // .getGenericType()=T
        //field.getGenericType()获取属性的泛型类型
        //field.getGType()获取属性的类型（如果属性不是泛型，两个没多大区别）
        Log.w("TAG",
                "ReflectiveTypeAdapterFactory getBoundFields candidate resolve前" +
                        "(准备调用reslove方法，返回里面的一层)" +
                        " " +
                        "属性field:"+field.getName()+"  " +
                        "（type是typeToken）type.getType()="+type.getType()+"   raw "+raw+"     " +
                "field.getGenericType()="+field.getGenericType()+" field.getType:"+field.getType());
        //incorrect 分别返回的是Result<java.util.List<T>>  Result 和T
        //incorrect 分别返回的是Result<java.util.List<com.darren.architect_day01.data.entity.User>>  Result和T
        Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
        ///incorrect 返回List<T>
        // incorrect 返回的是List<com.darren.architect_day01.data.entity.User>
        Log.w("TAG", "ReflectiveTypeAdapterFactory getBoundFields candidate 调用reslove方法后 " +
                "获取到的fieldType（其实是返回里面的一层Bean）="+fieldType+"  准备调用getFieldNames");
        //这里根据属性去拿到多少个fieldNames
        List<String> fieldNames = getFieldNames(field);
        Log.e("TAG", "ReflectiveTypeAdapterFactory getBoundFields getFieldNames " +
                "这里根据属性去拿到多少个fieldNames,准备for循环fieldNames:" );
        BoundField previous = null;
        for (int i = 0, size = fieldNames.size(); i < size; ++i) {
          String name = fieldNames.get(i);
          if (i != 0) serialize = false; // only serialize the default name
          Log.e("TAG",
                  "ReflectiveTypeAdapterFactory getBoundFields " +
                          "准备调用<<createBoundField>>（里面也会创建adapter）  " +
                          "name: "+name );
          BoundField boundField = createBoundField(context, field, name,
              TypeToken.get(fieldType), serialize, deserialize);
          BoundField replaced = result.put(name, boundField);
          if (previous == null) previous = replaced;
        }
        if (previous != null) {
          throw new IllegalArgumentException(declaredType
              + " declares multiple JSON fields named " + previous.name);
        }
      }
      type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
      raw = type.getRawType();
    }
    return result;
  }

  static abstract class BoundField {
    final String name;
    final boolean serialized;
    final boolean deserialized;

    protected BoundField(String name, boolean serialized, boolean deserialized) {
      this.name = name;
      this.serialized = serialized;
      this.deserialized = deserialized;
    }
    abstract boolean writeField(Object value) throws IOException, IllegalAccessException;
    abstract void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException;
    abstract void read(JsonReader reader, Object value) throws IOException, IllegalAccessException;
  }

  public static final class Adapter<T> extends TypeAdapter<T> {
    private final ObjectConstructor<T> constructor;
    private final Map<String, BoundField> boundFields;

    Adapter(ObjectConstructor<T> constructor, Map<String, BoundField> boundFields) {
      this.constructor = constructor;
      this.boundFields = boundFields;
    }

    @Override public T read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return null;
      }

      T instance = constructor.construct();
      Log.d("TAG赋值",
              "Adapter  ReflectiveTypeAdapterFactory 内部类Adapter read方法 通过constructor创建了实例 ," +
                      "实例instance为 :"+instance
              );

      try {
        in.beginObject();
        while (in.hasNext()) {
          String name = in.nextName();
          Log.e("TAG赋值",
                  "ReflectiveTypeAdapterFactory 内部类Adapter read方法  准备要调用BoundField的read方法 " +
                          "目的把实例instance "+instance+" 赋值给name" +
                          "=" +name+" (此时还没赋值）");
          BoundField field = boundFields.get(name);
          if (field == null || !field.deserialized) {
            in.skipValue();
          } else {
            field.read(in, instance);
          }
          //最外层是最后赋值的，Result是最后赋值的
          Log.w("TAG",
                  "ReflectiveTypeAdapterFactory 内部类Adapter read方法  Model赋值成功 instance:"+instance );
        }
      } catch (IllegalStateException e) {
        throw new JsonSyntaxException(e);
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
      in.endObject();
      return instance;
    }

    @Override public void write(JsonWriter out, T value) throws IOException {
      if (value == null) {
        out.nullValue();
        return;
      }

      out.beginObject();
      try {
        for (BoundField boundField : boundFields.values()) {
          if (boundField.writeField(value)) {
            out.name(boundField.name);
            boundField.write(out, value);
          }
        }
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
      out.endObject();
    }
  }
}
