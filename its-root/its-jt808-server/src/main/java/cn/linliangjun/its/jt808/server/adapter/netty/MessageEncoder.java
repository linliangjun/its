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

import cn.linliangjun.its.jt808.protocol.ProtocolDefinition;
import cn.linliangjun.its.jt808.protocol.message.Jt808Message;
import cn.linliangjun.its.uniprotocol.Codec;
import cn.linliangjun.its.uniprotocol.DefinitionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.var;
import org.springframework.util.Assert;

import static io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class MessageEncoder extends MessageToByteEncoder<Jt808Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Jt808Message msg, ByteBuf out) {
        var version = ChannelAttrUtils.getVersion(ctx);

        var protocol = new ProtocolDefinition(version);
        var pm = DefinitionManager.getProtocolDefinition(protocol);

        var messageDefinition = DefinitionManager.getMessageDefinition(pm, msg.getType().getValue());

        Assert.notNull(messageDefinition, String.format("协议定义 %s 未适配的消息类型 %s", protocol, msg.getType()));
        Codec<cn.linliangjun.its.uniprotocol.ProtocolDefinition, Jt808Message> codec = messageDefinition.getCodec();

        var buf = codec.encode(msg);
        out.writeBytes(buf);
    }
}
