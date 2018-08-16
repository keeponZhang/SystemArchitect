package com.darren.architect_day08.manager;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by hcDarren on 2017/9/17.
 */

public class ActivityManager {
    private static volatile ActivityManager mInstance;
    // 集合用谁 List LinkedList Stack  ?? 删除和添加比较多
    private Stack<Activity> mActivities;

    private ActivityManager(){
        mActivities = new Stack<>();
    }

    // 虽说解决了线程安全的问题，但是又会出现效率的问题，
    // 即保证线程的安全同是效率也是比较高的
    // 这种方式其实还是会有问题？
    public static ActivityManager getInstance() {
        if (mInstance == null) {
            synchronized (ActivityManager.class) {
                if (mInstance == null) {
                    mInstance = new ActivityManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 添加统一管理
     * @param activity
     */
    public void attach(Activity activity){
        mActivities.add(activity);
    }

    /**
     * 移除解绑 - 防止内存泄漏
     * @param detachActivity
     */
    public void detach(Activity detachActivity){
        // for 去移除有没有问题？ 一边循环一边移除会出问题 ，
        // 既然这个写法有问题，自己又想不到什么解决方法，参考一下别人怎么写的
        /*for (Activity activity : mActivities) {
            if(activity == detachActivity){
                mActivities.remove(activity);
            }
        }*/
        int size = mActivities.size();
        for (int i = 0; i < size; i++) {
            Activity activity = mActivities.get(i);
            if (activity == detachActivity) {
                mActivities.remove(i);
                i--;
                size--;
            }
        }
    }

    /**
     * 关闭当前的 Activity
     * @param finishActivity
     */
    public void finish(Activity finishActivity){
        // for 去移除有没有问题？
        /*for (Activity activity : mActivities) {
            if(activity == finishActivity){
                mActivities.remove(activity);
                activity.finish();
            }
        }
*/
        int size = mActivities.size();
        for (int i = 0; i < size; i++) {
            Activity activity = mActivities.get(i);
            if (activity == finishActivity) {
                mActivities.remove(i);
                activity.finish();
                i--;
                size--;
            }
        }
    }

    /**
     * 根据Activity的类名关闭 Activity
     */
    public void finish(Class<? extends Activity> activityClass){
        // for 去移除有没有问题？
        /*for (Activity activity : mActivities) {
            if(activity.getClass().getCanonicalName().equals(activityClass.getCanonicalName())){
                mActivities.remove(activity);
                activity.finish();
            }
        }*/

        int size = mActivities.size();
        for (int i = 0; i < size; i++) {
            Activity activity = mActivities.get(i);
            if (activity.getClass().getCanonicalName().equals(activityClass.getCanonicalName())) {
                mActivities.remove(i);
                activity.finish();
                i--;
                size--;
            }
        }
    }

    /**
     * 退出整个应用
     */
    public void exitApplication(){

    }

    /**
     * 获取当前的Activity（最前面）
     * @return
     */
    public Activity currentActivity(){
        return mActivities.lastElement();
    }
}
