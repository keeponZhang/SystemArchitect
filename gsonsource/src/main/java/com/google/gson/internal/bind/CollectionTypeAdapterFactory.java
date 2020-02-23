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

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Adapt a homogeneous collection of objects.
 */
public final class CollectionTypeAdapterFactory implements TypeAdapterFactory {
  private final ConstructorConstructor constructorConstructor;

  public CollectionTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
    this.constructorConstructor = constructorConstructor;
  }

  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
    Type type = typeToken.getType();

    Class<? super T> rawType = typeToken.getRawType();
    if (!Collection.class.isAssignableFrom(rawType)) {
      return null;
    }

    Log.w("TAG赋值",
            "CollectionTypeAdapterFactory candidate create 要开始获取集合类型啦" +
                    "(接着会调用<<getCollectionElementType>>  type" +
                    "(typeToken的type):"+type+
            " " +
            "rawType(typeToken的rawType)= "
            +rawType);
    Type elementType = $Gson$Types.getCollectionElementType(type, rawType);
    Log.e("TAG赋值",
            "CollectionTypeAdapterFactory （****注意******）candidate create 要开始调用获取<<gson" +
                    ".getAdapter>>集合集合类型的adapter啦 " +
                    "此时集合类型elementType:"+elementType);
    //如果使用List<T>,elementType此时为T,该TypeToken的TypeToken的rawType为Object
    TypeAdapter<?> elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));
    ObjectConstructor<T> constructor = constructorConstructor.get(typeToken);
    Log.w("TAG赋值", "CollectionTypeAdapterFactory candidate 创建好了constructor " +
            "和elementTypeAdapter，elementTypeAdapter="+elementTypeAdapter+ " 准备创建代理TypeAdapter");

    @SuppressWarnings({"unchecked", "rawtypes"}) // create() doesn't define a type parameter
    TypeAdapter<T> result = new Adapter(gson, elementType, elementTypeAdapter, constructor);
    Log.e("TAG赋值", "CollectionTypeAdapterFactory create 创建好了CollectionTypeAdapterFactory的 Adapter" +
            "(CollectionTypeAdapterFactory的内部类adapter,属于集合):" );
    return result;
  }

  private static final class Adapter<E> extends TypeAdapter<Collection<E>> {
    private final TypeAdapter<E> elementTypeAdapter;
    private final ObjectConstructor<? extends Collection<E>> constructor;

    public Adapter(Gson context, Type elementType,
        TypeAdapter<E> elementTypeAdapter,
        ObjectConstructor<? extends Collection<E>> constructor) {
      this.elementTypeAdapter =
          new TypeAdapterRuntimeTypeWrapper<E>(context, elementTypeAdapter, elementType);
      Log.e("TAG赋值", "CollectionTypeAdapterFactory 创建代理 TypeAdapter，原宿主为elementTypeAdapter:"+elementTypeAdapter );
      this.constructor = constructor;
    }

    @Override public Collection<E> read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return null;
      }


      Collection<E> collection = constructor.construct();
      Log.w("TAG赋值",
              " CollectionTypeAdapterFactory 集合Adapter  read 创建集合实例List ") ;
      in.beginArray();
      while (in.hasNext()) {
        Log.e("TAG赋值", "Adapter read 从流开始使用迭代  代理TypeAdapter（这个adapter针对集合Item的Model） 读出model " +
                "赋值给集合" +
                " " );
        E instance = elementTypeAdapter.read(in);
        collection.add(instance);
        Log.w("TAG赋值",
                "CollectionTypeAdapterFactory Adapter  通过elementTypeAdapter.read 创建集合实例中的Model成功，" +
                        " Model为="+instance+"  并加入集合");
      }
      in.endArray();
      return collection;
    }

    @Override public void write(JsonWriter out, Collection<E> collection) throws IOException {
      if (collection == null) {
        out.nullValue();
        return;
      }

      out.beginArray();
      for (E element : collection) {
        elementTypeAdapter.write(out, element);
      }
      out.endArray();
    }
  }
}
