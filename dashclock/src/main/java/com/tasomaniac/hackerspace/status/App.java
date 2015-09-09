package com.tasomaniac.hackerspace.status;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class App extends Application {
    private HackDashComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        buildComponentAndInject();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashReportingTree());
        }
    }

    public void buildComponentAndInject() {
        component = HackDashComponent.Initializer.init(this);
        component.inject(this);
    }

    public HackDashComponent component() {
        return component;
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }


    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            Crashlytics.log(priority, tag, message);
            if (t != null && priority >= Log.WARN) {
                Crashlytics.logException(t);
            }
        }
    }
}
