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

package cn.linliangjun.its.jt808.server.configure.integration;

import cn.linliangjun.its.jt808.protocol.message.Jt808Message;
import cn.linliangjun.its.jt808.protocol.message.Type;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;

/**
 * JT/T 808 消息路由器
 *
 * @author linliangjun
 */
@Slf4j
@MessageEndpoint
public class MessageRouter {

    /**
     * 消息请求路由
     *
     * @param message JT/T 808 消息
     * @return 对应的消息通道，若不存在，则新建一个 {@linkplain DirectChannel}
     */
    @Router(inputChannel = "messageRequestChannel", prefix = "messageRequestChannel_", resolutionRequired = "false", defaultOutputChannel = "messageRequestDiscardChannel")
    public String messageRequestRout(Message<Jt808Message> message) {
        Type type = message.getPayload().getType();
        return String.format("%0#6x", type.getValue());
    }

    /**
     * 丢弃不可路由的消息
     */
    @ServiceActivator(inputChannel = "messageRequestDiscardChannel")
    public void discardService(Jt808Message message) {
        log.error("未找到可用的消息端点，丢弃请求数据：{}", message);
    }

    @ServiceActivator(inputChannel = "messageResponseChannel")
    public void sendService(@Header Object ctx, Jt808Message message) {
        if (ctx instanceof ChannelHandlerContext) {
            ((ChannelHandlerContext) ctx).writeAndFlush(message);
        } else {
            log.error("无法发送数据，原因：不支持的 channel 上下文，{}", ctx);
        }
    }
}
