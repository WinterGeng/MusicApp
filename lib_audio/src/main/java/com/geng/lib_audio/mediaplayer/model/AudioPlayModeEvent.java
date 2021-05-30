package com.geng.lib_audio.mediaplayer.model;

import com.geng.lib_audio.mediaplayer.core.AudioController;

/**
 * 播放模式切换事件
 */
public class AudioPlayModeEvent {
  public AudioController.PlayMode mPlayMode;

  public AudioPlayModeEvent(AudioController.PlayMode playMode) {
    this.mPlayMode = playMode;
  }
}
