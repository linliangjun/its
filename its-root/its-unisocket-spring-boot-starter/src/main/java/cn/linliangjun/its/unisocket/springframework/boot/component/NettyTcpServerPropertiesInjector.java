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

package cn.linliangjun.its.unisocket.springframework.boot.component;

import cn.linliangjun.its.unisocket.server.Server;
import cn.linliangjun.its.unisocket.server.impl.NettyTcpServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class NettyTcpServerPropertiesInjector implements ServerPropertiesInjector {

    @Setter(onMethod_ = {@Autowired(required = false)})
    private ChannelInitializer<SocketChannel> tcpChannelInitializer;

    @Override
    public Class<? extends Server> getServerClass() {
        return NettyTcpServer.class;
    }

    @Override
    public void inject(Map<String, Object> map) {
        injectChannelInitializer(map);
        map.putIfAbsent("boss-threads", 1);
        map.putIfAbsent("worker-threads", 0);
    }

    private void injectChannelInitializer(Map<String, Object> map) {
        if (map.containsKey("channel-initializer")) {
            return;
        }
        if (tcpChannelInitializer != null) {
            map.put("channel-initializer", tcpChannelInitializer);
            return;
        }
        log.debug("未配置 tcpChannelInitializer，使用默认配置（仅输出 INFO 级别的日志）");
        var loggingHandler = new LoggingHandler(LogLevel.INFO);
        tcpChannelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(loggingHandler);
            }
        };
        map.put("channel-initializer", tcpChannelInitializer);
    }
}
