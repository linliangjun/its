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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.var;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.transport.socket.DatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

import javax.validation.constraints.NotNull;
import java.net.InetSocketAddress;

public class MinaUdpServer extends AbstractServer {

    private DatagramAcceptor acceptor;

    @Override
    protected void doInit() {
        var properties = (Properties) getProperties();
        acceptor = new NioDatagramAcceptor();
        acceptor.setFilterChainBuilder(properties.filterChainBuilder);
        acceptor.setHandler(properties.handler);
    }

    @Override
    protected void doLaunch() throws Exception {
        var properties = getProperties();
        var address = new InetSocketAddress(properties.getHost(), properties.getPort());
        acceptor.bind(address);
    }

    @Override
    protected void doClose() {
        if (acceptor != null) {
            acceptor.unbind();
        }
    }

    @Override
    protected boolean isSupported(IoModel model) {
        return model == IoModel.NIO;
    }

    @Override
    protected Class<? extends Properties> getPropertiesClass() {
        return Properties.class;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class Properties extends AbstractServer.Properties {

        @NotNull
        private IoHandler handler;

        @NotNull
        private IoFilterChainBuilder filterChainBuilder;
    }
}
