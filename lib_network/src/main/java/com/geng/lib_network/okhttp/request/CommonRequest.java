package com.geng.lib_network.okhttp.request;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

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
                mHeaderBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        //构建者模式
        Request request = new Request.Builder().
                url(url).
                headers(mHeaderBuilder.build()).
                post(mFormBodyBuilder.build()).build();
        return request;
    }

    public static Request createGetRequest(String url, RequestParams params) {
        return createGetRequest(url, params, null);
    }

    //对外创建get请求对象
    public static Request createGetRequest(String url, RequestParams params, RequestParams headers) {
        StringBuilder urlBuilder = new StringBuilder(url).append("?");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                //参数遍历
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        Headers.Builder mHeaderBuilder = new Headers.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                //请求头遍历
                mHeaderBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        return new Request.Builder().
                url(url).
                headers(mHeaderBuilder.build()).
                get().
                build();
    }

    public static final MediaType FILE_TYPE = MediaType.parse("application/octet-stream");

    //文件上传请求对象
    public static Request createMultiPostRequest(String url, RequestParams params, RequestParams headers) {
        MultipartBody.Builder requestBody = new MultipartBody.Builder();
        requestBody.setType(MultipartBody.FORM);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.fileParams.entrySet()) {
                if (entry.getValue() instanceof File) {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""), RequestBody.create(FILE_TYPE, (File) entry.getValue()));
                } else if (entry.getValue() instanceof String) {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""), RequestBody.create(null, (String) entry.getValue()));
                }
            }
        }
        return new Request.Builder().url(url).post(requestBody.build()).build();
    }
}
