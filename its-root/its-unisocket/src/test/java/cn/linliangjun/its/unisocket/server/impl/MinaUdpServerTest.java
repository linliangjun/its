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
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.linliangjun.its.unisocket.server.Server.Status;

class MinaUdpServerTest extends AbstractServerTest {

    private static final Map<String, Object> properties = new HashMap<>();

    static {
        properties.put("port", 8080);
        properties.put("host", "127.0.0.1");
        properties.put("ioModel", IoModel.NIO);
        properties.put("filterChainBuilder", (IoFilterChainBuilder) chain -> chain.addLast("log", new LoggingFilter()));
        properties.put("handler", new IoHandlerAdapter());
    }

    public MinaUdpServerTest() {
        super(new MinaUdpServer(), properties);
    }

    @Test
    void launch() throws Exception {
        Server server = getServer();
        server.launch();
        Assertions.assertEquals(server.getStatus(), Status.RUNNING);
        while (server.getStatus() != Status.CLOSED) {
            TimeUnit.SECONDS.sleep(3);
        }
    }
}
