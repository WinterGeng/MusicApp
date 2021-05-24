package com.geng.lib_network.okhttp.response;

import com.geng.lib_network.okhttp.exception.OkHttpException;
import com.geng.lib_network.okhttp.listener.DisposeDataHandle;
import com.geng.lib_network.okhttp.utils.ResponseEntityToModule;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author vision
 * @function 专门处理JSON的回调
 */
    public class CommonJsonCallback extends CommonCallBack<Void,String> {

    protected final int JSON_ERROR = -2; // the JSON relative error

    /**
     * 将其它线程的数据转发到UI线程
     */
    private Class<?> mClass; //转成哪个类的对象

    public CommonJsonCallback(DisposeDataHandle handle) {
        super(handle);
        this.mClass = handle.mClass;
    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        final String result = response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    @Override
    public Void handleResponse(String result) {
        if (result == null || result.trim().equals("")) {
            mListener.onFailure(new OkHttpException(NETWORK_ERROR, EMPTY_MSG));
            return null;
        }
        try {
            if (mClass == null) {
                mListener.onSuccess(result);
            } else {
                Object obj = ResponseEntityToModule.parseJsonToModule(result, mClass);
                if (obj != null) {
                    mListener.onSuccess(obj);
                } else {
                    mListener.onFailure(new OkHttpException(JSON_ERROR, EMPTY_MSG));
                }
            }
        } catch (Exception e) {
            mListener.onFailure(new OkHttpException(JSON_ERROR, EMPTY_MSG));
        }
        return null;
    }
}