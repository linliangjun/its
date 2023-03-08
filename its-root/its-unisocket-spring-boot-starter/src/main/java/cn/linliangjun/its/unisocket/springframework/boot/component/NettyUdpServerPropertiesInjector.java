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
import cn.linliangjun.its.unisocket.server.impl.NettyUdpServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.DatagramChannel;
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
public class NettyUdpServerPropertiesInjector implements ServerPropertiesInjector {

    @Setter(onMethod_ = @Autowired(required = false))
    private ChannelInitializer<DatagramChannel> udpChannelInitializer;

    @Override
    public Class<? extends Server> getServerClass() {
        return NettyUdpServer.class;
    }

    @Override
    public void inject(Map<String, Object> map) {
       injectChannelInitializer(map);
        map.putIfAbsent("worker-threads", 0);
    }

    private void injectChannelInitializer(Map<String, Object> map) {
        if (map.containsKey("channelInitializer")) {
            return;
        }
        if (udpChannelInitializer != null) {
            map.put("channelInitializer", udpChannelInitializer);
            return;
        }
        log.debug("未配置 udpChannelInitializer，使用默认配置（仅输出 INFO 级别的日志）");
        var loggingHandler = new LoggingHandler(LogLevel.INFO);
        udpChannelInitializer = new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(DatagramChannel ch) {
                ch.pipeline().addLast(loggingHandler);
            }
        };
        map.put("channel-initializer", udpChannelInitializer);
    }
}
