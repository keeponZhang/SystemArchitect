/*
 * Copyright (C) 2012-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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
package org.greenrobot.eventbus;

import android.util.Log;

import org.greenrobot.eventbus.meta.SubscriberInfo;
import org.greenrobot.eventbus.meta.SubscriberInfoIndex;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SubscriberMethodFinder {
    /*
     * In newer class files, compilers may add methods. Those are called bridge or synthetic methods.
     * EventBus must ignore both. There modifiers are not public but defined in the Java class file format:
     * http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.6-200-A.1
     */
    private static final int BRIDGE = 0x40;
    private static final int SYNTHETIC = 0x1000;

    private static final int MODIFIERS_IGNORE = Modifier.ABSTRACT | Modifier.STATIC | BRIDGE | SYNTHETIC;
    private static final Map<Class<?>, List<SubscriberMethod>> METHOD_CACHE = new ConcurrentHashMap<>();

    private List<SubscriberInfoIndex> subscriberInfoIndexes;
    private final boolean strictMethodVerification;
    private final boolean ignoreGeneratedIndex;

    private static final int POOL_SIZE = 4;
    private static final FindState[] FIND_STATE_POOL = new FindState[POOL_SIZE];

    SubscriberMethodFinder(List<SubscriberInfoIndex> subscriberInfoIndexes, boolean strictMethodVerification,
                           boolean ignoreGeneratedIndex) {
        this.subscriberInfoIndexes = subscriberInfoIndexes;
        this.strictMethodVerification = strictMethodVerification;
        this.ignoreGeneratedIndex = ignoreGeneratedIndex;
    }

    List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        //首先从缓存中取出subscriberMethodss，如果有则直接返回该已取得的方法
        List<SubscriberMethod> subscriberMethods = METHOD_CACHE.get(subscriberClass);
        if (subscriberMethods != null) {
            return subscriberMethods;
        }

        //从EventBusBuilder可知，ignoreGenerateIndex一般为false
        if (ignoreGeneratedIndex) {
            subscriberMethods = findUsingReflection(subscriberClass);
        } else {
            subscriberMethods = findUsingInfo(subscriberClass);
        }
        if (subscriberMethods.isEmpty()) {
            throw new EventBusException("Subscriber " + subscriberClass
                    + " and its super classes have no public methods with the @Subscribe annotation");
        } else {
            METHOD_CACHE.put(subscriberClass, subscriberMethods);
            return subscriberMethods;
        }
    }

    private List<SubscriberMethod> findUsingInfo(Class<?> subscriberClass) {
        //准备一个FindState，该FindState保存了订阅者类的信息
        FindState findState = prepareFindState();
        //对FindState初始化
        findState.initForSubscriber(subscriberClass);
        while (findState.clazz != null) {
            //获得订阅者的信息，一开始会返回null
            findState.subscriberInfo = getSubscriberInfo(findState);
            if (findState.subscriberInfo != null) {
                SubscriberMethod[] array = findState.subscriberInfo.getSubscriberMethods();
                for (SubscriberMethod subscriberMethod : array) {
                    if (findState.checkAdd(subscriberMethod.method, subscriberMethod.eventType)) {
                        findState.subscriberMethods.add(subscriberMethod);
                    }
                }
            } else {
                //1 、到了这里
                findUsingReflectionInSingleClass(findState);
            }
            //移动到父类继续查找
            findState.moveToSuperclass();
        }
        return getMethodsAndRelease(findState);
    }

    private List<SubscriberMethod> getMethodsAndRelease(FindState findState) {
	    //从findState获取subscriberMethods，放进新的ArrayList
        List<SubscriberMethod> subscriberMethods = new ArrayList<>(findState.subscriberMethods);
	    //把findState回收
	    findState.recycle();
        synchronized (FIND_STATE_POOL) {
            for (int i = 0; i < POOL_SIZE; i++) {
                if (FIND_STATE_POOL[i] == null) {
                    FIND_STATE_POOL[i] = findState;
                    break;
                }
            }
        }
        return subscriberMethods;
    }

    private FindState prepareFindState() {
        synchronized (FIND_STATE_POOL) {
            for (int i = 0; i < POOL_SIZE; i++) {
                FindState state = FIND_STATE_POOL[i];
                if (state != null) {
                    FIND_STATE_POOL[i] = null;
                    return state;
                }
            }
        }
        return new FindState();
    }

    private SubscriberInfo getSubscriberInfo(FindState findState) {
        if (findState.subscriberInfo != null && findState.subscriberInfo.getSuperSubscriberInfo() != null) {
            SubscriberInfo superclassInfo = findState.subscriberInfo.getSuperSubscriberInfo();
            if (findState.clazz == superclassInfo.getSubscriberClass()) {
                return superclassInfo;
            }
        }
        if (subscriberInfoIndexes != null) {
            for (SubscriberInfoIndex index : subscriberInfoIndexes) {
                SubscriberInfo info = index.getSubscriberInfo(findState.clazz);
                if (info != null) {
                    return info;
                }
            }
        }
        return null;
    }

    private List<SubscriberMethod> findUsingReflection(Class<?> subscriberClass) {
        FindState findState = prepareFindState();
        findState.initForSubscriber(subscriberClass);
        while (findState.clazz != null) {
            findUsingReflectionInSingleClass(findState);
            findState.moveToSuperclass();
        }
        return getMethodsAndRelease(findState);
    }

    private void findUsingReflectionInSingleClass(FindState findState) {
        Method[] methods;
        try {
            // This is faster than getMethods, especially when subscribers are fat classes like Activities
            methods = findState.clazz.getDeclaredMethods();
        } catch (Throwable th) {
            // Workaround for java.lang.NoClassDefFoundError, see https://github.com/greenrobot/EventBus/issues/149
            methods = findState.clazz.getMethods();
            findState.skipSuperClasses = true;
        }
        for (Method method : methods) {
            //获取方法的修饰符
            int modifiers = method.getModifiers();
            if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & MODIFIERS_IGNORE) == 0) {
                //获取方法的参数类型
                Class<?>[] parameterTypes = method.getParameterTypes();
                //如果参数个数为一个，则继续
                if (parameterTypes.length == 1) {
                    //获取该方法的@Subscribe注解
                    Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
                    if (subscribeAnnotation != null) {
                        //参数类型 即为 事件类型
                        Class<?> eventType = parameterTypes[0];
                        // 2 、调用checkAdd方法
                        if (findState.checkAdd(method, eventType)) {
                            //从注解中提取threadMode
                            ThreadMode threadMode = subscribeAnnotation.threadMode();
                            //新建一个SubscriberMethod对象，并添加到findState的subscriberMethods这个集合内
                            findState.subscriberMethods.add(new SubscriberMethod(method, eventType, threadMode,
                                    subscribeAnnotation.priority(), subscribeAnnotation.sticky()));
                        }
                    }
                    //如果开启了严格验证，同时当前方法又有@Subscribe注解，对不符合要求的方法会抛出异常
                } else if (strictMethodVerification && method.isAnnotationPresent(Subscribe.class)) {
                    String methodName = method.getDeclaringClass().getName() + "." + method.getName();
                    throw new EventBusException("@Subscribe method " + methodName +
                            "must have exactly 1 parameter but has " + parameterTypes.length);
                }
            } else if (strictMethodVerification && method.isAnnotationPresent(Subscribe.class)) {
                String methodName = method.getDeclaringClass().getName() + "." + method.getName();
                throw new EventBusException(methodName +
                        " is a illegal @Subscribe method: must be public, non-static, and non-abstract");
            }
        }
    }

    static void clearCaches() {
        METHOD_CACHE.clear();
    }

    static class FindState {
        //订阅方法的列表
        final List<SubscriberMethod> subscriberMethods = new ArrayList<>();
        //以event为key，以method为value
        final Map<Class, Object> anyMethodByEventType = new HashMap<>();
        //以method的名字生成一个method为key，以订阅者类为value
        final Map<String, Class> subscriberClassByMethodKey = new HashMap<>();
        final StringBuilder methodKeyBuilder = new StringBuilder(128);

        Class<?> subscriberClass;
        Class<?> clazz;
        boolean skipSuperClasses;
        SubscriberInfo subscriberInfo;

        void initForSubscriber(Class<?> subscriberClass) {
            this.subscriberClass = clazz = subscriberClass;
            skipSuperClasses = false;
            subscriberInfo = null;
        }

        void recycle() {
            subscriberMethods.clear();
            anyMethodByEventType.clear();
            subscriberClassByMethodKey.clear();
            methodKeyBuilder.setLength(0);
            subscriberClass = null;
            clazz = null;
            skipSuperClasses = false;
            subscriberInfo = null;
        }
        //checkAdd和checkAddWithMethodSignature允许一个类有多个参数相同的订阅方法
        boolean checkAdd(Method method, Class<?> eventType) {
            // 2 level check: 1st level with event type only (fast), 2nd level with complete signature when required.
            // Usually a subscriber doesn't have methods listening to the same event type.
            Object existing = anyMethodByEventType.put(eventType, method);
//            首先通过anyMethodByEventType.put(eventType, method) 将eventType以及method放进anyMethodByEventType这个Map中(上面提到),同时该put方法会返回同一个key的上一个value值，
            // 所以如果之前没有别的方法订阅了该事件，那么existing应该为null，可以直接返回true(大多数是这个情况)
            if (existing == null) {
                Log.e(TAG, "checkAdd existing ==null:"+eventType);
                return true;
            } else {
//                否则为某一个订阅方法的实例，要进行下一步的判断。接着往下走，会调用checkAddWithMethodSignature()方法。
                if (existing instanceof Method) {
                    if (!checkAddWithMethodSignature((Method) existing, eventType)) {
                        // Paranoia check
                        throw new IllegalStateException();
                    }
                    // Put any non-Method object to "consume" the existing Method
//                    处理一个对象有多个订阅函数处理eventType的情况，此时，anyMethodByEventType中eventType被映射到一个非Method对象(即this)。
                    anyMethodByEventType.put(eventType, this);
                }
                return checkAddWithMethodSignature(method, eventType);
            }
        }

        private static final String TAG = "FindState";
        private boolean checkAddWithMethodSignature(Method method, Class<?> eventType) {
            methodKeyBuilder.setLength(0);
            methodKeyBuilder.append(method.getName());
            methodKeyBuilder.append('>').append(eventType.getName());
            //methodKey由方法名与事件名组成
            String methodKey = methodKeyBuilder.toString();
            //获取当前方法所在的类的类名
            Class<?> methodClass = method.getDeclaringClass();
            //给subscriberClassByMethodKey赋值，并返回上一个相同key的 类名
            Class<?> methodClassOld = subscriberClassByMethodKey.put(methodKey, methodClass);
            //判断子父类a.isAssignableFrom(b) a是b的超类
            //这里处理同一个类，不同方法订阅同一种事件类型
            if (methodClassOld == null || methodClassOld.isAssignableFrom(methodClass)) {
                Log.e(TAG, "checkAddWithMethodSignature if:"+(methodClassOld == null)+"  methodKey="+methodKey+"  methodClass="+methodClass);
                // Only add if not already found in a sub class
                return true;
            } else {
                //走到这里，是要处理字父类方法重写(前面被父类的methodClass覆盖掉了，这里要覆盖回来)，同时返回false，父类这个subscriberMethod方法就不处理了
                Log.e(TAG, "checkAddWithMethodSignature else methodClassOld:"+methodClassOld+"  methodKey="+methodKey+"  methodClassOld="+methodClassOld);
                // Revert the put, old class is further down the class hierarchy
                subscriberClassByMethodKey.put(methodKey, methodClassOld);
                return false;
            }
        }

        void moveToSuperclass() {
            if (skipSuperClasses) {
                clazz = null;
            } else {
                clazz = clazz.getSuperclass();
                String clazzName = clazz.getName();
                /** Skip system classes, this just degrades performance. */
                if (clazzName.startsWith("java.") || clazzName.startsWith("javax.") || clazzName.startsWith("android.")) {
                    clazz = null;
                }
            }
        }
    }

}
