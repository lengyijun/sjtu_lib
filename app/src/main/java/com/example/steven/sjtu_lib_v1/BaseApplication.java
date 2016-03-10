package com.example.steven.sjtu_lib_v1;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

/**
 * Created by steven on 2016/3/10.
 */
public class BaseApplication extends Application {

    protected static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
        mContext = getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
