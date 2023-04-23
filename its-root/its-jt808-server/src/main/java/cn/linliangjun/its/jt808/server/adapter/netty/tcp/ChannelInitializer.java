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

package cn.linliangjun.its.jt808.server.adapter.netty.tcp;

import cn.linliangjun.its.jt808.server.adapter.netty.*;
import io.netty.channel.socket.SocketChannel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ChannelInitializer extends io.netty.channel.ChannelInitializer<SocketChannel> {

    private final BccChecker bccChecker = new BccChecker();

    private final EscapeHandler escapeHandler = new EscapeHandler();

    private final AccessHandler accessHandler = new AccessHandler();

    private final MessageDecoder messageDecoder = new MessageDecoder();

    private final MessageEncoder messageEncoder = new MessageEncoder();

    @Resource
    private MessageHandler messageHandler;

    @Override
    protected void initChannel(SocketChannel ch) {
        // 入站是从上往下找入站处理器，出站是从下往上找出站处理器
        ch.pipeline()
                .addLast(new FrameSplitter())
                .addLast(escapeHandler)
                .addLast(bccChecker)
                .addLast(accessHandler)
                .addLast(messageDecoder)
                .addLast(new DiscardHandler())
                .addLast(messageEncoder)
                .addLast(messageHandler);
    }
}
