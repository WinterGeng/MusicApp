package com.geng.lib_network.request;

import java.util.Map;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;

/**
 * 对外提供post文件上传请求
 */
public class CommonRequest {
    public static Request createPostRequest(String url, RequestParams params) {
        return createPostRequest(url, params, null);
    }

    //对外创建post请求对象
    public static Request createPostRequest(String url, RequestParams params, RequestParams headers) {
        FormBody.Builder mFormBodyBuilder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                //参数遍历
                mFormBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        Headers.Builder mHeaderBuilder = new Headers.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                //请求头遍历
                mFormBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        //构建者模式
        Request request = new Request.Builder().
                url(url).
                headers(mHeaderBuilder.build()).
                post(mFormBodyBuilder.build()).build();
        return request;
    }
}
