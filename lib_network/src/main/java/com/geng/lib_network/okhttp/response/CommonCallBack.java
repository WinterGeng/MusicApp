package com.geng.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;

import com.geng.lib_network.okhttp.exception.OkHttpException;
import com.geng.lib_network.okhttp.listener.DisposeDataHandle;
import com.geng.lib_network.okhttp.listener.DisposeDataListener;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class CommonCallBack<T,F> implements Callback {

    public DisposeDataListener mListener;
    public Handler mDeliveryHandler;

    public final int NETWORK_ERROR = -1; // the network relative error
    public final String EMPTY_MSG = "";

    public CommonCallBack(DisposeDataHandle handle) {
        this.mListener = handle.mListener;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(Call call, IOException e) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, e));
            }
        });
    }

    @Override
    public abstract void onResponse(Call call, Response response) throws IOException;

    public abstract T handleResponse(F result);
}
