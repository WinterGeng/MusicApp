package com.geng.imooc_voice.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.geng.imooc_voice.R;
import com.geng.imooc_voice.api.RequestCenter;
import com.geng.imooc_voice.view.login.manager.UserManager;
import com.geng.imooc_voice.view.login.user.LoginEvent;
import com.geng.imooc_voice.view.login.user.User;
import com.geng.lib_common_ui.base.BaseActivity;
import com.geng.lib_network.okhttp.listener.DisposeDataListener;

import org.greenrobot.eventbus.EventBus;

/**
 * 登录页面
 */
public class LoginActivity extends BaseActivity implements DisposeDataListener {

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        findViewById(R.id.login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestCenter.login(LoginActivity.this);
            }
        });
    }

    @Override
    public void onSuccess(Object responseObj) {
        //处理正常逻辑
        User user = (User) responseObj;
        UserManager.getInstance().saveUser(user);
        EventBus.getDefault().post(new LoginEvent());
        finish();
    }

    @Override
    public void onFailure(Object reasonObj) {
        //登陆失败逻辑
        Exception exception;
        exception = (Exception) reasonObj;
        Log.e("TAG", "Login failed:" + exception.getMessage());
    }
}
