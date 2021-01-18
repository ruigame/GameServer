package com.game.async.asynchttp;

import com.game.util.SimpleThreadFactory;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.client.methods.HttpAsyncMethods;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: liguorui
 * @Date: 2020/9/19 7:28 下午
 */
public class AsyncHttpClientUtils {

    private static CloseableHttpAsyncClient HTTP_CLIENT;

    private static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");

    /**
     * 异步http再解释域名时候同步，加多一条线程
     */
    private static ExecutorService executorService = Executors.newFixedThreadPool(1, new SimpleThreadFactory("AsyncHttpClientUtils"));

    static {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000)
                .setConnectionRequestTimeout(3000)
                .build();
        CloseableHttpAsyncClient httpClient = HttpAsyncClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setMaxConnPerRoute(50)
                .setMaxConnTotal(100)
                .disableAuthCaching()
                .disableCookieManagement()
                .disableConnectionState().build();
        HTTP_CLIENT = httpClient;
        HTTP_CLIENT.start();

    }

    public static void post(String url, Map<String, Object> params, FutureCallback<String> callback) {
        final HttpPost httpPost = new HttpPost(url);
        if (MapUtils.isNotEmpty(params)) {
            List<NameValuePair> nvps = new ArrayList<>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                Object[] values = null;
                if (value.getClass().isArray()) {
                    values = (Object[])value;
                } else {
                    values = new Object[]{value};
                }
                for (Object v : values) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), v.toString()));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, UTF_8_CHARSET));
        }

        final URI requestURI = httpPost.getURI();
        final HttpHost target = requestURI.isAbsolute() ? URIUtils.extractHost(requestURI) : null;
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                HTTP_CLIENT.execute(HttpAsyncMethods.create(target, httpPost), new StringConsumer(), callback);
            }
        });
    }

    public static void get(String url, Map<String, Object> params, FutureCallback<String> callback) {
        if (MapUtils.isNotEmpty(params)) {
            String param = HttpUtils.makeQueryString(params);
            url += "?" + param;
        }
        final HttpGet httpGet = new HttpGet(url);
        final URI requestURI = httpGet.getURI();
        final HttpHost target = requestURI.isAbsolute() ? URIUtils.extractHost(requestURI) : null;
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                HTTP_CLIENT.execute(HttpAsyncMethods.create(target, httpGet), new StringConsumer(), callback);
            }
        });
    }

    public static void stop() {
        try {
            HTTP_CLIENT.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
