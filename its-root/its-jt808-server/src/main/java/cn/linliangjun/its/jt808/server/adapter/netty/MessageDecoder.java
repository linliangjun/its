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
import cn.linliangjun.its.jt808.protocol.message.AbstractMessage;
import cn.linliangjun.its.jt808.protocol.message.Type;
import cn.linliangjun.its.uniprotocol.Codec;
import cn.linliangjun.its.uniprotocol.CodecException;
import cn.linliangjun.its.uniprotocol.DefinitionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.var;

/**
 * JT/T 808 消息解码器
 *
 * @author linliangjun
 */
public class MessageDecoder extends AbstractInboundByteBufWrapperHandler {

    public MessageDecoder() {
        super(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBufWrapper wrapper) {
        var version = ChannelAttrUtils.getVersion(ctx);
        if (version == null) {
            wrapper.discard("版本为 null");
            ctx.fireChannelRead(wrapper);
            return;
        }

        var protocol = new ProtocolDefinition(version);
        var pm = DefinitionManager.getProtocolDefinition(protocol);
        if (pm == null) {
            wrapper.discard(String.format("协议定义 %s 未注册", protocol));
            ctx.fireChannelRead(wrapper);
            return;
        }

        Type type = wrapper.getType();
        var messageDefinition = DefinitionManager.getMessageDefinition(pm, type.getValue());
        if (messageDefinition == null) {
            wrapper.discard(String.format("协议定义 %s 未适配的消息类型 %s", protocol, type));
            ctx.fireChannelRead(wrapper);
            return;
        }

        Codec<cn.linliangjun.its.uniprotocol.ProtocolDefinition, AbstractMessage> codec = messageDefinition.getCodec();

        try {
            ByteBuf buf = wrapper.getBuf();
            buf.markReaderIndex();
            buf.skipBytes(1);
            var message = codec.decode(protocol, buf);
            if (buf.readerIndex() != buf.writerIndex() - 2) {
                buf.resetReaderIndex();
                throw new CodecException("字节缓冲区未读取完");
            }
            System.out.println(message);
            wrapper.release();
        }catch (Exception e) {
            wrapper.discard("解码消息异常，具体原因：" + e.getMessage());
            ctx.fireChannelRead(wrapper);
        }
    }
}
