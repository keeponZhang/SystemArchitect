package com.darren.architect_day_15.simple4;

import java.util.*;

/**
 * Created by hcDarren on 2017/10/14.
 * 被观察者
 */
public class Observable<M,T extends Observer<M>> {
    private List<T> observables;

    public Observable(){
        observables = new ArrayList<>();
    }

    public void register(T observer){
        observables.add(observer);
    }

    public void unregister(T observer){
        observables.remove(observer);
    }

    public void addUpdate(M m){
        for (T observable : observables) {
            observable.update(m);
        }
    }
}
