package com.geng.imooc_voice.view.login.manager;

import com.geng.imooc_voice.view.login.User;

/**
 * 单例管理登录用户信息
 */
public class UserManager {
    private static UserManager mInstance;
    private User mUser;

    public static UserManager getInstance() {
        if (mInstance == null) {
            synchronized (UserManager.class) {
                if (mInstance == null) {
                    mInstance = new UserManager();
                }
            }
        }
        return mInstance;
    }

    //保存用户信息到内存
    public void saveUser(User user) {
        mUser = user;
        saveLocal(user);
    }

    //保存到数据库
    //持久化用户信息
    private void saveLocal(User user) {

    }

    //获取用户信息
    public User getUser() {
        return mUser;
    }

    //从本地获取
    private User getLocal() {
        return null;
    }

    //判断是否登录过
    public boolean hasLogin() {
        return getUser() == null ? false : true;
    }

    public void removeUser() {
        mUser = null;
        removeLocal();
    }

    //从数据库删除用户信息
    private void removeLocal() {

    }
}
