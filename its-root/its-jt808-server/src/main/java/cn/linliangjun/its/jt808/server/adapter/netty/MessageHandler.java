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

package cn.linliangjun.its.jt808.server.adapter.netty;

import cn.linliangjun.its.jt808.protocol.message.Jt808Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.var;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static io.netty.channel.ChannelHandler.Sharable;
@Sharable
@Component
public class MessageHandler extends SimpleChannelInboundHandler<Jt808Message> {

    @Resource
    private MessageChannel messageRequestChannel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Jt808Message msg) {
        var message = MessageBuilder
                .withPayload(msg)
                .setHeader("ctx", ctx)
                .build();
        messageRequestChannel.send(message);
    }
}
