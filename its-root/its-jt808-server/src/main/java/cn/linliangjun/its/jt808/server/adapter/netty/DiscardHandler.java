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

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

@Slf4j
public class DiscardHandler extends ChannelInboundHandlerAdapter {

    private static final int MAX_DISCARD_COUNT = 10;
    private int discardCount;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        var channel = ctx.channel();
        if (msg instanceof ByteBufWrapper) {
            var wrapper = (ByteBufWrapper) msg;
            String hexDump = ByteBufUtil.hexDump(wrapper.getBuf());
            if (wrapper.isDiscard()) {
                log.info("{} 丢弃数据，原因：{}。原始数据：{}", channel, wrapper.getDiscardCause(), hexDump);
                discardCount++;
            }else {
                log.warn("{} 入站流水线已结束，数据却仍未处理，默认丢弃。原始数据：{}", ctx.channel(), hexDump);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
        ReferenceCountUtil.release(msg);
        if (discardCount >= MAX_DISCARD_COUNT) {
            log.info("{} 检测到大量异常数据，关闭连接...", channel);
            channel.close().awaitUninterruptibly();
        }
    }
}
