package com.game.net;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Spliterator;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午3:31
 */
public class NullChannel extends AbstractChannel {

    public static final ImmediateEventLoop EVENT_LOOP = new ImmediateEventLoop();

    private static final SocketAddress localAddr = new InetSocketAddress(0);

    protected NullChannel() {
        super(null);
    }

    @Override
    public EventLoop eventLoop() {
        return EVENT_LOOP;
    }

    @Override
    public Channel parent() {
        return this;
    }

    @Override
    public ChannelConfig config() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isRegistered() {
        return false;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public ChannelMetadata metadata() {
        return null;
    }

    @Override
    public SocketAddress localAddress() {
        return localAddr;
    }

    @Override
    public SocketAddress remoteAddress() {
        return localAddr;
    }

    @Override
    public ChannelFuture closeFuture() {
        return null;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public Unsafe unsafe() {
        return null;
    }

    @Override
    public ChannelPipeline pipeline() {
        return null;
    }

    @Override
    public ByteBufAllocator alloc() {
        return null;
    }

    @Override
    public Channel read() {
        return this;
    }

    @Override
    public ChannelFuture write(Object msg) {
        ReferenceCountUtil.release(msg);
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        ReferenceCountUtil.release(msg);
        return newSucceededFuture();
    }

    @Override
    public Channel flush() {
        return this;
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        ReferenceCountUtil.release(msg);
        return newSucceededFuture();
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        ReferenceCountUtil.release(msg);
        return newSucceededFuture();
    }

    @Override
    protected AbstractUnsafe newUnsafe() {
        return null;
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return true;
    }

    @Override
    protected SocketAddress localAddress0() {
        return localAddr;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return localAddr;
    }

    @Override
    protected void doBind(SocketAddress localAddr) throws Exception {

    }

    @Override
    protected void doDisconnect() throws Exception {

    }

    @Override
    protected void doClose() throws Exception {

    }

    @Override
    protected void doBeginRead() throws Exception {

    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {

    }


    private static class ImmediateEventLoop extends AbstractEventExecutor implements EventLoop {

        @Override
        public EventLoop next() {
            return this;
        }

        @Override
        public boolean inEventLoop() {
            return true;
        }

        @Override
        public boolean isShuttingDown() {
            return false;
        }

        @Override
        public Future<?> shutdownGracefully(long l, long l1, TimeUnit timeUnit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Future<?> terminationFuture() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void execute(Runnable command) {
            if (command != null) {
                command.run();
            }
        }

        @Override
        public ChannelFuture register(Channel channel) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChannelFuture register(ChannelPromise channelPromise) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ChannelFuture register(Channel channel, ChannelPromise channelPromise) {
            throw new UnsupportedOperationException();
        }

        @Override
        public EventLoopGroup parent() {
            return this;
        }

        @Override
        public void shutdown() {
            throw new UnsupportedOperationException();
        }


        @Override
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return GlobalEventExecutor.INSTANCE.schedule(command, delay, unit);
        }

        @Override
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            return GlobalEventExecutor.INSTANCE.schedule(callable, delay, unit);
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            return GlobalEventExecutor.INSTANCE.scheduleAtFixedRate(command, initialDelay, period, unit);
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            return GlobalEventExecutor.INSTANCE.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }


        @Override
        public boolean inEventLoop(Thread thread) {
            return true;
        }

        @Override
        public void forEach(Consumer<? super EventExecutor> action) {

        }

        @Override
        public Spliterator<EventExecutor> spliterator() {
            return null;
        }
    }
}
