package com.geng.imooc_voice.view.home.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.geng.imooc_voice.model.CHANNEL;
import com.geng.imooc_voice.view.discory.DiscoryFragment;
import com.geng.imooc_voice.view.friend.FriendFragment;
import com.geng.imooc_voice.view.mine.MineFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {
    private CHANNEL[] mList;

    public HomePagerAdapter(FragmentManager supportFragmentManager, CHANNEL[] channels) {
        super(supportFragmentManager);
        mList = channels;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        int type = mList[position].getValue();
        switch (type) {
            case CHANNEL.MINE_ID:
                return MineFragment.newInstance();
            case CHANNEL.DISCORY_ID:
                return DiscoryFragment.newInstance();
            case CHANNEL.FRIEND_ID:
                return FriendFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.length;
    }
}
