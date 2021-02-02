package com.game.util;

import org.apache.http.client.HttpResponseException;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 4:08 下午
 */
public class DefaultHttpResponseException extends HttpResponseException {

    private static final long serialVersionUID = 8904013045763864890L;

    public DefaultHttpResponseException(int statusCode, String s) {
        super(statusCode, s + " " + statusCode);
    }
}
