package com.geng.lib_audio.app;

import android.content.Context;

//唯一与外界通信的帮助类
public class AudioHelper {
    public static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}
