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
import cn.linliangjun.its.unisocket.server.impl.MinaTcpServer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class MinaTcpServerPropertiesInjector extends AbstractMinaServerPropertiesInjector {

    @Setter(onMethod_ = {@Autowired(required = false)})
    private IoHandler tcpHandler;

    @Setter(onMethod_ = {@Autowired(required = false)})
    private IoFilterChainBuilder tcpFilterChainBuilder;

    @Override
    public Class<? extends Server> getServerClass() {
        return MinaTcpServer.class;
    }

    @Override
    public void inject(Map<String, Object> map) {
        if (!map.containsKey("handler")) {
            if (tcpHandler == null) {
                tcpHandler = new IoHandlerAdapter();
                log.debug("未配置 tcpHandler，使用默认配置（无操作）");
            }
            map.put("handler", tcpHandler);
        }
        if (!map.containsKey("filter-chain-builder")) {
            if (tcpFilterChainBuilder == null) {
                LoggingFilter loggingFilter = getLoggingFilter(LogLevel.INFO);
                tcpFilterChainBuilder = chain -> chain.addLast("log", loggingFilter);
                log.debug("未配置 tcpFilterChainBuilder，使用默认配置（仅输出 INFO 级别的日志）");
            }
            map.put("filter-chain-builder", tcpFilterChainBuilder);
        }
    }
}
