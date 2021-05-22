package com.geng.imooc_voice.application;

import android.app.Application;

public class ImoocVoiceApplication extends Application {

    private static ImoocVoiceApplication mApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static ImoocVoiceApplication getInstance() {
        return mApplication;
    }
}
