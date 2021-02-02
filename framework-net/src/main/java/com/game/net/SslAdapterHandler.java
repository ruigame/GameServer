package com.game.net;

import com.game.file.prop.PropConfig;
import com.game.file.prop.PropConfigStore;
import com.game.util.ExceptionUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.File;

/**
 * @Author: liguorui
 * @Date: 2020/12/1 下午11:48
 */
@ChannelHandler.Sharable
public class SslAdapterHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SslAdapterHandler.class);

    private static SslContext sslContext;

    public static final int SSL_RECORD_HEADER_LENGTH = 5;

    static {
        try {
            PropConfig netConfig = PropConfigStore.getPropConfig("independent/ssl.properties");
            boolean isSSL = netConfig.getBoolean("SSL");
            String cert = netConfig.getStr("CERT");
            String certKey = netConfig.getStr("CERT_KEY");
            LOGGER.info("ssl_enable={},CERT={},CERT_KEY={}", isSSL, cert, certKey);
            if (isSSL) {
                LOGGER.info("cert_path={}", cert);
                LOGGER.info("cert_key_path={}", certKey);
                File certFile = new File(cert);
                File keyFile = new File(certKey);
                sslContext = SslContextBuilder.forServer(certFile, keyFile).build();
            } else {
                sslContext = null;
            }
        } catch (SSLException e) {
            ExceptionUtils.log(e);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf data = (ByteBuf)msg;
        if (data.readableBytes() < SSL_RECORD_HEADER_LENGTH) {
            return;
        }
        ChannelPipeline pipeline = ctx.pipeline();
        if (sslContext != null && SslHandler.isEncrypted(data)) {
            pipeline.addAfter(ctx.name(), "sslHandler", sslContext.newHandler(ctx.channel().alloc()));
        }
        pipeline.remove(this);
        ctx.fireChannelRead(msg);
    }
}
