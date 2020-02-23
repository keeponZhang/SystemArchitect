/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.internal;

import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.*;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Static methods for working with types.
 *
 * @author Bob Lee
 * @author Jesse Wilson
 */
public final class $Gson$Types {
  static final Type[] EMPTY_TYPE_ARRAY = new Type[] {};
  public static final String TAG_赋值 = "TAG赋值";

  private $Gson$Types() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a new parameterized type, applying {@code typeArguments} to
   * {@code rawType} and enclosed by {@code ownerType}.
   *
   * @return a {@link Serializable serializable} parameterized type.
   */
  public static ParameterizedType newParameterizedTypeWithOwner(
      Type ownerType, Type rawType, Type... typeArguments) {
    return new ParameterizedTypeImpl(ownerType, rawType, typeArguments);
  }

  /**
   * Returns an array type whose elements are all instances of
   * {@code componentType}.
   *
   * @return a {@link Serializable serializable} generic array type.
   */
  public static GenericArrayType arrayOf(Type componentType) {
    return new GenericArrayTypeImpl(componentType);
  }

  /**
   * Returns a type that represents an unknown type that extends {@code bound}.
   * For example, if {@code bound} is {@code CharSequence.class}, this returns
   * {@code ? extends CharSequence}. If {@code bound} is {@code Object.class},
   * this returns {@code ?}, which is shorthand for {@code ? extends Object}.
   */
  public static WildcardType subtypeOf(Type bound) {
    Type[] upperBounds;
    if (bound instanceof WildcardType) {
      upperBounds = ((WildcardType) bound).getUpperBounds();
    } else {
      upperBounds = new Type[] { bound };
    }
    return new WildcardTypeImpl(upperBounds, EMPTY_TYPE_ARRAY);
  }

  /**
   * Returns a type that represents an unknown supertype of {@code bound}. For
   * example, if {@code bound} is {@code String.class}, this returns {@code ?
   * super String}.
   */
  public static WildcardType supertypeOf(Type bound) {
    Type[] lowerBounds;
    if (bound instanceof WildcardType) {
      lowerBounds = ((WildcardType) bound).getLowerBounds();
    } else {
      lowerBounds = new Type[] { bound };
    }
    return new WildcardTypeImpl(new Type[] { Object.class }, lowerBounds);
  }

  /**
   * Returns a type that is functionally equal but not necessarily equal
   * according to {@link Object#equals(Object) Object.equals()}. The returned
   * type is {@link Serializable}.
   */
  public static Type canonicalize(Type type) {
    Log.e("TAG", "$Gson$Types canonicalize 参数type:" +type);
    if (type instanceof Class) {
      Class<?> c = (Class<?>) type;
      boolean isArray = c.isArray();
      if(isArray){
        Log.e("TAG",
                "$Gson$Types canonicalize方法 isArray 准备递归调用canonicalize 传入参数:"+c.getComponentType() );
        GenericArrayTypeImpl genericArrayType =
                new GenericArrayTypeImpl(canonicalize(c.getComponentType()));
        Log.e(TAG_赋值, "$Gson$Types canonicalize方法 情况1 return 创建好的结果 genericArrayType:" +genericArrayType);
        return genericArrayType;
      }else{
        Log.e(TAG_赋值, "$Gson$Types canonicalize方法 情况1 return 结果 c:" +c);
        return c;
      }

    } else if (type instanceof ParameterizedType) {
      ParameterizedType p = (ParameterizedType) type;
      Type 泛型 = null;
      Type 原始类型 = ((ParameterizedType) type).getRawType();
      if(((ParameterizedType) type).getActualTypeArguments().length>0){
        泛型 = ((ParameterizedType) type).getActualTypeArguments()[0];
      }

      Log.w(TAG_赋值, "<<<<----$Gson$Types canonicalize方法 情况2 参数 准备创建ParameterizedTypeImpl" +
              " " +
              "type:" +type+"  原始类型="+原始类型+"  泛型="+泛型);
      ParameterizedTypeImpl parameterizedType = new ParameterizedTypeImpl(p.getOwnerType(),
              p.getRawType(), p.getActualTypeArguments());
      Log.w(TAG_赋值, "---->>>$Gson$Types canonicalize方法 ParameterizedTypeImpl创建成功" +
              "(###########这里对理解非常重要，基本是一层套一层，最里层创建完了会回溯到TypeToken构造函数第二行#############)" +
              " " +
              "return情况2 结果" +
              "  " +
              "parameterizedType:"+parameterizedType);
      return parameterizedType;
    } else if (type instanceof GenericArrayType) {
      GenericArrayType g = (GenericArrayType) type;
      GenericArrayTypeImpl genericArrayType = new GenericArrayTypeImpl(g.getGenericComponentType());
      Log.e(TAG_赋值, "$Gson$Types canonicalize方法 情况3 结果 genericArrayType:" +genericArrayType);
      return genericArrayType;

    } else if (type instanceof WildcardType) {
      WildcardType w = (WildcardType) type;
      WildcardTypeImpl wildcardType = new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());
      Log.e(TAG_赋值, "$Gson$Types canonicalize方法 情况4 结果 wildcardType:" +wildcardType);
      return wildcardType;

    } else {
      // type is either serializable as-is or unsupported
      Log.e(TAG_赋值, "$Gson$Types canonicalize方法 情况5 直接返回type(例如T):"+type );
      return type;
    }
  }

  public static Class<?> getRawType(Type type) {

    if (type instanceof Class<?>) {
      // type is a normal class.
      Log.e(TAG_赋值 ,"$Gson$Types getRawType 情况1 参数 type:"+type );
      return (Class<?>) type;

    } else if (type instanceof ParameterizedType) {
      Log.e(TAG_赋值, "$Gson$Types getRawType 情况2 参数 type:"+type );
      ParameterizedType parameterizedType = (ParameterizedType) type;

      // I'm not exactly sure why getRawType() returns Type instead of Class.
      // Neal isn't either but suspects some pathological case related
      // to nested classes exists.
      Type rawType = parameterizedType.getRawType();
      checkArgument(rawType instanceof Class);
      return (Class<?>) rawType;

    } else if (type instanceof GenericArrayType) {
      Log.e(TAG_赋值, "$Gson$Types getRawType  情况3参数 type:"+type );
      Type componentType = ((GenericArrayType)type).getGenericComponentType();
      return Array.newInstance(getRawType(componentType), 0).getClass();

    } else if (type instanceof TypeVariable) {
      Log.e(TAG_赋值, "$Gson$Types getRawType (type 为T，返回Object) 情况4参数 type:"+type );
      // we could use the variable's bounds, but that won't work if there are multiple.
      // having a raw type that's more general than necessary is okay
      return Object.class;

    } else if (type instanceof WildcardType) {
      Log.e(TAG_赋值, "$Gson$Types getRawType (一般是type做参数) 情况5参数 type:"+type );
      return getRawType(((WildcardType) type).getUpperBounds()[0]);

    } else {
      String className = type == null ? "null" : type.getClass().getName();
      throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
          + "GenericArrayType, but <" + type + "> is of type " + className);
    }
  }

  static boolean equal(Object a, Object b) {
    return a == b || (a != null && a.equals(b));
  }

  /**
   * Returns true if {@code a} and {@code b} are equal.
   */
  public static boolean equals(Type a, Type b) {
    if (a == b) {
      // also handles (a == null && b == null)
      return true;

    } else if (a instanceof Class) {
      // Class already specifies equals().
      return a.equals(b);

    } else if (a instanceof ParameterizedType) {
      if (!(b instanceof ParameterizedType)) {
        return false;
      }

      // TODO: save a .clone() call
      ParameterizedType pa = (ParameterizedType) a;
      ParameterizedType pb = (ParameterizedType) b;
      return equal(pa.getOwnerType(), pb.getOwnerType())
          && pa.getRawType().equals(pb.getRawType())
          && Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments());

    } else if (a instanceof GenericArrayType) {
      if (!(b instanceof GenericArrayType)) {
        return false;
      }

      GenericArrayType ga = (GenericArrayType) a;
      GenericArrayType gb = (GenericArrayType) b;
      return equals(ga.getGenericComponentType(), gb.getGenericComponentType());

    } else if (a instanceof WildcardType) {
      if (!(b instanceof WildcardType)) {
        return false;
      }

      WildcardType wa = (WildcardType) a;
      WildcardType wb = (WildcardType) b;
      return Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds())
          && Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds());

    } else if (a instanceof TypeVariable) {
      if (!(b instanceof TypeVariable)) {
        return false;
      }
      TypeVariable<?> va = (TypeVariable<?>) a;
      TypeVariable<?> vb = (TypeVariable<?>) b;
      return va.getGenericDeclaration() == vb.getGenericDeclaration()
          && va.getName().equals(vb.getName());

    } else {
      // This isn't a type we support. Could be a generic array type, wildcard type, etc.
      return false;
    }
  }

  static int hashCodeOrZero(Object o) {
    return o != null ? o.hashCode() : 0;
  }

  public static String typeToString(Type type) {
    return type instanceof Class ? ((Class<?>) type).getName() : type.toString();
  }

  /**
   * Returns the generic supertype for {@code supertype}. For example, given a class {@code
   * IntegerSet}, the result for when supertype is {@code Set.class} is {@code Set<Integer>} and the
   * result when the supertype is {@code Collection.class} is {@code Collection<Integer>}.
   */
  //incorrectExample 返回
  //context com.darren.architect_day01.data.entity.Result<java.util.List<com.darren.architect_day01.data.entity.User>>
  //rawType:class com.darren.architect_day01.data.entity.Result
  //toResolve:class com.darren.architect_day01.data.entity.Result

  //correctExample 返回
  //context com.darren.architect_day01.data.entity.Result<java.util.List<com.darren.architect_day01.data.entity.User>>
  //rawType:class com.darren.architect_day01.data.entity.Result
  //toResolve:class com.darren.architect_day01.data.entity.Result
  static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
    //相等就直接返回
    Log.w("TAG", "$Gson$Types getGenericSupertype 参数 context:" +context+" rawType"+rawType+" " +
            "toResolve="+toResolve);
    if (toResolve == rawType) {
      int tag = Log.e("TAG", "$Gson$Types  getGenericSupertype return 第一种(toResolve == rawType) " +
              "context:" + context);
      return context;
    }
    // we skip searching through interfaces if unknown is an interface
    if (toResolve.isInterface()) {
      Class<?>[] interfaces = rawType.getInterfaces();
      for (int i = 0, length = interfaces.length; i < length; i++) {
        if (interfaces[i] == toResolve) {
          Type genericInterface = rawType.getGenericInterfaces()[i];
          Log.e("TAG",
                  "$Gson$Types getGenericSupertype return 第二种情况  genericInterface :" +genericInterface);
          return genericInterface;
        } else if (toResolve.isAssignableFrom(interfaces[i])) {
          Log.e("TAG", "$Gson$Types getGenericSupertype 第三种情况  递归调用 getGenericSupertype:" );
          Type genericSupertype =
                  getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
          Log.e("TAG",
                  "$Gson$Types getGenericSupertype return 第三种情况  genericSupertype :" +genericSupertype);
          return genericSupertype;
        }
      }
    }

    // check our supertypes
    if (!rawType.isInterface()) {
      while (rawType != Object.class) {
        Class<?> rawSupertype = rawType.getSuperclass();
        if (rawSupertype == toResolve) {
          Type genericSuperclass = rawType.getGenericSuperclass();
          Log.e("TAG",
                  "$Gson$Types getGenericSupertype return 第四种情况  genericSuperclass :" +genericSuperclass);
          return genericSuperclass;
        } else if (toResolve.isAssignableFrom(rawSupertype)) {
          Log.e("TAG", "$Gson$Types getGenericSupertype 第五种情况  递归调用 getGenericSupertype:" );
          Type genericSupertype =
                  getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve);
          Log.e("TAG",
                  "$Gson$Types getGenericSupertype return 第五种情况  genericSupertype :" +genericSupertype);
          return genericSupertype;
        }
        rawType = rawSupertype;
      }
    }
    Log.e("TAG",
            "$Gson$Types getGenericSupertype return 第六种情况  toResolve :" +toResolve);
    // we can't resolve this further
    return toResolve;
  }

  /**
   * Returns the generic form of {@code supertype}. For example, if this is {@code
   * ArrayList<String>}, this returns {@code Iterable<String>} given the input {@code
   * Iterable.class}.
   *
   * @param supertype a superclass of, or interface implemented by, this.
   */
  static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
    if (context instanceof WildcardType) {
      // wildcards are useless for resolving supertypes. As the upper bound has the same raw type, use it instead
      context = ((WildcardType)context).getUpperBounds()[0];
    }
    Log.e("TAG", "$Gson$Types getSupertype 接着会调用 <<$Gson$Types.getGenericSupertype>> context" +
            "(typeToken的Type):"+
    context+"  contextRawType(typeToken的rawType)="+contextRawType +" supertype="+supertype );
    Type toResolve = $Gson$Types.getGenericSupertype(context, contextRawType, supertype);
    Log.e("TAG",
            "$Gson$Types getSupertype  <<getGenericSupertype>> 调用完毕返回 toResolve" +
                    "(resolve的toResolve参数) " +
                    "="+toResolve+
            "  接着会调用<<reslove>> context(typeToken的Type):"+context+" contextRawType" +
                    "(typeToken的rawType)="+contextRawType );

    checkArgument(supertype.isAssignableFrom(contextRawType));
    Type resolve = resolve(context, contextRawType,
            toResolve);
    Log.e("TAG", "$Gson$Types getSupertype 调用后返回 resolve:"+resolve );
    return resolve;
  }

  /**
   * Returns the component type of this array type.
   * @throws ClassCastException if this type is not an array.
   */
  public static Type getArrayComponentType(Type array) {
    return array instanceof GenericArrayType
        ? ((GenericArrayType) array).getGenericComponentType()
        : ((Class<?>) array).getComponentType();
  }

  /**
   * Returns the element type of this collection type.
   * @throws IllegalArgumentException if this type is not a collection.
   */
  public static Type getCollectionElementType(Type context, Class<?> contextRawType) {
    Log.e("TAG",
            "$Gson$Types getCollectionElementType 接着会调用<<getSupertype>> 返回集合类型 contextRawType" +
                    "(typeToken.getRawType()):" +contextRawType);
    Type collectionType = getSupertype(context, contextRawType, Collection.class);
    Log.e("TAG",
            "$Gson$Types getCollectionElementType 调用getSupertype 返回的集合类型 collectionType:"+collectionType );
    if (collectionType instanceof WildcardType) {
      collectionType = ((WildcardType)collectionType).getUpperBounds()[0];
    }
    if (collectionType instanceof ParameterizedType) {
      return ((ParameterizedType) collectionType).getActualTypeArguments()[0];
    }
    return Object.class;
  }

  /**
   * Returns a two element array containing this map's key and value types in
   * positions 0 and 1 respectively.
   */
  public static Type[] getMapKeyAndValueTypes(Type context, Class<?> contextRawType) {
    /*
     * Work around a problem with the declaration of java.util.Properties. That
     * class should extend Hashtable<String, String>, but it's declared to
     * extend Hashtable<Object, Object>.
     */
    if (context == Properties.class) {
      return new Type[] { String.class, String.class }; // TODO: test subclasses of Properties!
    }

    Type mapType = getSupertype(context, contextRawType, Map.class);
    // TODO: strip wildcards?
    if (mapType instanceof ParameterizedType) {
      ParameterizedType mapParameterizedType = (ParameterizedType) mapType;
      return mapParameterizedType.getActualTypeArguments();
    }
    return new Type[] { Object.class, Object.class };
  }

  public static Type resolve(Type context, Class<?> contextRawType, Type toResolve) {
    return resolve(context, contextRawType, toResolve, new HashSet<TypeVariable>());
  }
  //incorrectSample：
  //context:com.darren.architect_day01.data.entity.Result<java.util.List<T>>
  //contextRawType:class com.darren.architect_day01.data.entity.Result
  //toResolve:T

  // //	Type只有五种类型：
  // //	Class:所代表的是一个确定的类，比如Integer,String,Double等
  // //	ParameterizedType:ParameterizedType代表完整的泛型表达式
  // //	TypeVariable:TypeVariable代表泛型变量的符号即T,U等
  // //	WildcardType:WildcardType代表通配符,<? extends Integer>,<? super String>,或者<?>等
  // //	GenericArrayType:GenericArrayType代表数组类型
  private static Type resolve(Type context, Class<?> contextRawType, Type toResolve,
                              Collection<TypeVariable> visitedTypeVariables) {
    // this implementation is made a little more complicated in an attempt to avoid object-creation
    Log.w("TAG", "$Gson$Types resolve 参数 context:" +context+" contextRawType="+contextRawType+" " +
            "toResolve(Bean的泛型类型)="+toResolve);
    while (true) {
      if (toResolve instanceof TypeVariable) {
        TypeVariable<?> typeVariable = (TypeVariable<?>) toResolve;
        //TypeToken的type不是一种不变的,例如Result<java.util.List<User>> ,第一次是Result<java.util
        // .List<User>>，第二次是.List<User>
        Log.e("TAG",
                "$Gson$Types  <<resolve>>情况1 instanceof TypeVariable(泛型变量的符号即T,U等) context(TypeToken的type)" +
                        ":"+context+
                        "  contextRawType(外层)"+contextRawType+"   toResolve（T或者E）="+toResolve);
        if (visitedTypeVariables.contains(typeVariable)) {
          // cannot reduce due to infinite recursion
          Log.e("TAG", "$Gson$Types resolve  return情况1(条件visitedTypeVariables.contains" +
                  "(typeVariable))" +
                  ":" );
          return toResolve;
        } else {
          visitedTypeVariables.add(typeVariable);
        }
        Log.e("TAG", "$Gson$Types resolve 情况1(TypeVariable泛型变量的符号即T,U等)  准备调用<<resolveTypeVariable>>开始:" );
        toResolve = resolveTypeVariable(context, contextRawType, typeVariable);
        Log.e("TAG", "$Gson$Types resolve 情况1 调用<<resolveTypeVariable>>返回的 toResolve:" +toResolve);
        if (toResolve == typeVariable) {
          Log.e("TAG",
                  "********$Gson$Types candidate resolve return情况1 toResolve == typeVariable:"+toResolve);
          return toResolve;
        }else{
          Log.e("TAG", "$Gson$Types 情况1 继续调用resolve toResolve:" +toResolve);
        }

      } else if (toResolve instanceof Class && ((Class<?>) toResolve).isArray()) {
        Class<?> original = (Class<?>) toResolve;
        Type componentType = original.getComponentType();
        Log.e("TAG", "$Gson$Types resolve 情况2（toResolve instanceof Class && ((Class<?>) " +
                "toResolve).isArray()）调用resolve:"+toResolve );
        Type newComponentType = resolve(context, contextRawType, componentType, visitedTypeVariables);
        Log.e("TAG", "$Gson$Types resolve 情况2调用resolve 返回 newComponentType:"+newComponentType );

        Type type = componentType == newComponentType
                ? original
                : arrayOf(newComponentType);
        Log.e("TAG", "$Gson$Types resolve return 情况2 type:" +type);
        return type;
      } else if (toResolve instanceof GenericArrayType) {
        GenericArrayType original = (GenericArrayType) toResolve;
        Type componentType = original.getGenericComponentType();
        Log.e("TAG",
                "$Gson$Types resolve (GenericArrayType 代表数组类型)情况3调用resolve:" +toResolve);
        Type newComponentType = resolve(context, contextRawType, componentType, visitedTypeVariables);
        Log.e("TAG", "$Gson$Types resolve 情况3调用resolve 返回 newComponentType:"+newComponentType );
        GenericArrayType genericArrayType = componentType == newComponentType
                ? original
                : arrayOf(newComponentType);
        Log.e("TAG", "$Gson$Types resolve return情况3  genericArrayType:" +genericArrayType);
        return genericArrayType;
      } else if (toResolve instanceof ParameterizedType) {
        ParameterizedType original = (ParameterizedType) toResolve;
        Type ownerType = original.getOwnerType();
        Log.e("TAG", "$Gson$Types resolve （ParameterizedType 完整的泛型表达式）情况4调用resolve:"+toResolve+" " +
                "但是此时传入的reslove参数是=" +ownerType);
        Type newOwnerType = resolve(context, contextRawType, ownerType, visitedTypeVariables);
        Log.e("TAG", "$Gson$Types resolve 情况4调用resolve 返回 newOwnerType:"+newOwnerType );
        boolean changed = newOwnerType != ownerType;

        Type[] args = original.getActualTypeArguments();
        Log.w("TAG", "$Gson$Types resolve 情况4 args( original.getActualTypeArguments):" +args);
        for (int t = 0, length = args.length; t < length; t++) {
          Log.e("TAG",
                  "$Gson$Types resolve 情况4 for中调用resolve t:"+t+" args[t]（其实是resolve参数）="+args[t] );
          Type resolvedTypeArgument = resolve(context, contextRawType, args[t], visitedTypeVariables);
          Log.e("TAG", "$Gson$Types resolve 情况4 for中调用resolve 返回 t:"+t+"  resolvedTypeArgument="+resolvedTypeArgument );
          if (resolvedTypeArgument != args[t]) {
            if (!changed) {
              args = args.clone();
              changed = true;
            }
            args[t] = resolvedTypeArgument;
            Log.e("TAG", "$Gson$Types resolve 情况4 返回后 因为(resolvedTypeArgument != args[t]) 更新 " +
                    "args[t]:" );
          }
        }

        ParameterizedType parameterizedType = changed
                ? newParameterizedTypeWithOwner(newOwnerType, original.getRawType(), args)
                : original;
        if(changed){
          Log.e("TAG",
                  "$Gson$Types resolve return情况4 changed 为true parameterizedType:" +parameterizedType);
        }else{
          Log.e("TAG",
                  "$Gson$Types resolve return情况4 changed 为false（parameterizedType其实是最初传进来的） " +
                          "parameterizedType:" +parameterizedType);
        }

        return parameterizedType;
      } else if (toResolve instanceof WildcardType) {
        WildcardType original = (WildcardType) toResolve;
        Type[] originalLowerBound = original.getLowerBounds();
        Type[] originalUpperBound = original.getUpperBounds();

        if (originalLowerBound.length == 1) {
          Log.e("TAG", "$Gson$Types resolve （WildcardType代表通配符,<? extends Integer>）情况5 中调用resolve :" );
          Type lowerBound = resolve(context, contextRawType, originalLowerBound[0], visitedTypeVariables);
          Log.e("TAG", "$Gson$Types resolve 情况5 中调用resolve 返回 lowerBound:"+lowerBound );

          if (lowerBound != originalLowerBound[0]) {
            WildcardType wildcardType = supertypeOf(lowerBound);
            Log.e("TAG", "$Gson$Types resolve return 情况5 wildcardType:" +wildcardType);
            return wildcardType;
          }
        } else if (originalUpperBound.length == 1) {
          Log.e("TAG", "$Gson$Types resolve 情况5 中调用 originalUpperBound.length == 1 resolve :" );
          Type upperBound = resolve(context, contextRawType, originalUpperBound[0], visitedTypeVariables);
          Log.e("TAG", "$Gson$Types resolve 情况5 中调用 originalUpperBound.length == 1 调用resolve 返回 upperBound" +upperBound);

          if (upperBound != originalUpperBound[0]) {
            WildcardType wildcardType = subtypeOf(upperBound);
            Log.e("TAG", "$Gson$Types resolve return 情况6 wildcardType:" +wildcardType);
            return wildcardType;
          }
        }
        Log.e("TAG", "$Gson$Types resolve return 情况7 original:"+original );
        return original;

      } else {
        Log.e("TAG", "$Gson$Types resolve 直接return情况8 toResolve:"+toResolve );
        return toResolve;
      }
    }
  }
  //incorrect:
  //context:Result<java.util.List<T>> (TypeToken的type)
  //  contextRawType:class Result  unknown:T

  //correct:
  // context:Result<java.util.List<com.darren.architect_day01.data.entity.User>> (TypeToken的type)
  //  contextRawType:class Result  unknown:T
  static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
    //T是属于哪个类，这里是Result
    Class<?> declaredByRaw = declaringClassOf(unknown);
  Log.e("TAG",
          "$Gson$Types resolveTypeVariable 参数 context:"+context+" contextRawType="+contextRawType+"  unknown="+unknown );
    // we can't reduce this further
    if (declaredByRaw == null) {
      Log.e("TAG", "$Gson$Types resolveTypeVariable 返回情况0:" );
      return unknown;
    }
//这两个例子都把context返回了
    Log.w("TAG",
            "$Gson$Types resolveTypeVariable 准备调用<<getGenericSupertype>> 此时 toResolve参数为:"+declaredByRaw );
    Type declaredBy = getGenericSupertype(context, contextRawType, declaredByRaw);
    Log.w("TAG", "$Gson$Types resolveTypeVariable调用<<getGenericSupertype>>后返回 declaredBy:"+declaredBy );
    if (declaredBy instanceof ParameterizedType) {
      int index = indexOf(declaredByRaw.getTypeParameters(), unknown);
      Type actualTypeArgument = ((ParameterizedType) declaredBy).getActualTypeArguments()[index];
      Log.e("TAG", "$Gson$Types resolveTypeVariable 返回情况1 actualTypeArgument:"+actualTypeArgument );
      return actualTypeArgument;
    }
    Log.e("TAG", "$Gson$Types resolveTypeVariable 返回情况2 unknown:"+unknown );

    return unknown;
  }

  private static int indexOf(Object[] array, Object toFind) {
    for (int i = 0, length = array.length; i < length; i++) {
      if (toFind.equals(array[i])) {
        return i;
      }
    }
    throw new NoSuchElementException();
  }

  /**
   * Returns the declaring class of {@code typeVariable}, or {@code null} if it was not declared by
   * a class.
   */
  private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
    GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
    return genericDeclaration instanceof Class
        ? (Class<?>) genericDeclaration
        : null;
  }

  static void checkNotPrimitive(Type type) {
    checkArgument(!(type instanceof Class<?>) || !((Class<?>) type).isPrimitive());
  }

  private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
    private final Type ownerType;
    private final Type rawType;
    private final Type[] typeArguments;

    public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... typeArguments) {
      // require an owner type if the raw type needs it
      Log.w("TAG", "ParameterizedTypeImpl 传入的参数 ownerType:"+ownerType+" rawType" +
              "="+rawType+"  typeArguments(实际泛型类)="+(typeArguments!=null?typeArguments[0]:"空") );
      if (rawType instanceof Class<?>) {
        Class<?> rawTypeAsClass = (Class<?>) rawType;
        boolean isStaticOrTopLevelClass = Modifier.isStatic(rawTypeAsClass.getModifiers())
            || rawTypeAsClass.getEnclosingClass() == null;
        checkArgument(ownerType != null || isStaticOrTopLevelClass);
      }


      if(ownerType == null){
        this.ownerType = null;
      }else{
        Log.e("TAG",
                "ParameterizedTypeImpl  调用canonicalize传入 ownerType  :"+ownerType );
        this.ownerType = canonicalize(ownerType);
      }
      Log.w("TAG", "ParameterizedTypeImpl 创建成功并返回this.ownerType:"+this.ownerType );
      Log.e("TAG",
              "ParameterizwedTypeImpl  调用canonicalize传入 rawType :"+rawType );
      this.rawType = canonicalize(rawType);
      Log.w("TAG", "ParameterizedTypeImpl 创建成功并返回this.rawType:"+this.rawType );
      this.typeArguments = typeArguments.clone();
      for (int t = 0, length = this.typeArguments.length; t < length; t++) {
        checkNotNull(this.typeArguments[t]);
        checkNotPrimitive(this.typeArguments[t]);
        Log.w("TAG",
                "ParameterizedTypeImpl  [注意这里是for,这里有可能递归] 调用 canonicalize  t:"+t+"  传入"+
                        "  typeArguments[t]"
                       + typeArguments[t] );
        this.typeArguments[t] = canonicalize(this.typeArguments[t]);
        Log.e("TAG", "ParameterizedTypeImpl ParameterizedTypeImpl" +
                "(canonicalize调用返回的有可能也是一个ParameterizedTypeImpl) " +
                "递归结束一次(最里面的最先结束，这里是T或者User)---------------------this.typeArguments[t]:" +this.typeArguments[t]);
      }
      Log.e("TAG", "ParameterizedTypeImpl [ParameterizedTypeImpl  实例化完成] " +
              "设置完成后rawType:"+rawType+"  " +
              "typeArguments[t]  "+(typeArguments.length>0?typeArguments[0]:"空空如也") );
    }

    public Type[] getActualTypeArguments() {
      return typeArguments.clone();
    }

    public Type getRawType() {
      return rawType;
    }

    public Type getOwnerType() {
      return ownerType;
    }

    @Override public boolean equals(Object other) {
      return other instanceof ParameterizedType
          && $Gson$Types.equals(this, (ParameterizedType) other);
    }

    @Override public int hashCode() {
      return Arrays.hashCode(typeArguments)
          ^ rawType.hashCode()
          ^ hashCodeOrZero(ownerType);
    }

    @Override public String toString() {
      int length = typeArguments.length;
      if (length == 0) {
        return typeToString(rawType);
      }

      StringBuilder stringBuilder = new StringBuilder(30 * (length + 1));
      stringBuilder.append(typeToString(rawType)).append("<").append(typeToString(typeArguments[0]));
      for (int i = 1; i < length; i++) {
        stringBuilder.append(", ").append(typeToString(typeArguments[i]));
      }
      return stringBuilder.append(">").toString();
    }

    private static final long serialVersionUID = 0;
  }

  private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {
    private final Type componentType;

    public GenericArrayTypeImpl(Type componentType) {
      this.componentType = canonicalize(componentType);
    }

    public Type getGenericComponentType() {
      return componentType;
    }

    @Override public boolean equals(Object o) {
      return o instanceof GenericArrayType
          && $Gson$Types.equals(this, (GenericArrayType) o);
    }

    @Override public int hashCode() {
      return componentType.hashCode();
    }

    @Override public String toString() {
      return typeToString(componentType) + "[]";
    }

    private static final long serialVersionUID = 0;
  }

  /**
   * The WildcardType interface supports multiple upper bounds and multiple
   * lower bounds. We only support what the Java 6 language needs - at most one
   * bound. If a lower bound is set, the upper bound must be Object.class.
   */
  private static final class WildcardTypeImpl implements WildcardType, Serializable {
    private final Type upperBound;
    private final Type lowerBound;

    public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
      checkArgument(lowerBounds.length <= 1);
      checkArgument(upperBounds.length == 1);

      if (lowerBounds.length == 1) {
        checkNotNull(lowerBounds[0]);
        checkNotPrimitive(lowerBounds[0]);
        checkArgument(upperBounds[0] == Object.class);
        this.lowerBound = canonicalize(lowerBounds[0]);
        this.upperBound = Object.class;

      } else {
        checkNotNull(upperBounds[0]);
        checkNotPrimitive(upperBounds[0]);
        this.lowerBound = null;
        this.upperBound = canonicalize(upperBounds[0]);
      }
    }

    public Type[] getUpperBounds() {
      return new Type[] { upperBound };
    }

    public Type[] getLowerBounds() {
      return lowerBound != null ? new Type[] { lowerBound } : EMPTY_TYPE_ARRAY;
    }

    @Override public boolean equals(Object other) {
      return other instanceof WildcardType
          && $Gson$Types.equals(this, (WildcardType) other);
    }

    @Override public int hashCode() {
      // this equals Arrays.hashCode(getLowerBounds()) ^ Arrays.hashCode(getUpperBounds());
      return (lowerBound != null ? 31 + lowerBound.hashCode() : 1)
          ^ (31 + upperBound.hashCode());
    }

    @Override public String toString() {
      if (lowerBound != null) {
        return "? super " + typeToString(lowerBound);
      } else if (upperBound == Object.class) {
        return "?";
      } else {
        return "? extends " + typeToString(upperBound);
      }
    }

    private static final long serialVersionUID = 0;
  }
}
