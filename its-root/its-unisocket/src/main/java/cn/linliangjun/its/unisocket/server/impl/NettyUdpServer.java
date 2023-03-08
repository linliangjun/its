/*
 * Copyright 2023-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.linliangjun.its.unisocket.server.impl;

import cn.linliangjun.its.unisocket.IoModel;
import cn.linliangjun.its.unisocket.server.AbstractServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueueDatagramChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.var;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class NettyUdpServer extends AbstractServer {

    private EventLoopGroup worker;
    private ChannelFuture launchFuture;
    private Class<? extends DatagramChannel> channelClass;

    @Override
    protected void doInit() throws Exception {
        Class<? extends EventLoopGroup> eventLoopGroupClass = null;
        var properties = (Properties) getProperties();
        switch (properties.getIoModel()) {
            case NIO:
                channelClass = NioDatagramChannel.class;
                eventLoopGroupClass = NioEventLoopGroup.class;
                break;
            case EPOLL:
                channelClass = EpollDatagramChannel.class;
                eventLoopGroupClass = EpollEventLoopGroup.class;
                break;
            case K_QUEUE:
                channelClass = KQueueDatagramChannel.class;
                eventLoopGroupClass = KQueueEventLoopGroup.class;
        }
        var constructor = eventLoopGroupClass.getConstructor(int.class);
        worker = constructor.newInstance(properties.workerThreads);
    }

    @Override
    protected void doLaunch() throws Exception {
        var properties = ((Properties) getProperties());
        launchFuture = new Bootstrap().group(worker)
                .channel(channelClass)
                .handler(properties.channelInitializer)
                .bind(properties.getHost(), properties.getPort())
                .sync();
    }

    @Override
    protected void doClose() throws Exception {
        try {
            if (launchFuture != null) {
                launchFuture.channel().close().sync();
            }
        } finally {
            if (worker != null) {
                worker.shutdownGracefully().sync();
            }
        }
    }

    @Override
    protected boolean isSupported(IoModel model) {
        return true;
    }

    @Override
    protected Class<? extends AbstractServer.Properties> getPropertiesClass() {
        return Properties.class;
    }

    /**
     * Netty UDP 服务器属性
     *
     * @author linliangjun
     */
    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class Properties extends AbstractServer.Properties {

        /**
         * 工作线程池线程数量
         */
        @NotNull
        @PositiveOrZero
        private Integer workerThreads;

        /**
         * UDP 管道处理器初始化器
         */
        @NotNull
        private ChannelInitializer<DatagramChannel> channelInitializer;
    }
}
