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
import cn.linliangjun.its.unisocket.server.AbstractServerTest;
import cn.linliangjun.its.unisocket.server.Server;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class NettyUdpServerTest extends AbstractServerTest {

    private static final Map<String, Object> properties = new HashMap<>();

    static {
        properties.put("port", 8080);
        properties.put("host", "127.0.0.1");
        properties.put("ioModel", IoModel.NIO);
        properties.put("workerThreads", 4);
        properties.put("channelInitializer", new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(DatagramChannel ch) {
                ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
            }
        });
    }

    public NettyUdpServerTest() {
        super(new NettyUdpServer(), properties);
    }

    @Test
    void launch() throws Exception {
        Server server = getServer();
        server.launch();
        while (server.getStatus() != Server.Status.CLOSED) {
            TimeUnit.SECONDS.sleep(3);
        }
    }
}
