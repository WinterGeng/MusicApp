package com.geng.lib_audio.mediaplayer.core;

import com.geng.lib_audio.mediaplayer.exception.AudioQueueEmptyException;
import com.geng.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

/**
 * 控制播放逻辑类
 */
public class AudioController {
    //播放方式
    public enum PlayMode {
        //列表循环
        LOOP,
        //随机
        RENDOM,
        //单曲循环
        REPEAT
    }

    public static AudioController getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static AudioController instance = new AudioController();
    }

    private AudioPlayer mAudioPlayer;//核心播放器
    private ArrayList<AudioBean> mQueue;//歌曲队列
    private int mQueueIndex;//当前播放歌曲索引
    private PlayMode mPlayMode;//循环模式

    private AudioController() {
        mAudioPlayer = new AudioPlayer();
        mQueue = new ArrayList<>();
        mQueueIndex = 0;
        mPlayMode = PlayMode.LOOP;
    }

    public ArrayList<AudioBean> getmQueue() {
        return mQueue == null ? new ArrayList<>() : mQueue;
    }

    //设置播放队列
    public void setQueue(ArrayList<AudioBean> queue) {
        this.setQueue(queue, 0);
    }

    public void addAudio(AudioBean bean) {
        this.addAudio(0, bean);
    }

    private int queryAudio(AudioBean bean) {
        return 0;
    }

    private void addCustomAudio(int index, AudioBean audioBean) {

    }

    //添加单一歌曲
    public void addAudio(int index, AudioBean bean) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空");
        }
        int query = queryAudio(bean);
        if (query <= -1) {
            //没有添加过
            addCustomAudio(index, bean);
            setPlayIndex(index);
        } else {
            AudioBean curBean = getNowPlaying();
            if (curBean.id.equals(curBean.id)) {
                //已经添加过，且不在播放中
                setPlayIndex(query);
            }
        }
    }

    public void setQueue(ArrayList<AudioBean> queue, int queueIndex) {
        mQueue.addAll(queue);
        mQueueIndex = queueIndex;
    }

    public void setPlayMode(PlayMode playMode) {
        mPlayMode = playMode;
    }

    public PlayMode getPlayMode() {
        return mPlayMode;
    }

    public void setPlayIndex(int index) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        mQueueIndex = index;
        play();
    }

    private AudioBean getNowPlaying() {
        return getPlaying();
    }

    private AudioBean getPlaying() {
        if (mQueue != null && !mQueue.isEmpty() && mQueueIndex >= 0) {
            return mQueue.get(mQueueIndex);
        } else {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
    }

    private AudioBean getPreviousPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex - 1) % mQueue.size();
                break;
            case RENDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getPlaying();
    }

    private AudioBean getNextPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex + 1) % mQueue.size();
                break;
            case RENDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getPlaying();
    }

    //对外提供的play方法
    private void play() {
        AudioBean bean = getNowPlaying();
        mAudioPlayer.load(bean);

    }

    private void pause() {
        mAudioPlayer.pause();
    }

    private void resume() {
        mAudioPlayer.resume();
    }

    private void release() {
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }

    //播放下一首歌曲
    public void next() {
        AudioBean bean = getNextPlaying();
        mAudioPlayer.load(bean);
    }

    //播放上一首
    public void previous() {
        AudioBean bean = getPreviousPlaying();
        mAudioPlayer.load(bean);
    }

    //自动切换播放暂停
    public void playOrPause() {
        if (isStartState()) {
            pause();
        } else if (isPauseState()) {
            resume();
        }
    }

    public int getPlayIndex() {
        return mQueueIndex;
    }

    /**
     * 对外提供是否播放中状态
     */
    public boolean isStartState() {
        return CustomMediaPlayer.Status.STARTED == getStatus();
    }

    /**
     * 对外提提供是否暂停状态
     */
    public boolean isPauseState() {
        return CustomMediaPlayer.Status.PAUSED == getStatus();
    }

    /*
     * 获取播放器当前状态
     */
    private CustomMediaPlayer.Status getStatus() {
        return mAudioPlayer.getStatus();
    }

}
