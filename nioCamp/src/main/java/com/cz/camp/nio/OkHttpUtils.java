package com.cz.camp.nio;


import okhttp3.*;

import java.awt.*;
import java.io.IOException;

/**
 * code desc
 *
 * @author Zjianru
 */
public class OkHttpUtils {
    // 缓存客户端实例
    public static final MediaType JSON = MediaType.get("application/json");

    static OkHttpClient client = new OkHttpClient();

    static String post(String url, String json){
//        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .get()
//                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {

        String url = "https://square.github.io/okhttp/";
        String text = OkHttpUtils.post(url,"");
        System.out.println("url: " + url + " ; response: \n" + text);
        // 清理资源
        client = null;
    }
}
