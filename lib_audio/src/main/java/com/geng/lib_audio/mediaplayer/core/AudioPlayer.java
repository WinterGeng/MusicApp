package com.geng.lib_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.geng.lib_audio.app.AudioHelper;
import com.geng.lib_audio.mediaplayer.model.AudioBean;
import com.geng.lib_audio.mediaplayer.model.AudioCompleteEvent;
import com.geng.lib_audio.mediaplayer.model.AudioErrorEvent;
import com.geng.lib_audio.mediaplayer.model.AudioLoadEvent;
import com.geng.lib_audio.mediaplayer.model.AudioPauseEvent;
import com.geng.lib_audio.mediaplayer.model.AudioReleaseEvent;
import com.geng.lib_audio.mediaplayer.model.AudioStartEvent;

import org.greenrobot.eventbus.EventBus;

//播放音频
//对外发送各种类型的事件
public class AudioPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioFocusManager.AudioFocusListener {
    public static final String TAG = "AudioPlayer";
    public static final int TIME_MSG = 0X01;
    public static final int TIME_INVAL = 100;

    //真正负责音频的播放
    private CustomMediaPlayer mediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    //音频焦点监听器
    private AudioFocusManager mAudioFocusManager;
    private boolean isPauseByFocusLossTransient;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME_MSG:
                    break;
            }
        }
    };

    public AudioPlayer() {
        init();
    }

    //初始化
    private void init() {
        mediaPlayer = new CustomMediaPlayer();
        mediaPlayer.setWakeMode(AudioHelper.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnErrorListener(this);

        mWifiLock = ((WifiManager) AudioHelper.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
        mAudioFocusManager = new AudioFocusManager(AudioHelper.getContext(), this);
    }

    //内部开始播放
    private void start() {
        if (mAudioFocusManager.requestAudioFocus()) {
            Log.e(TAG, "获取音频焦点失败");
        }
        mediaPlayer.start();
        mWifiLock.acquire();
        //对外发送start事件
        EventBus.getDefault().post(new AudioStartEvent());
    }

    //设置音量
    private void setVolumn(float leftVol, float rightVol) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(leftVol, rightVol);
        }
    }

    //对外提供的加载方法
    public void load(AudioBean audioBean) {
        try {
            //正常加载逻辑
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioBean.mUrl);
            mediaPlayer.prepareAsync();
            //对外发送load事件
            EventBus.getDefault().post(new AudioLoadEvent(audioBean));
        } catch (Exception e) {
            //对外发送error事件
            EventBus.getDefault().post(new AudioErrorEvent());
        }
    }

    //暂停
    public void pause() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED) {
            mediaPlayer.pause();
            //释放音频焦点wifilock
            if (mWifiLock.isHeld()) {
                mWifiLock.release();
            }
            //释放音频焦点
            if (mAudioFocusManager != null) {
                mAudioFocusManager.abandonAudioFocus();
            }
            //发送暂停事件
            EventBus.getDefault().post(new AudioPauseEvent());
        }
    }

    //恢复
    public void resume() {
        if (getStatus() == CustomMediaPlayer.Status.PAUSED) {
            //直接复用start方法
            start();
        }
    }

    //清空播放器占用的资源
    public void release() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.release();
        mediaPlayer = null;
        if (mAudioFocusManager != null) {
            mAudioFocusManager.abandonAudioFocus();
        }
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
        mWifiLock = null;
        mAudioFocusManager = null;
        //发送release销毁事件
        EventBus.getDefault().post(new AudioReleaseEvent());
    }

    //获取播放器当前状态
    public CustomMediaPlayer.Status getStatus() {
        if (mediaPlayer != null) {
            return mediaPlayer.getmState();
        }
        return CustomMediaPlayer.Status.STOPPED;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //缓存进度回调
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放完毕回调
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //播放出错回调
        EventBus.getDefault().post(new AudioErrorEvent());
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //准备完毕
        start();
    }

    @Override
    public void audioFocusGrant() {
        //再次获得了音频焦点
        setVolumn(1.0f, 1.0f);
        if (isPauseByFocusLossTransient) {
            resume();
        }
        isPauseByFocusLossTransient = false;
    }

    @Override
    public void audioFocusLoss() {
        //永久失去焦点
        pause();
    }

    @Override
    public void audioFocusLossTransient() {
        //短暂性失去焦点
        pause();
        isPauseByFocusLossTransient = true;
    }

    @Override
    public void audioFocusLossDuck() {
        //瞬间失去焦点（其他软件有声音的通知）
        setVolumn(0.5f, 0.5f);
    }
}
