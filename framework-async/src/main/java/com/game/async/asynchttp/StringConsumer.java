package com.game.async.asynchttp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 3:49 下午
 */
public class StringConsumer extends AbstractAsyncResponseConsumer<String> {

    private volatile SimpleInputBuffer buf = new SimpleInputBuffer(256, new HeapByteBufferAllocator());

    private static Charset DEFAULT = Charset.forName("UTF-8");

    @Override
    protected void onResponseReceived(HttpResponse response) throws HttpException, IOException {

    }

    @Override
    protected void onContentReceived(ContentDecoder decoder, IOControl ioControl) throws IOException {
        this.buf.consumeContent(decoder);
    }

    @Override
    protected void onEntityEnclosed(HttpEntity entity, ContentType contentType) throws IOException {

    }

    @Override
    protected String buildResult(HttpContext httpContext) throws Exception {
        byte[] bs = new byte[this.buf.length()];
        this.buf.read(bs);
        return new String(bs, DEFAULT);
    }

    @Override
    protected void releaseResources() {
        buf.reset();
        buf.shutdown();
        buf = null;
    }

}
