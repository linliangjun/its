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

package cn.linliangjun.its.jt808.server.adapter;

import cn.linliangjun.its.jt808.server.adapter.netty.ChannelAttrUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CtxUtils {

    public static void setLogin(Object ctx) {
        if (ctx instanceof ChannelHandlerContext) {
            ChannelAttrUtils.setLogin(((ChannelHandlerContext) ctx));
        }else {
            log.error("无法设置上下文，原因：不支持的 channel 上下文，{}", ctx);
        }
    }
}
