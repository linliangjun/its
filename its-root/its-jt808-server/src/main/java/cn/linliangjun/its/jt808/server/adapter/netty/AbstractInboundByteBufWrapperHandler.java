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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.var;

public abstract class AbstractInboundByteBufWrapperHandler extends SimpleChannelInboundHandler<ByteBufWrapper> {

    private final boolean autoRelease;

    public AbstractInboundByteBufWrapperHandler(boolean autoRelease) {
        super(autoRelease);
        this.autoRelease = autoRelease;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;
        try {
            if (msg instanceof ByteBufWrapper) {
                var wrapper = (ByteBufWrapper) msg;
                if (wrapper.isDiscard()) {
                    ctx.fireChannelRead(wrapper);
                } else {
                    channelRead0(ctx, wrapper);
                }
            } else {
                release = false;
                ctx.fireChannelRead(msg);
            }
        } finally {
            if (autoRelease && release) {
                ReferenceCountUtil.release(msg);
            }
        }
    }

    protected abstract void channelRead0(ChannelHandlerContext ctx, ByteBufWrapper wrapper) throws Exception;
}
