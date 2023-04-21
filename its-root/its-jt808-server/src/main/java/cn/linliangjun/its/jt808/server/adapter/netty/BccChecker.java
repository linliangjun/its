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

import cn.linliangjun.its.uniprotocol.util.CodecUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import static io.netty.channel.ChannelHandler.Sharable;

/**
 * JT/T 808 BCC 校验器
 *
 * @author linliangjun
 */
@Slf4j
@Sharable
public class BccChecker extends AbstractInboundByteBufWrapperHandler {

    public BccChecker() {
        super(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBufWrapper wrapper) {
        var buf = wrapper.getBuf();
        int writerIndex = buf.writerIndex(), readerIndex = buf.readerIndex();
        int len = writerIndex - readerIndex;
        byte code = CodecUtils.getBccCode(buf, readerIndex + 1, len - 3);
        byte b = buf.getByte(writerIndex - 2);
        if (code != b) {
            wrapper.discard(String.format("BCC 校验码不正确，应为：%0#4x，实际：%0#4x", code, b));
        }
        ctx.fireChannelRead(wrapper);
    }
}
