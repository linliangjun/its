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
import cn.linliangjun.its.uniprotocol.util.Segment;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.util.LinkedHashMap;
import java.util.List;

import static io.netty.channel.ChannelHandler.Sharable;

/**
 * JT/T 808 入站转义处理器，转义 0x7d01、0x7d02
 *
 * @author linliangjun
 */
@Slf4j
@Sharable
public class EscapeHandler extends AbstractInboundByteBufWrapperHandler {

    public EscapeHandler() {
        super(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBufWrapper wrapper) {
        var buf = wrapper.getBuf();
        var indexes = CodecUtils.getIndexes(buf, (byte) 0x7d);
        if (indexes.isEmpty()) {
            ctx.fireChannelRead(wrapper);
            return;
        }
        LinkedHashMap<Segment, Byte> segments;
        try {
            segments = getSegments(buf, indexes);
        } catch (DecoderException e) {
            wrapper.discard(e.getMessage());
            ctx.fireChannelRead(wrapper);
            return;
        }
        var out = ByteBufAllocator.DEFAULT.buffer(buf.writerIndex() - buf.readerIndex() - indexes.size());
        segments.forEach((segment, b) -> {
            out.writeBytes(buf, segment.getBase(), segment.getLen());
            if (b != null) {
                out.writeByte(b);
            }
        });
        wrapper.release();
        wrapper = new ByteBufWrapper(out);
        ctx.fireChannelRead(wrapper);
    }

    private LinkedHashMap<Segment, Byte> getSegments(ByteBuf buf, List<Integer> indexes) {
        var segments = new LinkedHashMap<Segment, Byte>();
        int fromIndex = 0;
        for (int index : indexes) {
            byte b = getEscapeByte(buf, index);
            segments.put(new Segment(fromIndex, index - fromIndex), b);
            fromIndex = index + 2;
        }
        segments.put(new Segment(fromIndex, buf.writerIndex() - fromIndex), null);
        return segments;
    }

    private byte getEscapeByte(ByteBuf buf, int index) {
        byte b = buf.getByte(index + 1);
        switch (b) {
            case 1:
                return 0x7d;
            case 2:
                return 0x7e;
            default:
                throw new DecoderException(String.format("未知的转义：0x7d%02x", b));
        }
    }
}
