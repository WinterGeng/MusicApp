package com.geng.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.geng.lib_network.okhttp.exception.OkHttpException;
import com.geng.lib_network.okhttp.listener.DisposeDataHandle;
import com.geng.lib_network.okhttp.listener.DisposeDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @文件描述：专门处理文件下载回调
 */
public class CommonFileCallback extends CommonCallBack<File,Response> {

    protected final int IO_ERROR = -2; // the JSON relative error

    /**
     * 将其它线程的数据转发到UI线程
     */
    private static final int PROGRESS_MESSAGE = 0x01;
    private Handler mDeliveryHandler;
    private String mFilePath;
    private int mProgress;
    private DisposeDownloadListener mListener; //请求成功失败回调

    public CommonFileCallback(DisposeDataHandle handle) {
        super(handle);
        this.mListener = (DisposeDownloadListener) handle.mListener;
        this.mFilePath = handle.mSource;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case PROGRESS_MESSAGE:
                        mListener.onProgress((Integer) msg.obj);
                        break;
                }
            }
        };
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final File file = handleResponse(response);
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                if (file != null) {
                    mListener.onSuccess(file);
                } else {
                    mListener.onFailure(new OkHttpException(IO_ERROR, EMPTY_MSG));
                }
            }
        });
    }

    @Override
    public File handleResponse(Response result) {
        if (result == null) {
            return null;
        }
        InputStream inputStream = null;
        File file = null;
        FileOutputStream fos = null;
        byte[] buffer = new byte[2048];
        int length;
        double currentLeng = 0;
        double sumLength;
        try {
            checkLocalFilePath(mFilePath);
            file = new File(mFilePath);
            fos = new FileOutputStream(file);
            inputStream = result.body().byteStream();
            sumLength = result.body().contentLength();
            while ((length = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, buffer.length);
                currentLeng += length;
                mProgress = (int) (currentLeng / sumLength * 100);
                mDeliveryHandler.obtainMessage(PROGRESS_MESSAGE, mProgress).sendToTarget();
            }
            fos.flush();
        } catch (Exception e) {
            file = null;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                file = null;
            }
        }
        return file;
    }

    private void checkLocalFilePath(String mFilePath) {
        File path = new File(mFilePath.substring(0, mFilePath.lastIndexOf("/") + 1));
        File file = new File(mFilePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}