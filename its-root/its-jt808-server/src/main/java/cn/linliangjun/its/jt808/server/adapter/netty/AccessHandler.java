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

import cn.linliangjun.its.jt808.protocol.Version;
import cn.linliangjun.its.jt808.protocol.message.Type;
import io.netty.channel.ChannelHandlerContext;
import lombok.var;

/**
 * JT/T 808 准入处理器
 *
 * @author linliangjun
 */
public class AccessHandler extends AbstractInboundByteBufWrapperHandler {

    public AccessHandler() {
        super(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBufWrapper wrapper) {
        var buf = wrapper.getBuf();
        int typeId = buf.getUnsignedShort(1);
        var type = Type.getType(typeId);
        if (type == null) {
            wrapper.discard(String.format("未知的消息类型 %0#6x", typeId));
            ctx.fireChannelRead(wrapper);
            return;
        }
        wrapper.setType(type);
        int len = buf.writerIndex() - buf.readerIndex();
        if (type == Type.TERMINAL_REGISTER) {
            Version version;
            if (len < 50) {
                version = Version.V2011;
            } else if (len < 90) {
                version = Version.V2013;
            } else {
                version = Version.V2019;
            }
            ChannelAttrUtils.setVersion(ctx, version);
            ctx.fireChannelRead(wrapper);
            return;
        }
        if (type == Type.TERMINAL_AUTH) {
            var version = ChannelAttrUtils.getVersion(ctx);
            if (version == null) {
                version = len > 50 ? Version.V2019 : Version.V2013;
            }
            ChannelAttrUtils.setVersion(ctx, version);
            ctx.fireChannelRead(wrapper);
            return;
        }
        if (!ChannelAttrUtils.login(ctx)) {
            wrapper.discard("终端未鉴权");
            ctx.fireChannelRead(wrapper);
        }
    }
}
