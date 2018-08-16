package com.darren.architect_day13;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new  ReportingTree());
        // 但是打印是 low 得不行，所以我们一般需要自己扩展，配合我们的 Logger 一起
        Timber.tag("LifeCycles");
        Timber.e("Activity Created");
    }

    private static class ReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
           Log.e(tag,message);
            // 保存到 本地 文件
        }
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            // 保存到 本地 文件
        }
    }
}
