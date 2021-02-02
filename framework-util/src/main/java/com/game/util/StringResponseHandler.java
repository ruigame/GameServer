package com.game.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 4:04 下午
 */
public class StringResponseHandler implements ResponseHandler<String> {

    private static Charset DEFAULT = Charset.forName("UTF-8");

    @Override
    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        final StatusLine statusLine = response.getStatusLine();
        int status = statusLine.getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity  =response.getEntity();
            return entity != null ? EntityUtils.toString(entity, DEFAULT) : null;
        } else {
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
            throw new DefaultHttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
    }
}
