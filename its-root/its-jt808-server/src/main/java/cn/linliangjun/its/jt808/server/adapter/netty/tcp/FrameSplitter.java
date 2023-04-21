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

import cn.linliangjun.its.jt808.server.adapter.netty.ByteBufWrapper;
import cn.linliangjun.its.uniprotocol.util.CodecUtils;
import cn.linliangjun.its.uniprotocol.util.Segment;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.util.ArrayList;
import java.util.List;

/**
 * JT/T 808 帧分割器
 *
 * <p>将二进制数据流分割为 0x7e ... 0x7e 形式的帧。
 *
 * @author linliangjun
 */
@Slf4j
public class FrameSplitter extends ChannelInboundHandlerAdapter {

    private ByteBuf cache;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        // 获取 buf 中，0x7e 的位置
        var indexes = CodecUtils.getIndexes(buf, (byte) 0x7e);
        if (indexes.isEmpty()) {
            if (cache == null) {
                // 不存在 0x7e，且 cache 也不存在，则丢弃
                discard(ctx, buf, String.format("未找到起始符 %#4x", 0x7e));
            } else {
                // 将 buf 中的数据全数追加到 cache
                appendCache(ctx, buf, -1);
            }
            // 直接返回，等待下次数据入站
            return;
        }
        // 根据 0x7e 的位置，获取每一段的位置（起始位置，长度）
        for (var segment : getSegments(indexes)) {
            try {
                int base = segment.getBase();
                int len = segment.getLen();
                if (base == -1) {
                    // 当前段是后半段数据（半包）
                    if (!appendCache(ctx, buf, len)) {
                        continue;
                    }
                    fireCacheChannelRead(ctx);
                } else {
                    cache = ctx.alloc().buffer(256, 2048);
                    buf.readerIndex(base);
                    if (!appendCache(ctx, buf, len)) {
                        continue;
                    }
                    // len == -1 表示当前段是前半段数据（半包），需要等待下次数据入站
                    if (len != -1) {
                        fireCacheChannelRead(ctx);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        buf.release();
    }

    private boolean appendCache(ChannelHandlerContext ctx, ByteBuf buf, int len) {
        try {
            if (len == -1) {
                cache.writeBytes(buf);
                buf.release();
            } else {
                cache.writeBytes(buf, len);
            }
            return true;
        } catch (IndexOutOfBoundsException ignored) {
            buf = ctx.alloc().compositeBuffer().addComponents(true, cache, len == -1 ? buf : buf.readBytes(len));
            cache = null;
            discard(ctx, buf, "长度溢出");
            return false;
        }
    }

    private void discard(ChannelHandlerContext ctx, ByteBuf buf, String message) {
        var wrapper = new ByteBufWrapper(buf);
        wrapper.discard(message);
        ctx.fireChannelRead(wrapper);
    }

    private void fireCacheChannelRead(ChannelHandlerContext ctx) {
        var wrapper = new ByteBufWrapper(cache);
        try {
            if (cache.writerIndex() - cache.readerIndex() < 12) {
                wrapper.discard("长度过短");
            }
            ctx.fireChannelRead(wrapper);
        } finally {
            cache = null;
        }
    }

    private List<Segment> getSegments(List<Integer> indexes) {
        var segments = new ArrayList<Segment>();
        int size = indexes.size();
        for (int i = 0; i < size; i++) {
            int index = indexes.get(i);
            if (i == 0 && cache != null) {
                segments.add(new Segment(-1, index + 1));
                continue;
            }
            i++;
            int length = i < size ? indexes.get(i) - index + 1 : -1;
            segments.add(new Segment(index, length));
        }
        return segments;
    }
}
